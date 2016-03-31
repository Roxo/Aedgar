package edgar;

/**
 * @author Esta clase es un patron singleton.Sirve para que el proceso principal espere a que la solución este completa.
 *  El constructor es privado para evitar que se pueda instanciar otra en el resto de la aplicación.
 *  Todos sus métodos son por lo tanto estaticos. 
 *
 */
public class SolucionFinal {

	/**
	 * Contendrá la solución devuelta por el proceso
	 */
	private Solucion solucionActual=null;
	
	
	private static SolucionFinal instancia=new  SolucionFinal();
	
	
	// Constructor privado, para asegurarnos que solo existe un buffer para devolver la solución en toda la aplicación.
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
	 * @return  la solución
	 */
	public synchronized Solucion Get_SolucionFinal(){
		try{
			if(solucionActual==null){
				System.out.println(" EDGAR está buscando una solución ..." );
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
