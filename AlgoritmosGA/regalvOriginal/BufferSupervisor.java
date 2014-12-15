package regalvOriginal;
/**
 * BufferSupervisor hace uso del patrón de diseño singleton con el objetivo que solo exista un buffer de comunicación entre los nodos y el supervisor en toda la aplicación.
 * Es utilizado por los nodos y por el supervisor para comunicarse información. 
 * Por una parte el supervisor envía a los nodos reglas y datos de entrenamiento a los nodos, 
 * y por otra los nodos envían al supervisor las mejores reglas que encuentra.
 * 
 * Implementa el buffer que utiliza el supervisor para comunicarse con los nodos.
 * El supervisor envía a los nodos datos de entrenamiento y reglas, 
 * y los nodos envían al supervisor las reglas que describen los datos de entrenamiento.
 * 
 * @author José Luis Toscano Muñoz
 * @version Regal v2.0
 *  
 */

public class BufferSupervisor {
	
	private static BufferSupervisor instancia=new  BufferSupervisor();
	private ConjuntoEntrenamiento bufferDatosEntrenamientos[];
	private Solucion bufferReglasAsignadas[];
	private Solucion bufferComunicacionMejoresReglas = new Solucion();
	private ParametrosGlobales parametrosGlobales =ParametrosGlobales.getInstancia_Parametros();
	private int numeroComunicaciones[];
	
	/**
	 * Contructor privado de la clase. 
	 *
	 */
	private BufferSupervisor(){
		inicializarBufferDatosEntrenamiento();
		numeroComunicaciones=new int[parametrosGlobales.getNumeroNodosRegal()];
		for(int i=0;i<numeroComunicaciones.length;i++)
			numeroComunicaciones[i]=0;
	}
	
	/**
	 * Devuelve una instancia del buffer de comunicación entre los nodos y el supervisor.
	 * @return una instancia del BufferSupervisor
	 */
	public static BufferSupervisor getInstancia(){
		return instancia;
	}
	
	/**
	 * Inicializa los valores del buffer de comunicación.
	 */
	public void inicializarBufferDatosEntrenamiento(){
		int Numero_Nodos=parametrosGlobales.getNumeroNodosRegal();
		bufferDatosEntrenamientos=new ConjuntoEntrenamiento[Numero_Nodos];
		bufferReglasAsignadas=new Solucion[Numero_Nodos];
		numeroComunicaciones=new int[Numero_Nodos];
			
		for (int i=0;i<Numero_Nodos;i++){
			bufferDatosEntrenamientos[i]=new ConjuntoEntrenamiento();
			bufferReglasAsignadas[i]=new Solucion();
			numeroComunicaciones[i]=0;
		}
	}
	
	/**
	 * Comprueba si al nodo le ha sido asignado un nuevo conjunto de datos de entrenamiento.
	 * @param Nodo Identificador del nodo que comprueba si tiene nuevos datos de entrenamiento.
	 * @return un booleano indicando si existen nuevos datos de entrenamiento para el nodo.
	 */
	public  boolean hayNuevosDatosEntrenamientos(int Nodo){
		boolean hayDatos=false;
		synchronized(bufferDatosEntrenamientos){
			if (bufferDatosEntrenamientos.length>=Nodo){
				if (bufferDatosEntrenamientos[Nodo].getTamaño()>0){
					hayDatos=true;	
				}
			}
		}
		return hayDatos;
	}
	
	
	/**
	 * Asigna el nuevo conjunto de entrenamiento a los nodos.
	 * @param nuevosDatosEntrenamientos es una tabla con los nuevos datos de entrenamiento de cada nodo.
	 * @param reglasAsignadas es una tabla con los conjuntos de reglas asignados a cada nodo.
	 */
	
