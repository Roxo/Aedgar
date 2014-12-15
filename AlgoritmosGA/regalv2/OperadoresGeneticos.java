package regalv2;

import java.util.ArrayList;
/**
 * OperadoresGeneticos implementa los operadores gen�ticos que utilizan los nodos.
 * @author Jos� Luis Toscano Mu�oz
 * @version Regal v2.0
 */
public class OperadoresGeneticos {

	/**
	 * Representa el n�mero de tipos de cruces.
	 */
	private int NUM_CRUCES=4;
	
	/**
	 * Identificador del tipo de cruce uniforme.
	 */
	private int CRUCE_UNIFORME=0;
	
	/**
	 * Identificador del tipo de cruce dos puntos.
	 */
	private int CRUCE_2PT=1;
	
	/**
	 * Identificador del tipo de cruce especializaci�n.
	 */
	private int CRUCE_ESPECIALIZACION=2;
	
	/**
	 * Identificador del tipo de cruce generalizaci�n.
	 */
	private int CRUCE_GENERALIZACION=3;
	
	/**
	 * Intancia con los par�metros de configuraci�n de la ejecuci�n.
	 */
	private ParametrosGlobales parametrosGlobales=ParametrosGlobales.getInstancia_Parametros();
	

	
	/**
	 * El Operador de sembrado genera una regla a partir de un ejemplo, de tal forma que el ejemplo es cubierto positivamente por la regla. 
	 * @param ej es el ejemplo al que se le va a generar una regla que lo cubra.
	 * @return regla es una regla que cubre el ejemplo positivamente.
	 */
	public Regla sembrado(Ejemplo ej){
		Regla NuevaRegla=new Regla();
		for(int i=0;i<NuevaRegla.getLongitudCromosoma();i++){	
			int valAleatorio=parametrosGlobales.getGeneradorAleatorio().randInt(0,1);
			if (valAleatorio==1)NuevaRegla.setValor(i,'1');
			else NuevaRegla.setValor(i,'0');
		}
		for(int i_atr=0;i_atr<NuevaRegla.getNumAtributos();i_atr++){
				// Si el atributo est� evaluado, hacemos que cumpla el ejemplo
				if (NuevaRegla.atributoEvaluado(i_atr)){
					int ind_aux=parametrosGlobales.getPlantillaAtributos()[i_atr][1];
					// Cambio a 1 el m�nimo n�mero de valores
					for (int j=0;j<parametrosGlobales.getPlantillaAtributos()[i_atr][0];j++){
						if(ej.getValor(ind_aux+j)=='1'){
							 NuevaRegla.setValor(ind_aux+j,'1');
						}
					}
				}			
		}		
		// Le asigno la clase de la nueva regla
		NuevaRegla.setClase(ej.getClase());
		// Aqui he generado una regla completamente aleatoria, una cadena de bits aleatoria.
		return NuevaRegla;
	}
	
