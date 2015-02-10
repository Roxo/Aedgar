package edgar;

import java.util.ArrayList;

import Aleatorios.Aleatorio;

/**
 * @author miguel
 *
 */
public class Dataset {

	// JMGM - Cambiada visibilidad de los atributos a protected para poder heredar
	protected ArrayList set_entrenamiento = new ArrayList();
    protected Plantilla plantilla;
    
	public Plantilla getPlantilla() {
		return plantilla;
	}

	public void setPlantilla(Plantilla plantilla) {
		this.plantilla = plantilla;
	}
	
	public Dataset() {

	}

	public Dataset[] getN_Sub_conjuntos_ejemplos(
			int numSubConjuntos) {
		Dataset[] SubConjuntos = new Dataset[numSubConjuntos];

		int numClases = plantilla.get_numero_Clases();

		ArrayList IndicesDisponibles[] = new ArrayList[numClases];

		for (int i = 0; i < numClases; i++)
			IndicesDisponibles[i] = new ArrayList();

		for (int i = 0; i < set_entrenamiento.size(); i++)
			IndicesDisponibles[((Ejemplo) set_entrenamiento.get(i)).getClase()]
					.add(new Integer(i));

		// Reservo memoria para los distintos conjuntos de ejemplos
		for (int i = 0; i < numSubConjuntos; i++)
			SubConjuntos[i] = new Dataset();
		int ValAleatorio = 0;
		// se considera no balanceda si el número de instancias totales de la clase es un 30% de lo que corresponria si fuesen equibalanceadas
		
		final int claseObjetivo = Parametros.getInstancia_Parametros().getClaseObjetivo();

		for (int cl = 0; cl < numClases; cl++) {
			Parametros.getInstancia_Parametros().depura(" " + cl +  ":" + plantilla.contadorClases(cl) , 0);
			while (IndicesDisponibles[cl].size() > 0) {
				for (int i = 0; i < numSubConjuntos; i++) {
					if (IndicesDisponibles[cl].size() > 0) {
						Aleatorio gen_aleatorio = Parametros
								.getInstancia_Parametros()
								.get_GeneradorAleatorio();
						ValAleatorio = gen_aleatorio.Randint(0,
								IndicesDisponibles[cl].size() - 1);
						int indCromosoma = (Integer
								.parseInt(IndicesDisponibles[cl]
										.get(ValAleatorio)
										+ ""));
						
						if ((Parametros.getInstancia_Parametros().isBalanceoClases() && (cl == this.claseMinoritaria())) || cl == claseObjetivo ){// si no esta balanceada duplica la clase
							for (int j = 0; j < numSubConjuntos; j++)
								SubConjuntos[j].Insertar_Ejemplo((Ejemplo)set_entrenamiento.get(indCromosoma));
						}
						else	
							SubConjuntos[i].Insertar_Ejemplo((Ejemplo)set_entrenamiento.get(indCromosoma));
						
						IndicesDisponibles[cl].remove(ValAleatorio);
					}
				}
			}
		}
		return SubConjuntos;
	}

	public int getTamanho_conjunto_entrenamiento() {
		return set_entrenamiento.size();
	}

	public Ejemplo get_ejemplo(int ind_ejemplo) {
		return (Ejemplo) set_entrenamiento.get(ind_ejemplo);
	}

	public void Insertar_Ejemplo(Ejemplo ej) {

		try {
			if (set_entrenamiento == null)
				set_entrenamiento = new ArrayList();

			set_entrenamiento.add(ej);

		} catch (Exception e) {
			System.out
					.println("Error añadiendo un nuevo Cromosoma a la poblacion");
		}
	}

	public void Insertar_Ejemplo_SinRepeticion(Ejemplo ej) {

		try {
			if (set_entrenamiento == null)
				set_entrenamiento = new ArrayList();

			int i = 0;
			boolean encontrado = false;
			while (i < set_entrenamiento.size() && encontrado == false) {

				if (this.get_ejemplo(i).get_id() == ej.get_id())
					encontrado = true;

				i++;
			}
			if (encontrado == true)
				Parametros.getInstancia_Parametros().depura("ejemplo " + ej + " repetido",1);
			else
				set_entrenamiento.add(ej);

		} catch (Exception e) {
			System.out
					.println("Error añadiendo un nuevo Cromosoma a la poblacion");
		}
	}

	public void Eliminar_Ejemplo(int indice_ejemplo) {
		try {
			set_entrenamiento.remove(indice_ejemplo);
		} catch (Exception e) {
			System.out.println("Error elimando un ejemplo");
		}
	}

	public String toString() {
		String cadena = "";
		for (int i = 0; i < set_entrenamiento.size(); i++)
			cadena += set_entrenamiento.get(i) + "\n";
		return cadena;
	}

