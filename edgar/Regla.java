package edgar;

import java.util.ArrayList;
import java.util.List;

import Dataset.Attribute;

/**
 * Contienen un cromosoma con una clase posible y posibles varios valores por atributo
 * el comienz oy fin de cada atributo está definido en la plantilla asi como los nombres de los mismos
 * Cambios recientes: 
 * 			-08/05/05: añadida propiedad enviada (boolean isenviada() y setEnviada(boolean) 
 * 						Cuando se envia una regla al supervisor se marca como tal. 
 * 						- modificada getCopia(), para tener en cuenta enviada (lo copia).  
 *  *
 */

/**
 * @author Daniel Albendín
 *	Cambio reciente: Fecha 10-02-2015. Se ha agregado la funcionalidad de aproximativo. (Permite que la plantilla
 *  sea perteneciente a cada regla o una global.
 */

public class Regla extends Cromosoma {
	
	private double fitness;
	private boolean enviada = false;
	private double PI;
	private boolean parcial=false; 
	private int inicio=0;
	private int NumAtributosParcial= 0;
	// En las siguiente varible, tabla de enteros,
	// almaceno en cada indice correspondiente a una clase, el número de ejemplos que cubren la regla y son de esa clase
	private int num_ejemplos_cubiertos[];
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// JOSÉ MANUEL GARRIDO MORGADO 26/03/2011
	// Variables para almacenar los ids de los ejemplos que cubre la regla, y los ids de los ejemplos que posee la regla
	private ArrayList<Integer> ids_ejemplos_cubiertos;
	private ArrayList<Integer> ids_ejemplos_poseidos;
	
	// Variable que almacena el fitness que utilizaremos en para la generación de la población con Token Competition
	double fitness_token=0;
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public Regla(Plantilla _plantilla){
		fitness=0;
		PI=0;NumAtributosParcial = this.numAtributos;
		int numeroBits=0;
//<-- Daniel Albendín - APROXIMATIVO
		if(Parametros.getInstancia_Parametros().aproximativo())
			plantilla = new Plantilla(_plantilla,_plantilla.get_ValoresAtributos());
		else
			plantilla = _plantilla;
//--> 
		for(int i=0;i<numAtributos;i++) numeroBits=numeroBits+(plantilla.numValoresAtributo(i));
		cromosoma=new char[numeroBits];
		for(int i=0;i<numeroBits;i++) cromosoma[i]='0';
		
		num_ejemplos_cubiertos=new int[plantilla.get_numero_Clases()];
		for(int i=0;i<plantilla.get_numero_Clases();i++) num_ejemplos_cubiertos[i]=0;
		
		ids_ejemplos_cubiertos = new ArrayList<Integer>();
		ids_ejemplos_poseidos = new ArrayList<Integer>();
		
	}
	
	public ArrayList<Integer> getIds_ejemplos_cubiertos()
	{
		return ids_ejemplos_cubiertos;
	}
	
	public ArrayList<Integer> getIds_ejemplos_poseidos()
	{
		return ids_ejemplos_poseidos;
	}
	
	public double calcula_fitness_token()
	{
		int ejemplos_poseidos = ids_ejemplos_poseidos.size();
		int ejemplos_cubiertos = ids_ejemplos_cubiertos.size();
		
		if(ejemplos_cubiertos == 0)
			fitness_token = 0;
		else
			fitness_token = fitness * (ejemplos_poseidos/ejemplos_cubiertos);
		
		return fitness_token;
	}
	
	public void add_ejemplo_cubierto(int id)
	{
		ids_ejemplos_cubiertos.add(id);
	}
	
	public void remove_ejemplo_cubierto(int id)
	{
		ids_ejemplos_cubiertos.remove((Object)id);
	}
	
	public void add_ejemplo_poseido(int id)
	{
		ids_ejemplos_poseidos.add(id);
	}
	
	public void remove_ejemplo_poseido(int id)
	{
		ids_ejemplos_poseidos.remove((Object)id);
	}
	