	/**
	 * Operador de sufragio universal, es un operador de selecci�n. 
	 * Selecciona el conjunto de reglas que se van a  participar en la reproducci�n.
	 * @param ejemplos Datos de entrenamiento.
	 * @param Reglas son los individuos candidatos a ser seleccionados para la reproducci�n.
	 * @param m tama�o de la poblaci�n.
	 * @return un objeto Solucion con los individuos seleccionados para la reproducci�n.
	 */
	public Solucion sufragioUniversal(ConjuntoEntrenamiento ejemplos,Solucion Reglas,int m){
		Solucion B=new Solucion();
		
		// primer Paso es selecionar el conjundo de ejemplos que van ha votar por una regla.
		int numeroIndividuosVotan=(int)(parametrosGlobales.getG()*m);
		ArrayList indicesPoblacionTotal=new ArrayList();
		int individuosVotan[];
		
		for(int i=0;i<ejemplos.getTama�o();i++) indicesPoblacionTotal.add(new Integer(i));
		
		if(numeroIndividuosVotan<ejemplos.getTama�o()){
			individuosVotan=new int[numeroIndividuosVotan];
			for(int i=0;i<numeroIndividuosVotan;i++){
				int indice_aleatorio=parametrosGlobales.getGeneradorAleatorio().randInt(0,indicesPoblacionTotal.size()-1);
				individuosVotan[i]=Integer.parseInt(indicesPoblacionTotal.get(indice_aleatorio)+"");
				indicesPoblacionTotal.remove(indice_aleatorio); // Versi�n v1.2 Sino el mismo ejemplo puede votar varias veces
			}
		}else{  
			// En el caso de que queden menos o igual n�mero de ejemplos que ejemplos hay que selecionas
			numeroIndividuosVotan=ejemplos.getTama�o();
			individuosVotan=new int[numeroIndividuosVotan];
			for(int i=0;i<numeroIndividuosVotan;i++) individuosVotan[i]=Integer.parseInt(indicesPoblacionTotal.get(i)+"");
		}
		
		// En la Tabla Individuos_Votan estan los indices de los individuos 
		//que van ha votar por una regla
			
		//Para cada ejemplo debo crear una ruleta de las reglas que lo cubren
		for (int i=0;i<numeroIndividuosVotan;i++){
			// Obtengo el votante
			int indice_votante=individuosVotan[i];
			Ejemplo Votante= (Ejemplo)ejemplos.getEjemplo(indice_votante);
			Solucion reglas_cubren_Ejemplo=new Solucion();
			for (int j=0;j<Reglas.getTama�o();j++){
				Regla regla=Reglas.getRegla(j);
				if (regla.cubreEjemploPositivamente(Votante)){
					reglas_cubren_Ejemplo.insertarRegla(regla);
				}
			}
			if (reglas_cubren_Ejemplo.getTama�o()>0){
				// Si existen reglas que cubren el ejemplo, utilizamos la ruleta para selecionar una.
				init(reglas_cubren_Ejemplo);
				B.insertarRegla(seleccionaRegla(reglas_cubren_Ejemplo));
			}else{
				//Si no hemos encontrado ninguna regla que cubra el ejemplo, la creamos con el operador de sembrado. 
				B.insertarRegla(sembrado(Votante));		
			}
		}
		return B;
	}
	

	/**
	 * Devuelve un entero indicando qu� tipo de cruce se va a utilizar.
	 * @param parent1 es un individuo para cruzar.
	 * @param parent2 es el otro individuo para cruzar.
	 * @param ejemplos Datos de entrenamientos
	 * @return Devuelve un identificador del cruce a aplicar.
	 */
	public int selectorTipoCruce(Regla parent1, Regla parent2, ConjuntoEntrenamiento ejemplos){
			double probabilidades[]=new double[NUM_CRUCES];
			double prob=0;
			double r=0;
			double fm=0;				

			fm=(double)((parent1.getFitness()+parent2.getFitness())/2);			
			int n_positivo_parent1=0;
			int n_negativo_parent1=0;
			int n_positivo_parent2=0;
			int n_negativo_parent2=0;
			
			for(int i=0;i<parametrosGlobales.getNumeroClases();i++){
				if(parent1.getClase()==i)
					n_positivo_parent1+=parent1.getNumeroEjemplosCubiertos()[i];
				else 
					n_negativo_parent1+=parent1.getNumeroEjemplosCubiertos()[i];
				
				if(parent2.getClase()==i)
					n_positivo_parent2+=parent2.getNumeroEjemplosCubiertos()[i];
				else 
					n_negativo_parent2+=parent2.getNumeroEjemplosCubiertos()[i];
			}
			
		
			r=((double)(n_positivo_parent1+n_negativo_parent1+n_positivo_parent2+n_negativo_parent2)/(double)(ejemplos.getTama�o()^2));
			
			
			double a=parametrosGlobales.get_a();
			double b=parametrosGlobales.get_b();
			
			probabilidades[CRUCE_UNIFORME]=(1 - a*fm)*b;	
			probabilidades[CRUCE_2PT]=(1 - a*fm)*(1-b);
			probabilidades[CRUCE_ESPECIALIZACION]= fm * a * r;
			probabilidades[CRUCE_GENERALIZACION]= fm * a * (1 - r);
		
			prob=parametrosGlobales.getGeneradorAleatorio().rand();	
			   int idCruce = 0;
			   while (prob >= probabilidades[idCruce]){
				   prob-=probabilidades[idCruce];
				   idCruce++;
			   }			   
			   return(idCruce);	
	}
	
	
	/**
	 * Aplica el cruce entre dos individuos seleccionados.
	 * @param padre1 Individuo a cruzar.
	 * @param padre2 Individuo a cruzar.
	 * @param hijo1 es un individuo pasado por referencia, y en el se devuelve uno de los hijos generado en el cruce.
	 * @param hijo2 es un individuo pasado por referencia, y en el se devuelve uno de los hijos generado en el cruce.
	 * @param ejemplos es el conjunto de datos de entrenamiento.
	 * @return devuelve el identificador del cruce aplicado.
	 */
	public int  cruzarCromosomas(Regla padre1,Regla padre2,Regla hijo1,Regla hijo2,ConjuntoEntrenamiento ejemplos ){
		
		int idCruce=selectorTipoCruce(padre1,padre2,ejemplos);	
		switch (idCruce){
		case 0:
			cruceUniforme(padre1,padre2,hijo1,hijo2);
			break;
		case 1:
			cruce2Puntos(padre1,padre2,hijo1,hijo2);
			break;
		case 2:
			cruceGeneralizacionEspecializacion(padre1,padre2,hijo1,hijo2,'E');
			break;
		case 3:
			cruceGeneralizacionEspecializacion(padre1,padre2,hijo1,hijo2,'G');
			break;
		}
		
		return idCruce;
	}
	
