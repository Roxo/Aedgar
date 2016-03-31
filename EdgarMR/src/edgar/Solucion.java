package edgar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Collections;
import java.util.TreeMap;

public class Solucion {
	private ArrayList set_reglas=new ArrayList();
	
	public Solucion(){
		
	}
	
	
		
	public Regla get_regla(int ind_regla){
		return (Regla)set_reglas.get(ind_regla);
	}
		
	/**
	 * El siguiente mntodo introduce una regla en el conjunto de reglas de forma ordenada
	 * en orden descendente del valor PI
	 * @param nueva_regla es la nueva regla a introducir.
	 */
	public void Insertar_regla_Orden_PI(Regla nueva_regla){
		try{
			if (set_reglas == null) set_reglas=new ArrayList();
			int pos=0;
			boolean seguir=true;
			while((seguir)&&(pos<set_reglas.size())){
					if(((Regla)set_reglas.get(pos)).getPI()<nueva_regla.getPI()) seguir=false;
					else pos++;
			}		
			set_reglas.add(pos,nueva_regla);
			
		}catch(Exception e){
			System.out.println("Error anadiendo una nueva regla en orden PI");
		}
	}
	
	
	public void insertarRegla(Regla nueva_regla){
		try{
			if (set_reglas == null) set_reglas=new ArrayList();
			set_reglas.add(set_reglas.size(),nueva_regla);
		}catch(Exception e){
			System.out.println("Error anadiendo una nueva regla.");
		}
	}
	
	
	
	public void Insertar_regla_Posicion(Regla nueva_regla, int pos){
		try{
			if (set_reglas == null) set_reglas=new ArrayList();
			if(pos>set_reglas.size()) pos=set_reglas.size();
			
			set_reglas.add(pos,nueva_regla);
			
		}catch(Exception e){
			System.out.println("Error anadiendo una nueva regla en la posicion: "+pos);
		}
	}
	
	
	public Solucion copia(){
		Solucion clon= new Solucion();
		int num_reglas = this.getTamano_solucion();
	for (int i =0;i<num_reglas;i++)
		clon.insertarRegla(this.get_regla(i).getCopia());
	
	return clon;
	}
	
	/**
	 * El siguiente mntodo calcula la regla de mejor PI
	 * @return regla mejor PI=fitness*N+
	 */
	public int get_Indice_Mejor_Regla(){
		double mejor_Val=0.0;
		int indice_mejor_solucion=0;
		for(int i=0;i<set_reglas.size();i++){
			Regla individuo=((Regla)set_reglas.get(i));
			if(individuo.getPI()>mejor_Val){
				mejor_Val=individuo.getPI();
				indice_mejor_solucion=i;
			}
		}
		return indice_mejor_solucion;
	}
	
	
	
	public boolean Existe_regla(Regla reg){
		boolean existe=false;
		int i=0;
		while((existe==false)&&(i<set_reglas.size())){
			existe=reg.Igual((Regla)set_reglas.get(i));
			i++;
		}
		return existe;
	}
	
	public int getTamano_solucion(){
		return set_reglas.size();
	}
	
