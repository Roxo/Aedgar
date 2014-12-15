package NowGNET;

/**
 * Ruleta implementa una ruleta.
 * @author José Luis Toscano Muñoz
 * @version Regal v2.0
 */
public class Ruleta {

	    private double[] roul; 
	    private int pos; 

	    /**
	     * Constructor de la clase.
	     * @param num indica el número de posiciones de la ruleta.
	     */
	    public  Ruleta(int num) {        
	        roul = new double[num];
	        pos = 0;
	        for (int i=0; i<num; i++){
	        	roul[i] = 0;
	        }
	    }        
	
	    /**
	     * Añade un valor a la ruleta.
	     * @param num valor que se inserta en la ruleta.
	     */
	    public void add(double num) {        
	        if (pos == roul.length){ 
	        	System.err.println ("--> ROULETTE ERROR!! The roulette is full");
	        	return;
	        }
	        if (pos == 0) roul[pos] = num;
	        else roul[pos] = roul[pos-1] + num;
	        pos++;
	    }         

	    /**
	     * Inicializa la ruleta.
	     *
	     */
	    public void reset() {        
	        for (int i =0; i<roul.length; i++){
	        	roul[i] = 0;
	        }
	    } 
	
	    /**
	     * Lanza la ruleta y devuelve la posición selecionada. Seleciona una de las posiciones de la ruleta.
	     * @return un entero con la posición seleccionada.
	     */
	    public int selectRuleta() {        
	        if (pos == 0) return -1;
	        double aleat = ParametrosGlobales.getInstancia_Parametros().getGeneradorAleatorio().rand() * roul[pos-1];
	        boolean finish = false;
	        int i=0;
	       
	        while (i<roul.length && !finish){
	        	if (aleat <= roul[i]) finish = true;
	        	else i++;	
	        }
	        if (i== roul.length) i--;
	        return i;
	    }
	    
	    /**
	     * Imprime el contenido de la ruleta.
	     */
	    public void print() {        
	        System.out.println ("\nThe probabilities of the roulette are: ");
	        for (int i=0; i<pos; i++){
	        	System.out.println ("Position "+i+":"+ roul[i]);
	        }
	    }        	
}

