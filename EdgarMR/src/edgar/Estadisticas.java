package edgar;

public class Estadisticas extends Thread{
	private static Estadisticas instancia=new  Estadisticas();
	
	int Contador_Comunicaciones=0;
	int Contador_Evaluaciones=0;
	int Contador_EvaluacionesDesglosado=0;
	private Estadisticas(){};
	
	public static Estadisticas getInstancia_buffer(){
		return instancia;
	}

	public synchronized int get_NumeroComunicaciones(){
		return Contador_Comunicaciones;
	}
	
	
	public synchronized int get_NumeroEvaluaciones(){
		return Contador_Evaluaciones;
	}
	/**
	 * teniendo en cuenta el número de datos de entrenamiento
	 * 
	 */
	public synchronized int get_NumeroEvaluacionesDesglosado(){
		return Contador_EvaluacionesDesglosado;
	}
	
	
	public synchronized void add_Comunicaciones(int _NuevasComunicaciones){
		Contador_Comunicaciones+=_NuevasComunicaciones;
	}
	
	public synchronized void add_Evaluaciones(int _NuevasEvaluaciones){
		Contador_Evaluaciones+=_NuevasEvaluaciones;
	}
	public synchronized void add_EvaluacionesDesglosado(int _NuevasEvaluaciones){
		Contador_EvaluacionesDesglosado+=_NuevasEvaluaciones;
	}
	
}
