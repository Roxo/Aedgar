/*
 * ChiMergeDiscretizer.java
 *
 */

/**
 *
 */

package edgar;

import Dataset.*;

import java.util.*;
//import keel.barcelona.Globals.*;

public abstract class Discretizer {
	double [][]cutPoints;
	double [][]realValues;
	boolean []realAttributes;
	int []classOfInstances;
	
	private boolean mejorado;
	private int claseMinoritaria;
	private double ponderacion;
	
	public void buildCutPoints(int numAttributes, Instance []instances) {

		int numExamples = instances.length;
		
		classOfInstances= new int[numExamples];
		for(int i=0;i<numExamples;i++) 
			classOfInstances[i]=instances[i].getOutputNominalValuesInt(0);
		
		cutPoints=new double[numAttributes][];
		realAttributes = new boolean[numAttributes];
		realValues = new double[numAttributes][];
		for(int i=0;i<numAttributes;i++) {
			Attribute at=Attributes.getAttribute(i);
			if(at.getType()==Attribute.REAL || at.getType()==Attribute.INTEGER) {
				realAttributes[i]=true;

				realValues[i] = new double[numExamples];
				int []points= new int[numExamples];
				int numPoints=0;
				for(int j=0;j<instances.length;j++) {
					if(!instances[j].getInputMissingValues(i)) {
						points[numPoints++]=j;
						realValues[i][j]=instances[j].getInputRealValues(i);
					}
				}

				sortValues(i,points,0,numPoints-1);

				Vector cp=discretizeAttribute(i,points,0,numPoints-1); 
				if(cp.size()>0) {
					cutPoints[i]=new double[cp.size()];
					for(int j=0;j<cutPoints[i].length;j++) {
						cutPoints[i][j]=((Double)cp.elementAt(j)).doubleValue();
						//System.out.println("Cut point "+j+" of attribute "+i+" : "+cutPoints[i][j]);
					}
				} else {
					cutPoints[i]=null;
				}
				//System.out.println("Number of cut points of attribute "+i+" : "+cp.size());
			} else {
				realAttributes[i]=false;
			}
		}
	}


	protected void sortValues(int attribute,int []values,int begin,int end) {
		double pivot;
		int temp;
		int i,j;

		i=begin;j=end;
		pivot=realValues[attribute][values[(i+j)/2]];
		do {
			while(realValues[attribute][values[i]]<pivot) i++;
			while(realValues[attribute][values[j]]>pivot) j--;
			if(i<=j) {
				if(i<j) {
					temp=values[i];
					values[i]=values[j];
					values[j]=temp;
				}
				i++; j--;
			}
		} while(i<=j);
		if(begin<j) sortValues(attribute,values,begin,j);
		if(i<end) sortValues(attribute,values,i,end);
	}

	public int getNumIntervals(int attribute) {
		if(cutPoints[attribute] != null)
			return cutPoints[attribute].length+1;
		else
			return 0;
	}

	public double getCutPoint(int attribute,int cp) {
		return cutPoints[attribute][cp];
	}

	protected abstract Vector discretizeAttribute(int attribute,int []values,int begin,int end) ;

	public int discretize(int attribute,double value) {
		if(cutPoints[attribute]==null) return 0;
		for(int i=0;i<cutPoints[attribute].length;i++)
			if(value<cutPoints[attribute][i]) return i;
		return cutPoints[attribute].length;
	}


	public boolean isMejorado() {
		return mejorado;
	}


	public void setMejorado(boolean mejorado) {
		this.mejorado = mejorado;
	}


	public int getClaseMinoritaria() {
		return claseMinoritaria;
	}


	public void setClaseMinoritaria(int claseMinoritaria) {
		this.claseMinoritaria = claseMinoritaria;
	}


	public double getPonderacion() {
		return ponderacion;
	}


	public void setPonderacion(double ponderacion) {
		this.ponderacion = ponderacion;
	}
}
