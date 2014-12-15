package regalvOriginal;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import aleatorios.Aleatorio;

public class PreprocesamientoDatos {

	public ConjuntoEntrenamiento DuplicaClaseMinoritaria(ConjuntoEntrenamiento Inicial){
		
		Hashtable ListaEjemplosPorClase=new Hashtable();	
		ParametrosGlobales parametrosG=ParametrosGlobales.getInstancia_Parametros();
		
			
		Aleatorio generadorAleatorio=new Aleatorio();
		generadorAleatorio.setRandom(parametrosG.getSemilla());
		
		
		
		int i=0;
		for(i=0;i<Inicial.getTamaño();i++){
			Ejemplo e=Inicial.getEjemplo(i);
			if (e!=null){
				ArrayList ListaPorClase=(ArrayList)ListaEjemplosPorClase.get(e.getClase());
				if (ListaPorClase==null)
					ListaPorClase=new ArrayList();
				ListaPorClase.add(e);
				ListaEjemplosPorClase.put(e.getClase(), ListaPorClase);
			}
		}

		int TamanioClaseMayoritaria=0;
		
		
		Enumeration e = ListaEjemplosPorClase.keys(); 
		while (e.hasMoreElements()) 
		{  
			Object claseEjemplo = e.nextElement();  // do whatever you need with x }
			ArrayList ListaPorClase=(ArrayList)ListaEjemplosPorClase.get(claseEjemplo);
			if (ListaPorClase.size()>TamanioClaseMayoritaria){
				TamanioClaseMayoritaria=ListaPorClase.size();
			}
		}
		

		
		
		e = ListaEjemplosPorClase.keys();
		ArrayList ListaCompletaEjemplos=new ArrayList();
		while (e.hasMoreElements()) 
		{  
			Object claseEjemplo = e.nextElement();  // do whatever you need with x }
			ArrayList ListaPorClase=(ArrayList)ListaEjemplosPorClase.get(claseEjemplo);
			if (ListaPorClase!=null){
				int contadorEjemplos=0;
				
				int j=0;
				while(j<ListaPorClase.size()){
					Ejemplo ej=(Ejemplo)ListaPorClase.get(j);
					ListaCompletaEjemplos.add(ej);
					j++;
				}
				
				while(j<TamanioClaseMayoritaria){
					int indEjemploSeleccionado=generadorAleatorio.randInt(0,ListaPorClase.size()-1);
					Ejemplo ej=(Ejemplo)ListaPorClase.get(indEjemploSeleccionado);
					ListaCompletaEjemplos.add(ej);
					j++;
				}			
			}
		}
		
		ConjuntoEntrenamiento ConjuntoEntrenamientoPreprocesado=new ConjuntoEntrenamiento();
		while(ListaCompletaEjemplos.size()>0){
			int indEjemploSeleccionado=generadorAleatorio.randInt(0,ListaCompletaEjemplos.size()-1);
			Ejemplo ej=(Ejemplo)ListaCompletaEjemplos.get(indEjemploSeleccionado);
			ConjuntoEntrenamientoPreprocesado.insertarEjemplo(ej.getCopia());
			ListaCompletaEjemplos.remove(indEjemploSeleccionado);
		}
		
		return ConjuntoEntrenamientoPreprocesado;
	}
}
