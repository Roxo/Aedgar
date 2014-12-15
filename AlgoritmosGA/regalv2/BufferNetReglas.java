package regalv2;
import java.util.ArrayList;

import aleatorios.Aleatorio;

/**
 * BufferNetReglas hace uso del patrón de diseño singleton con el objetivo que solo exista un buffer de comunicación de reglas en toda la aplicación.  
 * Este buffer es utilizado solamente por los nodos para comunicarse las reglas entre ellos. Tiene dos métodos que utilizan los nodos para enviar y recibir reglas.
 * @author José Luis Toscano Muñoz
 * @version Regal v2.0
 */

public class BufferNetReglas {

	private ParametrosGlobales param=ParametrosGlobales.getInstancia_Parametros();
	
	private Regla bufferReglas[];
	private static BufferNetReglas instancia=new  BufferNetReglas();
	private int numIndividuosPorNodo=0;
	private Aleatorio genAleatorio;
	
	
	/**
	 * Constructor privado de la clase.
	 */
	private BufferNetReglas(){
		genAleatorio=param.getGeneradorAleatorio();
		numIndividuosPorNodo=(int)(param.getRatioMigracionNu()*param.getM());
		if(numIndividuosPorNodo%2!=0)
			numIndividuosPorNodo--;
		int num_Individuos_Total=numIndividuosPorNodo*param.getNumeroNodosRegal();		
		bufferReglas=new Regla[num_Individuos_Total];

		for(int i=0;i<bufferReglas.length;i++)
			bufferReglas[i]=null;
	};
	
	/**
	 * Devuelve la instancia del buffer.
	 * @return instancia
	 */
	
	public static BufferNetReglas getInstanciaBufferNet(){
		return instancia;
	}
	
	/**
	 * <p>
	 * Reinicia el buffer .
	 *</p>
	 */
	
	public static void reinicializar(){
		instancia=new  BufferNetReglas();
	}
	
	/**
	 * Envía las reglas al buffer de comunicación entre nodos.
	 * @param ident_Nodo Identificador del nodo que envía las reglas.
	 * @param reglasEnviar Conjunto de reglas que son enviadas al buffer.
	 */
	
	public  void enviarReglas(int ident_Nodo, Solucion reglasEnviar){
		synchronized(bufferReglas){
			param.depuracion("Nodo("+ident_Nodo+") Envia reglas a la red ...",3);
			int posicion_Inicio=ident_Nodo*numIndividuosPorNodo;
			int posicion_fin=posicion_Inicio+numIndividuosPorNodo;
			int x=0;
				try{
					for (int i=posicion_Inicio;i<posicion_fin;i++){
						bufferReglas[i]=reglasEnviar.getRegla(x).getCopia();
						x++;
					}
					bufferReglas.notifyAll();
				}catch (Exception e){
					//e.printStackTrace();
				}
		}
		synchronized(this){
			this.notifyAll();
		}
	}
	
	
	/**
	 * Recibe reglas de buffer.
	 * @param ident_Nodo es el identificador del nodo que solicita reglas.
	 * @return un conjunto de reglas de la red. 
	 */
	public  Solucion recibirReglas(int ident_Nodo){
		synchronized(bufferReglas){
			int posicion_Inicio=ident_Nodo*numIndividuosPorNodo;
			int posicion_fin=posicion_Inicio+numIndividuosPorNodo;
			 Solucion Reglas_Recibidas=null;
			boolean Recibidas=false;
			
			while ((Recibidas==false)&&(param.getContinuarBusqueda())){ 
				ArrayList Posiciones_con_Individuos=new ArrayList();
				 for(int i=0;i<bufferReglas.length;i++){
					 if(!((i>=posicion_Inicio)&(i<posicion_fin))){
						 if (bufferReglas[i]!=null){
							 Posiciones_con_Individuos.add(new Integer(i));
						 }
					 }			 
				 }
				
				 if(Posiciones_con_Individuos.size()>=numIndividuosPorNodo){
					 Reglas_Recibidas=new Solucion();
					 for (int i=0;i<this.numIndividuosPorNodo;i++){
						 int ind_pos=genAleatorio.randInt(0,Posiciones_con_Individuos.size()-1);
						 int posregla=Integer.parseInt(Posiciones_con_Individuos.get(ind_pos).toString());
						 Reglas_Recibidas.insertarRegla(bufferReglas[posregla].getCopia());
						 Posiciones_con_Individuos.remove(ind_pos);
					 }
					 Recibidas=true;
				 }else{
					 try{
						 param.depuracion("Nodo("+ident_Nodo+") Parado en espera de Reglas ...",3);
						 bufferReglas.wait();
						 param.depuracion("Nodo("+ident_Nodo+") Continúa la búsqueda ..." ,3);
					 }catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				 }
			} 
			 return Reglas_Recibidas;
		}
	}
	
	/**
	 * Libera las colas de los nodos que se quedan a la espera de reglas, cuando ha finalizado la búsqueda.
	 *
	 */
	public void liberaColas(){
		this.notifyAll();		
	}

}


