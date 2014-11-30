package edgar;


public class SeleccionRuleta implements Seleccion{

	

	/**
	 * <p>
	 * Objeto Ruleta.
	 * </p>
	 * 
	 */
	    private Ruleta roul; 
	    
	  ///////////////////////////////////////
	  // operations


	/**
	 * <p>
	 * Creates a RouletteSelection object.
	 * </p>
	 * 
	 */
	    public  SeleccionRuleta() {        
	    	roul = null;
	    } // end RouletteSelection        


	/**
	 * <p>
	 * It creates and initializes the roulette with the fitness of all
	 * classifiers in the population.
	 * </p>
	 * <p>
	 * 
	 * @param pop is the action set where the selection has to be applied.
	 * </p>
	 * <p>
	 * @see Roulette
	 * </p>
	 */
	    public void init(Solucion pop) {        
	      	int i = 0;
	  	//double lowerFitness=0;
	  	roul = new Ruleta (pop.getTama�o_solucion());      
	        for (i=0; i<pop.getTama�o_solucion(); i++){
	        	//Antigua , ya estaba comentada => roul.add(pop.get_regla(i).getfitness());
	        	// La que estaba cuando empec� a tocar el c�digo => roul.add(pop.get_regla(i).getPI());
	        	
	        	// La que yo a�ad� (JMGM)
	        	
	        	//OJO mirar get_fitness_token (el original era getPI() 
	        	//if ( Parametros.getInstancia_Parametros().isFuzzy()){
	        	// 	roul.add(pop.get_regla(i).getPIFuzzy(Ejemplo)
	        	//}
	        	roul.add(pop.get_regla(i).getPI());//vtoken  probar con *numejemplos poseidos.size 
	        	// en vez de PI , usar para Fuzzy el Ho*PI
	    }
	    } // end init        



	/**
	 * <p>
	 * Performs the roulette wheel selection
	 * </p>
	 * <p>
	 * @param pop is the population.
	 * </p>
	 * <p>
	 * @return a Classifier with the selected classifier
	 * </p>
	 */
	    public Regla SeleccionaRegla(Solucion sol) {        
	        int i = roul.selectRuleta();   
	        return sol.get_regla(i);
	    } // end Selecciona Regla       



	} // end SelecionRuleta
	
	
	

