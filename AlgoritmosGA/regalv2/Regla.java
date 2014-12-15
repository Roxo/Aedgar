package regalv2;

/**
 * <p>Regla implementa las reglas y todos los métodos necesarios para su utilización.</p>
 * @author José Luis Toscano Muñoz
 * @version Regal v2.0
 */

public class Regla extends Cromosoma {
	
	/**
	 * Fitness de la regla.
	 */
	private double fitness;
	
	/**
	 * PI de una regla. (PI= fitness * N+)
	 */
	private double PI;
	
	/**
	 * Es una instancia del objeto ParametrosGlobales con los parámetros de configuración de la ejecución.
	 */
	private ParametrosGlobales parametrosGlobales=ParametrosGlobales.getInstancia_Parametros();
	
	/**
	 * Es una tabla de enteros donde cada indice corresponde a una clase, 
	 * y en cada posición se almacena el número de ejemplos de esa clase que cubren la regla.
	 */
	private int numeroEjemplosCubiertos[];
	
	
	/**
	 * Constructor de la clase.
	 *
	 */
	public Regla(){
		fitness=0;
		PI=0;
		int numeroBits=0;
		for(int i=0;i<numAtributos;i++) numeroBits=numeroBits+parametrosGlobales.getPlantillaAtributos()[i][0];
		cromosoma=new char[numeroBits];
		for(int i=0;i<numeroBits;i++) cromosoma[i]='0';
		numeroEjemplosCubiertos=new int[parametrosGlobales.getNumeroClases()];
		for(int i=0;i<parametrosGlobales.getNumeroClases();i++) numeroEjemplosCubiertos[i]=0;
	}
	
	/**
	 * Devuelve el número de casos cubiertos negativamente por la regla.
	 * @return el número de casos cubiertos negativamente.
	 */
	public int getNumeroCasosNegativos(){
		int cont_neg=0;
		for(int i=0;i<numeroEjemplosCubiertos.length;i++){
			if(i!=clase) cont_neg+=numeroEjemplosCubiertos[i];
		}
		return cont_neg;
	}
	
	/**
	 * Devuelve el número de casos cubiertos positivamente por la regla.
	 * @return el número de casos cubiertos positivamente.
	 */
	public int getNumeroCasosPositivos(){
		return numeroEjemplosCubiertos[clase];
	}
	
	/**
	 * Devuelve una tabla con el número de casos cubiertos por la regla para cada clase.
	 * Cada posición de la tabla corresponde a una clase
	 * @return una tabla con el número de casos cubiertos por la regla para cada clase.
	 */
	public int[] getNumeroEjemplosCubiertos(){
		return numeroEjemplosCubiertos;
	}
	
	/**
	 * Actualiza una posición de la tabla de los casos cubiertos por clase.
	 * @param ind_clase es la clase a modificar.
	 * @param val es el valor que se va a signar a la clase.
	 */
	public void setNumeroEjemplosCubiertos(int ind_clase,int val){
		numeroEjemplosCubiertos[ind_clase]=val;
	}
	
	/**
	 * Establece el valor del fitness.
	 * @param _fitness es el valor del fitness a asignar a la regla.
	 */
	public void setFitness(double _fitness){
		fitness=_fitness;
	}
	
	/**
	 * Devuelve el valor del fitness de la regla.
	 * @return un double con el valor del fitness de la regla. 
	 */
	public double getFitness(){
		return fitness;
	}
	
	/**
	 * Establece el valor de PI.
	 * @param _PI es el valor de PI a asignar a la regla
	 */
	public void setPI(double _PI){
		PI=_PI;
	}
	
	/**
	 * Devuelve le valor de PI de la regla.
	 * @return un double con el valor de PI de la regla. 
	 */
	public double getPI(){
		return PI;
	}
	
