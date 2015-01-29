package edgar;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import Aleatorios.Aleatorio;
import Dataset.Attribute;
/**
 * 
 * 
 *	Esta clase implementa los operadores gen�ticos que utilizan los nodos.
 */
/**
 * @author Miguel Angel
 *
 */
public class Operador {

	
	static final int NUM_CRUCES=4;
	static final int CRUCE_UNIFORME=0;
	static final int CRUCE_2PT=1;
	static final int CRUCE_ESPECIALIZACION=2;
	static final int CRUCE_GENERALIZACION=3;
	
	
	
	
	/**
	 * Operador de sembrado, a partir de un ejemplo genera una regla que lo cubre positivamente.
	 * pero s�lo de parte de la regla , para hacer exploraci�n de la parte de atributos que le corresponda
	 * @param ej. Ejemplo al que se le va a generar una regla que lo cubra.
	 * @return regla. Devuelve una regla que cubre el ejemplo.
	 */
	public static Regla Sembrado(EjemploFuzzy votante,int inicio,int numAtributos){
//<-- Daniel Albend�n APROXIMATIVO
		Regla NuevaRegla;
		if(Parametros.getInstancia_Parametros().aproximativo())
			NuevaRegla=new Regla(votante.plantilla);
		else
			NuevaRegla = new Regla();
		int posInicio = votante.getPlantilla().posicionAtributo(inicio);
		int posFin =posInicio;
		for (int i = inicio ;i<numAtributos;i++)
			posFin += votante.getPlantilla().numValoresAtributo(i);
		Aleatorio al = Parametros.getInstancia_Parametros().get_GeneradorAleatorio();
		for(int i=posInicio;i<posFin;i++){	
			int valAleatorio=al.Randint(0,1);
			if (valAleatorio==1)NuevaRegla.setValor(i,'1');
			else NuevaRegla.setValor(i,'0');
		}
		for(int i_atr=inicio;i_atr<numAtributos;i_atr++){
				//Atributo de entrada
				// Si el atributo est� evaluado, hacemos que cumpla el ejemplo
				if (NuevaRegla.atributo_evaluado(i_atr)){
					int ind_aux=votante.getPlantilla().posicionAtributo(i_atr);
					// Cambio a 1 el m�nimo n�mero de valores
					for (int j=0;j<votante.getPlantilla().numValoresAtributo(i_atr);j++){
						if(votante.getValor(ind_aux+j).toString()=="1"){
							 NuevaRegla.setValor(ind_aux+j,'1');
						}
					}
				}			
		}		
		// Le asigno la clase del Ejemplo a la nueva regla
		NuevaRegla.setClase(votante.getClase());
		NuevaRegla.setInicio(inicio);
		NuevaRegla.setNumAtributosParcial(numAtributos);
		// Aqui he generado una regla completamente aleatoria, una cadena de bits aleatoria.
		return NuevaRegla;
	}
	
	
// <-- Daniel Albend�n
	/** Operador BLXAlpha. 
	 * 
	 * @param madre
	 * @param padre
	 * @param al
	 * @param tipoHijo
	 * @return
	 */
	
