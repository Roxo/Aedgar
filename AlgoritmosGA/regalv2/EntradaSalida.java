package regalv2;

import java.io.FileOutputStream;
import java.util.ArrayList;

import aleatorios.Aleatorio;

import dataset.Attribute;
import dataset.Attributes;
import dataset.DatasetException;
import dataset.HeaderFormatException;
import dataset.Instance;
import dataset.InstanceSet;


/**
 * EntradaSalida implementa los métodos necesarios para leer los datos de entrenamiento en formato Keel,
 * y generar los ficheros de salida con formato Keel.
 *   
 * @author José Luis Toscano Muñoz
 * @version Regal v2.0
 *
 */
public class EntradaSalida {

	/**
	 * Cabecera del fichero, donde se definen los atributos y sus posibles valores.
	 */
	private String Cabecera="";

/**
 * Lee de un fichero los ejemplos y devuelve un objeto ConjuntoEntrenamiento con los ejemplos.
 * @param NombreFichero es el nombre del fichero que contiene el conjunto de ejemplos.
 * @param esTrain indica si son datos de entrenamiento.
 * @return un objeto ConjuntoEntrenamiento con todos los ejemplos leídos del fichero.
 */
	public ConjuntoEntrenamiento cargarConjuntoEntrenamiento(String NombreFichero, boolean esTrain){	
		ConjuntoEntrenamiento Nuevos_Datos_Entrenamiento=new ConjuntoEntrenamiento();
		InstanceSet IS = new InstanceSet();
		try {
			IS.readSet(NombreFichero, esTrain);	
			Instance[] Instancias = IS.getInstances();
			Cabecera=IS.getHeader();
			int numInstancias = IS.getNumInstances();
			int numAtributos = Attributes.getInputNumAttributes();			
			int _plantillaAtributos[][] = new int[numAtributos][2];
			String _NombresAtributos[]=new String[numAtributos];
			ArrayList _ValoresAtributos[]=new ArrayList[numAtributos];

			//Primero leo los datos de los atributos de entrada
			int indiceAtributo=0;
			Attribute auxAtributo;
			for (int i = 0; i < numAtributos; i++) {
				auxAtributo = Attributes.getInputAttribute(i);
				_NombresAtributos[i]=auxAtributo.getName();	
				int numPosiblesValoresAtributo = auxAtributo.getNumNominalValues();
				_ValoresAtributos[i]=new ArrayList();
				for (int aux=0;aux<numPosiblesValoresAtributo;aux++){
					_ValoresAtributos[i].add(aux,auxAtributo.getNominalValue(aux));
				}	
				_plantillaAtributos[i][0] = numPosiblesValoresAtributo;
				_plantillaAtributos[i][1] =indiceAtributo;
				indiceAtributo=indiceAtributo+numPosiblesValoresAtributo;
			}
			
			ParametrosGlobales.getInstancia_Parametros().setNombresAtributos(_NombresAtributos);
			ParametrosGlobales.getInstancia_Parametros().setPlantillaAtributos(_plantillaAtributos);
			ParametrosGlobales.getInstancia_Parametros().setValoresAtributos(_ValoresAtributos);
			
			// Ahora leo los datos de los atributos de salida, en el caso del clasificador tan solo es la clase a la que pertenece
			auxAtributo = Attributes.getOutputAttribute(0);
		
			ParametrosGlobales.getInstancia_Parametros().setNombreClase(auxAtributo.getName());
			int NumeroClases=auxAtributo.getNumNominalValues();
			ParametrosGlobales.getInstancia_Parametros().setNumeroClases(NumeroClases);
			
			ArrayList _Valores_Clase=new ArrayList();
			
			for (int aux=0;aux<NumeroClases;aux++){
				_Valores_Clase.add(aux,auxAtributo.getNominalValue(aux));
			}
			ParametrosGlobales.getInstancia_Parametros().setValoresClase(_Valores_Clase);
						
			for (int x = 0; x < numInstancias; x++) {	
				int[] AtributosEntrada = Instancias[x].getInputNominalValuesInt();
				int[] AtributosSalida = Instancias[x].getOutputNominalValuesInt();
				boolean AtributosEntradaVacios[] = Instancias[x].getInputMissingValues();
				Ejemplo ej = new Ejemplo();
				for (int i = 0; i < AtributosEntrada.length; i++) {
					if (AtributosEntradaVacios[i] != true)
						ej.setValorAtributo(i,AtributosEntrada[i],'1');
				}				
				for (int i = 0; i < AtributosSalida.length; i++) {
					ej.setClase(AtributosSalida[i]);
				}
				Nuevos_Datos_Entrenamiento.insertarEjemplo(ej);
			}
			return Nuevos_Datos_Entrenamiento;

		} catch (DatasetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (HeaderFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	/**
	 * Devuelve la cabecera del fichero.
	 * @return un string con la cabecera del fichero.
	 */
	public String getCabecera(){
		return Cabecera;
	}
	
	
	/**
	 * Genera un fichero de datos seleccionando aleatoriamente
	 * un conjunto de ejemplos del fichero de entrada.
	 * @param NombreFichero_In es el nombre del fichero con el conjunto de ejemplos.
	 * @param NombreFichero_Out es el nombre del fichero que se va ha crear con la selección de un subconjunto de ejemplos.
	 * @param Numero_Ejemplos_Selecionar es el número de ejemplos a seleccionar aleatoriamente.
	 */
	public void generarFicheroSubConjunto(String NombreFichero_In, String NombreFichero_Out, int Numero_Ejemplos_Selecionar){
		
		InstanceSet IS = new InstanceSet();
		try {
			IS.readSet(NombreFichero_In, true);	
			Instance[] Instancias = IS.getInstances();
			Cabecera=IS.getHeader();
			
			ArrayList Instancias_No_Seleccionadas=new ArrayList();
			for(int i=0;i<Instancias.length;i++)Instancias_No_Seleccionadas.add(new Integer(i));
	
			String cabecera=IS.getHeader();
     		 FileOutputStream f = null;
		  
		     f = new FileOutputStream(NombreFichero_Out);
		     f.write(cabecera.getBytes());
		     f.write(new String("@data\n").getBytes());
		     
		     int cont_Sel=0;
		     String cad_instancia="";
		     int indice_instancia=0;
		     Aleatorio gen_aleatorio=ParametrosGlobales.getInstancia_Parametros().getGeneradorAleatorio();
		     
		     while((cont_Sel<Numero_Ejemplos_Selecionar)&&(Instancias_No_Seleccionadas.size()>0)){
		    	 indice_instancia=gen_aleatorio.randInt(0,Instancias_No_Seleccionadas.size()-1);
		    	 cad_instancia=Instancias[indice_instancia].toString()+"\n";
		    	 Instancias_No_Seleccionadas.remove(indice_instancia);
		    	 f.write(cad_instancia.getBytes());
		    	 cont_Sel++;
		     }    
		     f.close();			
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
		
	}
	
	
	/**
	 * Genera un fichero de salida con el formato Keel, con dos columnas, una que indica la clase de salida y la otra la predicción realizada por el clasificador.
	 * @param NombreFichero_Resultado es el nombre del fichero con los datos de prueba.
	 * @param Resultado_Clasificacion es una tabla con los resultados de la clasificación.
	 */
	
	public void generarFicheroResultado(String NombreFichero_Resultado, int[][] Resultado_Clasificacion){		
		try {
     		 FileOutputStream f = null;
		     f = new FileOutputStream(NombreFichero_Resultado);
		     f.write(Cabecera.getBytes());
		     
		     ArrayList _Valores_Clase=ParametrosGlobales.getInstancia_Parametros().getValoresClase();		     
		     for(int i=0;i<Resultado_Clasificacion.length;i++){
		    	 String Salida_Prediccion=_Valores_Clase.get(Resultado_Clasificacion[i][0])+"";
		    	 Salida_Prediccion+= " "+_Valores_Clase.get(Resultado_Clasificacion[i][1])+"\n";	 
		    	 f.write(Salida_Prediccion.getBytes());
		     }
		     f.close();			
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
		
	}
	
	
	/**
	 * Genera un fichero con las reglas que forman el clasificador.
	 * @param NombreFichero_Reglas es el nombre del fichero donde se van  escribir las reglas.
	 * @param conjunto_reglas es un objeto Solucion con el conjunto de reglas.
	 */
	public void generarFicheroReglas(String NombreFichero_Reglas, Solucion conjunto_reglas){	
		try {
     		 FileOutputStream f = null;
		     f = new FileOutputStream(NombreFichero_Reglas);
		     String cabecera="Conjunto de reglas\n";
		     cabecera+= "Número de Reglas: "+ conjunto_reglas.getTamaño()+"\n";
		     f.write(cabecera.getBytes());
		     for(int i=0;i<conjunto_reglas.getTamaño();i++){
		    	 String regla=i+"- "+conjunto_reglas.getRegla(i).getTextoRegla()+"\n";
		    	 regla+="  - Fitness: "+conjunto_reglas.getRegla(i).getFitness()+"\n";
		    	 regla+="  - Casos Positivos: "+conjunto_reglas.getRegla(i).getNumeroCasosPositivos()+"\n";
		    	 regla+="  - Negativos: "+conjunto_reglas.getRegla(i).getNumeroCasosNegativos()+"\n\n";
		    	 f.write(regla.getBytes());
		     }    
		     f.close();			
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
		
	}
	
	
	
	/**
	 * Particiona un fichero con formato Keel en 5 ficheros de entrenamiento y 5 ficheros de test, 
	 * para realizar una validación cruzada 5x2.  
	 * @param NombreFichero_In es el nombre del fichero de entrada.
	 */
	
	public void generar5x2Ficheros(String NombreFichero_In){
		InstanceSet IS = new InstanceSet();
		try {
			IS.readSet(NombreFichero_In, true);
			Instance[] Instancias = IS.getInstances();
			
			int ind_punto=NombreFichero_In.indexOf('.');
			if(ind_punto!=-1) NombreFichero_In=NombreFichero_In.substring(0,ind_punto);	
			String cabecera=IS.getHeader();
			
			for(int i=0;i<5;i++){
				for(int tr_tst=0;tr_tst<2;tr_tst++){
	     		 FileOutputStream f = null;
	     		 String Nombre_Fichero_Salida=NombreFichero_In+"-5x2-"+(i+1);
	     		 if (tr_tst==0) Nombre_Fichero_Salida+="tra.dat";
	     		 else Nombre_Fichero_Salida+="tst.dat";
	     		 f = new FileOutputStream(Nombre_Fichero_Salida);
			     f.write(cabecera.getBytes());
			     f.write(new String("@data\n").getBytes());
			     
			     ArrayList Instancias_No_Seleccionadas=new ArrayList();
					for(int aux=0;aux<Instancias.length;aux++)Instancias_No_Seleccionadas.add(new Integer(aux));
			     		     
			     int cont_Sel=0;
			     String cad_instancia="";
			     int indice_instancia=0;
			     int Numero_Ejemplos_Seleccionar=IS.getNumInstances()/5;
			     
			     Aleatorio gen_aleatorio=ParametrosGlobales.getInstancia_Parametros().getGeneradorAleatorio();
			     while((cont_Sel<Numero_Ejemplos_Seleccionar)&&(Instancias_No_Seleccionadas.size()>0)){
			    	 indice_instancia=gen_aleatorio.randInt(0,Instancias_No_Seleccionadas.size()-1);
			    	 cad_instancia=Instancias[indice_instancia].toString()+"\n";
			    	 Instancias_No_Seleccionadas.remove(indice_instancia);
			    	 f.write(cad_instancia.getBytes());
			    	 cont_Sel++;
			     }       
			     f.close();	
				}
			}
		
		}catch (Exception e){
			System.out.println(e.getMessage());
		}	
	}
	
}
