package NowGNET;
/**
 * CostesComputacionales es utilizada para controlar los costes computacionales de las
 * distintas ejecuciones y contabilizar el número de evaluaciones y comunicaciones que se producen.
 * @author José Luis Toscano Muñoz
 * @version Regal v2.0
 */

public class CostesComputacionales extends Thread{
	private static CostesComputacionales instancia=new  CostesComputacionales();
	
	private int contadorComunicaciones=0;
	private int contadorEvaluaciones=0;
	
	/**
	 * Constructor privado.
	 */
	private CostesComputacionales(){};
	
	/**
	 * Devuelve una instancia del objeto CostesComputacionales 
	 * @return una instancia de CostesComputacionales.
	 */
	public static CostesComputacionales getInstancia(){
		return instancia;
	}

	/**
	 * Devuelve el número de comunicaciones que se han producido.
	 * @return número de comunicaciones.
	 */
	public synchronized int getNumeroComunicaciones(){
		return contadorComunicaciones;
	}
	
	/**
	 * Devuelve el número de evaluaciones que se han producido.
	 * @return número de evaluaciones.
	 */
	public synchronized int getNumeroEvaluaciones(){
		return contadorEvaluaciones;
	}
	
	/**
	 * Incrementa el número de comunicaciones
	 * @param _NuevasComunicaciones es el número de comunicaciones que incrementará el contador.
	 */
	public synchronized void addComunicaciones(int _NuevasComunicaciones){
		contadorComunicaciones+=_NuevasComunicaciones;
	}
	
	/**
	 * Incrementa el número de evaluaciones
	 * @param _NuevasEvaluaciones es el número de evaluaciones que incrementará el contador.
	 */
	public synchronized void addEvaluaciones(int _NuevasEvaluaciones){
		contadorEvaluaciones+=_NuevasEvaluaciones;
	}
	
	
}
