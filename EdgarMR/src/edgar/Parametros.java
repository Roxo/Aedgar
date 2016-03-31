package edgar;

import java.util.ArrayList;

import Aleatorios.Aleatorio;


/**
 * @author Miguel
 *
 */
public class Parametros {
	
	private static Parametros instancia=new  Parametros();
	
	// Número de triángulos de cada atributo para la versión Fuzzy del algoritmo
	private int num_particiones=7;

	private boolean fuzzy = true;//Comportamiento de las Etiquetas, poner en un parametro externo para el estudio.

	
	//Niveles de depuración 1 -> Solo resultados  2-> solucion actual y evolucion 3 ->bajo nivel 4 -> verbose
	 private int nivelDepuracion=0;
	 private boolean generarFicheroDebug=false;	
	 private int NodosActivos=0;
	 private long Semilla=1232457;
 
	 private boolean optimizaParticiones = true;
	 
	 private double coberturaFuzzy = 0.3;
	 
	 public boolean getOptimizaParticiones()
	 {
		 return optimizaParticiones;
	 }
	 // Parametros del nodo
	 private double g=0.9;
	 private int poblacion=500;  //poblacion de cada nodo
	 private int numNodos=10;
	 	 private double Pcruce=0.6;
	 private double Pmutacion=0.001; //v2 antes mutación era 0.0001
	 private int numCruces=4;
	 private boolean clasificador = false;// utiliza un nodo final con todo entrenamiento para depurar la solucion final con cruce 2p y mutacion generalización
     private boolean eliminarEntrenamiento = false;//	eliminar los datos del origen al enviarlos
     private boolean sustituirEntrenamiento = false;//	suistituir entrenamiento en nodos por los recibidos, no utilizado en EDGAR
	 private int numPocoRepresentados = 1;// numero de reglas que cubren a un ejemplo poco representado 
	 private boolean envia_mejores=true; //Envia a la red las mejores reglas
	 private int numGeneracionesSinComunicacion=20; 
	 private boolean parcial= false; //Divide features por nodos;
	 private int claseObjetivo =-1; //inicialmente todas son equivalentes. de 0 a n-1 clases. las duplica si se establece. -1 para anular
	 private int Topologia_Red =2; // 1-> Estrella || 2-> Anillo
	 private boolean balanceoClases = true; //si se pone a true duplica las clases no balanceadas en los nodos.
	 private String  subdirectorio = "";
	 private boolean formatoDatosTra = true; //Depende de si los ficheros de entrada vieen con terminación .tst y tra (false)  o tra.dat y tst.dat (true)
	 // Parámetros utilizados para el modelo distribuido
	 
	 private double ratio_migracion_nu=0.1;
	 private double ratio_adaptacion_foraneo_P=0.1; 
	 //inicialmente 0.1, indica los elementos de la red forzados a participar en la seleccion y cruce. 
	 
		 
	 private int Numero_Maximo_Epocas_Sin_Mejora=5;
	 
	 // Parámetros supervisor 
	 
	 private boolean mecanismo_enfriamiento=true; // Decide si se aplica enfriamiento
	 private boolean  repartir_entrenamiento_nodos=true;// reparte en subgrupos los datos 
	 	 
	 //otros
	  private String Version=" EDGAR v2.0";
	  
	  
	 //Variables de cambio de estado 
	 private boolean Continuar_Busqueda=true;//Avisa a los nodos del final de la busqueda
	 private boolean enfria = false;//avisa a los nodos del modo de funcionamiento
//Variables globales	 	 
	 private Aleatorio GeneradorAleatorio=new Aleatorio();
	 private Plantilla plantilla;
	 //	 Rutas de las carpetas con los datos y los resultados
	 private String carpeta_datos="datos/";
	 private String carpeta_resultados="resultados/";
	 private String carpeta_debugNodos="debugNodos/";
	 
 
	 // Parámetros cruce
	 private double a=0.7;
	 private double b=0.4;

	private int numeroDeClases = 2;

	private boolean chiMejorado = true;

