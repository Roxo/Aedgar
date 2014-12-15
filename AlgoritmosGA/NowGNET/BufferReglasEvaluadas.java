package NowGNET;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import aleatorios.Aleatorio;
import NowGNET.ParametrosGlobales;
import NowGNET.Regla;




public class BufferReglasEvaluadas{

	  private Hashtable GNodosReglasEvaluadas;
	  private Hashtable GnodosEjemplos;
	  private ParametrosGlobales param=ParametrosGlobales.getInstancia_Parametros();
	  private static BufferReglasEvaluadas instancia=new  BufferReglasEvaluadas();

	  
	private BufferReglasEvaluadas () 
    {
	    GNodosReglasEvaluadas=new Hashtable();
	    GnodosEjemplos=new Hashtable();
    }
	
	public static BufferReglasEvaluadas getInstancia(){
		return instancia;
	}

	
    public void RegistrarNodo(int IdendificadorNodo, Ejemplo E){
    		GnodosEjemplos.put(IdendificadorNodo, E);
    		GNodosReglasEvaluadas.put(IdendificadorNodo, new ArrayList());
    }
    
   
    public Ejemplo getEjemplo(int IdendificadorNodo){   	
    		Ejemplo E=(Ejemplo)GnodosEjemplos.get(IdendificadorNodo);
    		return E;    		
    }
    
    
    public void setEjemplo(int IdendificadorNodo, Ejemplo E){   	
		if (GnodosEjemplos.containsKey(IdendificadorNodo)){
			GnodosEjemplos.remove(IdendificadorNodo);
			GNodosReglasEvaluadas.remove(IdendificadorNodo);
			GNodosReglasEvaluadas.put(IdendificadorNodo, new ArrayList());
		}
		GnodosEjemplos.put(IdendificadorNodo,E.getCopia());	
    }
    
    
    
    public ArrayList getReglasEvaluadas(Integer IdendificadorNodo){
    	ArrayList ListaReglas=null;
    	if (GNodosReglasEvaluadas.containsKey(IdendificadorNodo)){
    		synchronized(IdendificadorNodo){
    			ListaReglas=(ArrayList)GNodosReglasEvaluadas.get(IdendificadorNodo);    		
    			if(ListaReglas!=null){
    				GNodosReglasEvaluadas.remove(IdendificadorNodo);
    			}
    		}
    	}
    	return ListaReglas;
    }
    
    
    public void EnviarReglaEvaluada(Regla R){
    	if (GnodosEjemplos!=null){    		
	    	Enumeration ListaIdentificadores=GnodosEjemplos.keys();    	
	    	while(ListaIdentificadores.hasMoreElements()){	    		
	    		Integer IdentificadorNodo=(Integer)ListaIdentificadores.nextElement();
	    		Ejemplo E=(Ejemplo)GnodosEjemplos.get(IdentificadorNodo);
	    		if (E!=null){
	    			if (R.cubreEjemplo(E)){
	    				synchronized(IdentificadorNodo){	    				
		    				ArrayList ListaReglas=(ArrayList)GNodosReglasEvaluadas.get(IdentificadorNodo);
		    				if(ListaReglas==null) ListaReglas=new ArrayList();
		    				try{
			    				if (!ListaReglas.contains(R)){
			    					ListaReglas.add(R);
			    					GNodosReglasEvaluadas.remove(IdentificadorNodo);
			    					GNodosReglasEvaluadas.put(IdentificadorNodo, ListaReglas);
			    					this.param.depuracion("En el buffer del Nodo G("+IdentificadorNodo+") hay "+ListaReglas.size()+" reglas evaluadas." , 5);
			    				}	
		    				}catch(Exception e){
		    					this.param.depuracion("ERROR("+IdentificadorNodo+") Regla: "+R.getTextoRegla() , 2);
		    				}
	    				}   				
	    			}
	    		}	    		
	    	}	    	
    	}
    }


    
    
    
}