	public boolean posee_este_ejemplo(int id)
	{
		return ids_ejemplos_poseidos.contains(id);
	}
	
	public Regla(){
				this(Parametros.getInstancia_Parametros().getPlantilla());
	}
	
		
	public int get_NumCasos_Negativos(){
		int cont_neg=0;
		for(int i=0;i<num_ejemplos_cubiertos.length;i++){
			if(i!=clase) cont_neg+=num_ejemplos_cubiertos[i];
		}
		return cont_neg;
	}
	public int get_NumCasos_Positivos(){
		return num_ejemplos_cubiertos[clase];
	}
	public int getTotalCasosCubiertos(){
		int cont=0;
		for(int i=0;i<num_ejemplos_cubiertos.length;i++){
			 cont+=num_ejemplos_cubiertos[i];
		}
		return cont;
	}
	public int[] getNumEjemplosCubiertos(){
		return num_ejemplos_cubiertos;
	}
	
	public void set_num_ejemplos_cubiertos(int ind_clase,int val){
		num_ejemplos_cubiertos[ind_clase]=val;
	}
	public void setfitness(double _fitness){
		fitness=_fitness;
	}
	public double getfitness(){
		return fitness;
	}
	public void setPI(double _PI){
		PI=_PI;
	}	
	public double getPI(){
		return PI;
	}
	
	
	
	public boolean atributo_evaluado(int indAtributo){
		boolean evaluado=false;
		int i=0;
		int long_atr=plantilla.numValoresAtributo(indAtributo);
		int inicio_atr=plantilla.posicionAtributo(indAtributo);
		while ((!evaluado)&&(i<long_atr)){
			if(cromosoma[inicio_atr+i]=='1') evaluado=true;
			i++;
		}		
		return evaluado;
	}
	
	
	public boolean regla_evaluada(){
		boolean regla_evaluada=false;
		int i=0;
	
		while ((!regla_evaluada)&&(i<numAtributos)){
			if(atributo_evaluado(i)) regla_evaluada=true;
			i++;
		}		
		return regla_evaluada;
	}
	public void LimpiaRegla(){
		
		for (int i=0;i< numAtributos;i++){
		  if (atributoNoSelectivo(i)) 
		  		borraAtributo(i);//limpiar atributo
		}
		
	}
	
	public void borraAtributo(int indAtr){
		int ind_pos_atr=plantilla.posicionAtributo(indAtr);
		for (int i = 0;i<plantilla.numValoresAtributo(indAtr);i++){
					this.setValor(ind_pos_atr+i,'0');
		}
	}
	/**
	 * @param indAtr : indice del atributo a evaluar
	 * @return un booleano con True si el atributo esta todo a uno, si no es selectivo
	 */
	public boolean atributoNoSelectivo(int indAtr){
		boolean cumpleAtr=true;
		
		int i=0;
		int ind_pos_atr=plantilla.posicionAtributo(indAtr);
		while(i<plantilla.numValoresAtributo(indAtr)&&(cumpleAtr)){
			if((cromosoma[ind_pos_atr+i]=='0')) cumpleAtr=false;
			i++;
		}	
		return cumpleAtr;
	}
	
