package regalv2;
/**
 * CostesComputacionales es utilizada para controlar los costes computacionales de las
 * distintas ejecuciones y contabilizar el n�mero de evaluaciones y comunicaciones que se producen.
 * @author Jos� Luis Toscano Mu�oz
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
	 * Devuelve el n�mero de comunicaciones que se han producido.
	 * @return n�mero de comunicaciones.
	 */
	public synchronized int getNumeroComunicaciones(){
		return contadorComunicaciones;
	}
	
	/**
	 * Devuelve el n�mero de evaluaciones que se han producido.
	 * @return n�mero de evaluaciones.
	 */
	public synchronized int getNumeroEvaluaciones(){
		return contadorEvaluaciones;
	}
	
	/**
	 * Incrementa el n�mero de comunicaciones
	 * @param _NuevasComunicaciones es el n�mero de comunicaciones que incrementar� el contador.
	 */
	public synchronized void addComunicaciones(int _NuevasComunicaciones){
		contadorComunicaciones+=_NuevasComunicaciones;
	}
	
	/**
	 * Incrementa el n�mero de evaluaciones
	 * @param _NuevasEvaluaciones es el n�mero de evaluaciones que incrementar� el contador.
	 */
	public synchronized void addEvaluaciones(int _NuevasEvaluaciones){
		contadorEvaluaciones+=_NuevasEvaluaciones;
	}
	
	
}