	/**
	 * Devuelve un booleano indicando si un atributo de la regla es evaluado o no.
	 * @param indAtributo índice del atributo a comprobar si es evaluado.
	 * @return un booleano indicando si el atributo es evaluado.
	 */
	public boolean atributoEvaluado(int indAtributo){
		boolean evaluado=false;
		int i=0;
		int long_atr=parametrosGlobales.getPlantillaAtributos()[indAtributo][0];
		int inicio_atr=parametrosGlobales.getPlantillaAtributos()[indAtributo][1];
		while ((!evaluado)&&(i<long_atr)){
			if(cromosoma[inicio_atr+i]=='1') evaluado=true;
			i++;
		}		
		return evaluado;
	}
	
	/**
	 * Devuelve un booleano indicando si la regla evalúa algún atributo.
	 * @return un booleano indicando si la regla evalúa algún atributo.
	 */
	public boolean reglaEvaluada(){
		boolean regla_evaluada=false;
		int i=0;
	
		while ((!regla_evaluada)&&(i<numAtributos)){
			if(atributoEvaluado(i)) regla_evaluada=true;
			i++;
		}		
		return regla_evaluada;
	}
	
	
	/**
	 * Devuelve un booleano indicando si el atributo de un ejemplo es cubierto por la regla.
	 * @param indAtr es el índice del atributo.
	 * @param atr_ej es una tabla de char con el valor del atributo del ejemplo.
	 * @return un booleano indicando si la regla cubre el atributo.
	 */
	public boolean cumpleAtributo(int indAtr, char []atr_ej){
		boolean cumpleAtr=true;
		if (atr_ej==null)
			return false;
		int i=0;
		int ind_pos_atr=parametrosGlobales.getPlantillaAtributos()[indAtr][1];
		while((i<parametrosGlobales.getPlantillaAtributos()[indAtr][0])&&(cumpleAtr)){
			if((atr_ej[i]=='1')&&(cromosoma[ind_pos_atr+i]=='0')) cumpleAtr=false;
			i++;
		}	
		return cumpleAtr;
	}
	 
	/**
	 * Devuelve un booleano indicando si la regla cubre al ejemplo.
	 * EL siguiente método retorna verdadero, en el caso de que la regla cubra al ejemplo que le pasamos como parámetro.
	 * @param  ej es el Ejemplo a comprobar si es cubierto por la regla.
	 */
	public boolean cubreEjemplo(Ejemplo ej){
		boolean cubreRegla=true;
		int numAtributosEvaluados=0;
		if(ej==null){
			return false;
		}
		int i=0;
		while((i<numAtributos)&&(cubreRegla)){
				if(atributoEvaluado(i)){
					numAtributosEvaluados++;
					if (!cumpleAtributo(i,ej.getAtributo(i))) cubreRegla=false;  
				}		
			i++;
		}
		if (numAtributosEvaluados==0) cubreRegla=false;
		return cubreRegla;
	}
	
	/**
	 * Devuelve un booleano indicando si la regla cubre positivamente un ejemplo.
	 * @param ej es el Ejemplo a comprobar si es cubierto por la regla.
	 * @return un booleano indicando si el ejemplo es cubierto positivamente por la regla.
	 */
	public boolean cubreEjemploPositivamente(Ejemplo ej){
		if(ej.getClase()==clase)
			if (cubreEjemplo(ej)) 
				return true;
		return false;
	}
	
	/**
	 * Devuelve el valor de un atributo de la regla.
	 * @param indAtributo es el índice del atributo.
	 * @return una tabla de char con el valor del atributo.
	 */
	public char[] getAtributo(int indAtributo){
		char Atributo[]=new char[parametrosGlobales.getPlantillaAtributos()[indAtributo][0]];
		int inicio_atr=parametrosGlobales.getPlantillaAtributos()[indAtributo][1];
		for (int i=0;i<Atributo.length;i++)
			Atributo[i]=cromosoma[inicio_atr+i];
		return Atributo;
	}
	