	public Double cumple_Atributo(int indAtr, EjemploFuzzy ej)
	{
		Double cumpleAtr=1.0;
		
	    if(ej.plantilla.get_TiposAtributos()[indAtr]==Attribute.NOMINAL)
	    {
	        char []atr_ej=ej.getAtributo(indAtr);
	        if (ej==null)
	            return 0.0;
	        int i=0;
	        int ind_pos_atr=plantilla.get_plantillaAtributos()[indAtr][1];
	        while((i<plantilla.get_plantillaAtributos()[indAtr][0])&&(cumpleAtr.doubleValue()==1.0)){
	            if((atr_ej[i]=='1')&&(cromosoma[ind_pos_atr+i]=='0')) 
	                cumpleAtr=0.0;
	            i++;
	        }    
	        return cumpleAtr;
	    }
	    else //fuzzy o discreto.
	    {
	        double mayorH0 = 0;
	        int posicion_regla = 0;

	        int lugar_atributo = ej.plantilla.posicionAtributo(indAtr);
	        int posiciones_que_ocupa_el_atributo = ej.plantilla.numValoresAtributo(indAtr);

	        // Almacenar· el n˙mero de intervalos que incluye la regla para ese atributo
	        int numero_de_alternativas = 0;
	        
	        for(int i=0;i<posiciones_que_ocupa_el_atributo;i++)
	        {
	            if(this.cromosoma[i+lugar_atributo]=='1')
	            {
	                numero_de_alternativas++;
	            }
	        }

	        // Si la regla no dice nada sobre ese atributo, es que da igual lo que valga, asÌ que consideramos que sÌ se cumple la condiciÛn.
	        if(numero_de_alternativas == 0 || numero_de_alternativas == ej.plantilla.numValoresAtributo(indAtr))
	            return 1.0;

	        for(int j=0;j<numero_de_alternativas;j++)
	        {
	            // Cada vez, este n˙mero tendr· que coincidir con la j, asÌ estaremos buscando cada vez una de las alternativas
	            int encontrados = 0;

	            boolean fin_busqueda = false;
	            for(int i=0; !fin_busqueda && i<posiciones_que_ocupa_el_atributo;i++)
	            {
	                if(this.cromosoma[i+lugar_atributo]=='1')
	                {
	                    if(encontrados == j)
	                    {
	                        posicion_regla = i;
	                        fin_busqueda = true;
	                    }
	                    else
	                        encontrados++;
	                }
	            }

	      
	            Double valor = (Double)(ej.getValores().get(indAtr));

	            ArrayList[] valores = ej.plantilla.get_ValoresAtributos();

	            
	            Double altura = 0.0;
	   	//<-- Albendín
	            altura = plantilla.getCobertura(posicion_regla,valor,indAtr,valores[indAtr].size());
//	            System.out.println("Altura de los ejemplos == "+altura);/////////////////////////////TRAPEZOIDAL/////////////////////////
	            //-->
	            /*          ArrayList[] valoresT = cambiarTriangulos(valores,indAtr);
	            Double min;
	            //Si es la primera etiqueta , sólo hay un lado del triangulo
	            if(posicion_regla==0)
	                min = (Double)valores[indAtr].get(posicion_regla);
	            else
	                min = (Double)valores[indAtr].get(posicion_regla-1);

	            //////////////////////min coge un punto de corte y centro el siguiente.
	            Double centro = (Double)valores[indAtr].get(posicion_regla);

	            Double max;

	            //Si es la ultima etiqueta , sólo hay un lado del triangulo
	            if(posicion_regla == ej.plantilla.numValoresAtributo(indAtr)-1)
	                max = (Double)valores[indAtr].get(posicion_regla);
	            else
	                max = (Double)valores[indAtr].get(posicion_regla+1);
	            // si cumple esa etiqueta comprueba donde la corta
	            if(valor.doubleValue() >= min.doubleValue() && valor.doubleValue() <= max.doubleValue())
	            {
	                double altura = 0;
	                if(valor.doubleValue() < centro.doubleValue())
	                    altura = (valor - min.doubleValue()) / (centro.doubleValue() - min.doubleValue());

	                else if(valor.doubleValue() == centro.doubleValue())
	                    altura = 1;

	                else
	                    altura = (max.doubleValue() - valor) / (max.doubleValue() - centro.doubleValue());


	                if(altura>mayorH0)
	                    mayorH0 = altura;
	            }*/
                if(altura>mayorH0)
                    mayorH0 = altura;
	        }

	        return mayorH0;
	    }
	}
	
/**
 *  Método a cambiar
 * @author Daniel Albendín
 * @param valores 
 * @param indAtr
 * @return
 */
	private ArrayList[] cambiarTriangulos(ArrayList[] valores, int indAtr) {
		ArrayList[] devolver = new ArrayList[valores.length];
		devolver[indAtr] = new ArrayList();
		devolver[indAtr].add(valores[indAtr].get(0));
		for (int i = 2; i<valores[indAtr].size()-1; i++){
			Double a =(Double)valores[indAtr].get(i);
			Double b = (Double)valores[indAtr].get(i-1);
			Double result = b + ((a-b)/2);
			devolver[indAtr].add(result);
		}
		devolver[indAtr].add(valores[indAtr].get(valores[indAtr].size()-1));
		return devolver;
		
	}