	static Plantilla pcBlxAlpha(Plantilla madre, Plantilla padre, Aleatorio al)
	{
		int tipoGeneracion = 1;
		// cambiar a una d�cima parte del blx alpha
		double variacionExtremos = 0.1;
		

	//	ArrayList[] madre, padre;
		int numAtributos = madre.get_TiposAtributos().length;
		double max = 0;
		double min = 0;
		double I = 0;
		ArrayList[] hijo = new ArrayList[numAtributos];
		
		
		for (int i = 0; i < numAtributos; i++)		//Para cada atributo
		{
			// Ser� null cuando sea un atributo nominal y no nos interese
			if(madre.get_TiposAtributos()[i] != Attribute.NOMINAL)
			{
				hijo[i] = new ArrayList();
				
				// El primer y el �ltimo valor de cada atributo no cambia
				hijo[i].add(madre.get_ValoresAtributos()[i].get(0));
				
				int numParticiones = madre.get_ValoresAtributos()[i].size();
				
				for (int j = 1; j < numParticiones-1;j++)
				{
					double x = (Double)(padre.get_ValoresAtributos()[i].get(j));
					double y = (Double)(madre.get_ValoresAtributos()[i].get(j));
					double valorI = Math.abs(x-y) * variacionExtremos;
					if(Math.abs(x-y)==0){
							valorI = valor_medio((Double)padre.get_ValoresAtributos()[i].get(j-1),(Double)padre.get_ValoresAtributos()[i].get(j+1),y);

					}
					double tempA = (Double)(madre.get_ValoresAtributos()[i].get(0));
					
					double tempResta;
					if(al.Rand()>0.5)
						tempResta = x - valorI;
					else
						tempResta = y - valorI;
					
					double ele1;
					if(tempA < tempResta)
					{
						ele1 = tempResta;
					}
					else
					{
						ele1 = tempA;
					}
					
					double tempB = (Double)(madre.get_ValoresAtributos()[i].get(numParticiones-1));
					
					double tempSuma;
					if(al.Rand()>0.5)
						tempSuma = x + valorI;
					else
						tempSuma = y + valorI;
					
					double u1;
					if(tempB < tempSuma)
					{
						u1 = tempB;
					}
					else
					{
						u1 = tempSuma;
					}
					
					double dif = Math.abs(u1 - ele1);
					
					double aleatorio = al.Rand() * dif;

					double puntoBueno;
					
					if(u1 < ele1)
					{
						puntoBueno = u1 + aleatorio;
					}
					else
					{
						puntoBueno = ele1 + aleatorio;
					}
					
					hijo[i].add(puntoBueno);
				}
				
				// El primer y el �ltimo valor de cada atributo no cambia
				hijo[i].add(madre.get_ValoresAtributos()[i].get(numParticiones-1));
			}
			/**
			 * @author Danien Albend�n
			 * No se hac�a nada cuando el atributo era nominal
			 */
			else{
				hijo[i] = new ArrayList();
				int num = madre.get_ValoresAtributos()[i].size();
				for(int s = 0; s < num; s++){
					hijo[i].add(madre.get_ValoresAtributos()[i].get(s));
				}
			}
		}
		
		// Validaci�n del individuo
		
		// Ponemos a m�nimo los valores menores que el m�nimo y al m�ximo los valores menores que el m�ximo
		for(int i=0;i < numAtributos; i++)
		{
			if(madre.get_TiposAtributos()[i] != Attribute.NOMINAL)
			{
				int numParticiones = hijo[i].size();
				
				min = ((Double)(hijo[i].get(0)));
				max = ((Double)(hijo[i].get(numParticiones-1)));
				
				for (int j = 1; j < numParticiones-1;j++)
				{
					if(((Double)(hijo[i].get(j))) < min)
					{
						hijo[i].remove(j);
						hijo[i].add(j, min);
					}
					else if((((Double)(hijo[i].get(j))) > max))
					{
						hijo[i].remove(j);
						hijo[i].add(j, max);
					}
				}
			}
		}
		
		// Ordenamos los valores de menor a mayor
		for(int i=0;i < numAtributos; i++)
		{
			if(madre.get_TiposAtributos()[i] != Attribute.NOMINAL)
			{
				int numParticiones = hijo[i].size();
				
				for (int j = 1; j < numParticiones-2;j++)
				{
					for(int k = 1; k < numParticiones-2; k++)
					{
						double temp = ((Double)(hijo[i].get(k)));
						double compara = ((Double)(hijo[i].get(k+1)));
						
						if(temp > compara)
						{
							hijo[i].remove(k);
							hijo[i].add(k, compara);
							hijo[i].remove(k+1);
							hijo[i].add(k+1, temp);
						}
					}
				}
			}
		}
		
		return (new Plantilla(madre,hijo));	
	}
// -->
	
