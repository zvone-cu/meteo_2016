package com.bdb.weather.display.windrose;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//podaci za jedan smjer
public class WindSlice {
	
	//ukupno
	private double percentageOfWind;
	
	//pomoæni bafer
	private List<Integer> speedBinCount = new ArrayList<Integer>();
	
	//pojedinaèni
	public List<Double> speedBinPercentage = new ArrayList<Double>();
	

	public double getPercentageOfWind() {
		return percentageOfWind;
	}
	public void setPercentageOfWind(double p) {
		percentageOfWind=p;
	}
	
	
	public int getNumSpeedBins() {
		return speedBinPercentage.size();
	}

	public double getSpeedBinPercentage(int i) {
		if (speedBinPercentage.size()>i)
			return speedBinPercentage.get(i);
		else {
			speedBinPercentage.add(0.0);
			return getSpeedBinPercentage(i);
		}
	}
	
	public void setSpeedBinPercentage(int i, double val) {
		if (speedBinPercentage.size() >i)
			speedBinPercentage.set(i,val);
		else {
			speedBinPercentage.add(0.0);
			setSpeedBinPercentage(i,val);
		}
			
	}
	
	
	public Integer getSpeedBinCount(int i) {
		if (speedBinCount.size()>i)
			return speedBinCount.get(i);
		else {
			speedBinCount.add(0);
			return getSpeedBinCount(i);
		}
	}
	public void setSpeedBinCount(int i, int val) {
		if (speedBinCount.size() >i)
			speedBinCount.set(i,val);
		else
			speedBinCount.add(i,val);
	}

}
