package NowGNET;

import java.io.IOException;
import java.util.ArrayList;





import aleatorios.Aleatorio;

/**
 * Supervisor hereda de Thread, e implementa el comportamiento del supervisor. 
 * @author José Luis Toscano Muñoz
 * @version Regal v2.0
 */
public class Supervisor extends Thread{
	
	private Solucion solucionG_NET=new Solucion();
	
	ParametrosGlobales parametrosGlobales=ParametrosGlobales.getInstancia_Parametros();	
	private int numeroNodosG=parametrosGlobales.getNumeroNodosG();
	private int numeroNodosE=parametrosGlobales.getNumeroNodosE();
	
	private int contadorComunicacionesSupervisor=0;
	private int contadorEvaluacionesSupervisor=0;
	

	private Solucion reglasRecibidasNodos=new Solucion();
	
	

	private Solucion subConceptoActual=new Solucion();
	private Solucion conceptoFinal=new Solucion();
	private ConjuntoEntrenamiento ejemplos;
	
	private BufferReglasNoEvaluadas BufferReglasNoEvaluadas=NowGNET.BufferReglasNoEvaluadas.getInstancia();		  
	private BufferReglasEvaluadas BufferReglasEvaluadas=NowGNET.BufferReglasEvaluadas.getInstancia();		
	private BufferSupervisor BufferSupervisor=NowGNET.BufferSupervisor.getInstancia();
	
	
	
	
	/**
	 * Constructor de la clase.
	 * Asigna un conjunto de entrenamiento al supervisor.
	 * @param _ejemplos conjunto de entrenamiento asignado al supervisor.
	 * @throws IOException 
	 */
	public Supervisor(ConjuntoEntrenamiento _ejemplos){
		this.ejemplos=_ejemplos;


		

        



		
	}
	

	
	/**
	 * Inicia el supervisor.
	 */
	public void run(){
		 numeroNodosG=parametrosGlobales.getNumeroNodosG();
		 parametrosGlobales.getGeneradorAleatorio().setRandom(parametrosGlobales.getSemilla());
		 subConceptoActual=new Solucion();
		 conceptoFinal=new Solucion();	

		 int contadorMacroCiclosSinMejoras=0;

		 
		 
		 
		 
	    	NodoG NodosG[]=new NodoG[parametrosGlobales.getNumeroNodosG()];
			NodoE NodosE[]=new NodoE[parametrosGlobales.getNumeroNodosE()];
					 
			 
			
			for(int i=0;i<parametrosGlobales.getNumeroNodosE();i++){
				NodosE[i]=new NodoE(ejemplos, i);
				NodosE[i].start();
			}
			
			
			for(int i=0;i<parametrosGlobales.getNumeroNodosG();i++){
				int IndiceEjemplo =parametrosGlobales.getGeneradorAleatorio().randInt(0, ejemplos.getTamaño()-1);
				NodosG[i]=new NodoG(ejemplos.getEjemplo(IndiceEjemplo), i);
				NodosG[i].start();
			}
						
			
			parametrosGlobales.depuracion(" Comienza el Supervisor...  ",0);
		 
			
			
			
				while(contadorMacroCiclosSinMejoras<parametrosGlobales.getNumeroMacroCiclos()){				
							
					Solucion NuevasReglasEncontradas;
					try{
						NuevasReglasEncontradas=BufferSupervisor.getConceptoEncontrado();	
					}catch(Exception e){
						NuevasReglasEncontradas=null;
					}
					
					
					Solucion Nuevo_Concepto=conceptoFinal.getCopia();
					
					if(NuevasReglasEncontradas!=null){
						parametrosGlobales.depuracion(" Supervisor recibe " + NuevasReglasEncontradas.getTamaño() + " reglas.",2);						
						for(int i=0;i<NuevasReglasEncontradas.getTamaño();i++){
							if(!Nuevo_Concepto.existeRegla(NuevasReglasEncontradas.getRegla(i))){
								Nuevo_Concepto.insertarReglaOrdenFitness(NuevasReglasEncontradas.getRegla(i));
							}
						}
					}
					
					//Ahora eliminamos las reglas innecesarias
					
					//conceptoFinal=conceptoFinal.getConcepto(ejemplos);
					//conceptoFinal=conceptoFinal.getConceptoReducido(ejemplos);									
//		 			Obengo el nuevo concepto y compruebo si ha mejorado
					
					//Nuevo_Concepto=Nuevo_Concepto.getConcepto(ejemplos);
					Nuevo_Concepto=Nuevo_Concepto.getConceptoReducido(ejemplos);
					//Solucion Nuevo_Concepto=conceptoFinal.getConceptoReducido(ejemplos);
					
					if (Nuevo_Concepto.esIgual(conceptoFinal)){
						contadorMacroCiclosSinMejoras++;
						parametrosGlobales.depuracion("El supervisor no detecta mejora",3);
					}
					else{
						//contadorMacroCiclosSinMejoras=0;
						contadorMacroCiclosSinMejoras++;
						conceptoFinal=Nuevo_Concepto.getCopia();						
						parametrosGlobales.depuracion("El supervisor ha detectado MEJORA",3);
					}
					
					
					//Si no es el primer MacroCiclo reasigno los datos de entrenamientos
					if (BufferSupervisor.getNumeroMacroCiclos()>0){
						
						//ConjuntoEntrenamiento EjemplosNoCubiertos=getEjemplosNoCubiertos(ejemplos,conceptoFinal);
						ConjuntoEntrenamiento EjemplosNoCubiertos=getEjemplosMalClasificadosONoCubiertos(ejemplos,conceptoFinal);
						parametrosGlobales.depuracion("Num ejemplos cubiertos negativamente: " + EjemplosNoCubiertos.getTamaño(),1);
						parametrosGlobales.depuracion("Num reglas del clasificador: " + conceptoFinal.getTamaño(),1);

						if(EjemplosNoCubiertos.getTamaño()>=parametrosGlobales.getNumeroNodosG()){
							for(int i=0;i<parametrosGlobales.getNumeroNodosG();i++){
								int indiceEjemplo=parametrosGlobales.getGeneradorAleatorio().randInt(0, EjemplosNoCubiertos.getTamaño()-1);
								Ejemplo E=EjemplosNoCubiertos.getEjemplo(indiceEjemplo);
								EjemplosNoCubiertos.eliminarEjemplo(indiceEjemplo);
								BufferSupervisor.setEjemplo(i, E);
							}
							
						}else{
							for(int i=0;i<parametrosGlobales.getNumeroNodosG();i++){
								Ejemplo E=null;
								if (EjemplosNoCubiertos.getTamaño()>0){
									int indiceEjemplo=parametrosGlobales.getGeneradorAleatorio().randInt(0, EjemplosNoCubiertos.getTamaño()-1);
									E=EjemplosNoCubiertos.getEjemplo(indiceEjemplo);
									EjemplosNoCubiertos.eliminarEjemplo(indiceEjemplo);	
								}else{
									int indiceEjemplo=parametrosGlobales.getGeneradorAleatorio().randInt(0, ejemplos.getTamaño()-1);
									E=ejemplos.getEjemplo(indiceEjemplo);		
								}								
								BufferSupervisor.setEjemplo(i, E);
							}		
						}
						
						// Se reasignan los datos de entrenamiento

					}
					
					
					 BufferReglasEvaluadas ReglasEvaludas=BufferReglasEvaluadas.getInstancia();						 
					 for(int i=0;i<conceptoFinal.getTamaño()-1;i++){
						 ReglasEvaludas.EnviarReglaEvaluada(conceptoFinal.getRegla(i));						 
					 }
					 
					
					
					parametrosGlobales.depuracion("Concepto Actual: Fit("+conceptoFinal.getFitness()+") \n\n"+conceptoFinal.getTextoSolucionCompleta(),2);
					parametrosGlobales.depuracion("Macrociclo: " + BufferSupervisor.getNumeroMacroCiclos(),1);
					BufferSupervisor.incrementarNumeroMacroCiclo();
				}

			
			
			
				parametrosGlobales.setContinuarBusqueda(false);
				parametrosGlobales.depuracion("FINALIZA EL SUPERVISOR",0);		
				
			
				SolucionFinal solucionFinalSupervisor=SolucionFinal.getInstancia_SolucionFinal();
				solucionFinalSupervisor.Put_SolucionFinal(conceptoFinal);
			
				for(int i=0;i<parametrosGlobales.getNumeroNodosE();i++){
					try{
						NodosE[i].stop();	
					}catch(Exception e){
						System.out.println("Error deteniendo al nodo E "+i );
					}
					
				}
				
				
				for(int i=0;i<parametrosGlobales.getNumeroNodosG();i++){
					
					try{
						NodosG[i].stop();	
					}catch(Exception e){
						System.out.println("Error deteniendo al nodo G "+i );
					}
					
				}
					
			
			
		
	}	
	
	
	
	
	