	static Plantilla blxAlpha(Plantilla madre, Plantilla padre, Aleatorio al)
	{
		int numAtributos = madre.get_TiposAtributos().length;
		double max = 0;
		double min = 0;
		double I = 0;
		ArrayList[] hijo = new ArrayList[numAtributos];
		double variacionExtremos = 0.1;
		
		for (int i = 0; i < numAtributos; i++)		//Para cada atributo
		{
			// Ser� null cuando sea un atributo nominal y no nos interese
			if(madre.get_TiposAtributos()[i] != Attribute.NOMINAL)
			{
				hijo[i] = new ArrayList();
				
				// El primer y el �ltimo valor de cada atributo no cambia
				hijo[i].add(madre.get_ValoresAtributos()[i].get(0));
				
				int numParticiones = madre.get_ValoresAtributos()[i].size();
				
				for (int j = 1; j < numParticiones-1;j++)
				{
					if ((Double)(madre.get_ValoresAtributos()[i].get(j)) < (Double)(padre.get_ValoresAtributos()[i].get(j)))
					{	
						max = (Double)(padre.get_ValoresAtributos()[i].get(j));
						min = (Double)(madre.get_ValoresAtributos()[i].get(j));
					}
					else							//Si el gen de la madre es mayor
					{
						min = (Double)(padre.get_ValoresAtributos()[i].get(j));
						max = (Double)(madre.get_ValoresAtributos()[i].get(j));
					}					 
					I = Math.abs(max - min);	//Obtenemos la diferencia
					min = min - (I * variacionExtremos);	//Calculamos el extremo inferior
					max = max + (I * variacionExtremos);	//Calculamos el extremo superior
					hijo[i].add(min + al.Rand() * (max - min));	//Calculamos un punto aleatorio dentro del intervalo
				}
				
				// El primer y el �ltimo valor de cada atributo no cambia
				hijo[i].add(madre.get_ValoresAtributos()[i].get(numParticiones-1));
			}			
			/**
			 * @author Danien Albend�n
			 *  No se hac�a nada cuando el atributo era nominal
			 */
			else{
				hijo[i] = new ArrayList();
				int num = madre.get_ValoresAtributos()[i].size();
				for(int s = 0; s < num; s++){
					hijo[i].add(madre.get_ValoresAtributos()[i].get(s));
				}
			}

		}
		
		// Validaci�n del individuo
		
		// Ponemos a m�nimo los valores menores que el m�nimo y al m�ximo los valores menores que el m�ximo
		for(int i=0;i < numAtributos; i++)
		{
			if(madre.get_TiposAtributos()[i] != Attribute.NOMINAL)
			{
				int numParticiones = hijo[i].size();
				
				min = ((Double)(hijo[i].get(0)));
				max = ((Double)(hijo[i].get(numParticiones-1)));
				
				for (int j = 1; j < numParticiones-1;j++)
				{
					if(((Double)(hijo[i].get(j))) < min)
					{
						hijo[i].remove(j);
						hijo[i].add(j, min);
					}
					else if((((Double)(hijo[i].get(j))) > max))
					{
						hijo[i].remove(j);
						hijo[i].add(j, max);
					}
				}
			}
		}
		
		// Ordenamos los valores de menor a mayor
		for(int i=0;i < numAtributos; i++)
		{
			if(madre.get_TiposAtributos()[i] != Attribute.NOMINAL)
			{
				int numParticiones = hijo[i].size();
				
				for (int j = 1; j < numParticiones-2;j++)
				{
					for(int k = 1; k < numParticiones-2; k++)
					{
						double temp = ((Double)(hijo[i].get(k)));
						double compara = ((Double)(hijo[i].get(k+1)));
						
						if(temp > compara)
						{
							hijo[i].remove(k);
							hijo[i].add(k, compara);
							hijo[i].remove(k+1);
							hijo[i].add(k+1, temp);
						}
					}
				}
			}
		}
		
		return new Plantilla(madre,hijo);	
	}
	/**
	 * Operador de sembrado, a partir de un ejemplo genera una regla que lo cubre positivamente.
	 * @param ej. Ejemplo al que se le va a generar una regla que lo cubra.
	 * @return regla. Devuelve una regla que cubre el ejemplo.
	 */
	public static  Regla Sembrado(EjemploFuzzy ejemploFuzzy){
		Regla NuevaRegla=new Regla(ejemploFuzzy.getPlantilla());
		for(int i=0;i<NuevaRegla.getLongitudCromosoma();i++){	
			int valAleatorio=Parametros.getInstancia_Parametros().get_GeneradorAleatorio().Randint(0,1);
			if (valAleatorio==1)NuevaRegla.setValor(i,'1');
			else NuevaRegla.setValor(i,'0');
		}
		for(int i_atr=0;i_atr<NuevaRegla.getNumAtributos();i_atr++){
				//Atributo de entrada
				// Si el atributo est� evaluado, hacemos que cumpla el ejemplo
				if (NuevaRegla.atributo_evaluado(i_atr)){
					int ind_aux=ejemploFuzzy.getPlantilla().posicionAtributo(i_atr);
					// Cambio a 1 el m�nimo n�mero de valores
					for (int j=0;j<ejemploFuzzy.getPlantilla().numValoresAtributo(i_atr);j++)
					{
						if(ejemploFuzzy.getValor(ind_aux+j).toString()=="1"){
							 NuevaRegla.setValor(ind_aux+j,'1');
						}
					}
				}			
		}		
		// Le asigno la clase de la nueva regla
		NuevaRegla.setClase(ejemploFuzzy.getClase());
		// Aqui he generado una regla completamente aleatoria, una cadena de bits aleatoria.
		return NuevaRegla;
	}
	
	
	
