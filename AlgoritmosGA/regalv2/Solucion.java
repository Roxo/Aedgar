package regalv2;
import java.util.ArrayList;


/**
 * Solucion contiene los métodos para manejar un conjunto de Reglas.
 * @author José Luis Toscano Muñoz
 * @version Regal v2.0
 */
public class Solucion {
	
	/**
	 * Es una colección de Reglas.
	 */
	private ArrayList setReglas=new ArrayList();
	
	/**
	 * Es el constructor de la clase.
	 */
	public Solucion(){}
	
	/**
	 * Devuelve la regla que se encuentra en la posición pasada como parámetro.
	 * @param ind_regla posición de la regla a devolver.
	 * @return la Regla que se encunetra en la posición indicada.
	 */
	public Regla getRegla(int ind_regla){
		return (Regla)setReglas.get(ind_regla);
	}
	
	
	/**
	 * Introduce una regla en el conjunto de reglas de forma ordenada
	 * en orden descendente del valor PI.
	 * @param nueva_regla es la nueva regla a introducir.
	 */
	public void insertarReglaOrdenPI(Regla nueva_regla){
		try{
			if (setReglas == null) setReglas=new ArrayList();
			int pos=0;
			boolean seguir=true;
			while((seguir)&&(pos<setReglas.size())){
					if(((Regla)setReglas.get(pos)).getPI()<nueva_regla.getPI()) seguir=false;
					else pos++;
			}		
			setReglas.add(pos,nueva_regla);
			
		}catch(Exception e){
			System.out.println("Error añadiendo una nueva regla en orden PI");
		}
	}
	
	/**
	 * Introduce una regla en el conjunto de reglas en la última posición.
	 * @param nueva_regla es la nueva regla a introducir.
	 */
	public void insertarRegla(Regla nueva_regla){
		try{
			if (setReglas == null) setReglas=new ArrayList();
			setReglas.add(setReglas.size(),nueva_regla);
		}catch(Exception e){
			System.out.println("Error añadiendo una nueva regla.");
		}
	}
	
	
	/**
	 * Introduce una regla en el conjunto de reglas en la posición indicada por parámetro.
	 * @param nueva_regla es la nueva regla a introducir.
	 * @param pos es la posición donde se va a insertar la regla.
	 */
	public void insertarReglaPosicion(Regla nueva_regla, int pos){
		try{
			if (setReglas == null) setReglas=new ArrayList();
			if(pos>setReglas.size()) pos=setReglas.size();
			setReglas.add(pos,nueva_regla);
		}catch(Exception e){
			System.out.println("Error añadiendo una nueva regla en la posicion: "+pos);
		}
	}
	

	/**
	 * Devuelve el índice de la regla con mejor PI
	 * @return índice de la regla mejor PI=fitness*N+
	 */
	public int getIndiceMejorRegla(){
		double mejor_Val=0.0;
		int indice_mejor_solucion=0;
		for(int i=0;i<setReglas.size();i++){
			Regla individuo=((Regla)setReglas.get(i));
			if(individuo.getPI()>mejor_Val){
				mejor_Val=individuo.getPI();
				indice_mejor_solucion=i;
			}
		}
		return indice_mejor_solucion;
	}
	
	/**
	 * Devuelve un booleano indicando si una regla existe ya en la Solucion.  
	 * @param regla es la regla a comprobar si existe. 
	 * @return un booleano indicando si existe ya la regla.
	 */
	public boolean existeRegla(Regla regla){
		boolean existe=false;
		int i=0;
		while((existe==false)&&(i<setReglas.size())){
			existe=regla.igual((Regla)setReglas.get(i));
			i++;
		}
		return existe;
	}
	
	/**
	 * Devuelve el número de reglas del objeto Solucion.
	 * @return un entero con el número de reglas.
	 */
	public int getTamaño(){
		return setReglas.size();
	}
	
	
	/**
	 * Devuelve una Solucion formada por las reglas necesarias para cubrir
	 * un conjunto de ejemplos que se pasan por parámetro.
	 * @param conjunto_ejemplos son los ejemplos. 
	 * @return Una solución con las reglas necesarias para cubrir los ejemplos.
	 */
	public Solucion getConcepto(ConjuntoEntrenamiento conjunto_ejemplos){
		Solucion nuevo_concepto=new Solucion();		
		int cont_reglas=0;			
		ArrayList indice_ejemplos_No_Cubiertos=new ArrayList();
		
		for (int i=0;i<conjunto_ejemplos.getTamaño();i++){
			indice_ejemplos_No_Cubiertos.add(new Integer(i));
		}			
		
		while((indice_ejemplos_No_Cubiertos.size()>0)&&(cont_reglas<getTamaño())){				
			Regla regla=getRegla(cont_reglas);
			boolean regla_cubre_algun_ejemplo=false;
			int i=0;
			int cont_ej_cubiertos=0;
			
			while(i<indice_ejemplos_No_Cubiertos.size()){
				int ind_Ejemplo=Integer.parseInt(indice_ejemplos_No_Cubiertos.get(i)+"");
				Ejemplo ej=conjunto_ejemplos.getEjemplo(ind_Ejemplo);
				if(regla.cubreEjemploPositivamente(ej)) {
					regla_cubre_algun_ejemplo=true;
					cont_ej_cubiertos++;
					indice_ejemplos_No_Cubiertos.remove(i);
				}
				else i++; 
			}
			
			if (regla_cubre_algun_ejemplo) {
				nuevo_concepto.insertarReglaOrdenPI(regla.getCopia());
			}
			cont_reglas++;			
		}			
		return nuevo_concepto;
	}
	

