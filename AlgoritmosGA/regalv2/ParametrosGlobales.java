package regalv2;
import java.util.ArrayList;

import aleatorios.Aleatorio;


/**
 * ParametrosGlobales contiene los parámetros de configuración globales de Regal y los métodos necesarios para consultar o modificar sus valores. 
 * @author José Luis Toscano Muñoz
 * @version Regal v2.0
 */
public class ParametrosGlobales {
	
	/**
	 * Una instancia de ParametrosGlobales.
	 */
	private static ParametrosGlobales instancia=new  ParametrosGlobales();
	
	/**
	 * En esta tabla se define la estructuta de los ejemplos y las reglas
	 * en la posicion [i-esima][0] se almacena el número de posibles valores del atributo.
	 *  en la posicion [i-esima][1] indice en el cromosoma
	 */
	private int plantillaAtributos[][];

	 /**
	  * Tabla con el nombre de los atributos.
	  */
	 private String nombresAtributos[];
	 
	 /**
	  * Tabla de ArrayList con los valores de los atributos.
	  */
	 private ArrayList valoresAtributos[];
	 
	 /**
	  * Nombre de la clase.
	  */
	 private String nombreClase;
	 
	 /**
	  * Posibles valores que puede tomar la clase.
	  */
	 private ArrayList valoresClase;
	 
	 /**
	  * Número de clases.
	  */
	 private int numeroClases;
	 
	 /**
	  * Booleano que inndica si se continúa la búsqueda o se detiene.
	  */
	 private boolean continuarBusqueda=true;

	 /**
	  * Este objeto genera valores aleatorios.
	  */
	 private Aleatorio generadorAleatorio=new Aleatorio();
	 
	 /**
	  * Semilla para la inicialización del generador aleatorio.
	  */
	 private long semilla=1232457;

	 /**
	  * Factor de adptación.
	  */
	 private double g=0.9;
	 
	 /**
	  * Tamaño de la población de individuos de los nodos
	  */
	 private int M=50;
	 
	 /**
	  * Número de nodos.
	  */
	 private int numeroNodosRegal=4;
	 
	 /**
	  * Probabilidad de cruce.
	  */
	 private double probabilidadCruce=0.6;
	 
	 /**
	  * Probabilidad de mutar un bit del cromosoma.
	  */
	 private double probabilidadMutacion=0.0001;
	 
	 /**
	  * Número de tipos de cruces.
	  */
	 private int numeroCruces=4;
	 
	 /**
	  * Parámetros utilizados para la función de fitness
	  */ 
	 private double a=0.7;
	 private double b=0.4;
	 private double A=0.1;
	 
	 // Parámetros utilizados para el modelo distribuido
	 
	 /**
	  * Ratio de migración, indica el porcentaje de individuos de la población de un nodo que van
	  * a migrar a otros nodos.
	  */
	 private double ratioMigracionNu=0.2;
	 
	 /**
	  * Ratio de adaptación foráneo, indica el porcentaje de individuos recibidos de la red que van
	  * a ser seleccionados directamente para la reproducción. 
	  */
	 private double ratioAdaptacionForaneoP=0.1;
	 
	 /**
	  * Número de generaciones por nodo.
	  */
	 private int numeroGeneracionesPorNodo=20;
	 
	 /**
	  * Número máximo de épocas sin mejora.
	  * Se utiliza como criterio de parada si no se hace uso del mecanismo de enfriamiento.
	  */
	 private int numeroMaximoEpocasSinMejora=5;
	 
	 /**
	  * Número de épocas entre cada enfriamiento.
	  */
	 private int numeroEpocasEntreEnfriamiento=3;
	 
	 /**
	  *  Número máximo de épocas que unas reglas están asignadas a un nodo.
	  */
	 private int numeroMaximoEpocasReglasAsignadas=2;
	 
	 /**
	  * Indica si se hace uso del mecanismo de enfriamiento.
	  */
	 private boolean mecanismoEnfriamiento=true;

	/**
	 * ArrayList con los nombres de los ficheros de entrada.
	 */
	 public ArrayList listaFicherosEntrada=new ArrayList();
	 
