package com.bdb.weather.display.windrose;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import beans.MeteoConfigBean;
import dao.Podaci;

public class WindRoseData {
	
	static Logger logger = null;

	private int numSlices;
	
	private List<WindSlice> windSlices = new ArrayList<WindSlice>();
	
	//broj brzina na nivou cijelog grafa
	private List<SpeedBin> speedBins = new ArrayList<SpeedBin>();
	
	
	public WindRoseData(List<Podaci> data, int num)  {
		
		logger = Logger.getLogger(WindRoseData.class);
		
		numSlices=num;
		
		fillData(data);
			
	}
	

	private void fillData(List<Podaci> data) {
		
		Map<Integer, ArrayList<Double>> winds = new HashMap<>();
		
		for (int i=0; i<numSlices; i++) 
			winds.put(i,  new ArrayList<Double>( ));
		

		//potrpaj sve vjetrove po smjerovima:
		double maxSpeed=0;
		int ukBrUzoraka=0;
		
		if (!data.isEmpty()) {
			for (Podaci p : data) {
				if (p.getVjetarBrzina()!=null && !p.getVjetarBrzina().equals(BigDecimal.ZERO) && p.getVjetarSmjerDeg()!=null ) {
					
					ArrayList x = winds.get(p.getVjetarSmjerDeg()/45);
					x.add(p.getVjetarBrzina().doubleValue()*MeteoConfigBean.dMsToKmhFactor);
					//winds.replace(p.getVjetarSmjerDeg()/45, x);
					
					if (p.getVjetarBrzina().doubleValue()>maxSpeed) 
						maxSpeed=p.getVjetarBrzina().doubleValue()*MeteoConfigBean.dMsToKmhFactor;
					
					ukBrUzoraka=ukBrUzoraka+1;
				}
			}
		}
		
		
       
		int speedRange= (int)(Math.floor(maxSpeed+0.5)); 
		if (speedRange % 2 ==1) speedRange=speedRange+1;

		createSpeedBins(speedRange,20);
		
		
		//obrada po smjerovima:
		for (int i=0; i<numSlices; i++) 
		{
			WindSlice w = new WindSlice(); 
			windSlices.add(w);
			
			w.setPercentageOfWind(100.*winds.get(i).size()/(double)ukBrUzoraka);
			logger.info("slice:"+i+"->"+w.getPercentageOfWind()+"%" );
			
			for (Double val : winds.get(i) ) {
				
				for (int j=0; j<speedBins.size(); j++ ) {
					
					if (val>=speedBins.get(j).getFrom() && val<speedBins.get(j).getTo()) {
						
						w.setSpeedBinCount(j, w.getSpeedBinCount(j)+1);
						
						w.setSpeedBinPercentage(j, 100.*w.getSpeedBinCount(j)/(double)winds.get(i).size());
					}
				}
			}
			
		}
		
			
		logger.info("ukBrUzoraka="+ukBrUzoraka);
		logger.info("maxSpeed="+maxSpeed);
		logger.info("speedRange="+speedRange);
	}


	private void createSpeedBins(int maxSpeed, int proposedMax) {
		
		if (maxSpeed<=proposedMax)
			for (double x=0; x<proposedMax; x=x+proposedMax/10) {  //10 je max broj stepenica brzine vjetra
				
				SpeedBin s= new SpeedBin(x, x+proposedMax/10);
				speedBins.add(s);
			}
			
		else
			createSpeedBins(maxSpeed, proposedMax+10);
		
	}


	public List<SpeedBin> getSpeedBins() {
		return speedBins;
	}

	public int getNumSlices() {
		return numSlices;
	}
	public void setNumSlices(int numSlices) {
		this.numSlices = numSlices;
	}

	
	public WindSlice getSlice(int i) {
		return windSlices.get(i);
	}

	public void setSlice(WindSlice slice) {
		windSlices.add(slice);
	}

}