	/**
	 * Devuelve una tabla con la clasificación de un conjunto de ejemplos de prueba.
	 * Esta tabla está formada por dos columnas y tantas filas como ejemplos.
	 * En la columna 0 se almacena la clase del ejemplo, y en la columna 1 la clase que predicha por el clasificador.  
	 * @param datos_test es el conjunto de ejemplos a clasificar.
	 * @return una tabla con la clasificación de los ejemplos.
	 */
	public int[][] clasificar(ConjuntoEntrenamiento datos_test){
		int [][] resultado_clasificacion=new int[datos_test.getTamaño()][2];
		for (int i=0;i<datos_test.getTamaño();i++){		
			Ejemplo ej=datos_test.getEjemplo(i);	
			int clase=0;
			int cont_reglas=0;
			boolean clasificado=false;
			while((cont_reglas<setReglas.size())&&(clasificado==false)){
				Regla Regla=(Regla)setReglas.get(cont_reglas);				
				//si cumpre la regla incrementamos la clase
				if(Regla.cubreEjemplo(ej)){
					clasificado=true;
					clase=Regla.getClase();
				}
				cont_reglas++;
			}
			resultado_clasificacion[i][0]=datos_test.getEjemplo(i).getClase();
			resultado_clasificacion[i][1]=clase;
		}
		return resultado_clasificacion;
	}
	
	/**
	 * Devuelve un booleano indicando si dos Soluciones son iguales.
	 * @param sol1 es una Solucion.
	 * @param sol2 es la otra solucion a comparar.
	 * @return un booleano indicando si las dos soluciones son iguales.
	 */
	public boolean poblacionesIguales(Solucion sol1, Solucion sol2){	
		boolean iguales=true;		
		if(sol1.getTamaño()!=sol2.getTamaño()) iguales=false;		
		if(iguales){
			int i=0;
			while((iguales)&&(i<sol1.getTamaño())){
				if(!sol2.existeRegla(sol1.getRegla(i))) iguales=false; 
				i++;
			}
		}
		return iguales;
	}
	
	/**
	 * Devuelve un booleano indicando si una Solucion pasada por parámetro es igual a la Solucion. 
	 * @param sol es la Solucion a comparar.
	 * @return un booleano indicando si la Solucion es igual.
	 */
	public boolean esIgual(Solucion sol){	
		boolean iguales=true;		
		if(getTamaño()!=sol.getTamaño()) iguales=false;		
		if(iguales){
			int i=0;
			while((iguales)&&(i<getTamaño())){
				if(!existeRegla(sol.getRegla(i))) iguales=false; 
				i++;
			}
		}
		return iguales;
	}
	
	
	/**
	 * Elimina una regla de la Solucion.
	 * @param indice_regla es la posición de la regla a eliminar.
	 */
	public void eliminarRegla(int indice_regla){
		try{
			setReglas.remove(indice_regla);
		}catch(Exception e){
			System.out.println("Error elimando un nuevo cromosoma a la poblacion");
		}
	}
	
	/**
	 * Devuelve un string con la representación de la Solucion
	 * @return un string con la representacion de la Solucion.
	 */
	public String getTextoSolucionCompleta(){
		String text_solucion="";
		for(int i=0;i<getTamaño();i++){				
			text_solucion+=(i+1)+".- ";
			text_solucion+=getRegla(i).getTextoRegla()+"\n";
			text_solucion+="     Fitness: "+getRegla(i).getFitness();
			text_solucion+=" N+: "+getRegla(i).getNumeroCasosPositivos();
			text_solucion+="   N-: "+ getRegla(i).getNumeroCasosNegativos();
			text_solucion+="   PI (fit*N+): "+ getRegla(i).getPI();
			text_solucion+="\n";
		}
		
		return text_solucion;
	}
	
	
	/**
	 * Devuelve una copia de la Solucion
	 * @return una copia de la Solucion.
	 */
	public Solucion getCopia(){
		Solucion copia=new Solucion();
		for (int i=0;i<this.setReglas.size();i++){
			Regla regla_copiar=(Regla)setReglas.get(i);
			copia.insertarRegla(regla_copiar.getCopia());
		}
		
		return copia;
	}
	
	
}
