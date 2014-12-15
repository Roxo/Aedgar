package NowGNET;

import java.util.ArrayList;



public class BufferReglasNoEvaluadas  {

    private ArrayList listaReglasNoEvaluadas;   
    private static BufferReglasNoEvaluadas instancia=new BufferReglasNoEvaluadas();
    private ParametrosGlobales Parametros;
    
    private BufferReglasNoEvaluadas () 
    {
    	Parametros=ParametrosGlobales.getInstancia_Parametros();
		listaReglasNoEvaluadas=new ArrayList();    
    }
    
	public synchronized static  BufferReglasNoEvaluadas getInstancia(){
		return instancia;
	}
    
    public void enviarRegla(Regla R){
    	synchronized(listaReglasNoEvaluadas){
    		int numeroMaximoReglasSinEval=500;//Parametros.getM()*Parametros.getNumeroNodosG();
    		if(listaReglasNoEvaluadas.size()>numeroMaximoReglasSinEval){
    			try {
    				Parametros.depuracion("NODO G DETENIDO (DEMASIADAS REGLAS PENDIENTES DE EVALUAR...) "+listaReglasNoEvaluadas.size()+" Reglas." ,5);
    				listaReglasNoEvaluadas.wait();
				//} catch (InterruptedException e) {
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
    		}
    		
    		
    		try{
    			listaReglasNoEvaluadas.add(R);
    			Parametros.depuracion("Nueva regla pendiente de ser evaluada. Total reglas Pendientes de Evaluar("+listaReglasNoEvaluadas.size()+")." ,5);
    			listaReglasNoEvaluadas.notifyAll();
			}catch (Exception e){
				//e.printStackTrace();
			}
    	}

    }
    
    public  Regla getRegla(){
    	synchronized(listaReglasNoEvaluadas){
	    	Regla r= null;
	    	boolean ReglaRecibida=false;
	    	while ((ReglaRecibida==false)&&(Parametros.getContinuarBusqueda())){ 
	    	 	if (listaReglasNoEvaluadas.size()>0){	    	 		
	    	 		ReglaRecibida=true;
		    		r=((Regla)listaReglasNoEvaluadas.get(0)).getCopia();
		    		listaReglasNoEvaluadas.remove(0);
		    		if (listaReglasNoEvaluadas.size()==0){
		    			try{
		    				listaReglasNoEvaluadas.notifyAll();	
		    			}catch(Exception e){
		    				 if (listaReglasNoEvaluadas!=null){
			    	 				Parametros.depuracion("Nº Reglas en listaReglasNoEvaluadas: " +listaReglasNoEvaluadas.size() ,2);
			    	 			 }else{
			    	 				Parametros.depuracion("listaReglasNoEvaluadas Es NULL...",2);	    	
			    	 			 }
									e.printStackTrace();
		    			}
		    		}
	    	 	}else{
	    	 		Parametros.depuracion("Nodo E parado esperando recibir reglas...",5);	    	 		
	    	 		 try{
	    	 			 
						 listaReglasNoEvaluadas.wait();						 
						 ReglaRecibida=false;
	    	 		 }catch (InterruptedException e) {
							// TODO Auto-generated catch block
	    	 			 if (listaReglasNoEvaluadas!=null){
	    	 				Parametros.depuracion("Nº Reglas en listaReglasNoEvaluadas" +listaReglasNoEvaluadas.size() ,2);
	    	 			 }else{
	    	 				Parametros.depuracion("listaReglasNoEvaluadas Es NULL...",2);	    	
	    	 			 }
							e.printStackTrace();
					}
	    	 	}
	    	}	    	
	    	return r;
    	}
    	
    	}
    
    	public static void Reiniciar(){
    	   	//synchronized(listaReglasNoEvaluadas){
    		instancia=new BufferReglasNoEvaluadas();
    	   	/*	try{
    	   			//listaReglasNoEvaluadas.notifyAll();
    	   			
        	   		listaReglasNoEvaluadas=new ArrayList();	
    	   		}catch(Exception e){
    	   			
    	   		}
    	   		*/    			
    	   	//}
    	    
    	}
    	
    }
    

