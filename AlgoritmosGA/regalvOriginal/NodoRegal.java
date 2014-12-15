package regalvOriginal;

import java.io.FileOutputStream;
import aleatorios.Aleatorio;


/**
 * NodoRegal hereda de Thread, e implementa el comportamiento de los nodos. 
 * El m�todo run(), es el encargado de lanzar el Nodo y este m�todo desarrolla las generaciones que lleva a cabo un nodo.
 * @author Jos� Luis Toscano Mu�oz
 * @version Regal v2.0
 */

public class NodoRegal extends Thread{

	/**
	 * Buffer nesario para comunicrse con el el supervisor.
	 */
	private BufferSupervisor bufferDatosEntrenamineto=BufferSupervisor.getInstancia();
	
	
	/**
	 * Este es el objeto necesario para contabilizar el n�mero de evaluciones y comunicaciones.
	 */
	private CostesComputacionales costes=CostesComputacionales.getInstancia();

	/**
	 * Regula el porcentaje de individuos que migran de una poblaci�n a otra.
	 */
	private double ratioMigracionNu=0;
	
	/**
	 * Define el n�mero de individuos recibidos de la red
	 * que son directamente selecionados para la reproducci�n.
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
	 * Factor de adaptaci�n.
	 */
	private double  g=0;
	
	/**
	 * Tama�o de la poblaci�n.
	 */
	private int m=0;
	
	/**
	 * Operadores gen�ticos necesarios para el nodo.
	 */
	private OperadoresGeneticos operadorGenetico=new OperadoresGeneticos();
	
	
	/**
	 * Es el identificador del nodo.
	 */
	private int identificadorNodo=0;
	
	/**
	 * Es el nombre del fichero de depuraci�n.
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
	 * Representa a la poblaci�n de individuos del nodo.
	 */
	private Solucion Av=new Solucion();


	/**
	 * Representa al conjunto de individuos seleccionados para la reproducci�n.
	 */
	private Solucion Bv=null;
	
	/**
	 * Representa el conjunto de individuos que llegan al nodo desde la red.
	 */
	private Solucion Anet=null;
	
	/**
	 * Representa el n�mero de individuos que se envian y se reciben de la red.
	 */
	private int numeroIndividuosComunicacion=0;
	
	/**
	 * Este objeto contiene los par�metros de configuraci�n de la ejecuci�n.
	 */
	private ParametrosGlobales parametrosGlobales;
		
	/**
	 * Contador del n�mero de comunicaiones del nodo.
	 */
	private int numeroSolucionesEnviadas=0;
	
	/**
	 * Contador del n�mero de evaluaciones relizadas por el nodo.
	 */
	private int numeroEvaluaciones=0;
	