	/**
	 * Operador de sufragio universal, Operador de selecci�n. 
	 * Selecciona el conjunto de reglas que se van a  participar en la reproducci�n.
	 * @param datosEntrenamiento Datos de entrenamiento.
	 * @param Reglas Individuos entre los que se van a seleccionar un subconjunto de individuos.
	 * @param m tama�o de la poblaci�n.
	 * @return Conjuntos de individuos seleccionados para la reproducci�n.
	 */
	
	
	public static Solucion US(Dataset datosEntrenamiento,Solucion Reglas,int m){
		Seleccion SelectorRuleta=null;
		Solucion B=new Solucion();
		Parametros param_globales = Parametros.getInstancia_Parametros();
		// primer Paso es selecionar el conjundo de ejemplos que van ha votar por una regla.
		int num_Individuos_Votan=(int)(param_globales.get_g()*m);
		ArrayList Indices_Poblacion_Total=new ArrayList();
		int Individuos_Votan[];
		
		for(int i=0;i<datosEntrenamiento.getTama�o_conjunto_entrenamiento();i++) Indices_Poblacion_Total.add(new Integer(i));
		
		if(num_Individuos_Votan<datosEntrenamiento.getTama�o_conjunto_entrenamiento()){
			Individuos_Votan=new int[num_Individuos_Votan];
			for(int i=0;i<num_Individuos_Votan;i++){
				int indice_aleatorio=param_globales.get_GeneradorAleatorio().Randint(0,Indices_Poblacion_Total.size()-1);
				Individuos_Votan[i]=Integer.parseInt(Indices_Poblacion_Total.get(indice_aleatorio)+"");
				Indices_Poblacion_Total.remove(indice_aleatorio); // Versi�n v1.2 Sino el mismo ejemplo puede votar varias veces
				
			}
		}else{  // En el caso de que queden menos o igual n�mero de ejemplos que ejemplos hay que selecionas
			//num_Individuos_Votan=ejemplos.getTama�o_conjunto_entrenamiento();
			
			num_Individuos_Votan=datosEntrenamiento.getTama�o_conjunto_entrenamiento();
			Individuos_Votan=new int[num_Individuos_Votan];
			for(int i=0;i<num_Individuos_Votan;i++) Individuos_Votan[i]=Integer.parseInt(Indices_Poblacion_Total.get(i)+"");
			
					
			
		}
		
		// En la Tabla Individuos_Votan tengo los indices de los individuos 
		//que van ha votar por una regla
			
		//Para cada ejemplo debo crear una ruleta de las reglas que lo cubren
		for (int i=0;i<num_Individuos_Votan;i++){
			// Obtengo el votante
			int indice_votante=Individuos_Votan[i];
			
	         if (indice_votante > datosEntrenamiento.getTama�o_conjunto_entrenamiento())
	        	 System.out.println(datosEntrenamiento);//para capturar el problema 
			EjemploFuzzy votante=new EjemploFuzzy(); 
				try{votante=(EjemploFuzzy)datosEntrenamiento.get_EjemploFuzzy(indice_votante);}
			catch(Exception e){
				System.out.println(e);
			}
			Solucion reglas_cubren_Ejemplo=new Solucion();
			for (int j=0;j<Reglas.getTama�o_solucion();j++){
				Regla regla=Reglas.get_regla(j);
				if (regla.Cubre_Ejemplo_Pos(votante)){ // Parte importante, el concepto de cubrir un ejemplo va a cambiar
					reglas_cubren_Ejemplo.insertarRegla(regla);
				}
			}
			if (reglas_cubren_Ejemplo.getTama�o_solucion()>0){
				// Si existen reglas que cubren el ejemplo, utilizamos la ruleta para selecionar una.
				SelectorRuleta=new SeleccionRuleta();
				SelectorRuleta.init(reglas_cubren_Ejemplo);
				B.insertarRegla(SelectorRuleta.SeleccionaRegla(reglas_cubren_Ejemplo));
			}else{
				//Si no hemos encontrado ninguna regla que cubra el ejemplo, la creamos con el operador de sembrado.
				Regla NuevaRegla;
					//s�lo se usa si se hce exploraci�n de features por nodo
					if (param_globales.isParcial() )
						NuevaRegla=Operador.Sembrado(votante,Reglas.get_regla(1).getInicio(),Reglas.get_regla(1).getNumAtributosParcial());
					else	
						NuevaRegla=Operador.Sembrado(votante);
				B.insertarRegla(NuevaRegla);		
			}
		}
		return B;
	}
	