	/**
	 * 
	 * Antes se calculaban los puntos del triángulo en este método, lo hemos cambiado para que use el método getCobertura de la plantilla que se le pasa
	 * por parámetros.
	 * 
	 * @param indAtr
	 * @param ej
	 * @param plan
	 * @return
	 */
	public Double cumple_Atributo(int indAtr, EjemploFuzzy ej, Plantilla plan)
	{
		Double cumpleAtr=1.0;
		
		if(plan.get_TiposAtributos()[indAtr]==Attribute.NOMINAL)
		{
			char []atr_ej=ej.getAtributo(indAtr);
			if (ej==null)
				return 0.0;
			int i=0;
			int ind_pos_atr=plantilla.get_plantillaAtributos()[indAtr][1];
			while((i<plantilla.get_plantillaAtributos()[indAtr][0])&&(cumpleAtr.doubleValue()==1.0)){
				if((atr_ej[i]=='1')&&(cromosoma[ind_pos_atr+i]=='0')) 
					cumpleAtr=0.0;
				i++;
			}	
			return cumpleAtr;
		}
		else //fuzzy o discreto.
		{
			double mayorH0 = 0;
			int posicion_regla = 0;
			
			
			
			int lugar_atributo = plan.posicionAtributo(indAtr);
			int posiciones_que_ocupa_el_atributo = plan.numValoresAtributo(indAtr);
			
			// Almacenará el número de intervalos que incluye la regla para ese atributo
			int numero_de_alternativas = 0;
			
			
			for(int i=0;i<posiciones_que_ocupa_el_atributo;i++)
			{
				if(this.cromosoma[i+lugar_atributo]=='1')
				{
					numero_de_alternativas++;
				}
			}
			
			// Si la regla no dice nada sobre ese atributo, es que da igual lo que valga, así que consideramos que sí se cumple la condición.
			if(numero_de_alternativas == 0 || numero_de_alternativas == plan.numValoresAtributo(indAtr))
				return 1.0;
			
			for(int j=0;j<numero_de_alternativas;j++)
			{
				// Cada vez, este número tendrá que coincidir con la j, así estaremos buscando cada vez una de las alternativas
				int encontrados = 0;
				
				boolean fin_busqueda = false;
				for(int i=0; !fin_busqueda && i<posiciones_que_ocupa_el_atributo;i++)
				{
					if(this.cromosoma[i+lugar_atributo]=='1')
					{
						if(encontrados == j)
						{
							posicion_regla = i;
							fin_busqueda = true;
						}
						else
							encontrados++;
					}
				}
				
				Double valor = (Double)(ej.getValores().get(indAtr));
				
				ArrayList[] valores = plan.get_ValoresAtributos();
				
	            Double altura = 0.0;
	   	//<-- Albendín Cobertura en plantilla2
	            altura = plan.getCobertura(posicion_regla,valor,indAtr,valores[indAtr].size());
			//-->		
					
					if(altura>mayorH0)
						mayorH0 = altura;
				}
			
			
			return mayorH0;
		}
	}
	
	
	
	// Esta función devuelve un booleano indicando si la regla cubre al ejemplo, pero la parte de entrada
	
