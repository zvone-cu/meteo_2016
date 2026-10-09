package com.bdb.weather.display.windrose;

public class Heading {
	
	private String  text; 
	
	private double headingValue;

//	public static Heading headingForSlice(int i, int numSlices) {
//		Heading h = new Heading (i, numSlices);
//		return h;
//	}
	
	public Heading (int i, int numSlices) {
		
		text= "slice-"+i+"-"+numSlices+"-";
		headingValue=i*45;
	}
	
	public String toString(){
		return text;
	}
	
	public double getHeadingValue(){
		return headingValue;
	}

}
