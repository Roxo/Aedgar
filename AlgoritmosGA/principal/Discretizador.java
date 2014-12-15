package principal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;



public class Discretizador {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
			
			
			
			
			
			
			// File inputFile = new File("original.txt"); 
	         
	         
	        // FileInputStream fis = new FileInputStream(inputFile);
	        
	         
	        try{
	         BufferedReader in
	         = new BufferedReader(new FileReader("ListaExperimentos.txt"));
	         String strLinea="";
	         int idExperimento=1;
	         while (((strLinea = in.readLine()) != null) ){
	        	 strLinea=strLinea.replace("'", "\"");
	        	 File outputFile = new File("ficherosExperimentos/Experimento_"+idExperimento+".txt");
	        	 FileOutputStream fos = new FileOutputStream(outputFile);
	        	 fos.write(strLinea.getBytes());
	        	 fos.close();	        	 
	        	 idExperimento++;
	         }
	         
	         
	         in.close();
	         
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
		
		
 }

		
		
		
		
		
		
		
		
	}


