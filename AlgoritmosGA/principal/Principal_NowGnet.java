package principal;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

//import regal.*;

import NowGNET.*;

/**
 * Esta es la clase principal.
 * @author José Luis Toscano Muñoz
 * @version Regal v2.0
 */


public class Principal_NowGnet {


		
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
			//if (args.length>0)
				//runKeel(args[0]);
			//else
				runConsola();
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
			System.out.println("     -> 7)  'Datos Train: LETTER-2-1 train || Datos Test: LETTER-2-1 test'  >>> ");
			System.out.println("     -> 8)  'Datos Train: LETTER-2-2 train || Datos Test: LETTER-2-2 test'  >>> ");
				
			
			int opcion=-1;
			System.out.print(" Introduzca el número del prueba: ");
			while ((opcion<1) || (opcion>8)){
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
			}else if (opcion==7){
				nombre_Fichero_train="LETTER-2-1tra.dat";
				nombre_Fichero_tst="LETTER-2-1tst.dat";
			}else if (opcion==8){
				nombre_Fichero_train="LETTER-2-2tra.dat";
				nombre_Fichero_tst="LETTER-2-2tst.dat";
			}
			
			
			
			
			
			System.out.println("");
			System.out.println("");

			
			System.out.println("<<<  REGAL Seleccione la prueba a realizar >>> \n");
			System.out.println("     ->1)  ' Ejecutar Prueba REGAL'  >>> ");
			System.out.println("     ->2)  ' Ejecutar Baterias de Pruebas REGAL'  >>> ");		
			System.out.println("     ->3)  ' Ejecutar Prueba Now-GNET'  >>> ");

			
			System.out.println("     ______________________");	
			System.out.println("     -> 0)   Salir  ");

			opcion=-1;
			System.out.print(" Introduzca el número del prueba: ");
			while ((opcion!=0) && (opcion!=1) && (opcion!=2) && (opcion!=3)){
				System.out.print(" Introduzca el número del Algoritmo: ");
				opcion=Integer.parseInt(readWord());
			}
			
			if (opcion==1){
				//ejecutarExperimento(nombre_Fichero_train, nombre_Fichero_tst);
			}else if (opcion==2){
				//ejecutarBateriaExperimentos(nombre_Fichero_train,nombre_Fichero_tst);
			}else if (opcion==3){
				ejecutarExperimento_Gnet(nombre_Fichero_train,nombre_Fichero_tst);
			}
		}
		
		

	
		
		
		/**
		 * Ejecuta un experimento.
		 * @param nombreFicheroEntrenamiento es el fichero con los datos de entrenamiento.
		 * @param nombreFicheroTest es el fichero con los datos de test.
		 * @throws IOException 
		 */
		public static void ejecutarExperimento_Gnet(String nombreFicheroEntrenamiento, String nombreFicheroTest) {

			ParametrosGlobales param = ParametrosGlobales.getInstancia_Parametros();
			EntradaSalida InOut = new EntradaSalida();
			
			nombreFicheroEntrenamiento = param.getCarpetaDatos()
					+ nombreFicheroEntrenamiento;
			nombreFicheroTest = param.getCarpetaDatos() + nombreFicheroTest;
			long Hora_Inicio = System.currentTimeMillis();
			ConjuntoEntrenamiento ejemplos_train = InOut
					.cargarConjuntoEntrenamiento(nombreFicheroEntrenamiento,
							true);
			
			PreprocesamientoDatos Preprocesador=new PreprocesamientoDatos();
			ejemplos_train=Preprocesador.DuplicaClaseMinoritaria(ejemplos_train);
			
			
			
			
			
			
//			 Lanza el Supervisor
			Supervisor superv = new Supervisor(ejemplos_train);
			superv.start();


			 // Lanza los Hilos, con susconjuntos de entrenamientos iniciales
			 
				// Crea los distintos Nodos
		/*	NodoG NodosG[]=new NodoG[param.getNumeroNodosG()];
			NodoE NodosE[]=new NodoE[param.getNumeroNodosE()];
					 
			 
			
			for(int i=0;i<param.getNumeroNodosE();i++){
				NodosE[i]=new NodoE(ejemplos_train, i);
				NodosE[i].start();
			}
			
			
			for(int i=0;i<param.getNumeroNodosG();i++){
				int IndiceEjemplo =param.getGeneradorAleatorio().randInt(0, ejemplos_train.getTamaño()-1);
				NodosG[i]=new NodoG(ejemplos_train.getEjemplo(IndiceEjemplo), i);
				NodosG[i].start();
			}
			
			*/
			
			SolucionFinal solucionSupervisor=SolucionFinal.getInstancia_SolucionFinal();
			Solucion conjunto_reglas =solucionSupervisor.Get_SolucionFinal();
			solucionSupervisor.Reiniciar_SolucionFinal();
			try{
				superv.join();
				superv.stop();
				
			}catch (Exception e){
				System.out.println("Error deteniendo al supervisor");
			}
			

			long Hora_Fin = System.currentTimeMillis();
			double minutos_empleados = (Hora_Fin - Hora_Inicio) / (Double.parseDouble("60000"));
		

			
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

			Texto_Resultado += "Número de G Nodos: " + param.getNumeroNodosG()
					+ "\n";
			/*
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
					*/
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
		
		
		
		
		
	}
