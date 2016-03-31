package edgar;

import java.util.ArrayList;
import java.util.Random;

public class Pruebas {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String divisible = "hola.";
		
		if(divisible.contains("."))
		{
			System.out.println(divisible.indexOf("--"));
			
			ArrayList atributo1 = new ArrayList();
			atributo1.add(0.2);
			atributo1.add(0.4);
			atributo1.add(0.6);
			atributo1.add(0.9);
			atributo1.add(1.0);
			atributo1.add(1.8);
			atributo1.add(3.81);
			
			ArrayList atributo2 = new ArrayList();
			atributo2.add(0.2);
			atributo2.add(0.4);
			atributo2.add(0.7);
			atributo2.add(1.0);
			atributo2.add(1.4);
			atributo2.add(1.5);
			atributo2.add(3.81);
			
			ArrayList atributo3 = new ArrayList();
			atributo3.add(0.2);
			atributo3.add(1.2);
			atributo3.add(1.6);
			atributo3.add(2.0);
			atributo3.add(2.4);
			atributo3.add(3.1);
			atributo3.add(3.81);
			
			ArrayList[] individuo = new ArrayList[3];
			
			individuo[0] = atributo1;
			individuo[1] = atributo2;
			individuo[2] = atributo3;
			
			ArrayList[] individuo1 = new ArrayList[3];
			
			individuo1[0] = atributo2;
			individuo1[1] = atributo3;
			individuo1[2] = atributo1;
			
			Random al = new Random(526896528);
			
			Pruebas prueba= new Pruebas();
			
			ArrayList[] individuo2 = prueba.blxAlpha(individuo, individuo1, al);
			
			int dif = prueba.hamming(individuo, individuo1);
			
			System.out.println("La distancia es " + dif);
			
			ArrayList[][] poblacion = prueba.generaPoblacionInicial(individuo, al);
			String parte0 = divisible.substring(divisible.indexOf(".")+1);
			
			System.out.println(parte0);
			
			System.out.println("adiossss");
		}
		
		
		//System.out.println("Parte 0:" + partes[0]);
		//System.out.println("Parte 1:" + partes[1]);
	}
	
	/**
	 * Operador de cruce BLX-alpha
	 * @param ArrayList madre
	 * @param ArrayList padre
	 * @return ArrayList hijo
	 */
	private ArrayList[] blxAlpha(ArrayList[] madre, ArrayList[] padre, Random al)
	{
		int numAtributos = madre.length;
		double max = 0;
		double min = 0;
		double I = 0;
		ArrayList[] hijo = new ArrayList[numAtributos];
		
		
		for (int i = 0; i < numAtributos; i++)		//Para cada atributo
		{
			hijo[i] = new ArrayList();
			
			// El primer y el último valor de cada atributo no cambia
			hijo[i].add(madre[i].get(0));
			
			int numParticiones = madre[i].size();
			
			for (int j = 1; j < numParticiones-1;j++)
			{
				if ((Double)(madre[i].get(j)) < (Double)(padre[i].get(j)))
				{	
					max = (Double)(padre[i].get(j));
					min = (Double)(madre[i].get(j));
				}
				else							//Si el gen de la madre es mayor
				{
					min = (Double)(padre[i].get(j));
					max = (Double)(madre[i].get(j));
				}					 
				I = max - min;	//Obtenemos la diferencia
				min = min - I * 0.15;	//Calculamos el extremo inferior
				max = max + I * 0.15;	//Calculamos el extremo superior
				hijo[i].add(min + al.nextDouble() * (max - min));	//Calculamos un punto aleatorio dentro del intervalo
			}
			
			// El primer y el último valor de cada atributo no cambia
			hijo[i].add(madre[i].get(numParticiones-1));
		}
		
		// Validación del individuo
		
		// Ponemos a mínimo los valores menores que el mínimo y al máximo los valores menores que el máximo
		for(int i=0;i < numAtributos; i++)
		{
			int numParticiones = hijo[i].size();
			
			min = ((Double)(hijo[i].get(0)));
			max = ((Double)(hijo[i].get(numParticiones-1)));
			
			for (int j = 1; j < numParticiones-1;j++)
			{
				if(((Double)(hijo[i].get(j))) < min)
				{
					hijo[i].add(j, min);
				}
				else if((((Double)(hijo[i].get(j))) > max))
				{
					hijo[i].add(j, max);
				}
			}
		}
		
		// Ordenamos los valores de menor a mayor
		for(int i=0;i < numAtributos; i++)
		{
			int numParticiones = hijo[i].size();
			
			for (int j = 1; j < numParticiones-2;j++)
			{
				for(int k = 1; k < numParticiones-2; k++)
				{
					double temp = ((Double)(hijo[i].get(k)));
					double compara = ((Double)(hijo[i].get(k+1)));
					
					if(temp > compara)
					{
						hijo[i].remove(k);
						hijo[i].add(k, compara);
						hijo[i].remove(k+1);
						hijo[i].add(k+1, temp);
					}
				}
			}
		}
		
		return hijo;	
	}
	
	/**
	 * Distancia de Hamming entre 2 padres. Devuelve el número de valores diferentes que tienen
	 * dos padres
	 * @param ArrayList[] madre
	 * @param ArrayList[] padre
	 * @return
	 */
	private int hamming(ArrayList[] madre, ArrayList[] padre)
	{
		double valor = 0;
		int diferentes = 0;
		
		int numAtributos = madre.length;
		
		// Comparamos los valores ae las particiones de cada atributo
		for (int i = 0; i<numAtributos; i++)	//Para cada variable
		{
			int numParticiones = madre[i].size();
			//La tolerancia es proporcinal al ancho del intervalo (ahora está puesto al 1%)
			double tolerancia = ((Double)(madre[i].get(numParticiones-1))) - ((Double)(madre[i].get(0))) * 0.01;
			
			// Comprobamos todos los valores menos el primero y el último, que son fijos
			for(int j=1; j<numParticiones-1; j++)
			{
				double vMadre = (Double) madre[i].get(j);	//Tomamos la variable de la madre
				double vPadre = (Double) padre[i].get(j);	//Tomamos la variable del padre

				valor = Math.abs(vMadre - vPadre);  //Calculamos la distancia entre los alelos
					if (valor > tolerancia)					//Si es mayor que la tolerancia es que son diferentes.
						diferentes++;
			}
		}
		return diferentes;
	}
	
	public ArrayList[][] generaPoblacionInicial(ArrayList[] individuo, Random al)
	{
		ArrayList[][] poblacion = new ArrayList[30][];
		
		poblacion[0] = individuo;
		
		int num_atributos = individuo.length;
		
		for(int i=1;i<30;i++)
		{
			poblacion[i] = new ArrayList[num_atributos];
			
			for(int j=0;j<num_atributos;j++)
			{
				ArrayList atributo = individuo[j];
				
				int num_particiones = atributo.size();
				
				
				poblacion[i][j] = new ArrayList();
				poblacion[i][j].add(individuo[j].get(0));
				
				for(int z=1;z<num_particiones-1;z++)
				{
					double porcentaje = al.nextDouble() * 0.15;
					double mayor_o_menor = al.nextDouble();
					
					double valor_nuevo;
					
					if(mayor_o_menor < 0.5)
						valor_nuevo = ((Double)(atributo.get(z)) * (1+porcentaje));
					else
						valor_nuevo = ((Double)(atributo.get(z)) * (1-porcentaje));
					
					
					double valor_anterior = ((Double)(poblacion[i][j].get(z-1)));
					if(valor_nuevo < valor_anterior)
						poblacion[i][j].add(valor_nuevo);
					else
						poblacion[i][j].add(valor_nuevo);
							
				}
				
				poblacion[i][j].add(individuo[j].get(num_particiones-1));
			}
		}
		
		return poblacion;
	}

}
