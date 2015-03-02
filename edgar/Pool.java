package edgar;

import java.io.FileOutputStream;
import java.util.ArrayList;


public class Pool extends Thread implements i_Supervisor{
	
	
	Parametros param_globales=Parametros.getInstancia_Parametros();	
		
	private int comunicaciones=0;
	private int evaluaciones=0;
	
	
	private Solucion concepto;
	private Solucion conceptoFinal=new Solucion();	
	private Dataset ejemplos;
	
	
	public Thread hilo(){
		return this;
	}
	public Pool(Dataset ejemplos_train){
		ejemplos=ejemplos_train;
	}
	
	public Solucion Get_Clasificador(){
		return conceptoFinal;
	}
	

	

	
	public double  porcentajeClasificador(){
		int[][] ResultadoClasificaciontra =concepto.Clasificar(ejemplos);
		int aciertos = 0;
		for (int i = 0; i < ResultadoClasificaciontra.length; i++) {
			if (ResultadoClasificaciontra[i][0] == ResultadoClasificaciontra[i][1])
				aciertos++;
			}
		
		return ((double) aciertos/ResultadoClasificaciontra.length);
				
	}
	

	
	protected Solucion recibeReglas(BufferMejoresReglas reglasEnviadas,Dataset ejemplos_aux){
		
		Solucion Anet=reglasEnviadas.get_mejores_reglas();
		comunicaciones++;
		
		param_globales.depura(" Pool : Ha recibido de los nodos --> " + Anet.getTamaño_solucion() + " reglas.",0);	
		
		
		Solucion reglas_Recibidas=new Solucion();
		for(int i=0;i<Anet.getTamaño_solucion();i++){
			Regla reglaRecibida=Anet.get_regla(i);
			reglaRecibida.evaluar_solucion(ejemplos_aux);
			evaluaciones++;
			reglas_Recibidas.Insertar_regla_Orden_PI(reglaRecibida);
		}
		return reglas_Recibidas;
	}
	protected Solucion recalculaConcepto (Solucion concepto, Solucion reglas_Recibidas, Dataset ejemplos_aux){
		Solucion Nuevo_Concepto=new Solucion();
		for(int j=0;j<concepto.getTamaño_solucion();j++){
					Nuevo_Concepto.Insertar_regla_Orden_PI(concepto.get_regla(j));
			}			
		// Ahora las reglas recibidas por los nodos
		
			for(int j=0;j<reglas_Recibidas.getTamaño_solucion();j++){
				if(!Nuevo_Concepto.Existe_regla(reglas_Recibidas.get_regla(j)))
					Nuevo_Concepto.Insertar_regla_Orden_PI(reglas_Recibidas.get_regla(j).getCopia());
				else
					param_globales.depura("Ya existe Regla en pool "+ reglas_Recibidas.get_regla(j),3);
			}	
			
// 			devuelve  el concepto ampliado
			return Nuevo_Concepto.get_Concepto(ejemplos_aux);
	}
	

