package NowGNET;

import java.rmi.Naming;

public class NodoE extends Thread{

	/**
	 * Es el identificador del nodo E.
	 */
	private int identificadorNodoE=0;
	
	
	/**
	 * Representa el conjunto de entrenamiento que el supervisor a asignado al nodo.
	 */
	private ConjuntoEntrenamiento datosEntrenamiento;
	
	
	/**
	 * Este objeto contiene los parámetros de configuración de la ejecución.
	 */
	private ParametrosGlobales parametrosGlobales=ParametrosGlobales.getInstancia_Parametros();
	
	
	public NodoE(ConjuntoEntrenamiento _datosEntrenamiento, int Identificacion){
		datosEntrenamiento=_datosEntrenamiento;
		identificadorNodoE=Identificacion;
	}
	
	public void run(){
		  BufferReglasNoEvaluadas ReglasEvaluar=BufferReglasNoEvaluadas.getInstancia();		  
		  BufferReglasEvaluadas ReglasE=BufferReglasEvaluadas.getInstancia();	
	
		while (this.parametrosGlobales.getContinuarBusqueda()){
			
			try
	        {		
				  Regla R =ReglasEvaluar.getRegla();				  				  
				  if (R!=null){					    
						R.evaluarSolucion(datosEntrenamiento);
						parametrosGlobales.depuracion("NODO E(" + this.identificadorNodoE+") Evalúa la regla: "+R.getTextoRegla(), 5);
						/*if(R.getFitness()==0){
							parametrosGlobales.depuracion("NODO E(" + this.identificadorNodoE+") FITNESS = 0 Casi "+R.getFitness(), 5);
						}*/
						ReglasE.EnviarReglaEvaluada(R);
						
				   }
	        }
	        catch (Exception e)
	        {
	        	System.out.println(e.getMessage());
	            e.printStackTrace();
	        }			
		}
	}
	

	
}