	/**
	 * Este par�metro indica si se genera un fichero con la depuraci�n del nodo.
	 */
	private boolean generarFicherosDepuracion=false;
	

	
	/**
	 * Constructor del nodo, inicializa la poblaci�n y lee los par�metros de configuraci�n del nodo.
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
			int indiceDatoEntrenamiento=generadorAleatorio.randInt(0,datosEntrenamiento.getTama�o()-1);
			Regla NuevaRegla=operadorGenetico.sembrado(datosEntrenamiento.getEjemplo(indiceDatoEntrenamiento));
			// Aqui debo llamar a la funci�n de evaluaci�n
			NuevaRegla.evaluarSolucion(datosEntrenamiento);	
			numeroEvaluaciones++;
			Av.insertarReglaOrdenPI(NuevaRegla);
		}
		
		this.setName(identificadorNodo+"");
	};
	
	
	
	/**
	 * Lee los par�metros de configuraci�n del nodo.
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
			this.parametrosGlobales.depuracion(" Nodo  ("+ this.identificadorNodo+ ") ha recibido de la red: "+ Anet.getTama�o(),3);
			this.parametrosGlobales.depuracion(" Reglas Recibidas Nodo ("+ this.identificadorNodo+ ") "+ Anet.getTextoSolucionCompleta(),4);
			
			int aux=0;
			// Una vez se han recibido las reglas, estas son evaluadas en el nodo.		
			while(aux<Anet.getTama�o()){
				Anet.getRegla(aux).evaluarSolucion(datosEntrenamiento);
				numeroEvaluaciones++;
				aux++;
			}	
		}
	}
	
	/**
	 * Selecciona aleatoriamente un n�mero de reglas y las env�a a la red.
	 *
	 */
	public void enviarReglasRed(){
		Solucion reglas_enviar=new Solucion();
		int i=0;
		int indice_Regla=0;
		while((i<numeroIndividuosComunicacion)&&(Av.getTama�o()>0)){		
			indice_Regla =generadorAleatorio.randInt(0,Av.getTama�o()-1);
			reglas_enviar.insertarRegla(Av.getRegla(indice_Regla));
			Av.eliminarRegla(indice_Regla);
			i++;
			numeroSolucionesEnviadas++;
		}
		
		BufferNetReglas red=ParametrosGlobales.getInstancia_Parametros().getNET();	
		parametrosGlobales.depuracion(" Nodo  ("+ this.identificadorNodo+ ") Envia a la red: "+ reglas_enviar.getTama�o(),3);
		parametrosGlobales.depuracion(" Reglas Enviadas por el Nodo ("+ this.identificadorNodo+ ") "+ reglas_enviar.getTextoSolucionCompleta(),4);
		
		red.enviarReglas(this.identificadorNodo,reglas_enviar);
		numeroSolucionesEnviadas+=reglas_enviar.getTama�o();
		costes.addComunicaciones(numeroSolucionesEnviadas);
		numeroSolucionesEnviadas=0;
	}
	
	

	
	/**
	 * Env�a al buffer del supervisor la mejor regla del nodo.
	 * @param _Descriptor es un objeto Solucion formado por las reglas que definen el nodo.
	 */
	public void enviarMejorRegla(Solucion _Poblacion){
	// Ahora se envia la mejor regla al supervisor
		if (_Poblacion.getTama�o()>0){
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
				// Si es la primera Generaci�n no se puede ir a la red por reglas.
				recibirReglasRed();
			else
				Primera_Generacion=false;
							
			if (datosEntrenamiento.getTama�o()>0){
				//inserto los individuos recibidos en la poblacion
				Solucion Av_U_Anet=new Solucion();
				
				if (Anet!=null){
					for(int i=0;i<Anet.getTama�o();i++){
						Av_U_Anet.insertarRegla(Anet.getRegla(i).getCopia());
						Av.insertarRegla(Anet.getRegla(i).getCopia());
					}
					
				}
				
				for(int i=0;i<Av.getTama�o();i++)
					Av_U_Anet.insertarRegla(Av.getRegla(i).getCopia());
		

				//Haciendo uso del operador de sufragio Universal selecionamos un conjunto extraemos un conjunto Bv de reglas de la poblaci�n.
				Bv=operadorGenetico.sufragioUniversal(datosEntrenamiento,Av_U_Anet,m);
				
				// Selecciono aleatoriamente P*|Anet| individuos de Anet y los a�ado a Bv
				int i=0;
				double num_Individuos_foraneos=0;
				if (Anet!=null) num_Individuos_foraneos=Anet.getTama�o()*ratioAdaptacionForaneo;
				
				while(i<num_Individuos_foraneos){
					int ind_regla_red=generadorAleatorio.randInt(0,Anet.getTama�o()-1);
					Regla aux_sol=Anet.getRegla(ind_regla_red).getCopia();
					Bv.insertarRegla(aux_sol);
					i++;
				}
				
				
				for(int contador_cruces=0;contador_cruces<(Bv.getTama�o() / 2)-1;contador_cruces++) {
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

				this.parametrosGlobales.depuracion(" Nodo ("+ this.identificadorNodo + ") Recibe  "+ejemplosEntrenamiento.getTama�o() +" datos entrenamiento.",1);
				this.parametrosGlobales.depuracion(" Nodo ("+ this.identificadorNodo + ") Recibe Reglas Asignadas: "+reglasAsignadas.getTama�o(),1);
				this.parametrosGlobales.depuracion(" Reglas Asignadas al Nodo ("+ this.identificadorNodo+ ") "+ reglasAsignadas.getTextoSolucionCompleta(),2);

			}
		}
		this.parametrosGlobales.depuracion(" El Nodo (" + this.identificadorNodo + ") ha finalizado la b�squeda ",1);
		BufferNetReglas.reinicializar();
	}
	
	

	/**
	 * Define la pol�tica de reemplazo del nodo.
	 * Para ello, selecciona aleatoriamente una serie de individuos de la poblaci�n actual Av,
	 * y los sustituye por los nuevos individuos generados en Bv.
	 */

	public void politicaReemplazo(){

		char reemplazados[]=new char[Av.getTama�o()];
		for(int i=0;i<reemplazados.length;i++) reemplazados[i]='0';
		
		int indice=generadorAleatorio.randInt(0,Av.getTama�o()-1);
		int i=0;
		
		while(i<Bv.getTama�o()){
			if((generadorAleatorio.rand()<=g) && (reemplazados[indice]=='0')){
				reemplazados[indice]='1';
				Av.eliminarRegla(indice);
				Av.insertarReglaPosicion(Bv.getRegla(i).getCopia(),indice);
				i++;
			}
			indice=(indice+1)%(Av.getTama�o()-1);
		}
	}
	
	
	
	/**
	 * Recibe los datos de entrenamiento que el supervisor le ha asignado al nodo.
	 * @param reglas_asignadas son las reglas que el supervisor a asignado al nodo.
	 * @param _datosEntrenamiento es el conjunto de entrenamineto que el supervisor a asignado al nodo.
	 */
	private void recibirDatosEntrenamiento(Solucion reglas_asignadas, ConjuntoEntrenamiento _datosEntrenamiento){
		if (_datosEntrenamiento.getTama�o()>0)
			datosEntrenamiento=_datosEntrenamiento;
		else 
			System.out.println("Error se ha asignado un conjunto de entrenamiento vacio");		
		
		
		// Se recalcula el fitness de los las reglas 
		for(int i=0;i< Av.getTama�o();i++){
			Av.getRegla(i).evaluarSolucion(datosEntrenamiento);
			numeroEvaluaciones++;
		}
		
		// Inserto las Reglas Asignadas
		if(reglas_asignadas!=null){
			for(int i=0;i<reglas_asignadas.getTama�o();i++){
				Regla Nuevo=reglas_asignadas.getRegla(i);
				// Aqui debo llamar a la funci�n de evaluaci�n
				Nuevo.evaluarSolucion(datosEntrenamiento);
				numeroEvaluaciones++;
				Av.insertarRegla(Nuevo);
			}	
		}
	}
	
}
