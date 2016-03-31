package edgar;

/**
 * @author Esta clase es un patron singleton.Sirve para que el proceso principal espere a que la soluci�n este completa.
 *  El constructor es privado para evitar que se pueda instanciar otra en el resto de la aplicaci�n.
 *  Todos sus m�todos son por lo tanto estaticos. 
 *
 */
public class SolucionFinal {

	/**
	 * Contendr� la soluci�n devuelta por el proceso
	 */
	private Solucion solucionActual=null;
	
	
	private static SolucionFinal instancia=new  SolucionFinal();
	
	
	// Constructor privado, para asegurarnos que solo existe un buffer para devolver la soluci�n en toda la aplicaci�n.
	private SolucionFinal(){};
	
	public static SolucionFinal getInstancia_SolucionFinal(){
		return instancia;
	}
	
	
	
	public synchronized void Put_SolucionFinal(Solucion _SolucionFinal){
		solucionActual=_SolucionFinal;
		this.notifyAll();
	}
	
	/**
	 * El programa principal se queda esperando a que algun hilo (pool en este caso) 
	 * libere el cerrojo para continuar cuando haya acabado el proceso de busqueda
	 * @return  la soluci�n
	 */
	public synchronized Solucion Get_SolucionFinal(){
		try{
			if(solucionActual==null){
				System.out.println(" EDGAR est� buscando una soluci�n ..." );
				wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		solucionActual.quitaAtributosNoSelectivos();
		return this.solucionActual;
	}
		
	
	public synchronized void Reiniciar_SolucionFinal(){
		solucionActual=null;
	}
	
	
}