	/**
	 * EL siguiente método retorna verdadero, en el caso de que la regla cubra al ejemplo que le pasamos como parámetro
	 * @param Ejemplo a comprobar si cumple la regla.
	 */

	
	public double Cubre_Ejemplo(EjemploFuzzy ej){
		
		if(ej==null)
			return 0.0;
		
		int[] tipos = ej.getPlantilla().get_TiposAtributos();
		
		int posRealAtributoNumerico = 0;
		int i=0;
		double h0 = Double.MAX_VALUE;
		double menor_h0 = Double.MAX_VALUE;
				
		int numAtributosEvaluados = 0;
		// devolver el minimo de los h0. 
		while(i<numAtributos)
		{
			if(tipos[i] == Attribute.NOMINAL)
			{
					if(atributo_evaluado(i)){
						h0 = Math.abs(cumple_Atributo(i,ej));
						numAtributosEvaluados++;
					}

			}else
			{
				if(atributo_evaluado(i)){
					h0 = Math.abs(cumple_Atributo(posRealAtributoNumerico, ej));
					numAtributosEvaluados++;
				}
					posRealAtributoNumerico++;

			}
				
			if (h0<menor_h0) 
			menor_h0 = h0;  
		
			i++;
		}
		
		if(numAtributosEvaluados == 0)
			return 0.0;
		return menor_h0;
	}	
/*
 * Anteriormente no hacía nada con la plantilla, ahora hemos agregado la plantilla a cumple_atributo
 * @param ej
 * @param plan
 * @return
 */
public double Cubre_Ejemplo(EjemploFuzzy ej, Plantilla plan){
 		
		if(ej==null)
			return 0.0;
		
		int[] tipos = ej.getPlantilla().get_TiposAtributos();
		
		int posRealAtributoNumerico = 0;
		int i=0;
		double h0 = Double.MAX_VALUE;
		double menor_h0 = Double.MAX_VALUE;
				
		int numAtributosEvaluados = 0;
		// devolver el minimo de los h0. 
		while(i<numAtributos)
		{
			if(tipos[i] == Attribute.NOMINAL)
			{
					if(atributo_evaluado(i)){
						h0 = Math.abs(cumple_Atributo(i,ej,plan));
						numAtributosEvaluados++;
					}

			}else
			{
				if(atributo_evaluado(i)){
					h0 = Math.abs(cumple_Atributo(posRealAtributoNumerico, ej,plan));
					numAtributosEvaluados++;
				}
					posRealAtributoNumerico++;

			}
				
			if (h0<menor_h0) 
			menor_h0 = h0;  
		
			i++;
		}
		
		if(numAtributosEvaluados == 0)
			return 0.0;
		return menor_h0;
	}

	/**
	 * Elimina de un conjunto de entrenamiento todos aquellos datos que esta regla soporta con casos positivos
	 * @param datosEntrenamiento Conjunto de datos de entrenamientos de los que queremos eliminar el soporte de esta regla
	 */
	
	
	public void EliminaEntrenamiento(Dataset datosEntrenamiento){
		int num_ejemplos = datosEntrenamiento.getTamanho_conjunto_entrenamiento();
		for (int i=num_ejemplos-1;i>=0;i-- ){
			if (Cubre_Ejemplo_Pos(datosEntrenamiento.get_EjemploFuzzy(i)) )
				datosEntrenamiento.Eliminar_Ejemplo(i);
		}
	}
	
