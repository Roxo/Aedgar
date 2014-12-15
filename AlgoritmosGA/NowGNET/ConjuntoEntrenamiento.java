package NowGNET;

import java.util.ArrayList;

import aleatorios.Aleatorio;

/**
 * ConjuntoEntrenamiento contiene los métodos para manejar un conjunto de Ejemplos.
 * @author José Luis Toscano Muñoz
 * @version Regal v2.0
 */

public class ConjuntoEntrenamiento {

	private ArrayList setEntrenamiento = new ArrayList();

	/**
	 * Constructor de la clase.
	 *
	 */
	public ConjuntoEntrenamiento(){
		
	}
	
	/**
	 * Divide el conjunto de datos de entrenamiento en un número de subconjuntos, cada ejemplo es seleccionado de forma aleatoria.
	 * @param numSubConjuntos número de subconjuntos en los va a ser particionado el conjunto de entrenamiento.
	 * @return una tabla con los subconjuntos de datos de entrenamiento.
	 */
	
	public ConjuntoEntrenamiento[] getNSubConjuntosEjemplos(int numSubConjuntos){
		ConjuntoEntrenamiento[] SubConjuntos=new ConjuntoEntrenamiento[numSubConjuntos];
		
		int numClases=ParametrosGlobales.getInstancia_Parametros().getNumeroClases();
		
		ArrayList IndicesDisponibles[]=new ArrayList[numClases];
		
		for (int i=0;i<numClases;i++)
			IndicesDisponibles[i]=new ArrayList();
		
		for (int i=0;i<setEntrenamiento.size();i++) 
			IndicesDisponibles[((Ejemplo)setEntrenamiento.get(i)).getClase()].add(new Integer(i));	
		
		// Reservo memoria para los distintos conjuntos de ejemplos
		for (int i=0;i<numSubConjuntos;i++) SubConjuntos[i]=new ConjuntoEntrenamiento();
		int ValAleatorio=0;
		
		for (int cl=0;cl<numClases;cl++){
			while(IndicesDisponibles[cl].size()>0){
				for (int i=0;i<numSubConjuntos;i++){
					if(IndicesDisponibles[cl].size()>0){
						Aleatorio gen_aleatorio=ParametrosGlobales.getInstancia_Parametros().getGeneradorAleatorio();
						ValAleatorio=gen_aleatorio.randInt(0,IndicesDisponibles[cl].size()-1);
						int indCromosoma=(Integer.parseInt(IndicesDisponibles[cl].get(ValAleatorio)+""));
						SubConjuntos[i].insertarEjemplo((Ejemplo)setEntrenamiento.get(indCromosoma));
						IndicesDisponibles[cl].remove(ValAleatorio);
					}
				}
			}
		}
		return SubConjuntos;
	}
	
	/**
	 * Devuelve el tamaño del conjunto de entrenamiento.
	 * @return un int con el tamaño del conjunto de entrenamiento.
	 */
	public int getTamaño(){
		return setEntrenamiento.size();
	}
	
	/**
	 * Devuelve el ejemplo que esté en la posición que se le pasa como parámetro.
	 * @param indiceEjemplo índice del ejemplo.
	 * @return el ejemplo que se encuentre en la posición pasada como parámetro.
	 */
	public Ejemplo getEjemplo(int indiceEjemplo){
		return (Ejemplo)setEntrenamiento.get(indiceEjemplo);
	}
	
	
	/**
	 * Inserta un ejemplo en el conjunto de entrenamiento.
	 * @param ej Ejemplo a insertar en el conjunto de entrenamiento.
	 */
	public void insertarEjemplo(Ejemplo ej){
		try{
			if (setEntrenamiento == null) setEntrenamiento=new ArrayList();
			setEntrenamiento.add(ej);
			
		}catch(Exception e){
			System.out.println("Error añadiendo un nuevo cromosoma a la poblacion");
		}
	}
	
	/**
	 * Elimina el ejemplo que esté en la posición que se le pasa como parámetro. 
	 * @param indiceEjemplo índice del ejemplo.
	 */
	public void eliminarEjemplo(int indiceEjemplo){	
		try{
			setEntrenamiento.remove(indiceEjemplo);
		}catch(Exception e){
			System.out.println("Error elimando un ejemplo");
		}
	}
	
	
}
