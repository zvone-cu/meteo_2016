package com.bdb.weather.display.windrose;

import beans.MeteoConfigBean;

//drži opise brzine
public class SpeedBin {
	
	private double from;
	private double to;
	
	
	public SpeedBin(double f, double t )  {
		from=f; 
		to=t;
		
	}

	public String getSpeedRange() {
		return upristoji(from)+"-"+upristoji(to)+(MeteoConfigBean.dMsToKmhFactor==1?" m/s" :" km/h" );
	}

	private String upristoji(double x) {

		double tr1= Math.floor(x*10+0.5)/10;
		String tr2= String.valueOf(tr1).replaceAll("(\\.0[0-9]*)", "");
		
		return tr2;
		
		
		
		
	}

	public double getFrom() {
		return from;
	}

	public void setFrom(double from) {
		this.from = from;
	}

	public double getTo() {
		return to;
	}

	public void setTo(double to) {
		this.to = to;
	}

}
