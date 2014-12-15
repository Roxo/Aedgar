package regalvOriginal;

import java.io.FileOutputStream;
import aleatorios.Aleatorio;


/**
 * NodoRegal hereda de Thread, e implementa el comportamiento de los nodos. 
 * El método run(), es el encargado de lanzar el Nodo y este método desarrolla las generaciones que lleva a cabo un nodo.
 * @author José Luis Toscano Muñoz
 * @version Regal v2.0
 */

public class NodoRegal extends Thread{

	/**
	 * Buffer nesario para comunicrse con el el supervisor.
	 */
	private BufferSupervisor bufferDatosEntrenamineto=BufferSupervisor.getInstancia();
	
	
	/**
	 * Este es el objeto necesario para contabilizar el número de evaluciones y comunicaciones.
	 */
	private CostesComputacionales costes=CostesComputacionales.getInstancia();

	/**
	 * Regula el porcentaje de individuos que migran de una población a otra.
	 */
	private double ratioMigracionNu=0;
	
	/**
	 * Define el número de individuos recibidos de la red
	 * que son directamente selecionados para la reproducción.
	 */
	private double ratioAdaptacionForaneo=0;
	
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
//	private Solucion descripcionNodo = new Solucion();
	
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
	 * Representa el conjunto de individuos que llegan al nodo desde la red.
	 */
	private Solucion Anet=null;
	