	/**
	 * ArrayList con los nombres de los ficheros de salida.
	 */
	 public ArrayList listaFicherosSalida=new ArrayList();
	 
	/**
	* Rutas de las carpetas con los datos y los resultados
	*/ 
	 private String carpetaDatos="datos/";
	 private String carpetaResultados="resultados/";
	 private String carpetaDebugNodos="debugNodos/";
	 
	 /**
	  *  Nivel de depuración de la ejecución.
	  *  Niveles de depuración 0 -> Solo resultados  1-> solucion actual y evolucion 2 ->
	  */
	 private int nivelDepuracion=0;
	 
	 /**
	  * Indica si se genera los ficheros de depuración.
	  */
	 private boolean generarFicheroDebug=false;
	
	 
	 /**
	  * Indica la versión del proyecto.
	  */
	 private String Version=" Regal v2.0";
	 
	/**
	 * Constructor privado de la clase.
	 *
	 */
	private ParametrosGlobales(){};
	
	public void depuracion(String cadena, int nivel ){
		if (nivel <= this.nivelDepuracion) 
			System.out.println(cadena);
	}
	
	public static ParametrosGlobales getInstancia_Parametros(){
		return instancia;
	}
	
		
	public BufferNetReglas getNET(){
		return BufferNetReglas.getInstanciaBufferNet();		
	}
	
	public void setPlantillaAtributos(int _plantillaAtributos[][]){
		plantillaAtributos=_plantillaAtributos;
	}
	public int[][] getPlantillaAtributos(){
		return plantillaAtributos;
	}

	public void setNombresAtributos(String _NombresAtributos[]){
		nombresAtributos=_NombresAtributos;
	}
	public String[] getNombresAtributos(){
		return nombresAtributos;
	}

	public void setValoresAtributos(ArrayList _ValoresAtributos[]){
		valoresAtributos=_ValoresAtributos;
	}
	public ArrayList[] getValoresAtributos(){
		return valoresAtributos;
	}
	
	
	public void setNombreClase(String _Nombre_Clase){
		nombreClase=_Nombre_Clase;
	}
	public String getNombreClase(){
		return nombreClase;
	}
	
	
	public void setValoresClase(ArrayList _Valores_Clase){
		valoresClase=_Valores_Clase;
	}
	public ArrayList getValoresClase(){
		return valoresClase;
	}	
	
	
	public void setNumeroClases(int _numeroClases){
		numeroClases=_numeroClases;
	}
	public int getNumeroClases(){
		return numeroClases;
	}	
		
	 
	public void setSemilla(long _Semilla){
		semilla=_Semilla;
	}
	public long getSemilla(){
		return semilla;
	}	
	
	public void setGeneradorAleatorio(Aleatorio _GeneradorAleatorio){
		generadorAleatorio=_GeneradorAleatorio;
	}
	public Aleatorio getGeneradorAleatorio(){
		return generadorAleatorio;
	}	
	 
	 
	public void setContinuarBusqueda(boolean _Continuar_Busqueda){
		continuarBusqueda=_Continuar_Busqueda;
	}
	public boolean getContinuarBusqueda(){
		return continuarBusqueda;
	}	
	
	public void setEnviarReglasBusqueda(boolean _EnviarReglas_Busqueda){
		continuarBusqueda=_EnviarReglas_Busqueda;
	}
	
	public void setG(double _g){
		g=_g;
	}
	public double getG(){
		return g;
	}	
	
	public void setM(int _M){
		M=_M;
	}
	public int getM(){
		return M;
	}	
	
	public void setNumeroNodosRegal(int _Numero_Nodos_Regal){
		numeroNodosRegal=_Numero_Nodos_Regal;
	}
	public int getNumeroNodosRegal(){
		return numeroNodosRegal;
	}	
	
	public void setProbabilidadCruce(double _Pcruce){
		probabilidadCruce=_Pcruce;
	}
	public double getProbabilidadCruce(){
		return probabilidadCruce;
	}	
	
