package edgar;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Dataset.Attribute;


/**
 * @author miguelangelwww
 *
 */
public class CHCOptimizarParticiones {
	
	private Dataset ejemplos;
	
	private int tamPoblacion = 24;
	private Solucion concepto;
	private int[] tipos_atributos;
	private ArrayList[] valores_atributos;
	private double mejorEncontrado;
	private Random al;
	private int num_atributos;
	private ArrayList mejor_individuo;
	
	// JMGM
	// El tipoGeneracion vale 1 indica que los nuevos individuos se generarán como pequeñas variaciones 
	// del mejor. Si se pone cualquier otro valor, se generarán de forma aleatoria
	private int tipoGeneracion = 1;

	// cambiar a una décima parte del blx alpha
	private double variacionExtremos = 0.1;
	
	private double valTolerancia = variacionExtremos / 10;
	// Necesitamos 
	//private double mejorFitnessEncontrado = 0;
	
	private ArrayList poblacion;
	private ArrayList<Double> fitnessPoblacion; 
	
	private ArrayList[] particion;
	private ArrayList[] particionActual;
	
	private int umbral;
	
	// Si tipoHijo vale 1 devolveremos O1, si vale 2, devolveremos O2
	// Daniel Albendín Eliminamos la variable tipoHijo.
	private ArrayList[] pcBlxAlpha(ArrayList[] madre, ArrayList[] padre, Random al)
	{
		int numAtributos = madre.length;
		double max = 0;
		double min = 0;
		double I = 0;
		ArrayList[] hijo = new ArrayList[numAtributos];
		
		
		for (int i = 0; i < numAtributos; i++)		//Para cada atributo
		{
			// Será null cuando sea un atributo nominal y no nos interese
			if(tipos_atributos[i] != Attribute.NOMINAL)
			{
				hijo[i] = new ArrayList();
				
				// El primer y el último valor de cada atributo no cambia
				hijo[i].add(madre[i].get(0));
				
				int numParticiones = madre[i].size();
				
				for (int j = 1; j < numParticiones-1;j++)
				{
					double x = (Double)(padre[i].get(j));
					double y = (Double)(madre[i].get(j));
					double valorI = Math.abs(x-y) * variacionExtremos;
					
					double tempA = (Double)(madre[i].get(0));
					
					double tempResta;
// <-- Daniel Albendín Cambiado tipo hijo por un valor random porque antes siempre cogía el mismo padre como origen de todo los puntos (Variable tipo hijo
		//	ANTES	if(tipoHijo == 1)
					if(al.nextDouble() > 0.5)
						tempResta = x - valorI;
					else
						tempResta = y - valorI;
					
					double ele1;
					if(tempA < tempResta)
					{
						ele1 = tempResta;
					}
					else
					{
						ele1 = tempA;
					}
					
					double tempB = (Double)(madre[i].get(numParticiones-1));
					
					double tempSuma;
					// <-- Daniel Albendín Cambiado tipo hijo por un valor random porque antes siempre cogía el mismo padre como origen de todo los puntos (Variable tipo hijo
					//	ANTES	if(tipoHijo == 1)
					if(al.nextDouble() > 0.5)
						tempSuma = x + valorI;
					else
						tempSuma = y + valorI;
					
					double u1;
					if(tempB < tempSuma)
					{
						u1 = tempB;
					}
					else
					{
						u1 = tempSuma;
					}
					
					double dif = Math.abs(u1 - ele1);
					
					double aleatorio = al.nextDouble() * dif;
					
					double puntoBueno;
					
					if(u1 < ele1)
					{
						puntoBueno = u1 + aleatorio;
					}
					else
					{
						puntoBueno = ele1 + aleatorio;
					}
					
					hijo[i].add(puntoBueno);
				}
				
				// El primer y el último valor de cada atributo no cambia
				hijo[i].add(madre[i].get(numParticiones-1));
			}
			/**
			 * @author Danien Albendín
			 * No se hacía nada cuando el atributo era nominal
			 */
			else{
				hijo[i] = new ArrayList();
				int num = madre[i].size();
				for(int s = 0; s < num; s++){
					hijo[i].add(madre[i].get(s));
				}
			}
		}
		
		// Validación del individuo
		
		// Ponemos a mínimo los valores menores que el mínimo y al máximo los valores menores que el máximo
		for(int i=0;i < numAtributos; i++)
		{
			if(tipos_atributos[i] != Attribute.NOMINAL)
			{
				int numParticiones = hijo[i].size();
				
				min = ((Double)(hijo[i].get(0)));
				max = ((Double)(hijo[i].get(numParticiones-1)));
				
				for (int j = 1; j < numParticiones-1;j++)
				{
					if(((Double)(hijo[i].get(j))) < min)
					{
						hijo[i].remove(j);
						hijo[i].add(j, min);
					}
					else if((((Double)(hijo[i].get(j))) > max))
					{
						hijo[i].remove(j);
						hijo[i].add(j, max);
					}
				}
			}
		}
		
		// Ordenamos los valores de menor a mayor
		for(int i=0;i < numAtributos; i++)
		{
			if(tipos_atributos[i] != Attribute.NOMINAL)
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
		}
		
		return hijo;	
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
			// Será null cuando sea un atributo nominal y no nos interese
			if(tipos_atributos[i] != Attribute.NOMINAL)
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
					I = Math.abs(max - min);	//Obtenemos la diferencia
					min = min - (I * variacionExtremos);	//Calculamos el extremo inferior
					max = max + (I * variacionExtremos);	//Calculamos el extremo superior
					hijo[i].add(min + al.nextDouble() * (max - min));	//Calculamos un punto aleatorio dentro del intervalo
				}
				
