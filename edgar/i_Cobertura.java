package edgar;

import java.util.ArrayList;

/**
 * @author Daniel Albendín
 * 
 * Interfaz con la que podemos implementar varios tipos de coberturas de las reglas.
 *
 */
public interface i_Cobertura {

	/**
	 * @param posicion_regla	Intervalo del atributo de una regla que está activo.
	 * @param valor				Valor del ejemplo el cual hay que ver si la regla lo cubre
	 * @param indAtr			Indice del atributo de la regla que estamos analizando
	 * @param sizeAtr			Tamaño del atributo[indAtr]
	 * @return Devuelve la altura [0,1] del cumplimiento de un atributo en una regla (0) si la parte del atributo que estamos analizando no cubre el valor
	 */
	Double GetCobertura(int posicion_regla, Double valor, int indAtr, int sizeAtr);
}