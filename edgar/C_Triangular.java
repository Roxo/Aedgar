package edgar;

import java.util.ArrayList;

import Dataset.Attributes;

public class C_Triangular implements i_Cobertura{

		ArrayList[] ptos_triangulos;

		/*
	C_Triangular(ArrayList[] ptos_corte, int indAtr){
		this.indAtr = indAtr;
		ptos_triangulos = new ArrayList[ptos_corte.length];
		ptos_triangulos[indAtr] = new ArrayList();
		ptos_triangulos[indAtr].add(Attributes.getInputAttribute(indAtr).getMinAttribute());
		for (int i = 0; i<ptos_corte[indAtr].size()-1; i++){
			double result = comprobarPuntos(ptos_corte[indAtr],i);
			ptos_triangulos[indAtr].add(result);
		}
		Double ultimo_punto = (Double)ptos_corte[indAtr].get(ptos_corte[indAtr].size()-1);
		Double penultimo = (Double)ptos_corte[indAtr].get(ptos_corte[indAtr].size()-2);
		Double res = (ultimo_punto - penultimo)/2;
		res = ultimo_punto-res;
		ptos_triangulos[indAtr].add(res);
		ptos_triangulos[indAtr].add(ultimo_punto);
	}*/
		/**
		 *  Este método calcula y almacena los puntos necesarios para realizar un triangulo.
		 * 
		 * @param ptos_corte Valores de la plantilla que contienen los puntos de corte de cada atributo que nos da el discretizador
		 */
	
	C_Triangular(ArrayList[] ptos_corte){
		ptos_triangulos = new ArrayList[ptos_corte.length];
		for(int j = 0; j<ptos_corte.length;j++){
			ptos_triangulos[j] = new ArrayList();
			ptos_triangulos[j].add(Attributes.getInputAttribute(j).getMinAttribute());
			for (int i = 0; i<ptos_corte[j].size()-1; i++){
				double result = comprobarPuntos(ptos_corte[j],i,j);
				ptos_triangulos[j].add(result);
			}
			Double ultimo_punto = (Double)ptos_corte[j].get(ptos_corte[j].size()-1);
			if(ptos_corte[j].size()!= 1){
				Double penultimo = (Double)ptos_corte[j].get(ptos_corte[j].size()-2);
				Double res = (ultimo_punto - penultimo)/2;
				res = ultimo_punto-res;
				ptos_triangulos[j].add(res);
			}
			ptos_triangulos[j].add(ultimo_punto);
		}
	}

	
	/**
	 * @param ptos_corte -> Puntos de corte de un atributo
	 * @param i			 -> Selecciona la posición de un atributo para calcular el centro del triangulo entre i e i-1
	 * @param j			 -> Indica el atributo que estamos analizando
	 * @return			 -> Devuelve el punto medio del triangulo entre i e i-1
	 */
	
	private double comprobarPuntos(ArrayList ptos_corte, int i,int j) {
		double pto_anterior, pto_corte;
		pto_corte = (Double) ptos_corte.get(i);
		if(i==0)
			pto_anterior = pto_corte-((pto_corte-(Double) ptos_triangulos[j].get(0))/2);
		else{
			pto_anterior = pto_corte-(pto_corte- (Double)  ptos_corte.get(i-1))/2;
		}
		return pto_anterior;
			
	}
	
	
	
	
	public Double GetCobertura(int posicion_regla,Double valor,int indAtr, int sizeAtr) {
		Double inicio=0.0;
		Double alto=0.0;
		Double fin=0.0;
		Double altura=0.0;
		inicio = (Double)ptos_triangulos[indAtr].get(posicion_regla);
		alto = (Double)ptos_triangulos[indAtr].get(posicion_regla+1);
		fin = (Double)ptos_triangulos[indAtr].get(posicion_regla+2);

		if(posicion_regla == 0){
			if(valor >= inicio && valor<=alto){
				altura = 1.0;
			}
			else if(valor>alto && valor <fin){
	            altura = (fin.doubleValue() - valor) / (fin.doubleValue() - alto.doubleValue());
			}
			return altura;
		}
		else if(posicion_regla == sizeAtr-1){
			if(valor >= inicio && valor<=alto){
	            altura = (valor - inicio.doubleValue()) / (alto.doubleValue() - inicio.doubleValue());
			}
			else if(valor>alto && valor <fin){
				altura=1.0;
			}
			return altura;

		}

		if(valor == alto){
			altura = 1.0;
		}
		else if(valor > inicio && valor<alto){
            altura = (valor - inicio.doubleValue()) / (alto.doubleValue() - inicio.doubleValue());
		}
		else if(valor>alto && valor <fin){
            altura = (fin.doubleValue() - valor) / (fin.doubleValue() - alto.doubleValue());
		}
		return altura;
		
	}

}