	public ConjuntoEntrenamiento getEjemplosMalClasificadosONoCubiertos(ConjuntoEntrenamiento conjunto_ejemplos, Solucion conjunto_reglas){
		
		ConjuntoEntrenamiento ejemplos_noCubiertos=new ConjuntoEntrenamiento();
		int resultados[][]=conjunto_reglas.clasificar(conjunto_ejemplos);
		
		for (int i=0;i<conjunto_ejemplos.getTamaño();i++){
			if(resultados[i][0]!=resultados[i][1]){
				Ejemplo ej=conjunto_ejemplos.getEjemplo(i).getCopia();
				ejemplos_noCubiertos.insertarEjemplo(ej);
			}
		}
		
		return ejemplos_noCubiertos;
		

		/*
		int cont_reglas=0;			
		ArrayList indice_ejemplos_No_Cubiertos=new ArrayList();
		for (int i=0;i<conjunto_ejemplos.getTamaño();i++){
			indice_ejemplos_No_Cubiertos.add(new Integer(i));
		}
		
		

		while((indice_ejemplos_No_Cubiertos.size()>0)&&(cont_reglas<conjunto_reglas.getTamaño())){				
			Regla regla=conjunto_reglas.getRegla(cont_reglas);
			int i=0;
			
			while(i<indice_ejemplos_No_Cubiertos.size()){
				int ind_Ejemplo=Integer.parseInt(indice_ejemplos_No_Cubiertos.get(i)+"");
				Ejemplo ej=conjunto_ejemplos.getEjemplo(ind_Ejemplo);
				if(regla.cubreEjemploPositivamente(ej)) {
					indice_ejemplos_No_Cubiertos.remove(i);
				}
				else i++; 
			}
			cont_reglas++;			
		}
		
		ConjuntoEntrenamiento ejemplos_noCubiertos=new ConjuntoEntrenamiento();
		int i=0;
		while(i<indice_ejemplos_No_Cubiertos.size()){
			int ind_Ejemplo=Integer.parseInt(indice_ejemplos_No_Cubiertos.get(i)+"");
			Ejemplo ej=conjunto_ejemplos.getEjemplo(ind_Ejemplo);
			ejemplos_noCubiertos.insertarEjemplo(ej);
			i++;
		}
		if (parametrosGlobales.getNivelDepuracion()>0){
			System.out.println("Numero de ejemplos no cubiertos: "+indice_ejemplos_No_Cubiertos.size());	
		}
		return ejemplos_noCubiertos;*/
	}
	
	
	
	
	
	
	
	
	public ConjuntoEntrenamiento getEjemplosNoCubiertos(ConjuntoEntrenamiento conjunto_ejemplos, Solucion conjunto_reglas){
		
		int cont_reglas=0;			
		ArrayList indice_ejemplos_No_Cubiertos=new ArrayList();
		for (int i=0;i<conjunto_ejemplos.getTamaño();i++){
			indice_ejemplos_No_Cubiertos.add(new Integer(i));
		}

		while((indice_ejemplos_No_Cubiertos.size()>0)&&(cont_reglas<conjunto_reglas.getTamaño())){				
			Regla regla=conjunto_reglas.getRegla(cont_reglas);
			int i=0;
			
			while(i<indice_ejemplos_No_Cubiertos.size()){
				int ind_Ejemplo=Integer.parseInt(indice_ejemplos_No_Cubiertos.get(i)+"");
				Ejemplo ej=conjunto_ejemplos.getEjemplo(ind_Ejemplo);
				if(regla.cubreEjemploPositivamente(ej)) {
					indice_ejemplos_No_Cubiertos.remove(i);
				}
				else i++; 
			}
			cont_reglas++;			
		}
		
		ConjuntoEntrenamiento ejemplos_noCubiertos=new ConjuntoEntrenamiento();
		int i=0;
		while(i<indice_ejemplos_No_Cubiertos.size()){
			int ind_Ejemplo=Integer.parseInt(indice_ejemplos_No_Cubiertos.get(i)+"");
			Ejemplo ej=conjunto_ejemplos.getEjemplo(ind_Ejemplo);
			ejemplos_noCubiertos.insertarEjemplo(ej);
			i++;
		}
		if (parametrosGlobales.getNivelDepuracion()>0){
			System.out.println("Numero de ejemplos no cubiertos: "+indice_ejemplos_No_Cubiertos.size());	
		}
		return ejemplos_noCubiertos;
	}
	
	
	
	

	
	