	public boolean Cubre_Ejemplo_Pos(EjemploFuzzy ejemploFuzzy){
		if(ejemploFuzzy.getClase()==clase)
		{
			double valor = Cubre_Ejemplo(ejemploFuzzy);
			if(Parametros.getInstancia_Parametros().isFuzzy()) 
			{
				if(valor > 0.01)
					return true;
				else
					return false;
			
			}
			// Si llega aquí es que es discreto
			// En fuzzy es cierto, pero en discreto en Discreto tiene que ser 0.5 (ahora el parámetro es 0.3)
			else if (valor > Parametros.getInstancia_Parametros().getCoberturaFuzzy()) 
				return true;
		
		}
		// si es fuzzy, todas las mayores que 0.01
		return false;
	}
	
	
	
	
	public char[] getAtributo(int indAtributo){
		char Atributo[]=new char[plantilla.get_plantillaAtributos()[indAtributo][0]];
		int inicio_atr=plantilla.get_plantillaAtributos()[indAtributo][1];
		for (int i=0;i<Atributo.length;i++)
			Atributo[i]=cromosoma[inicio_atr+i];
		return Atributo;
	}
	

	
	public String get_texto_Regla(){
		int []tiposAtributos = plantilla.get_TiposAtributos();
		String cadena_Regla="";
		int numero_conjunciones=0;
		for(int i=0;i<numAtributos;i++){
			if(atributo_evaluado(i)){
					if (numero_conjunciones>0) cadena_Regla=cadena_Regla+" AND ";
					numero_conjunciones++;
					
					String nomAtr=plantilla.get_NombresAtributos()[i];
					cadena_Regla=cadena_Regla+" ( "+nomAtr+"= ";
					
					int inicio_atributo=plantilla.get_plantillaAtributos()[i][1];
					cadena_Regla+= " (";
							
					int numero_disyunciones=0;
					String disy=" || ";

					for(int aux=0;aux<plantilla.get_plantillaAtributos()[i][0];aux++){
						int aux_ind=aux+inicio_atributo;
						if(cromosoma[aux_ind]=='1') {
							if (numero_disyunciones>0) cadena_Regla+=disy;
							numero_disyunciones++;
							String valAtributo;
							//if(tiposAtributos[i] != Attribute.NOMINAL)
								valAtributo=plantilla.get_ValoresAtributos()[i].get(aux)+"";
							//else
								//valAtributo="Aquí falta algo";
							cadena_Regla=cadena_Regla+valAtributo;
						}
					}
					cadena_Regla=cadena_Regla+" ) ) ";
				}
			}					
					cadena_Regla=cadena_Regla+" --> ";
					cadena_Regla=cadena_Regla+" ( "+plantilla.get_Nombre_Clase()+" = "+ plantilla.get_Valores_Clase().get(clase) +")";
					

		return cadena_Regla;
	}
	
	public String toString(){
		return get_texto_Regla(); 
	}
	
	
	public Regla getCopia(){
//<-- Daniel Albendín - Aproximativo
		// Se añade la plantilla a la llamada al constructor.
		Regla copia=new Regla(plantilla);
//-->
		copia.setValorCromosoma(cromosoma);
		copia.setClase(clase);
		for(int i=0;i<num_ejemplos_cubiertos.length;i++)
			copia.set_num_ejemplos_cubiertos(i,num_ejemplos_cubiertos[i]);
		copia.setfitness(fitness);
		copia.setPI(PI);
		copia.setEnviada(enviada);
		return copia;
	}
	/**
	 * EvalDGA
	 * 
	 * 
	 * 
	 */
	
	
	public int numero_de_0(char [] cadena){
		int numero_0=0;
		for(int i=0;i<cadena.length;i++)
			if (cadena[i]=='0') numero_0++;
		return numero_0;
	}
	
	public double evaluar_z(){
		return ( ((double)numero_de_0(getValorCromosoma())/(double)(getValorCromosoma().length)));
	}
	
	public int evaluar_Clase(int num_ejemplos_cubiertos[]){
		int clase=-1;
		double max=-1.0;
		double val=0.0;
		for(int i=0;i<plantilla.get_numero_Clases();i++){
			val=num_ejemplos_cubiertos[i];
			if((val>max)||((val==max)&&(Parametros.getInstancia_Parametros().get_GeneradorAleatorio().Rand()>Parametros.getInstancia_Parametros().getCoberturaFuzzy()))){
				clase=i;
				max=val;
			}
		}
		return clase;
	}

	
	/**
	 * Función que evalúa el fitness de una regla.
	 * z= longitud de la regla, numero de ceros/longitud; Si todos cero = 1; potencia reglas cortas;
	 * w= casos negativos de la regla. si no hay casos negaticos 
	 * 
	 * @param regla Regla a la cual se le va a calcular el valor de su función de fitness. 
	 * @return Devuelve el valor de la función de fitness.
	 */
		

	
	