	/**
	 * Representa el número de individuos que se envian y se reciben de la red.
	 */
	private int numeroIndividuosComunicacion=0;
	
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
	public NodoRegal(ConjuntoEntrenamiento _datosEntrenamiento, int Identificacion){
		datosEntrenamiento=_datosEntrenamiento;
		identificadorNodo=Identificacion;
		recibirParametros();
		numeroIndividuosComunicacion=(int)(this.m*ratioMigracionNu);
		if ((numeroIndividuosComunicacion%2)!=0) numeroIndividuosComunicacion--;
		
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
			NuevaRegla.evaluarSolucion(datosEntrenamiento);	
			numeroEvaluaciones++;
			Av.insertarReglaOrdenPI(NuevaRegla);
		}
		
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
		ratioMigracionNu=parametrosGlobales.getRatioMigracionNu();
		ratioAdaptacionForaneo=parametrosGlobales.getRatioAdaptacionForaneoP();
		probabilidadCruce=parametrosGlobales.getProbabilidadCruce();
		g=parametrosGlobales.getG();
		this.generarFicherosDepuracion=parametrosGlobales.getGenerarFicheroDebug();
	}
	
	
	/**
	 * Recibe de la red un conjunto de reglas y las almacena en la variable Anet.
	 *
	 */
	public void recibirReglasRed(){
		BufferNetReglas red=ParametrosGlobales.getInstancia_Parametros().getNET();
		Anet=red.recibirReglas(identificadorNodo);
		if (Anet!=null){
			this.parametrosGlobales.depuracion(" Nodo  ("+ this.identificadorNodo+ ") ha recibido de la red: "+ Anet.getTamaño(),3);
			this.parametrosGlobales.depuracion(" Reglas Recibidas Nodo ("+ this.identificadorNodo+ ") "+ Anet.getTextoSolucionCompleta(),4);
			
			int aux=0;
			// Una vez se han recibido las reglas, estas son evaluadas en el nodo.		
			while(aux<Anet.getTamaño()){
				Anet.getRegla(aux).evaluarSolucion(datosEntrenamiento);
				numeroEvaluaciones++;
				aux++;
			}	
		}
	}
	
	/**
	 * Selecciona aleatoriamente un número de reglas y las envía a la red.
	 *
	 */
	public void enviarReglasRed(){
		Solucion reglas_enviar=new Solucion();
		int i=0;
		int indice_Regla=0;
		while((i<numeroIndividuosComunicacion)&&(Av.getTamaño()>0)){		
			indice_Regla =generadorAleatorio.randInt(0,Av.getTamaño()-1);
			reglas_enviar.insertarRegla(Av.getRegla(indice_Regla));
			Av.eliminarRegla(indice_Regla);
			i++;
			numeroSolucionesEnviadas++;
		}
		
		BufferNetReglas red=ParametrosGlobales.getInstancia_Parametros().getNET();	
		parametrosGlobales.depuracion(" Nodo  ("+ this.identificadorNodo+ ") Envia a la red: "+ reglas_enviar.getTamaño(),3);
		parametrosGlobales.depuracion(" Reglas Enviadas por el Nodo ("+ this.identificadorNodo+ ") "+ reglas_enviar.getTextoSolucionCompleta(),4);
		
		red.enviarReglas(this.identificadorNodo,reglas_enviar);
		numeroSolucionesEnviadas+=reglas_enviar.getTamaño();
		costes.addComunicaciones(numeroSolucionesEnviadas);
		numeroSolucionesEnviadas=0;
	}
	
	

	
	/**
	 * Envía al buffer del supervisor la mejor regla del nodo.
	 * @param _Descriptor es un objeto Solucion formado por las reglas que definen el nodo.
	 */
	public void enviarMejorRegla(Solucion _Poblacion){
	// Ahora se envia la mejor regla al supervisor
		if (_Poblacion.getTamaño()>0){
			BufferSupervisor mejores=BufferSupervisor.getInstancia();
			Regla r=_Poblacion.getRegla(_Poblacion.getIndiceMejorRegla());
			this.parametrosGlobales.depuracion("NODO(" + this.identificadorNodo + ") Envia Regla al Supervisor->" + r.getTextoRegla(),2);
			numeroSolucionesEnviadas++;
			mejores.addMejorRegla(r,this.identificadorNodo);
		}
	}

	
	/**
	 * Implementa el funcionamiento del nodo.
	 */
	public void run(){
		boolean Primera_Generacion=true;
		
		while (parametrosGlobales.getContinuarBusqueda()){
			recibirParametros();

			Anet=new Solucion();
			if(!Primera_Generacion)
				// Si es la primera Generación no se puede ir a la red por reglas.
				recibirReglasRed();
			else
				Primera_Generacion=false;
							
			if (datosEntrenamiento.getTamaño()>0){
				//inserto los individuos recibidos en la poblacion
				Solucion Av_U_Anet=new Solucion();
				
				if (Anet!=null){
					for(int i=0;i<Anet.getTamaño();i++){
						Av_U_Anet.insertarRegla(Anet.getRegla(i).getCopia());
						Av.insertarRegla(Anet.getRegla(i).getCopia());
					}
					
				}
				
				for(int i=0;i<Av.getTamaño();i++)
					Av_U_Anet.insertarRegla(Av.getRegla(i).getCopia());
		

				//Haciendo uso del operador de sufragio Universal selecionamos un conjunto extraemos un conjunto Bv de reglas de la población.
				Bv=operadorGenetico.sufragioUniversal(datosEntrenamiento,Av_U_Anet,m);
				
				// Selecciono aleatoriamente P*|Anet| individuos de Anet y los añado a Bv
				int i=0;
				double num_Individuos_foraneos=0;
				if (Anet!=null) num_Individuos_foraneos=Anet.getTamaño()*ratioAdaptacionForaneo;
				
				while(i<num_Individuos_foraneos){
					int ind_regla_red=generadorAleatorio.randInt(0,Anet.getTamaño()-1);
					Regla aux_sol=Anet.getRegla(ind_regla_red).getCopia();
					Bv.insertarRegla(aux_sol);
					i++;
				}
				
				
				for(int contador_cruces=0;contador_cruces<(Bv.getTamaño() / 2)-1;contador_cruces++) {
					if (generadorAleatorio.rand() < probabilidadCruce) {
						int ind_padre1=contador_cruces*2;
						int ind_padre2=contador_cruces*2+1;
						
						Regla padre1 = Bv.getRegla(ind_padre1);
						Regla padre2 = Bv.getRegla(ind_padre2);
						
						Regla hijo1 = new Regla();
						Regla hijo2 = new Regla();
						
						// Cruzo las dos soluciones
						operadorGenetico.cruzarCromosomas(padre1, padre2, hijo1, hijo2,datosEntrenamiento);	
						
						
						operadorGenetico.mutar(hijo1);
						operadorGenetico.mutar(hijo2);
							
						// reemplazo los padres por los hijos
						
						// Me aseguro que la nueva regla evalue al menos un atributo
						if(hijo1.reglaEvaluada()){
							hijo1.evaluarSolucion(datosEntrenamiento);
							numeroEvaluaciones++;
							Bv.eliminarRegla(ind_padre1);
							Bv.insertarReglaPosicion(hijo1,ind_padre1);
						}
		
						if(hijo2.reglaEvaluada()){
							hijo2.evaluarSolucion(datosEntrenamiento);
							numeroEvaluaciones++;
							Bv.eliminarRegla(ind_padre2);
							Bv.insertarReglaPosicion(hijo2,ind_padre2);
						}
					}
				}		
							
				politicaReemplazo();
				

				enviarMejorRegla(Av);

				
				costes.addComunicaciones(numeroSolucionesEnviadas);
				numeroSolucionesEnviadas=0;
				costes.addEvaluaciones(numeroEvaluaciones);
				numeroEvaluaciones=0;
				enviarReglasRed();
			}
			
				
			// Ahora el nodo comprueba si hay nuevos datos de entrenamiento.
			int IndNodo=Integer.parseInt(this.getName());
		
			Solucion reglasAsignadas=new Solucion();
			ConjuntoEntrenamiento ejemplosEntrenamiento=new ConjuntoEntrenamiento();
			
			if(bufferDatosEntrenamineto.getDatosReglasNodo(IndNodo,ejemplosEntrenamiento,reglasAsignadas)){
				recibirDatosEntrenamiento(reglasAsignadas,ejemplosEntrenamiento);

				this.parametrosGlobales.depuracion(" Nodo ("+ this.identificadorNodo + ") Recibe  "+ejemplosEntrenamiento.getTamaño() +" datos entrenamiento.",1);
				this.parametrosGlobales.depuracion(" Nodo ("+ this.identificadorNodo + ") Recibe Reglas Asignadas: "+reglasAsignadas.getTamaño(),1);
				this.parametrosGlobales.depuracion(" Reglas Asignadas al Nodo ("+ this.identificadorNodo+ ") "+ reglasAsignadas.getTextoSolucionCompleta(),2);

			}
		}
		this.parametrosGlobales.depuracion(" El Nodo (" + this.identificadorNodo + ") ha finalizado la búsqueda ",1);
		BufferNetReglas.reinicializar();
	}
	
	

	/**
	 * Define la política de reemplazo del nodo.
	 * Para ello, selecciona aleatoriamente una serie de individuos de la población actual Av,
	 * y los sustituye por los nuevos individuos generados en Bv.
	 */

	public void politicaReemplazo(){

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
	
	
	
	/**
	 * Recibe los datos de entrenamiento que el supervisor le ha asignado al nodo.
	 * @param reglas_asignadas son las reglas que el supervisor a asignado al nodo.
	 * @param _datosEntrenamiento es el conjunto de entrenamineto que el supervisor a asignado al nodo.
	 */
	private void recibirDatosEntrenamiento(Solucion reglas_asignadas, ConjuntoEntrenamiento _datosEntrenamiento){
		if (_datosEntrenamiento.getTamaño()>0)
			datosEntrenamiento=_datosEntrenamiento;
		else 
			System.out.println("Error se ha asignado un conjunto de entrenamiento vacio");		
		
		
		// Se recalcula el fitness de los las reglas 
		for(int i=0;i< Av.getTamaño();i++){
			Av.getRegla(i).evaluarSolucion(datosEntrenamiento);
			numeroEvaluaciones++;
		}
		
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
	}
	
}
