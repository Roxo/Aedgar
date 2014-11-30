package edgar;
import java.util.ArrayList;

import Aleatorios.Aleatorio;

public class Net_Estrella implements NET{

	
	
	private Parametros param=Parametros.getInstancia_Parametros();
	
	
	private Regla buffer_Net_Estrella[];
	private Dataset buffer_datos_entrenamientos;
	private static Net_Estrella instancia=new  Net_Estrella();
	private int num_Individuos_x_Nodo=0;
	private Aleatorio gen_aleatorio;
	
	
	// Constructor privado, para asegurarnos que solo existe un buffer en toda la aplicación.
	private Net_Estrella(){
		
		gen_aleatorio=param.get_GeneradorAleatorio();
		num_Individuos_x_Nodo=(int)(param.get_ratio_migracion_nu()*param.getPoblacion());
		if(num_Individuos_x_Nodo%2!=0)
			num_Individuos_x_Nodo--;
		int num_Individuos_Total=num_Individuos_x_Nodo*param.get_Numero_Nodos();		
		buffer_Net_Estrella=new Regla[num_Individuos_Total];
		buffer_datos_entrenamientos= new Dataset();

		for(int i=0;i<buffer_Net_Estrella.length;i++)
			buffer_Net_Estrella[i]=null;
	};
	
	
	public static Net_Estrella getInstancia_NET(){
		return instancia;
	}
	
	
	public void ReinicializarNET(){
		instancia=new  Net_Estrella();
	}
	
	
	public  void EnviarReglas(int ident_Nodo, Solucion reglasEnviar){
		synchronized (buffer_Net_Estrella){
		if (param.get_Nivel_Depuracion()>1){
			System.out.println("Nodo("+ident_Nodo+") ENVIA NET ESTRELLA ..." );	
		}
		int posicion_Inicio=ident_Nodo*num_Individuos_x_Nodo;
		int posicion_fin=posicion_Inicio+num_Individuos_x_Nodo;
		int x=0;
		for (int i=posicion_Inicio;i<posicion_fin;i++){
			buffer_Net_Estrella[i]=reglasEnviar.get_regla(x).getCopia();
			x++;
		}
		}
		
	}
	
	public void EnviarDatosEntrenamiento(int ident_Nodo, Dataset nuevosDatosEntrenamientos){
		synchronized(buffer_datos_entrenamientos){
			param.depura("Nodo("+ident_Nodo+") ENVIA DATOS "+nuevosDatosEntrenamientos.getTamaño_conjunto_entrenamiento() +" NET ANILLO ..." ,1);	
			
			for (int i=0;i<nuevosDatosEntrenamientos.getTamaño_conjunto_entrenamiento();i++)
						buffer_datos_entrenamientos.Insertar_Ejemplo_SinRepeticion(nuevosDatosEntrenamientos.get_EjemploFuzzy(i));
		}
	}
	
	public  Dataset Get_Datos_Entrenamiento (int Nodo){
		Dataset datosEntrenamiento = new Dataset();
		synchronized(buffer_datos_entrenamientos){
			if(buffer_datos_entrenamientos.getTamaño_conjunto_entrenamiento() >0){
				// Primero asigno los datos de entrenamiento
				int particion = buffer_datos_entrenamientos.getTamaño_conjunto_entrenamiento()/param.get_Numero_Nodos();
				for (int i = particion; i>0;i--){
					int indice = param.get_GeneradorAleatorio().Randint(0,i-1);
				
					datosEntrenamiento.Insertar_Ejemplo_SinRepeticion(buffer_datos_entrenamientos.get_EjemploFuzzy(indice));
					buffer_datos_entrenamientos.Eliminar_Ejemplo(indice);
				}
				param.depura("Nodo(" +Nodo + "): Ha obtenido "+ datosEntrenamiento.getTamaño_conjunto_entrenamiento()+" Datos de entrenamiento ...",2);
			}
		}
		return datosEntrenamiento;
	}
	
	
	
	
	public synchronized Solucion RecibirReglas(int ident_Nodo){
		
		if (param.get_Nivel_Depuracion()>1){
			System.out.println("Nodo("+ident_Nodo+") RECIBE NET ESTRELLA ..." );	
		}
		
		int posicion_Inicio=ident_Nodo*num_Individuos_x_Nodo;
		int posicion_fin=posicion_Inicio+num_Individuos_x_Nodo;
		 Solucion Reglas_Recibidas=null;
			ArrayList Posiciones_con_Individuos=new ArrayList(); // indice con las posiciones llenas del buffer
			 for(int i=0;i<buffer_Net_Estrella.length;i++){
				 if(!((i>=posicion_Inicio)&(i<posicion_fin))){
					 if (buffer_Net_Estrella[i]!=null){
						 Posiciones_con_Individuos.add(new Integer(i));
					 }
				 }			 
			 }
			 Reglas_Recibidas=new Solucion();
			 if(Posiciones_con_Individuos.size()>=num_Individuos_x_Nodo){
				
				 for (int i=0;i<this.num_Individuos_x_Nodo;i++){
					 int ind_pos=gen_aleatorio.Randint(0,Posiciones_con_Individuos.size()-1);
					 int posregla=Integer.parseInt(Posiciones_con_Individuos.get(ind_pos).toString());
					 Reglas_Recibidas.insertarRegla(buffer_Net_Estrella[posregla].getCopia());
					 Posiciones_con_Individuos.remove(ind_pos); //quitar esta linea si se quiere que se pueda volver a elegir 
				 }
				
			 }
					 
		 return Reglas_Recibidas;
	}


	




}