	public Dataset get_Ejemplos_No_Cubiertos(
			Dataset conjunto_ejemplos) {

		int cont_reglas = 0;
		ArrayList indice_ejemplos_No_Cubiertos = new ArrayList();
		for (int i = 0; i < conjunto_ejemplos
				.getTamano_conjunto_entrenamiento(); i++) {
			indice_ejemplos_No_Cubiertos.add(new Integer(i));
		}

		while ((indice_ejemplos_No_Cubiertos.size() > 0)
				&& (cont_reglas < getTamano_solucion())) {
			Regla regla = get_regla(cont_reglas);
			int i = 0;

			while (i < indice_ejemplos_No_Cubiertos.size()) {
				int ind_Ejemplo = Integer.parseInt(indice_ejemplos_No_Cubiertos
						.get(i)
						+ "");
				EjemploFuzzy ej = conjunto_ejemplos.get_EjemploFuzzy(ind_Ejemplo);
				if (regla.Cubre_Ejemplo_Pos(ej)) {
					indice_ejemplos_No_Cubiertos.remove(i);
				} else
					i++;
			}
			cont_reglas++;
		}

		Dataset ejemplos_noCubiertos = new Dataset();
		int i = 0;
		while (i < indice_ejemplos_No_Cubiertos.size()) {
			int ind_Ejemplo = Integer.parseInt(indice_ejemplos_No_Cubiertos
					.get(i)
					+ "");
			EjemploFuzzy ej = conjunto_ejemplos.get_EjemploFuzzy(ind_Ejemplo);
			ejemplos_noCubiertos.Insertar_Ejemplo(ej);
			i++;
		}
		if (Parametros.getInstancia_Parametros().get_Nivel_Depuracion() > 0) {
			System.out.println("Numero de ejemplos no cubiertos: "
					+ indice_ejemplos_No_Cubiertos.size());
		}
		return ejemplos_noCubiertos;
	}
/**
 * Devuelve una lista con los ejemplos cubiertos por n o menos reglas
 * @param datosEntrenamiento
 * @param n
 * @return
 */
	public Dataset get_Ejemplos_Poco_Cubiertos(
			Dataset datosEntrenamiento,int n,boolean eliminar) {
		
		int num_ejemplos =datosEntrenamiento.getTamano_conjunto_entrenamiento();
        int cont_ejemplos[][] = new int[num_ejemplos][2];
		int cont_reglas = 0;
		
		for (int i = 0; i < num_ejemplos; i++) {
				cont_ejemplos[i][0]= i;
				cont_ejemplos[i][1]= 0;
		}
		int num_reglas = getTamano_solucion();
		for( ;cont_reglas < num_reglas;cont_reglas++) {
			Regla regla = get_regla(cont_reglas);
		  	for (int i = 0;i < num_ejemplos;i++) {
				EjemploFuzzy ej = datosEntrenamiento.get_EjemploFuzzy(i);
				if (regla.Cubre_Ejemplo_Pos(ej)) cont_ejemplos[i][1]++; 
			}			
		}

		Dataset ejemplos= new Dataset();
		for (int i=0;i < num_ejemplos;i++) {
			if (n>cont_ejemplos[i][1]){
				EjemploFuzzy ej = datosEntrenamiento.get_EjemploFuzzy(i);
				ejemplos.Insertar_Ejemplo(ej);
				}
		}
		if (eliminar==true){
			for (int i=num_ejemplos-1;i >=0  ;i--) {
			if (n>cont_ejemplos[i][1]){
				datosEntrenamiento.Eliminar_Ejemplo(i);
				}
			}
		}
		
		return ejemplos;
	}

	

	
	
	
	public int[][] Clasificar(Dataset ejemplos)
	{
		int [][] resultado_clasificacion=new int[ejemplos.getTamano_conjunto_entrenamiento()][2];
		
		int num_reglas = set_reglas.size();
		
		for (int i=0;i<ejemplos.getTamano_conjunto_entrenamiento();i++)
		{		
			EjemploFuzzy ej=ejemplos.get_EjemploFuzzy(i);	
			int clase=0;
			int cont_reglas=0;
			double tabla_h0[] = new double[num_reglas];
			int clases[] = new int[num_reglas];
			double mayor_h0 = -1;
			
			Regla regla_elegida = null;
			boolean encontrado = false;
			double acumulado_anterior = 0;
			while(cont_reglas<num_reglas && (!encontrado))
			{
				Regla regla_actual=(Regla)set_reglas.get(cont_reglas);				
				
				double valor_actual = regla_actual.Cubre_Ejemplo(ej);
				
				if (Parametros.getInstancia_Parametros().isFuzzy())
				{
					 {
						if(cont_reglas > 0)
							acumulado_anterior += tabla_h0[cont_reglas-1];
						/*
						double acumulado_anterior = 0;
						for(int y=0;y<cont_reglas;y++)
						{
							acumulado_anterior += tabla_h0[y]; // H00 real * el porcentaje que le queda. 
						}
						*/
						
						tabla_h0[cont_reglas] = valor_actual * (1 - acumulado_anterior);
						clases[cont_reglas] = regla_actual.getClase();
					}
					
					// Si es mayor que 0.5, no hace falta seguir mirando, la regla es esa seguro (el 0.5 ya es ponderado)
					if(tabla_h0[cont_reglas] > 0.5)
					{
						encontrado = true;
						regla_elegida = regla_actual;
					}
					
					
					if(tabla_h0[cont_reglas] > mayor_h0)// mntodo gana la mejor.
					{
						mayor_h0 = tabla_h0[cont_reglas];
						regla_elegida = regla_actual;
					}
				}
				else //modo discreto
				{
					if(valor_actual > 0.5)
					{
						encontrado = true;
						regla_elegida = regla_actual;
					}
					if(valor_actual > mayor_h0)// SI NO GANA NINGUNA REGLA COGE LA QUE MAS CUBRA. 
					{
						mayor_h0 = tabla_h0[cont_reglas];
						regla_elegida = regla_actual;
					}
				}
				cont_reglas++;
			}//FIN WHILE	
			//ojo varios metodos Fuzzy:
				// 1n   evaluar todos los h0 y coger la regla con el mayor h0
				// 2n  SUmar agregando igual que en torcs y se desfuzzyfica al final y la etiqueta que de es la clase
				// 3n cn2--> definir un h0 min. h0 < 0.2 , es como 2 , por ejemplo si aplican tres reglas: h01 .7, h02 .3 , h03 .2 -->
				// clase final --> 0,7 * clase1n + 0.3*(0.2*clase 2n + ... , pdte enviar formula.
			
			
			Boolean mayorH0 = false; //OJO para luego poner en un parametro de lanzamiento 
			if (mayorH0 || !Parametros.getInstancia_Parametros().isFuzzy()) 
			{
			////dos metodos--> 
			//a)quedarnos con el mayor valor relativo (ya implementado en el while anterior) y esta siguiente lnnea
			    clase=regla_elegida.getClase();
			}
			else//b) sumar en un array las n clases los valores acumulados -> Es el siguiente bloque de cndigo entre corchetes
			{
				int[] clases_distintas = clases_distintas(clases);
				double[] suma_h0_clases = suma_h0_de_cada_clase(tabla_h0, clases, clases_distintas);
				int indice_clase_ganadora = dame_clase_ganadora(suma_h0_clases);
				clase = clases_distintas[indice_clase_ganadora];
			}
		
			resultado_clasificacion[i][0]=ejemplos.get_EjemploFuzzy(i).getClase();
			resultado_clasificacion[i][1]=clase;
		}
		
		return resultado_clasificacion;
	}
	
