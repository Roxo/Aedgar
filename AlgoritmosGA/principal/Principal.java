package principal;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import C45.C45;

import regalv2.*;



/**
 * Esta es la clase principal.
 * @author José Luis Toscano Muñoz
 * @version Regal v2.0
 */
public class Principal{

	
	/**
	 * Devuelve un string con una cadena leída desde teclado.
	 * @return un string con la cadena leída desde teclado.
	 */
	private static String readWord() {
		try {
			StringBuffer buffer = new StringBuffer();
			char symbol = (char) System.in.read();
			while(symbol != '\n' && symbol != '!' && symbol != '\r') {
				buffer.append(symbol);
				symbol = (char) System.in.read();
			}
			if(symbol == '!') return null;
			if(buffer.length() == 0) return readWord();
			return buffer.toString();
		} catch(IOException ex) {
			return null;
		}
	}
	
	/**
	 * Devuelve una cabecera con el título del proyecto.
	 * @return un string con la cabecera.
	 */
	public static String get_Cabecera(){
		String cabecera=" Proyecto Fin de Carrera: \n      Algoritmos distribuidos para la extracción de reglas de asociación en Minería de Datos.\n\n\n";
		cabecera+="     Alumno: José Luis Toscano Muñoz.\n";
		cabecera+="     Director del proyecto: Miguel Angel Rodriguez Román.\n";
		cabecera+="     Codirector del proyecto: Antonio Peregrin Rubio.\n";
		return cabecera;
	}
	
	
	/**
	 * Método Main, si se le pasa un fichero con los parámetros de configuración lanza el método runKeel(),
	 * en caso contrario lanza el método runConsola.
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length>0)
			runKeel(args[0]);
		else
			runConsola();
	}
	
		
	/**
	 * Ejecuta los experimentos de Keel. 
	 * @param Fichero_Config es el fichero de configuración con los parámetros del experimento. 
	 */
	public static void runKeel(String Fichero_Config){
		// Primero se cargan los datos de configuracion
		dataset.ParseadorParametros.doParse(Fichero_Config);

		EntradaSalida InOut = new EntradaSalida();
		ParametrosGlobales parametrosGlobales=ParametrosGlobales.getInstancia_Parametros();

			long Hora_Inicio = System.currentTimeMillis();
			ConjuntoEntrenamiento ejemplos_train = InOut.cargarConjuntoEntrenamiento(parametrosGlobales.listaFicherosEntrada.get(0).toString(),true);
//			 Lanza el Supervisor
			Supervisor superv = new Supervisor(ejemplos_train);
			superv.start();	
			 
//			 Lanza los Hilos, con susconjuntos de entrenamientos iniciales
			 ConjuntoEntrenamiento ejemplos_asignados_Nodos[]=new ConjuntoEntrenamiento[parametrosGlobales.getNumeroNodosRegal()];
			 ejemplos_asignados_Nodos=ejemplos_train.getNSubConjuntosEjemplos(parametrosGlobales.getNumeroNodosRegal());
			 
				// Creo los distintos Nodos
				NodoRegal NGA[]=new NodoRegal[parametrosGlobales.getNumeroNodosRegal()];
				 NGA=new NodoRegal[parametrosGlobales.getNumeroNodosRegal()];
			 
			for(int j=0;j<parametrosGlobales.getNumeroNodosRegal();j++){
				 NGA[j]=new NodoRegal(ejemplos_asignados_Nodos[j], j);
				NGA[j].start();
			}
			
			Solucion conjunto_reglas = superv.getSolucionFinal().getCopia();
			superv.reiniciarSolucionFinal();
		
	
		ConjuntoEntrenamiento ejemplos_tst =InOut.cargarConjuntoEntrenamiento(parametrosGlobales.listaFicherosEntrada.get(1).toString(),false);
		
		int[][] ResultadoClasificaciontst = conjunto_reglas
				.clasificar(ejemplos_tst);
		int[][] ResultadoClasificaciontra = conjunto_reglas
				.clasificar(ejemplos_train);
		
		InOut.generarFicheroResultado(parametrosGlobales.listaFicherosSalida.get(0).toString(),	ResultadoClasificaciontra);
		InOut.generarFicheroResultado(parametrosGlobales.listaFicherosSalida.get(1).toString(),	ResultadoClasificaciontst);

		System.out.println();
		System.out.println();

		long Hora_Fin = System.currentTimeMillis();
		double minutos_empleados = (Hora_Fin - Hora_Inicio) / (Double.parseDouble("60000"));
		System.out.println(" Tiempo empleado: " + minutos_empleados);
		
	}
	
	

