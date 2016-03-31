package edgar;

public interface NET {
	public  void EnviarReglas(int ident_Nodo, Solucion reglasEnviar);
	public Solucion RecibirReglas(int ident_Nodo);
	public void ReinicializarNET();
	public  void EnviarDatosEntrenamiento(int ident_Nodo, Dataset nuevosDatosEntrenamientos);
	public  Dataset Get_Datos_Entrenamiento (int Nodo);
	
}
