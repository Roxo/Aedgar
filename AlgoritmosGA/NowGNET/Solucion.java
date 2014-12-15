package NowGNET;
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
	/*public void insertarReglaOrdenPI(Regla nueva_regla){
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
	*/
	
	/**
	 * Introduce una regla en el conjunto de reglas de forma ordenada
	 * en orden descendente del valor del fitness.
	 * @param nueva_regla es la nueva regla a introducir.
	 */
	public void insertarReglaOrdenFitness(Regla nueva_regla){
		try{
			if (setReglas == null) setReglas=new ArrayList();
			int pos=0;
			boolean seguir=true;
			while((seguir)&&(pos<setReglas.size())){
					if(((Regla)setReglas.get(pos)).getFitness()<nueva_regla.getFitness()) seguir=false;
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
			if(individuo.getFitness()>mejor_Val){
				mejor_Val=individuo.getFitness();
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
			Regla r=(Regla)setReglas.get(i);
			if ((r!=null)&&(regla!=null)){
				existe=regla.igual(r);	
			}
			
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
	/*
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
	*/

	/**
	 * Devuelve una tabla con la clasificación de un conjunto de ejemplos de prueba.
	 * Esta tabla está formada por dos columnas y tantas filas como ejemplos.
	 * En la columna 0 se almacena la clase del ejemplo, y en la columna 1 la clase que predicha por el clasificador.  
	 * @param datos_test es el conjunto de ejemplos a clasificar.
	 * @return una tabla con la clasificación de los ejemplos.
	 */
	public int[][] clasificar(ConjuntoEntrenamiento datos_test){
		this.numeroEjemplosNoCubiertos=0;
		this.numeroEjemplosCubiertosPositivamente=0;
		this.numeroEjemplosCubiertosNegativamente=0;
		this.numeroEjemplos=datos_test.getTamaño();
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
			
			if (clasificado){
				if (datos_test.getEjemplo(i).getClase()==clase){
					numeroEjemplosCubiertosPositivamente++;
				}else{
					numeroEjemplosCubiertosNegativamente++;
				}
			}else{
				numeroEjemplosNoCubiertos++;
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
			//text_solucion+="     MDL: "+getRegla(i).getFitness();
			text_solucion+=" N+: "+getRegla(i).numeroEjemplosCubiertosPositivamente;
			text_solucion+="   N-: "+ getRegla(i).numeroEjemplosCubiertosNegativamente;
			text_solucion+="   N?: "+ getRegla(i).numeroEjemplosNoCubiertos;
			text_solucion+="   N+-: "+ getRegla(i).numeroEjemplosPositosNoCubiertos;
			
			
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
		copia.fitness=this.fitness;
		return copia;
	}
	
	
	
	
	
	
	
	
	
	
	
/*	
	private double fg_MDL=0;
	public double evaluarMDL2(ConjuntoEntrenamiento ejemplos){
		double Kh=0.0;
		double w=0.0;
		double longitudCromosoma=0;
		if(this.setReglas.size()>0){
			longitudCromosoma=((Regla)setReglas.get(0)).getLongitudCromosoma();
		}		
		for (int i=0;i<this.setReglas.size();i++){
			Kh+=longitudCromosoma-((Regla)setReglas.get(0)).evaluarZ();
		}
		Kh=Kh*this.getTamaño();
		w=numeroEjemplosMalCubiertos(ejemplos);
		this.fg_MDL=Math.pow(2, -Kh)+Math.pow(2, -w);
		return this.fg_MDL;
	}

	
	
	public double getFgMDL(){
		return this.fg_MDL;
	}
	*/
	
	public int numeroEjemplosMalCubiertos(ConjuntoEntrenamiento ejemplos){	
		// Inicializo el número de ejemplos cubiertos
		int numeroEjemplosMalCubiertos=0;		
		int Resultados[][]=this.clasificar(ejemplos);
		for(int i=0;i<Resultados.length;i++){
			if (Resultados[i][0]!=Resultados[i][1]){		
				numeroEjemplosMalCubiertos++;
			}
		}
		return numeroEjemplosMalCubiertos;
	}


	
	/**
	 * Devuelve una Solucion formada por las reglas necesarias para cubrir
	 * un conjunto de ejemplos que se pasan por parámetro.
	 * @param conjunto_ejemplos son los ejemplos. 
	 * @return Una solución con las reglas necesarias para cubrir los ejemplos.
	 */
	public Solucion getConceptoReducido(ConjuntoEntrenamiento conjunto_ejemplos){
		
		Solucion ConceptoReducido=this.getCopia();
		
		if (this.getTamaño()>1) {
			
		
		Solucion ConceptoActual=this.getCopia();
		int indRegla=ConceptoReducido.getTamaño()-1;
		
		
		
		//double fgActual=ConceptoReducido.evaluarMDL(conjunto_ejemplos);
		double fgActual=ConceptoReducido.evaluarFitness(conjunto_ejemplos);
		
		
		
		while (indRegla>=0){
			
			Regla reglaP=ConceptoReducido.getRegla(indRegla).getCopia();
			ConceptoReducido.eliminarRegla(indRegla);
			
			//double fgReducido=ConceptoReducido.evaluarMDL(conjunto_ejemplos);
			double fgReducido=ConceptoReducido.evaluarFitness(conjunto_ejemplos);
			
			
			// Si se produce una mejora
			if(fgReducido>fgActual){
				ConceptoActual.eliminarRegla(indRegla);
				fgActual=fgReducido;
				indRegla--;				
				//indRegla=ConceptoReducido.getTamaño()-1;
				//System.out.println("REDUCE");
				
			}else{
				ConceptoReducido.insertarReglaOrdenFitness(reglaP);
				indRegla--;
				//System.out.println("NO Reduce Reducido(" +fgReducido+ ") Actual("+fgActual+")" );
			}
				
		  }	
		}
		return ConceptoReducido;
		
	
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
				nuevo_concepto.insertarReglaOrdenFitness(regla.getCopia());
			}
			cont_reglas++;			
		}			
		return nuevo_concepto;
	}
	
	
	/***
	 *   FITNESS SOLUCION
	 */
	
	
	
	/**
	 * Fitness de la solución.
	 */
	private double fitness;
	
	private int numeroEjemplosNoCubiertos=0;
	private int numeroEjemplosCubiertosPositivamente=0;
	private int numeroEjemplosCubiertosNegativamente=0;
	private int numeroEjemplosPositosNoCubiertos=0;
	private int numeroEjemplos=0;
	
	
	
	private ParametrosGlobales parametrosGlobales =ParametrosGlobales.getInstancia_Parametros();
	
	/**
	 * Establece el valor del fitness.
	 * @param _fitness es el valor del fitness a asignar a la regla.
	 */
	public void setFitness(double _fitness){
		fitness=_fitness;
	}
	
	/**
	 * Devuelve el valor del fitness de la regla.
	 * @return un double con el valor del fitness de la regla. 
	 */
	public double getFitness(){
		return fitness;
	}
	
	
	
	/**
	 * Devuelve el fitness de la regla.
	 * @return devuelve el valor de la función de fitness.
	 */
	public double evaluarFitness(ConjuntoEntrenamiento ejemplos){
			
		this.clasificar(ejemplos);
		this.fitness=this.gevalfitBasic(this.getTamaño(), -1, this.numeroEjemplosPositosNoCubiertos, this.numeroEjemplosCubiertosNegativamente, 1);
		
		/*	double z=0.0;
		for(int i=0;i<getTamaño();i++){
			z+=this.getRegla(i).evaluarZ();
		}*/
						
		//this.fitness=(1 + parametrosGlobales.get_A() * (z/(double)Math.exp(this.getTamaño())))*Math.exp(-(this.numeroEjemplosCubiertosNegativamente-this.numeroEjemplosNoCubiertos));
		//this.fitness=(Math.exp(-this.getTamaño()))*Math.exp(-(this.numeroEjemplosCubiertosNegativamente+this.numeroEjemplosNoCubiertos));
		//this.fitness=this.gevalfit(this.getTamaño(), -1, this.numeroEjemplosNoCubiertos, this.numeroEjemplosCubiertosNegativamente, 1);

		this.parametrosGlobales.depuracion("Fitenss Sol: " + this.fitness +" e+: "+this.numeroEjemplosCubiertosPositivamente + " e-: "+this.numeroEjemplosCubiertosNegativamente, 5);
		return this.fitness;
	}
	
	
	/**
	 * Evalúa una regla para un conjunto de datos de entrenamiento.
	 * @param ejemplos es el conjunto de ejmplos necesarios para evaluar la regla.
	
	public void evaluarSolucion(ConjuntoEntrenamiento ejemplos){
		// primero comprobamos los ejemplos que cumplen la clase y le 
		evaluarEjemplosCubiertos(ejemplos);
		this.clase=evaluarClase(getNumeroEjemplosCubiertos());
		evaluarFitness();		
		evaluarPI();
	} */

	
	
	
	
	

	
	
	private double wpos = 10.0;
	private double wneg = 10.0; 
	private double wovr = 0.1;
	

	private double gevalfitBasic(int nd,int toskip,int m, int w, int rm){ 
		  double g=0;
		  double fg=0;
		  double v=0;
		  double z=0;
		  double mm=0;
		  int i;
		  int lgoa =this.numeroEjemplosCubiertosPositivamente+m;
		  mm = (double)(lgoa - m);
		  z = 0.0;
		  for(i=0; i<nd; i++) if(i != toskip)
			  z += (double)((Regla)this.setReglas.get(i)).evaluarZ();
			  //z += (double)((Regla)this.setReglas.get(i)).getNumeroDe0();
		  
		  //z=z/(double)nd;
		  
		  v = mm/((double)lgoa);
		  if(m==lgoa) g =0;
		    else g = 1.0 - z/mm;
		  
		  if(g<0.0) g = 0.0;
		  fg = 1.0 + g + wpos * v - wovr * (1.0 - mm/(double)rm);
		  return(Math.pow((double) Math.E,-wneg*(double)w/mm) * fg);
		  }

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*********** TEMPORA PRUEBAS *****************/
	
	
	
	/*
	 * levalfit: local fitness evaluation
	 * - z : no. of zeros
	 * - m : positive uncovered
	 * - w : negative covered
	 */

	
	
	
	double levalfitBasic(int z, int m, int w) {
		  double g, fg, v, mm;
		
		  int lgoa =this.numeroEjemplosCubiertosPositivamente+m;
		  
		  lgoa=this.numeroEjemplosNoCubiertos;// this.numeroEjemplos;
		  mm = (double)(lgoa - m);
		  v = mm/(double)lgoa;
		  if(m==lgoa) g =0;
		    else g = 1.0 - (double)z/mm;
		  if(g<0.0)
			  g = 0.0;
		  /*fg = 1.0 + g + wpos * v;*/
		  fg = 1.0 + g;
		  return(Math.pow((double) Math.E,-wneg*0.1*(double)w/mm) * fg);
		  }
	


	/*
	 * gevalfit: global fitness evaluation
	 * - nd     : no. of disjuncts (partial descriptions)
	 * - darray : pointers to descriptions (solutions)
	 * - toskip : solution to skip (-1 = disabled)
	 * - m      : positive uncovered
	 * - w      : negative covered
	 * - rm     : redundancy (not yet used)
	 */

	double gevalfit(int nd,int toskip,int m,int w,int rm)  
	{
		/*
		 * Nuevo
		 */
		  int lgoa =this.numeroEjemplosCubiertosPositivamente+m;
		
		double  MNT= 0.0000000000000000001;
		//int LTEMPLATE=1;//*this.getRegla(0).getValorCromosoma().length;
		int i;
		
		double LTEMPLATE=0;
		//for(i=0; i<nd; i++)
		//	LTEMPLATE+=this.getRegla(i).getNumeroAtributosEvaluados();
		
		LTEMPLATE=1;
		
		// ********
	  double MMDLA = -1.0; /* data part */
	  int MM = -1;
	  int MM2 = -1;
	  double dg = 0.0, dd = 0.0, fg = 0.0;
	  Numcodes=this.numeroEjemplos;
	  lgoa=this.numeroEjemplosCubiertosPositivamente;
	  if(MM <0){	
	     MM = Numcodes - lgoa;
	     MM2 = MM/2;
	     }

	  if(MMDLA < 0.0) /* compute maximum (data part) */
	    MMDLA = eval_mdl(wpos*(double)lgoa + wneg*(double)(Numcodes-lgoa), (wpos*(double)lgoa)/2.0 + (wneg*(double)MM)/2.0,
	                     (double)LTEMPLATE,(double)0);

	  dd = eval_mdl(wpos*(double)lgoa + wneg*(double)(Numcodes-lgoa), wpos*(double)m + wneg*(double)(w<MM2 ? w : MM2),
			(double)LTEMPLATE,((double)LTEMPLATE)/2.0);

	  for(i=0; i<nd; i++)
	    if(i != toskip)
	      dg += eval_mdl(wpos*(double)lgoa + wneg*(double)(Numcodes-lgoa),
	 		     (double)0,
			     (double)LTEMPLATE,(double)(this.getRegla(i).evaluarZ()));

	  dg += (lgoa-m > 0 ? ((double)rm / (double)(lgoa - m)) * wovr : 0.0);

	  
	

	  if((dg+dd) <= MMDLA)
	    fg = 2.0*MMDLA - dg - dd; /* MDL to Fitness */
	  else
		  fg = MMDLA *Math.pow(Math.E, (-1)*0.01*((dg + dd) - MMDLA));
	    //fg = MMDLA * pow(E,-0.01*(dg + dd - MMDLA)); /* too long descriptions */

	
	  
	  if(fg < MNT)
		  fg = MNT;

	  return fg;
	}
	
	
	

	int Numcodes=1;
	
	
	static double eval_mdl(double md,double kd,double mg,double kg)	{
	  double dg=0;
	  double dd=0;

	  if(kd>0.0 && kd<md) {
	    dd = md * entropy(kd/md);
	    dd += 1.5 * (Math.log(md)/Math.log(2));
	    dd -= 0.5 * (Math.log(kd)/Math.log(2));
	    dd -= 0.5 * (Math.log(md-kd)/Math.log(2));
	    dd -= 1.3257;
	  }
	  else dd = 0.0;
	  if(kg>0.0 && kg<mg) {
	    dg = mg * entropy(kg/mg); 
	    dg += 1.5 * (Math.log(mg)/Math.log(2));
	    dg -= 0.5 * (Math.log(kg)/Math.log(2));
	    dg -= 0.5 * (Math.log(mg-kg)/Math.log(2));
	    dg -= 1.3257;
	  }
	  else dg = 0.0;
	  return dg + dd; /* MDL */
	}
	
	static double entropy(double p)
	{ 
	  double cp = 1-p;

	  cp *= Math.log(cp);
	  return -1.0 * p * (Math.log(p)/Math.log(2)) - cp;
	}
	

	
	/*
	 * Calcular  MDL
	 */
	
	//private double wpos=1;
	//private double wneg=1;
	//private double wovr=0;
	
	
	
	
}
