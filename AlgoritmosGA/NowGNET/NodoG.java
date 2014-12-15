package NowGNET;



import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Hashtable;

import aleatorios.Aleatorio;


/**
 * NodoG hereda de Thread, e implementa el comportamiento de los nodos. 
 * El método run(), es el encargado de lanzar el Nodo y este método desarrolla las generaciones que lleva a cabo un nodo.
 * @author José Luis Toscano Muñoz
 * @version Now-GNET v1.0
 */



public class NodoG extends Thread{

	
	
	
	
	


		/**
		 * Buffer nesario para comunicrse con el el supervisor.
		 */
		
		
		
		/**
		 * Este es el objeto necesario para contabilizar el número de evaluciones y comunicaciones.
		 */
		private CostesComputacionales costes=CostesComputacionales.getInstancia();


		
		
		/**
		 * Objeto Aleatorio.
		 */
		private Aleatorio generadorAleatorio;
		
		/**
		 * Probabilidad de cruce.
		 */
		private double probabilidadCruce=0;
		
		/**
		 * Factor de adaptación.
		 */
		private double  g=0;
		
		/**
		 * Tamaño de la población.
		 */
		private int m=0;
		
		/**
		 * Operadores genéticos necesarios para el nodo.
		 */
		private OperadoresGeneticos operadorGenetico=new OperadoresGeneticos();
		
		
		/**
		 * Es el identificador del nodo.
		 */
		private int identificadorNodo=0;
		
		/**
		 * Es el nombre del fichero de depuración.
		 */
		private String nombreFicheroDepuracion="";
		
		/**
		 * Es un objeto Solucion donde el nodo almacena el clasificador que obtiene. 
		 */
		private Solucion descripcionNodo = new Solucion();
		
		/**
		 * Representa el conjunto de entrenamiento que el supervisor a asignado al nodo.
		 */
		private ConjuntoEntrenamiento datosEntrenamiento;
		
		/**
		 * Representa a la población de individuos del nodo.
		 */
		private Solucion Av=new Solucion();


		/**
		 * Representa al conjunto de individuos seleccionados para la reproducción.
		 */
		private Solucion Bv=null;
		
		
		/**
		 * Este objeto contiene los parámetros de configuración de la ejecución.
		 */
		private ParametrosGlobales parametrosGlobales;
			
		/**
		 * Contador del número de comunicaiones del nodo.
		 */
		private int numeroSolucionesEnviadas=0;
		
		/**
		 * Contador del número de evaluaciones relizadas por el nodo.
		 */
		private int numeroEvaluaciones=0;
		
		/**
		 * Este parámetro indica si se genera un fichero con la depuración del nodo.
		 */
		private boolean generarFicherosDepuracion=false;
		
		
		/**
		 * Constructor del nodo, inicializa la población y lee los parámetros de configuración del nodo.
		 * @param _datosEntrenamiento es el conjunto de entrenamiento que se asigna inicialmente al nodo.
		 * @param Identificacion es el identificador del nodo.
		 */
		public NodoG(Ejemplo _e, int Identificacion){
			this.e=_e;
			datosEntrenamiento= new ConjuntoEntrenamiento();
			datosEntrenamiento.insertarEjemplo(_e);
			identificadorNodo=Identificacion;
			recibirParametros();
			BufferReglasNoEvaluadas ReglasEvaluar=BufferReglasNoEvaluadas.getInstancia();
			
			if (this.generarFicherosDepuracion){
				nombreFicheroDepuracion="debugNodos\\"+identificadorNodo+".txt";	
				try {
		   		 FileOutputStream f = null;
				     f = new FileOutputStream(nombreFicheroDepuracion);
				     String cab="Fichero info nodo: "+identificadorNodo+"\n";
				     f.write(cab.getBytes());
				     f.close();			
				}catch (Exception e){
					System.out.println(e.getMessage());
				}
			}
		
			// Inicializo la poblacion inicial Av	
			for(int i=0;i<m;i++){
				int indiceDatoEntrenamiento=generadorAleatorio.randInt(0,datosEntrenamiento.getTamaño()-1);
				Regla NuevaRegla=operadorGenetico.sembrado(datosEntrenamiento.getEjemplo(indiceDatoEntrenamiento));
				// Aqui debo llamar a la función de evaluación
				//NuevaRegla.evaluarSolucion(datosEntrenamiento);	
				ReglasEvaluar.enviarRegla(NuevaRegla);
				//numeroEvaluaciones++;
				//Av.insertarReglaOrdenFitness(NuevaRegla);
			}
			//descripcionNodo=Av.getConcepto(datosEntrenamiento);
			this.setName(identificadorNodo+"");
		};
		
		
		
