package edgar;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import Aleatorios.Aleatorio;
;

/**
 * @author Miguel Angel Rodriguez
 * Esta clase simula el funcionamiento de un nodo.
 *
 */

public class Clasificador extends Thread implements i_Nodo{
	
	
	private Estadisticas costes=Estadisticas.getInstancia_buffer();


	// Defino los parametros del nodo, posteriormente se inicializan con los valores de los parametros globales
	
	Aleatorio Gen_Aleatorio;
	private double Pcruce;
	private double  g;
	private int m; //Tamano de la poblacion
		
	// delaracinn de los distintos objetos necesarios.	
	
	
	private String NombreFichero_Resultado="";
	
	private Solucion descripcionNodo = new Solucion();
	private Dataset datosEntrenamiento;
	BufferMejoresReglas mejores;//buffer recepcinn de reglas desde 
	private Solucion Av=new Solucion();  //Poblacinn activa
		private Solucion Bv=null; // Poblacinn de hijos producidos
	
	Parametros param_globales;
	private int numero_generaciones_nodo;
	private int numero_soluciones_enviadas;
	private int numero_evaluaciones;

	boolean envia_mejores; //se envian las mejores reglas a la red o se eligen aleatoriamente.
	
	//todos se copian de los parametros  globales al crear el nodo
	// asi se permite cambiar el modo del nodo sin necesidad de reiniciar el hilo
	private boolean Generar_ficheros_Depuracion=false;
	private int tamanoInicial;
		