	/**
	 * Devuelve un string con la representación de la regla.
	 * @return un string con la representación de la regla.
	 */
	public String getTextoRegla(){
		String cadena_Regla="";
		int numero_conjunciones=0;
		for(int i=0;i<numAtributos;i++){
			if(atributoEvaluado(i)){
					if (numero_conjunciones>0) cadena_Regla=cadena_Regla+" AND ";
					numero_conjunciones++;
					
					String nomAtr=parametrosGlobales.getNombresAtributos()[i];
					cadena_Regla=cadena_Regla+" ( "+nomAtr+"= ";
					
					int inicio_atributo=parametrosGlobales.getPlantillaAtributos()[i][1];
					cadena_Regla+= " (";
							
					int numero_disyunciones=0;
					String disy=" || ";

					for(int aux=0;aux<parametrosGlobales.getPlantillaAtributos()[i][0];aux++){
						int aux_ind=aux+inicio_atributo;
						if(cromosoma[aux_ind]=='1') {
							if (numero_disyunciones>0) cadena_Regla+=disy;
							numero_disyunciones++;
							String valAtributo=parametrosGlobales.getValoresAtributos()[i].get(aux)+"";
							cadena_Regla=cadena_Regla+valAtributo;
						}
					}
					cadena_Regla=cadena_Regla+" ) ) ";
				}
			}					
					cadena_Regla=cadena_Regla+" --> ";
					cadena_Regla=cadena_Regla+" ( "+parametrosGlobales.getNombreClase()+" = "+ parametrosGlobales.getValoresClase().get(clase) +")";
					
		return cadena_Regla;
	}
	
	/**
	 * Devuelve una copia de la regla. 
	 * @return una copia de la Regla.
	 */
	public Regla getCopia(){
		Regla copia=new Regla();
		copia.setValorCromosoma(cromosoma);
		copia.setClase(clase);
		for(int i=0;i<numeroEjemplosCubiertos.length;i++)
			copia.setNumeroEjemplosCubiertos(i,numeroEjemplosCubiertos[i]);
		copia.setFitness(fitness);
		copia.setPI(PI);
		return copia;
	}
	
	
	
	
	/**
	 * Devuelve el número de '0' que tiene un individuo. 
	 * A mayor número de '0' el individuo es más simple. 
	 * @param cadena es una tabla de char que codifica un individuo.
	 * @return el número de '0' del individuo.
	 */
	public int numeroDe0(char [] cadena){
		int numero_0=0;
		for(int i=0;i<cadena.length;i++)
			if (cadena[i]=='0') numero_0++;
		return numero_0;
	}
	
	/**
	 * Devuelve el valor Z, que es igual al número de '0' de la regla entre su longitud.
	 * El número de '0' indica la complejidad de  la regla, y un mayor número de '0' indica que una regla es más simple.
	 * Por tanto, a mayor valor de Z, mayor será la función de fitness, por lo que los individuos más simples tendrán mayor fitness. 
	 * @return un double con el valor de Z.
	 */
	public double evaluarZ(){
		return ( ((double)numeroDe0(this.cromosoma)/(double)(this.cromosoma.length)));
	}
	
	/**
	 * Devuelve la clase de un individuo.
	 * @param num_ejemplos_cubiertos es una tabla de enteros, con dimensión igual al número de clases, 
	 * y en cada posición está el número ejemplos por clase que cumplen la regla.
	 * @return un entero que indica la clase a la que pertenece el individuo.
	 */
	public int evaluarClase(int num_ejemplos_cubiertos[]){
		int clase=-1;
		double max=-1.0;
		double val=0.0;
		for(int i=0;i<parametrosGlobales.getNumeroClases();i++){
			val=num_ejemplos_cubiertos[i];
			if((val>max)||((val==max)&&(parametrosGlobales.getGeneradorAleatorio().rand()>0.5))){
				clase=i;
				max=val;
			}
		}
		return clase;
	}

	/**
	 * Devuelve el fitness de la regla.
	 * @return devuelve el valor de la función de fitness.
	 */
	public double evaluarFitness(){
		double z=0.0;
		double w=0.0;
		z=evaluarZ();
		w=numeroCasosNegativos(getNumeroEjemplosCubiertos());
		this.fitness=(1 + parametrosGlobales.get_A() * z)*Math.exp(-w);
		return this.fitness;
	}
	
