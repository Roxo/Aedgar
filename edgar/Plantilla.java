package edgar;

import java.util.ArrayList;

import Dataset.Attribute;
import Dataset.Attributes;

public class Plantilla 
{
	 // JMGM
     // en la posicion [i-esima][0] n�mero de posibles valores 
     // (solo si es un atributo nominal, en caso contrario ser� siempre el n�mero de particiones definidas en los par�metros para atributos fuzzy) (JMGM)
	
     // en la posicion [i-esima][1] indice en el Cromosoma
	 private int plantillaAtributos[][];
	 private String nombresAtributos[];
	 
	 /*
	 // Almacenar� en qu� orden real de la estructura interna se almacena el atributo num�rico, teniendo en cuenta que los atributos nominales intermedios alteran el orden
	 // porque su valor no se guarda en esa estructura
	 private ArrayList posicionesAtributosNumericos = new ArrayList();
	 
	 public void addPosicionAtributoNumerico(int posicion)
	 {
		 posicionesAtributosNumericos.add(posicion);
	 }
	 */
	 
	 // JMGM 
	 // Si es nominal, se almacenar�n las distintas posibilidades, y si es real o entero, los extremos que definir�n las particiones
	 private ArrayList valoresAtributos[];
	 
	 // JMGM 
	 private int tiposAtributos[];
	 
	 private String nombreClase;
	 private ArrayList valoresClase;
	 private int numeroClases;
	 private int contadorClases[];
	 // constructor de copia de plantilla
	 public Plantilla(Plantilla plantilla, ArrayList[] particiones) {
		// TODO Auto-generated constructor stub
		 this.numeroClases = plantilla.numeroClases;
		 this.nombreClase = plantilla.nombreClase;
		 this.contadorClases = plantilla.contadorClases;
		 this.nombresAtributos = plantilla.nombresAtributos;
		 this.tiposAtributos = plantilla.tiposAtributos;
		 this.plantillaAtributos = plantilla.plantillaAtributos;
		 Attribute auxAtributo;

		 int longitud = plantilla.valoresAtributos.length;
		 valoresAtributos = new ArrayList[longitud];
//<-- Daniel Albend�n APROXIMATIVO
		 this.valoresClase = new ArrayList();
		 for(int i=0;i<plantilla.valoresClase.size();i++){
			 this.valoresClase.add(plantilla.valoresClase.get(i));
		 }
		 for(int i=0;i<longitud;i++)
		 {
			 this.valoresAtributos[i] = new ArrayList();
			
			 
			 //******** !OJO copiar el array list de una plantilla a la otra 
//			  if((tiposAtributos[i] == Attribute.NOMINAL)){;
//				 auxAtributo =Attributes.getOutputAttribute(i);
//				 int numPosiblesValoresAtributo = auxAtributo.getNumNominalValues();
//					
//					
//					for (int aux=0;aux<numPosiblesValoresAtributo;aux++){
//						this.valoresAtributos[i].add(aux,auxAtributo.getNominalValue(aux));
//					}
//					
//					_plantillaAtributos[i][0] = numPosiblesValoresAtributo;
//					
//					_plantillaAtributos[i][1] =indiceAtributo;
//					indiceAtributo=indiceAtributo+numPosiblesValoresAtributo;
//				}
//			 
			 //**********
//			 else
			 {
			 
			 
			 int num = particiones[i].size();
			 
			 for(int j=0;j<num;j++){
				 Double newDouble = new Double(Double.parseDouble(""+particiones[i].get(j)));
				 this.valoresAtributos[i].add(newDouble);
			 }
			 
		 } //for j
		 }//for i
	}
	 /**
		 * Numero de atributos que son numericos y por tanto se pueden optimizar sus particiones
		 * Esta funci�n sirve para detectar si hay que llamar al optimizador y tambien dentro del optimizador para saber cuantos hay que optimizar
		 * 
		 * @param _concepto
		 */
	 
	 public int numeroAtributosNumericos()
		{		

			int  numAtributosNumericos = 0;
			int numAtributosTotales = tiposAtributos.length;
			for(int i=0;i<numAtributosTotales;i++)
			{
				if(!(tiposAtributos[i] == Attribute.NOMINAL))
				{
					numAtributosNumericos++;
				}
			}
			return numAtributosNumericos;
		}

	public  double getPorcentajeClase(int posicion)
	 {
		 double resultado =0.0;
		 for (int i=0;i<numeroClases;i++) resultado+=contadorClases[i];
		 resultado = contadorClases[posicion]/resultado;
		 if (resultado < 0.001 ) resultado = 0.001;
		 return resultado;
	 }
	
