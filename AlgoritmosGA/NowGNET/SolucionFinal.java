package NowGNET;

public class SolucionFinal {
	private Solucion SolucionGNET=null;
	
	
	private static SolucionFinal instancia=new  SolucionFinal();
	
	
	// Constructor privado, para asegurarnos que solo existe un buffer en toda la aplicación.
	private SolucionFinal(){};
	
	public static SolucionFinal getInstancia_SolucionFinal(){
		return instancia;
	}
	
	
	/**
	 * Asigna el concepto final obtenido por el supervisor e indica que este a finalizado la búsqueda. 
	 * @param _SolucionFinal es la Solucion encotrada por el supervior.
	 */
	public synchronized void Put_SolucionFinal(Solucion _SolucionFinal){
		SolucionGNET=_SolucionFinal;
		this.notify();
	}
	
	/**
	 * Devuelve el concepto final alcanzado por el supervisor.
	 * @return es la Solucion encotrada por el supervior.
	 */
	public synchronized Solucion Get_SolucionFinal(){
		try{
			if(SolucionGNET==null){
				System.out.println(" GNET está buscando una solución ..." );
				wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.SolucionGNET;
	}
		
	/**
	 * Reinicia la Solucion final alcanzada por el supervisor.
	 */
	public synchronized void Reiniciar_SolucionFinal(){
		SolucionGNET=null;
	}
}
