package dataset;

import java.util.*;
import java.io.*;


/**
 * <p>
 * <b> InstanceSet </b>
 * </p>
 *
 * The instance set class mantains a pool of instances read from the keel
 * formated data file. It provides a set of methods that permit to get
 * each instance, get the whole set of instances, get the number of instances,
 * etc.
 *
 * @author Albert Orriols Puig
 * @version keel0.1
 * @see Instance
 * @see Attributes
 */


public class InstanceSet {
	
/////////////////////////////////////////////////////////////////////////////
//////////////// ATTRIBUTES OF THE INSTANCESET CLASS ////////////////////////
/////////////////////////////////////////////////////////////////////////////


/**
 * Attribute where all the instances of the DB are stored.
 */
  private Instance[] instanceSet;
  
/**
 * String where the header of the file is stored.
 */
  private String header;

/**
 * String where only the attributes definition header is stored
 */
  private String attHeader;

/**
 * Object that collects all the errors happened while reading the test and
 * train datasets.
 */
  static FormatErrorKeeper errorLogger = new FormatErrorKeeper();
  
  
/////////////////////////////////////////////////////////////////////////////
///////////////// METHODS OF THE INSTANCESET CLASS //////////////////////////
/////////////////////////////////////////////////////////////////////////////
  
/**
 * It instances a new instance of InstanceSet
 */
  public InstanceSet(){
  }//end InstanceSet
  
  
/** 
 * This method reads all the information in a DB and load it to memory.
 * @param fileName is the database file name. 
 * @param isTrain is a flag that indicate if the database is for a train or for a test.
 * @throws DatasetException if there is any semantical error in the input file.
 * @throws HeaderFormatException if there is any lexical or sintactical error in the 
 * header of the input file
 */
  public void readSet(String fileName,boolean isTrain) throws DatasetException, HeaderFormatException{
    String line;
    header ="";
    attHeader = null;
    
    System.out.println ("Opening the file: "+fileName+".");
    //Parsing the header of the DB.
    errorLogger = new FormatErrorKeeper();
    DataParser dParser = new DataParser();
    dParser.headerParse(fileName, isTrain);
    
    //The attributes statistics are init if we are in train mode.
    if (isTrain && Attributes.getOutputNumAttributes() == 1) Attributes.initStatistics();
    
    //After that, the rest of the file is parsed.
    InstanceParser parser=new InstanceParser(fileName,isTrain);
    while (!(line = parser.getLine()).equalsIgnoreCase("@data")){ 
        if (line.toLowerCase().indexOf("@relation")!=-1 && isTrain){
            line=line.toLowerCase();
            Attributes.setRelationName(line.replaceAll("@relation",""));
        }
        if (line.toLowerCase().indexOf("@inputs") != -1) attHeader = header;
        if (line.toLowerCase().indexOf("@outputs")!= -1 && attHeader == null) attHeader = header;
        header += line + "\n";
    }
    if (attHeader == null) attHeader = header;
        
    //A temporal vector is used to store the instances read.
    Vector tempSet=new Vector(1000,100000);
    while((line=parser.getLine())!=null) {
        tempSet.addElement(new Instance(line,isTrain, tempSet.size()));
    }
   
    //The vector of instances is converted to an array of instances.
    int sizeInstance=tempSet.size();
	System.out.println ("Number of instances read: "+tempSet.size());
    instanceSet=new Instance[sizeInstance];
    for (int i=0; i<sizeInstance; i++) {
        instanceSet[i]=(Instance)tempSet.elementAt(i);
    }
	//System.out.println("After converting all instances");
  
	//System.out.println("The error logger has any error: "+errorLogger.getNumErrors()); 
    if (errorLogger.getNumErrors() > 0){
        
		System.out.println ("There has been "+errorLogger.getAllErrors().size()+
                                    " errors in the dataset format.");
		for (int k=0;k<errorLogger.getNumErrors();k++){
			errorLogger.getError(k).print();
		}
		throw new DatasetException("There has been "+errorLogger.getAllErrors().size()+
                                    " errors in the dataset format", errorLogger.getAllErrors());
    }
   
	System.out.println ("Finishing the statistics: "+isTrain+", "+Attributes.getOutputNumAttributes());
    //If being on a train dataset, the statistics are finished
    if (isTrain && Attributes.getOutputNumAttributes() == 1){ 
        Attributes.finishStatistics();
    }
    
    System.out.println ("File LOADED CORRECTLY!!");
  }//end of InstanceSet constructor.

  
  
/**
 * It returns the number of instances.
 * @return an int with the number of instances.
 */
  public int getNumInstances() {
    return instanceSet.length;
  }//end numInstances

  
/**
 * Gets the instance located at the cursor position.
 * @return the instance located at the cursor position.
 */
  public Instance getInstance(int whichInstance) {
    if (whichInstance <0 || whichInstance>= instanceSet.length) return null;
    return instanceSet[whichInstance];
  }//end getInstance


/**
 * It returns all the instances of the class.
 * @return Instance[] with all the instances of the class.
 */
  public Instance[] getInstances() {
    return instanceSet;
  }//end getInstances

/**
 * Returns the value of an integer or a real input attribute of an instance
 * in the instanceSet.
 * @param whichInst is the position of the instance.
 * @param whichAttr is the position of the input attribute.
 * @return a String with the numeric value.
 * @throws ArrayIndexOutOfBoundsException If the index is out of the instance
 * set size.
 */
  public double getInputNumericValue(int whichInst, int whichAttr) throws ArrayIndexOutOfBoundsException{
    if (whichInst<0 || whichInst>= instanceSet.length) 
        throw new ArrayIndexOutOfBoundsException("You are trying to access to "+whichInst+" instance and there are only "+instanceSet.length+"."); 
    return instanceSet[whichInst].getInputRealValues(whichAttr);
  }//end getInputNumericValue

  
/**
 * Returns the value of an integer or a real output attribute of an instance
 * in the instanceSet.
 * @param whichInst is the position of the instance.
 * @param whichAttr is the position of the output attribute.
 * @return a String with the numeric value.
 * @throws ArrayIndexOutOfBoundsException If the index is out of the instance
 * set size.
 */
  public double getOutputNumericValue(int whichInst, int whichAttr) throws ArrayIndexOutOfBoundsException{
    if (whichInst<0 || whichInst>= instanceSet.length) 
        throw new ArrayIndexOutOfBoundsException("You are trying to access to "+whichInst+" instance and there are only "+instanceSet.length+"."); 
    return instanceSet[whichInst].getOutputRealValues(whichAttr);
  }//end getOutputNumericValue

  
/**
 * Returns the value of a nominal input attribute of an instance in the 
 * instanceSet.
 * @param whichInst is the position of the instance.
 * @param whichAttr is the position of the input attribute.
 * @return a String with the nominal value.
 * @throws ArrayIndexOutOfBoundsException If the index is out of the instance
 * set size.
 */
  public String getInputNominalValue(int whichInst, int whichAttr) throws ArrayIndexOutOfBoundsException{
    if (whichInst<0 || whichInst>= instanceSet.length) 
        throw new ArrayIndexOutOfBoundsException("You are trying to access to "+whichInst+" instance and there are only "+instanceSet.length+"."); 
    return instanceSet[whichInst].getInputNominalValues(whichAttr);
  }//end getInputNominalValue
  
  
  
/**
 * Returns the value of a nominal output attribute of an instance in the 
 * instanceSet.
 * @param whichInst is the position of the instance.
 * @param whichAttr is the position of the output attribute.
 * @return a String with the nominal value.
 * @throws ArrayIndexOutOfBoundsException If the index is out of the instance
 * set size.
 */
  public String getOutputNominalValue(int whichInst, int whichAttr) throws ArrayIndexOutOfBoundsException{
    if (whichInst<0 || whichInst>= instanceSet.length) 
        throw new ArrayIndexOutOfBoundsException("You are trying to access to "+whichInst+" instance and there are only "+instanceSet.length+"."); 
    return instanceSet[whichInst].getOutputNominalValues(whichAttr);
  }//end getOutputNumericValue
  
  
  
/**
 * It does remove the instance i from the instanceSet.
 * @param instNum is the instance removed from the instanceSet.
 */
  public void removeInstance(int instNum){
    if (instNum<0 || instNum>=instanceSet.length) return;
    Instance[] aux = new Instance[instanceSet.length - 1];
    int add = 0;
    for (int i=0; i<instanceSet.length; i++){
        if (instNum == i) add=1;
        else{
            aux[i-add] = instanceSet[i];
        }
    }
    //Copying the auxiliar to the instanceSet variable
    instanceSet = aux;
    aux = null; //avoiding memory leaks (not necessary in this case)
  }//end removeInstance
 

/**
 * It does remove an attribute. To remove an attribute, the train an the
 * test sets have to be passed to mantain the coherence of the system. 
 * Otherwise, only the attribute of the train set would be removed, leaving
 * inconsistent the instances of the test set, because of having one extra
 * attribute inexistent anymore.
 *
 * @param tSet is the test set. 
 * @param inputAtt is a boolean that is true when the attribute that is 
 * wanted to be removed is an input attribute.
 * @param whichAtt is a integer that indicate the position of the attriubte
 * to be deleted.
 * @return a boolean indicating if the attribute has been deleted
 */
  public boolean removeAttribute(InstanceSet tSet, boolean inputAtt, int whichAtt){
    Attribute attToDel=null;
    //Getting a reference to the attribute to del
    if (inputAtt)
        attToDel = (Attribute)Attributes.getInputAttribute(whichAtt);
    else
        attToDel = (Attribute)Attributes.getOutputAttribute(whichAtt);
    
    if (!Attributes.removeAttribute(inputAtt,whichAtt)) return false;
    
    for (int i=0; i<instanceSet.length; i++){
        instanceSet[i].removeAttribute(attToDel, inputAtt, whichAtt);
    }
    
    
    if (tSet != null) for (int i=0; i<tSet.instanceSet.length; i++){
        tSet.instanceSet[i].removeAttribute(attToDel, inputAtt, whichAtt);
    }
    return true;
  }//end removeAttribute
  
  
  
/**
 * It returns the header.
 * @return a String with the header of the file.
 */
  public String getHeader() {
    return header;
  }//end getHeader

  
  
/**
 * It does return a new header (not necessary the same header as the 
 * input file one). It only includes the valid attributes, those ones
 * defined in @inputs and @outputs (or taken as that role following the
 * keel format specification).
 * @return a String with the new header
 */
  public String getNewHeader(){
                                                                                                                                 
    //Getting the relation name
    String line = "@relation "+Attributes.getRelationName()+"\n";
                                                                                                                             
    //Getting all the attributes
    Attribute []attrs = Attributes.getInputAttributes();
    for (int i=0; i<attrs.length; i++){
        line += attrs[i].toString()+"\n";
    }
                                                                                                                             
    //Gettin all the outputs attributes
    attrs = Attributes.getOutputAttributes();
    line += attrs[0].toString()+"\n";
                                                                                                                             
    //Getting @inputs and @outputs
    line += Attributes.getInputHeader()+"\n";
    line += Attributes.getOutputHeader()+"\n";
                                                                                                                             
    return line;
  }//end getNewHeader
  
  
/**
 * It does return the original header definiton but
 * without @input and @output in there
 */
  private String getOriginalHeaderWithoutInOut(){
      //Getting the relation name
    String line = "@relation "+Attributes.getRelationName()+"\n";
                                                                                                                             
    //Getting all the attributes
    Attribute []attrs = Attributes.getAttributes();
    for (int i=0; i<attrs.length; i++){
        line += attrs[i].toString()+"\n";
    }
    return line;
  }//end getOriginalHeaderWithoutInOut
  
  
  
/**
 * It prints the dataset to the specified PrintWriter
 * @param out is the PrintWriter where to print
 */
  public void print (PrintWriter out){
      for (int i=0; i<instanceSet.length; i++){
          out.println ("> Instance "+i+":");
          instanceSet[i].print(out);
      }
  }//end print
  
  
/**
 * It prints the dataset to the specified PrintWriter.
 * The order of the attributes is the same as in the 
 * original file
 * @param out is the PrintWriter where to print
 * @param printInOut indicates if the @inputs (1), @outputs(2), 
 * both of them (3) or any (0) has to be printed
 */
  public void printAsOriginal (PrintWriter out, int printInOut){    
      /*Printing the header as the original one*/
      out.println(attHeader);
      if(printInOut==1 || printInOut==3)
        out.println(Attributes.getInputHeader());
      if(printInOut==2 || printInOut==3)
        out.println(Attributes.getOutputHeader());
      
      out.print("@data");
      for (int i=0; i<instanceSet.length;i++){
          out.println();
          instanceSet[i].printAsOriginal(out);
      }
  }//end printAsOriginal
  
  
  public void print (){
      System.out.println ("------------- ATTRIBUTES --------------");
      Attributes.print();
      System.out.println ("-------------- INSTANCES --------------");
      for (int i=0; i<instanceSet.length; i++){
          System.out.print ("\n> Instance "+i+":");
          instanceSet[i].print();
      }
  }//end print
  
}//end of InstanceSet Class.