	public void run(){
	
		//_Numero_Nodos_DGA=param_globales.get_Numero_Nodos_DGA();
		
		 param_globales.get_GeneradorAleatorio().Set_random(param_globales.get_Semilla());
		 concepto=new Solucion();
		 conceptoFinal=new Solucion();	
		
		 int contador_iteraciones=0;
		 boolean buscando=true;
		 double porcentaje_anterior = 0.0;	
				 
		 System.out.println(" Supervisor starts  ");
		Dataset ejemplos_aux=new Dataset();
		for (int i=0;i<ejemplos.getTamanho_conjunto_entrenamiento();i++){
			ejemplos_aux.Insertar_Ejemplo(ejemplos.get_EjemploFuzzy(i).get_CopiaFuzzy());
		}
	
		while(buscando){
			
			
			// recibe las reglas de la red.
			// Devolviendo una lista de regals ya evaluadas
			//************************************************************************************************************/
			
				Solucion reglas_Recibidas= recibeReglas(BufferMejoresReglas.getInstancia_buffer(),ejemplos_aux);
				    	
			/** Comprobar si se ha producido una mejora significativa.
    		  *         reglas_Recibidas: almacena las ordenes recibidas en orden de PI (Fitness*casos positivos)
			  * 		Nuevo Concepto: se almacena el concepto obtenido hasta el momento (concepto + las reglas
              * 	    provenientes de los nodos (reglas_Recibidas) para ver si hay mejora
              */
		
			concepto = recalculaConcepto(concepto,reglas_Recibidas,ejemplos_aux);
					
				param_globales.depura(" Supervisor concepto actual: " +concepto.get_texto_solucion_Completa(),0);
				param_globales.depura(" Supervisor número Reglas del concepto actual: " +concepto.getTamaño_solucion(),-1);
				param_globales.depura("Casos no cubiertos: "+ concepto.get_Ejemplos_No_Cubiertos(ejemplos).getTamanho_conjunto_entrenamiento(),-1);
		
			//cambiado criterio a mejora de clasificador  //
			double porcentaje_actual =porcentajeClasificador(); //MARR 2011/01/14 calculart GM; 
			//ISSUE Bloqueo. Cambiado IF
			//if (porcentaje_actual <= (porcentaje_anterior +0.01) && porcentaje_actual> 0.1 || porcentaje_actual > 0.999  ){
			if (porcentaje_actual <= (porcentaje_anterior +0.01) && concepto.getReglas().size() > 0 || porcentaje_actual > 0.999  ){
				//********TUNING CHC****************
				/*if (param_globales.getOptimizaParticiones())
				{   System.out.println("********* OPTIMIZANDO CHC***********"+ ejemplos.plantilla);
					CHCOptimizarParticiones opt = new CHCOptimizarParticiones(ejemplos, concepto);
					synchronized(BufferMejoresReglas.getInstancia_buffer().getBuffer_comunicacion_mejores_reglas()){
						ArrayList[] valoresObtenidos = opt.ejecutar();
						ejemplos.plantilla.set_ValoresAtributos(valoresObtenidos);	
					
					System.out.println("********* FIN CHC***********" + ejemplos.plantilla);
					}
				}
				
				*/
				//********TUNING CHC****************
				contador_iteraciones++;	
				if (param_globales.get_mecanismo_enfriamiento()) 
						param_globales.set_enfria(true);
				if(contador_iteraciones>=param_globales.get_Numero_Maximo_Epocas_Sin_Mejora() )
					buscando=false;
			}
			else
				porcentaje_anterior =porcentaje_actual ; 
			param_globales.depura("porcentaje clasificador: "+ porcentaje_anterior + ", " + porcentaje_actual,-1);
			
		}
		if (Parametros.getInstancia_Parametros().getNodosActivos()== 0) {
			buscando = false;
			System.out.println("Supervisor Detener por: Ultimo nodo");
			
		}
			param_globales.set_Continuar_Busqueda(false);
			
			//Si quiero depurar la solucion final en longitud con todos los ejemplos en un nodo michigan
			if (param_globales.getClasificador()){
					 
				Clasificador iNodo= new Clasificador(ejemplos,concepto);
				iNodo.start();
				
				try {
					iNodo.join();
				} catch (InterruptedException e) {
					
					System.out.println("Error al mejorar la solucion final "); 
					e.printStackTrace();
				}
			}
		
			//S—lo optimiza si hay algœn atributo numŽrico
			
			
//<-- Daniel Albendín
			/**
			 *  Añado al if una condición, la de !aproximativo();
			 */
		/*	if (param_globales.getOptimizaParticiones() && ((Regla)(concepto.getReglas().get(0))).plantilla.numeroAtributosNumericos() > 0 && !Parametros.getInstancia_Parametros().aproximativo())
		//-->
			{
				CHCOptimizarParticiones opt = new CHCOptimizarParticiones(ejemplos, concepto);
				
				ArrayList[] valoresObtenidos = opt.ejecutar();
				ejemplos.plantilla.set_ValoresAtributos(valoresObtenidos);
				
				
			}*/
			
			  // chc --ª modificando  los puntos de corte  y el fitness : calcular la clasificación sobre training. 
			// cromosoma : plantilla
	/*
	 * poblacion 30 plantillas creadas por mutación de la primera
	 *   Selección por torneo-- 5--: coge 5 aleatorias y se queda con la mejor y tiene un padre.
	 *   genera 30 hijos que son la nueva generacion (generacional)
	 *   reinicializacón: sin son muy parecidos (distancia hamming) -ª mo se pueden cruzar --ª si no se puede cruzar ninguno, se regenera la población ( con el mejor) 
	 *   Operador de cruce:  blx-Alpha , 0,5 gebera un nuevo numero que estará entreun minimo-alpha y maximo+alpha,
	 */
			Solucion reglas_Recibidas= recibeReglas(BufferMejoresReglas.getInstancia_buffer(),ejemplos_aux);
	    	
			/** Comprobar si se ha producido una mejora significativa.
    		  *         reglas_Recibidas: almacena las ordenes recibidas en orden de PI (Fitness*casos positivos)
			  * 		Nuevo Concepto: se almacena el concepto obtenido hasta el momento (concepto + las reglas
              * 	    provenientes de los nodos (reglas_Recibidas) para ver si hay mejora
              */
			concepto = recalculaConcepto(concepto,reglas_Recibidas,ejemplos_aux);
			param_globales.depura("Porcentage Clasificador GM Optimizado " + porcentajeClasificadorGM(),-1);
			System.out.println("Supervisor Detener por: Se ha salido del principal");	
			
	
			for(int i=0;i<concepto.getTamaño_solucion();i++){
				if(!conceptoFinal.Existe_regla(concepto.get_regla(i))){
					concepto.get_regla(i).evaluar_solucion(ejemplos);
					evaluaciones++;
					conceptoFinal.Insertar_regla_Orden_PI(concepto.get_regla(i));
				}
			}
	      
			conceptoFinal=conceptoFinal.get_Concepto(ejemplos);
			param_globales.depura("Concepto Final: \n\n"+conceptoFinal.get_texto_solucion_Completa() +"\nPool ha finalizado...",0);
			
			Estadisticas costes=Estadisticas.getInstancia_buffer();
			costes.add_Evaluaciones(evaluaciones);
			costes.add_Comunicaciones(comunicaciones);
			
			SolucionFinal solucionEdgar=SolucionFinal.getInstancia_SolucionFinal();
			//clasificador final
			//solucionRegal.
			solucionEdgar.Put_SolucionFinal(conceptoFinal);
		}

	///////////////////////////////////////////
	// CÓDIGO DE JOSÉ MANUEL GARRIDO MORGADO //
	///////////////////////////////////////////
	
	
	public double  porcentajeClasificadorGM()
	{
		int[][] ResultadoClasificaciontra =concepto.Clasificar(ejemplos);
		double aciertos_clase_1 = 0;
		double aciertos_clase_2 = 0;
		double num_total_clase_1 = 0;
		double num_total_clase_2 = 0;
		for (int i = 0; i < ResultadoClasificaciontra.length; i++) 
		{
			if (ResultadoClasificaciontra[i][0] == 0)
			{
				num_total_clase_1++;
				
				if(ResultadoClasificaciontra[i][0] == ResultadoClasificaciontra[i][1])
					aciertos_clase_1++;
			}
			else
			{
				num_total_clase_2++;
				
				if(ResultadoClasificaciontra[i][0] == ResultadoClasificaciontra[i][1])
					aciertos_clase_2++;
			}
		}
		double dev = Math.sqrt((aciertos_clase_1/num_total_clase_1)*(aciertos_clase_2/num_total_clase_2));
		if(dev == 0)
			dev = 0.1;
		return dev;
	}

}
