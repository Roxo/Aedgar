package regalv2;

/**
 * <p>Ejemplo implementa los datos de entrenamiento o ejemplos, 
 * y todos los métodos necesarios para su utilización.</p>
 * @author José Luis Toscano Muñoz
 * @version Regal v2.0
 *
 */
public class Ejemplo extends Cromosoma{
	ParametrosGlobales param_globales=ParametrosGlobales.getInstancia_Parametros();
	
	/**
	 * Constructor de la clase.
	 *
	 */
	public Ejemplo(){	
		int numeroBits=0;
		for(int i=0;i<numAtributos;i++) numeroBits=numeroBits+param_globales.getPlantillaAtributos()[i][0];
		cromosoma=new char[numeroBits];
		for(int i=0;i<numeroBits;i++) cromosoma[i]='0';
		
	}
	
	
	/**
	 * Devuelve un booleano indicando si un atributo está evaluado o no.
	 * @param indAtributo es el índice del atributo.
	 * @return un booleano indicando si está evaluado el atributo. 
	 */
	public boolean atributoEvaluado(int indAtributo){
		boolean evaluado=false;
		int i=0;
		int long_atr=param_globales.getPlantillaAtributos()[indAtributo][0];
		while ((!evaluado)&&(i<long_atr)){
			if(cromosoma[param_globales.getPlantillaAtributos()[indAtributo][1]+i]=='1') evaluado=true;
			i++;
		}		
		return evaluado;
	}
	
	/**
	 * Devuelve una tabla de caracteres con los valores de un atributo.
	 * @param indAtributo es el índice del atributo.
	 * @return una tabla de char con los valores del atributo.
	 */
	public char[] getAtributo(int indAtributo){
		char Atributo[]=new char[param_globales.getPlantillaAtributos()[indAtributo][0]];
		for (int i=0;i<Atributo.length;i++)
			Atributo[i]=cromosoma[param_globales.getPlantillaAtributos()[indAtributo][1]+i];
		return Atributo;
	}
	
	/**
	 * Modifica un valor de un atributo.
	 * @param indAtributo es el índice del atributo que se va a modificar.
	 * @param indvalor es la posición del atributo a modificar.
	 * @param valor es el valor que se le va a asignar a una posición del atributo. 
	 */
	public void setValorAtributo(int indAtributo,int indvalor,char valor){
		if ((valor!='0') && (valor!='1')) valor='0';
		int indCromosoma=param_globales.getPlantillaAtributos()[indAtributo][1]+indvalor;
		cromosoma[indCromosoma]=valor;
	}
	
	/**
	 * Devuelve un copia del Ejemplo.
	 * @return un objeto Ejemplo.
	 */
	public Ejemplo getCopia(){
		Ejemplo copia=new Ejemplo();
		copia.setValorCromosoma(cromosoma);
		copia.setClase(clase);
		return copia;
	}
	

	/**
	 * Devuelve una cadena con la representación del ejemplo.
	 * @return un string con la representación del ejemplo.
	 */
	public String getTextoEjemplo(){
		String cadena_ejemplo="";
		int numero_conjunciones=0;
		for(int i=0;i<numAtributos;i++){
			if(atributoEvaluado(i)){
					if (numero_conjunciones>0) cadena_ejemplo=cadena_ejemplo+" AND ";
					numero_conjunciones++;
					
					String nomAtr=param_globales.getNombresAtributos()[i];
					cadena_ejemplo=cadena_ejemplo+" ( "+nomAtr+"= ";
					
					int inicio_atributo=param_globales.getPlantillaAtributos()[i][1];
					cadena_ejemplo+= " (";
					
					int numero_disyunciones_conjunciones=0;
					String conj_disy=" || ";
					String Neg="";

					
					for(int aux=0;aux<param_globales.getPlantillaAtributos()[i][0];aux++){
						int aux_ind=aux+inicio_atributo;
						if(cromosoma[aux_ind]=='1') {
							if (numero_disyunciones_conjunciones>0) cadena_ejemplo+=conj_disy;
							numero_disyunciones_conjunciones++;
							cadena_ejemplo+=Neg;
							String valAtributo=param_globales.getValoresAtributos()[i].get(aux)+"";
							cadena_ejemplo=cadena_ejemplo+valAtributo;
						}
					}
					cadena_ejemplo=cadena_ejemplo+" ) ) ";
					
				}
		}
					
		cadena_ejemplo=cadena_ejemplo+" --> ";
		cadena_ejemplo=cadena_ejemplo+" ( "+ param_globales.getNombreClase()+" = "+ param_globales.getValoresClase().get(clase) +")";
		return cadena_ejemplo;
	}
	
	
	
}