	public int[][] Clasificar(Dataset ejemplos, Plantilla plan)
	{
		int [][] resultado_clasificacion=new int[ejemplos.getTamano_conjunto_entrenamiento()][2];
		
		int num_reglas = set_reglas.size();
		
		for (int i=0;i<ejemplos.getTamano_conjunto_entrenamiento();i++)
		{		
			EjemploFuzzy ej=ejemplos.get_EjemploFuzzy(i);	
			int clase=0;
			int cont_reglas=0;
			double tabla_h0[] = new double[num_reglas];
			int clases[] = new int[num_reglas];
			double mayor_h0 = -1;
			
			Regla regla_elegida = null;
			boolean encontrado = false;
			double acumulado_anterior = 0;
			while(cont_reglas<num_reglas && (!encontrado))
			{
				Regla regla_actual=(Regla)set_reglas.get(cont_reglas);				
				
				double valor_actual = regla_actual.Cubre_Ejemplo(ej, plan);
				
				if (Parametros.getInstancia_Parametros().isFuzzy())
				{
					 {
						if(cont_reglas > 0)
							acumulado_anterior += tabla_h0[cont_reglas-1];
						/*
						double acumulado_anterior = 0;
						for(int y=0;y<cont_reglas;y++)
						{
							acumulado_anterior += tabla_h0[y]; // H00 real * el porcentaje que le queda. 
						}
						*/
						
						tabla_h0[cont_reglas] = valor_actual * (1 - acumulado_anterior);
						clases[cont_reglas] = regla_actual.getClase();
					}
					
					// Si es mayor que 0.5, no hace falta seguir mirando, la regla es esa seguro (el 0.5 ya es ponderado)
					if(tabla_h0[cont_reglas] > 0.5)
					{
						encontrado = true;
						regla_elegida = regla_actual;
					}
					
					
					if(tabla_h0[cont_reglas] > mayor_h0)// mntodo gana la mejor.
					{
						mayor_h0 = tabla_h0[cont_reglas];
						regla_elegida = regla_actual;
					}
				}
				else //modo discreto
				{
					if(valor_actual > 0.5)
					{
						encontrado = true;
						regla_elegida = regla_actual;
					}
					if(valor_actual > mayor_h0)// SI NO GANA NINGUNA REGLA COGE LA QUE MAS CUBRA. 
					{
						mayor_h0 = tabla_h0[cont_reglas];
						regla_elegida = regla_actual;
					}
				}
				cont_reglas++;
			}//FIN WHILE	
			//ojo varios metodos Fuzzy:
				// 1n   evaluar todos los h0 y coger la regla con el mayor h0
				// 2n  SUmar agregando igual que en torcs y se desfuzzyfica al final y la etiqueta que de es la clase
				// 3n cn2--> definir un h0 min. h0 < 0.2 , es como 2 , por ejemplo si aplican tres reglas: h01 .7, h02 .3 , h03 .2 -->
				// clase final --> 0,7 * clase1n + 0.3*(0.2*clase 2n + ... , pdte enviar formula.
			
			
			Boolean mayorH0 = false; //OJO para luego poner en un parametro de lanzamiento 
			if (mayorH0 || !Parametros.getInstancia_Parametros().isFuzzy()) 
			{
			////dos metodos--> 
			//a)quedarnos con el mayor valor relativo (ya implementado en el while anterior) y esta siguiente lnnea
			    clase=regla_elegida.getClase();
			}
			else//b) sumar en un array las n clases los valores acumulados -> Es el siguiente bloque de cndigo entre corchetes
			{
				int[] clases_distintas = clases_distintas(clases);
				double[] suma_h0_clases = suma_h0_de_cada_clase(tabla_h0, clases, clases_distintas);
				int indice_clase_ganadora = dame_clase_ganadora(suma_h0_clases);
				clase = clases_distintas[indice_clase_ganadora];
			}
		
			resultado_clasificacion[i][0]=ejemplos.get_EjemploFuzzy(i).getClase();
			resultado_clasificacion[i][1]=clase;
		}
		
		return resultado_clasificacion;
	}
	
	
	
	private int dame_clase_ganadora(double[] suma_h0_clases) {

		double mayor = -1;
		
		int indice_ganador = 0;
		
		int tam = suma_h0_clases.length;
		
		for(int i=0;i<tam;i++)
		{
			if(mayor<suma_h0_clases[i])
			{
				mayor = suma_h0_clases[i];
				indice_ganador = i;
			}
		}
		return indice_ganador;
	}

	private double[] suma_h0_de_cada_clase(double[] tabla_h0, int[] clases, int[] clases_distintas) {

		int tam_clases_distintas = clases_distintas.length;
		
		double sumas[] = new double[tam_clases_distintas];
		
		for(int i=0;i<tam_clases_distintas;i++)
			sumas[i] = 0;
		
		int tam_clases_originales = clases.length;
		
		for(int i=0;i<tam_clases_distintas;i++)
		{
			int clase_a_calcular_la_suma = clases_distintas[i];
			
			for(int j=0;j<tam_clases_originales;j++)
			{
				if(clases[j] == clase_a_calcular_la_suma)
				{
					sumas[i] += tabla_h0[j];
				}
				
			}
		}
		
		return sumas;
	}



	private int[] clases_distintas(int[] clases) 
	{
		int tam = clases.length;
		
		ArrayList clases_sin_repetir = new ArrayList();
		
		List lista = new ArrayList<Integer>();
		
		for(int i=0;i<tam;i++)
			lista.add(clases[i]);
		
		int clases_distintas_que_tiene_la_tabla = 0;
		
		while (!lista.isEmpty())
		{
			clases_distintas_que_tiene_la_tabla++;
			
			Integer num = (Integer) lista.get(0);

			clases_sin_repetir.add(num.intValue());
			lista.remove(num);
			
			while(lista.contains(num))
				lista.remove(num);
		}
		
		int valores_de_clases_sin_repetir[] = new int[clases_distintas_que_tiene_la_tabla];
		
		for(int i=0;i<clases_distintas_que_tiene_la_tabla;i++)
		{
			valores_de_clases_sin_repetir[i] = (Integer) clases_sin_repetir.get(i);
		}
		
		return valores_de_clases_sin_repetir;
	}



	public void quitaAtributosNoSelectivos(){
		for (int i=0;i < this.getTamano_solucion();i++)
			this.get_regla(i).LimpiaRegla();
	}
	
	
	
