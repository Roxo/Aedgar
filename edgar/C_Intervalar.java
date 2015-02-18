package edgar;

import java.util.ArrayList;

import Dataset.Attributes;

public class C_Intervalar implements i_Cobertura{

		ArrayList[] ptos_corte;

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
	
	C_Intervalar(ArrayList[] ptos_corte){
		this.ptos_corte = new ArrayList[ptos_corte.length];
		for(int j = 0; j<ptos_corte.length;j++){
			try{
				ptos_corte[j] = convertirpuntos(ptos_corte[j]);
			}catch(Exception e){			
			}
			this.ptos_corte[j] = new ArrayList();
			this.ptos_corte[j].add(Attributes.getInputAttribute(j).getMinAttribute());
			for (int i = 0; i<ptos_corte[j].size()-1; i++){
				this.ptos_corte[j].add(ptos_corte[j]);
			}
			Double ultimo_punto = (Double)ptos_corte[j].get(ptos_corte[j].size()-1);
			this.ptos_corte[j].add(ultimo_punto);
			
		}
	}

	
	/**
	 * @param arrayList Lista de puntos (Valor String, si no, intentamos la conversión)
	 * @return  Un nuevo ArrayList con los contenidos del que le pasamos por parametros convertidos a Double
	 */
	private ArrayList convertirpuntos(ArrayList arrayList) {
		ArrayList ptos_nuevos=new ArrayList();
		for(int i = 0; i<arrayList.size();i++){
			String a = (String) arrayList.get(i);
			Double agregar = Double.parseDouble(a);
			ptos_nuevos.add(agregar);
		}
		return ptos_nuevos;
	}

	/**
	 * @param ptos_corte -> Puntos de corte de un atributo
	 * @param i			 -> Selecciona la posición de un atributo para calcular el centro del triangulo entre i e i-1
	 * @param j			 -> Indica el atributo que estamos analizando
	 * @return			 -> Devuelve el punto medio del triangulo entre i e i-1
	 */
	
	
	
	public Double GetCobertura(int posicion_regla,Double valor,int indAtr, int sizeAtr) {
		Double inicio=0.0;
		Double alto=0.0;
		Double fin=0.0;
		Double altura=0.0;
		inicio = (Double)ptos_corte[indAtr].get(posicion_regla);
		fin = (Double)ptos_corte[indAtr].get(posicion_regla+1);

		if(valor >= inicio && valor<=fin)
			altura = 1.0;
		return altura;
			
	}

}
