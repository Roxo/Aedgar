package Principal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.io.*;



import edgar.*;


 /**
* Esta clase realiza una ejecución con los parametros recibidos por linea de comando.
* usage: 
* Almacena la salida en la carpeta de resultados con el nombre del fichero de datos más la fecha de ejecución.
* edgar         [nodos 1,] [poblacion] [representados 0,1,] [semilla] [topologia 1,2] [claseobjetivo -1,0,] [GenSinComunic 1,..] [EpocasSinMejora 1,..] [TipoDeDiscretizador: es un entero]
  por defecto      [10]       [500]       [2]              [1234567] [1 Estrella]       [-1 ninguna]             [50]                  [5]
* La salida está compuesta por los ficheros de entrenamiento y test utilizados, reglas generadas, resultado (copia de salida por pantalla) 
*  y resumen de resultado separado por comas para su importación a fichero csv en excel . 
*  Etiqueta, Nodos, Poblacion, Representados, Semilla, Topologia, Objetivo, EpocasMejora, Tiempo, Ejemplos, Evaluaciones, Evaluaciones*d,Comunicaciones,Reglas,%test, %prueba
 */



public class Principal   {

	
	public static String get_Cabecera(){
		String cabecera=" \n\n     EDGAR Evolutionary Distributed Genetic Algorithm for Rule extraction\n\n\n";

		return cabecera;
	}
	