	/**
	 * Operador de cruce 2 puntos, se selecciona dos puntos de forma aleatoria de las reglas Padre1 y Padre2
	 * en los par�metros hijo1 y hijo2, se devuelven las dos nuevas reglas generadas.
	 * @param Padre1 es uno de los individuos a cruzar.
	 * @param Padre2 es uno de los individuos a cruzar.
	 * @param Hijo1 es un individuo pasado por referencia, y en el se devuelve uno de los hijos generado en el cruce.
	 * @param Hijo2 es un individuo pasado por referencia, y en el se devuelve uno de los hijos generado en el cruce.
	 * @return devuelve 1 todo ha ido bien
	 */
	
	public int cruce2Puntos(Regla Padre1, Regla Padre2, Regla Hijo1, Regla Hijo2){		

		int longCromosoma=Padre1.getLongitudCromosoma();
		int primer_Punto_Cruce=parametrosGlobales.getGeneradorAleatorio().randInt(0,longCromosoma-1);
		int segundo_Punto_Cruce=parametrosGlobales.getGeneradorAleatorio().randInt(0,longCromosoma-1);
		
		if (primer_Punto_Cruce>segundo_Punto_Cruce){
			int temp=primer_Punto_Cruce;
			primer_Punto_Cruce=segundo_Punto_Cruce;
			segundo_Punto_Cruce=temp;	
		}
		
		for (int i=0 ; i < primer_Punto_Cruce ; i++) {
			Hijo1.setValor(i,Padre1.getValor(i));
			Hijo2.setValor(i,Padre2.getValor(i));
		   }
		for (int i = primer_Punto_Cruce ; i <= segundo_Punto_Cruce ; i++) {
			   Hijo1.setValor(i,Padre2.getValor(i));
			   Hijo2.setValor(i,Padre1.getValor(i));
		   }
		for (int i = segundo_Punto_Cruce + 1 ; i < longCromosoma ; i++) {
			   Hijo1.setValor(i,Padre1.getValor(i));
			   Hijo2.setValor(i,Padre2.getValor(i));
		   }
		return 1;
	}
	
	
/**
 * Cruce Uniforme, A este m�todo se le pasa dos reglas padre1 y padre2, y genera dos reglas hijas, donde cada bit tiene
 * una probabilidad del 50% de pertenecer a una regla padre1 o a la padre2.
 * @param Padre1 es uno de los individuos a cruzar.
 * @param Padre2 es uno de los individuos a cruzar.
 * @param Hijo1 es un individuo pasado por referencia, y en �l se devuelve uno de los hijos generado en el cruce.
 * @param Hijo2	es un individuo pasado por referencia, y en �l se devuelve uno de los hijos generado en el cruce.
 * @return devuelve 1 si todo ha ido bien.
 */
	public int cruceUniforme(Regla Padre1, Regla Padre2, Regla Hijo1, Regla Hijo2){
		int longCromosoma=Padre1.getLongitudCromosoma();
		   for (int i=0 ; i<longCromosoma ; i++)
		      if (parametrosGlobales.getGeneradorAleatorio().rand()<0.5) {
		    	  Hijo1.setValor(i,Padre1.getValor(i));
		    	  Hijo2.setValor(i,Padre2.getValor(i));
		      }
		      else {
		    	  Hijo1.setValor(i,Padre2.getValor(i));
		    	  Hijo2.setValor(i,Padre1.getValor(i));
		      }	

		return 1;
	}
	
	
	
