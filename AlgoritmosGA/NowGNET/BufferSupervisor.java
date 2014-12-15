package NowGNET;

import java.util.Hashtable;


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

public class BufferSupervisor  {

	private static BufferSupervisor instancia=new  BufferSupervisor();
	private Hashtable bufferEjemplosNodosG=new Hashtable();
	private Solucion bufferConceptosNodos = new Solucion();
	private ParametrosGlobales parametrosGlobales =ParametrosGlobales.getInstancia_Parametros();

	private Integer contadorMicroCiclos=0;
	private Integer contadorMacroCiclos=0;
	
	
	
	/**
	 * Contructor privado de la clase. 
	 *
	 */
	private BufferSupervisor(){
		//instancia=new  BufferSupervisor();
		bufferEjemplosNodosG=new Hashtable();
		bufferConceptosNodos = new Solucion();
	}
	
	
	/**
	 * Reinicializa el Buffer. 
	 * @throws RemoteException 
	 *
	 */
	
	public static void Inicializar(){
		instancia=new  BufferSupervisor();
	}
	
	
	/**
	 * Devuelve una instancia del buffer de comunicación entre los nodos y el supervisor.
	 * @return una instancia del BufferSupervisor
	 */
	public synchronized static BufferSupervisor getInstancia(){
		return instancia;
	}
	
	
	
	public Ejemplo getEjemplo(int _identificadorNodo){
		Ejemplo E=(Ejemplo)bufferEjemplosNodosG.get(_identificadorNodo);
		if (E!=null){
			bufferEjemplosNodosG.remove(_identificadorNodo);
		}
		return E;
	}
	
	public void setEjemplo(int _identificadorNodo, Ejemplo _E){
		if (bufferEjemplosNodosG.containsKey(_identificadorNodo)){
			bufferEjemplosNodosG.remove(_identificadorNodo);
		}
		bufferEjemplosNodosG.put(_identificadorNodo,_E);	
	}
	
	
	
	public int getNumeroMicroCiclos(){
		return this.contadorMicroCiclos;
	}
	
	
	public synchronized void incrementarNumeroMicroCiclo(){
		
		
		synchronized(bufferConceptosNodos){
			if ((this.contadorMicroCiclos< parametrosGlobales.getNumeroMicroCiclos())){ 
				this.contadorMicroCiclos++;
				parametrosGlobales.depuracion("MICRO: "+ this.contadorMicroCiclos ,5);
			}else{
				bufferConceptosNodos.notifyAll();
				parametrosGlobales.depuracion("Despertando al supervisor",5);
				this.contadorMicroCiclos=0;
			}
			
		}
		
		
		
		
	/*	synchronized(this.contadorMicroCiclos){
			if ((this.contadorMicroCiclos< parametrosGlobales.getNumeroMicroCiclos())){ 
				this.contadorMicroCiclos++;
				//parametrosGlobales.depuracion("Micro: " +this.contadorMicroCiclos, 5);
			}else{
				//this.contadorMicroCiclos.notifyAll();
				//bufferConceptosNodos.notifyAll();
				reinicarNumeroMicroCiclo();
				this.incrementarNumeroMacroCiclo();
				parametrosGlobales.depuracion("Despertando al supervisor",2);	

			}
		}

		synchronized(this){
			this.notifyAll();
		}*/
		
		

		
		
		
		
		
	}
	
	public void reinicarNumeroMicroCiclo(){
		synchronized(this.contadorMicroCiclos){
			this.contadorMicroCiclos=0;	
		}			
	}
	
	public int getNumeroMacroCiclos(){
		return this.contadorMacroCiclos;
	}
	
		
	public void incrementarNumeroMacroCiclo(){
		this.contadorMacroCiclos+=1;	
	}
	
		
	
	/**
	 * Envía las reglas encontradas en los nodos G al supervisor.
	 * @param new_regla es la regla que el nodo envía al supervisor. 
	 */
	public void enviarRegla(Regla new_regla){
		synchronized(this.bufferConceptosNodos){
			if (this.bufferConceptosNodos==null){
				this.bufferConceptosNodos=new Solucion();
			}
			if (!this.bufferConceptosNodos.existeRegla(new_regla))
				this.bufferConceptosNodos.insertarRegla(new_regla.getCopia());
			parametrosGlobales.depuracion("Nº Reglas Enviadas al supervisor..."+bufferConceptosNodos.getTamaño(),5);
			}
		
		}
	
	
	
	/**
	 * El supervisor recupera las reglas enviadas por los nodos.
	 * @return un objeto Solución con el conjunto de reglas enviadas por los nodos.
	 */
	
/*	public  Solucion getConceptoEncontrado(){
		Solucion conjuntodeReglas=new Solucion();
		synchronized(this.contadorMicroCiclos){
		
			try {
				parametrosGlobales.depuracion("El supervisor se detiene en espera de finalice un Macrociclo...",1);
				if (this.contadorMicroCiclos<parametrosGlobales.getNumeroMicroCiclos()){
					this.contadorMicroCiclos.wait();
					parametrosGlobales.depuracion("El supervisor analiza las reglas recibidas...",2);
				}
					
				} catch (InterruptedException e) {
					System.err.println("Problema leyendo reglas del buffer mejores re"+ e);
				e.printStackTrace();
				}
				
				parametrosGlobales.depuracion("Eniando Regas al Supervi...",2);
			for(int i=0;i<bufferConceptosNodos.getTamaño();i++){
				parametrosGlobales.depuracion("Eniando Regas al Supervi 22222...",2);
				Regla regla_devolver=null;
				regla_devolver=bufferConceptosNodos.getRegla(i).getCopia();
				conjuntodeReglas.insertarRegla(regla_devolver);
			}
		
		return conjuntodeReglas;
		}		
}*/	
		
		public  Solucion getConceptoEncontrado(){
			
			synchronized(bufferConceptosNodos){
				Solucion conjuntodeReglas=new Solucion();
				if (contadorMicroCiclos<parametrosGlobales.getNumeroMicroCiclos())
					try {
						parametrosGlobales.depuracion("El supervisor se detiene en espera de reglas...",1);
						bufferConceptosNodos.wait();
						parametrosGlobales.depuracion("El supervisor analiza las reglas recibidas...",1);
						} catch (InterruptedException e) {
							System.err.println("Problema leyendo reglas del buffer mejores re"+ e);
						e.printStackTrace();
					}
				for(int i=0;i<bufferConceptosNodos.getTamaño();i++){
					Regla regla_devolver=null;
					regla_devolver=bufferConceptosNodos.getRegla(i).getCopia();
					conjuntodeReglas.insertarRegla(regla_devolver);
				}
				bufferConceptosNodos=new Solucion();
			
			return conjuntodeReglas;
			}
		}
		
		
		
		
		

	
	
	
	
	
	

	
	
	

}