	/**
	 * Genera el menú con opciones para seleccionar el dataset a cargar y el tipo de experimento.
	 */
	public static void runConsola(){
		
		System.out.println(get_Cabecera());
		
		System.out.println("");
		System.out.println("");

		System.out.println("<<<  Seleccione el DataSet  >>> \n");
		System.out.println("     -> 1)  'Datos Train: mushroom-50tra.dat || Datos Test: mushroom-50tst.dat'  >>> ");		
		System.out.println("     -> 2)  'Datos Train: mushroom_16MB.dat || Datos Test: mushroom-5x2-2tst.dat'  >>> ");
		System.out.println("     -> 3)  'Datos Train: mushroom_82MB.dat || Datos Test: mushroom-5x2-3tst.dat'  >>> ");
		System.out.println("     -> 4)  'Datos Train: mushroom-5x2-4tra.dat || Datos Test: mushroom-5x2-4tst.dat'  >>> ");
		System.out.println("     -> 5)  'Datos Train: Nursery-50 train || Datos Test: Nursery-50 test'  >>> ");
		System.out.println("     -> 6)  'Datos Train: Zoo-50 train || Datos Test: Zoo-50 test'  >>> ");
			
		
		int opcion=-1;
		System.out.print(" Introduzca el número del prueba: ");
		while ((opcion<1) || (opcion>6)){
			System.out.print(" Introduzca el número del dataset: ");
			opcion=Integer.parseInt(readWord());
		}
		String nombre_Fichero_train="";
		String nombre_Fichero_tst="";
		if (opcion==1){
			nombre_Fichero_train="Mushroom/mushroom-2-1tra.dat";
			nombre_Fichero_tst="Mushroom/mushroom-2-1tst.dat";
		}else if (opcion==2){
			nombre_Fichero_train="Mushroom/mushroomR.dat";
			//nombre_Fichero_train="Mushroom/mushroom_16MB.dat";
			nombre_Fichero_tst="Mushroom/mushroom-2-1tst.dat";
		}else if (opcion==3){
			nombre_Fichero_train="Mushroom/mushroom_82MB.dat";
			nombre_Fichero_tst="Mushroom/mushroom-2-1tst.dat";
		}else if (opcion==4){
			nombre_Fichero_train="Mushroom/mushroom-5x2-1tra.dat";
			nombre_Fichero_tst="Mushroom/mushroom-5x2-1tst.dat";
		}else if (opcion==5){
			nombre_Fichero_train="Nursery/Nursery-2-1tra.dat";
			nombre_Fichero_tst="Nursery/Nursery-2-1tst.dat";
		}else if (opcion==6){
			nombre_Fichero_train="Zoo/zoo-2-1tra.dat";
			nombre_Fichero_tst="Zoo/zoo-2-1tst.dat";
		}
		
		
		System.out.println("");
		System.out.println("");

		
		System.out.println("<<<  REGAL Seleccione la prueba a realizar >>> \n");
		System.out.println("     ->1)  ' Ejecutar Prueba REGAL'  >>> ");
		System.out.println("     ->2)  ' Ejecutar Baterias de Pruebas REGAL'  >>> ");		
		System.out.println("     ->3)  ' Ejecutar Prueba Now-GNET'  >>> ");
		System.out.println("     ->4)  ' Ejecutar Prueba C45'  >>> ");

		
		System.out.println("     ______________________");	
		System.out.println("     -> 0)   Salir  ");

		opcion=-1;
		System.out.print(" Introduzca el número del prueba: ");
		while ((opcion!=0) && (opcion!=1) && (opcion!=2) && (opcion!=3)&&(opcion!=4)){
			System.out.print(" Introduzca el número del Algoritmo: ");
			opcion=Integer.parseInt(readWord());
		}
		
		if (opcion==1){
			ejecutarExperimento(nombre_Fichero_train, nombre_Fichero_tst);
		}else if (opcion==2){
			ejecutarBateriaExperimentos(nombre_Fichero_train,nombre_Fichero_tst);
		}else if (opcion==3){
			ejecutarBateriaExperimentos(nombre_Fichero_train,nombre_Fichero_tst);
		}else if (opcion==4){
			
			try {
				C45 classifier = new C45("configuracionC45/config.txt");
			} catch (Exception e) {
				 System.err.println(e.getMessage());
		         System.exit( -1);
			}
		
		}
		
	}
	
	

	
	/**
	 * Ejecuta un experimento.
	 * @param nombreFicheroEntrenamiento es el fichero con los datos de entrenamiento.
	 * @param nombreFicheroTest es el fichero con los datos de test.
	 */
	public static void ejecutarExperimento(String nombreFicheroEntrenamiento, String nombreFicheroTest) {

		ParametrosGlobales param = ParametrosGlobales.getInstancia_Parametros();
		EntradaSalida InOut = new EntradaSalida();
		
		nombreFicheroEntrenamiento = param.getCarpetaDatos()
				+ nombreFicheroEntrenamiento;
		nombreFicheroTest = param.getCarpetaDatos() + nombreFicheroTest;
		long Hora_Inicio = System.currentTimeMillis();
		ConjuntoEntrenamiento ejemplos_train = InOut
				.cargarConjuntoEntrenamiento(nombreFicheroEntrenamiento,
						true);
		
		
//		 Lanza el Supervisor
		Supervisor superv = new Supervisor(ejemplos_train);
		superv.start();


		 // Lanza los Hilos, con susconjuntos de entrenamientos iniciales
		 ConjuntoEntrenamiento ejemplos_asignados_Nodos[]=new ConjuntoEntrenamiento[param.getNumeroNodosRegal()];
		 ejemplos_asignados_Nodos=ejemplos_train.getNSubConjuntosEjemplos(param.getNumeroNodosRegal());
			// Crea los distintos Nodos
		NodoRegal NGA[]=new NodoRegal[param.getNumeroNodosRegal()];
		NGA=new NodoRegal[param.getNumeroNodosRegal()];
		 
		 
		for(int i=0;i<param.getNumeroNodosRegal();i++){
			 NGA[i]=new NodoRegal(ejemplos_asignados_Nodos[i], i);
			NGA[i].start();
		}
		
		
		Solucion conjunto_reglas =  superv.getSolucionFinal().getCopia();
		superv.reiniciarSolucionFinal();

		ConjuntoEntrenamiento ejemplos_tst = InOut.cargarConjuntoEntrenamiento(nombreFicheroTest, false);

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

		long Hora_Fin = System.currentTimeMillis();

		double minutos_empleados = (Hora_Fin - Hora_Inicio) / (Double.parseDouble("60000"));
	
		
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

		Texto_Resultado += "Número de Nodos: " + param.getNumeroNodosRegal()
				+ "\n";
		Texto_Resultado += "Tamaño Población de cada Nodo (M/N): "
				+ (ejemplos_train.getTamaño() / param
						.getNumeroNodosRegal()) + "\n";
		Texto_Resultado += "Semilla: " + param.getSemilla() + "\n";
		Texto_Resultado += "Máximo número de epocas sin mejora: "
				+ param.getNumeroMaximoEpocasSinMejora() + "\n";
		Texto_Resultado += "Fichero datos test: " + nombreFicheroTest + "\n";
		Texto_Resultado += "Fichero datos entrenamiento: "
				+ nombreFicheroEntrenamiento + "\n";

		Texto_Resultado += "Valor g: " + param.getG() + "\n";
		Texto_Resultado += "Ratio de Migracion nu: "
				+ param.getRatioMigracionNu() + "\n";
		Texto_Resultado += "Ratio de foraneos P: "
				+ param.getRatioAdaptacionForaneoP() + "\n";
		Texto_Resultado += "Tiempo empleado: " + minutos_empleados
				+ " minutos\n";
		Texto_Resultado += "\n\n";
		Texto_Resultado += "\n\n";

		int num_acietos = ResultadoClasificaciontst.length - cont_fallos;
		Texto_Resultado += "******************  RESULTADOS  5x2 Test  **************\n";
		Texto_Resultado += "Numero de ejemplos: "
				+ ResultadoClasificaciontst.length + "\n";
		Texto_Resultado += "Numero de ejemplos bien clasificados: "
				+ (num_acietos) + " -> "
				+ (num_acietos / (double) ResultadoClasificaciontst.length)
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
		num_acietos = ResultadoClasificaciontra.length - cont_fallos;
		Texto_Resultado += "******************  RESULTADOS  Datos Entrenamiento ************\n";
		Texto_Resultado += "Numero de ejemplos: "
				+ ResultadoClasificaciontra.length + "\n";
		Texto_Resultado += "Numero de ejemplos bien clasificados: "
				+ (num_acietos) + " -> "
				+ (num_acietos / (double) ResultadoClasificaciontra.length)
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

	}
	
	
	
	
	
	
	
	
	/**
	 * Lanza una batería de experimentos.
	 * @param nombreFicheroEntrenamiento es el fichero con los datos de entrenamiento.
	 * @param nombreFicheroTest es el fichero con los datos de test.
	 */
	public static void ejecutarBateriaExperimentos(String nombreFicheroEntrenamiento, String nombreFicheroTest) {

		ParametrosGlobales param = ParametrosGlobales.getInstancia_Parametros();
		EntradaSalida InOut = new EntradaSalida();
		nombreFicheroEntrenamiento = param.getCarpetaDatos()+ nombreFicheroEntrenamiento;
		nombreFicheroTest = param.getCarpetaDatos() + nombreFicheroTest;
		
		java.util.Calendar fecha = java.util.Calendar.getInstance();
		String nombreFichero_resultado = "resultado_bateria ";
		String nombreFichero_reglas="resultado_reglas";
		String FechaHora = fecha.get(java.util.Calendar.DATE) + "-"+ (fecha.get(java.util.Calendar.MONTH)+1) + "-"
				+ fecha.get(java.util.Calendar.YEAR) + "  "+ fecha.get(java.util.Calendar.HOUR) + "-"
				+ fecha.get(java.util.Calendar.MINUTE);
		nombreFichero_resultado += " " + FechaHora + ".dat";
		nombreFichero_reglas+=" " + FechaHora + ".dat";
		
		String Texto_Resultado = "";

		Texto_Resultado += "   --- RESULTADOS OBTENIDOS el día: " + FechaHora +" Versión del Proyecto "+param.getVersion()	+ "  ---\n\n";
		
		Texto_Resultado += "  Semilla    Num_Nodos    M    g     nu    P    Segundos/Busqueda    NmaxEpocSinMejora  EpocasPorEnfriamiento   Enfriamiento  ";
		Texto_Resultado += "  Num_Datos    Minutos    Num_Evaluaciones    Num_Comunicaciones    F_Train    F_Test";
		Texto_Resultado += "  Num_Reglas    AV_PI    AV_fitness    AV_N+    AV_N-    ReglaMayor_PI    ReglaMenor_PI    %AcietosTrain    %AciertosTest  \n";
		
		try {
			FileOutputStream f = new FileOutputStream(param.getCarpetaResultados()+ nombreFichero_resultado, true);
			f.write(Texto_Resultado.getBytes());
			f.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		
		String texto_cab=" Reglas Obtenidas: "+ FechaHora +" Versión del Proyecto "+param.getVersion()	+ "  ---\n\n";;
		try {
			FileOutputStream f = new FileOutputStream(param.getCarpetaResultados()+ nombreFichero_reglas, true);
			f.write(texto_cab.getBytes());
			f.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		

		// Leo los datos de entrenamiento de los ficheros.
		ConjuntoEntrenamiento ejemplos_train = InOut.cargarConjuntoEntrenamiento(nombreFicheroEntrenamiento,true);
		ConjuntoEntrenamiento ejemplos_tst = InOut.cargarConjuntoEntrenamiento(nombreFicheroTest, false);

		DecimalFormat Formateador = new DecimalFormat("###.##");
		
		
		
		for (int sem=0;sem<1;sem++){
			if(sem==0) param.setSemilla(123456789);
			else if(sem==1) param.setSemilla(556667834);
			else param.setSemilla(28848688);
		
		for (int enfri = 0; enfri <= 1; enfri++) {
			if (enfri == 0)
				param.setMecanismoEnfriamiento(false);
			else
				param.setMecanismoEnfriamiento(true);

			for (int n_nodos = 8; n_nodos <= 8; n_nodos = n_nodos*2) {
				param.setNumeroNodosRegal(n_nodos);

				for (double nu = 0.1; nu <= 0.1; nu=nu+0.1) {
					param.setRatioMigracionNu(nu);
					long Hora_Inicio = System.currentTimeMillis();
					
					param.setContinuarBusqueda(true);
					
					 //Inicializo los buffers de comunicación de los datos de entrenamiento
					 BufferSupervisor DatosEntrenamiento=BufferSupervisor.getInstancia();
					 DatosEntrenamiento.inicializarBufferDatosEntrenamiento();
					 BufferNetReglas.reinicializar();
					 
					 //	 Se lanza el supervisor.
					Supervisor superv = new Supervisor(ejemplos_train);
					superv.start();
					 
					 // Lanza los Hilos, con susconjuntos de entrenamientos iniciales
					 ConjuntoEntrenamiento ejemplos_asignados_Nodos[]=new ConjuntoEntrenamiento[param.getNumeroNodosRegal()];
					 ejemplos_asignados_Nodos=ejemplos_train.getNSubConjuntosEjemplos(param.getNumeroNodosRegal());
					 
						// Crea los distintos Nodos
					NodoRegal NGA[]=new NodoRegal[param.getNumeroNodosRegal()];
					NGA=new NodoRegal[param.getNumeroNodosRegal()];
					 
					for(int i=0;i<param.getNumeroNodosRegal();i++){
						 NGA[i]=new NodoRegal(ejemplos_asignados_Nodos[i], i);
						NGA[i].start();
					}
					Solucion conjunto_reglas = superv.getSolucionFinal().getCopia();
					superv.reiniciarSolucionFinal();
				
					for(int j=0;j<param.getNumeroNodosRegal();j++){
						try{
							NGA[j].stop();
						}catch(Exception e){
						}	
					}
			
					
					long Hora_Fin = System.currentTimeMillis();
					double minutos_empleados = (Hora_Fin - Hora_Inicio) / (Double.parseDouble("60000"));
					
					
					// Primero los parámetros de configuración			
					Texto_Resultado = "  " + param.getSemilla() 
					+ "    "+ param.getNumeroNodosRegal()
					+ "    "+ param.getM()
					+ "    "+ param.getG()
					+ "    "+ param.getRatioMigracionNu()
					+ "    "+ param.getRatioAdaptacionForaneoP()
					+ "    "+ param.getNumeroGeneracionesPorNodo()
					+ "    "+ param.getNumeroMaximoEpocasSinMejora()
					+ "    "+ param.getNumeroEpocasEntreEnfriamiento()
					+ "    "+ param.getMecanismoEnfriamiento();
					
															
					// Costes
					CostesComputacionales costes=CostesComputacionales.getInstancia();
					
					int _Num_Evaluaciones=costes.getNumeroEvaluaciones();
					int _Num_Comunicaciones=costes.getNumeroComunicaciones();
					
					
					Texto_Resultado += "      "+ejemplos_train.getTamaño()
					+ "    "+ Formateador.format(minutos_empleados)
					+ "    "+ _Num_Evaluaciones

					+ "    "+ _Num_Comunicaciones
					+ "    "+ nombreFicheroEntrenamiento
					+ "    "+ nombreFicheroTest;
					
					
					// Resultados

					// Calculo los % de aciertos
					int[][] ResultadoClasificaciontst = conjunto_reglas.clasificar(ejemplos_tst);
					int[][] ResultadoClasificaciontra = conjunto_reglas.clasificar(ejemplos_train);					

					int cont_fallos_tst = 0;
					for (int i = 0; i < ResultadoClasificaciontst.length; i++) {
						if (ResultadoClasificaciontst[i][0] != ResultadoClasificaciontst[i][1])
							cont_fallos_tst++;
					}
					int num_acietos_tst = ResultadoClasificaciontst.length- cont_fallos_tst;
					double por_aciertos_test = (num_acietos_tst / (double) ResultadoClasificaciontst.length);

					int cont_fallos_train = 0;
					for (int i = 0; i < ResultadoClasificaciontra.length; i++) {
						if (ResultadoClasificaciontra[i][0] != ResultadoClasificaciontra[i][1])
							cont_fallos_train++;
					}
					int num_acietos_train = ResultadoClasificaciontra.length - cont_fallos_train;
					double por_aciertos_train = (num_acietos_train / (double) ResultadoClasificaciontra.length);


					double AV_PI=0;
					double AV_fitness=0;
					double AV_Npos=0;
					double AV_Nneg=0;
					
					for (int i = 0; i <conjunto_reglas.getTamaño();i++){			
						AV_PI+=conjunto_reglas.getRegla(i).getPI();
						AV_fitness+=conjunto_reglas.getRegla(i).getFitness();
						AV_Npos+=conjunto_reglas.getRegla(i).getNumeroCasosPositivos();
						AV_Nneg+=conjunto_reglas.getRegla(i).getNumeroCasosNegativos();
					}
					AV_PI=AV_PI/Double.parseDouble(conjunto_reglas.getTamaño()+"");
					AV_fitness=AV_fitness/Double.parseDouble(conjunto_reglas.getTamaño()+"");
					
					
					Texto_Resultado += "      "+conjunto_reglas.getTamaño()
					+ "    " + Formateador.format(AV_PI)
					+ "    " + Formateador.format(AV_fitness)
					+ "    " + Formateador.format(AV_Npos)
					+ "    " + Formateador.format(AV_Nneg)
					+ "    " + conjunto_reglas.getRegla(0).getPI()
					+ "    " + conjunto_reglas.getRegla(conjunto_reglas.getTamaño()-1).getPI()
					+ "    " + por_aciertos_train
					+ "    " + por_aciertos_test
					+ "   \n";
						
					try {
						FileOutputStream f = new FileOutputStream(param.getCarpetaResultados()+ nombreFichero_resultado, true);
						f.write(Texto_Resultado.getBytes());
						f.close();
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
					
					
					String Texto_reglas="\n\n  -------------------------------\n"+
					conjunto_reglas.getTextoSolucionCompleta()+"\n"+
					"-------------------------------\n";
						
					
					try {
						FileOutputStream f = new FileOutputStream(param.getCarpetaResultados()+ nombreFichero_reglas, true);
						f.write(Texto_reglas.getBytes());
						f.close();
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
					}
				}
			
		}
		}

		Texto_Resultado = "*******************************************************\n";
		System.out.println(Texto_Resultado);

		FileOutputStream f = null;
		try {
			f = new FileOutputStream(param.getCarpetaResultados()
					+ nombreFichero_resultado,true);
			f.write(Texto_Resultado.getBytes());
			f.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

	}

}
