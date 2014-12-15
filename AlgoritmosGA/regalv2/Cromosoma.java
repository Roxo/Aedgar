package regalv2;
/**
 * <p>Cromosoma define la codificaci�n de los individuos, 
 * e implementa los m�todos necesarios para su utilizaci�n.</p>
 * 
 * @author Jos� Luis Toscano Mu�oz
 * @version Regal v2.0
 */

public class Cromosoma {

	/**
	 * Cadena de caracters con la que se codifica cada individuo
	 */
	protected char[] cromosoma;
	
	/**
	 * Clase de individuo.
	 */
	protected int clase;
	
	/**
	 * N�mero de atributos que forman el cromosoma.
	 */
	protected int numAtributos;
	
	
	/**
	 * Constructor de la clase.
	 */
	public Cromosoma(){
		ParametrosGlobales parm=ParametrosGlobales.getInstancia_Parametros();
		numAtributos=parm.getPlantillaAtributos().length;
		int numeroBits=0;	
		for(int i=0;i<numAtributos;i++) numeroBits=numeroBits+parm.getPlantillaAtributos()[i][0];
		cromosoma=new char[numeroBits];
		for(int i=0;i<numeroBits;i++) cromosoma[i]='0';	
	}
	
	/**
	 * Devuelve el n�mero de atributos que forman el cromosoma.
	 * @return el n�mero de atributos que forman el cromosoma.
	 */
	public int getNumAtributos(){
		return numAtributos;
	}
	
	/**
	 * Devuelve la longitud del cromosoma.
	 * @return el tama�o del cromosoma.
	 */
	public int getLongitudCromosoma(){
		return cromosoma.length;
	}
	
	/**
	 * Devuelve la clase del cromosoma.
	 * @return clase del cromosoma.
	 */
	public int getClase(){
		return clase;
	}
	
	/**
	 * Modifica el valor de una posici�n del cromosoma.
	 * @param ind es la posici�n del cromosoma a modificar.
	 * @param valor es el valor que se va a asignar. 
	 */
	public void setValor(int ind, char valor){
		if ((valor!='0') && (valor!='1')) valor='0'; 
		cromosoma[ind]=valor;
	}
	
	/**
	 * Modifica la clase del cromosoma. 
	 * @param _clase es el valor que se va a asignar a la clase.
	 */
	public void setClase(int _clase){
		clase=_clase;
	}
	
	/**
	 * Devuelve una tabla de caracteres con la codificaci�n del cromosoma.
	 * @return una tabla de caracteres con los valores del cromosoma. 
	 */
	public char[] getValorCromosoma(){
		return cromosoma;
	}
	
	/**
	 * Devuelve el valor que tiene una posici�n del cromosoma.
	 * @param ind es la posici�n del cromosoma 
	 * @return un char con el valor que hay en la posici�n indicada.
	 */
	public char getValor(int ind){
		return cromosoma[ind];
	}
	
	/**
	 * Copia la codificaci�n de un cromosoma.
	 * @param _Cromosoma es el cromosoma que de va a copiar.
	 */
	public void setValorCromosoma(char _Cromosoma[]){
		for(int i=0;i<_Cromosoma.length;i++)
		cromosoma[i]=_Cromosoma[i];
	}
	
	/**
	 * Devuelve un booleano indicando si el cromosoma que le pasamos como par�metro es igual.
	 * @param cromosomaComparar es el cromosoma que se va a comparar.
	 * @return un booleano indicando sin son iguales o no.
	 */
	public boolean igual(Cromosoma cromosomaComparar){
		boolean igual=true;
		try{
			if(cromosomaComparar.getClase()!=clase) igual=false;	
		
		
		if(igual){
			char aux_cromosoma[]=cromosomaComparar.getValorCromosoma();
			int i=0;
			while((igual)&&(i<aux_cromosoma.length)){
				if(aux_cromosoma[i]!=cromosoma[i]) igual=false;
				i++;
				}
			}
		}catch(Exception e){
			igual=false;
		}
		return igual;
	}
}