		/**
		 * Lee los parámetros de configuración del nodo.
		 *
		 */
		public void recibirParametros(){
			parametrosGlobales=ParametrosGlobales.getInstancia_Parametros();
			generadorAleatorio=new Aleatorio();
			generadorAleatorio.setRandom(parametrosGlobales.getSemilla());
			m=parametrosGlobales.getM();

			probabilidadCruce=parametrosGlobales.getProbabilidadCruce();
			g=parametrosGlobales.getG();
			this.generarFicherosDepuracion=parametrosGlobales.getGenerarFicheroDebug();
		}
		
		

		
		
		/**
		 * Envía al buffer del supervisor el conjunto de reglas que describen el nodo.
		 * @param _Descriptor es un objeto Solucion formado por las reglas que definen el nodo.
		 */
		/*public void enviarDescriptorNodo(Solucion _Descriptor){
		// Ahora se envia la mejor regla al supervisor
			BufferSupervisor mejores=BufferSupervisor.getInstancia();
			for(int aux=0;aux<_Descriptor.getTamaño();aux++){
				mejores.addMejorRegla(_Descriptor.getRegla(aux));
				this.parametrosGlobales.depuracion("NODO(" + this.identificadorNodo + ") Envia Regla al Supervisor->" + _Descriptor.getRegla(aux).getTextoRegla(),2);
				numeroSolucionesEnviadas++;
			}
			mejores.finComunicacion();
		}*/

		
		

		
		
		private Ejemplo e=null;
		
		/**
		 * Implementa el funcionamiento del nodo.
		 */
		

