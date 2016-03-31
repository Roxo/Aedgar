package edgar;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import Aleatorios.Aleatorio;
;

/**
 * @author Miguel Angel Rodriguez
 * Esta clase simula el funcionamiento de un nodo.
 *
 */
public class Isla extends Thread implements i_Nodo{
	
	
	private Estadisticas costes=Estadisticas.getInstancia_buffer();


	// Defino los parametros del nodo, posteriormente se inicializan con los valores de los parametros globales
	private double ratio_migracion_nu;
	private double ratio_adaptacion_foraneo;
	Aleatorio Gen_Aleatorio;
	private double Pcruce;
	private double  g;
	private int poblacion; //Tamano de la poblacion
	
	
	// delaracinn de los distintos objetos necesarios.	
	private Operador opGen=new Operador();
		
	private int Id_Nodo;
	private String NombreFichero_Resultado="";
	
	
	private Solucion descripcionNodo = new Solucion();
	private Dataset datosEntrenamiento;
	BufferMejoresReglas mejores;//buffer recepcinn de reglas desde el pool
	private Solucion Av=new Solucion();  //Poblacinn activa
	private Solucion Bv=null; // Poblacinn de hijos producidos
	private Solucion Anet=null; // Poblacinn recibida de la red
	private int Numero_Individuos_Comunicacion=0;
	
	Parametros param;
	private int numeroGeneracionesNodo;
	private int numeroSolucionesEnviadas;
	private int numeroEvaluaciones;
	private int numeroGeneracionesSinComunicacion;
	private int contadorGeneracionesSinComunicacion;
	
	private int numPocoRepresentados;
	boolean enviaMejores; //se envian las mejores reglas a la red o se eligen aleatoriamente.
	private boolean eliminarEntrenamiento;
	private boolean sustituirEntrenamiento;
	//todos se copian de los parametros  globales al crear el nodo
	// asi se permite cambiar el modo del nodo sin necesidad de reiniciar el hilo
	private boolean generarFicherosDepuracion=false;
	
	boolean comunicacionNodos=false;
	
