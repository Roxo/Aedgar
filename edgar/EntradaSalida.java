package edgar;

import java.io.FileOutputStream;
import java.util.ArrayList;


import Aleatorios.Aleatorio;
import Dataset.Attribute;
import Dataset.Attributes;
import Dataset.DatasetException;
import Dataset.HeaderFormatException;
import Dataset.Instance;
import Dataset.InstanceSet;

public class EntradaSalida {

	private String Cabecera="";

/**
 * Este método lee de un fichero el conjunto de ejemplos y lo introduze en una estructura poblacion
 * @param NombreFichero Nombre del fichero que contiene el conjunto de ejemplos.
 * @param esTrain
 * @return
 */
	public Dataset Cargar_conjuntoEntrenamiento(String NombreFichero, boolean esTrain){	
		Dataset Nuevos_Datos_Entrenamiento=new Dataset();
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
			//ParametrosGlobales.longitud_plantilla=indiceAtributo;					
			
			
			Plantilla plantilla = new Plantilla();
			plantilla.set_NombresAtributos(_NombresAtributos);
			plantilla.set_plantillaAtributos(_plantillaAtributos);
			plantilla.set_ValoresAtributos(_ValoresAtributos);
			
			// Ahora leo los datos de los atributos de salida, en el caso del clasificador tan solo es la clase a la que pertenece
			auxAtributo = Attributes.getOutputAttribute(0);
		    
			plantilla.set_Nombre_Clase(auxAtributo.getName());
			int NumeroClases=auxAtributo.getNumNominalValues();
			plantilla.set_numero_Clases(NumeroClases);
			
			ArrayList _Valores_Clase=new ArrayList();
			int porcentajeClases[] = new int[NumeroClases];
			
			for (int aux=0;aux<NumeroClases;aux++){
				_Valores_Clase.add(aux,auxAtributo.getNominalValue(aux));
			}
			plantilla.set_Valores_Clase(_Valores_Clase);
			Nuevos_Datos_Entrenamiento.setPlantilla(plantilla);
						
			for (int x = 0; x < numInstancias; x++) {	
				int[] AtributosEntrada = Instancias[x].getInputNominalValuesInt();
				int[] AtributosSalida = Instancias[x].getOutputNominalValuesInt();
				boolean AtributosEntradaVacios[] = Instancias[x].getInputMissingValues();
				Ejemplo ej = new Ejemplo(plantilla);
				ej.set_id(x);
				for (int i = 0; i < AtributosEntrada.length; i++) {
					if (AtributosEntradaVacios[i] != true)
						ej.setValorAtributo(i,AtributosEntrada[i],'1');
				}				
				for (int i = 0; i < AtributosSalida.length; i++) {
					ej.setClase(AtributosSalida[i]);
					porcentajeClases[AtributosSalida[i]]++;
				}
				Nuevos_Datos_Entrenamiento.Insertar_Ejemplo(ej);
			}
			plantilla.setContadorClases(porcentajeClases);
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
	
	public String get_Cabecera(){
		return Cabecera;
	}
	
	
	/**
	 * El siguiente método genera un fichero de datos seleccionando aleatoriamente
	 * un conjunto de ejemplos del fichero de entrada.
	 * @param NombreFichero_In Nombre del fichero con el conjunto de ejemplos.
	 * @param NombreFichero_Out Nombre del fichero que se va ha crear con la selección de un subconjunto de ejemplos
	 * @param Numero_Ejemplos_Selecionar Número de ejemplos a seleccionar.
	 */
	
	public void generar_Fichero_subConjunto(String NombreFichero_In, String NombreFichero_Out, int Numero_Ejemplos_Selecionar){
		
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
		     Aleatorio gen_aleatorio=Parametros.getInstancia_Parametros().get_GeneradorAleatorio();
		     
		     while((cont_Sel<Numero_Ejemplos_Selecionar)&&(Instancias_No_Seleccionadas.size()>0)){
		    	 indice_instancia=gen_aleatorio.Randint(0,Instancias_No_Seleccionadas.size()-1);
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
	 * Este método genera un fichero de salida, con dos columnas, una que indica la clase de salida y la otra la predicción realizada por el clasificador.
	 * @param NombreFichero_Test
	 * @param NombreFichero_Resultado
	 * @param Resultado_Clasificacion
	 */
	
	public void generar_Fichero_Resultado(String NombreFichero_Resultado, int[][] Resultado_Clasificacion){		
		try {
     		 FileOutputStream f = null;
		     f = new FileOutputStream(NombreFichero_Resultado);
		     f.write(Cabecera.getBytes());
		     
		     ArrayList _Valores_Clase=Parametros.getInstancia_Parametros().getPlantilla().get_Valores_Clase();		     
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
	
	
	
	public void generar_Fichero_Reglas(String NombreFichero_Reglas, Solucion conjunto_reglas){	
		try {
     		 FileOutputStream f = null;
		     f = new FileOutputStream(NombreFichero_Reglas);
		     String cabecera="Conjunto de reglas\n";
		     cabecera+= "Número de Reglas: "+ conjunto_reglas.getTamaño_solucion()+"\n";
		     f.write(cabecera.getBytes());
		     for(int i=0;i<conjunto_reglas.getTamaño_solucion();i++){
		    	 String regla=i+"- "+conjunto_reglas.get_regla(i).get_texto_Regla()+"\n";
		    	 regla+="  - Fitness: "+conjunto_reglas.get_regla(i).getfitness()+"\n";
		    	 regla+="  - Casos Positivos: "+conjunto_reglas.get_regla(i).get_NumCasos_Positivos()+"\n";
		    	 regla+="  - Negativos: "+conjunto_reglas.get_regla(i).get_NumCasos_Negativos()+"\n\n";
		    	 f.write(regla.getBytes());
		     }    
		     f.close();			
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
		
	}
	
	
		
	
	public void Generar_5x2_Ficheros(String NombreFichero_In){
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
			     
			     Aleatorio gen_aleatorio=Parametros.getInstancia_Parametros().get_GeneradorAleatorio();
			     while((cont_Sel<Numero_Ejemplos_Seleccionar)&&(Instancias_No_Seleccionadas.size()>0)){
			    	 indice_instancia=gen_aleatorio.Randint(0,Instancias_No_Seleccionadas.size()-1);
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