	public static Solucion US_sinSembrado(Dataset ejemplos,Solucion Reglas,int m){
		Seleccion SelectorRuleta=null;
		Solucion B=new Solucion();
		Parametros param_globales = Parametros.getInstancia_Parametros();
		// primer Paso es selecionar el conjundo de ejemplos que van ha votar por una regla.
		int num_Individuos_Votan=(int)(param_globales.get_g()*m);
		ArrayList Indices_Poblacion_Total=new ArrayList();
		int Individuos_Votan[];
		
		for(int i=0;i<ejemplos.getTama�o_conjunto_entrenamiento();i++) Indices_Poblacion_Total.add(new Integer(i));
		
		if(num_Individuos_Votan<ejemplos.getTama�o_conjunto_entrenamiento()){
			Individuos_Votan=new int[num_Individuos_Votan];
			for(int i=0;i<num_Individuos_Votan;i++){
				int indice_aleatorio=param_globales.get_GeneradorAleatorio().Randint(0,Indices_Poblacion_Total.size()-1);
				Individuos_Votan[i]=Integer.parseInt(Indices_Poblacion_Total.get(indice_aleatorio)+"");
				Indices_Poblacion_Total.remove(indice_aleatorio); // Versi�n v1.2 Sino el mismo ejemplo puede votar varias veces
				
			}
		}else{  // En el caso de que queden menos o igual n�mero de ejemplos que ejemplos hay que selecionas
			//num_Individuos_Votan=ejemplos.getTama�o_conjunto_entrenamiento();
			
			num_Individuos_Votan=ejemplos.getTama�o_conjunto_entrenamiento();
			Individuos_Votan=new int[num_Individuos_Votan];
			for(int i=0;i<num_Individuos_Votan;i++) Individuos_Votan[i]=Integer.parseInt(Indices_Poblacion_Total.get(i)+"");
			
					
			
		}
		
		// En la Tabla Individuos_Votan tengo los indices de los individuos 
		//que van ha votar por una regla
			
		//Para cada ejemplo debo crear una ruleta de las reglas que lo cubren
		for (int i=0;i<num_Individuos_Votan;i++){
			// Obtengo el votante
			int indice_votante=Individuos_Votan[i];
	         if (indice_votante > ejemplos.getTama�o_conjunto_entrenamiento())
	        	 System.out.println(ejemplos);//para capturar el problema 
			EjemploFuzzy votante= ejemplos.get_EjemploFuzzy(indice_votante);
			Solucion reglas_cubren_Ejemplo=new Solucion();
			for (int j=0;j<Reglas.getTama�o_solucion();j++){
				Regla regla=Reglas.get_regla(j);
				if (regla.Cubre_Ejemplo_Pos(votante)){
					reglas_cubren_Ejemplo.insertarRegla(regla);
				}
			}
			if (reglas_cubren_Ejemplo.getTama�o_solucion()>0){
				// Si existen reglas que cubren el ejemplo, utilizamos la ruleta para selecionar una.
				SelectorRuleta=new SeleccionRuleta();
				SelectorRuleta.init(reglas_cubren_Ejemplo);
				B.insertarRegla(SelectorRuleta.SeleccionaRegla(reglas_cubren_Ejemplo));
			}
		}
		return B;
	}
	
/**
 * Este m�todo se utiliza para decidir qu� tipo de cruce se va a utilizar.
 * @param parent1 Individuo a cruzar.
 * @param parent2 Individuo a cruzar.
 * @param ejemplos Datos de entrenamientos
 * @return Devuelve un identificador del cruce a aplicar.
 */
	
	
	public static int Selector_Tipo_Cruce(Regla parent1, Regla parent2, Dataset ejemplos){
			double probabilidades[]=new double[NUM_CRUCES];
			double prob=0;
			double r=0;
			double fm=0;				

			fm=(double)((parent1.getfitness()+parent2.getfitness())/2);			
			int n_positivo_parent1=0;
			int n_negativo_parent1=0;
			int n_positivo_parent2=0;
			int n_negativo_parent2=0;
			
			for(int i=0;i<parent1.getPlantilla().get_numero_Clases();i++){
				if(parent1.getClase()==i)
					n_positivo_parent1+=parent1.getNumEjemplosCubiertos()[i];
				else 
					n_negativo_parent1+=parent1.getNumEjemplosCubiertos()[i];
				
				if(parent2.getClase()==i)
					n_positivo_parent2+=parent2.getNumEjemplosCubiertos()[i];
				else 
					n_negativo_parent2+=parent2.getNumEjemplosCubiertos()[i];
			}
			
		
			r=((double)(n_positivo_parent1+n_negativo_parent1+n_positivo_parent2+n_negativo_parent2)/(double)(ejemplos.getTama�o_conjunto_entrenamiento()^2));
			Parametros param_globales=Parametros.getInstancia_Parametros();
			
			double a=param_globales.get_a();
			double b=param_globales.get_b();
			
			probabilidades[CRUCE_UNIFORME]=(1 - a*fm)*b;	
			probabilidades[CRUCE_2PT]=(1 - a*fm)*(1-b);
			probabilidades[CRUCE_ESPECIALIZACION]= fm * a * r;
			probabilidades[CRUCE_GENERALIZACION]= fm * a * (1 - r);
		
			prob=param_globales.get_GeneradorAleatorio().Rand();	
			   int idCruce = 0;
			   while (prob >= probabilidades[idCruce]){
				   prob-=probabilidades[idCruce];
				   idCruce++;
			   }			   
			   return(idCruce);	
	}
	
	
	
	
	/**
	 * Este m�todo es el encargado de aplicar el cruce entre dos individuos seleccionados.
	 * @param padre1 Individuo a cruzar.
	 * @param padre2 Individuo a cruzar.
	 * @param hijo1 Este es un par�metro que se pasa por referencia, y el se devuelve un hijo generado por el cruce.
	 * @param hijo2 Este es un par�metro que se pasa por referencia, y el se devuelve un hijo generado por el cruce.
	 * @param ejemplos
	 * @return Devuelve el identificador del cruce aplicado.
	 */
	