	public Isla(Dataset aux_conjunto, int Identificacion){
		datosEntrenamiento=aux_conjunto;
		Id_Nodo=Identificacion;
		Recibir_Parametros();
		
		Numero_Individuos_Comunicacion=(int)(this.poblacion*ratio_migracion_nu);
		if ((Numero_Individuos_Comunicacion%2)!=0) Numero_Individuos_Comunicacion--;
		
		
		if (this.generarFicherosDepuracion){
			NombreFichero_Resultado="debugNodos\\"+Id_Nodo+".txt";	
			try {
	   		 FileOutputStream f = null;
			     f = new FileOutputStream(NombreFichero_Resultado);
			     String cab="Fichero info nodo: "+Id_Nodo+"\n";
			     f.write(cab.getBytes());
			     f.close();			
			}catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
	
		// Inicializo la poblacion inicial Av	
		// se crean m (parametro de nnmero de individuos por nodo) individuos, utilizando el operador de sembrado.
		int numAtributos=param.getPlantilla().numAtributos()/param.get_Numero_Nodos();
		int inicio=this.Id_Nodo*(numAtributos);
		if (this.Id_Nodo == (param.get_Numero_Nodos()-1)) numAtributos += param.getPlantilla().numAtributos()%param.get_Numero_Nodos() ;
		
		//snlo se usa si se hce exploracinn de features por nodo
		for(int i=0;i<poblacion;i++){
			int indiceDatoEntrenamiento=Gen_Aleatorio.Randint(0,datosEntrenamiento.getTamano_conjunto_entrenamiento()-1);
			Regla NuevaRegla;
			if (param.isParcial() ){
				NuevaRegla=Operador.Sembrado(datosEntrenamiento.get_EjemploFuzzy(indiceDatoEntrenamiento),inicio,numAtributos);
			}
				
			else	
				NuevaRegla=Operador.Sembrado(datosEntrenamiento.get_EjemploFuzzy(indiceDatoEntrenamiento));
			// Aqui debo llamar a la funcinn de evaluacinn
			NuevaRegla.evaluar_solucion(datosEntrenamiento);	
			numeroEvaluaciones++;
			Av.Insertar_regla_Orden_PI(NuevaRegla);
		}
		descripcionNodo=Av.get_Concepto(datosEntrenamiento);
		this.setName(Id_Nodo+"");
	};
	
	public Thread hilo(){
		return this;
	}
	
	
	public void Recibir_Parametros(){
		param=Parametros.getInstancia_Parametros();
		Gen_Aleatorio=new Aleatorio();
		Gen_Aleatorio.Set_random(param.get_Semilla());
		poblacion=param.getPoblacion();
		ratio_migracion_nu=param.get_ratio_migracion_nu();
		ratio_adaptacion_foraneo=param.get_ratio_adaptacion_foraneo_P();
		Pcruce=param.get_Pcruce();
		g=param.get_g();
		eliminarEntrenamiento = param.getEliminarEntrenamiento();
		sustituirEntrenamiento = param.getSustituirEntrenamiento();
		numeroGeneracionesSinComunicacion=param.get_numGeneracionesSinComunicacion();
		numPocoRepresentados= param.get_num_poco_representados();
		enviaMejores = param.get_envia_mejores ();
		this.generarFicherosDepuracion=param.get_generar_Fichero_Debug();
		mejores=BufferMejoresReglas.getInstancia_buffer();
	}
	
	
	/**
	 * Este mntodo se recupera de la red un conjunto de reglas y las almacena el la variable Anet
	 *
	 */
	public void Recibir_Reglas_Red(){
		Anet= new Solucion();
		NET red=Parametros.getInstancia_Parametros().get_NET();
		Anet=red.RecibirReglas(Id_Nodo);
		if (Anet == null) Anet=new Solucion();
		param.depura(" Nodo  ("+ this.Id_Nodo+ ") ha recibido de la red: "+ Anet.getTamano_solucion(),1);
		param.depura("Reglas Recibidas Nodo ("+ this.Id_Nodo+ ") "+ Anet.get_texto_solucion_Completa(),2);
		
		int aux=0;
		// Una vez se han recibido las reglas, estas son evaluadas en el nodo.		
		//Cuidado no ha recibido reglas de la red
		while(aux<Anet.getTamano_solucion()){
			Anet.get_regla(aux).evaluar_solucion(datosEntrenamiento);
			numeroEvaluaciones++;
			aux++;
		}	
	}
	
	
	
	/**
	 * Este mntodo selecciona las mejores o aleatoriamente 
	 * (parametro enviamejores)reglas para enviar a la red
	 *
	 */
	
	public void Enviar_Reglas_Red(){
		Solucion reglas_enviar=new Solucion();
		int i=0;
		int indiceRegla=0;
			
		if (enviaMejores){
			for(int aux=0;aux<descripcionNodo.getTamano_solucion() && i<Numero_Individuos_Comunicacion ;aux++){
			//	Solucion mejoresReglas= new Solucion();
					reglas_enviar.insertarRegla(descripcionNodo.get_regla(aux).getCopia());
					descripcionNodo.get_regla(aux).setEnviada(true);
					//System.out.println(" Nodo: " + this.Id_Nodo +  " :-> " + descripcionNodo.get_regla(aux) );
					i++; 
			}
		}
		while(i<Numero_Individuos_Comunicacion){		
			indiceRegla =Gen_Aleatorio.Randint(0,Av.getTamano_solucion()-1);
			reglas_enviar.insertarRegla(Av.get_regla(indiceRegla).getCopia());
			Av.eliminarRegla(indiceRegla);
			i++;
			numeroSolucionesEnviadas++;
		}
		
		NET red=Parametros.getInstancia_Parametros().get_NET();	
		param.depura(" Nodo  ("+ this.Id_Nodo+ ") Envia a la red: "+ reglas_enviar.getTamano_solucion(),1);
		param.depura(" Reglas Enviadas por el Nodo ("+ this.Id_Nodo+ ") "+ reglas_enviar.get_texto_solucion_Completa(),3);
		red.EnviarReglas(this.Id_Nodo,reglas_enviar);
		numeroSolucionesEnviadas+=reglas_enviar.getTamano_solucion();
		Parametros.getInstancia_Parametros().depura("nodo" + this.Id_Nodo + " Tiempo :" + System.currentTimeMillis(), 2);
		costes.add_Comunicaciones(numeroSolucionesEnviadas);
		numeroSolucionesEnviadas=0;
	}
	
	
	
	/**
	 * Envia las reglas que forman el concepto al pool
	 * 
	 */

	public void enviarReglasPool(){
		 int numNoEnviadas=0;
		for(int aux=0;aux<descripcionNodo.getTamano_solucion();aux++){
		 if (descripcionNodo.get_regla(aux).isEnviada()==false){
			descripcionNodo.get_regla(aux).setEnviada(true); 
			Regla _MejorRegla = descripcionNodo.get_regla(aux).getCopia();
			param.depura("NODO(" + this.Id_Nodo + ") Envia Regla al Supervisor->" + _MejorRegla.get_texto_Regla(),2);
			mejores.add_regla(_MejorRegla);
			numeroSolucionesEnviadas++;
		 }
		 else numNoEnviadas++;
	    }
		param.depura("NODO(" + this.Id_Nodo + ") No envia Repetidas ->" + numNoEnviadas,2);
	}
	/**
	 * Ralentiza el funcionamiento de los nodos por si se quiere simular distintas velocidades de microporcesador
	 * @param milisec
	 */
	 private void duerme(int milisec){
	   try {
			sleep(milisec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
   }
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 * Cambios: 080509
	 *        1: Enfriamiento controlado comparando los dos ultimos conceptos oldConcepto descripcionNodo.
	 * 			Comentado, porque el concepto.igual no funciona bien "aparentemente".
	 * 		  2> a;adida condicion de parada para salir si no hay datos por enfriamiento. 
	 *          avisa a parametros globales de que hay un nodo menos a traves de DecrementaNodosActivos
	 *          para que el supervisor pueda salir si no hay nodos activos
	 */
	public void run(){
		comunicacionNodos=false;
		int cont_featureSelection = 0;
		NET red=Parametros.getInstancia_Parametros().get_NET();
		while (param.get_Continuar_Busqueda() && datosEntrenamiento.getTamano_conjunto_entrenamiento()>0 ){
			Recibir_Parametros();
			Solucion oldConcepto = this.descripcionNodo.copia();
			//	Si es la primera generacinn no se puede ir a la red por reglas. 
             
			Anet=new Solucion();
			if(comunicacionNodos)
				Recibir_Reglas_Red();	
			
			if (datosEntrenamiento.getTamano_conjunto_entrenamiento()>0){
				//inserta los individuos recibidos en la poblacion
				Solucion Av_U_Anet=new Solucion();
				
				for(int i=0;i<Anet.getTamano_solucion();i++){
					Av.insertarRegla(Anet.get_regla(i).getCopia());
				}
				
				for(int i=0;i<Av.getTamano_solucion();i++)
					Av_U_Anet.insertarRegla(Av.get_regla(i).getCopia());
						
				//Haciendo uso del operador que elijamos selecionamos un conjunto extraemos un conjunto Bv de reglas de la poblacinn.
				//opGen.TokenCompetition(datosEntrenamiento,Av_U_Anet,poblacion); //asignar fitnessToken
				Bv=opGen.US(datosEntrenamiento,Av_U_Anet,poblacion);//seleccion 
				//SeleccionTorneo
				
				/**
				 * Ver la posibilidad de generar otra poblacinn haciendo uso de otro operador como:
				 *  - fitness por crowding
				 *  - fitness con conteo de cubrimiento (cuanto mns Cubierto por distintos individuos menos fitnes).
				 *    es similar al nicho secuencial, porque elimina los casos que y ahan sido cubiertos
				 *  - Si se reparte la poblacinn entre los nodos serna interesante enviar todos los casos 
				 *    no cubiertos cada cierto tiempo al siguiente nodo y evitar que un mismo concepto este en mns de un nodo, 
				 *    para  eso se deberna recibir una lista de las reglas existentes en otros nodos como "mejor regla"y 
				 *    evitar que fuesen exploradas en este nodo    
				 */
				
				// Selecciono aleatoriamente P*|Anet| individuos de Anet y los anado a Bv
				int i=0;
				double num_Individuos_foraneos=Anet.getTamano_solucion()*ratio_adaptacion_foraneo;
				while(i<num_Individuos_foraneos){
					int ind_regla_red=Gen_Aleatorio.Randint(0,Anet.getTamano_solucion()-1);
					Regla aux_sol=Anet.get_regla(ind_regla_red).getCopia();
					Bv.insertarRegla(aux_sol);
					i++;
				}
				
			
				for(int contador_cruces=0;contador_cruces<(Bv.getTamano_solucion() / 2)-1;contador_cruces++) {
					if (Gen_Aleatorio.Rand() < Pcruce) {
						int ind_padre1=contador_cruces*2;
						int ind_padre2=contador_cruces*2+1;
						
						Regla padre1 = Bv.get_regla(ind_padre1);
						Regla padre2 = Bv.get_regla(ind_padre2);
						
						Regla hijo1 = new Regla();
						Regla hijo2 = new Regla();
						
						// Cruzo las dos soluciones
						int id_Cruce=opGen.CruzarCromosmas(padre1, padre2, hijo1, hijo2,datosEntrenamiento);	
						
						opGen.Mutar(hijo1);
						opGen.Mutar(hijo2);
																
						// si la nueva regla evalua al menos un atributo sustituye al padre en bv
						if(hijo1.regla_evaluada()){//vtoken si mejora sustituye
							hijo1.evaluar_solucion(datosEntrenamiento);
							numeroEvaluaciones++;
							Bv.eliminarRegla(ind_padre1);
							Bv.Insertar_regla_Posicion(hijo1,ind_padre1);
						}
		
						if(hijo2.regla_evaluada()){
							hijo2.evaluar_solucion(datosEntrenamiento);
							numeroEvaluaciones++;
							Bv.eliminarRegla(ind_padre2);
							Bv.Insertar_regla_Posicion(hijo2,ind_padre2);
						}
					}
				}		
	
	
				Politica_Reemplazo2();  
				/**  Actualmente aleatoria, ver posibles politicas de reemplazo como:
				 *   (crowding) Buscar aquellos mns parecidos geneticamente para realizar nicho
				 *   posibles comparaciones por torneo de tipo pareto si se implementa 
				 *   un conjunto de valores pareto 
				 */
								
				if((contadorGeneracionesSinComunicacion>=numeroGeneracionesSinComunicacion) & (ratio_migracion_nu>0)){
//					if (param.isParcial() && (cont_featureSelection < 40*numeroGeneracionesSinComunicacion))
//						cont_featureSelection ++;
//					else{	
						comunicacionNodos=true;
						contadorGeneracionesSinComunicacion=0;
						cont_featureSelection=0;
//						if (param.isParcial()) param.depura("Nodo " + this.Id_Nodo + " sale de seleccinn de features", 0);
//						param.setParcial(false);
						
//					}	
				}else{
					contadorGeneracionesSinComunicacion++;
					comunicacionNodos=false;
					
				}
					
				if(comunicacionNodos){
					
					for(int aux=0;aux<Av.getTamano_solucion();aux++){
						descripcionNodo.Insertar_regla_Orden_PI(Av.get_regla(aux).getCopia());
					}
					descripcionNodo=descripcionNodo.get_Concepto(datosEntrenamiento);
				   /*
				    * Envia las reglas que definen al nodo al supervisor
				    */
					enviarReglasPool();
					
					Enviar_Reglas_Red();
					mejores.fin_comunicacion(); //para avisar al supervisor al cabo de NumNodos comunicaciones de recoger el nuevo concepto
					
					costes.add_Comunicaciones(numeroSolucionesEnviadas);
					numeroSolucionesEnviadas=0;
					costes.add_Evaluaciones(numeroEvaluaciones);
					costes.add_EvaluacionesDesglosado(numeroEvaluaciones*datosEntrenamiento.getTamano_conjunto_entrenamiento());
					
					numeroEvaluaciones=0;
					
					if (param.get_enfria() ){
						
					// pdte : mejorar la gestion de enfriamiento, excesivamente simple. debe eliminar snlo cuando el concepto haya convergido de nuevo	    
					//	if (descripcionNodo.Es_Igual(oldConcepto)){
							param.depura(" Nodo "+ this.Id_Nodo + " Enfriando",1);
							param.depura("Nodo " + this.Id_Nodo + " Elimina regla "+ Av.get_regla(Av.get_Indice_Mejor_Regla()),0);
							Av.get_regla(Av.get_Indice_Mejor_Regla()).EliminaEntrenamiento(datosEntrenamiento);
							Av.eliminarRegla(Av.get_Indice_Mejor_Regla());
					//	}
					}
					
					/**************************************************************************************************
					 * gestion de datos de entrenamiento 
					 *************************************************************************************************/
					
					// si se pone num_poco_representados >=1, envia casos no cubiertos
					Dataset Ejemplos_Poco_Cubiertos = Av.get_Ejemplos_Poco_Cubiertos(this.datosEntrenamiento, numPocoRepresentados,eliminarEntrenamiento);
					red.EnviarDatosEntrenamiento(Id_Nodo,Ejemplos_Poco_Cubiertos);
					costes.add_Comunicaciones(Ejemplos_Poco_Cubiertos.getTamano_conjunto_entrenamiento());
					//this.EnviarTodosEjemplos();
					
					// Comprueba si hay nuevos datos de entrenamiento.
					Dataset ejemplosEntrenamiento=red.Get_Datos_Entrenamiento(this.Id_Nodo);
					Recibir_Datos_Entrenamiento(ejemplosEntrenamiento,sustituirEntrenamiento); //nunca se sustituye en EDGAR los datos de entrenamiento
					param.depura(this.Id_Nodo + " Numero de ejemplos  "+this.datosEntrenamiento.getTamano_conjunto_entrenamiento(),2);
							
				}
			}
						
		}
		Parametros.getInstancia_Parametros().depura(" El Nodo (" + this.Id_Nodo + ") ha finalizado la bnsqueda ",-1);
		Parametros.getInstancia_Parametros().decrementaNodosActivos();
		mejores.fin_comunicacion() ; //para despertar al supervisor en caso de ser el ultimo nodo
	}
	
	/**
	 * Este mntodo selecciona a los 5 peores de la poblacinn Av
	 * y los sustituye por los 5 mejores individuos generados en Bv.
	 */
	public void Politica_Reemplazo4()
	{	
		int num_a = Av.getTamano_solucion();
		int num_b = Bv.getTamano_solucion();
		
		HashMap<Integer, Double> pob_a_ordenada_menor_mayor = new HashMap<Integer, Double>();
		ValueComparator comparator = new ValueComparator(pob_a_ordenada_menor_mayor);
		TreeMap<Integer, Double> sorted_list = new TreeMap(comparator);
		
		for(int i=0;i<num_a;i++)
		{
			pob_a_ordenada_menor_mayor.put(i, Av.get_regla(i).get_fitness_token());
		}
		
        sorted_list.putAll(pob_a_ordenada_menor_mayor); 
        int reemplazos = 0;
        Set<Integer> ordenados = sorted_list.keySet();
        
        for (Integer individuo : ordenados) 
        { 
        	if(reemplazos < 5)
            {
        		Av.eliminarRegla(individuo);
        		Av.Insertar_regla_Posicion(Bv.get_regla(reemplazos), Gen_Aleatorio.Randint(0,Av.getTamano_solucion())-1);
        		reemplazos++;
            }
        	else
        		break;
        } 
	}
	/**
	 * Este mntodo selecciona aleatoriamente una serie de indiviuos de la poblacinn actual Av,
	 * y los sustituye por los nuevos individuos generados en Bv.
	 */
	public void Politica_Reemplazo3()
	{	
		int maximoA = Av.getTamano_solucion()-1;
		int maximoB = Bv.getTamano_solucion()-1;
		
		int num_a_sustituir=Gen_Aleatorio.Randint(0,Av.getTamano_solucion()-1);
		
		for(int i=0;i<num_a_sustituir;i++)
		{
			int indiceA = Gen_Aleatorio.Randint(0, maximoA);
			int indiceB = Gen_Aleatorio.Randint(0, maximoB);
			
			Av.eliminarRegla(indiceA);
			Av.Insertar_regla_Posicion(Bv.get_regla(indiceB).getCopia(),indiceA);
		}
	}
	
	/**
	 * Este mntodo selecciona aleatoriamente una serie de indiviuos de la poblacinn actual Av,
	 * y los sustituye por los nuevos individuos generados en Bv.
	 */
	public void Politica_Reemplazo2(){
		
		char reemplazados[]=new char[Av.getTamano_solucion()];
		for(int i=0;i<reemplazados.length;i++) 
			reemplazados[i]='0';
		
		int indice=Gen_Aleatorio.Randint(0,Av.getTamano_solucion()-1);
		int i=0;
		
		while(i<Bv.getTamano_solucion()){
			
			if((Gen_Aleatorio.Rand()<=g) && (reemplazados[indice]=='0')){
				reemplazados[indice]='1';
				//System.out.println(indice+ "_" + (Av.getTamano_solucion()-1));
				Av.eliminarRegla(indice);
				Av.Insertar_regla_Posicion(Bv.get_regla(i).getCopia(),indice);//vtoken si existe en Av esta regla: Bv.get_regla(i).getCopia(), entonces no repetirla (no hacer nada, ni elimino ni inserto). 
				i++;
			}
			indice=(indice+1)%(Av.getTamano_solucion()-1);
		}
	}
	
	
	
/**
 *  	No utilizado
 */	
	public void EnviarEjemplosNoCubiertos(boolean eliminar) {
		NET red=param.get_NET();
		Dataset Ejemplos_No_Cubiertos = Av.get_Ejemplos_No_Cubiertos(this.datosEntrenamiento);
		red.EnviarDatosEntrenamiento(Id_Nodo,Ejemplos_No_Cubiertos);
		
		}
	
	
		
	
	public void EnviarTodosEjemplos() {
		NET red=param.get_NET();
		Dataset copia = new Dataset();
		int num_ejemplos = datosEntrenamiento.getTamano_conjunto_entrenamiento();
		for (int i=0;i < num_ejemplos;i++) {
				EjemploFuzzy ej = datosEntrenamiento.get_EjemploFuzzy(i);
				copia.Insertar_Ejemplo(ej);
				}
		red.EnviarDatosEntrenamiento(Id_Nodo,copia);
		}	
	
	private void Recibir_Datos_Entrenamiento(Dataset ejemplosEntrenamiento,boolean sustituirEntrenamientoDestino){
		param.depura("RECIBIENDO DATOS DE ENTRENAMIENTO ... Nodo " + this +" Nn Datos: "+ ejemplosEntrenamiento.getTamano_conjunto_entrenamiento(),2);
		if (ejemplosEntrenamiento.getTamano_conjunto_entrenamiento()>0){
			if (sustituirEntrenamientoDestino==true) // Solo en caso de estrategias tipo Regal de asignacinn de conjuntos a nodos
				datosEntrenamiento=ejemplosEntrenamiento;
			else{
				int num_ejemplos = ejemplosEntrenamiento.getTamano_conjunto_entrenamiento();
				EjemploFuzzy ej= null;
				for(int i = 0; i< num_ejemplos;i++ ){
					ej = ejemplosEntrenamiento.get_EjemploFuzzy(i).get_CopiaFuzzy();
					datosEntrenamiento.Insertar_Ejemplo_SinRepeticion(ej);
				}
				param.depura("DATOS DE ENTRENAMIENTO Total... Nodo " + this +" Nn Datos: "+ datosEntrenamiento.getTamano_conjunto_entrenamiento(),2);
			}
		}
		else 
			param.depura("Conjunto de entrenamiento vacio hacia nodo "+ this,2);
		
		
		boolean Inicializar_pob_nodo=false; 
		/**
		 *
		 * Si  cuando se reciben nuevos datos no se inicializa la poblacinn
		 * se cambian parcialmente los datos de entrenamiento sin perder la exploracion realizada
		 */
		
		if(!Inicializar_pob_nodo){
				for(int i=0;i<Av.getTamano_solucion();i++){
				Av.get_regla(i).evaluar_solucion(datosEntrenamiento);
				numeroEvaluaciones++;
			}
		}else{
		
			//Inicializo la poblacion inicial Av		
			
			Av=new Solucion();
			// Inserto las Reglas Asignadas
			for(int i=Av.getTamano_solucion();i<poblacion;i++){
				int indiceDatoEntrenamiento=Gen_Aleatorio.Randint(0,datosEntrenamiento.getTamano_conjunto_entrenamiento()-1);
				Regla Nuevo=Operador.Sembrado(datosEntrenamiento.get_EjemploFuzzy(indiceDatoEntrenamiento));
				Nuevo.evaluar_solucion(datosEntrenamiento);
				numeroEvaluaciones++;
				Av.Insertar_regla_Orden_PI(Nuevo);
			}
			descripcionNodo=Av.get_Concepto(datosEntrenamiento);
		}
				
	}
	
	/**
	 * Este mntodo se encarga de recibir los datos de entrenamiento que el supervisor le ha asignado al nodo.
	 * dependiendo de la configuracinn , sustituye a los datos de entrenamiento, los anade, inicializa la poblacinn 
	 * o continua con su entrenamiento permitiendo distintos modelos de distribucinn.
	 * @param reglas_asignadas : Provenientes del buffer de datos de entrenamiento, hay que ejecutar antes get_datos_entrenamiento
	 * @param _datosEntrenamiento: Provenientes del buffer de datos de entrenamiento, hay que ejecutar antes get_datos_entrenamiento
	 */
	
	private void Recibir_Datos_Entrenamiento(Solucion reglas_asignadas, Dataset _datosEntrenamiento){
		param.depura("RECIBIENDO DATOS DE ENTRENAMIENTO ... Nodo " + this +" Nn Datos: "+ _datosEntrenamiento.getTamano_conjunto_entrenamiento(),1);
		if (_datosEntrenamiento.getTamano_conjunto_entrenamiento()>0){
			if (eliminarEntrenamiento==true) 
				datosEntrenamiento=_datosEntrenamiento;
			else{
				int num_ejemplos = _datosEntrenamiento.getTamano_conjunto_entrenamiento();
				EjemploFuzzy ej= null;
				for(int i = 0; i< num_ejemplos;i++ ){
					ej = _datosEntrenamiento.get_EjemploFuzzy(i).get_CopiaFuzzy();
					datosEntrenamiento.Insertar_Ejemplo(ej);
				}
				param.depura("DATOS DE ENTRENAMIENTO Total... Nodo " + this +" Nn Datos: "+ datosEntrenamiento.getTamano_conjunto_entrenamiento(),1);
			}
		}
		else 
			System.out.println("Error se ha asignado un conjunto de entrenamiento vacio");
		
		
		boolean Inicializar_pob_nodo=false; 
		/**
		 *
		 * Si  cuando se reciben nuevos datos no se inicializa la poblacinn
		 * se cambian parcialmente los datos de entrenamiento sin perder la exploracion realizada
		 */
		
		if(!Inicializar_pob_nodo){
		// Esto en el caso de DE NO INICIALIZAR LA POBLACION
			param.depura(" No INICIALIZACION POBLACION NODO",0);
			for(int i=0;i<Av.getTamano_solucion();i++){
				Av.get_regla(i).evaluar_solucion(datosEntrenamiento);
				numeroEvaluaciones++;
			}
		}else{
		
			//Inicializo la poblacion inicial Av		
			
			Av=new Solucion();
			// Inserto las Reglas Asignadas
			if(reglas_asignadas!=null){
				for(int i=0;i<reglas_asignadas.getTamano_solucion();i++){
					Regla Nuevo=reglas_asignadas.get_regla(i).getCopia();
					// Aqui debo llamar a la funcinn de evaluacinn
					Nuevo.evaluar_solucion(datosEntrenamiento);
					numeroEvaluaciones++;
					Av.insertarRegla(Nuevo);
				}	
			}
					
			for(int i=Av.getTamano_solucion();i<poblacion;i++){
				int indiceDatoEntrenamiento=Gen_Aleatorio.Randint(0,datosEntrenamiento.getTamano_conjunto_entrenamiento()-1);
				Regla Nuevo=Operador.Sembrado(datosEntrenamiento.get_EjemploFuzzy(indiceDatoEntrenamiento));
				Nuevo.evaluar_solucion(datosEntrenamiento);
				numeroEvaluaciones++;
				Av.Insertar_regla_Orden_PI(Nuevo);
			}
			descripcionNodo=Av.get_Concepto(datosEntrenamiento);
		}
				
	}
		// Tan solo se envnan a la red reglas nuevas
	
	public Dataset get_Datos_Entrenamiento(){
		return datosEntrenamiento;
	}
	
	public int get_numGeneraciones(){
		return numeroGeneracionesNodo;
	}
	
	public int get_numComunicaciones(){
		return numeroSolucionesEnviadas;
	}
	
	public int get_numEvaluaciones(){
		return numeroEvaluaciones;
	}

	
}
