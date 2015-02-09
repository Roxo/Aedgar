package edgar;

import java.util.ArrayList;

import Dataset.Attributes;

public class C_Trapezoidal implements i_Cobertura{

		ArrayList[] ptos_trap;
		ArrayList[] ptos_inicio;
		int indAtr;
		int pos_regla;
		
	C_Trapezoidal(ArrayList[] ptos_corte, int indAtr){
		this.indAtr = indAtr;
		ptos_trap = new ArrayList[ptos_corte.length];
		ptos_trap[indAtr] = new ArrayList();
		ptos_inicio = new ArrayList[ptos_corte.length];
		ptos_inicio[indAtr] = new ArrayList();

		//Cojo el valor inicial de este atributo, que no se guarda en la plantilla.
		ptos_trap[indAtr].add(Attributes.getInputAttribute(indAtr).getMinAttribute());
		ptos_inicio[indAtr].add(Attributes.getInputAttribute(indAtr).getMinAttribute());
		for (int i = 0; i<ptos_corte[indAtr].size()-1; i++){
			double result = comprobarPuntos(ptos_corte[indAtr],i);
			ptos_trap[indAtr].add((Double)ptos_corte[indAtr].get(i)-result);
			ptos_trap[indAtr].add((Double)ptos_corte[indAtr].get(i)+result);
			agregar_pto_inicio(ptos_corte[indAtr],i);
		}
		Double ultimo_punto = (Double)ptos_corte[indAtr].get(ptos_corte[indAtr].size()-1);
		Double penultimo = (Double)ptos_corte[indAtr].get(ptos_corte[indAtr].size()-2);
		Double resultado = (ultimo_punto-penultimo)/2;
		resultado = ultimo_punto - resultado;
		ptos_inicio[indAtr].add(resultado);
		ptos_inicio[indAtr].add(ultimo_punto);
		ptos_trap[indAtr].add(ultimo_punto);
	}
	private void agregar_pto_inicio(ArrayList arrayList, int i) {
		double pto_inicio,pto_corte;
		pto_corte = (Double) arrayList.get(i);
		if(i==0)
			pto_inicio = (pto_corte - (Double)ptos_trap[indAtr].get(0))/2;
		else
			pto_inicio = (pto_corte - (Double)arrayList.get(i-1))/2;
		ptos_inicio[indAtr].add(pto_corte-(Double)pto_inicio);
	}
	private double comprobarPuntos(ArrayList ptos_corte, int i) {
		double pto_anterior, pto_posterior, pto_corte;
		pto_corte = (Double) ptos_corte.get(i);
		if(i==0)
			pto_anterior = pto_corte-((pto_corte-(Double) ptos_trap[indAtr].get(0))/2);
		else{
			pto_anterior = pto_corte-(pto_corte- (Double)  ptos_corte.get(i-1))/3;
		}
		
		if(i==ptos_corte.size()-2){
			pto_posterior = pto_corte+(((Double)ptos_corte.get(i+1)-pto_corte)/2);
		}
		else{
			pto_posterior = pto_corte+(((Double)ptos_corte.get(i+1)-pto_corte)/3);
		}
		if(Math.abs(pto_corte-pto_anterior) < Math.abs(pto_posterior-pto_corte)){
			return pto_corte-pto_anterior;
		}else{
			return pto_posterior-pto_corte;
		}
			
	}
	public Double GetCobertura(int posicion_regla,Double valor) {
		Double inicio=0.0;
		Double alto1=0.0;
		Double alto2=0.0;
		Double fin=0.0;
		Double altura=0.0;
		
		inicio = (Double)ptos_inicio[indAtr].get(posicion_regla);
		fin = (Double)ptos_inicio[indAtr].get(posicion_regla+2);
		alto1 = (Double)ptos_trap[indAtr].get(2*posicion_regla);
		alto2 = (Double)ptos_trap[indAtr].get((2*posicion_regla)+1);
		if(valor >= alto1 && valor <= alto2){
			altura = 1.0;
		}
		else if(valor<alto1&&valor>inicio){
            altura = (valor - inicio.doubleValue()) / (alto1.doubleValue() - inicio.doubleValue());
		}
		else if(valor>alto2&&valor<fin){
            altura = (fin.doubleValue() - valor) / (fin.doubleValue() - alto2.doubleValue());
		}
		return altura;
		
	}

}