	public void setProbabilidadMutacion(double _Pmutacion){
		probabilidadMutacion=_Pmutacion;
	}
	public double getProbabilidadMutacion(){
		return probabilidadMutacion;
	}	
	
	public void setNumeroCruces(int _NumeroCruces){
		numeroCruces=_NumeroCruces;
	}
	public int getNumeroCruces(){
		return numeroCruces;
	}	

	public void set_a(double _a){
		a=_a;
	}
	public double get_a(){
		return a;
	}	
	
	public void set_b(double _b){
		b=_b;
	}
	public double get_b(){
		return b;
	}	
	
	public void set_A(double _A){
		A=_A;
	}
	public double get_A(){
		return A;
	}
	
	public void setRatioMigracionNu(double _ratio_migracion_nu){
		ratioMigracionNu=_ratio_migracion_nu;
	}
	public double getRatioMigracionNu(){
		return ratioMigracionNu;
	}
	
	public void setRatioAdaptacionForaneoP(double _ratio_adaptacion_foraneo_P){
		ratioAdaptacionForaneoP=_ratio_adaptacion_foraneo_P;
	}
	public double getRatioAdaptacionForaneoP(){
		return ratioAdaptacionForaneoP;
	}
	
	public void setNumeroMaximoEpocasSinMejora(int _Numero_Maximo_Epocas_Sin_Mejora){
		numeroMaximoEpocasSinMejora=_Numero_Maximo_Epocas_Sin_Mejora;
	}
	public int getNumeroMaximoEpocasSinMejora(){
		return numeroMaximoEpocasSinMejora;
	}	
	
	
	public void setNumeroEpocasEntreEnfriamiento(int _NumeroEpocasEntreEnfriamiento){
		numeroEpocasEntreEnfriamiento=_NumeroEpocasEntreEnfriamiento;
	}
	public int getNumeroEpocasEntreEnfriamiento(){
		return numeroEpocasEntreEnfriamiento;
	}
	
	
	public void setNumeroGeneracionesPorNodo(int _NumeroGeneracionesPorNodo){
		numeroGeneracionesPorNodo=_NumeroGeneracionesPorNodo;
	}
	public int getNumeroGeneracionesPorNodo(){
		return numeroGeneracionesPorNodo;
	}	
	
	public void setNumeroMaximoEpocasReglasAsignadas(int _NumMaxEpocasNodo){
		numeroMaximoEpocasReglasAsignadas=_NumMaxEpocasNodo;
	}
	public int getNumeroMaximoEpocasReglasAsignadas(){
		return numeroMaximoEpocasReglasAsignadas;
	}		
	

	public void setMecanismoEnfriamiento(boolean _mecanismo_enfriamiento){
		mecanismoEnfriamiento=_mecanismo_enfriamiento;
	}
	public boolean getMecanismoEnfriamiento(){
		return mecanismoEnfriamiento;
	}
	
	public void setCarpetaDatos(String _carpeta_datos){
		carpetaDatos=_carpeta_datos;
	}
	public String getCarpetaDatos(){
		return carpetaDatos;
	}
	
	public void setCarpetaResultados(String _carpeta_resultados){
		carpetaResultados=_carpeta_resultados;
	}
	public String getCarpetaResultados(){
		return carpetaResultados;
	}
	
	public void setCarpetaDebugNodos(String _carpeta_debugNodos){
		carpetaDebugNodos=_carpeta_debugNodos;
	}
	
	public String getCarpetaDebugNodos(){
		return carpetaDebugNodos;
	}
	
	public String getVersion(){
		return Version;
	}
	
	public void setNivelDepuracion(int _Nivel_Depuracion){
		nivelDepuracion=_Nivel_Depuracion;
	}
	
	public int getNivelDepuracion(){
		return nivelDepuracion;
	}
	
	public void setGenerarFicheroDebug(boolean _generar_Fichero_Debug){
		generarFicheroDebug=_generar_Fichero_Debug;
	}
	
	public boolean getGenerarFicheroDebug(){
		return generarFicheroDebug;
	}
 
}
