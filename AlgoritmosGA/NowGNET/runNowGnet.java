package NowGNET;

import java.io.FileOutputStream;



public class runNowGnet {

	public static String getCabecera(){
		String cabecera=" Tesina Fin de Máster:\n\n\n";
		cabecera+="     Alumno: José Luis Toscano Muñoz.\n";
		cabecera+="     Director del proyecto: Miguel Angel Rodriguez Román.\n";
		cabecera+="     Codirector del proyecto: Antonio Peregrin Rubio.\n";
		return cabecera;
	}

	
	public static void main(String[] args) {
		
		
		
		//System.out.println(get_Cabecera());
	
		
		System.out.println("");
		System.out.println("");
		String p_etiqueta = "NowGnet" ;//
		String nombre_Fichero_train="";
		String nombre_Fichero_tst="";
		boolean aplicarDuplicarClaseMinoritaria=false;
		
		if (args.length>0){
			nombre_Fichero_train=args[0]+ ".tra";
		    nombre_Fichero_tst= args[0]+".tst";
		    p_etiqueta = args[0];
		    if (args.length>1)//numero de nodos
		    	{
		    	ParametrosGlobales.getInstancia_Parametros().setNumeroNodosG(Integer.parseInt( args[1]));  
		    	ParametrosGlobales.getInstancia_Parametros().setNumeroNodosE(Integer.parseInt( args[1]));
		    	System.out.println(" Numero de nodos G: " + args[1]);
		    	}
		    if (args.length>2)//Población
		    {ParametrosGlobales.getInstancia_Parametros().setM(Integer.parseInt( args[2]));
	    	System.out.println(" Poblacion: " + args[2]);
		    }
	   

	    	if (args.length>3)//Semilla
		    {ParametrosGlobales.getInstancia_Parametros().setSemilla(Integer.parseInt( args[3]));
	    	System.out.println(" semilla: " + args[3]);
		    }
	    	
	    	if (args.length>4)//SubCarpeta
		    {
	    		if (args[4]!=""){
	    			String CarpetaDatos=ParametrosGlobales.getInstancia_Parametros().getCarpetaDatos();
	    			CarpetaDatos+=args[4]+"/";
	    			ParametrosGlobales.getInstancia_Parametros().setCarpetaDatos(CarpetaDatos);
	    			
	    			/*String CarpetaResultados=ParametrosGlobales.getInstancia_Parametros().getCarpetaResultados();
	    			CarpetaResultados+=args[4]+"/";
	    			ParametrosGlobales.getInstancia_Parametros().setCarpetaResultados(CarpetaResultados);
	    		
	    			File file=new File(CarpetaResultados);
	    			if(!file.exists()) file.mkdir();
	    			*/
	    			p_etiqueta=args[4]+"_"+p_etiqueta;
	    			
	    		}
	    		
	    	System.out.println(" Subcarpeta de datos: " + args[4]);
		    }

	    	
	     	if (args.length>5)
		    {
	     		if  (args[5].contains("1")){
	     			aplicarDuplicarClaseMinoritaria=true;
	     		}
		    }
	     	
	     	
		    if (args.length>6)//Macro Ciclos
		    	{
		    	ParametrosGlobales.getInstancia_Parametros().setNumeroMacroCiclos(Integer.parseInt( args[6]));
		    	System.out.println(" Macro ciclos: " + args[6]);
		    }
		    
		    if (args.length>7)//Micro Ciclos
	    	{
		    	ParametrosGlobales.getInstancia_Parametros().setNumeroMicroCiclos(Integer.parseInt( args[7]));
		    	System.out.println(" Micro ciclos: " + args[7]);
	    	}
	    	

		}
		else{
		 
			System.out.print(" usage : Gnet [Etiqueta] [nodosG 1,] [poblacion] [semilla] [Subcarpeta] [Preprocesamineto 0, 1] [Macro Ciclos] [Micro Ciclos]");
			return;
		 }
					
			EjecutarPruebaUnix(nombre_Fichero_train, nombre_Fichero_tst,p_etiqueta,aplicarDuplicarClaseMinoritaria);
	}
	
	
	
	
	public static void EjecutarPruebaUnix(String nombre_fichero_Entrenamiento,
			String Nombre_fichero_test,String etiqueta, boolean _aplicarDuplicarClaseMinoritaria) {

		
		ParametrosGlobales param = ParametrosGlobales.getInstancia_Parametros();
		
		EntradaSalida InOut = new EntradaSalida();
		


		nombre_fichero_Entrenamiento = param.getCarpetaDatos()+ nombre_fichero_Entrenamiento;
		Nombre_fichero_test = param.getCarpetaDatos() + Nombre_fichero_test;
		long Hora_Inicio = System.currentTimeMillis();
		ConjuntoEntrenamiento ejemplos_train = InOut.cargarConjuntoEntrenamiento(nombre_fichero_Entrenamiento,
						true);
		
		if (_aplicarDuplicarClaseMinoritaria){
			param.depuracion("Prepocesamiento de datos de entrenamiento. Duplicar ejemplos de la clase minoritaria", 1);
			PreprocesamientoDatos Preprocesador=new PreprocesamientoDatos();
			ejemplos_train=Preprocesador.DuplicaClaseMinoritaria(ejemplos_train);
			etiqueta="DUPLICA_MIN_"+etiqueta;
		}
		
		
		
		 
		 
	
	
		
		
		
		
		
		
//		 Lanza el Supervisor
		Supervisor supervisor = new Supervisor(ejemplos_train);
		supervisor.start();


		
		SolucionFinal solucionSupervisor=SolucionFinal.getInstancia_SolucionFinal();
		Solucion conjunto_reglas =solucionSupervisor.Get_SolucionFinal();
		solucionSupervisor.Reiniciar_SolucionFinal();
		try{
			supervisor.join();
			supervisor.stop();
			
		}catch (Exception e){
			System.out.println("Error deteniendo al supervisor");
		}
		

		long Hora_Fin = System.currentTimeMillis();
		double minutos_empleados = (Hora_Fin - Hora_Inicio) / (Double.parseDouble("60000"));
	

				
		ConjuntoEntrenamiento ejemplos_tst = InOut.cargarConjuntoEntrenamiento(Nombre_fichero_test, false);
		int[][] ResultadoClasificaciontst = conjunto_reglas.clasificar(ejemplos_tst);
		int[][] ResultadoClasificaciontra = conjunto_reglas.clasificar(ejemplos_train);

		String _carpeta_resultados = ParametrosGlobales.getInstancia_Parametros().getCarpetaResultados();

		InOut.generarFicheroResultado(_carpeta_resultados + "result.tst",
				ResultadoClasificaciontst);
		InOut.generarFicheroResultado(_carpeta_resultados + "result.tra",
				ResultadoClasificaciontra);
		InOut.generarFicheroReglas(_carpeta_resultados + "reglas.dat",
				conjunto_reglas);

		System.out.println();
		System.out.println();

	
		
		int _NumClases = param.getNumeroClases();
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

		java.util.Calendar fecha = java.util.Calendar.getInstance();
		String nombreFichero_resultado = "resultado ";

		String FechaHora = fecha.get(java.util.Calendar.DATE) + "-"
				+ fecha.get(java.util.Calendar.MONTH) + "-"
				+ fecha.get(java.util.Calendar.YEAR) + "  "
				+ fecha.get(java.util.Calendar.HOUR) + "-"
				+ fecha.get(java.util.Calendar.MINUTE);
		nombreFichero_resultado += " " + FechaHora + ".dat";

		String Texto_Resultado = getCabecera();

		Texto_Resultado += "   --- RESULTADOS OBTENIDOS el día: " + FechaHora
				+ " ---\n\n";

		Texto_Resultado += "Número de Nodos: " + param.getNumeroNodosG()+ "\n";
		Texto_Resultado += "Tamaño Población de cada Nodo (M/N): "
				+ (ejemplos_train.getTamaño() / param
						.getNumeroNodosG()) + "\n";
		Texto_Resultado += "Semilla: " + param.getSemilla() + "\n";
		Texto_Resultado += "Máximo número de MacroCliclos: "
				+ param.getNumeroMacroCiclos() + "\n";
		Texto_Resultado += "Fichero datos test: " + Nombre_fichero_test + "\n";
		Texto_Resultado += "Fichero datos entrenamiento: "
				+ nombre_fichero_Entrenamiento + "\n";

		Texto_Resultado += "Valor g: " + param.getG() + "\n";
	

		Texto_Resultado += "Tiempo empleado: " + minutos_empleados
				+ " minutos\n";
		Texto_Resultado += "\n\n";
		Texto_Resultado += "\n\n";

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
		for (int aux = 0; aux < param.getNumeroClases(); aux++)
			Texto_Resultado += "      " + param.getValoresClase().get(aux);
		Texto_Resultado += "\n";
		for (int aux = 0; aux < param.getNumeroClases(); aux++) {
			Texto_Resultado += param.getValoresClase().get(aux) + "    ";
			for (int aux2 = 0; aux2 < param.getNumeroClases(); aux2++) {
				Texto_Resultado += matriz_Confusion[aux][aux2] + "     ";
			}
			Texto_Resultado += "\n";
		}
		Texto_Resultado += "*******************************************************\n";
		Texto_Resultado += "\n\n";

		
		int TPtst=-1;
		int TNtst=-1;
		int FNtst=-1;
		int FPtst=-1;
		if (param.getNumeroClases()==2){
			TPtst=matriz_Confusion[0][0];
			TNtst=matriz_Confusion[1][1];
			FPtst=matriz_Confusion[1][0];
			FNtst=matriz_Confusion[0][1];
		}
		
		for (int aux = 0; aux < param.getNumeroClases(); aux++) {
			for (int aux2 = 0; aux2 < param.getNumeroClases(); aux2++) {
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
		for (int aux = 0; aux < param.getNumeroClases(); aux++)
			Texto_Resultado += "      " + param.getValoresClase().get(aux);
		Texto_Resultado += "\n";
		for (int aux = 0; aux < param.getNumeroClases(); aux++) {
			Texto_Resultado += param.getValoresClase().get(aux) + "    ";
			for (int aux2 = 0; aux2 < param.getNumeroClases(); aux2++) {
				Texto_Resultado += matriz_Confusion[aux][aux2] + "    ";
			}
			Texto_Resultado += "\n";
		}
		Texto_Resultado += "*******************************************************\n";
		System.out.println(Texto_Resultado);


		int TPtra=-1;
		int TNtra=-1;
		int FNtra=-1;
		int FPtra=-1;
		if (param.getNumeroClases()==2){
			TPtra=matriz_Confusion[0][0];
			TNtra=matriz_Confusion[1][1];
			FPtra=matriz_Confusion[1][0];
			FNtra=matriz_Confusion[0][1];
		}
		
		FileOutputStream f = null;
		try {
			f = new FileOutputStream(param.getCarpetaResultados()
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
		etiqueta += "_N"+ param.getNumeroNodosG()+"_P"+ param.getM()+ "_S" + param.getSemilla();
		String Texto_Clases = "";
       int totalClase =0, aciertosClase=0;
		for (int aux = 0; aux < param.getNumeroClases(); aux++) {
			for (int aux2 = 0; aux2 < param.getNumeroClases(); aux2++) {
				totalClase += matriz_Confusion[aux][aux2]; 
				if (aux==aux2)  aciertosClase = matriz_Confusion[aux][aux2] ; 
				
			}
			Texto_Clases +=  "," + aciertosClase/(double)totalClase;
			totalClase =0;
			aciertosClase=0;
			
		}
		
		CostesComputacionales costes=CostesComputacionales.getInstancia();
		
		int _Num_Evaluaciones=costes.getNumeroEvaluaciones();
		int _Num_EvaluacionesDesglosado=costes.getNumeroEvaluaciones() ; // ojo pendiente calculo MARR
		int _Num_Comunicaciones=costes.getNumeroComunicaciones();
		etiqueta = "NowGnet_"+ etiqueta ;
		
		String Texto_Resumen =  etiqueta +"," +  param.getNumeroNodosG() +","  + param.getM()+ "," 
		+ param.getSemilla() + "," +  "," + param.getNumeroMacroCiclos()+  ","  + 
		minutos_empleados + "," + ejemplos_train.getTamaño() + "," + _Num_Evaluaciones +"," +  _Num_EvaluacionesDesglosado +
		","+ _Num_Comunicaciones +  "," + conjunto_reglas.getTamaño() + ", "+ (numAciertosTst / (double) ResultadoClasificaciontst.length)+ 
		"," + (numAciertosTra / (double) ResultadoClasificaciontra.length)+ Texto_Clases+"," +TPtst+"," +TNtst+"," +FPtst+"," +FNtst+"," +TPtra+"," +TNtra+"," +FPtra+"," +FNtra+ "\n";
		System.out.println("Etiqueta	 Nodos	 Poblacion	 Representados	 Topologia	 Objetivo	 EpocasMejora	 Tiempo	T Grid	 Ejemplos	 Evaluaciones	 Evaluaciones*d	Comunicaciones	Reglas	%test	 %Entrenamiento TPtst TNtst FPtst FNtst TPtst TNtra FPtra FNtra");
		System.out.println(Texto_Resumen);
		
      String nombreFichero_Resumen = etiqueta + "resumen ";

		
		nombreFichero_Resumen+= " " + FechaHora + ".dat";
		f = null;
		try {
			f = new FileOutputStream(param.getCarpetaResultados()
					+ nombreFichero_Resumen);
			f.write(Texto_Resumen.getBytes());
			f.close();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.getMessage());
		}


	}


	
	
}