				// El primer y el último valor de cada atributo no cambia
				hijo[i].add(madre[i].get(numParticiones-1));
			}			
			/**
			 * @author Danien Albendín
			 *  No se hacía nada cuando el atributo era nominal
			 */
			else{
				hijo[i] = new ArrayList();
				int num = madre[i].size();
				for(int s = 0; s < num; s++){
					hijo[i].add(madre[i].get(s));
				}
			}

		}
		
		// Validación del individuo
		
		// Ponemos a mínimo los valores menores que el mínimo y al máximo los valores menores que el máximo
		for(int i=0;i < numAtributos; i++)
		{
			if(tipos_atributos[i] != Attribute.NOMINAL)
			{
				int numParticiones = hijo[i].size();
				
				min = ((Double)(hijo[i].get(0)));
				max = ((Double)(hijo[i].get(numParticiones-1)));
				
				for (int j = 1; j < numParticiones-1;j++)
				{
					if(((Double)(hijo[i].get(j))) < min)
					{
						hijo[i].remove(j);
						hijo[i].add(j, min);
					}
					else if((((Double)(hijo[i].get(j))) > max))
					{
						hijo[i].remove(j);
						hijo[i].add(j, max);
					}
				}
			}
		}
		
		// Ordenamos los valores de menor a mayor
		for(int i=0;i < numAtributos; i++)
		{
			if(tipos_atributos[i] != Attribute.NOMINAL)
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
			// Será null cuando sea un atributo nominal, que no nos interesa
			if(tipos_atributos[i] != Attribute.NOMINAL)
			{
				int numParticiones = madre[i].size();
				//La tolerancia es proporcinal al ancho del intervalo
				double tolerancia = ( ((Double)(madre[i].get(numParticiones-1))) - ((Double)(madre[i].get(0))) ) * valTolerancia;
				
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
		}
		return diferentes;
	}
	
	// tipoGeneracion valdrá 1 si se utiliza un número cercano al del mejor individuo
	// En cualquier otro caso, se generará de forma aleatoria
	public ArrayList generaPoblacionInicial(ArrayList[] mejorIndividuo, Random al, int[] tiposAtributos)
	{
		// Versión 1
		
		// Versión 2 => blx alpha pequeño que va por parámetro (0.01) y que la población incial sea muy parecida al individuo que se quiere optimizar (0.01)
		// optimizará muy levemente las etiquetas
		poblacion = new ArrayList();
		fitnessPoblacion = new ArrayList<Double>();
		poblacion.add(mejorIndividuo);
		mejorEncontrado = porcentajeClasificadorGM(mejorIndividuo);
		fitnessPoblacion.add(mejorEncontrado);
		
		//int numParticiones = Parametros.getInstancia_Parametros().get_num_particiones();

		int numAtributos = mejorIndividuo.length;
		
		for(int i=1;i<tamPoblacion;i++)
		{				
			ArrayList[] individuo = new ArrayList[numAtributos];
			
			for(int j=0;j<numAtributos;j++)
			{
				if(!(tipos_atributos[j] == Attribute.NOMINAL))
				{
					int numParticiones = mejorIndividuo[j].size();
					double min = (Double) mejorIndividuo[j].get(0);
					double max = (Double) mejorIndividuo[j].get(numParticiones-1);
					
					double valoresAtributo[] = new double[numParticiones];
					double amplitud = max - min;
					
					valoresAtributo[0] = min;
					valoresAtributo[numParticiones - 1] = max;
					
					for(int z=1;z<numParticiones-1;z++)
					{
						double nuevo;
						
						// Si vale 1, se generará cada individuo como una mínima variación del mejor hasta ahora
						if(tipoGeneracion == 1)
						{
							if(al.nextDouble() > 0.5)
							{
								nuevo = (Double) mejorIndividuo[j].get(z) + (amplitud * valTolerancia * (1 + al.nextDouble()));
							}
							
							//JMGM Esto estaba puesto a + también (FALLO)
							// tema de la amplitud
							else
					            nuevo = (Double) mejorIndividuo[j].get(z) - (amplitud * valTolerancia * (1 + al.nextDouble()));
						}
						// En cualquier otro caso, se generará de forma aleatoria
						else
							nuevo = valoresAtributo[z] = min + (amplitud * al.nextDouble());
						
						valoresAtributo[z] = nuevo;
					}
					
					// Se ordenan los valores de mayor a menor
					for (int h = 1; h < numParticiones-2;h++)
					{
						for(int k = 1; k < numParticiones-2; k++)
						{
							double temp = valoresAtributo[k];
							double compara = valoresAtributo[k+1];
							
							if(temp > compara)
							{
								valoresAtributo[k] = compara;
								valoresAtributo[k+1] = temp;
							}
						}
					}
					ArrayList atributo = new ArrayList();
					
					for(int h=0;h<numParticiones;h++)
					{
						atributo.add(valoresAtributo[h]);
					}
					
					individuo[j] = atributo;
				}
				/**
				 * @author Danien Albendín
				 * 
				 */
				else{
					int numParticiones = mejorIndividuo[j].size();
					ArrayList atributo = new ArrayList();

					for(int h=0;h<numParticiones;h++)
					{
						atributo.add(Integer.parseInt(""+mejorIndividuo[j].get(h)));
					}
					
					individuo[j] = atributo;
				
				}
			}
			poblacion.add(individuo);
			fitnessPoblacion.add(porcentajeClasificadorGM(individuo));
		}
		return poblacion;
	}
	
	
	
	
	
	public CHCOptimizarParticiones(Dataset _ejemplos, Solucion _concepto)
	{		
		this.ejemplos = _ejemplos;
		this.concepto = _concepto;
		al = new Random(Parametros.getInstancia_Parametros().get_Semilla());
		

		tipos_atributos = ((Regla)(concepto.getReglas().get(0))).plantilla.get_TiposAtributos();
		valores_atributos = ((Regla)(concepto.getReglas().get(0))).plantilla.get_ValoresAtributos();
		
		
		int numAtributosNumericos =  ((Regla)(concepto.getReglas().get(0))).getPlantilla().numeroAtributosNumericos() ;
		
		int numAtributos = tipos_atributos.length;
		
	
		generaPoblacionInicial(valores_atributos, al, tipos_atributos);
		
		//if(tipos_atributos.length > 10)
		   
		int tam = 0;
		for(int j=0;j<numAtributos;j++)
		{
			tam += valores_atributos[j].size();
		}
			
		if(tam < 5)
		   this.umbral = tam - 1;
		else
			this.umbral = tam / 4;
	}