	public static void  lanzarHilos(Dataset ejemplos_train){
		Parametros param = Parametros.getInstancia_Parametros();
		i_Supervisor superv; 
		//		 Lanzo el Supervisor eligiendo el tipo de supervisor segun los parametros globales
		superv = new Pool(ejemplos_train);
		superv.hilo().start(); 

		 
 
		 // Lanza los Hilos, con susconjuntos de entrenamientos iniciales
		Dataset ejemplos_asignados_Nodos[]=new Dataset[param.get_Numero_Nodos()];
		 ejemplos_asignados_Nodos=ejemplos_train.getN_Sub_conjuntos_ejemplosFuzzy(param.get_Numero_Nodos());
		 
			// Creo los distintos Nodos
			i_Nodo NGA[]=new i_Nodo[param.get_Numero_Nodos()];
			 NGA=new i_Nodo[param.get_Numero_Nodos()];
		 
		 Dataset aux_conjunto;
		 
		for(int i=0;i<param.get_Numero_Nodos();i++){
			if (param.get_repartir_entrenamiento_nodos())
			//Distribuido 
				aux_conjunto = ejemplos_asignados_Nodos[i];
			//modelo islas
			else
				aux_conjunto=ejemplos_train;
				NGA[i]=new Isla (aux_conjunto, i);
		}
		for(int i=0;i<param.get_Numero_Nodos();i++){
					NGA[i].hilo().start();
		}
			
	}
	
	
	public static void ejecutar(String nombre_fichero_Entrenamiento,
			String Nombre_fichero_test,String etiqueta) {
		String prefijo = etiqueta;
		etiqueta = nombre_fichero_Entrenamiento.substring(0,nombre_fichero_Entrenamiento.length()-7);
		int k=nombre_fichero_Entrenamiento.length();
		int app = 0;
		for (int i = nombre_fichero_Entrenamiento.length(); (i = nombre_fichero_Entrenamiento.lastIndexOf('-', i - 1)) != -1; ){ 
		    if (app < 2)
		    	k=i;
			app +=1;
	}
		String particion;
		if (app >= 2){
			particion = nombre_fichero_Entrenamiento.substring(k,nombre_fichero_Entrenamiento.length()-7); 
		}
		else
		    particion = "";
		Parametros param = Parametros.getInstancia_Parametros();
		EntradaSalidaFuzzy InOut = new EntradaSalidaFuzzy();
			
		nombre_fichero_Entrenamiento = param.get_carpeta_datos()
				+ nombre_fichero_Entrenamiento;
		Nombre_fichero_test = param.get_carpeta_datos() + Nombre_fichero_test;
		long Hora_Inicio = System.currentTimeMillis();
		Dataset ejemplos_train = InOut
				.Cargar_conjuntoEntrenamiento(nombre_fichero_Entrenamiento,
						true);
		Dataset ejemplos_tst = InOut
		.Cargar_conjuntoEntrenamiento(Nombre_fichero_test, false);
             
		
			param.setPlantilla(ejemplos_train.getPlantilla());	
			
			/*v2
	    	 *  Modificar dinamicamente el número de nodos y la población por balanceo de clases. 
	    	 *  se copia a cada nodo la clase minoritaria y se calcula el número de nodos por el ratio entre la mayoritaria y la minoritaria.
	    	 *  Minimo de nodos = 2; Máximo de nodos 16.
	    	 *  La población se calcula con un ratio de 1 regla por ejemplo máximo (para datasets muy pequeños), 
	    	 *  y ratio ideal de 10 ejemplos por regla con un máximo de 200.
	    	 */
	    	if (Parametros.getInstancia_Parametros().isBalanceoClases()){
	    		//Ratio de balanceo es la mayoritaria entre minoritaria. El número de nodos es ese númro.
	    		int tempNodos= 0;
	    		tempNodos = Math.round(ejemplos_train.RatioBalanceo());
	    		//if (tempNodos > 40 ) tempNodos = 40;
	    		if (tempNodos < 3 ) tempNodos = 2;
	    		Parametros.getInstancia_Parametros().set_Numero_Nodos(tempNodos);
	    		Parametros.getInstancia_Parametros().setClaseObjetivo(ejemplos_train.claseMinoritaria());
	    		param.depura("Nodos reales: " + tempNodos, 0);
	    	}
	    	
	    		
	       //FIN MOD v2    		
	    
			
			
			
			
			
			
			
			lanzarHilos(ejemplos_train);
		
		//Se queda esperando aquí hasta que la solución final esté disponible con un wait

			SolucionFinal SolucionRegal=SolucionFinal.getInstancia_SolucionFinal();
		Solucion conjunto_reglas = SolucionRegal.Get_SolucionFinal().get_Copia();
		SolucionRegal.Reiniciar_SolucionFinal();
			
		param.depura("Solución Final" ,0);
		param.depura(conjunto_reglas.get_texto_solucion_Completa(),0);

		int[][] ResultadoClasificaciontst = conjunto_reglas
				.Clasificar(ejemplos_tst);
		int[][] ResultadoClasificaciontra = conjunto_reglas
				.Clasificar(ejemplos_train);
		
		java.util.Calendar fecha = java.util.Calendar.getInstance();
		String FechaHora = fecha.get(java.util.Calendar.DATE) + "-"
		+ fecha.get(java.util.Calendar.MONTH) + "-"
		+ fecha.get(java.util.Calendar.YEAR) + "  "
		+ fecha.get(java.util.Calendar.AM) + "-"
		+ fecha.get(java.util.Calendar.HOUR) + "-"
		+ fecha.get(java.util.Calendar.MINUTE);
		
		String _carpeta_resultados = Parametros
				.getInstancia_Parametros().get_carpeta_resultados();
		
		etiqueta += "_N"+ Parametros
		.getInstancia_Parametros().get_Numero_Nodos() +"_P"+ Parametros
		.getInstancia_Parametros().getPoblacion()+"_R"+ Parametros
		.getInstancia_Parametros().get_num_poco_representados()+"_S"+ Parametros
		.getInstancia_Parametros().get_Semilla()+"_T"+ Parametros
		.getInstancia_Parametros().get_Topologia()+ "_C"+  Parametros
		.getInstancia_Parametros().getClaseObjetivo() + "_G" +Parametros
		.getInstancia_Parametros().get_numGeneracionesSinComunicacion();

		InOut.generar_Fichero_Resultado(_carpeta_resultados + etiqueta + "result "+ FechaHora + ".tst",
				ResultadoClasificaciontst);
		InOut.generar_Fichero_Resultado(_carpeta_resultados + etiqueta+ "result "+ FechaHora + ".tra",
				ResultadoClasificaciontra);
		
		InOut.generar_Fichero_Reglas(_carpeta_resultados + etiqueta + "reglas "+ FechaHora + ".dat",
				conjunto_reglas);
		
		InOut.generar_Fichero_PlantillaResumen(_carpeta_resultados + etiqueta + "plantilla_Resumen "+ FechaHora + ".dat");
		
		InOut.generar_Fichero_PlantillaCompleta(_carpeta_resultados + etiqueta + "plantilla_Completa "+ FechaHora + ".dat");

		System.out.println();
		System.out.println();

		long Hora_Fin = System.currentTimeMillis();

		double minutos_empleados = (Hora_Fin - Hora_Inicio) / (Double.parseDouble("60000"));

		int _NumClases = param.getPlantilla().get_numero_Clases();
		int cont_fallos = 0;
		int matriz_Confusion[][] = new int[_NumClases][_NumClases];

		for (int aux = 0; aux < _NumClases; aux++) {
			for (int aux2 = 0; aux2 < _NumClases; aux2++) {
				matriz_Confusion[aux][aux2] = 0;
			}
		}

		for (int i = 0; i < ResultadoClasificaciontst.length; i++) {
			if (ResultadoClasificaciontst[i][0] != ResultadoClasificaciontst[i][1])
				cont_fallos++;
			matriz_Confusion[ResultadoClasificaciontst[i][0]][ResultadoClasificaciontst[i][1]]++;
		}

		
		String nombreFichero_resultado = etiqueta + "resultado ";

		
		nombreFichero_resultado += " " + FechaHora + ".dat";

		String Texto_Resultado = get_Cabecera();
	

		Texto_Resultado += "   --- RESULTADOS  día: " + FechaHora
				+ " ---\n\n";

		Texto_Resultado += "Número de hilos: " + param.get_Numero_Nodos()
				+ "\n";
		double ejemplosNodo = (param.get_repartir_entrenamiento_nodos()==true ? (ejemplos_train.getTamanho_conjunto_entrenamiento() / param
				.get_Numero_Nodos()) : ejemplos_train.getTamanho_conjunto_entrenamiento());
		Texto_Resultado += "Ejemplos de entrenamientos distribuidos por nodo "	+ ejemplosNodo; 
		Texto_Resultado += "\nSemilla: " + param.get_Semilla() + "\n";
		Texto_Resultado += "Epocas sin mejora para parar: "
				+ param.get_Numero_Maximo_Epocas_Sin_Mejora() + "\n";
		Texto_Resultado += "Fichero datos test: " + Nombre_fichero_test + "\n";
		Texto_Resultado += "Fichero datos entrenamiento: "
				+ nombre_fichero_Entrenamiento + "\n";

		
		Texto_Resultado += "Repartir datos entrenamiento: " + param.get_repartir_entrenamiento_nodos() + "\n";
		
		Texto_Resultado += "Valor g: " + param.get_g() + "\n";
		Texto_Resultado += "Ratio de Migracion nu: "
				+ param.get_ratio_migracion_nu() + "\n";
		Texto_Resultado += "Ratio de foraneos P: "
				+ param.get_ratio_adaptacion_foraneo_P() + "\n";
		Texto_Resultado += "Tiempo empleado: " + minutos_empleados
				+ " minutos\n";
		
		Estadisticas costes=Estadisticas.getInstancia_buffer();
		
		int _Num_Evaluaciones=costes.get_NumeroEvaluaciones();
		int _Num_EvaluacionesDesglosado=costes.get_NumeroEvaluacionesDesglosado();
		int _Num_Comunicaciones=costes.get_NumeroComunicaciones();
		
		
		
		Texto_Resultado += "\nTamaño Entrenamiento:      "+ejemplos_train.getTamanho_conjunto_entrenamiento()
		+ "\nEvaluaciones:     "+ _Num_Evaluaciones
		+ "\nEvaluaciones*datos:     "+ _Num_EvaluacionesDesglosado
		+ "\nComunicaciones    "+ _Num_Comunicaciones;
		
		
		Texto_Resultado += "\n";
		Texto_Resultado += "Número de Reglas: " + conjunto_reglas.getTamaño_solucion();
		Texto_Resultado += "\n\n\n";
		

		int numAciertosTst = ResultadoClasificaciontst.length - cont_fallos;
		Texto_Resultado += "******************  RESULTADOS  5x2 Test  **************\n";
		Texto_Resultado += "Numero de ejemplos: "
				+ ResultadoClasificaciontst.length + "\n";
		Texto_Resultado += "Numero de ejemplos bien clasificados: "
				+ (numAciertosTst) + " -> "
				+ (numAciertosTst / (double) ResultadoClasificaciontst.length)
				+ "\n";
		Texto_Resultado += "Numero de ejemplos mal clasificados: "
				+ cont_fallos + "\n";
		Texto_Resultado += "*******************************************************\n";
		Texto_Resultado += "\n";

		Texto_Resultado += "***  Matriz de confusión Test  ***\n";
		for (int aux = 0; aux < param.getPlantilla().get_numero_Clases(); aux++)
			Texto_Resultado += "      " + param.getPlantilla().get_Valores_Clase().get(aux);
		Texto_Resultado += "\n";
		for (int aux = 0; aux < param.getPlantilla().get_numero_Clases(); aux++) {
			Texto_Resultado += param.getPlantilla().get_Valores_Clase().get(aux) + "    ";
			for (int aux2 = 0; aux2 < param.getPlantilla().get_numero_Clases(); aux2++) {
				Texto_Resultado += matriz_Confusion[aux][aux2] + "     ";
			}
			Texto_Resultado += "\n";
		}
		Texto_Resultado += "*******************************************************\n";
		Texto_Resultado += "\n\n";
		
		String Texto_Clases = "";
	       int totalClase =0, aciertosClase=0;
			for (int aux = 0; aux < param.getPlantilla().get_numero_Clases(); aux++) {
				for (int aux2 = 0; aux2 < param.getPlantilla().get_numero_Clases(); aux2++) {
					totalClase += matriz_Confusion[aux][aux2]; 
					if (aux==aux2)  aciertosClase = matriz_Confusion[aux][aux2] ; 
					
				}
				Texto_Clases +=  "," + aciertosClase/(double)totalClase;
				totalClase =0;
				aciertosClase=0;
				
			}

		for (int aux = 0; aux < param.getPlantilla().get_numero_Clases(); aux++) {
			for (int aux2 = 0; aux2 < param.getPlantilla().get_numero_Clases(); aux2++) {
				matriz_Confusion[aux][aux2] = 0;
			}
		}
		// Resultados de los datos de entrenamiento
		cont_fallos = 0;
		for (int i = 0; i < ResultadoClasificaciontra.length; i++) {
			if (ResultadoClasificaciontra[i][0] != ResultadoClasificaciontra[i][1])
				cont_fallos++;
			matriz_Confusion[ResultadoClasificaciontra[i][0]][ResultadoClasificaciontra[i][1]]++;
		}
		int numAciertosTra = ResultadoClasificaciontra.length - cont_fallos;
		Texto_Resultado += "******************  RESULTADOS  Datos Entrenamiento ************\n";
		Texto_Resultado += "Numero de ejemplos: "
				+ ResultadoClasificaciontra.length + "\n";
		Texto_Resultado += "Numero de ejemplos bien clasificados: "
				+ (numAciertosTra) + " -> "
				+ (numAciertosTra / (double) ResultadoClasificaciontra.length)
				+ "\n";
		;
		Texto_Resultado += "Numero de ejemplos mal clasificados: "
				+ cont_fallos + "\n";
		Texto_Resultado += "*******************************************************\n";
		Texto_Resultado += "\n\n";

		Texto_Resultado += "***  Matriz de confusión Entrenamiento  ***\n";
		for (int aux = 0; aux < param.getPlantilla().get_numero_Clases(); aux++)
			Texto_Resultado += "      " + param.getPlantilla().get_Valores_Clase().get(aux);
		Texto_Resultado += "\n";
		for (int aux = 0; aux < param.getPlantilla().get_numero_Clases(); aux++) {
			Texto_Resultado += param.getPlantilla().get_Valores_Clase().get(aux) + "    ";
			for (int aux2 = 0; aux2 < param.getPlantilla().get_numero_Clases(); aux2++) {
				Texto_Resultado += matriz_Confusion[aux][aux2] + "    ";
			}
			Texto_Resultado += "\n";
		}
		Texto_Resultado += "*******************************************************\n\n";
		
		Texto_Resultado += "Particiones de la plantilla\n\n";
		
		Texto_Resultado += param.getPlantilla().toString();
		
		Texto_Resultado += "\n*******************************************************\n";
		
		System.out.println(Texto_Resultado);

		FileOutputStream f = null;
		try {
			f = new FileOutputStream(param.get_carpeta_resultados()
					+ nombreFichero_resultado);
			f.write(Texto_Resultado.getBytes());
			f.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		
		/**
		 * Etiqueta, Nodos, Poblacion, Representados, Semilla, Topologia, Objetivo, EpocasMejora, Tiempo, Ejemplos, Evaluaciones, Evaluaciones*d,Comunicaciones,Reglas,%test, %prueba, 
		 */
		
		
		
		
		String Texto_Resumen = prefijo+","+ particion+ "," + etiqueta +"," +  param.get_Numero_Nodos() +","  + param.getPoblacion()+ ","+ param.get_num_poco_representados()+"," 
		+ param.get_Semilla() + "," +param.get_Topologia() +"," +  param.getClaseObjetivo()+ "," + param.get_Numero_Maximo_Epocas_Sin_Mejora() +  ","  + 
		minutos_empleados + "," + ejemplos_train.getTamanho_conjunto_entrenamiento() + "," + _Num_Evaluaciones +"," +  _Num_EvaluacionesDesglosado +
		","+ _Num_Comunicaciones +  "," + conjunto_reglas.getTamaño_solucion() + ","+ (numAciertosTst / (double) ResultadoClasificaciontst.length)+ 
		"," + (numAciertosTra / (double) ResultadoClasificaciontra.length)+ Texto_Clases+ "\n";
		System.out.println("Etiqueta	 Nodos	 Poblacion	 Representados	 Topologia	 Objetivo	 EpocasMejora	 Tiempo	T Grid	 Ejemplos	 Evaluaciones	 Evaluaciones*d	Comunicaciones	Reglas	%test	 %Entrenamiento");
		System.out.println(Texto_Resumen);
		
      String nombreFichero_Resumen = etiqueta + "resumen ";

		
		nombreFichero_Resumen+= " " + FechaHora + ".dat";
		f = null;
		try {
			f = new FileOutputStream(param.get_carpeta_resultados()
					+ nombreFichero_Resumen);
			f.write(Texto_Resumen.getBytes());
			f.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}	

	}

	public static void main(String[] args) {
	
		System.out.println(get_Cabecera());
		
		System.out.println("");
		System.out.println("");
		
		String p_etiqueta = "EDGAR2.0";//etiqueta que se usa para diferenciar ejecuciones en modo script unix; 
		String nombre_Fichero_train="";
		String nombre_Fichero_tst="";
		
		if (args.length>0){
			if (Parametros.getInstancia_Parametros().isFormatoDatosTra())//v2
			{
				nombre_Fichero_train=args[0]+ "tra.dat";
				nombre_Fichero_tst= args[0]+"tst.dat";
			}
			else{
				nombre_Fichero_train=args[0]+ ".tra";
				nombre_Fichero_tst= args[0]+".tst";
			}
		    p_etiqueta = args[0];
		   
		    if (args.length>1)//numero de nodos
		    	{int tempNodos= Integer.parseInt( args[1]);  
		    	Parametros.getInstancia_Parametros().set_Numero_Nodos(tempNodos);
		    	System.out.println(" Numero de nodos: " + tempNodos);
		    }
		    	
		    if (args.length>2)//Poblacion
		    {Parametros.getInstancia_Parametros().set_M(Integer.parseInt( args[2]));
	    	System.out.println(" Poblacion: " + args[2]);
		    }
	    	if (args.length>3)//Num. poco representados
		    {Parametros.getInstancia_Parametros().set_num_poco_representados(Integer.parseInt( args[3]));
	    	System.out.println(" Poco Representados: " + args[3]);}
	    	if (args.length>4)//semilla
		    {Parametros.getInstancia_Parametros().set_Semilla(Integer.parseInt( args[4]));
	    	System.out.println(" semilla: " + args[4]);
		    }
	    	if (args.length>5)//Topologia
		    {Parametros.getInstancia_Parametros().set_Topologia(Integer.parseInt( args[5]));
	    	System.out.println(" Topología: " + args[5]);
	        }
	    	if (args.length>6)//Clase Objetivo
		    {Parametros.getInstancia_Parametros().setClaseObjetivo(Integer.parseInt( args[6]));
	    	System.out.println(" Clase Objetivo: " + args[6]);
	        }
	    	if (args.length>7)//Generaciones sin comunicacion
		    {Parametros.getInstancia_Parametros().set_numGeneracionesSinComunicacion(Integer.parseInt( args[7]));
	    	System.out.println(" GeneracionesSinComunic: " + args[7]);
	        }
	    	if (args.length>8)//Epocas sin mejora 
		    {Parametros.getInstancia_Parametros().set_Numero_Maximo_Epocas_Sin_Mejora(Integer.parseInt( args[8]));
	    	System.out.println(" Epocas Sin Mejora: " + args[8]);
	        }
	    	if (args.length>9)//carpeta de datos
	    	
		    {System.out.println(" carpeta datos: " + args[9]);
	    	Parametros.getInstancia_Parametros().set_carpeta_datos(args[9]+ "/");
	    	}
	    	if (args.length>10)//v2 Para guardar restados por carpetas
	    	{
	    		System.out.println(" tipoCobertura: " + args[10]);
	    		Parametros.getInstancia_Parametros().set_cobertura((args[10]));
	    	}
		    if (args.length> 11)// v2 nivel depuracion
	    	{
	    		System.out.println(" subdirectorio: " + args[11]);
	    		Parametros.getInstancia_Parametros().set_carpeta_resultados((args[11]));
	    		File directorio = new File(Parametros.getInstancia_Parametros().get_carpeta_resultados());
	    	
			   if (directorio.mkdir())
			     System.out.println("Se ha creado directorio");
			   else
			     System.out.println("No se ha podido crear el directorio");
	    	}
	    	if (args.length>12)
	    	{
		    	System.out.println(" Depuracion: " + args[12]);
		    	Parametros.getInstancia_Parametros().set_Nivel_Depuracion(Integer.parseInt(args[12]));
	    	}
	    	if (args.length>13)
	    	{
	    		System.out.println(" tipoDeDiscretizador: " + args[13]);
	    		Parametros.getInstancia_Parametros().set_tipo_de_discretizador((args[13]));
	    	}
	    /*	if (args.length> 12)//v2  Prefijo para agrupar
	    	{System.out.println(" Prefijo: " + args[12]);
	    	p_etiqueta = args[12];
	    	}*/

	    	
	    	//Parametros.getInstancia_Parametros().set_carpeta_resultados((args[9]));
	    	 /*
	          por si quisieramos guardar los resultados por carpeta
			  */ 
	
		}
		else{
		 
			System.out.print(" usage : edgar [nodos 1,] [poblacion] [representados 0,1,] [semilla] [topologia 1,2] [claseobjetivo -1,0,] [GenSinComunic 1,..] [EpocasSinMejora 1,..][fichero salida a..z] [tipo de discretizador 1..7");
			System.out.print(" Defaults:     [10]       [500]       [2]                  [1234567] [1 Estrella]    [-1 ninguna]          [50]                 [5]");
			return;
		 }
					
				ejecutar(nombre_Fichero_train, nombre_Fichero_tst,p_etiqueta);
				
	}


	
	}