	/**
	 * Aplica la política de cooperación
	 * @param BEST Concepto as distribuir entre los nodos
	 * @param ejemplos_aux Conjunto de  datos de entrenamiento a distribuir entre los nodos.
	 * @param ident_Politica Identificador de la política de cooperació a aplicar.
	 */

/*	public void aplicarPoliticaCooperacion(Solucion BEST,ConjuntoEntrenamiento ejemplos_aux,int ident_Politica){
		ConjuntoEntrenamiento Ejemplos_No_Cubiertos=getEjemplosNoCubiertos(ejemplos_aux,BEST);
		parametrosGlobales.depuracion("     ------   Política de Cooperación   ------     ", 1);

		switch (ident_Politica){
			case 1:
				coopLearningStrategyLSE(BEST,ejemplos_aux,Ejemplos_No_Cubiertos);
				parametrosGlobales.depuracion("     ------       LSE       ------     ", 1);
				break;
			case 2:
				coopLearningStrategyDTSU(BEST,ejemplos_aux,Ejemplos_No_Cubiertos);
				parametrosGlobales.depuracion("     ------       DTSU       ------     ", 1);
				break;
			default:
				coopLearningStrategyLSE(BEST,ejemplos_aux,Ejemplos_No_Cubiertos);
				parametrosGlobales.depuracion("     ------       LSE       ------     ", 1);
		}
		
		bufferDatosEntrenamiento.asignarDatosReglasNodo(ejemplosAsignadosNodos,reglasAsignadasNodos);
		for(int contNodo=0;contNodo<numeroNodosRegal;contNodo++){
			contadorComunicacionesSupervisor+=ejemplosAsignadosNodos[contNodo].getTamaño();
		}
		
	}
	
	*/
	
