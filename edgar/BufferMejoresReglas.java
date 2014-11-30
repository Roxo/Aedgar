package edgar;

/**
 * Buffer Mejores reglas es el almacen donde los nodos van dejando 
 * las reglas seleccionadas a ser candidatas del clasificador general
 * El buffer tiene un constructor privado para evitar que se pueda generar otro buffer
 * Este por lo tanto esta accesible  través del nombre de la clase con un metodo estático 
 */
public class BufferMejoresReglas {
	static int num_comunicaciones = 0; //con
	final int nodos= Parametros.getInstancia_Parametros().get_Numero_Nodos();
	private static BufferMejoresReglas instancia=new  BufferMejoresReglas();
	
	private Solucion buffer_comunicacion_mejores_reglas=new Solucion();
	
	private BufferMejoresReglas(){};
	/**
	 * Este metodo permite tener acceso a la unica instancia posible del buffer
	 * @return buffer_Mejores_Reglas
	 */
	public static BufferMejoresReglas getInstancia_buffer(){
		return instancia;
	}
	
	public synchronized Solucion getBuffer_comunicacion_mejores_reglas(){
		return buffer_comunicacion_mejores_reglas;
	}
	
	/**
	 * Devuelve el numero de reglas pendiente de leer por el supervisor
	 * proveniente de los nodos
	 */
	public synchronized int get_tamaño_bufferReglas(){
		return buffer_comunicacion_mejores_reglas.getTamaño_solucion();
	}
	/** 
	 * Añade una regla al buffer de comunicación con el supervisor
	 * comprueba que no exista ya antes de añadirla
	 */
	
	public void add_regla(Regla new_regla){
		synchronized(buffer_comunicacion_mejores_reglas){
		if (!buffer_comunicacion_mejores_reglas.Existe_regla(new_regla))
			buffer_comunicacion_mejores_reglas.insertarRegla(new_regla.getCopia());
		}
		
	}
	
	public  void fin_comunicacion(){
		synchronized(buffer_comunicacion_mejores_reglas){
		if (num_comunicaciones<nodos && Parametros.getInstancia_Parametros().get_Continuar_Busqueda() ) 
			num_comunicaciones++;
		else{
			buffer_comunicacion_mejores_reglas.notifyAll();
			Parametros.getInstancia_Parametros().depura("Despertando al supervisor",0);
			num_comunicaciones=0;
		}
		}
	}
	
	
	public  Solucion get_mejores_reglas(){
		
		Solucion conjuntodeReglas=new Solucion();
		synchronized(buffer_comunicacion_mejores_reglas){
		if (num_comunicaciones< nodos && Parametros.getInstancia_Parametros().get_Continuar_Busqueda())
			try {
				Parametros.getInstancia_Parametros().depura("parando al supervisor",0);
				buffer_comunicacion_mejores_reglas.wait();
				} catch (InterruptedException e) {
				System.out.println( "problema leyendo reglas de buffer "+ e);
				e.printStackTrace();
			}
		for(int i=0;i<buffer_comunicacion_mejores_reglas.getTamaño_solucion();i++){
			Regla regla_devolver=null;
			regla_devolver=buffer_comunicacion_mejores_reglas.get_regla(i).getCopia();
			conjuntodeReglas.insertarRegla(regla_devolver);
		}
		buffer_comunicacion_mejores_reglas=new Solucion();
		}
		return conjuntodeReglas;
	}
	
	
	
}