package edgar;

import java.util.ArrayList;

import Dataset.Attributes;

/**
 * @author Daniel Albendín
 * 
 *  Implementación de la cobertura de una regla a un ejemplo con trapecios.
 *
 */
public class C_Trapezoidal implements i_Cobertura{

		ArrayList[] ptos_trap;
		
	/*C_Trapezoidal(ArrayList[] ptos_corte, int indAtr){
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
		}
		Double ultimo_punto = (Double)ptos_corte[indAtr].get(ptos_corte[indAtr].size()-1);
	/*	Double penultimo = (Double)ptos_corte[indAtr].get(ptos_corte[indAtr].size()-2);
		Double resultado = (ultimo_punto-penultimo)/2;
		resultado = ultimo_punto - resultado;
		ptos_inicio[indAtr].add(resultado);
		ptos_inicio[indAtr].add(ultimo_punto);
		ptos_trap[indAtr].add(ultimo_punto);
	}*/
	
	/**
	 *  Este método calcula y almacena los puntos necesarios para realizar un trapecio
	 * 
	 * @param ptos_corte Valores de la plantilla que contienen los puntos de corte de cada atributo que nos da el discretizador
	 */
	C_Trapezoidal(ArrayList[] ptos_corte){
		ptos_trap = new ArrayList[ptos_corte.length];
		for(int j = 0;j<ptos_corte.length;j++){
			ptos_trap[j] = new ArrayList();
			//Cojo el valor inicial de este atributo, que no se guarda en la plantilla.
			ptos_trap[j].add(Attributes.getInputAttribute(j).getMinAttribute());
			for (int i = 0; i<ptos_corte[j].size()-1; i++){
				double result = comprobarPuntos(ptos_corte[j],i,j);
				ptos_trap[j].add((Double)ptos_corte[j].get(i)-result);
				ptos_trap[j].add((Double)ptos_corte[j].get(i)+result);
			}
			Double ultimo_punto = (Double)ptos_corte[j].get(ptos_corte[j].size()-1);
			ptos_trap[j].add(ultimo_punto);
		}
	}
	/*private void agregar_pto_inicio(ArrayList arrayList, int i) {
		double pto_inicio,pto_corte;
		pto_corte = (Double) arrayList.get(i);
		if(i==0)
			pto_inicio = (pto_corte - (Double)ptos_trap[indAtr].get(0))/2;
		else
			pto_inicio = (pto_corte - (Double)arrayList.get(i-1))/2;
		ptos_inicio[indAtr].add(pto_corte-(Double)pto_inicio);
	}
*/
	
	/**
	 * @param ptos_corte -> Puntos de corte de un atributo
	 * @param i			 -> Selecciona la posición de un atributo para calcular el alfa del trapecio
	 * @param j			 -> Indica el atributo que estamos analizando
	 * @return			 -> Devuelve la distancia más pequeña de un punto ptos_corte(i) entre el anterior o el siguiente
	 */
	private double comprobarPuntos(ArrayList ptos_corte, int i,int j) {
		double pto_anterior, pto_posterior, pto_corte;
		pto_corte = (Double) ptos_corte.get(i);
		if(i==0)
			pto_anterior = Math.abs(pto_corte-(Double) ptos_trap[j].get(0));
		else
			pto_anterior = Math.abs(pto_corte-(Double) ptos_corte.get(i-1));

		pto_posterior =Math.abs((Double)ptos_corte.get(i+1)-pto_corte);
		if(pto_anterior<= pto_posterior){
			return Math.abs(pto_anterior)/2;
		}else{
			return Math.abs(pto_posterior)/2;
		}
			
	}
	public Double GetCobertura(int posicion_regla,Double valor,int indAtr,int sizeAtr) {
		Double inicio=0.0;
		Double alto1=0.0;
		Double alto2=0.0;
		Double fin=0.0;
		Double altura=0.0;
		alto1 = (Double)ptos_trap[indAtr].get(2*posicion_regla);
		alto2 = (Double)ptos_trap[indAtr].get((2*posicion_regla)+1);
		if(posicion_regla!=0){
			inicio = (Double)ptos_trap[indAtr].get(2*posicion_regla-1);
			if(posicion_regla==sizeAtr-1){
				fin = (Double)ptos_trap[indAtr].get(2*posicion_regla+1);
				if(valor >= alto1 && valor <= fin){
					altura = 1.0;
				}
				else if(valor<alto1&&valor>inicio){
		            altura = (valor - inicio.doubleValue()) / (alto1.doubleValue() - inicio.doubleValue());
				}
				return altura;
			}else{///////////////////////////ÚLTIMO/////////////////////
				fin = (Double)ptos_trap[indAtr].get(2*posicion_regla+2);
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
		else{
			inicio = (Double)ptos_trap[indAtr].get(posicion_regla);
			fin = (Double)ptos_trap[indAtr].get(2*posicion_regla+2);
			if(valor<=alto2&&valor>=inicio){
				altura = 1.0;
			}
			else if(valor>alto2&&valor<fin){
	            altura = (fin.doubleValue() - valor) / (fin.doubleValue() - alto2.doubleValue());
			}
			return altura;
		}
		
	}

}