		public void run(){
			boolean Primera_Generacion=true;
						
			//Ahora se envian los hijos para ser evaluados
			
			  BufferReglasNoEvaluadas ReglasEvaluar=BufferReglasNoEvaluadas.getInstancia();		  
			  BufferReglasEvaluadas ReglasE=BufferReglasEvaluadas.getInstancia();		
			  BufferSupervisor bufferComunicacionServidor=BufferSupervisor.getInstancia();
			
			  //generadorAleatorio=parametrosGlobales.getGeneradorAleatorio();
			
			  
			  
			  ReglasE.RegistrarNodo(this.identificadorNodo, e);
			  parametrosGlobales.depuracion("Comienza NODO G" + this.identificadorNodo,5);
			  
			  			
			Solucion UltimoConceptoEnviado=null;
			
			while (parametrosGlobales.getContinuarBusqueda()){
				recibirParametros();	


				
				
				// El Nodo COmprueba si el supervisor le ha asigna un nuevo ejemplo 
				Ejemplo NuevoEjemplo=null;
				try {
					NuevoEjemplo = bufferComunicacionServidor.getEjemplo(this.identificadorNodo);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
				if (NuevoEjemplo!=null){
					descripcionNodo=new Solucion();
					this.Av=new Solucion();
					this.e=NuevoEjemplo.getCopia();
					ReglasE.setEjemplo(this.identificadorNodo, e);
					//ReglasEvaluar.Reiniciar();
					this.datosEntrenamiento=new ConjuntoEntrenamiento();
					datosEntrenamiento.insertarEjemplo(e);
					this.parametrosGlobales.depuracion(" Nodo G ("+ this.identificadorNodo + ") Recibe  un nuevo ejemplo e("+ e.getTextoEjemplo()+")",5);
				}
				
				
				
				
				ArrayList ListaReglasEvaluadas=null;
				try {
					parametrosGlobales.depuracion("NODO (" + this.identificadorNodo+") Comprueba si tiene reglas evaluadas",5);
					ListaReglasEvaluadas = ReglasE.getReglasEvaluadas(this.identificadorNodo);					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				
				
				// Con el siguiente código las reglas del Nodo compiten con las nuevas reglas que vienen de otros nodos.
				
				
				if (ListaReglasEvaluadas!=null){
					parametrosGlobales.depuracion("NODO G (" + this.identificadorNodo+") Recibe:  " +ListaReglasEvaluadas.size() + " Reglas evaluadas. ",5);	
					if (ListaReglasEvaluadas.size()>0){
						for (int i=0;i<ListaReglasEvaluadas.size();i++){
							Regla R=(Regla)ListaReglasEvaluadas.get(i);
								if (!Av.existeRegla(R)){
									Av.insertarRegla(R);
								}
						}
					}
				}else{
					parametrosGlobales.depuracion("NODO (" + this.identificadorNodo+") NO RECIBE REGLAS Ejemplo Asignado. "+ e.getTextoEjemplo(),5);
				}
				
					
				
				
				int NumeroReglasEliminar=Av.getTamaño()-parametrosGlobales.getM();
				
				parametrosGlobales.depuracion("NODO G(" + this.identificadorNodo+") debe eliminar " +NumeroReglasEliminar + " Reglas. ",5);
				
				
				if (NumeroReglasEliminar>0){				
					ArrayList IncideReglasEliminar=new ArrayList();
					
					double SumaTotalFitnessInv=0;
					for(int i=0;i<Av.getTamaño();i++){
						if (Av.getRegla(i).getFitness()>0.001)
							//SumaTotalFitnessInv+=Math.pow(Math.E, (-1)*Av.getRegla(i).getFitness());
							SumaTotalFitnessInv+=(1/Av.getRegla(i).getFitness());
						else
							SumaTotalFitnessInv+=1;
							//SumaTotalFitnessInv+=Math.E;
					}
					
					while(NumeroReglasEliminar>0){
						double valorAleatorioInv=SumaTotalFitnessInv*generadorAleatorio.rand();
						double sumaFitnessInv =0;
						int indiceReglaEliminar=-1;
						int i=0;
						while((i<Av.getTamaño())&&(indiceReglaEliminar<0)){
							double siguienteFitness=0;
							if (Av.getRegla(i).getFitness()>0.001)
								//siguienteFitness=sumaFitnessInv+Math.pow(Math.E, ((-1)*Av.getRegla(i).getFitness()));
								siguienteFitness=sumaFitnessInv+(1/Av.getRegla(i).getFitness());
							else
								siguienteFitness=sumaFitnessInv+1;
								//siguienteFitness=sumaFitnessInv+Math.E;
														
							if ((valorAleatorioInv>sumaFitnessInv)&&(valorAleatorioInv<=siguienteFitness)){
								indiceReglaEliminar=i;
								parametrosGlobales.depuracion("NODO G(" + this.identificadorNodo+") Política de reemplazo, individuo a eliminar: " + indiceReglaEliminar,5);
							}
							sumaFitnessInv=siguienteFitness;							
							i++;
							
						}
						

						if (!IncideReglasEliminar.contains(indiceReglaEliminar)){
							int j=0;
							boolean buscarPosicion=true;
							while((j<IncideReglasEliminar.size()) && (buscarPosicion)){
								int indiceActual=(Integer)IncideReglasEliminar.get(j);
								if (indiceActual<indiceReglaEliminar){
									buscarPosicion=false;
								}	
								else{
									j++;	
								}
							}
							IncideReglasEliminar.add(j,indiceReglaEliminar);
							parametrosGlobales.depuracion("NODO G(" + this.identificadorNodo+") ELIMINADO: " + indiceReglaEliminar,5);
							NumeroReglasEliminar--;
						}
					}
					
					for(int i=0;i<IncideReglasEliminar.size();i++){
						int IndiceEliminar=(Integer)IncideReglasEliminar.get(i);
						Av.eliminarRegla(IndiceEliminar);
					}
					
					
					
									
					
					
					
				}else if(NumeroReglasEliminar<0){

					// Si la Población No tiene el tamaño necesario, se siembran nuevos individuos
					int numeroReglasAcrear=parametrosGlobales.getM()-Av.getTamaño();
					int i=0;
					parametrosGlobales.depuracion("NODO G(" + this.identificadorNodo+") Siembra Nº Reglas: "+  numeroReglasAcrear,5);
					while(i<numeroReglasAcrear){
						Regla R=operadorGenetico.sembrado(this.e);
						ReglasEvaluar.enviarRegla(R.getCopia());
						i++;
					}
				}
				
	
		
				
				
				
				
				
				
				
				
				if (NumeroReglasEliminar>=0){
				
			
				/* En las siguientes lineas se seleccionan los individuos que se van a cruzar */
					double SumaTotalFitness=0;
					int indicePadre1=-1;
					int indicePadre2=-1;
					for(int i=0;i<Av.getTamaño();i++){
						SumaTotalFitness+=Av.getRegla(i).getFitness();
					}

					

					double valorAleatorio1=SumaTotalFitness*generadorAleatorio.rand();
					double sumaFitness =0;

					int i=0;
					while((i<Av.getTamaño())&&(indicePadre1<0)){					
						if ((valorAleatorio1>sumaFitness)&&(valorAleatorio1<=Av.getRegla(i).getFitness()+sumaFitness)){
							indicePadre1=i;							
						}
						sumaFitness+=Av.getRegla(i).getFitness();
						i++;
					}
				
				
					while((indicePadre2==indicePadre1)||(indicePadre2==-1)){
						double valorAleatorio2=SumaTotalFitness*generadorAleatorio.rand();
						i=0;
						indicePadre2=-1;
						sumaFitness=0;
						while((i<Av.getTamaño())&&(indicePadre2<0)){
							if ((valorAleatorio2>sumaFitness)&&(valorAleatorio2<=Av.getRegla(i).getFitness()+sumaFitness)){
								indicePadre2=i;
							}
							sumaFitness+=Av.getRegla(i).getFitness();
							i++;
						}
						
						/* En indicePadre1 y indicePadre2 tenomos las indices de los individuso a cruzar*/
						if (indicePadre1==indicePadre2){
							while(indicePadre2==indicePadre1){
								indicePadre2=generadorAleatorio.randInt(0, Av.getTamaño()-1);
							}
						}
					}
				
				
				//parametrosGlobales.depuracion("NODO G(" + this.identificadorNodo+") Cruza los Individuos " + indicePadre1+ " y " +indicePadre2 ,2);
				

					
					
				Regla padre1=null;
				try{
					padre1=Av.getRegla(indicePadre1);
				}catch(Exception e){
					padre1=null;	
				}
				
				Regla padre2=null;
				try{
					padre2=Av.getRegla(indicePadre2);
				}catch(Exception e){
					padre2=null;	
				}
				if ((padre1!=null)&&(padre2!=null)){
					Regla hijo1 = new Regla();
					Regla hijo2 = new Regla();
					
					// Cruzo las dos soluciones
					operadorGenetico.cruzarCromosomas(padre1, padre2, hijo1, hijo2,datosEntrenamiento);									
					operadorGenetico.mutar(hijo1);
					operadorGenetico.mutar(hijo2);
					
					try {
						//parametrosGlobales.depuracion("NODO G(" + this.identificadorNodo+") Envia un nuevo Individuo (" + hijo1.getTextoRegla()+")" ,2);
						ReglasEvaluar.enviarRegla(hijo1.getCopia());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						//parametrosGlobales.depuracion("NODO G(" + this.identificadorNodo+") Envia un nuevo Individuo (" + hijo2.getTextoRegla()+")" ,5);
						ReglasEvaluar.enviarRegla(hijo2.getCopia());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}

				
				// Envio la Regla de Mayor Fitnes
				int indiceReglaMayorFitness=-1;
				double maxFitness=0;
				for(int aux=0;aux<Av.getTamaño();aux++){
					if (Av.getRegla(aux).getFitness()>maxFitness){
						maxFitness=Av.getRegla(aux).getFitness();
						indiceReglaMayorFitness=aux;
					}
				}
				
				if (indiceReglaMayorFitness>-1){					
					try {
						bufferComunicacionServidor.enviarRegla(Av.getRegla(indiceReglaMayorFitness).getCopia());
						numeroSolucionesEnviadas++;
						this.parametrosGlobales.depuracion("NODO(" + this.identificadorNodo + ") Envia Mejor Regla" + Av.getRegla(indiceReglaMayorFitness).getTextoRegla(),3);
						this.parametrosGlobales.depuracion("NODO(" + this.identificadorNodo + ") Mejor Regla: Fit:" + Av.getRegla(indiceReglaMayorFitness).getFitness()+" e+: "+Av.getRegla(indiceReglaMayorFitness).numeroEjemplosCubiertosPositivamente+" e-: "+Av.getRegla(indiceReglaMayorFitness).numeroEjemplosCubiertosNegativamente ,3);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
				}
				
				
				
				
				/*
				
				
				
					// Calculo el descriptor del nodo, el conjunto de reglas que describen el nodo.
					for(int aux=0;aux<Av.getTamaño();aux++){
						descripcionNodo.insertarReglaOrdenPI(Av.getRegla(aux));
					}
					//descripcionNodo=descripcionNodo.getConcepto(datosEntrenamiento);
					descripcionNodo=descripcionNodo.getConceptoReducido(datosEntrenamiento);
					
					
					boolean enviarConcepto=false;
					if (UltimoConceptoEnviado==null){
						enviarConcepto=true;
					}else{
						if (!descripcionNodo.esIgual(UltimoConceptoEnviado)){
							enviarConcepto=true;
						}
					}
					
					if (enviarConcepto){
						// El concepto es enviado al supervisor
						
						for(int aux=0;aux<descripcionNodo.getTamaño();aux++){
							try {
								bufferComunicacionServidor.enviarRegla(descripcionNodo.getRegla(aux));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							this.parametrosGlobales.depuracion("NODO(" + this.identificadorNodo + ") Envia Regla al Supervisor->" + descripcionNodo.getRegla(aux).getTextoRegla(),5);
							numeroSolucionesEnviadas++;
						}
						UltimoConceptoEnviado=descripcionNodo;
					}
					
					*/
					

					
					
					try {
						bufferComunicacionServidor.incrementarNumeroMicroCiclo();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
				}
				
					
			
			
			
		
			
		
			
			this.parametrosGlobales.depuracion(" El Nodo G (" + this.identificadorNodo + ") ha finalizado la búsqueda ",1);
			//BufferNetReglas.reinicializar();
		}
		
		
		
		
		
		
	

		/**
		 * Define la política de reemplazo del nodo.
		 * Para ello, selecciona aleatoriamente una serie de individuos de la población actual Av,
		 * y los sustituye por los nuevos individuos generados en Bv.
		 */

	/*	public void politicaReemplazo(){

			char reemplazados[]=new char[Av.getTamaño()];
			for(int i=0;i<reemplazados.length;i++) reemplazados[i]='0';
			
			int indice=generadorAleatorio.randInt(0,Av.getTamaño()-1);
			int i=0;
			
			while(i<Bv.getTamaño()){
				if((generadorAleatorio.rand()<=g) && (reemplazados[indice]=='0')){
					reemplazados[indice]='1';
					Av.eliminarRegla(indice);
					Av.insertarReglaPosicion(Bv.getRegla(i).getCopia(),indice);
					i++;
				}
				indice=(indice+1)%(Av.getTamaño()-1);
			}
		}
		
		*/
		
		/**
		 * Recibe los datos de entrenamiento que el supervisor le ha asignado al nodo.
		 * @param reglas_asignadas son las reglas que el supervisor a asignado al nodo.
		 * @param _datosEntrenamiento es el conjunto de entrenamineto que el supervisor a asignado al nodo.
		 */
		/*private void recibirDatosEntrenamiento(Solucion reglas_asignadas, ConjuntoEntrenamiento _datosEntrenamiento){
			if (_datosEntrenamiento.getTamaño()>0)
				datosEntrenamiento=_datosEntrenamiento;
			else 
				System.out.println("Error se ha asignado un conjunto de entrenamiento vacio");		
			
				//Inicializo la poblacion inicial Av		
				Av=new Solucion();
				// Inserto las Reglas Asignadas
				if(reglas_asignadas!=null){
					for(int i=0;i<reglas_asignadas.getTamaño();i++){
						Regla Nuevo=reglas_asignadas.getRegla(i);
						// Aqui debo llamar a la función de evaluación
						Nuevo.evaluarSolucion(datosEntrenamiento);
						numeroEvaluaciones++;
						Av.insertarRegla(Nuevo);
					}	
				}
						
				for(int i=Av.getTamaño();i<m;i++){
					int indiceDatoEntrenamiento=generadorAleatorio.randInt(0,datosEntrenamiento.getTamaño()-1);
					Regla Nuevo=operadorGenetico.sembrado(datosEntrenamiento.getEjemplo(indiceDatoEntrenamiento));
					// Aqui debo llamar a la función de evaluación
					Nuevo.evaluarSolucion(datosEntrenamiento);
					numeroEvaluaciones++;
					Av.insertarReglaOrdenPI(Nuevo);
				}
				descripcionNodo=Av.getConcepto(datosEntrenamiento);		
		}*/
}
