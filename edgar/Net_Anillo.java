package edgar;

public class Net_Anillo implements NET {

	
	
	private Parametros param=Parametros.getInstancia_Parametros();
	private Solucion buffer_Net_Anillo[]=new Solucion[param.get_Numero_Nodos()];
	private Dataset buffer_datos_entrenamientos[]= new Dataset[param.get_Numero_Nodos()];
	private static Net_Anillo instancia=new  Net_Anillo();
	//private Object semaforo[]=new Object[param.get_Numero_Nodos()];
	
	// Constructor privado, para asegurarnos que solo existe un buffer en toda la aplicación.
	private Net_Anillo(){
		for(int i=0;i<buffer_Net_Anillo.length;i++){
			buffer_Net_Anillo[i]=null;
			buffer_datos_entrenamientos[i]= null;
		}
	};
	/**
	 * Es posible que algun hilo este pendiente de recibir datos cuando se acaba la busqueda
	 */
	public synchronized void liberaColas(){this.notifyAll();}
	
	
	public static Net_Anillo getInstancia_NET(){
		return instancia;
	}
	
	public void ReinicializarNET(){
		instancia=new Net_Anillo();
	}
	public void EnviarReglas(int ident_Nodo, Solucion reglasEnviar){
		synchronized(buffer_Net_Anillo){
			param.depura("Nodo("+ident_Nodo+") ENVIA REGLAS NET ANILLO ..." ,1);	
			
			int Ident_Nodo_destino=ident_Nodo+1;
			if (Ident_Nodo_destino==param.get_Numero_Nodos())
				Ident_Nodo_destino=0;
			if (buffer_Net_Anillo[Ident_Nodo_destino]==null)
				buffer_Net_Anillo[Ident_Nodo_destino]=reglasEnviar.get_Copia();
			else
				for (int i=0;i<reglasEnviar.getTamaño_solucion();i++ )
					buffer_Net_Anillo[Ident_Nodo_destino].insertarRegla(reglasEnviar.get_regla(i));
		}
		
	}
	
	
		
	public  Solucion RecibirReglas(int ident_Nodo){
		
		Solucion Reglas_Recibidas=null;
		synchronized(buffer_Net_Anillo){
			Reglas_Recibidas=buffer_Net_Anillo[ident_Nodo];
			buffer_Net_Anillo[ident_Nodo]=null;
			
			if (Reglas_Recibidas==null)Reglas_Recibidas=new Solucion();
			param.depura("Nodo("+ident_Nodo+") RECIBE NET ANILLO ..."+ Reglas_Recibidas ,2);// poner a 2
			return Reglas_Recibidas;
		}
		
	}
	/**
	 * @author Miguel Angel
	 * @param ident_Nodo Nodo que envia los datos de entrenamiento
	 * @param nuevosDatosEntrenamientos : Conjunto de datos que queremos pasar al siguiente nodo;
	 */	
	public void EnviarDatosEntrenamiento(int ident_Nodo, Dataset nuevosDatosEntrenamientos){
		synchronized(buffer_datos_entrenamientos){
			param.depura("Nodo("+ident_Nodo+") ENVIA DATOS "+nuevosDatosEntrenamientos.getTamanho_conjunto_entrenamiento() +" NET ANILLO ..." ,1);	
			
			int Ident_Nodo_destino=ident_Nodo+1;
			if (Ident_Nodo_destino==param.get_Numero_Nodos())
				Ident_Nodo_destino=0;
			
			if (buffer_datos_entrenamientos[Ident_Nodo_destino]== null)
						buffer_datos_entrenamientos[Ident_Nodo_destino]=nuevosDatosEntrenamientos;
			else
				for (int i=0;i<nuevosDatosEntrenamientos.getTamanho_conjunto_entrenamiento();i++)
						buffer_datos_entrenamientos[Ident_Nodo_destino].Insertar_Ejemplo_SinRepeticion(nuevosDatosEntrenamientos.get_EjemploFuzzy(i));
		}
	}
	
	public  Dataset Get_Datos_Entrenamiento (int Nodo){
		Dataset datosEntrenamiento = new Dataset();
		synchronized(buffer_datos_entrenamientos){
			if(buffer_datos_entrenamientos[Nodo]!= null){
				// Primero asigno los datos de entrenamiento
				datosEntrenamiento= buffer_datos_entrenamientos[Nodo];
				// Cuando toma los datos de entrenaiento, se vacia el buffer
				buffer_datos_entrenamientos[Nodo]=null;
				param.depura("Nodo(" +Nodo + "): Ha obtenido "+ datosEntrenamiento.getTamanho_conjunto_entrenamiento()+" Datos de entrenamiento ...",2);
			}
		}
		return datosEntrenamiento;
	}
//Fin de clase	
}
	

