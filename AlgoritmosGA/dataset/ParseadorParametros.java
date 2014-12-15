/*
 * ParserParameters.java
 *
 */

/**
 * This class read the configuration file using the KEEL format
 */
package dataset;

import java.util.*;
import java.io.*;


public class ParseadorParametros {
	static BufferedReader br;
	static String algorithmName="regal";
	static regalv2.ParametrosGlobales param=regalv2.ParametrosGlobales.getInstancia_Parametros();
	
	/** Creates a new instance of ParserParameters */
	public static void doParse(String fileName) {
		try {
			br=new BufferedReader(new FileReader(fileName));
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		parseParameters();	
	}
	
	/**
	 *  Parses the header of the ARFF file
	 */
	static void parseParameters() {
		String str=getLine();
		while(str!= null) {
			StringTokenizer st = new StringTokenizer(str,"=");
			String name = st.nextToken();
			name=name.trim();
			name.replaceAll(" ","");

			if(name.equalsIgnoreCase("algorithm")) 
				processAlgorithmName(st);
			else if(name.equalsIgnoreCase("inputData"))
				processInputs(st);
			else if(name.equalsIgnoreCase("outputData"))
				processOutputs(st);
			else processParameters(st,name);

			str=getLine();
		}
	}

	static void processAlgorithmName(StringTokenizer st) {
		if(!st.hasMoreTokens()) {
			System.err.println("Parse error processing algorithm name");
			System.exit(1);
		}
		String name=st.nextToken();
		name=name.trim();
		if(!algorithmName.equalsIgnoreCase(name)) {
			System.err.println("This config file is not for us");
			System.exit(1);
		}
	}

	static void processInputs(StringTokenizer st) {
		if(!st.hasMoreTokens()) {
			System.err.println("Parse error processing inputs");
			System.exit(1);
		}
		String files=st.nextToken();
		files=files.trim();
		if(!(files.startsWith("\"") && files.endsWith("\""))) {
			System.err.println("Parse error processing inputs "+files);
			System.exit(1);
		}
		files.replaceAll("^\"",""); files.replaceAll("\"$","");
		StringTokenizer st2 = new StringTokenizer(files,"\"");
		try {
			String file1=st2.nextToken();
			param.listaFicherosEntrada.add(file1);
			String sep=st2.nextToken();
			String file2=st2.nextToken();
			param.listaFicherosEntrada.add(file2);
			sep=st2.nextToken();
			String file3=st2.nextToken();
			System.err.println(file1);
			System.err.println(file2);
			//insertStringParameter("trainInputFile",file1);
			//insertStringParameter("train2InputFile",file2);
			//insertStringParameter("testInputFile",file3);
		} catch(NoSuchElementException e) {
			System.err.println("Parse error processing inputs "+files);
			System.exit(1);
		}
	}

	static void processOutputs(StringTokenizer st) {
		if(!st.hasMoreTokens()) {
			System.err.println("Parse error processing outputs");
			System.exit(1);
		}
		String files=st.nextToken();
		files=files.trim();
		if(!(files.startsWith("\"") && files.endsWith("\""))) {
			System.err.println("Parse error processing outputs "+files);
			System.exit(1);
		}
		files.replaceAll("^\"",""); files.replaceAll("\"$","");
		StringTokenizer st2 = new StringTokenizer(files,"\"");
		try {
			String file1=st2.nextToken();
			param.listaFicherosSalida.add(file1);
			String sep=st2.nextToken();
			String file2=st2.nextToken();
			param.listaFicherosSalida.add(file2);
			sep=st2.nextToken();
			String file3=st2.nextToken();
			System.err.println(file1);
			System.err.println(file2);
			
			//insertStringParameter("trainOutputFile",file1);
			//insertStringParameter("testOutputFile",file2);
			//insertStringParameter("logOutputFile",file3);
		} catch(NoSuchElementException e) {
			System.err.println("Parse error processing outputs "+files);
			System.exit(1);
		}
	}

	static void processParameters(StringTokenizer st,String paramName) {
		
		if(!st.hasMoreTokens()) {
			System.err.println("Parse error processing parameter "+paramName);
			System.exit(1);
		}
		String paramValue=st.nextToken();
		paramValue=paramValue.trim();
		System.err.println("Parametro: " +paramName+ "="+paramValue);
	
		if (paramName.equals("Factor_Adaptacion"))
			param.setRatioAdaptacionForaneoP(Double.parseDouble(paramValue));
		
		else if (paramName.equals("M"))
			param.setM(Integer.parseInt(paramValue));
		
		else if (paramName.equals("seed"))
			param.setSemilla(Long.parseLong(paramValue));
		
		else if (paramName.equals("Pcruce"))
			param.setProbabilidadCruce(Double.parseDouble(paramValue));

		else if (paramName.equals("Pmutacion"))
			param.setProbabilidadMutacion(Double.parseDouble(paramValue));
	
		else if (paramName.equals("ratio_migracion_nu"))
			param.setRatioMigracionNu(Double.parseDouble(paramValue));
		
		else if (paramName.equals("ratio_adaptacion_foraneo"))
			param.setRatioAdaptacionForaneoP(Double.parseDouble(paramValue));
		
		else if (paramName.equals("Numero_Nodos"))
			param.setNumeroNodosRegal(Integer.parseInt(paramValue));
		
		else if (paramName.equals("Numero_Generaciones_Por_Nodo"))
			param.setNumeroGeneracionesPorNodo(Integer.parseInt(paramValue));
		
		else if (paramName.equals("Numero_Maximo_Epocas_Sin_Mejora"))
			param.setNumeroMaximoEpocasSinMejora(Integer.parseInt(paramValue));
		
		else if (paramName.equals("Numero_Epocas_Entre_Enfriamiento"))
			param.setNumeroEpocasEntreEnfriamiento(Integer.parseInt(paramValue));
		
		else if (paramName.equals("Mecanismo_Enfriamiento"))
			param.setMecanismoEnfriamiento(Boolean.parseBoolean(paramValue));
		
		
		else if(isString(paramName))
			System.err.println("Parametro No reconocido "+paramName);
		else{
			System.err.println("Unknown parameter "+paramName);
			System.exit(1);
		}
	}

	static boolean isReal(String paramName) {
		if(paramName.equalsIgnoreCase("Factor_Adaptacion")) return true;
		if(paramName.equalsIgnoreCase("Pcruce")) return true;
		if(paramName.equalsIgnoreCase("Pmutacion")) return true;
		if(paramName.equalsIgnoreCase("ratio_migracion_nu")) return true;
		if(paramName.equalsIgnoreCase("ratio_adaptacion_foraneo")) return true;
		

		return false;
	}

	static boolean isInteger(String paramName) {
		if(paramName.equalsIgnoreCase("M")) return true;
		if(paramName.equalsIgnoreCase("Numero_Nodos")) return true;
		if(paramName.equalsIgnoreCase("Numero_Segundos_Solicitar_Reglas")) return true;
		if(paramName.equalsIgnoreCase("Numero_Maximo_Epocas_Sin_Mejora")) return true;
		if(paramName.equalsIgnoreCase("Numero_Epocas_Entre_Enfriamiento")) return true;
		
		return false;
	}

	static boolean isBoolean(String paramName) {
		if(paramName.equalsIgnoreCase("Mecanismo_Enfriamiento")) return true;
		
		return false;
	}

	static boolean isString(String paramName) {
		if(paramName.equalsIgnoreCase("discretizer1")) return true;
		if(paramName.equalsIgnoreCase("discretizer2")) return true;
		if(paramName.equalsIgnoreCase("discretizer3")) return true;
		if(paramName.equalsIgnoreCase("discretizer4")) return true;
		if(paramName.equalsIgnoreCase("discretizer5")) return true;
		if(paramName.equalsIgnoreCase("discretizer6")) return true;
		if(paramName.equalsIgnoreCase("discretizer7")) return true;
		if(paramName.equalsIgnoreCase("discretizer8")) return true;
		if(paramName.equalsIgnoreCase("discretizer9")) return true;
		if(paramName.equalsIgnoreCase("discretizer10")) return true;
		return false;
	}


	static String getLine() {
		String st=null;
		do {
			try {
				st=br.readLine();
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		} while(st!=null && st.equalsIgnoreCase(""));
		return st;
	}
}
