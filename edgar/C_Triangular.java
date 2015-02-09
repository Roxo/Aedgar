package edgar;

import java.util.ArrayList;

import Dataset.Attributes;

public class C_Triangular implements i_Cobertura{

		ArrayList[] ptos_triangulos;
		int indAtr;
		int pos_regla;
		
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
	}

	private double comprobarPuntos(ArrayList ptos_corte, int i) {
		double pto_anterior, pto_corte;
		pto_corte = (Double) ptos_corte.get(i);
		if(i==0)
			pto_anterior = pto_corte-((pto_corte-(Double) ptos_triangulos[indAtr].get(0))/2);
		else{
			pto_anterior = pto_corte-(pto_corte- (Double)  ptos_corte.get(i-1))/2;
		}
		return pto_anterior;
			
	}
	public Double GetCobertura(int posicion_regla,Double valor) {
		Double inicio=0.0;
		Double alto=0.0;
		Double fin=0.0;
		Double altura=0.0;
		
		if(posicion_regla == 0){
			inicio = (Double)ptos_triangulos[indAtr].get(0);
			alto = (Double)ptos_triangulos[indAtr].get(0);
			fin = (Double)ptos_triangulos[indAtr].get(posicion_regla+1);			
		}else if(posicion_regla == ptos_triangulos[indAtr].size()-1){
			inicio = (Double)ptos_triangulos[indAtr].get(posicion_regla-1);
			alto = (Double)ptos_triangulos[indAtr].get(posicion_regla);
			fin = (Double)ptos_triangulos[indAtr].get(posicion_regla);			
		}
		else{
			inicio = (Double)ptos_triangulos[indAtr].get(posicion_regla-1);
			alto = (Double)ptos_triangulos[indAtr].get(posicion_regla);
			fin = (Double)ptos_triangulos[indAtr].get(posicion_regla+1);
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