	public boolean Poblaciones_Iguales(Solucion sol1, Solucion sol2){	
		boolean iguales=true;		
		if(sol1.getTamano_solucion()!=sol2.getTamano_solucion()) iguales=false;		
		if(iguales){
			int i=0;
			while((iguales)&&(i<sol1.getTamano_solucion())){
				if(!sol2.Existe_regla(sol1.get_regla(i))) iguales=false; 
				i++;
			}
		}
		return iguales;
	}
	
	
	public boolean Es_Igual(Solucion sol){	
		boolean iguales=true;		
		if(getTamano_solucion()!=sol.getTamano_solucion()) iguales=false;		
		if(iguales){
			int i=0;
			while((iguales)&&(i<getTamano_solucion())){
				if(!Existe_regla(sol.get_regla(i))) iguales=false; 
				i++;
			}
		}
		return iguales;
	}
	
	
	
	public void eliminarRegla(int indice_regla){
		try{
			set_reglas.remove(indice_regla);
		}catch(Exception e){
			System.out.println("Error elimando un nuevo Cromosoma a la poblacion");
		}
	}
	
	
	public String get_texto_solucion_Completa(){
		String text_solucion="";
		for(int i=0;i<getTamano_solucion();i++){				
			text_solucion+=(i+1)+".- ";
			text_solucion+=get_regla(i).get_texto_Regla()+"\n";
			text_solucion+="     Fitness: "+get_regla(i).getfitness();
			text_solucion+=" N+: "+get_regla(i).get_NumCasos_Positivos();
			text_solucion+="   N-: "+ get_regla(i).get_NumCasos_Negativos();
			text_solucion+="   PI (fit*N+): "+ get_regla(i).getPI();
			text_solucion+="\n";
		}
		
		return text_solucion;
	}
	
	public String toString(){
		return get_texto_solucion_Completa(); 
	}
	
	public Solucion get_Copia()
	{
		Solucion copia=new Solucion();
		for (int i=0;i<this.set_reglas.size();i++){
			Regla regla_copiar=(Regla)set_reglas.get(i);
			copia.insertarRegla(regla_copiar.getCopia());
		}
		
		return copia;
	}
	
	
	/**
	 * Esta funcinn devuelve el concepto en el conjunto de ejemplos enviados como parametro. 
	 * El  concepto son todas las reglas que cubren algun ejemplo en el conjunto.  
	 * @param datosEntrenamiento
	 * @return solucion
	 */
	
	
	public List getReglas()
	{
		return set_reglas;
	}
	
	private Solucion calculaConceptoGreedy(Dataset datosEntrenamiento){
		Solucion nuevo_concepto=new Solucion();		
		int cont_reglas=0;			
		ArrayList indice_ejemplos_No_Cubiertos=new ArrayList();
		
		for (int i=0;i<datosEntrenamiento.getTamano_conjunto_entrenamiento();i++){
			indice_ejemplos_No_Cubiertos.add(new Integer(i));
		}		
		
		int numEjemplosNoCubiertos = indice_ejemplos_No_Cubiertos.size();
		
		Double []pertenenciaFuzzyEjemplos = new Double[numEjemplosNoCubiertos];
		
		for(int i=0;i<numEjemplosNoCubiertos;i++)
		{
			pertenenciaFuzzyEjemplos[i] = 1.0;
		}
		
		while((indice_ejemplos_No_Cubiertos.size()>0)&&(cont_reglas<getTamano_solucion() ) ){				
			Regla regla=get_regla(cont_reglas);
			boolean regla_cubre_algun_ejemplo=false;
			int i=0;
			int cont_ej_cubiertos=0;
			
			if(!Parametros.getInstancia_Parametros().isFuzzy())
			{
				while(i<indice_ejemplos_No_Cubiertos.size()){
					int ind_Ejemplo=Integer.parseInt(indice_ejemplos_No_Cubiertos.get(i)+"");
					if (ind_Ejemplo > datosEntrenamiento.getTamano_conjunto_entrenamiento())
						ind_Ejemplo--;//excusa para pararlo aqui
					EjemploFuzzy ej=datosEntrenamiento.get_EjemploFuzzy(ind_Ejemplo);
					
					// ojo la que estn escrita es la versinn discreta , los cambios hechos son para la versinn fuzzy. hacer un if es fuzzy else
					if(regla.Cubre_Ejemplo_Pos(ej)) {
						regla_cubre_algun_ejemplo=true;
						cont_ej_cubiertos++;  //ojo tenr en cuenta "quitar" fuzzy, % del ejemplo que cubre . `oner un limite para quitar.  <0.1.
						indice_ejemplos_No_Cubiertos.remove(i);//removeFuzzy (i, %cubierto)--> quitarle a lo queda el % cubierto.
					}
					else i++; 
				}
			}
			else
			{
				while(i<indice_ejemplos_No_Cubiertos.size())
				{
					int ind_Ejemplo=Integer.parseInt(indice_ejemplos_No_Cubiertos.get(i)+"");
					if (ind_Ejemplo > datosEntrenamiento.getTamano_conjunto_entrenamiento())
						ind_Ejemplo--;//excusa para pararlo aqui
					EjemploFuzzy ej=datosEntrenamiento.get_EjemploFuzzy(ind_Ejemplo);
					
					// ojo la que estn escrita es la versinn discreta , los cambios hechos son para la versinn fuzzy. hacer un if es fuzzy else
					double pertenencia = regla.Cubre_Ejemplo(ej);
					
					//if(pertenencia > 0)
					//	System.out.println("Pasa por aqun");
					
					pertenenciaFuzzyEjemplos[ind_Ejemplo] = pertenenciaFuzzyEjemplos[ind_Ejemplo] - (pertenenciaFuzzyEjemplos[ind_Ejemplo] * pertenencia);
					
					if(pertenenciaFuzzyEjemplos[ind_Ejemplo] < 0.1)
					{
						regla_cubre_algun_ejemplo=true;
						cont_ej_cubiertos++;  //ojo tenr en cuenta "quitar" fuzzy, % del ejemplo que cubre . `oner un limite para quitar.  <0.1.
						indice_ejemplos_No_Cubiertos.remove(i);//removeFuzzy (i, %cubierto)--> quitarle a lo queda el % cubierto.
					}
					else
						i++;
				}
			}
			
			if (regla_cubre_algun_ejemplo) {
				//if (regla.get_NumCasos_Positivos()-regla.get_NumCasos_Negativos()>1)
				nuevo_concepto.Insertar_regla_Orden_PI(regla.getCopia());
			}
			cont_reglas++;			
		}
				
		return nuevo_concepto;
	}
	