	public static int  CruzarCromosmas(Regla padre1,Regla padre2,Regla hijo1,Regla hijo2,Dataset ejemplos ){
		
		int idCruce=Selector_Tipo_Cruce(padre1,padre2,ejemplos);
		//ojo marr
		//idCruce = 2;
		switch (idCruce){
		case 0:
			cruce_U(padre1,padre2,hijo1,hijo2);
			break;
		case 1:
			cruce_2pt(padre1,padre2,hijo1,hijo2);
			break;
		case 2:
			cruce_Generalizacion_Especializacion(padre1,padre2,hijo1,hijo2,'E');
			break;
		case 3:
			cruce_Generalizacion_Especializacion(padre1,padre2,hijo1,hijo2,'G');
			break;
		}
		//elimina los atributos no validos
		//hijo1.LimpiaRegla();
		//hijo2.LimpiaRegla();
		return idCruce;
	}
	
	/**
	 * Operador de cruce 2 puntos, se selecciona dos puntos de forma aleatoria de las reglas Padre1 y Padre2
	 * en los par�metros hijo1 y hijo2, se devuelven las dos nuevas reglas generadas.
	 * @param Padre1 
	 * @param Padre2
	 * @param Hijo1
	 * @param Hijo2
	 * @return 1
	 */
	
