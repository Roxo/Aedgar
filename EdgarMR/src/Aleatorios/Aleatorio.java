package Aleatorios;


public class Aleatorio
{
	private long Seed = 0L;
	private long MASK=2147483647;
	private int PRIME= 65539;
	private double SCALE=0.4656612875e-9;

	public void Set_random (long x)
	/* Inicializa la semilla al valor x.
	   Solo debe llamarse a esta funcion una vez en todo el programa */
	{
	    Seed = (long) x;
	}

	public long Get_random ()
	/* Devuelve el valor actual de la semilla */
	{
	    return Seed;
	}

	public double Rand()
	/* Genera un numero aleatorio real en el intervalo [0,1]
	   (incluyendo el 0 pero sin incluir el 1) */
	{
	    return (( Seed = ( (Seed * PRIME) & MASK) ) * SCALE );
	}

	public int Randint(int low, int high)
	/* Genera un numero aleatorio entero en {low,...,high} */
	{
	    return (int) (low + (high-(low)+1) * Rand());
	}

	public float Randfloat(float low, float high)
	/* Genera un numero aleatorio real en el intervalo [low,...,high[
	   (incluyendo 'low' pero sin incluir 'high') */
	{
	    return (float)(low + (high-(low))*Rand());
        }
        
}