	/*
	 * V2 ojo esta información al igual que el porcentaje de clases se encuentra ahora en plantilla, pero en realidad debería encontrarse en el dataset
	 * dado que es posible que en el dataset de entrada y en el de salida exista diferentes niveles de balanceo. Tampoco se tiene en cuenta el efecto que produce la partición de training /test, 
	 * Debería tenerse en cuenta esto en el dataset para poder analizar el grado de balanceo en un nodo determinado , en el servidor y en el test. 
	 *
	 */
	/**
	 * Devuelve la división de la clase mayoritaria entre la minoritaria. 
	 */
	public float RatioBalanceo() {
		return plantilla.RatioBalanceo();
		 
	}
public int claseMayoritaria(){
     return plantilla.claseMayoritaria();
}
	public int claseMinoritaria() {
		  return plantilla.claseMinoritaria();
	}
	
	
	// JMGM
	public Dataset[] getN_Sub_conjuntos_ejemplosFuzzy(int numSubConjuntos) 
	{
		Dataset[] SubConjuntos = new Dataset[numSubConjuntos];

		int numClases = plantilla.get_numero_Clases();

		ArrayList IndicesDisponibles[] = new ArrayList[numClases];

		for (int i = 0; i < numClases; i++)
			IndicesDisponibles[i] = new ArrayList();

		for (int i = 0; i < set_entrenamiento.size(); i++)
			IndicesDisponibles[((EjemploFuzzy) set_entrenamiento.get(i)).getClase()]
					.add(new Integer(i));

		// Reservo memoria para los distintos conjuntos de ejemplos
		for (int i = 0; i < numSubConjuntos; i++)
			SubConjuntos[i] = new Dataset();
		int ValAleatorio = 0;
		// se considera no balanceda si el número de instancias totales de la clase es un 30% de lo que corresponria si fuesen equibalanceadas
		
		final int claseObjetivo = Parametros.getInstancia_Parametros().getClaseObjetivo();

		for (int cl = 0; cl < numClases; cl++) {
			Parametros.getInstancia_Parametros().depura(" " + cl +  ":" + plantilla.contadorClases(cl) , 0);
			while (IndicesDisponibles[cl].size() > 0) {
				for (int i = 0; i < numSubConjuntos; i++) {
					if (IndicesDisponibles[cl].size() > 0) {
						Aleatorio gen_aleatorio = Parametros
								.getInstancia_Parametros()
								.get_GeneradorAleatorio();
						ValAleatorio = gen_aleatorio.Randint(0,
								IndicesDisponibles[cl].size() - 1);
						int indCromosoma = (Integer
								.parseInt(IndicesDisponibles[cl]
										.get(ValAleatorio)
										+ ""));
						
						if ((Parametros.getInstancia_Parametros().isBalanceoClases() && (cl == this.claseMinoritaria())) || cl == claseObjetivo ){// si no esta balanceada duplica la clase
							for (int j = 0; j < numSubConjuntos; j++)
								SubConjuntos[j].Insertar_Ejemplo((EjemploFuzzy)set_entrenamiento.get(indCromosoma));
						}
						else	
							SubConjuntos[i].Insertar_Ejemplo((EjemploFuzzy)set_entrenamiento.get(indCromosoma));
						
						IndicesDisponibles[cl].remove(ValAleatorio);
					}
				}
			}
		}
		return SubConjuntos;
	}


	public void Insertar_Ejemplo(EjemploFuzzy ej) {

		try {
			if (set_entrenamiento == null)
				set_entrenamiento = new ArrayList();

			set_entrenamiento.add(ej);

		} catch (Exception e) {
			System.out
					.println("Error añadiendo un nuevo Cromosoma a la poblacion");
		}
	}

	public EjemploFuzzy get_EjemploFuzzy(int ind_ejemplo) {
		return (EjemploFuzzy) set_entrenamiento.get(ind_ejemplo);
	}
	
	public void Insertar_Ejemplo_SinRepeticion(EjemploFuzzy ej) {

		try {
			if (set_entrenamiento == null)
				set_entrenamiento = new ArrayList();

			int i = 0;
			boolean encontrado = false;
			while (i < set_entrenamiento.size() && encontrado == false) {

				if (this.get_EjemploFuzzy(i).get_id() == ej.get_id())
					encontrado = true;

				i++;
			}
			if (encontrado == true)
				Parametros.getInstancia_Parametros().depura("ejemplo " + ej + " repetido",1);
			else
				set_entrenamiento.add(ej);

		} catch (Exception e) {
			System.out
					.println("Error añadiendo un nuevo Cromosoma a la poblacion");
		}
	}
}
