package principal;

import java.io.FileOutputStream;
import NowGNET.*;


public class principalNowGNET {


	
	/**
	 * Esta función devuelve una cabecera con el títtulo del proyecto.
	 * @return un string con la cabecera.
	 */
	public static String get_Cabecera(){
		String cabecera=" Tesina Máster TIA ";
		cabecera+="     Alumno: José Luis Toscano Muñoz.\n";
		cabecera+="     Director del proyecto: Miguel Angel Rodriguez Román.\n";
		cabecera+="     Codirector del proyecto: Antonio Peregrin Rubio.\n";
		return cabecera;
	}
	
	

	
	public static void main(String[] args) {

		System.out.println(get_Cabecera());
	
		
		System.out.println("");
		System.out.println("");
		String p_etiqueta = "Now GNET" ;//
		String nombre_Fichero_train="";
		String nombre_Fichero_tst="";
		
		if (args.length>0){
			nombre_Fichero_train=args[0]+"tra.dat";
		    nombre_Fichero_tst= args[0]+"tst.dat";
		    p_etiqueta = args[0];
		    if (args.length>1)//Número de nodos G
		    	{ParametrosGlobales.getInstancia_Parametros().setNumeroNodosG(Integer.parseInt( args[1]));  
		    	System.out.println(" Numero de nodos G: " + args[1]);
		    	}
		    
		    if (args.length>2)//Número de nodos E
	    	{ParametrosGlobales.getInstancia_Parametros().setNumeroNodosE(Integer.parseInt( args[2]));  
	    	System.out.println(" Numero de nodos G: " + args[1]);
	    	}
		    
		    if (args.length>3)//Población
		    {ParametrosGlobales.getInstancia_Parametros().setM(Integer.parseInt( args[3]));
	    	System.out.println(" Poblacion: " + args[3]);
		    }
		     
		    
		    if (args.length>4)//Macro ciclos
		    {ParametrosGlobales.getInstancia_Parametros().setNumeroMacroCiclos(Integer.parseInt( args[4]));
	    	System.out.println(" Macrociclos: " + args[4]);
		    }
		    
		    if (args.length>5)//Micro ciclos
		    {ParametrosGlobales.getInstancia_Parametros().setNumeroMicroCiclos(Integer.parseInt( args[5]));
	    	System.out.println(" Microciclos: " + args[5]);
		    }
		    
	    	if (args.length>6)//semilla
		    {ParametrosGlobales.getInstancia_Parametros().setSemilla(Integer.parseInt( args[6]));
	    	System.out.println(" semilla: " + args[6]);
		    }

		}
		else{
		 
			System.out.print(" usage : NowGNET [nodos G 1,] [nodos E 1,] [poblacion] [Macrociclos 1,] [Microciclos 1,] [semilla] ");
			return;
		 }
				EjecutarPruebaUnix(nombre_Fichero_train, nombre_Fichero_tst,p_etiqueta);
	}
	
	
	
	
	
