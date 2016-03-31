package edgar;

public class Ejemplo extends Cromosoma{
	
	private int id ;
	public Ejemplo(){	
		int numeroBits=0;
		for(int i=0;i<numAtributos;i++) numeroBits=numeroBits+plantilla.numValoresAtributo(i);
		cromosoma=new char[numeroBits];
		for(int i=0;i<numeroBits;i++) cromosoma[i]='0';
		
	}
	public Ejemplo(Plantilla _plantilla){	
		super(_plantilla);
		int numeroBits=0;
		for(int i=0;i<numAtributos;i++) numeroBits=numeroBits+plantilla.numValoresAtributo(i);
		cromosoma=new char[numeroBits];
		for(int i=0;i<numeroBits;i++) cromosoma[i]='0';
		
	}
	public void set_id(int _id){
		id=_id;
	}
	public int get_id(){
		return id;
	}
	
	public boolean atributo_evaluado(int indAtributo){
		boolean evaluado=false;
		int i=0;
		int long_atr=plantilla.numValoresAtributo(indAtributo);
		while ((!evaluado)&&(i<long_atr)){
			if(cromosoma[plantilla.posicionAtributo(indAtributo)+i]=='1') evaluado=true;
			i++;
		}		
		return evaluado;
	}
	
	public char[] getAtributo(int indAtributo){
		char Atributo[]=new char[plantilla.numValoresAtributo(indAtributo)];
		for (int i=0;i<Atributo.length;i++)
			Atributo[i]=cromosoma[plantilla.posicionAtributo(indAtributo)+i];
		return Atributo;
	}
	
	public void setValorAtributo(int indAtributo,int indvalor,char valor){
		if ((valor!='0') && (valor!='1')) valor='0';
		int indCromosoma=plantilla.posicionAtributo(indAtributo)+indvalor;
		cromosoma[indCromosoma]=valor;
	}
	
	public Ejemplo get_Copia(){
		Ejemplo copia=new Ejemplo();
		copia.setValorCromosoma(cromosoma);
		copia.setClase(clase);
		copia.set_id(id);
		return copia;
	}
	

	public String get_texto_ejemplo(){
		String cadena_ejemplo="";
		int numero_conjunciones=0;
		for(int i=0;i<numAtributos;i++){
			if(atributo_evaluado(i)){
					if (numero_conjunciones>0) cadena_ejemplo=cadena_ejemplo+" AND ";
					numero_conjunciones++;
					
					String nomAtr=plantilla.get_NombresAtributos()[i];
					cadena_ejemplo=cadena_ejemplo+" ( "+nomAtr+"= ";
					
					int inicio_atributo=plantilla.posicionAtributo(i);
					cadena_ejemplo+= " (";
					
					int numero_disyunciones_conjunciones=0;
					String conj_disy=" || ";
					String Neg="";

					
					for(int aux=0;aux<plantilla.numValoresAtributo(i);aux++){
						int aux_ind=aux+inicio_atributo;
						if(cromosoma[aux_ind]=='1') {
							if (numero_disyunciones_conjunciones>0) cadena_ejemplo+=conj_disy;
							numero_disyunciones_conjunciones++;
							cadena_ejemplo+=Neg;
							String valAtributo=plantilla.get_ValoresAtributos()[i].get(aux)+"";
							cadena_ejemplo=cadena_ejemplo+valAtributo;
						}
					}
					cadena_ejemplo=cadena_ejemplo+" ) ) ";
					
				}
		}
					
		cadena_ejemplo=cadena_ejemplo+" --> ";
		cadena_ejemplo=cadena_ejemplo+" ( "+ plantilla.get_Nombre_Clase()+" = "+ plantilla.get_Valores_Clase().get(clase) +")";
		return cadena_ejemplo;
	}
	
	public String get_texto_ejemplo_simple(){
		String cadena_ejemplo=""+ this.id+ "- ";
		
		for(int i=0;i<numAtributos;i++){
			if(atributo_evaluado(i)){
		
					
				int inicio_atributo=plantilla.posicionAtributo(i);
							
					for(int aux=0;aux<plantilla.numValoresAtributo(i);aux++){
						int aux_ind=aux+inicio_atributo;
						 if(cromosoma[aux_ind]=='1') {
							String valAtributo=plantilla.valorAtributo(i,aux)+"";
							cadena_ejemplo=cadena_ejemplo+valAtributo;
						}
					}
		
					
				}
		}
					
		cadena_ejemplo=cadena_ejemplo+" --> ";
		cadena_ejemplo=cadena_ejemplo+" "+  plantilla.get_Valores_Clase().get(clase);
		return cadena_ejemplo;
	}
		
public String toString(){
	return get_texto_ejemplo_simple();
}
	
}