/*
	public double getFitness(ArrayList[] particiones)
	{
		((Regla)(concepto.getReglas().get(0))).plantilla.set_ValoresAtributos(particiones);
		int[][] ResultadoClasificaciontra = concepto.Clasificar(ejemplos);
		int cont_fallos = 0;
		for (int i = 0; i < ResultadoClasificaciontra.length; i++) {
			if (ResultadoClasificaciontra[i][0] != ResultadoClasificaciontra[i][1])
				cont_fallos++;
		}
		int numAciertosTra = ResultadoClasificaciontra.length - cont_fallos;
		return (numAciertosTra / (double) ResultadoClasificaciontra.length);
	}
*/
	
	public double  porcentajeClasificadorGM(ArrayList[] particiones)
	{

		Plantilla plantillaOriginal = ((Regla)(concepto.getReglas().get(0))).plantilla;
		
		Plantilla plan = new Plantilla(plantillaOriginal, particiones);
		
		
		//((Regla)(concepto.getReglas().get(0))).plantilla.set_ValoresAtributos(particiones);
		
		//int[][] ResultadoClasificaciontra =concepto.Clasificar(ejemplos, plantilla);
		int[][] ResultadoClasificaciontra =concepto.Clasificar(ejemplos, plan);
		double aciertos_clase_1 = 0;
		double aciertos_clase_2 = 0;
		double num_total_clase_1 = 0;
		double num_total_clase_2 = 0;
		for (int i = 0; i < ResultadoClasificaciontra.length; i++) 
		{
			if (ResultadoClasificaciontra[i][0] == 0)
			{
				num_total_clase_1++;
				
				if(ResultadoClasificaciontra[i][0] == ResultadoClasificaciontra[i][1])
					aciertos_clase_1++;
			}
			else
			{
				num_total_clase_2++;
				
				if(ResultadoClasificaciontra[i][0] == ResultadoClasificaciontra[i][1])
					aciertos_clase_2++;
			}
		}
		Double dev = Math.sqrt((aciertos_clase_1/num_total_clase_1)*(aciertos_clase_2/num_total_clase_2));
		return dev;
	}

	public ArrayList[] ejecutar()
	{			
		int reinicios = 0;
		
		
		particion = valores_atributos;
		//double fitnessSolucion = porcentajeClasificadorGM(valores_atributos);
		//double fitnessActual = 0;
 		
		//int iteracionesEjecutadas = 0;
		
		int iteracionesTotales = 0;
		int iteracionesMaximas = 10;
		
		int iteracionesSinCambios = 0;
		
		// JMGM 2/3/2012
		// JMGM => cambiar a iteracionesSinCambios < iteracionesMaximas para dejar como el original (el CHC pesado)
		// JMGM => y poner iteracionesMaximas a 10
		while (iteracionesSinCambios < iteracionesMaximas)
		{
			//iteracionesEjecutadas++;
			
			iteracionesTotales++;
			ArrayList c = seleccionar();
			ArrayList cPrima = generarHijos(c);
			
			
			boolean seguir = true;
			int numCambios = 0;
			
			if(cPrima.size() == 0)
				seguir = false;
			
			while(seguir)
			{
				ArrayList[] peorDeLaPoblacion = damePeor();
				ArrayList<Double> cPrimaFitness = new ArrayList<Double>();
				ArrayList[] mejorHijo = mejorIndividuo(cPrima, cPrimaFitness); 
				// Devuelve el mejor hijo y lo elimina del ArrayList cPrima, además mete en cPrimaFitness los fitness de todos los que quedan en cPrima
				
				double fitnessPeor = damePeorFitness();
				
				int tamCPrimaFiness = cPrimaFitness.size();
				
				double fitnessMejor = Double.MIN_VALUE;
				//double mejorIndividuo = -1;
				
				for(int x=0;x<tamCPrimaFiness;x++)
				{
					double temp = cPrimaFitness.get(x);
					if(fitnessMejor < temp)
					{
						fitnessMejor = temp;
						//mejorIndividuo = x;
					}
				}
				
				if(fitnessMejor > fitnessPeor)
				{
					poblacion.remove(peorDeLaPoblacion);
					fitnessPoblacion.remove(fitnessPeor);
					
					poblacion.add(mejorHijo);
					fitnessPoblacion.add(fitnessMejor);
					
					//cPrima.remove(mejorHijo); // No hace falta eliminarlo, ya lo hace el método que devuelve el mejor antes
					cPrimaFitness.remove(fitnessMejor);
					numCambios++;
				}
				else
				{
					seguir = false;
				}
			}
		    
		    if (numCambios == 0)
		   	{
		    	iteracionesSinCambios++;
		    	umbral--;
		   	}
		    
		    if (umbral <= 0)
		    {
			   this.poblacion = reiniciaPoblacion();
			 
			   int numAtributos = valores_atributos.length;
			   
				int tam = 0;
				for(int j=0;j<numAtributos;j++)
				{
					tam += valores_atributos[j].size();
				}
					
				if(tam < 5)
				   this.umbral = tam - 1;
				else
					this.umbral = tam / 4;
			   
			   reinicios++;
		   	   System.out.println("	  --> POBLACION REINICIALIZADA: " + reinicios + " veces.");
		    }
		    
		    mejorIndividuoSinEliminar(poblacion, fitnessPoblacion);
		    
		    /*if(iteracionesTotales%5 == 0)
		    {
		    	//mejorIndividuoSinEliminar(poblacion);
		    	//System.out.println("Vamos por la generación " + iteracionesTotales);
		    }
		    */
		}
		
 		//System.out.println("	  --> POBLACION REINICIALIZADA: " + reinicios + " VECES <--");
		
 		ArrayList[] devolver = mejorIndividuoSinEliminar(poblacion, fitnessPoblacion);
 		
 		// Los valores de los atributos nominales se han perdido, los vamos a recuperar
 		int longitud = devolver.length;
 		
 		for(int i=0;i<longitud;i++)
 		{
 			if(tipos_atributos[i] == Attribute.NOMINAL)
 			{
 				devolver[i] = valores_atributos[i];
 			}
 		}
 		return devolver;
	}
	
	private ArrayList reiniciaPoblacion() 
	{
		ArrayList[] mejorIndividuo = mejorIndividuo(poblacion, fitnessPoblacion);
		
		ArrayList nuevaPoblacion = new ArrayList();
		
		nuevaPoblacion.add(mejorIndividuo);
		
		//int numParticiones = Parametros.getInstancia_Parametros().get_num_particiones();
		int numAtributos = mejorIndividuo.length;
		
		for(int i=1;i<tamPoblacion;i++)
		{
			ArrayList[] individuo = new ArrayList[numAtributos];
			
			for(int j=0;j<numAtributos;j++)
			{
				if(mejorIndividuo[j] != null)
				{
					int numParticiones = mejorIndividuo[j].size();
					double min = (Double) mejorIndividuo[j].get(0);
					double max = (Double) mejorIndividuo[j].get(numParticiones-1);
					
					double valoresAtributo[] = new double[numParticiones];
					double amplitud = max - min;
					
					valoresAtributo[0] = min;
					valoresAtributo[numParticiones - 1] = max;
					
					for(int z=1;z<numParticiones-1;z++)
					{
						double nuevo;
						// Si vale 1, se generará cada individuo como una mínima variación del mejor hasta ahora
						if(tipoGeneracion == 1)
						{
							//JMGM - SUMA O QUITA COMO MUCHO UN VALOR ABSOLUTO (AHORA MISMO 0.1) ¿NO SERÍA MÁS LÓGICO CAMBIARLO POR EL 0.1 % DE LA AMPLITUD DEL ATRIBUTO?
							// TAMBIÉN TENÍA EN LOS DOS UN +
							if(al.nextDouble() > 0.5)
							{
								nuevo = (Double) mejorIndividuo[j].get(z) + (amplitud * valTolerancia * (1 + al.nextDouble()));
							}
							else
					            nuevo = (Double) mejorIndividuo[j].get(z) - (amplitud * valTolerancia * (1 + al.nextDouble()));
						}
						// En cualquier otro caso, se generará de forma aleatoria
						else
							nuevo = valoresAtributo[z] = min + (amplitud * al.nextDouble());
						
						
						valoresAtributo[z] = nuevo;
					}
					
					
					
					
					for (int h = 1; h < numParticiones-2;h++)
					{
						for(int k = 1; k < numParticiones-2; k++)
						{
							double temp = valoresAtributo[k];
							double compara = valoresAtributo[k+1];
							
							if(temp > compara)
							{
								valoresAtributo[k] = compara;
								valoresAtributo[k+1] = temp;
							}
						}
					}
					ArrayList atributo = new ArrayList();
					
					for(int h=0;h<numParticiones;h++)
					{
						atributo.add(valoresAtributo[h]);
					}
					
					individuo[j] = atributo;
				}
			}
			nuevaPoblacion.add(individuo);
		}
		return nuevaPoblacion;
	}

	private ArrayList[] mejorIndividuo(ArrayList cPrima, ArrayList cPrimaFitness) 
	{
		ArrayList[] devolver = null;

		int num = cPrima.size();
		
		if(num > 0)
		{
			double mejorFitness = Double.MIN_VALUE;
			int mejorIndividuo = 0;
			
			for(int i=0;i<num;i++)
			{
				double fitness = porcentajeClasificadorGM((ArrayList[]) cPrima.get(i));
				cPrimaFitness.add(fitness);
				
				if(fitness > mejorFitness)
				{
					mejorFitness = fitness;
					mejorIndividuo = i;
				}
			}
			devolver = (ArrayList[]) cPrima.get(mejorIndividuo);
			
			cPrima.remove(devolver);
			cPrimaFitness.remove(mejorIndividuo);
		}
		
		return devolver;
	}
	
	private ArrayList[] mejorIndividuoSinEliminar(ArrayList cPrima, ArrayList fitnessPoblacion) 
	{
		ArrayList[] devolver = null;
		
		double mejorFitness = Double.MIN_VALUE;

		int num = cPrima.size();
		
		if(num > 0)
		{
			int mejorIndividuo = 0;
			
			for(int i=0;i<num;i++)
			{
				double fitness = (Double) fitnessPoblacion.get(i);
		
				if(fitness > mejorFitness)
				{
					mejorFitness = fitness;
					mejorIndividuo = i;
				}
			}
			devolver = (ArrayList[]) cPrima.get(mejorIndividuo);
			
			
		}
		
		if(mejorEncontrado < mejorFitness)
		{
			System.out.println("Fitness mejorado: " + mejorFitness);
			mejorEncontrado = mejorFitness;
		}
		
		//System.out.println("Mejor fitness: " + mejorFitness);
		return devolver;
	}

	private ArrayList[] damePeor() {

		int num = poblacion.size();
		
		double peorFitness = Double.MAX_VALUE;
		int peorIndividuo = 0;
		
		for(int i=0;i<num;i++)
		{
			double fitness = (Double) fitnessPoblacion.get(i);
			
			if(fitness < peorFitness)
			{
				peorFitness = fitness;
				peorIndividuo = i;
			}
		}
		return (ArrayList[]) poblacion.get(peorIndividuo);
	}

	private double damePeorFitness() {

		int num = poblacion.size();
		
		double peorFitness = Double.MAX_VALUE;
		//int peorIndividuo = 0;
		
		for(int i=0;i<num;i++)
		{
			double fitness = (Double) fitnessPoblacion.get(i);
			
			if(fitness < peorFitness)
			{
				peorFitness = fitness;
				//peorIndividuo = i;
			}
		}
		return peorFitness;
	}

	private ArrayList seleccionar() 
	{
		/*
		 * VERSIÓN ANTERIOR
		 
		
		ArrayList padres = new ArrayList();
		
		for(int i=0;i<tamPoblacion;i++)
		{
			double mejorFitness = Double.MIN_VALUE;
			int mejorPadre = 0;
			
			for(int j=0;j<5;j++)
			{
				int num = al.nextInt(tamPoblacion);
				double fitnessNum = getFitness(poblacion[num]);
				
				if(fitnessNum > mejorFitness)
				{
					mejorFitness = fitnessNum;
					mejorPadre = j;
				}
			}
			
			padres.add(poblacion[mejorPadre]);
		}
		
		return padres;
		*/
		
		ArrayList auxiliar = new ArrayList();
		for(int i=0;i<tamPoblacion;i++)
		{
			auxiliar.add(poblacion.get(i));
		}
		
		ArrayList C = new ArrayList();
		
		for(int i=0;i<tamPoblacion;i++)
		{
			int num = al.nextInt(auxiliar.size());
			C.add(auxiliar.get(num));
			auxiliar.remove(num);
		}
		return C;
	}
	
	/**
	 * Este método implementa el cruce BLX-alpha para una población de Padres ya emparejados. 
	 * @param int generacion
	 * @param List<double> poblacion Padres
	 * @return List<Individuo> lista de hijos generados.
	 */
	public ArrayList generarHijos(ArrayList padres)
	{	
		//boolean decrementarUmbral = true;
		ArrayList nuevaPoblacion = new ArrayList();
		
		for (int i = 0; i < tamPoblacion; i+=2)
		{
			ArrayList[] madre = (ArrayList[]) padres.get(i);
			ArrayList[] padre = (ArrayList[]) padres.get(i+1);
			int h = hamming(madre,padre); //Calculamos la distancia de HAMMING
			if (h > this.umbral)	//Si es mayor que el umbral, cruzamos los padres
			{
				//decrementarUmbral = false;
				
				// CÓDIGO PARA PCBLXALPHA
				ArrayList[] hijo1 = pcBlxAlpha(madre,padre,al);
				nuevaPoblacion.add(hijo1);
				
				ArrayList[] hijo2 = pcBlxAlpha(madre,padre,al);
				nuevaPoblacion.add(hijo2);
				
				
				// CÓDIGO PARA BLXALPHA
				/*ArrayList[] hijo1 = blxAlpha(madre,padre,al);
				nuevaPoblacion.add(hijo1);
				
				ArrayList[] hijo2 = blxAlpha(madre,padre,al);
				nuevaPoblacion.add(hijo2);
				*/
			}
			
			/*
			else
			{
				padres.remove(i);
				padres.remove(i+1);
				total = total-2;
				i = i-2;
			}
			*/
		}
		
		/*
		if(decrementarUmbral)
		{
			umbral--;
		}
		*/
		
		return nuevaPoblacion;
	}
}
