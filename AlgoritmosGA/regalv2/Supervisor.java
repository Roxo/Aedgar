package regalv2;

import java.util.ArrayList;

/**
 * Supervisor hereda de Thread, e implementa el comportamiento del supervisor. 
 * @author Jos� Luis Toscano Mu�oz
 * @version Regal v2.0
 */
public class Supervisor extends Thread{
	
	private Solucion solucionRegal=new Solucion();
	
	ParametrosGlobales parametrosGlobales=ParametrosGlobales.getInstancia_Parametros();	
	private int numeroNodosRegal=parametrosGlobales.getNumeroNodosRegal();
	
	private int contadorComunicacionesSupervisor=0;
	private int contadorEvaluacionesSupervisor=0;
	
	private Solucion reglasAsignadasNodos[]=new Solucion[numeroNodosRegal];
	private Solucion reglasRecibidasNodos[]=new Solucion[numeroNodosRegal];
	private ConjuntoEntrenamiento ejemplosAsignadosNodos[]=new ConjuntoEntrenamiento[numeroNodosRegal];
	
	private BufferSupervisor bufferDatosEntrenamiento=BufferSupervisor.getInstancia();
	private Solucion subConceptoActual=new Solucion();
	private Solucion conceptoFinal=new Solucion();
	private Solucion conceptoEnfriado=new Solucion();
	private ConjuntoEntrenamiento ejemplos;
	
	
	/**
	 * Constructor de la clase.
	 * Asigna un conjunto de entrenamiento al supervisor.
	 * @param _ejemplos conjunto de entrenamiento asignado al supervisor.
	 */
	public Supervisor(ConjuntoEntrenamiento _ejemplos){
		ejemplos=_ejemplos;
	}
	

	
	/**
	 * Inicia el supervisor.
	 */
	public void run(){
		 numeroNodosRegal=parametrosGlobales.getNumeroNodosRegal();
		 ejemplosAsignadosNodos=new ConjuntoEntrenamiento[numeroNodosRegal];
		 parametrosGlobales.getGeneradorAleatorio().setRandom(parametrosGlobales.getSemilla());
		 subConceptoActual=new Solucion();
		 conceptoFinal=new Solucion();	

		 
		int Contador_Generaciones_Regla_Asignada_Nodo[]=new int[numeroNodosRegal];		
		int contadorEpocasSinMejoras=0;

		 ejemplosAsignadosNodos=ejemplos.getNSubConjuntosEjemplos(numeroNodosRegal);
		 bufferDatosEntrenamiento.asignarDatosReglasNodo(ejemplosAsignadosNodos,null);
		 
		 
		 parametrosGlobales.depuracion(" Comienza el Supervisor...  ",0);
		ConjuntoEntrenamiento ejemplos_aux=new ConjuntoEntrenamiento();
		for (int i=0;i<ejemplos.getTama�o();i++){
			ejemplos_aux.insertarEjemplo(ejemplos.getEjemplo(i));
			//ejemplos_aux.insertarEjemplo(ejemplos.getEjemplo(i).getCopia());
		}
	

		// Inicializo las variables necesarias
		for (int i=0;i<numeroNodosRegal;i++){
			 Contador_Generaciones_Regla_Asignada_Nodo[i]=0;
			 reglasAsignadasNodos[i]=new Solucion();
			 reglasRecibidasNodos[i]=new Solucion();
		}
		

		int contador_iteraciones=0;
		boolean continuar_busqueda=true;
		while(continuar_busqueda){
			System.out.println(" regal est� buscando una soluci�n ..." );
			// El supervisor recibe las reglas de la red.
			BufferSupervisor reglasEnviadas=BufferSupervisor.getInstancia();
			Solucion Anet=reglasEnviadas.getMejoresReglas();
			parametrosGlobales.depuracion(" Supervisor: Ha recibido de los nodos --> " + Anet.getTama�o() + " reglas.",2);

			Solucion reglas_Recibidas=new Solucion();
			for(int i=0;i<Anet.getTama�o();i++){
				Regla reglaRecibida=Anet.getRegla(i);
				reglaRecibida.evaluarSolucion(ejemplos_aux);
				contadorEvaluacionesSupervisor++;
				reglas_Recibidas.insertarReglaOrdenPI(reglaRecibida);
			}
			

// 			Primer paso es comprobar si se ha producido una mejora significativa.
			
// 			Defino el objeto donde se almacena el concepto obtenido hasta el momento						
			Solucion Nuevo_Concepto=new Solucion();
			
//			 Ahora copio las reglas de la soluci�n actual BEST
			for(int j=0;j<subConceptoActual.getTama�o();j++){
					Nuevo_Concepto.insertarReglaOrdenPI(subConceptoActual.getRegla(j));
			}			
			
//			Inserto en nuevo concepto las reglas recibidas por los nodos			
			for(int j=0;j<reglas_Recibidas.getTama�o();j++){
				if(!Nuevo_Concepto.existeRegla(reglas_Recibidas.getRegla(j)))
					Nuevo_Concepto.insertarReglaOrdenPI(reglas_Recibidas.getRegla(j));
					//Nuevo_Concepto.insertarReglaOrdenPI(reglas_Recibidas.getRegla(j).get_Copia());
			}	
			
// 			Obengo el nuevo concepto y compruebo si ha mejorado
			Nuevo_Concepto=Nuevo_Concepto.getConcepto(ejemplos_aux);
			if (Nuevo_Concepto.esIgual(subConceptoActual)){
				contadorEpocasSinMejoras++;
				parametrosGlobales.depuracion("EL SUPERVISOR NO ha detectado MEJORA",1);
			}
			else{
				contadorEpocasSinMejoras=0;
				subConceptoActual=Nuevo_Concepto.getConcepto(ejemplos_aux);
				parametrosGlobales.depuracion("EL SUPERVISOR ha detectado MEJORA",1);
			}
				
			
			parametrosGlobales.depuracion(" Supervisor n�mero Reglas del concepto actual: " +subConceptoActual.getTama�o(),1);
			parametrosGlobales.depuracion("Supervisor concepto actual: " +subConceptoActual.getTextoSolucionCompleta(),2);
			
			contador_iteraciones++;	
			
			if(parametrosGlobales.getMecanismoEnfriamiento()){
				if(contador_iteraciones%parametrosGlobales.getNumeroEpocasEntreEnfriamiento()==0){
					parametrosGlobales.depuracion("Supervisor aplica Mecanismo Enfriamiento ",0);
					mecanismoEnfriamiento(subConceptoActual, conceptoEnfriado,conceptoFinal, ejemplos,ejemplos_aux);
					conceptoEnfriado=subConceptoActual.getCopia();
					
//					 Si despu�s de aplicar el mecanismo de enfriamineto no quedan ejemplos, se detiene la b�squeda
					if (ejemplos_aux.getTama�o()==0){
						continuar_busqueda=false;
						parametrosGlobales.depuracion("Supervisor Deteniene B�squeda: No quedan datos de entrenamiento...",0);
					}else{
						aplicarPoliticaCooperacion(subConceptoActual,ejemplos_aux,1);
					}
					
				}else{
					if((contadorEpocasSinMejoras%parametrosGlobales.getNumeroMaximoEpocasReglasAsignadas())==0){		
						aplicarPoliticaCooperacion(subConceptoActual,ejemplos_aux,2);
					}
				}
					
				
				
			}else{
				if(contadorEpocasSinMejoras==0){
					// Si se detecta mejora, se aplica la politica de cooperaci�n
					aplicarPoliticaCooperacion(subConceptoActual,ejemplos_aux,1);
				}else{
					// Si no se detecta mejora, y a transcurrido una serie de �pocas, se aplica la pol�tica de cooperaci�n
					if((contadorEpocasSinMejoras%parametrosGlobales.getNumeroMaximoEpocasReglasAsignadas())==0){		
						aplicarPoliticaCooperacion(subConceptoActual,ejemplos_aux,2);
					}
				}
				
				
				
			}
			

		// Si no se utiliza el mecanismo de enfriamiento, el criterio de parada ser� un cierto n�mero de �pocas sin mejora
		if(contadorEpocasSinMejoras>=parametrosGlobales.getNumeroMaximoEpocasSinMejora() && (parametrosGlobales.getMecanismoEnfriamiento()==false))
				continuar_busqueda=false;
		}
		
		parametrosGlobales.setContinuarBusqueda(false);
		parametrosGlobales.depuracion("FINALIZA EL SUPERVISOR",0);
		
		

		for(int i=0;i<subConceptoActual.getTama�o();i++){
			if(!conceptoFinal.existeRegla(subConceptoActual.getRegla(i))){
				subConceptoActual.getRegla(i).evaluarSolucion(ejemplos);
				contadorEvaluacionesSupervisor++;
				conceptoFinal.insertarReglaOrdenPI(subConceptoActual.getRegla(i));
			}
		}

		conceptoFinal=conceptoFinal.getConcepto(ejemplos);
		parametrosGlobales.depuracion("Concepto Final: \n\n"+conceptoFinal.getTextoSolucionCompleta(),0);
		
		CostesComputacionales costes=CostesComputacionales.getInstancia();
		costes.addEvaluaciones(contadorEvaluacionesSupervisor);
		costes.addComunicaciones(contadorComunicacionesSupervisor);

		putSolucionFinal(conceptoFinal);
		BufferSupervisor.reinicializar();
	}	
	
	

	
	