	/* Propuestas de otros fitness posibles
	 * 
	 * 	
	 public double evaluar_Fitness4(){
		double val_fitness=0.0;
		double z=0.0;
		double w=0.0;
		z=evaluar_z(); 
		w=numero_Casos_Negativos(get_num_ejemplos_cubiertos(), getClase());
		val_fitness=(1 + 0.1* z)*Math.exp(-w);
		setfitness(val_fitness);
		return val_fitness;
	}
	 public double evaluar_Fitness2(){
		double val_fitness=0.0;
		double z=0.0;
		double pos=0.0;
		double w=0.0;
		int tot=0;
		z=evaluar_z();
		w=numero_Casos_Negativos(get_num_ejemplos_cubiertos(), this.getClase());
		tot = this.get_total_Casos_Cubiertos();
		pos=numero_Casos_Positivos(this.get_num_ejemplos_cubiertos(), this.getClase());
		 
		val_fitness=-w/(z+0.1)		;
		setfitness(val_fitness);
		return val_fitness;
	}
	public double evaluar_FitnessPositivos(){
		double val_fitness=0.0;
		double z=0.0;
		double w=0.0;
		double p=0.0;
		z=evaluar_z(); 
		w=numero_Casos_Negativos(get_num_ejemplos_cubiertos(), getClase());
		p=this.numero_Casos_Positivos(get_num_ejemplos_cubiertos(), getClase());
		val_fitness=(p*z)/(100*w+1);
		setfitness(val_fitness);
		return val_fitness;
	}
	public double evaluar_FitnessLongitud(){
		double val_fitness=0.0;
		double z=0.0;
		double w=0.0;
		double p=0.0;
		z=evaluar_z(); 
		w=numero_Casos_Negativos(get_num_ejemplos_cubiertos(), getClase());
		p=this.numero_Casos_Positivos(get_num_ejemplos_cubiertos(), getClase());
		val_fitness=p*z/(10*w+1);
		setfitness(val_fitness);
		return val_fitness;
	}
	
	public double evaluar_FitnessNegativo(){
		double val_fitness=0.0;
		double z=0.0;
		double w=0.0;
		z=evaluar_z(); 
		w=numero_Casos_Negativos(get_num_ejemplos_cubiertos(), getClase());
		val_fitness=(1 + 0.1 * z)*Math.exp(-100*w);
		setfitness(val_fitness);
		return val_fitness;
	}
	
	*/
	
	/**
	 * Elimina los atributos que no evaluan a ninguna instancia
	 */
	public void refinaRegla(){
		
	}
	
	
	public void evaluar_ejemplos_cubiertos(Dataset datosEntrenamiento){	
		// Inicializo el número de ejemplos cubiertos
		for(int i=0;i<plantilla.get_numero_Clases();i++)
			set_num_ejemplos_cubiertos(i,0);
		
		for(int i=0;i<datosEntrenamiento.getTamanho_conjunto_entrenamiento();i++){
			EjemploFuzzy ej=datosEntrenamiento.get_EjemploFuzzy(i);
			//Si el ejemplo cumple la parte de la izquierda, incrementamos en 1 la clase correspondiente de la regla.
			if(Cubre_Ejemplo(ej)>Parametros.getInstancia_Parametros().getCoberturaFuzzy()){
				int aux=getNumEjemplosCubiertos()[ej.getClase()]+1;
				set_num_ejemplos_cubiertos(ej.getClase(),aux);
			}
		}	
	}
	
	
	/**
	 * Dependiendo del balanceo de clase y de la catalogación como clase objetivo, se pondera el fitness para seleccionar 
	 * la regla en el clasificador final 
	 */
	public void evaluar_PI(){
		double factor=1;
		 
		if (Parametros.getInstancia_Parametros().isBalanceoClases())	factor =1/plantilla.getPorcentajeClase(clase);
		if (this.clase == Parametros.getInstancia_Parametros().getClaseObjetivo()) factor *=2;
		double val_PI=getfitness()*get_NumCasos_Positivos()*factor;
		setPI(val_PI);
	}
	