	public Solucion get_Concepto(Dataset datosEntrenamiento)
	{
		
				
		// Aqui es donde hay que descomentar la opcion oportuna
		return calculaConceptoGreedy(datosEntrenamiento);
		//return calculaConceptoGeneticoNoGeneracional(datosEntrenamiento, calculaConceptoGreedy(datosEntrenamiento));
		//return calculaConceptoHeuristica1(conjunto_ejemplos);
		//retunr calculaConceptoHeuristica2(conjunto_ejemplos);

		
	}
	
	////////////////////////////////////////////////////////////
	//                                                        //
	//    A PARTIR DE AQUn SON FUNCIONES PROGRAMADAS POR MI   //
	//                                                        //
	////////////////////////////////////////////////////////////
	
	
	public Solucion(ArrayList reglas)
	{
		this.set_reglas = reglas;
	}
	
	public double  fitness_antiguo(Solucion concepto, Dataset ejemplos){
		int[][] ResultadoClasificaciontra =concepto.Clasificar(ejemplos);
		int aciertos = 0;
		for (int i = 0; i < ResultadoClasificaciontra.length; i++) {
			if (ResultadoClasificaciontra[i][0] == ResultadoClasificaciontra[i][1])
				aciertos++;
			}
		
		return ((double) aciertos/ResultadoClasificaciontra.length);
				
	}
	
	public double fitness(Solucion concepto, Dataset ejemplos, int reglas_totales)
	{
		if(concepto.set_reglas.size() == 0)
			return Double.MIN_VALUE;
		
		int[][] ResultadoClasificaciontra =concepto.Clasificar(ejemplos);
		int aciertos = 0;
		int fallos = 0;
		
		double aciertos_clase_1 = 0;
		double aciertos_clase_2 = 0;
		double num_total_clase_1 = 0;
		double num_total_clase_2 = 0;
		
		for (int i = 0; i < ResultadoClasificaciontra.length; i++) 
		{
			if (ResultadoClasificaciontra[i][0] == ResultadoClasificaciontra[i][1])
				aciertos++;
			else
				fallos++;
			
			/*
			if (ResultadoClasificaciontra[i][0] == 0)
			{
				num_total_clase_1++;
				
				if(ResultadoClasificaciontra[i][0] == ResultadoClasificaciontra[i][1])
					aciertos_clase_1++;
			}
			else
			{
				num_total_clase_2++;
				
				if(ResultadoClasificaciontra[i][0] == ResultadoClasificaciontra[i][1])
					aciertos_clase_2++;
			}
			*/
		}
		//double balanceo = Math.sqrt((aciertos_clase_1/num_total_clase_1)*(aciertos_clase_2/num_total_clase_2));
		return ((1.1 * (reglas_totales / concepto.set_reglas.size()) * Math.exp(-fallos)));///balanceo); // 2011/01/14 /MARR--> dividirlo por porcentaje de balanceo. 
	}
	