	/**
	 * Aplica la pol�tica de cooperaci�n
	 * @param BEST Concepto as distribuir entre los nodos
	 * @param ejemplos_aux Conjunto de  datos de entrenamiento a distribuir entre los nodos.
	 * @param ident_Politica Identificador de la pol�tica de cooperaci� a aplicar.
	 */

	public void aplicarPoliticaCooperacion(Solucion BEST,ConjuntoEntrenamiento ejemplos_aux,int ident_Politica){
		ConjuntoEntrenamiento Ejemplos_No_Cubiertos=getEjemplosNoCubiertos(ejemplos_aux,BEST);
		parametrosGlobales.depuracion("     ------   Pol�tica de Cooperaci�n   ------     ", 1);

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
			contadorComunicacionesSupervisor+=ejemplosAsignadosNodos[contNodo].getTama�o();
		}
		
	}
	
	
	
	/**
	 * Mecanismo de enfriamiento. Implementa el mecanismo de enfriamiento.
	 * @param ConceptoActual  soluci�n del que se quitan las reglas que no han mejorado.
	 * @param ConceptoFinal   soluci�n donde se almacena la regla
	 * @param ejemplos_completos conjunto completo de los datos de entremamiento
	 * @param subConjunto_ejemplos Subconjunto de los datos de entrenamiento, se quitan los ejemplos cubiertos por la regla enfriada.
	 */
	
	
	public void mecanismoEnfriamiento(Solucion ConceptoActual, Solucion ConceptoAnterior, Solucion ConceptoFinal,ConjuntoEntrenamiento ejemplos_completos, ConjuntoEntrenamiento subConjunto_ejemplos){
		parametrosGlobales.depuracion(" Supervisor: Aplica el mecanismo de enfriamiento...  ",1);
		if (ConceptoActual.getTama�o()>0){
			int i=0;
			while(i<ConceptoActual.getTama�o()){
				boolean enfriar=false;
				Regla regla_Enfriar=ConceptoActual.getRegla(i);
			
				if(ConceptoAnterior.existeRegla(regla_Enfriar))
					enfriar=true;
				
				if (enfriar){
					// Primero se eliman todos los ejemplos cubiertos por esta regla
					int cont_ejemplos_quitados=0;
					int cont_ej=0;
					while(cont_ej<subConjunto_ejemplos.getTama�o()){
						Ejemplo aux_ej=subConjunto_ejemplos.getEjemplo(cont_ej);
						if(regla_Enfriar.cubreEjemploPositivamente(aux_ej)){
							subConjunto_ejemplos.eliminarEjemplo(cont_ej);
							cont_ejemplos_quitados++;
						}else
							cont_ej++;
					}
					
//					 Eval�a la regla con la poblaci�n total de ejemplos
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
	

	
	/**
	 * Devuelve el conjunto de los datos de entrenamiento no cubiertos por el concepto.
	 * @param conjunto_ejemplos
	 * @param conjunto_reglas
	 * @return conjunto_entrenamiento
	 */
	
	public ConjuntoEntrenamiento getEjemplosNoCubiertos(ConjuntoEntrenamiento conjunto_ejemplos, Solucion conjunto_reglas){
			
		int cont_reglas=0;			
		ArrayList indice_ejemplos_No_Cubiertos=new ArrayList();
		for (int i=0;i<conjunto_ejemplos.getTama�o();i++){
			indice_ejemplos_No_Cubiertos.add(new Integer(i));
		}

		while((indice_ejemplos_No_Cubiertos.size()>0)&&(cont_reglas<conjunto_reglas.getTama�o())){				
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
	 * Pol�tica de cooperaci�n LSE, asigna a cada nodo un conjunto de reglas de la soluci�n actual
	 * y env�a todos los ejemplos cubiertos por las reglas asignadas a cada nodo. 
	 */
	
	public void coopLearningStrategyLSE(Solucion Concepto,ConjuntoEntrenamiento ejemplos,ConjuntoEntrenamiento NoCubiertos){
		char Asignado[][]=new char[numeroNodosRegal][ejemplos.getTama�o()];
		for(int iNodo=0;iNodo<numeroNodosRegal;iNodo++)
			for(int i=0;i<ejemplos.getTama�o();i++)
				Asignado[iNodo][i]='0';
		
		for(int i=0;i<numeroNodosRegal;i++){
			ejemplosAsignadosNodos[i]=new ConjuntoEntrenamiento();
		}
		for(int i=0;i<numeroNodosRegal;i++){
			for (int j=0;j<NoCubiertos.getTama�o();j++){
				ejemplosAsignadosNodos[i].insertarEjemplo(NoCubiertos.getEjemplo(j));
				//ejemplosAsignadosNodos[i].insertarEjemplo(NoCubiertos.getEjemplo(j).getCopia());
			}
		}
		for(int i=0;i<numeroNodosRegal;i++){
			reglasAsignadasNodos[i]=new Solucion();
			reglasRecibidasNodos[i]=new Solucion();
		}
		if(Concepto.getTama�o()>0){	
			if(Concepto.getTama�o()>=numeroNodosRegal){
				// Hay m�s conceptos que nodos
				int ind_Nodo=0;	
				for(int i=0;i<Concepto.getTama�o();i++){
					Regla regla=Concepto.getRegla(i);
					//Regla regla=Concepto.getRegla(i).get_Copia();
					reglasAsignadasNodos[ind_Nodo].insertarReglaOrdenPI(regla);
					reglasRecibidasNodos[ind_Nodo].insertarReglaOrdenPI(regla);
					for(int j=0;j<ejemplos.getTama�o();j++){
						if(regla.cubreEjemplo(ejemplos.getEjemplo(j))&& (Asignado[ind_Nodo][j]=='0')){
							ejemplosAsignadosNodos[ind_Nodo].insertarEjemplo(ejemplos.getEjemplo(j));
							//ejemplosAsignadosNodos[ind_Nodo].insertarEjemplo(ejemplos.getEjemplo(j).getCopia());
							Asignado[ind_Nodo][j]='1';
						}
					}    
					ind_Nodo=(ind_Nodo+1)%numeroNodosRegal;
				}
			}else{
				// En este caso hay un n�mero mayor de nodos que de conceptos
				int ind_Concepto=0;
				for(int i=0;i<numeroNodosRegal;i++){
					Regla regla=Concepto.getRegla(ind_Concepto);
					reglasAsignadasNodos[i].insertarReglaOrdenPI(regla);
					reglasRecibidasNodos[i].insertarReglaOrdenPI(regla);
					for(int j=0;j<ejemplos.getTama�o();j++){
						if(regla.cubreEjemplo(ejemplos.getEjemplo(j))&& (Asignado[i][j]=='0')){
							ejemplosAsignadosNodos[i].insertarEjemplo(ejemplos.getEjemplo(j));
							Asignado[i][j]='1';
						}
					}    
					ind_Concepto=(ind_Concepto+1)%Concepto.getTama�o();
				}	
			}
		}
	}

	
	/**
	 * Pol�tica de cooperaci�n DTSU, asigna a cada nodo un conjunto de reglas de la soluci�n actual
	 * y env�a todos los ejemplos cubiertos por las reglas asignadas a cada nodo. 
	 */
	
	public void coopLearningStrategyDTSU(Solucion Concepto,ConjuntoEntrenamiento ejemplos,ConjuntoEntrenamiento NoCubiertos){
		char Asignado[]=new char[ejemplos.getTama�o()];
		for(int i=0;i<Asignado.length;i++)
			Asignado[i]='0';

		for(int i=0;i<numeroNodosRegal;i++){
			ejemplosAsignadosNodos[i]=new ConjuntoEntrenamiento();
			for (int j=0;j<NoCubiertos.getTama�o();j++){
				ejemplosAsignadosNodos[i].insertarEjemplo(NoCubiertos.getEjemplo(j));
			}
			
			reglasAsignadasNodos[i]=new Solucion();
			reglasRecibidasNodos[i]=new Solucion();
		}
		
		if (Concepto.getTama�o()>0){
			if(Concepto.getTama�o()>=numeroNodosRegal){
				int ind_Nodo=0;	
				for(int i=0;i<Concepto.getTama�o();i++){
					Regla regla=Concepto.getRegla(i);
					reglasAsignadasNodos[ind_Nodo].insertarReglaOrdenPI(regla);
					reglasRecibidasNodos[ind_Nodo].insertarReglaOrdenPI(regla);
					for(int j=0;j<ejemplos.getTama�o();j++){
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
					for(int j=0;j<ejemplos.getTama�o();j++){
						if(regla.cubreEjemploPositivamente(ejemplos.getEjemplo(j))){
							ejemplosAsignadosNodos[i].insertarEjemplo(ejemplos.getEjemplo(j));
							Asignado[j]='1';
						}
					}    
					ind_Concepto=(ind_Concepto+1)%Concepto.getTama�o();
				}
			}
		}
	}
	
	
	
	/**
	 * Asigna el concepto final obtenido por el supervisor e indica que este a finalizado la b�squeda. 
	 * @param _SolucionFinal es la Solucion encotrada por el supervior.
	 */
	public synchronized void putSolucionFinal(Solucion _SolucionFinal){
			solucionRegal=_SolucionFinal;
			notifyAll();
			this.parametrosGlobales.depuracion("Ya existe una soluci�n disponible ...",3);
	}
	
	/**
	 * Devuelve el concepto final alcanzado por el supervisor.
	 * @return es la Solucion encotrada por el supervior.
	 */
	public synchronized Solucion getSolucionFinal(){
		
			try{
				if(solucionRegal.getTama�o()==0){
					this.parametrosGlobales.depuracion("Esperando soluci�n ...",3);
					wait();
					this.parametrosGlobales.depuracion("Soluci�n encontrada ...",3);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			return this.solucionRegal;
	}
		
	
	/**
	 * Reinicia la Solucion final alcanzada por el supervisor.
	 */
	public void reiniciarSolucionFinal(){
		synchronized(solucionRegal){
			solucionRegal= new Solucion();
		}
	}




	
	
	
	
	
	
	
	

}