	public Clasificador(Dataset _datosEntrenamiento,Solucion reglas){
		datosEntrenamiento=_datosEntrenamiento;
		Recibir_Parametros();
		Av= reglas;
		tamanoInicial=Av.getTamano_solucion();
				
		if (this.Generar_ficheros_Depuracion){
			NombreFichero_Resultado="debugNodos\\Clasificador.txt";	
			try {
	   		 FileOutputStream f = null;
			     f = new FileOutputStream(NombreFichero_Resultado);
			     String cab="Fichero info nodo: Clasificador\n";
			     f.write(cab.getBytes());
			     f.close();			
			}catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
	
		// Inicializo la poblacion inicial Av	
		// se crean m (parametro de nnmero de individuos por nodo) individuos, utilizando el operador de sembrado.
		
		for(int i=0;i<Av.getTamano_solucion();i++) Av.get_regla(i).evaluarPorLongitud(datosEntrenamiento);
			
		descripcionNodo=Av.get_Concepto(datosEntrenamiento);
		this.setName("Clasificador");
	};
	
	public Thread hilo(){
		return this;
	}
	
	
	public void Recibir_Parametros(){
		param_globales=Parametros.getInstancia_Parametros();
		Gen_Aleatorio=new Aleatorio();
		Gen_Aleatorio.Set_random(param_globales.get_Semilla());
		m=param_globales.getPoblacion();
		Pcruce=param_globales.get_Pcruce();
		g=param_globales.get_g();
		envia_mejores = param_globales.get_envia_mejores ();
		this.Generar_ficheros_Depuracion=param_globales.get_generar_Fichero_Debug();
		mejores=BufferMejoresReglas.getInstancia_buffer();
	}
	
	
	public void Enviar_Mejor_Regla(Regla _MejorRegla){
		param_globales.depura("Clasificador Envia Regla al Supervisor->" + _MejorRegla.get_texto_Regla(),2);
		mejores.add_regla(_MejorRegla);
	}
		
	public void run(){
		Recibir_Parametros();
		int numIteraciones =0;
		boolean sinCruce = true;
		while (numIteraciones < 100){
			
		
			
		//Haciendo uso del operador de sufragio Universal selecionamos un conjunto extraemos un conjunto Bv de reglas de la poblacinn.
				Bv=Operador.US_sinSembrado(datosEntrenamiento,Av,m);
				System.out.println("va por " + numIteraciones);	
				for(int contador_cruces=0;contador_cruces<(Bv.getTamano_solucion() / 2)-1;contador_cruces++) {
					if (Gen_Aleatorio.Rand() < Pcruce) {
						int ind_padre1=contador_cruces*2;
						int ind_padre2=contador_cruces*2+1;
						
						Regla padre1 = Bv.get_regla(ind_padre1);
						Regla padre2 = Bv.get_regla(ind_padre2);
						
						Regla hijo1 = new Regla();
						Regla hijo2 = new Regla();
						
						// Cruzo las dos soluciones
						//Operador.cruce_Generalizacion_Especializacion(padre1,padre2,hijo1,hijo2,'G');
						
						if (sinCruce) {
						    hijo1 =padre1.getCopia();	
						    hijo2 =padre2.getCopia();
						}
						else{
							Operador.cruce_2pt(padre1,padre2,hijo1,hijo2);
						}
						
						Operador.Generalizar(hijo1);
						Operador.Generalizar(hijo2);															
						// si la nueva regla no disminuye en cobertura 
						if(hijo1.regla_evaluada()){
							hijo1.evaluarPorLongitud(datosEntrenamiento);
							numero_evaluaciones++;
							if (hijo1.get_NumCasos_Positivos()>= padre1.get_NumCasos_Positivos()){
								Bv.eliminarRegla(ind_padre1);
								Bv.Insertar_regla_Posicion(hijo1,ind_padre1);
						}
						}
						
						
		
						if(hijo2.regla_evaluada()){
							hijo2.evaluarPorLongitud(datosEntrenamiento);
							numero_evaluaciones++;
							if (hijo2.get_NumCasos_Positivos()>= padre2.get_NumCasos_Positivos()){
								Bv.eliminarRegla(ind_padre2);
								Bv.Insertar_regla_Posicion(hijo2,ind_padre2);
						}
						}
					}
				}		
	
			Politica_Reemplazo2();  
			numIteraciones ++;
		 
				
			
					
								
					
		}
  for(int aux=0;aux<Av.getTamano_solucion();aux++){
				descripcionNodo.Insertar_regla_Orden_PI(Av.get_regla(aux).getCopia());
	}
	descripcionNodo=descripcionNodo.get_Concepto(datosEntrenamiento);
		for(int aux=0;aux<descripcionNodo.getTamano_solucion();aux++){
			Enviar_Mejor_Regla(descripcionNodo.get_regla(aux).getCopia());
			numero_soluciones_enviadas++;
}
		
		costes.add_Comunicaciones(numero_soluciones_enviadas);
		numero_soluciones_enviadas=0;
		costes.add_Evaluaciones(numero_evaluaciones);
		costes.add_EvaluacionesDesglosado(numero_evaluaciones*datosEntrenamiento.getTamano_conjunto_entrenamiento());
		
		numero_evaluaciones=0;
		Parametros.getInstancia_Parametros().depura(" El Clasificador  ha finalizado la bnsqueda ",-1);
		
	}
	
	/**
	 * Este mntodo selecciona aleatoriamente una serie de indiviuos de la poblacinn actual Av,
	 * y los sustituye por los nuevos individuos generados en Bv.
	 */
	
	public void Politica_Reemplazo2(){
		
		
		int i=0;
		// Si la poblacion es menor que m, primero rellena hasta m.
		if (Av.getTamano_solucion() < m){
			int gap =m-Av.getTamano_solucion();
			for (;i<gap && i< Bv.getTamano_solucion();i++)
				Av.insertarRegla(Bv.get_regla(i).getCopia());
				
		}
		int tamanio= Av.getTamano_solucion();
		int indice=Gen_Aleatorio.Randint(0,tamanio-1);
		char reemplazados[]=new char[Av.getTamano_solucion()];
		for(int ic=0;ic<reemplazados.length;ic++) reemplazados[ic]='0';
		
		
		while(i<Bv.getTamano_solucion()){
			if((Gen_Aleatorio.Rand()<=g) && (reemplazados[indice]=='0')  ){
				reemplazados[indice]='1';
					if (Av.getTamano_solucion()>=m){
					Av.eliminarRegla(indice);
					Av.Insertar_regla_Posicion(Bv.get_regla(i).getCopia(),indice);
				}
				else{
					Av.insertarRegla(Bv.get_regla(i).getCopia());
				}
					
				
				
				i++;
			}
			indice=(indice+1)%(tamanio-1);
		}
	}
	

	
	public Dataset get_Datos_Entrenamiento(){
		return datosEntrenamiento;
	}
	
	public int get_numGeneraciones(){
		return numero_generaciones_nodo;
	}
	
	public int get_numComunicaciones(){
		return numero_soluciones_enviadas;
	}
	
	public int get_numEvaluaciones(){
		return numero_evaluaciones;
	}

	
}