	/**
	 * Mecanismo de enfriamiento. Implementa el mecanismo de enfriamiento.
	 * @param ConceptoActual  solución del que se quitan las reglas que no han mejorado.
	 * @param ConceptoFinal   solución donde se almacena la regla
	 * @param ejemplos_completos conjunto completo de los datos de entremamiento
	 * @param subConjunto_ejemplos Subconjunto de los datos de entrenamiento, se quitan los ejemplos cubiertos por la regla enfriada.
	 */
	
	
	/*
	public void mecanismoEnfriamiento(Solucion ConceptoActual, Solucion ConceptoAnterior, Solucion ConceptoFinal,ConjuntoEntrenamiento ejemplos_completos, ConjuntoEntrenamiento subConjunto_ejemplos){
		parametrosGlobales.depuracion(" Supervisor: Aplica el mecanismo de enfriamiento...  ",1);
		if (ConceptoActual.getTamaño()>0){
			int i=0;
			while(i<ConceptoActual.getTamaño()){
				boolean enfriar=false;
				Regla regla_Enfriar=ConceptoActual.getRegla(i);
			
				if(ConceptoAnterior.existeRegla(regla_Enfriar))
					enfriar=true;
				
				if (enfriar){
					// Primero se eliman todos los ejemplos cubiertos por esta regla
					int cont_ejemplos_quitados=0;
					int cont_ej=0;
					while(cont_ej<subConjunto_ejemplos.getTamaño()){
						Ejemplo aux_ej=subConjunto_ejemplos.getEjemplo(cont_ej);
						if(regla_Enfriar.cubreEjemploPositivamente(aux_ej)){
							subConjunto_ejemplos.eliminarEjemplo(cont_ej);
							cont_ejemplos_quitados++;
						}else
							cont_ej++;
					}
					
//					 Evalúa la regla con la población total de ejemplos
					regla_Enfriar.evaluarSolucion(ejemplos_completos);
					contadorEvaluacionesSupervisor++;

					parametrosGlobales.depuracion("Ejemplos quitados: " + cont_ejemplos_quitados + " -> Regla enfriada" + regla_Enfriar.getTextoRegla(),1);
					ConceptoFinal.insertarReglaOrdenPI(regla_Enfriar.getCopia());
					ConceptoActual.eliminarRegla(i);
				}else
					i++;
			}
		}
	}
*/	

	
	/**
	 * Devuelve el conjunto de los datos de entrenamiento no cubiertos por el concepto.
	 * @param conjunto_ejemplos
	 * @param conjunto_reglas
	 * @return conjunto_entrenamiento
	 */
	/*
	public ConjuntoEntrenamiento getEjemplosNoCubiertos(ConjuntoEntrenamiento conjunto_ejemplos, Solucion conjunto_reglas){
			
		int cont_reglas=0;			
		ArrayList indice_ejemplos_No_Cubiertos=new ArrayList();
		for (int i=0;i<conjunto_ejemplos.getTamaño();i++){
			indice_ejemplos_No_Cubiertos.add(new Integer(i));
		}

		while((indice_ejemplos_No_Cubiertos.size()>0)&&(cont_reglas<conjunto_reglas.getTamaño())){				
			Regla regla=conjunto_reglas.getRegla(cont_reglas);
			int i=0;
			
			while(i<indice_ejemplos_No_Cubiertos.size()){
				int ind_Ejemplo=Integer.parseInt(indice_ejemplos_No_Cubiertos.get(i)+"");
				Ejemplo ej=conjunto_ejemplos.getEjemplo(ind_Ejemplo);
				if(regla.cubreEjemploPositivamente(ej)) {
					indice_ejemplos_No_Cubiertos.remove(i);
				}
				else i++; 
			}
			cont_reglas++;			
		}
		
		ConjuntoEntrenamiento ejemplos_noCubiertos=new ConjuntoEntrenamiento();
		int i=0;
		while(i<indice_ejemplos_No_Cubiertos.size()){
			int ind_Ejemplo=Integer.parseInt(indice_ejemplos_No_Cubiertos.get(i)+"");
			Ejemplo ej=conjunto_ejemplos.getEjemplo(ind_Ejemplo);
			ejemplos_noCubiertos.insertarEjemplo(ej);
			i++;
		}
		if (parametrosGlobales.getNivelDepuracion()>0){
			System.out.println("Numero de ejemplos no cubiertos: "+indice_ejemplos_No_Cubiertos.size());	
		}
		return ejemplos_noCubiertos;
	}
	
	
*/
	
	
	/**
	 * Política de cooperación LSE, asigna a cada nodo un conjunto de reglas de la solución actual
	 * y envía todos los ejemplos cubiertos por las reglas asignadas a cada nodo. 
	 */
	/*
	public void coopLearningStrategyLSE(Solucion Concepto,ConjuntoEntrenamiento ejemplos,ConjuntoEntrenamiento NoCubiertos){
		char Asignado[][]=new char[numeroNodosRegal][ejemplos.getTamaño()];
		for(int iNodo=0;iNodo<numeroNodosRegal;iNodo++)
			for(int i=0;i<ejemplos.getTamaño();i++)
				Asignado[iNodo][i]='0';
		
		for(int i=0;i<numeroNodosRegal;i++){
			ejemplosAsignadosNodos[i]=new ConjuntoEntrenamiento();
		}
		for(int i=0;i<numeroNodosRegal;i++){
			for (int j=0;j<NoCubiertos.getTamaño();j++){
				ejemplosAsignadosNodos[i].insertarEjemplo(NoCubiertos.getEjemplo(j));
				//ejemplosAsignadosNodos[i].insertarEjemplo(NoCubiertos.getEjemplo(j).getCopia());
			}
		}
		for(int i=0;i<numeroNodosRegal;i++){
			reglasAsignadasNodos[i]=new Solucion();
			reglasRecibidasNodos[i]=new Solucion();
		}
		if(Concepto.getTamaño()>0){	
			if(Concepto.getTamaño()>=numeroNodosRegal){
				// Hay más conceptos que nodos
				int ind_Nodo=0;	
				for(int i=0;i<Concepto.getTamaño();i++){
					Regla regla=Concepto.getRegla(i);
					//Regla regla=Concepto.getRegla(i).get_Copia();
					reglasAsignadasNodos[ind_Nodo].insertarReglaOrdenPI(regla);
					reglasRecibidasNodos[ind_Nodo].insertarReglaOrdenPI(regla);
					for(int j=0;j<ejemplos.getTamaño();j++){
						if(regla.cubreEjemplo(ejemplos.getEjemplo(j))&& (Asignado[ind_Nodo][j]=='0')){
							ejemplosAsignadosNodos[ind_Nodo].insertarEjemplo(ejemplos.getEjemplo(j));
							//ejemplosAsignadosNodos[ind_Nodo].insertarEjemplo(ejemplos.getEjemplo(j).getCopia());
							Asignado[ind_Nodo][j]='1';
						}
					}    
					ind_Nodo=(ind_Nodo+1)%numeroNodosRegal;
				}
			}else{
				// En este caso hay un número mayor de nodos que de conceptos
				int ind_Concepto=0;
				for(int i=0;i<numeroNodosRegal;i++){
					Regla regla=Concepto.getRegla(ind_Concepto);
					reglasAsignadasNodos[i].insertarReglaOrdenPI(regla);
					reglasRecibidasNodos[i].insertarReglaOrdenPI(regla);
					for(int j=0;j<ejemplos.getTamaño();j++){
						if(regla.cubreEjemplo(ejemplos.getEjemplo(j))&& (Asignado[i][j]=='0')){
							ejemplosAsignadosNodos[i].insertarEjemplo(ejemplos.getEjemplo(j));
							Asignado[i][j]='1';
						}
					}    
					ind_Concepto=(ind_Concepto+1)%Concepto.getTamaño();
				}	
			}
		}
	}
*/
	