	public void asignarDatosReglasNodo(ConjuntoEntrenamiento nuevosDatosEntrenamientos[], Solucion reglasAsignadas[]){
		synchronized(bufferDatosEntrenamientos){
			for (int i=0;i<nuevosDatosEntrenamientos.length;i++){
				bufferDatosEntrenamientos[i]=nuevosDatosEntrenamientos[i];
				
				if(reglasAsignadas!=null)
					bufferReglasAsignadas[i]=reglasAsignadas[i];
			}
			// Aqui el supervisor debe desperetar a todos los nodos
			bufferDatosEntrenamientos.notifyAll();
		}
	}
	
	
	/**
	 * Recupera el nuevo conjunto de datos de entrenamiento y reglas,
	 * asignados por el supervisor.
	 * 
	 * @param Nodo identificador del nodo que solicita los datos de entrenamiento.
	 * @param datosEntrenamiento en este parámetro es devuelto el nuevo conjunto de datos de entrenamiento para el nodo.  
	 * @param reglasAsignadas en este parámetro es devuelto el nuevo conjunto de reglas asignadas al nodo. 
	 * @return un booleano indicando si ha obtenido un nuevo conjunto de datos de entrenamiento.
	 */
	public boolean getDatosReglasNodo (int Nodo, ConjuntoEntrenamiento datosEntrenamiento, Solucion reglasAsignadas){
		synchronized(bufferDatosEntrenamientos){
			if(hayNuevosDatosEntrenamientos(Nodo)){
				// Primero asigno los datos de entrenamiento
				for(int i=0;i<bufferDatosEntrenamientos[Nodo].getTamaño();i++){
						Ejemplo ej=bufferDatosEntrenamientos[Nodo].getEjemplo(i);
						datosEntrenamiento.insertarEjemplo(ej);
					}
				// Cuando toma los datos de entrenaiento, se vacia el buffer
				bufferDatosEntrenamientos[Nodo]=new ConjuntoEntrenamiento();

				// Se asignan las Reglas
				for(int i=0;i<bufferReglasAsignadas[Nodo].getTamaño();i++){
					Regla rg=bufferReglasAsignadas[Nodo].getRegla(i).getCopia();
					reglasAsignadas.insertarRegla(rg);
				}
				// Vacio el Buffer
				bufferReglasAsignadas[Nodo]=new Solucion();
				return true;
			}
			return false;
		}
		
	}
	
	
	 
	
	/**
	 * Recupera las reglas enviadas por los nodos.
	 * @return un objeto Solución con el conjunto de reglas enviadas por los nodos.
	 */
	
	public  Solucion getMejoresReglas(){
		Solucion conjuntodeReglas=new Solucion();
		boolean todosNodosHanFinalizado=false;
		
		synchronized(bufferComunicacionMejoresReglas){
			while(!todosNodosHanFinalizado){
				// Aqui se debe comprobar si todos los nodos han enviado las reglas
				todosNodosHanFinalizado=true;
				int i=0;
				while((todosNodosHanFinalizado)&&(i<parametrosGlobales.getNumeroNodosRegal())){
					if(numeroComunicaciones[i]<parametrosGlobales.getNumeroGeneracionesPorNodo())
						todosNodosHanFinalizado=false;
					i++;
				}
				
				if (!todosNodosHanFinalizado){
					try {
						parametrosGlobales.depuracion("El supervisor espernado que los nodos finalicen sus generaciones...",1);
						bufferComunicacionMejoresReglas.wait();			
						} catch (InterruptedException e) {
							System.err.println("Problema leyendo reglas del buffer mejores re"+ e);
						e.printStackTrace();
					}
				}
			}
			
			parametrosGlobales.depuracion("El supervisor analiza las reglas recibidas...",1);
				
			for(int i=0;i<bufferComunicacionMejoresReglas.getTamaño();i++){
				Regla regla_devolver=null;
				regla_devolver=bufferComunicacionMejoresReglas.getRegla(i).getCopia();
				conjuntodeReglas.insertarRegla(regla_devolver);
			}
			bufferComunicacionMejoresReglas=new Solucion();
		}
		return conjuntodeReglas;
	}
	
	
	
	/**
	 * Envía las mejores reglas al buffer de mejores reglas.
	 * @param new_regla es la regla que el nodo envía al supervisor. 
	 */
	public void addMejorRegla(Regla new_regla, int Nodo){
		synchronized(bufferComunicacionMejoresReglas){
			if (bufferComunicacionMejoresReglas!=null){
				if (!bufferComunicacionMejoresReglas.existeRegla(new_regla))
					bufferComunicacionMejoresReglas.insertarRegla(new_regla.getCopia());
				}
			numeroComunicaciones[Nodo]++;
		}
		
		// Si se ha llegado al número máximo de generaciones, el nodo se detendrá a la espera de daors de entrenamientos
		if(numeroComunicaciones[Nodo]==parametrosGlobales.getNumeroGeneracionesPorNodo()){	
			// Avisa al supervisor que un nodo a terminado sus generaciones
			synchronized(bufferComunicacionMejoresReglas){
				bufferComunicacionMejoresReglas.notifyAll();
			}
			synchronized(bufferDatosEntrenamientos){
				if(!hayNuevosDatosEntrenamientos(Nodo)){
					try {
						parametrosGlobales.depuracion("Nodo("+ Nodo +") se detiene a la espera de nuevos datos de entrenamiento...",3);
						bufferDatosEntrenamientos.wait();
						numeroComunicaciones[Nodo]=0;
						parametrosGlobales.depuracion("Nodo("+ Nodo +") ha recibido nuevos datos de entrenamientoo...",3);
					} catch (InterruptedException e) {
						System.err.println("Problema leyendo reglas del buffer mejores re"+ e);
						e.printStackTrace();
					}
					
				}
			}
		}
		
	}

	
	
	/**
	 * Reinicializa el Buffer. 
	 *
	 */
	
	public static void reinicializar(){
		instancia=new  BufferSupervisor();
	}
	
	
	
	

}
