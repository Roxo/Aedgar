package edgar;

import java.util.ArrayList;

import Dataset.Attribute;
import Dataset.Attributes;

/**
 * @author Daniel Albendín
 *	Cambio reciente: Fecha 10-02-2015. Se ha agregado cobertura en trapezoidales y triangulares
 */
public class Plantilla 
{
	 // JMGM
     // en la posicion [i-esima][0] número de posibles valores 
     // (solo si es un atributo nominal, en caso contrario será siempre el número de particiones definidas en los parámetros para atributos fuzzy) (JMGM)
	
     // en la posicion [i-esima][1] indice en el Cromosoma
	 private int plantillaAtributos[][];
	 private String nombresAtributos[];
	 
	 /*
	 // Almacenará en qué orden real de la estructura interna se almacena el atributo numérico, teniendo en cuenta que los atributos nominales intermedios alteran el orden
	 // porque su valor no se guarda en esa estructura
	 private ArrayList posicionesAtributosNumericos = new ArrayList();
	 
	 public void addPosicionAtributoNumerico(int posicion)
	 {
		 posicionesAtributosNumericos.add(posicion);
	 }
	 */
	 
	 // JMGM 
	 // Si es nominal, se almacenarán las distintas posibilidades, y si es real o entero, los extremos que definirán las particiones
	 private ArrayList valoresAtributos[];
	 
	 // JMGM 
	 private int tiposAtributos[];
	 
	 private String nombreClase;
	 private ArrayList valoresClase;
	 private int numeroClases;
	 private int contadorClases[];
	 private i_Cobertura cob;
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
		 
		 //<-- Daniel Albendín 
		 // Cuando creamos la plantilla con un constructor de copia creamos el tipo de cobertura que queremos implementar
		 // CAMBIO FUTURO POSIBLE -> Cambiar esto para tener plantillas que implementen distintos tipos de cobertura
		 // Pensar si tiene sentido
		 CrearCobertura();
		//-->
	}
	 /**
		 * Numero de atributos que son numericos y por tanto se pueden optimizar sus particiones
		 * Esta funci—n sirve para detectar si hay que llamar al optimizador y tambien dentro del optimizador para saber cuantos hay que optimizar
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
	
	public void set_ValoresAtributos(ArrayList _ValoresAtributos[],boolean cambio){
		valoresAtributos=_ValoresAtributos;
		if(cambio)
			CrearCobertura();
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
		String devolver = ""+ get_ValoresAtributos()[numAributo].get(numValor);
		return devolver;
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
	 * V2 ojo esta información al igual que el porcentaje de clases se encuentra ahora en plantilla, pero en realidad debería encontrarse en el dataset
	 * dado que es posible que en el dataset de entrada y en el de salida exista diferentes niveles de balanceo. Tampoco se tiene en cuenta el efecto que produce la partición de training /test, 
	 * Debería tenerse en cuenta esto en el dataset para poder analizar el grado de balanceo en un nodo determinado , en el servidor y en el test. 
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
	
	 // Albendín ISSUE1 problema intervalos.
	 // Array de objetos que contienen información detallada de cada atributo. [Ini, fin]
	// private Attributes atributos;
	public Attribute get_Atributoi(int i){
		return Attributes.getInputAttribute(i);
	}
	
	/**
	 *  Crea un objeto que implementa la interfaz i_Cobertura dependiendo del valor que indiquemos en los parametros globales 
	 */
	public void CrearCobertura(){
		 int cobertura = Parametros.getInstancia_Parametros().getCobertura();
		 switch(cobertura){
		 	case	0:
		 		cob = new C_Intervalar(this.valoresAtributos);
			 break;
		 	case 1:
	            cob = new C_Trapezoidal(this.valoresAtributos);
	            break;
		 	case 2:
	            cob = new C_Triangular(this.valoresAtributos);
	            break;
	        default:
	        	break;
		 }
	}
	
	/**
	 * 
	 * @param posicion_regla	Intervalo del atributo de una regla que está activo.
	 * @param valor				Valor del ejemplo el cual hay que ver si la regla lo cubre
	 * @param indAtr			Indice del atributo de la regla que estamos analizando
	 * @param sizeAtr			Tamaño del atributo[indAtr]
	 * @return Devuelve la altura [0,1] del cumplimiento de un atributo en una regla (0) si la parte del atributo que estamos analizando no cubre el valor
	 */
	public Double getCobertura(int posicion_regla,Double valor, int indAtr, int sizeAtr) {
		return cob.GetCobertura(posicion_regla,valor,indAtr,sizeAtr);
    }
	
	
}