	public void evaluar_solucion(Dataset datosEntrenamiento){
		// primero comprobamos los ejemplos que cumplen la clase y le 
		evaluar_ejemplos_cubiertos(datosEntrenamiento);
		int clase=evaluar_Clase(getNumEjemplosCubiertos());
		setClase(clase);		
		evaluar_Fitness();
		evaluar_PI();
	}
	
	public void evaluarPorLongitud(Dataset datosEntrenamiento){
		// primero comprobamos los ejemplos que cumplen la clase y le 
		evaluar_ejemplos_cubiertos(datosEntrenamiento);
		int clase=evaluar_Clase(getNumEjemplosCubiertos());
		setClase(clase);		
		evaluar_FitnessLongitud();
		evaluar_Fitness();
		evaluar_PI();
	}
	
	// Esta función me devuelve el número de casos negativos N- de la regla
	int numero_Casos_Negativos(int ejemplosCubiertos[], int clase){
		int numNeg=0;
		for (int i=0;i<plantilla.get_numero_Clases();i++){
			if(i!=clase) numNeg+=ejemplosCubiertos[i];
		}
		return numNeg;
		}
	
//	 Esta función me devuelve el número de casos positivos N+ de la regla
	int numero_Casos_Positivos(int ejemplosCubiertos[], int clase){
		int numPos=0;
		numPos=ejemplosCubiertos[clase];
		return numPos;
		}

	public int numeroEjemplosCubierto(Dataset ejemplos){
		int numEjCubiertos=0;
		for(int i=0;i<ejemplos.getTamanho_conjunto_entrenamiento();i++){
			EjemploFuzzy ej=ejemplos.get_EjemploFuzzy(i);
			if ((Cubre_Ejemplo(ej)>Parametros.getInstancia_Parametros().getCoberturaFuzzy()) && (getClase()==ej.getClase())) numEjCubiertos++; // chapuza
		}
		return numEjCubiertos;
	}
	
	
	public int numeroEjemplosNoCumpleClase(Dataset ejemplos){
		int numEjCubiertos=0;
		for(int i=0;i<ejemplos.getTamanho_conjunto_entrenamiento();i++){
			EjemploFuzzy ej=ejemplos.get_EjemploFuzzy(i);
			if ((Cubre_Ejemplo(ej) > Parametros.getInstancia_Parametros().getCoberturaFuzzy()) && (getClase()!=ej.getClase())) numEjCubiertos++; // chapuza
		}
		return numEjCubiertos;
	}

	public int getFin() {
		int posInicio=	plantilla.posicionAtributo(inicio);
		int fin= posInicio;
		for (int i= inicio; i< this.NumAtributosParcial;i++)
			fin += plantilla.numValoresAtributo(i);
		return fin;
	}
	
	public int getInicio() {
		return inicio;
	}

	public void setInicio(int inicio) {
		this.inicio = inicio;
	}

	public int getNumAtributosParcial() {
		return NumAtributosParcial;
	}

	public void setNumAtributosParcial(int numAtributosParcial) {
		NumAtributosParcial = numAtributosParcial;
	}

	public double evaluar_FitnessLongitud(){
		double val_fitness=0.0;
		double z=0.0;
		double w=0.0;
		z=evaluar_z(); 
		w=numero_Casos_Negativos(getNumEjemplosCubiertos(), getClase());
		val_fitness=(1 +  z)*Math.exp(-w);
		setfitness(val_fitness);
		return val_fitness;
	} // ojo prueba marr esta es la funcion de fitness original
	//clase objetivo 
	public double evaluar_Fitness(){
		double val_fitness=0.0;
		double z=0.0;
		double w=0.0;
		z=evaluar_z(); 
		w=numero_Casos_Negativos(getNumEjemplosCubiertos(), getClase());//vtoken this.getIds_ejemplos_poseidos()
		val_fitness=(1 + 0.1* z)*Math.exp(-w);
		setfitness(val_fitness);
		return val_fitness;
	}

	public boolean isEnviada() {
		return enviada;
	}

	public void setEnviada(boolean enviada) {
		this.enviada = enviada;
	}

	public double get_fitness_token() {

		return this.fitness_token;
	}
	
	
}