	private int tipo_de_discretizador = 1;
	 public boolean isFuzzy() {
			return fuzzy;
		}
	 public synchronized void  decrementaNodosActivos(){
		 NodosActivos--;
		 if (getNodosActivos() == 0)
			Parametros.getInstancia_Parametros().depura(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<>>>>>>>>>>>>>>>>>>>>>>>>>>>>><ultimo nodo ", -1 );
		 
	 }
	 public synchronized int getNodosActivos(){
		 return NodosActivos;
		 
	 }
	 
	 public double getCoberturaFuzzy()
	 {
		 return coberturaFuzzy;
	 }
	 
	 public void set_num_poco_representados(int _num_poco_representados){
		 numPocoRepresentados=_num_poco_representados;
		}
	 public int get_num_poco_representados(){
			return numPocoRepresentados;
		} 
	
	 public int get_num_particiones()
	 {
		 return num_particiones;
	 }
	
	public void set_envia_mejores(boolean _envia_mejores){envia_mejores= _envia_mejores;}
	 public boolean get_envia_mejores(){return envia_mejores; }
	 
	 public void set_enfria(boolean _enfria){enfria = _enfria;}
	 public boolean get_enfria(){return enfria; }
	 
	 
	 

	public void set_repartir_entrenamiento_nodos(boolean _repartir_entrenamiento_nodos){
		 repartir_entrenamiento_nodos=_repartir_entrenamiento_nodos;
		}
	
	/**
	 * 
	 * indica si el entrenamiento en cada nodo es toda la población o un conjunto distriuido
	 */
	public boolean get_repartir_entrenamiento_nodos(){
			return repartir_entrenamiento_nodos;
		}
	/**
	 * 
	 * @param _eliminar_entrenamiento : boolean que indica si el entrenamiento no representado se elimina
	 */
	public void setEliminarEntrenamiento(boolean _eliminar_entrenamiento){
		eliminarEntrenamiento=	_eliminar_entrenamiento;
		}
	
	/**
	 * 
	 * indica si el entrenamiento recibido se elimina en el origen
	 */
	public boolean getEliminarEntrenamiento(){
			return eliminarEntrenamiento;
		}
	
	/**
	 * 
	 * indica si el entrenamiento recibido sustituye al existente o se añade
	 */
	public boolean getSustituirEntrenamiento(){
			return sustituirEntrenamiento ;
		}
	 
	/**
	 * 
	 * @param _sustituir_entrenamiento : boolean que indica si el entrenamiento recibido sustituye al existente o se añade
	 * indica si el entrenamiento recibido se elimina en el origen
	 */
	public void setSustituirEntrenamientoOrigen(boolean _sustituir_entrenamiento){
		sustituirEntrenamiento =_sustituir_entrenamiento;
		}

	
	 
		
	/**
	 * Envia a salida standard la cadena segun el nivel marcado por setNivelDepuracion 
	 * @param cadena cadena a enviar
	 * @param nivel a enviar
	 */
	public void  depura (String cadena, int nivel ){
		if (get_Nivel_Depuracion()>nivel) System.out.println(cadena);
	}
		
	
	/**
	 * Constructor privado para evitar más de una instanci de parametros globales 
	 */
	private Parametros(){};
	
	
	public static Parametros getInstancia_Parametros(){
		return instancia;
	}
	
	
	
	public NET get_NET(){
		if(Topologia_Red==1){
			return Net_Estrella.getInstancia_NET();
		}
		else{
			return 	Net_Anillo.getInstancia_NET();
		}
		
	}
	
	 public int get_Topologia(){
		return Topologia_Red;
		
	}
	 public void set_Topologia(int _Topologia_Red){
		 Topologia_Red=_Topologia_Red;
		}
	
	// Ahora implemento los métodos Set y get de cada variable
	
	
	
			
	 
	public void set_Semilla(long _Semilla){
		Semilla=_Semilla;
	}
	public long get_Semilla(){
		return Semilla;
	}	
	
	public void set_GeneradorAleatorio(Aleatorio _GeneradorAleatorio){
		GeneradorAleatorio=_GeneradorAleatorio;
	}
	public Aleatorio get_GeneradorAleatorio(){
		return GeneradorAleatorio;
	}	
	 
	 
	public synchronized void set_Continuar_Busqueda(boolean _Continuar_Busqueda){
		Continuar_Busqueda=_Continuar_Busqueda;
	}
	public boolean get_Continuar_Busqueda(){
				return Continuar_Busqueda && this.getNodosActivos()> 0;
	}	
	
	


	
	public void set_g(double _g){
		g=_g;
	}
	public double get_g(){
		return g;
	}	
	
	public void set_M(int _M){
		poblacion=_M;
	}
	public int getPoblacion(){
		return poblacion;
	}	
	
	
	public void set_Numero_Nodos(int _Numero_Nodos){
		numNodos=_Numero_Nodos;
		NodosActivos = numNodos;
	}
	public int get_Numero_Nodos(){
		return numNodos;
	}	
	
	
	public void set_Pcruce(double _Pcruce){
		Pcruce=_Pcruce;
	}
	public double get_Pcruce(){
		return Pcruce;
	}	
	
	
	public void set_Pmutacion(double _Pmutacion){
		Pmutacion=_Pmutacion;
	}
	public double get_Pmutacion(){
		return Pmutacion;
	}	
	
	
	public void set_NumeroCruces(int _NumeroCruces){
		numCruces=_NumeroCruces;
	}
	public int get_NumeroCruces(){
		return numCruces;
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
	
	
	
	
	
	public void set_ratio_migracion_nu(double _ratio_migracion_nu){
		ratio_migracion_nu=_ratio_migracion_nu;
	}
	public double get_ratio_migracion_nu(){
		return ratio_migracion_nu;
	}
	
	
	public void set_ratio_adaptacion_foraneo_P(double _ratio_adaptacion_foraneo_P){
		ratio_adaptacion_foraneo_P=_ratio_adaptacion_foraneo_P;
	}
	public double get_ratio_adaptacion_foraneo_P(){
		return ratio_adaptacion_foraneo_P;
	}
	
	
	public void set_Numero_Maximo_Epocas_Sin_Mejora(int _Numero_Maximo_Epocas_Sin_Mejora){
		Numero_Maximo_Epocas_Sin_Mejora=_Numero_Maximo_Epocas_Sin_Mejora;
	}
	public int get_Numero_Maximo_Epocas_Sin_Mejora(){
		return Numero_Maximo_Epocas_Sin_Mejora;
	}	


	

	


	public void set_mecanismo_enfriamiento(boolean _mecanismo_enfriamiento){
		mecanismo_enfriamiento=_mecanismo_enfriamiento;
	}
	public boolean get_mecanismo_enfriamiento(){
		return mecanismo_enfriamiento;
	}
	
	public void set_carpeta_datos(String _carpeta_datos){
		carpeta_datos=_carpeta_datos;
	}
	public String get_carpeta_datos(){
		return carpeta_datos;
	}
	
	public void set_carpeta_resultados(String _carpeta_resultados){
		carpeta_resultados=_carpeta_resultados;
	}
	public String get_carpeta_resultados(){
		return carpeta_resultados;
	}
	
	public void set_carpeta_debugNodos(String _carpeta_debugNodos){
		carpeta_debugNodos=_carpeta_debugNodos;
	}
	
	public String get_carpeta_debugNodos(){
		return carpeta_debugNodos;
	}
	
	
	public String get_Version(){
		return Version;
	}
	

	
	public void set_numGeneracionesSinComunicacion(int _NumGeneracionesSinComunicacion){
		numGeneracionesSinComunicacion=_NumGeneracionesSinComunicacion;
	}
	
	
	public int get_numGeneracionesSinComunicacion(){
		return numGeneracionesSinComunicacion;
	}

	public void set_Nivel_Depuracion(int _Nivel_Depuracion){
		nivelDepuracion=_Nivel_Depuracion;
	}
	public int get_Nivel_Depuracion(){
		return nivelDepuracion;
	}
	
	public void set_generar_Fichero_Debug(boolean _generar_Fichero_Debug){
		generarFicheroDebug=_generar_Fichero_Debug;
	}
	public boolean get_generar_Fichero_Debug(){
		return generarFicheroDebug;
	}
	public Plantilla getPlantilla() {
		return plantilla;
	}
	public void setPlantilla(Plantilla plantilla) {
		this.plantilla = plantilla;
	}
	public boolean isParcial() {
		return parcial;
	}
	public void setParcial(boolean parcial) {
		this.parcial = parcial;
	}
	
	/**
	 * Si se establece una clase como preferente, esta se copia a todos los nodos
	 * para que los ejemplos negativos se descubran en todos los nodos. 
	 * Si la clase es no balanceada no tiene efecto, ya que el pi es relativo al balanceo de la clase
	 * @return la clase preferente para el clasificador
	 */
	public int getClaseObjetivo() {
		return claseObjetivo;
	}
	public void setClaseObjetivo(int claseObjetivo) {
		this.claseObjetivo = claseObjetivo;
	}
	public boolean getClasificador() {
		return clasificador;
	}
	public void setClasificador(boolean clasificador) {
		this.clasificador = clasificador;
	}
	public boolean isBalanceoClases() {
		return balanceoClases;
	}
	public void setBalanceoClases(boolean balanceoClases) {
		this.balanceoClases = balanceoClases;
	}
	public String getSubdirectorio() {
		return subdirectorio;
	}
	public void setSubdirectorio(String subdirectorio) {
		this.subdirectorio = subdirectorio;
	}
	public boolean isFormatoDatosTra() {
		// TODO Auto-generated method stub
		return formatoDatosTra;
	}
	public int getNumeroDeClases() {
		
		return numeroDeClases ;
	}
	public boolean getChiMejorado() {
		// TODO Auto-generated method stub
		return chiMejorado ;
	}
	public void set_tipo_de_discretizador(String tipo) {
		tipo_de_discretizador  = Integer.parseInt(tipo);
	}
	
	public int getTipoDeDiscretizador()
	{
		return tipo_de_discretizador;
	}
 
	
	

}