	/**
	 * Política de cooperación DTSU, asigna a cada nodo un conjunto de reglas de la solución actual
	 * y envía todos los ejemplos cubiertos por las reglas asignadas a cada nodo. 
	 */
	
	/*
	public void coopLearningStrategyDTSU(Solucion Concepto,ConjuntoEntrenamiento ejemplos,ConjuntoEntrenamiento NoCubiertos){
		char Asignado[]=new char[ejemplos.getTamaño()];
		for(int i=0;i<Asignado.length;i++)
			Asignado[i]='0';

		for(int i=0;i<numeroNodosRegal;i++){
			ejemplosAsignadosNodos[i]=new ConjuntoEntrenamiento();
			for (int j=0;j<NoCubiertos.getTamaño();j++){
				ejemplosAsignadosNodos[i].insertarEjemplo(NoCubiertos.getEjemplo(j));
			}
			
			reglasAsignadasNodos[i]=new Solucion();
			reglasRecibidasNodos[i]=new Solucion();
		}
		
		if (Concepto.getTamaño()>0){
			if(Concepto.getTamaño()>=numeroNodosRegal){
				int ind_Nodo=0;	
				for(int i=0;i<Concepto.getTamaño();i++){
					Regla regla=Concepto.getRegla(i);
					reglasAsignadasNodos[ind_Nodo].insertarReglaOrdenPI(regla);
					reglasRecibidasNodos[ind_Nodo].insertarReglaOrdenPI(regla);
					for(int j=0;j<ejemplos.getTamaño();j++){
						if(regla.cubreEjemplo(ejemplos.getEjemplo(j))&&(Asignado[j]=='0')){
							ejemplosAsignadosNodos[ind_Nodo].insertarEjemplo(ejemplos.getEjemplo(j));
							Asignado[j]='1';
						}
					}    
					ind_Nodo=(ind_Nodo+1)%numeroNodosRegal;
				}
			}else{
				int ind_Concepto=0;
				for(int i=0;i<numeroNodosRegal;i++){
					
					if (ind_Concepto==0){
						for(int aux=0;aux<Asignado.length;aux++)
							Asignado[aux]='0';
					}
					
					Regla regla=Concepto.getRegla(ind_Concepto);
					reglasAsignadasNodos[i].insertarReglaOrdenPI(regla);
					reglasRecibidasNodos[i].insertarReglaOrdenPI(regla);
					for(int j=0;j<ejemplos.getTamaño();j++){
						if(regla.cubreEjemploPositivamente(ejemplos.getEjemplo(j))){
							ejemplosAsignadosNodos[i].insertarEjemplo(ejemplos.getEjemplo(j));
							Asignado[j]='1';
						}
					}    
					ind_Concepto=(ind_Concepto+1)%Concepto.getTamaño();
				}
			}
		}
	}
	
	*/
	
/*	*//**
	 * Asigna el concepto final obtenido por el supervisor e indica que este a finalizado la búsqueda. 
	 * @param _SolucionFinal es la Solucion encotrada por el supervior.
	 *//*
	public synchronized void putSolucionFinal(Solucion _SolucionFinal){
			this.solucionG_NET=_SolucionFinal;
			notifyAll();
			this.parametrosGlobales.depuracion("Ya existe una solución disponible ...",3);
	}
	
	*//**
	 * Devuelve el concepto final alcanzado por el supervisor.
	 * @return es la Solucion encotrada por el supervior.
	 *//*
	public synchronized Solucion getSolucionFinal(){
		
			try{
				if(solucionG_NET.getTamaño()==0){
					this.parametrosGlobales.depuracion("Esperando solución ...",3);
					wait();
					this.parametrosGlobales.depuracion("Solución encontrada ...",3);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			return this.solucionG_NET;
	}
		
	
	*//**
	 * Reinicia la Solucion final alcanzada por el supervisor.
	 *//*
	public void reiniciarSolucionFinal(){
		synchronized(solucionG_NET){
			solucionG_NET= new Solucion();
		}
	}
*/



	
	
	
	
	
	
	
	

}