	public static void EjecutarPruebaUnix(String nombre_fichero_Entrenamiento,
			String Nombre_fichero_test,String etiqueta) {

		
		ParametrosGlobales param = ParametrosGlobales.getInstancia_Parametros();
		EntradaSalida InOut = new EntradaSalida();
		


		nombre_fichero_Entrenamiento = param.getCarpetaDatos()
				+ nombre_fichero_Entrenamiento;
		Nombre_fichero_test = param.getCarpetaDatos() + Nombre_fichero_test;
		long Hora_Inicio = System.currentTimeMillis();
		ConjuntoEntrenamiento ejemplos_train = InOut.cargarConjuntoEntrenamiento(nombre_fichero_Entrenamiento,
						true);
		
		Supervisor superv = new Supervisor(ejemplos_train);
		superv.start();
		 //Inicializo los buffers de comunicación de los datos de entrenamiento
		 //BufferSupervisor DatosEntrenamiento=BufferSupervisor.getInstancia();
		 //DatosEntrenamiento.inicializar_bufferDatos_entrenamiento(param.get_Numero_Nodos_Regal());
		 // Lanzo los Hilos, con susconjuntos de entrenamientos iniciales
		// ConjuntoEntrenamiento ejemplos_asignados_Nodos[]=new ConjuntoEntrenamiento[param.get_Numero_Nodos_Regal()];
		 //ejemplos_asignados_Nodos=ejemplos_train.getNSubConjuntosEjemplos(param.get_Numero_Nodos_Regal());
		 
			// Creo los distintos Nodos E
			
	/*	NodoE NE[]=new NodoE[param.getNumeroNodosE()];
		for(int i=0;i<param.getNumeroNodosE();i++){
			NE[i]=new NodoE(ejemplos_train, i);
			NE[i].start();
		}
			
		// Creo los distintos Nodos G			
		NodoG NG[]=new NodoG[param.getNumeroNodosG()];		 
		for(int i=0;i<param.getNumeroNodosG();i++){
			int indiceEjemplo=param.getGeneradorAleatorio().randInt(0, ejemplos_train.getTamaño()-1);
			Ejemplo e=ejemplos_train.getEjemplo(indiceEjemplo);
			NG[i]=new NodoG(e, i);
			NG[i].start();
		}
		
		*/
		

		
		
		
		SolucionFinal SolucionRegal=SolucionFinal.getInstancia_SolucionFinal();
		Solucion conjunto_reglas = SolucionRegal.Get_SolucionFinal().getCopia();
		SolucionRegal.Reiniciar_SolucionFinal();
		// Ahora se detienen a todos los nodos
	/*	for(int i=0;i<param.getNumeroNodosG();i++){;
			try{
			
				NG[i].stop();
			}catch(Exception e){
				System.out.println(" Error parando el nodo G" + i);
			}
		}
		
		for(int i=0;i<param.getNumeroNodosE();i++){;
			try{		
				NE[i].stop();
			}catch(Exception e){
				System.out.println(" Error parando el nodo E" + i);
			}
		}*/
		
		try{
			superv.join();
			superv.stop();
			
		}catch(Exception e){
			System.out.println(" Error eliminando el Supervisor");
		}
		
		long Hora_Fin = System.currentTimeMillis();
		double minutos_empleados = (Hora_Fin - Hora_Inicio) / (Double.parseDouble("60000"));
		
		
		//solución conjunto_reglas=supe_sec.RunSupervisor_Secuencial(ejemplos_train);

		ConjuntoEntrenamiento ejemplos_tst = InOut.
				cargarConjuntoEntrenamiento(Nombre_fichero_test, false);
		int[][] ResultadoClasificaciontst = conjunto_reglas
				.clasificar(ejemplos_tst);
		int[][] ResultadoClasificaciontra = conjunto_reglas
				.clasificar(ejemplos_train);

		String _carpeta_resultados = ParametrosGlobales
				.getInstancia_Parametros().getCarpetaResultados();

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

		String Texto_Resultado = get_Cabecera();

		Texto_Resultado += "   --- RESULTADOS OBTENIDOS el día: " + FechaHora
				+ " ---\n\n";

		Texto_Resultado += "Número de Nodos G: " + param.getNumeroNodosG()
				+ "\n";
		Texto_Resultado += "Número de Nodos E: " + param.getNumeroNodosE()
		+ "\n";
		Texto_Resultado += "Tamaño Población: "
				+  param.getM() + "\n";
		Texto_Resultado += "Semilla: " + param.getSemilla() + "\n";
		Texto_Resultado += "Número Macrociclos: "
				+ param.getNumeroMacroCiclos() + "\n";
		Texto_Resultado += "Número Microciclos: "
			+ param.getNumeroMicroCiclos() + "\n";
		Texto_Resultado += "Fichero datos test: " + Nombre_fichero_test + "\n";
		Texto_Resultado += "Fichero datos entrenamiento: "
				+ nombre_fichero_Entrenamiento + "\n";


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
etiqueta += "_NG"+ param.getNumeroNodosG()+"_NE"+ param.getNumeroNodosE()+"_P"+ param.getM()+ "_S" + param.getSemilla();
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
		etiqueta = "NOW_GNET"+ etiqueta ;
		
		String Texto_Resumen =  etiqueta +"," +  param.getNumeroNodosG() +","+param.getNumeroNodosE() +","  + param.getM()+","  + param.getNumeroMacroCiclos()+","  + param.getNumeroMicroCiclos()+ ","+ param.getSemilla() + ","
		+  "[Clase Objetivo]" + "," + minutos_empleados + "," + ejemplos_train.getTamaño() + "," + _Num_Evaluaciones +"," +  _Num_EvaluacionesDesglosado +
		","+ _Num_Comunicaciones +  "," + conjunto_reglas.getTamaño() + ","+ (numAciertosTst / (double) ResultadoClasificaciontst.length)+ 
		"," + (numAciertosTra / (double) ResultadoClasificaciontra.length)+ Texto_Clases+ "\n";
	
		System.out.println("Etiqueta	 NodosG   NodosE	 Poblacion	 MacroCiclos	MicroCiclos	 Semilla Objetivo	 Tiempo	T Grid	 Ejemplos	 Evaluaciones	 Evaluaciones*d	Comunicaciones	Reglas	%test	 %Entrenamiento");
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