	public static int cruce_2pt(Regla Padre1, Regla Padre2, Regla Hijo1, Regla Hijo2){		
		Parametros param_globales=Parametros.getInstancia_Parametros();
		int longCromosoma=Padre1.getLongitudCromosoma();
		int primer_Punto_Cruce=param_globales.get_GeneradorAleatorio().Randint(0,longCromosoma-1);
		int segundo_Punto_Cruce=param_globales.get_GeneradorAleatorio().Randint(0,longCromosoma-1);
		
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
 * una probalilidad del 50% de pertenecer a una regla padre1 o a la padre2.
 * @param Padre1 Regla padre 1
 * @param Padre2 Regla padre 2
 * @param Hijo1  regla hijo 1
 * @param Hijo2	regla hijo 2
 * @return Devuelve 1 si todo ha ido bien.
 */
	
	
	public static int cruce_U(Regla Padre1, Regla Padre2, Regla Hijo1, Regla Hijo2){
		int longCromosoma=Padre1.getLongitudCromosoma();
		Parametros param_globales=Parametros.getInstancia_Parametros();
		   for (int i=0 ; i<longCromosoma ; i++)
		      if (param_globales.get_GeneradorAleatorio().Rand()<0.5) {
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
	 * para el 40% de los puntos realiza un or (especializa) o un and (generaliza) bit a bit entre los dos padres 
	 * para generar dos hijos
	 * @param Padre1
	 * @param Padre2
	 * @param Hijo1:debe contener una regla vacia
	 * @param Hijo2:debe contener una regla vacia
	 * @param tipo_G_E: Si generaliza 'G' , si especializa 'E'
	 * 
	 */
	public static int cruce_Generalizacion_Especializacion(Regla Padre1, Regla Padre2, Regla Hijo1, Regla Hijo2,char tipo_G_E){
		Parametros param_globales=Parametros.getInstancia_Parametros();
		int inicioAtributo=0;
		int finAtributo=0;
		int NumeroAtributos=Padre1.getNumAtributos();
		   for (int nAtributo=0 ; nAtributo<NumeroAtributos ; nAtributo++) {
//<-- Daniel Albend�n
			   /**
			    * Cambiamos la plantilla global por la de una regla. Ahora mismo, ya que vamos 
			    * a implementar las plantillas con el mismo tama�o y n�mero de atributos nos da
			    * igual la longitud. Cuando la longitud de ambas plantillas sean variables,
			    * tendremos que retocar este c�digo. (ELSE)
			   */
			   if(!param_globales.aproximativo()){
			   	inicioAtributo=param_globales.getPlantilla().posicionAtributo(nAtributo);
			   	finAtributo=inicioAtributo+param_globales.getPlantilla().numValoresAtributo(nAtributo);			
			   }
			   else{
				    inicioAtributo= Padre1.getPlantilla().numValoresAtributo(nAtributo);
				   	finAtributo=inicioAtributo+Padre1.getPlantilla().numValoresAtributo(nAtributo);			
			   }
			   // -->
			   if (param_globales.get_GeneradorAleatorio().Rand()<0.4)
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
	 * Este m�todo aplica la mutaci�n al individuo que se le pasa por referencia, 
	 * generalizando el primer valor que se encuentre a uno en un cromosoma
	 * @param hijo regla a mutar. 
	 */
	public static void Generalizar(Regla hijo){		
	int longCromosoma=hijo.getLongitudCromosoma();
	Parametros param_globales=Parametros.getInstancia_Parametros();
	int inicio = param_globales.get_GeneradorAleatorio().Randint(0,longCromosoma);
	boolean noEncontrado= true;
	for (int i=0 ; i<longCromosoma && noEncontrado; i++){
		int pos = (i+inicio)%longCromosoma;
		if (hijo.getValor(pos)=='1'){
			hijo.setValor(pos,'0');
			noEncontrado=false;
		}
			   
		}
	BorrarAtributo(hijo);
	}		
	
	
	public static void BorrarAtributo(Regla hijo){		
		
		Parametros param_globales=Parametros.getInstancia_Parametros();
		int ind = param_globales.get_GeneradorAleatorio().Randint(0,hijo.getNumAtributos()-1);
		hijo.borraAtributo(ind);
	}	
	
	
	public static void Mutar(Regla hijo){
		int inicio=0;int numAtributos=1;
		int posInicio = hijo.getPlantilla().posicionAtributo(inicio);
		int posFin =posInicio;
		for (int i = inicio ;i<numAtributos;i++)
			posFin += hijo.getPlantilla().numValoresAtributo(i);
		Aleatorio al = Parametros.getInstancia_Parametros().get_GeneradorAleatorio();
		double pMutacion =Parametros.getInstancia_Parametros().get_Pmutacion();
		int longCromosoma=hijo.getLongitudCromosoma();
		for (int i=posInicio ; i<posFin ; i++){
			if(al.Rand()<=pMutacion){
				if (hijo.getValor(i)=='1')hijo.setValor(i,'0');
				else hijo.setValor(i,'1');	   
			}
		}	
		//hijo.LimpiaRegla();
	}

	/*
	public static void Mutar(Plantilla hijo){
		int inicio=0;int numAtributos=hijo.get_ValoresAtributos().length;
		int posInicio = hijo.posicionAtributo(inicio);
		int posFin =posInicio;
		for (int i = inicio ;i<numAtributos;i++){
			posFin += hijo.numValoresAtributo(i);
			Aleatorio al = Parametros.getInstancia_Parametros().get_GeneradorAleatorio();
			double pMutacion =Parametros.getInstancia_Parametros().get_Pmutacion();
			for (int i=posInicio ; i<posFin ; i++){
				if(al.Rand()<=pMutacion){
					double valor = (Double) hijo.get_ValoresAtributos()[0].get(i);
					if(valor != 0.0){
						
					}
				}
			}	
		}
		//hijo.LimpiaRegla();
	}*/

/**
 * Cuando coincidan dos valores a la hora de realizar el pbLXAlpha devolvemos un rango calculado en base al punto
 * medio entre el punto anterior y el punto posterior.
 * @param inicio : Punto anterior
 * @param fin : Punto siguiente
 * @param valor : Punto en cuesti�n
 * @return Rango restado a valor./
 */
private static double valor_medio(double inicio, double fin,double valor) {
	double vm = Math.abs((fin-inicio)/2);
	if(vm == valor){
		vm = Math.abs((fin-inicio)/4);		
	}
	return Math.abs(valor - vm);
}


/*
 * 
 * C�DIGO CREADO POR JOS� MANUEL GARRIDO MORGADO
 * 
 */

	public static Solucion TokenCompetition(Dataset ejemplos,Solucion Reglas,int m)
	{
		int num_reglas = Reglas.getTama�o_solucion();
		int num_ejemplos = ejemplos.getTama�o_conjunto_entrenamiento();
		
		double fitness_reglas[] = new double[num_reglas];
		
		for(int i=0;i<num_reglas;i++)
		{
			Regla r_actual = Reglas.get_regla(i);
			double fitness_r_actual = r_actual.evaluar_Fitness();
			fitness_reglas[i] = fitness_r_actual;
			
			int num_ejemplos_que_cubre = 0;
			
			for(int j=0;j<num_ejemplos;j++)
			{
				EjemploFuzzy e_actual = ejemplos.get_EjemploFuzzy(j);
				
				if(r_actual.Cubre_Ejemplo(e_actual)>Parametros.getInstancia_Parametros().getCoberturaFuzzy())
				{
					r_actual.add_ejemplo_cubierto(j);
					num_ejemplos_que_cubre++;
				}
			}
			
			for(int j=0;j<num_ejemplos_que_cubre;j++)
			{
				ArrayList<Integer> ejemplos_cubiertos = r_actual.getIds_ejemplos_cubiertos();

				int id_e_actual = ejemplos_cubiertos.get(j);
				EjemploFuzzy e_actual = ejemplos.get_EjemploFuzzy(id_e_actual);
				// La regla actual lucha con todas las reglas anteriores por poseer el ejemplo actual
				
				for(int z=0;z<i;z++)
				{
					Regla r_contrincante = Reglas.get_regla(z);
					if(r_contrincante.Cubre_Ejemplo(e_actual)>Parametros.getInstancia_Parametros().getCoberturaFuzzy())
					{
						if(fitness_r_actual < r_contrincante.evaluar_Fitness())
						{
							r_actual.remove_ejemplo_poseido(id_e_actual);
							
							// Si no pose�a este ejemplo, se lo asignamos como suyo
							if(!r_contrincante.getIds_ejemplos_poseidos().contains(id_e_actual))
								r_contrincante.add_ejemplo_poseido(id_e_actual);
							
						}
						else
						{
							r_contrincante.remove_ejemplo_poseido(id_e_actual);
							
							// Si no pose�a este ejemplo, se lo asignamos como suyo
							if(!r_actual.getIds_ejemplos_poseidos().contains(id_e_actual))
								r_actual.add_ejemplo_poseido(id_e_actual);
						}
					}
				}
			}
		}
		
		
		HashMap<Integer, Double> fitness_individuo = new HashMap<Integer, Double>();
		ValueComparatorMayorMenor comparator = new ValueComparatorMayorMenor(fitness_individuo);
		TreeMap<Integer, Double> sorted_list = new TreeMap(comparator);
		// Ahora que ya han luchado todas las reglas con todas por poseer los ejemplos y cada una tiene de forma
		// definitva los ejemplos que posee, vamos a calcular los nuevos fitness, y devolveremos las m reglas mejor fitness 
		for(int i=0;i<num_reglas;i++)
		{
			// Metemos en el HashMap el �ndice de la regla (la i) y su fitness, para luego ordenarlas m�s f�cilmente
			fitness_individuo.put(i, Reglas.get_regla(i).calcula_fitness_token());
		}
		
        sorted_list.putAll(fitness_individuo); 
        
        int num_elitistas = 5;
        int introducidos = 0;
        
        Set<Integer> ordenados = sorted_list.keySet();
        
        ArrayList reglas_elegidas = new ArrayList();
        
        int reglas_introducidas = 0;

        for (Integer individuo : ordenados) 
        { 
        	if(reglas_introducidas < m)
            {
        		reglas_elegidas.add(Reglas.get_regla(individuo));
        		reglas_introducidas++;
            }
        	else
        		break;
        } 
        
        Reglas = new Solucion(reglas_elegidas);
        return Reglas;
	}
	
}
