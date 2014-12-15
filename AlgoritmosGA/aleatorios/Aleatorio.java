package aleatorios;

/**
 * 
 * Esta clase se utiliza para generar los números aleatorios.
 *
 */
public class Aleatorio {

	/**
	 * Constructor de la clase.
	 */
	public Aleatorio(){}
	
	private long Seed = 0L;
	private long MASK=2147483647;
	private int PRIME= 65539;
	private double SCALE=0.4656612875e-9;

	/**
	 * Inicializa la semilla al valor x.
	 * Solo debe llamarse a esta función una vez en todo el programa 
	 * @param x es la semilla con la que se inicializa la clase.
	 */
	public void setRandom (long x)
	{
	    Seed = (long) x;
	}

	/**
	 * Devuelve el valor actual de la semilla
	 * @return el valor de la semilla
	 */
	public long getRandom ()
	{
	    return Seed;
	}

	/**
	 * Genera un numero aleatorio real en el intervalo [0,1]
	 * (incluyendo el 0 pero sin incluir el 1)
	 * @return un double
	 */
	public double rand()
	{
	    return (( Seed = ( (Seed * PRIME) & MASK) ) * SCALE );
	}

	/**
	 * Genera un numero aleatorio entero en {low,...,high} 
	 * @param low es el valor mínimo que puede tomar el aleatorio.
	 * @param high es el valor máximo que puede tomar el aleatorio.
	 * @return un entero.
	 */
	public int randInt(int low, int high)
	{
	    return (int) (low + (high-(low)+1) * rand());
	}

	/**
	 * Genera un numero aleatorio real en el intervalo [low,...,high]
	 * (incluyendo 'low' pero sin incluir 'high') 
	 * @param low
	 * @param high
	 * @return un float
	 */
	public float randFloat(float low, float high)
	/* 
	   */
	{
	    return (float)(low + (high-(low))*rand());
	}
	
}