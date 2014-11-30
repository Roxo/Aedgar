package edgar;

import java.util.*;
import java.lang.*;
import java.io.*;

import Aleatorios.Aleatorio;


public class Ruleta {


	/**
	 * <p>
	 * Represents the roulette. In each position it has a probability
	 * </p>
	 * 
	 */
	    private double[] roul; 

	/**
	 * <p>
	 * Represents the number of positions completed.
	 * </p>
	 * 
	 */
	    private int pos; 

	  ///////////////////////////////////////
	  // operations


	/**
	 * <p>
	 * Constructs a roulette. It inicializes all its values to 0, and the
	 * pointer to the first position. To use it, you have to update all the
	 * relative probabilities (not the sum).
	 * </p>
	 * @param num is the number of the elements in the roulette.
	 */
	    public  Ruleta(int num) {        
	        roul = new double[num];
	        pos = 0;
	        for (int i=0; i<num; i++){
	        	roul[i] = 0;
	        }
	    } // end Roulette        

	/**
	 * <p>
	 * Enter a new probability in the roulette. It has to be a relative
	 * probability and not a sum, because it performs the sum internally.
	 * </p>
	 * @param num is the new probability.
	 */
	    public void add(double num) {        
	        if (pos == roul.length){ 
	        	System.err.println ("--> ROULETTE ERROR!! The roulette is full");
	        	return;
	        }
	        if (pos == 0) roul[pos] = num;
	        else roul[pos] = roul[pos-1] + num;
	        pos++;
	    } // end add        

	/**
	 * <p>
	 * Resets the roulette (puts all the probabilities to 0).
	 * </p>
	 */
	    public void reset() {        
	        for (int i =0; i<roul.length; i++){
	        	roul[i] = 0;
	        }
	    } // end reset        

	/**
	 * <p>
	 * Selecciona una posici�n de la ruleta y la devuelve.
	 * </p>
	 * <p>
	 * 
	 * @return a int with the position selected
	 * </p>
	 */
	    public int selectRuleta() {        
	        if (pos == 0) return -1;
	        double aleat = Parametros.getInstancia_Parametros().get_GeneradorAleatorio().Rand() * roul[pos-1];
	        boolean finish = false;
	        int i=0;
	       
	        while (i<roul.length && !finish){
	        	if (aleat <= roul[i]) finish = true;
	        	else i++;	
	        }
	        if (i== roul.length) i--;
	        return i;
	    } // end selectRoulette        


	/**
	 * <p>
	 * Imprimir ruleta
	 * </p>
	 * 
	 */
	    public void print() {        
	        System.out.println ("\nThe probabilities of the roulette are: ");
	        for (int i=0; i<pos; i++){
	        	System.out.println ("Position "+i+":"+ roul[i]);
	        }
	    } // end print        



	
}// End Ruleta