	public void set_plantillaAtributos(int _plantillaAtributos[][]){
		plantillaAtributos=_plantillaAtributos;
	}
	public int[][] get_plantillaAtributos(){
		return plantillaAtributos;
	}


	
	public void set_NombresAtributos(String _NombresAtributos[]){
		nombresAtributos=_NombresAtributos;
	}
	public String[] get_NombresAtributos(){
		return nombresAtributos;
	}


	public Plantilla()
	{
	}
	public void set_ValoresAtributos(ArrayList _ValoresAtributos[]){
		valoresAtributos=_ValoresAtributos;
	}
	public ArrayList[] get_ValoresAtributos(){
		return valoresAtributos;
	}
	
	
	public void set_Nombre_Clase(String _Nombre_Clase){
		nombreClase=_Nombre_Clase;
	}
	public String get_Nombre_Clase(){
		return nombreClase;
	}
	
	
	public void set_Valores_Clase(ArrayList _Valores_Clase){
		valoresClase=_Valores_Clase;
	}
	public ArrayList get_Valores_Clase(){
		return valoresClase;
	}	
	
	public void set_TiposAtributos(int _TiposAtributos[]){
		tiposAtributos=_TiposAtributos;
	}
	public int[] get_TiposAtributos(){
		return tiposAtributos;
	}
	
    public int getTipoAtributo(int indice)
    {
    	if(indice < tiposAtributos.length)
    	{
    		return tiposAtributos[indice];
    	}
    	return -1;
    }
	
	
	public void set_numero_Clases(int _numero_Clases){
		numeroClases=_numero_Clases;
	}
	public int get_numero_Clases(){
		return numeroClases;
	}	
	
	
	/*****************************************************
	*Metodos
	*******************************************************/
	public int numAtributos(){
		return get_plantillaAtributos().length;
	}
	
	public int numValoresAtributo(int posicion){
		return plantillaAtributos[posicion][0];
	}
	
	public int posicionAtributo(int posicion){
		return plantillaAtributos[posicion][1];
	}
	
	public String valorAtributo(int numAributo, int numValor){
		return (String) get_ValoresAtributos()[numAributo].get(numValor);
	}
	
	public String toString(){
		String texto=new String("");
		for (int i =0; i< this.numAtributos();i++){
			texto+= "" + this.nombresAtributos[i]  ;
			texto+= " (" + this.valoresAtributos[i] + ")\n" ;
		}
		texto+= "  --> " + this.get_Nombre_Clase() + "(" + this.valoresClase+ ")\n";	
		
		return texto;
	}
	public int[] getContadorClases() {
		return contadorClases;
	}
	/**
	 * 
	 * @param contador: array con el numero de clases que hay en el conjunto de datos
	 */
	public void setContadorClases(int[] contador) {
		this.contadorClases = contador;
	}
	/**
	 * @param posicion de la clase 
	 * @return numero de instancias de dicha clase en el conjunto de datos
	 */
	public int contadorClases(int posicion){
		return contadorClases[posicion];
	}

	
	/*
	 * V2 ojo esta informaci�n al igual que el porcentaje de clases se encuentra ahora en plantilla, pero en realidad deber�a encontrarse en el dataset
	 * dado que es posible que en el dataset de entrada y en el de salida exista diferentes niveles de balanceo. Tampoco se tiene en cuenta el efecto que produce la partici�n de training /test, 
	 * Deber�a tenerse en cuenta esto en el dataset para poder analizar el grado de balanceo en un nodo determinado , en el servidor y en el test. 
	 *
	 */
	public int claseMayoritaria() {
		double max=0;
		int clase=0;
		for (int i= 0; i< this.get_numero_Clases();i++)
			if (getPorcentajeClase(i) > max){
				 max = getPorcentajeClase(i);
				 clase=i;
			}
		return clase;
	}

	public int claseMinoritaria() {
		double min=1;
		int clase=0;
		for (int i= 0; i< get_numero_Clases();i++)
			if (getPorcentajeClase(i) < min){
				 min = getPorcentajeClase(i);
				 clase=i;
			}
		return clase;
	}

	public float RatioBalanceo() {
		float porcMayor =  (float) this.getPorcentajeClase(claseMayoritaria());
		float porcMenor = (float)(this.getPorcentajeClase(claseMinoritaria()));
		return  (porcMayor/porcMenor);
	}

	public void set_tiposAtributos(int[] _tiposAtributos) {
		tiposAtributos = _tiposAtributos;
		
	}
	
	 // Albend�n ISSUE1 problema intervalos.
	 // Array de objetos que contienen informaci�n detallada de cada atributo. [Ini, fin]
	// private Attributes atributos;
	public Attribute get_Atributoi(int i){
		return Attributes.getInputAttribute(i);
	}
	
	
}