	/**
	 * Cruce de especializaci�n y generalizaci�n.
	 * @param Padre1 es uno de los individuos a cruzar.
	 * @param Padre2 es uno de los individuos a cruzar.
	 * @param Hijo1 es un individuo pasado por referencia, y en �l se devuelve uno de los hijos generado en el cruce.
	 * @param Hijo2 es un individuo pasado por referencia, y en �l se devuelve uno de los hijos generado en el cruce.
	 * @param tipo_G_E indica el tipo de cruce a aplicar.
	 * @return devuelve 1 si todo ha ido bien.
	 */
	public int cruceGeneralizacionEspecializacion(Regla Padre1, Regla Padre2, Regla Hijo1, Regla Hijo2,char tipo_G_E){
		int NumeroAtributos=Padre1.getNumAtributos();
		   for (int nAtributo=0 ; nAtributo<NumeroAtributos ; nAtributo++) {
			   	int inicioAtributo=parametrosGlobales.getPlantillaAtributos()[nAtributo][1];
			   	int finAtributo=inicioAtributo+parametrosGlobales.getPlantillaAtributos()[nAtributo][0];			
			      if (parametrosGlobales.getGeneradorAleatorio().rand()<0.4)
			         for (int i=inicioAtributo; i<finAtributo ; i++) { 
				       char valBit='0';
				       if(tipo_G_E=='G'){
				    	   if((Padre1.getValor(i)==1)||(Padre2.getValor(i)==1)) valBit='1';
				       }else{
				    	   if((Padre1.getValor(i)==1)&&(Padre2.getValor(i)==1)) valBit='1';
				       }		       
				       Hijo1.setValor(i,valBit);
				       Hijo2.setValor(i,Hijo1.getValor(i));
			         }			      
				 else
				    for (int i=inicioAtributo; i<finAtributo ; i++) {
				    	Hijo1.setValor(i,Padre1.getValor(i));
				    	Hijo2.setValor(i,Padre2.getValor(i));
				    }
			   }
		return 1;		
	}

	
	/**
	 * Mutaci�n del individuo que se le pasa por referencia.
	 * @param hijo es la regla a mutar. 
	 */
	public void mutar(Regla hijo){		
		int longCromosoma=hijo.getLongitudCromosoma();
		for (int i=0 ; i<longCromosoma ; i++){
			if(parametrosGlobales.getGeneradorAleatorio().rand()<=parametrosGlobales.getProbabilidadMutacion()){
				if (hijo.getValor(i)=='1')hijo.setValor(i,'0');
				else hijo.setValor(i,'1');	   
			}
		}		
	}
	
	/**
	 * Ruleta utilizada en la selecci�n de
	 * individuos.
	*/
	private Ruleta roul;

	/**
	 * Inicializa la ruleta.
	 * @param pop es un conjunto de individuos entre los que se va a seleccionar uno.
	 */
	public void init(Solucion pop) {
		int i = 0;
		roul = new Ruleta(pop.getTama�o());
		for (i = 0; i < pop.getTama�o(); i++) {
			roul.add(pop.getRegla(i).getPI());
		}
	}         
	
	/**
	 * Selecciona una regla de la ruleta. Lanza la ruleta.
	 * @param sol es el conjunto de reglas.
	 * @return devuelve la regla que es seleccionada por la ruleta.
	 */
	public Regla seleccionaRegla(Solucion sol) {
		int i = roul.selectRuleta();
		return sol.getRegla(i);
	}      

}
