package edgar;

public class Cromosoma {

	protected char[] cromosoma;
	protected int clase;
	protected int numAtributos;
	protected Plantilla plantilla;
	
	
	public Cromosoma(){
				this(Parametros.getInstancia_Parametros().getPlantilla());
	}
	public Cromosoma(Plantilla _plantilla){
		
		plantilla = _plantilla;
		numAtributos=plantilla.numAtributos();
		int numeroBits=0;	
		for(int i=0;i<numAtributos;i++) numeroBits=numeroBits+plantilla.numValoresAtributo(i);
		cromosoma=new char[numeroBits];
		for(int i=0;i<numeroBits;i++) cromosoma[i]='0';	
	}	
	public int getNumAtributos(){
		return numAtributos;
	}
	
	public int getLongitudCromosoma(){
		return cromosoma.length;
	}
	public int getClase(){
		return clase;
	}
	
	public void setValor(int ind, char valor){
		if ((valor!='0') && (valor!='1')) valor='0'; 
		cromosoma[ind]=valor;
	}
	
	public void setClase(int _clase){
		clase=_clase;
	}
	
	public char[] getValorCromosoma(){
		return cromosoma;
	}
	
	public String toString(){
		return getValorCromosoma().toString();
	}
	
	
	
	
	public char getValor(int ind){
		return cromosoma[ind];
	}
	
	public void setValorCromosoma(char _Cromosoma[]){
		for(int i=0;i<_Cromosoma.length;i++)
		cromosoma[i]=_Cromosoma[i];
	}
	
	
	
	/**
	 * El siguinte método me devuelve un booleano indicando si el Cromosoma que le pasamos como parámetro es igual
	 * @param sol regla a comparar
	 * @return
	 */
	
	public boolean Igual(Cromosoma sol){
		boolean igual=true;
		if(sol.getClase()!=clase) igual=false;
		if(igual){
			char aux_cromosoma[]=sol.getValorCromosoma();
			int i=0;
			while((igual)&&(i<aux_cromosoma.length)){
				if(aux_cromosoma[i]!=cromosoma[i]) igual=false;
				i++;
			}
		}
		return igual;
	}
	public Plantilla getPlantilla() {
		return plantilla;
	}
	
	
	
	
}