	private Solucion calculaConceptoGeneticoGeneracional(Dataset conjunto_ejemplos)
	{
		double porcentaje_cruce = 0.7;
		
		ArrayList mejor_solucion = null;
		
		int num_reglas_conseguidas = set_reglas.size();
		
		// Generamos las poblaciones iniciales
		// Van a ser un nnmero aleatorio entre 1 y 100
		Random al = new Random(Parametros.getInstancia_Parametros().get_Semilla());
		int num_individuos = 100;//  Si tarda mucho hacerlo dependiente del numero de ejemplos de trn y de etiquetas *caracteristicas.
		
		ArrayList<ArrayList<Regla>> poblacion_inicial = new ArrayList();
		
		ArrayList mejor_individuo = null;
		double mejor_fitness = Double.MIN_VALUE;
		
		for(int i=0;i<num_individuos;)
		{
			// Generamos el nnmero de reglas que va a tener el individuo que vamos a generar
			int num_reglas = al.nextInt(num_reglas_conseguidas)+1; // Van a ser entre 1 y el nnmero total de reglas que tengamos
			ArrayList individuo = new ArrayList();
			for (int j=0;j<num_reglas;)
			{
				// Seleccionamos de manera aleatoria la regla a insertar
				int regla_aleatoria = al.nextInt(num_reglas_conseguidas);
				
				// No podemos meter dos veces la misma regla
				Regla regla = (Regla) set_reglas.get(regla_aleatoria);
				if(!individuo.contains(regla))
				{
					individuo.add(regla);
					j++;
				}
			}
			
			poblacion_inicial.add(individuo);
			
			Solucion nueva_sol = new Solucion(individuo);
			double fitness_nuevo_individuo = fitness(nueva_sol, conjunto_ejemplos, num_reglas_conseguidas);
			
			//System.out.println("Fitness del individuo inicial "+i+": "+fitness_nuevo_individuo);
			

			// Seleccionamos el mejor y lo guardamos para no perder el nptimo
			if(fitness_nuevo_individuo > mejor_fitness)
			{
				mejor_fitness = fitness_nuevo_individuo;
				mejor_individuo = individuo;
			}
			i++;
		}
		
		int num_generaciones_sin_mejora = 0;
		
		ArrayList poblacion_antigua = poblacion_inicial;
		
		while(num_generaciones_sin_mejora < 100)//100-500 sin mejora 
		{				
						
			int num_individuos_nueva_poblacion =100;
			
			// Generar la nueva poblacinn mediante seleccinn aleatoria de padres, cruce y mutacinn
			int num_individuos_actuales = poblacion_antigua.size();
			ArrayList nueva_poblacion = new ArrayList();
			
			boolean hemos_mejorado = false;
			
			//nueva_poblacion.add(mejor_individuo);
			
			
			// --- ELITISMO --- //
			
			HashMap<Integer, Double> fitness_individuo = new HashMap<Integer, Double>();
			ValueComparator comparator = new ValueComparator(fitness_individuo);
			TreeMap<Integer, Double> sorted_list = new TreeMap(comparator);
			for(int i=0;i<num_individuos_actuales;i++)
			{
				fitness_individuo.put(i, fitness(new Solucion((ArrayList) poblacion_antigua.get(i)), conjunto_ejemplos, num_reglas_conseguidas));
			}
			
	        sorted_list.putAll(fitness_individuo); 
	        
	        int num_elitistas = 5;
	        int introducidos = 0;
	        for (Integer individuo : sorted_list.keySet()) 
	        { 
	            //if(individuo != null && introducidos < num_elitistas)
	        	if(introducidos < num_elitistas)
	            {
	            	nueva_poblacion.add(poblacion_antigua.get(individuo));
	            	introducidos++;
	            }
	        	else
	        		break;
	        } 
	        
	        // --- FIN DEL ELITISMO --- //
			
			for(int h=0;h<num_individuos_nueva_poblacion-introducidos;h++) // El -introducidos es por el elitismo
			{
				ArrayList padre1 = torneo(5, poblacion_antigua, conjunto_ejemplos, num_reglas_conseguidas, al);//(ArrayList) poblacion_antigua.get(al.nextInt(num_individuos_actuales));//MARR 2011/01/14 Metodo seleccinn --> Torneo 5
				ArrayList padre2 = torneo(5, poblacion_antigua, conjunto_ejemplos, num_reglas_conseguidas, al);//(ArrayList) poblacion_antigua.get(al.nextInt(num_individuos_actuales));//
				
				// En la funcinn cruce ya estn considerado que devuelva un individuo vnlido
				ArrayList nuevo_individuo = cruce_en_dos_puntos(padre1, padre2, al, porcentaje_cruce, conjunto_ejemplos);
				ArrayList individuo_mutado = mutacion(nuevo_individuo, al);
				nueva_poblacion.add(individuo_mutado);  //metodo de  Reemplazo --> Aleatorio si mejor, si no otro aleatorio. si en 5 veces nada, adios. //
				// si mejoro a uno de los padres pa dentro. 
				double fitness_actual = fitness(new Solucion(individuo_mutado), conjunto_ejemplos, num_reglas_conseguidas);
				
				if(fitness_actual > mejor_fitness)
				{
					hemos_mejorado = true;
					mejor_fitness = fitness_actual;
					mejor_individuo = individuo_mutado;
				}
			}
			poblacion_antigua = nueva_poblacion;
			if(hemos_mejorado)
				num_generaciones_sin_mejora=0;
			else
				num_generaciones_sin_mejora++;
		}
		
		Solucion devolver = new Solucion(mejor_individuo);
		return devolver;
	}
	
	
	private Solucion calculaConceptoGeneticoNoGeneracional(Dataset conjunto_ejemplos, Solucion solGreedy)
	{
		
		double porcentaje_cruce = 0.7;
		
		int num_reglas_conseguidas = set_reglas.size();
		
		Random al = new Random(Parametros.getInstancia_Parametros().get_Semilla());
		int num_individuos = 100;//  Si tarda mucho hacerlo dependiente del numero de ejemplos de trn y de etiquetas *caracteristicas.
		
		ArrayList<ArrayList<Regla>> poblacion = new ArrayList();
		
		// Partimos de la base del individuo generado por el greedy
		int num_reglas_sol_greedy = solGreedy.set_reglas.size();
		ArrayList greedy = new ArrayList();
		for(int i=0;i<num_reglas_sol_greedy;i++)
			greedy.add(solGreedy.set_reglas.get(i));
		
		ArrayList mejor_individuo = greedy;
		double mejor_fitness = fitness(new Solucion(greedy), conjunto_ejemplos, num_reglas_conseguidas);
		
		poblacion.add(greedy);
		
		// Empezamos en 1 porque ya hemos metido el greedy, que inicialmente es nuestro mejor individuo
		for(int i=1;i<num_individuos;)
		{
			// Generamos el nnmero de reglas que va a tener el individuo que vamos a generar
			int num_reglas = 0;
			if (num_reglas_conseguidas < 4)
				num_reglas = num_reglas_conseguidas;
			else
				num_reglas = al.nextInt(num_reglas_conseguidas-3)+3; // Van a ser entre 3 y el nnmero total de reglas que tengamos
			ArrayList individuo = new ArrayList();
			for (int j=0;j<num_reglas;)
			{
				// Seleccionamos de manera aleatoria la regla a insertar
				int regla_aleatoria = al.nextInt(num_reglas_conseguidas);
				
				// No podemos meter dos veces la misma regla
				Regla regla = (Regla) set_reglas.get(regla_aleatoria);
				if(!individuo.contains(regla))
				{
					individuo.add(regla);
					j++;
				}
			}
			
			poblacion.add(individuo);
			
			Solucion nueva_sol = new Solucion(individuo);
			double fitness_nuevo_individuo = fitness(nueva_sol, conjunto_ejemplos, num_reglas_conseguidas);
			
			// Seleccionamos el mejor y lo guardamos para no perder el nptimo
			if(fitness_nuevo_individuo > mejor_fitness)
			{
				mejor_fitness = fitness_nuevo_individuo;
				mejor_individuo = individuo;
			}
			i++;
		}
		
		int num_generaciones_sin_mejora = 0;
				
		while(num_generaciones_sin_mejora < 200)//100-500 sin mejora 
		{				
			ArrayList padre1 = torneo(10, poblacion, conjunto_ejemplos, num_reglas_conseguidas, al);//(ArrayList) poblacion_antigua.get(al.nextInt(num_individuos_actuales));//MARR 2011/01/14 Metodo seleccinn --> Torneo 5
			ArrayList padre2 = torneo(10, poblacion, conjunto_ejemplos, num_reglas_conseguidas, al);//(ArrayList) poblacion_antigua.get(al.nextInt(num_individuos_actuales));//
			
			// En la funcinn cruce ya estn considerado que devuelva un individuo vnlido
			ArrayList nuevo_individuo = cruce_en_dos_puntos(padre1, padre2, al, porcentaje_cruce, conjunto_ejemplos);
			ArrayList individuo_mutado = mutacion(nuevo_individuo, al);
			double fitness_actual = fitness(new Solucion(individuo_mutado), conjunto_ejemplos, num_reglas_conseguidas);
			
			int ind_aleatorio_menor = 0;
			double fittnes_menor = Double.MAX_VALUE;

			// Cogemos al peor de 10 individuos aleatorios
			for(int z=0;z<10;z++)
			{
				int aleatorio = al.nextInt(num_individuos);				
				Solucion candidato = new Solucion((ArrayList)(poblacion.get(aleatorio)));
				
				double fitness_candidato = fitness(candidato, conjunto_ejemplos, num_reglas_conseguidas);
				
				if(fitness_candidato < fittnes_menor)
				{
					fittnes_menor = fitness_candidato;
					ind_aleatorio_menor = aleatorio;
				}
			}
			
			// Vemos si es peor que el recinn creado, 
			// y si el recinn creado es mejor que el peor de estos 10 aleatorios,
			// entonces sustituimos un individuo por otro
			if (fitness_actual > fittnes_menor)
			{
				poblacion.add(individuo_mutado);
				poblacion.remove(ind_aleatorio_menor);
			}
			
			if(fitness_actual > mejor_fitness)
			{
				mejor_fitness = fitness_actual;
				mejor_individuo = individuo_mutado;
				num_generaciones_sin_mejora=0;
			}
			else
				num_generaciones_sin_mejora++;
		}
		
		Solucion devolver = new Solucion(mejor_individuo);
		return devolver;
	}
	