	/**
	 * Evalúa el número de ejemplos que cubre la regla.
	 * @param ejemplos es el conjunto de ejemplos.
	 */
	public void evaluarEjemplosCubiertos(ConjuntoEntrenamiento ejemplos){	
		// Inicializo el número de ejemplos cubiertos
		for(int i=0;i<parametrosGlobales.getNumeroClases();i++)
			setNumeroEjemplosCubiertos(i,0);
		
		for(int i=0;i<ejemplos.getTamaño();i++){
			Ejemplo ej=ejemplos.getEjemplo(i);
			//Si el ejemplo cumple la parte de la izquierda, incrementamos en 1 la clase correspondiente de la regla.
			if(cubreEjemplo(ej)){
				int aux=getNumeroEjemplosCubiertos()[ej.getClase()]+1;
				setNumeroEjemplosCubiertos(ej.getClase(),aux);
			}
		}	
	}
	
	/**
	 * Calcula el valor PI de la Regla. (PI = Fitness * N+)
	 */
	public void evaluarPI(){
		this.PI=getFitness()*getNumeroCasosPositivos();
	}
	
	/**
	 * Evalúa una regla para un conjunto de datos de entrenamiento.
	 * @param ejemplos es el conjunto de ejmplos necesarios para evaluar la regla.
	 */
	public void evaluarSolucion(ConjuntoEntrenamiento ejemplos){
		// primero comprobamos los ejemplos que cumplen la clase y le 
		evaluarEjemplosCubiertos(ejemplos);
		this.clase=evaluarClase(getNumeroEjemplosCubiertos());
		evaluarFitness();
		evaluarPI();
	}

	
	/**
	 * Devuelve el número de casos negativos N- de la regla
	 * @param ejemplosCubiertos es una tabla de enteros con el número de ejemplos cubiertos por una regla para cada clase.
	 * @return un entero que indica el número de casos cubiertos negativamente por la regla.
	 */
	public int numeroCasosNegativos(int ejemplosCubiertos[]){
		int numNeg=0;
		for (int i=0;i<parametrosGlobales.getNumeroClases();i++){
			if(i!=this.clase) numNeg+=ejemplosCubiertos[i];
		}
		return numNeg;
		}
	
	/**
	 * Devuelve el número de casos positivos N+ de la regla
	 * @param ejemplosCubiertos es una tabla de enteros con el número de ejemplos cubiertos por una regla para cada clase.
	 * @return un entero que indica el número de casos cubiertos positivamente por la regla.
	 */
	public int numeroCasosPositivos(int ejemplosCubiertos[]){
		int numPos=0;
		numPos=ejemplosCubiertos[this.clase];
		return numPos;
		}
	
	/**
	 * Devuelve el número de ejemplos cubiertos positivamente por la regla de un conjunto de ejemplos. 
	 * @param ejemplos es el conjunto de ejemplos necesarios para evaluar los casos positivos de la regla.
	 * @return un entero que indica el número de casos cubiertos positivamente por la regla.
	 */
	public int numeroEjemplosCubiertosPositivamente(ConjuntoEntrenamiento ejemplos){
		int numEjCubiertos=0;
		for(int i=0;i<ejemplos.getTamaño();i++){
			Ejemplo ej=ejemplos.getEjemplo(i);
			if ((cubreEjemplo(ej)) && (getClase()==ej.getClase())) numEjCubiertos++;
		}
		return numEjCubiertos;
	}
	
	/**
	 * Devuelve el número de ejemplos cubiertos negativamente por la regla para un conjunto de ejemplos. 
	 * @param ejemplos es el conjunto de ejemplos necesarios para evaluar los casos negativos de la regla.
	 * @return un entero que indica el número de casos cubiertos negativamente por la regla.
	 */
	public int numeroEjemplosCubiertosNegativamente(ConjuntoEntrenamiento ejemplos){
		int numEjCubiertos=0;
		for(int i=0;i<ejemplos.getTamaño();i++){
			Ejemplo ej=ejemplos.getEjemplo(i);
			if ((cubreEjemplo(ej)) && (getClase()!=ej.getClase())) numEjCubiertos++;
		}
		return numEjCubiertos;
	}
	
}