	/*
	public ArrayList mutacion_antigua(ArrayList individuo, Random al)
	{
		ArrayList hijo = new ArrayList();
		
		int longitud = individuo.size();
		
		
		double aleatorio = al.nextDouble();
		
		if(aleatorio < 0.05  && longitud > 2)//0.05 es el porcentaje de mutacinn. 
		{
			// Vamos a hacer entre 1 y longitud/2 cambios
			int num_cambios = al.nextInt(longitud/2) + 1;
			
			// Los nndices al principio harnn referencia a sus mismas posiciones
			int indices[] = new int[longitud];
			for(int i=0;i<longitud;i++)
			{
				indices[i]=i;
			}
			
			// Hacemos los cambios en los nndices
			for(int i=0;i<num_cambios;i++)
			{
				int indice_1 = al.nextInt(longitud);
				int indice_2 = al.nextInt(longitud);
				
				int temporal = indices[indice_1];
				indices[indice_1] = indices[indice_2];
				indices[indice_2] = temporal;
			}
			
			// Creamos el hijo mutado con los cambios realizados
			for(int i=0;i<longitud;i++)
			{
				hijo.add(individuo.get(indices[i]));
			}
			
			return hijo;
		}
		
		return individuo;
		
	}
	*/
	
	public ArrayList mutacion(ArrayList individuo, Random al)
	{
		double prob = al.nextDouble();
		
		if(prob < 0.95)
		{
			ArrayList hijo = new ArrayList();
			
			int longitud = individuo.size();
			double aleatorio = al.nextDouble();
			
			if(aleatorio < 0.05  && longitud > 2)//0.05 es el porcentaje de mutacinn. 
			{
				// Vamos a hacer entre 1 y longitud/2 cambios
				int num_cambios = al.nextInt(longitud/2) + 1;
				
				// Los nndices al principio harnn referencia a sus mismas posiciones
				int indices[] = new int[longitud];
				for(int i=0;i<longitud;i++)
				{
					indices[i]=i;
				}
				
				// Hacemos los cambios en los nndices
				for(int i=0;i<num_cambios;i++)
				{
					int indice_1 = al.nextInt(longitud);
					int indice_2 = al.nextInt(longitud);
					
					int temporal = indices[indice_1];
					indices[indice_1] = indices[indice_2];
					indices[indice_2] = temporal;
				}
				
				// Creamos el hijo mutado con los cambios realizados
				for(int i=0;i<longitud;i++)
				{
					hijo.add(individuo.get(indices[i]));
				}
				
				return hijo;
			}
			return individuo;
		}
		else
		{
			boolean mutacion_realizada = false;
			
			for(int intentos=0;intentos<10 && !mutacion_realizada;intentos++)
			{
				int reglas_totales = set_reglas.size();
				int num_regla_aleatoria = al.nextInt(reglas_totales);
			
				if(!(individuo.contains(set_reglas.get(num_regla_aleatoria))))
				{
					int regla_a_machacar = al.nextInt(individuo.size());
					individuo.set(regla_a_machacar, set_reglas.get(num_regla_aleatoria));
					mutacion_realizada = true;
				}
			}
			return individuo;
		}
	}
	
	private ArrayList torneo(int num_contrincantes, ArrayList poblacion, Dataset conjunto_ejemplos, int num_reglas_conseguidas, Random al)
	{
		HashMap<Integer, Double> fitness_individuo = new HashMap<Integer, Double>();
		ValueComparator comparator = new ValueComparator(fitness_individuo);
		TreeMap<Integer, Double> sorted_list = new TreeMap(comparator);
		int num_individuos_actuales = poblacion.size();
		
		for(int i=0;i<num_contrincantes;i++)
		{
			int num_aleatorio = al.nextInt(num_individuos_actuales);
			fitness_individuo.put(num_aleatorio, fitness(new Solucion((ArrayList) poblacion.get(num_aleatorio)), conjunto_ejemplos, num_reglas_conseguidas));
		}

		sorted_list.putAll(fitness_individuo); 
 
		Object[]lista = sorted_list.keySet().toArray();
		
		int ganador = Integer.parseInt(lista[0].toString());
		
		return (ArrayList)poblacion.get(ganador);
	}
	
	public ArrayList cruce_en_dos_puntos(ArrayList lista1, ArrayList lista2, Random al, double porcentaje_cruce, Dataset conjunto_ejemplos)
	{
		ArrayList hijo = null;
		
		int long1 = lista1.size();
		int long2 = lista2.size();
		
		int maximo = 0;
		
		if(long1<=long2)
			maximo = long1;
		else
			maximo = long2;
		
		hijo = new ArrayList();
		
		// maximo es el punto mnximo por donde podemos hacer el segundo corte
		// mitad_maximo es el punto mnximo por donde haremos el primer corte
		int mitad_maximo = maximo/2;
		
		// Si son dos listas muy pequenas, devolvemos la primera (no hay cruce)
		if(mitad_maximo < 2)
			return lista1;
		
		
		int primer_corte = al.nextInt(mitad_maximo);
		
		int segundo_corte = primer_corte + al.nextInt(maximo-primer_corte) + 1;
		
		List trozo_intermedio = lista1.subList(primer_corte, segundo_corte);
		
		for(int i=0;i<primer_corte;i++)
		{
			if(!trozo_intermedio.contains(lista2.get(i)) && !hijo.contains(lista2.get(i)))
			{
				hijo.add(lista2.get(i));
			}
			else
			{
				int indice_alternativo = i + 1;
				
				boolean hemos_introducido_uno = false;
				
				// Vamos a introducir un hijo de la del primer padre que no es el previsto, porque ese ya
				// estn en el trozo intermedio que vamos a meter del segundo padre
				while(!hemos_introducido_uno && indice_alternativo<long1)
				{
					if(!trozo_intermedio.contains(lista2.get(indice_alternativo)) && !hijo.contains(lista2.get(indice_alternativo)))
					{
						hijo.add(lista2.get(indice_alternativo));
						hemos_introducido_uno = true;
					}
					else
					{
						indice_alternativo++;
					}
				}
			}
		}
		
		
		for(int i=primer_corte;i<segundo_corte;i++)
		{
			if(!hijo.contains(lista1.get(i)))
			{
				hijo.add(lista1.get(i));
			}
		}
		
		for(int i=segundo_corte;i<long2;i++)
		{
			if(!hijo.contains(lista2.get(i)))
			{
				hijo.add(lista2.get(i));
			}
		}
		return hijo;
	}

	public ArrayList cruce(ArrayList lista1, ArrayList lista2, Random al, double porcentaje_cruce, Dataset conjunto_ejemplos)
	{
		ArrayList hijo = null;
	
		hijo = new ArrayList();
		double num = al.nextDouble();
		
		// Vamos a hacer siempre cruce
		//if(num<porcentaje_cruce)
		//{
			int num1 = lista1.size();
			int num2 = lista2.size();
			
			int corte_inicial;
			int corte_fin;
			if(num1<num2)
			{
				corte_inicial = al.nextInt(num1);
				corte_fin = num2;
				
				for(int i=0;i<corte_inicial;i++)
				{
					if(!hijo.contains(lista1.get(i)))
						hijo.add(lista1.get(i));
				}
				for(int i=corte_inicial;i<corte_fin;i++)
				{
					if(!hijo.contains(lista2.get(i)))
						hijo.add(lista2.get(i));
				}
			}
			else
			{
				corte_inicial = al.nextInt(num2);
				corte_fin = num1;
				
				for(int i=0;i<corte_inicial;i++)
				{
					if(!hijo.contains(lista2.get(i)))
						hijo.add(lista2.get(i));
				}
				for(int i=corte_inicial;i<corte_fin;i++)
				{
					if(!hijo.contains(lista1.get(i)))
						hijo.add(lista1.get(i));
				}
			}
		/*
		}
		 
		else
		{
			double elegir_padre = al.nextDouble();
			if(elegir_padre<0.5)
				hijo = lista1;
			else
				hijo = lista2;
		}
	    */
		return hijo;
		
	}
	
	
	/*
	public boolean es_valido(ArrayList conjunto_de_reglas, Dataset conjunto_ejemplos) {

		int cont_reglas = 0;
		ArrayList indice_ejemplos_No_Cubiertos = new ArrayList();
		for (int i = 0; i < conjunto_ejemplos
				.getTamano_conjunto_entrenamiento(); i++) {
			indice_ejemplos_No_Cubiertos.add(new Integer(i));
		}

		while ((indice_ejemplos_No_Cubiertos.size() > 0)
				&& (cont_reglas <conjunto_de_reglas.size())) {
			Regla regla = (Regla) conjunto_de_reglas.get(cont_reglas);
			int i = 0;

			while (i < indice_ejemplos_No_Cubiertos.size()) {
				int ind_Ejemplo = Integer.parseInt(indice_ejemplos_No_Cubiertos
						.get(i)
						+ "");
				Ejemplo ej = conjunto_ejemplos.get_ejemplo(ind_Ejemplo);
				if (regla.Cubre_Ejemplo_Pos(ej)) {
					indice_ejemplos_No_Cubiertos.remove(i);
				} else
					i++;
			}
			cont_reglas++;
		}
		
		if(indice_ejemplos_No_Cubiertos.size()>0)
			return false;
		else
			return true;
	}
	*/
}
