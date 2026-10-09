package beans;


import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import beans.MeteoConfigBean.StatTip;
import dao.PodaciAnal;



@ManagedBean(name = "statBean", eager=false )
@SessionScoped
//@RequestScoped
// @ApplicationScoped
public class StatisticsBean {

	static Logger logger = null;
	
	private StreamedContent grafH1; // za preko JFreeChart
	
	private boolean ref=true;
	private boolean min=true;
	private boolean max=true;
	private boolean avg=true;
	

	
	@ManagedProperty(value="#{meteoConfigBean}")
	private MeteoConfigBean meteoConfigBean;
	
	
	
	@PostConstruct
	public void initData() {

		// PropertyConfigurator.configure("classes/log4j.properties");
		logger = Logger.getLogger(StatisticsBean.class);

		logger.debug("StatisticsBean.initData()");
		
	

	}
	
	
	
	
	public String drawGrafH1 () throws ParseException, IOException{
		
		
		
		HistogramDataset dataset = new HistogramDataset();
	    dataset.setType(HistogramType.RELATIVE_FREQUENCY);
	   //dataset.setType(HistogramType.FREQUENCY);
	    
    
//		Date d2 = meteoConfigBean.danasnjiDan(false);
//		Date d1 = meteoConfigBean.addMonths(d2,-1);
		
		List<PodaciAnal> data = getData(meteoConfigBean.getdOd(),meteoConfigBean.getdDo() );	
		
		
		logger.debug("data size:" + data.size() );
		
		if (data.isEmpty() ) { 
			
			logger.error("drawGrafH1 - nema podataka za datum :" + meteoConfigBean.getdOd() + "-" + meteoConfigBean.getdDo());
			return "index";


		}

		double histDataAvg[]= new double[data.size()];
		double histDataMin[]= new double[data.size()];
		double histDataMax[]= new double[data.size()];
		
		double histDataAvgAvg[]= new double[data.size()];
		double histDataMinAvg[]= new double[data.size()];
		double histDataMaxAvg[]= new double[data.size()];
		
		double histDataRefAvg[]= new double[data.size()];
		double histDataRefMin[]= new double[data.size()];
		double histDataRefMax[]= new double[data.size()];
		
		int i=0; for (PodaciAnal  x: data) {
			
			histDataAvg[i]=x.getTempSred().doubleValue();
			histDataMax[i]=x.getTempMax().doubleValue();
			histDataMin[i]=x.getTempMin().doubleValue();			
			
			histDataRefAvg[i]= meteoConfigBean.meanTempKorigirana( x.getDatum(), StatTip.AVG  );
			histDataRefMax[i]= meteoConfigBean.meanTempKorigirana( x.getDatum(), StatTip.MAX  );
			histDataRefMin[i]= meteoConfigBean.meanTempKorigirana( x.getDatum(), StatTip.MIN  );
			
			i=i+1;
			
		}
		
		double suma; 
		suma=0; for (double   x: histDataRefAvg ) suma=suma+x;   suma=suma/data.size();	for (int i2=0; i2<data.size(); i2++) histDataRefAvg[i2] =suma;
		suma=0; for (double   x: histDataRefMax ) suma=suma+x;   suma=suma/data.size();	for (int i2=0; i2<data.size(); i2++) histDataRefMax[i2] =suma;
		suma=0; for (double   x: histDataRefMin ) suma=suma+x;   suma=suma/data.size();	for (int i2=0; i2<data.size(); i2++) histDataRefMin[i2] =suma;

		suma=0; for (double   x: histDataAvg ) suma=suma+x;   suma=suma/data.size();	for (int i2=0; i2<data.size(); i2++) histDataAvgAvg[i2] =suma;
		suma=0; for (double   x: histDataMax ) suma=suma+x;   suma=suma/data.size();	for (int i2=0; i2<data.size(); i2++) histDataMaxAvg[i2] =suma;
		suma=0; for (double   x: histDataMin ) suma=suma+x;   suma=suma/data.size();	for (int i2=0; i2<data.size(); i2++) histDataMinAvg[i2] =suma;


		if (avg) dataset.addSeries("Avg_T", histDataAvg, data.size(), 
				          Math.min(getMin(histDataMin) ,getMin(histDataRefMin)), 
				          Math.max(getMax(histDataMax), getMax(histDataRefMax))  );
		
		if (max) dataset.addSeries("Max_T", histDataMax, data.size(), 
		          Math.min(getMin(histDataMin) ,getMin(histDataRefMin)), 
		          Math.max(getMax(histDataMax), getMax(histDataRefMax))  );
		
		if (min) dataset.addSeries("Min_T", histDataMin, data.size(), 
		          Math.min(getMin(histDataMin) ,getMin(histDataRefMin)), 
		          Math.max(getMax(histDataMax), getMax(histDataRefMax))  );
		
		
		
		
		if (avg) dataset.addSeries("Avg Avg_T", histDataAvgAvg, data.size()*10, 
		          Math.min(getMin(histDataMin) ,getMin(histDataRefMin)), 
		          Math.max(getMax(histDataMax), getMax(histDataRefMax))  );

		if (max) dataset.addSeries("Avg Max_T", histDataMaxAvg, data.size()*10, 
				Math.min(getMin(histDataMin) ,getMin(histDataRefMin)), 
				Math.max(getMax(histDataMax), getMax(histDataRefMax))  );

		if (min) dataset.addSeries("Avg Min_T", histDataMinAvg, data.size()*10, 
				Math.min(getMin(histDataMin) ,getMin(histDataRefMin)), 
				Math.max(getMax(histDataMax), getMax(histDataRefMax))  );
		
		
		
		
		
		
		
		
		
		if (ref) {
			if (avg) dataset.addSeries("Avg_T_Ref", histDataRefAvg, data.size()*10 , 
										Math.min(getMin(histDataMin) ,getMin(histDataRefMin)), 
										Math.max(getMax(histDataMax), getMax(histDataRefMax))  );
			
			if (max) dataset.addSeries("Max_T_Ref", histDataRefMax, data.size()*10 , 
					Math.min(getMin(histDataMin) ,getMin(histDataRefMin)), 
					Math.max(getMax(histDataMax), getMax(histDataRefMax))  );
			
			if (min) dataset.addSeries("Min_T_Ref", histDataRefMin, data.size()*10 , 
					Math.min(getMin(histDataMin) ,getMin(histDataRefMin)), 
					Math.max(getMax(histDataMax), getMax(histDataRefMax))  );
		}
		
       JFreeChart c1 = ChartFactory.createHistogram( "Histogram temperature", "Temp\u00b0C", "%", 
		                dataset, PlotOrientation.VERTICAL, true, true, false);		
		
		
       XYPlot plot = (XYPlot) c1.getPlot();
       //plot.setBackgroundPaint( Color.WHITE );
       XYBarRenderer bar_renderer = (XYBarRenderer)plot.getRenderer();
       int rbSerije=0;
       if (avg) { bar_renderer.setSeriesPaint( rbSerije, Color.orange );	rbSerije=rbSerije+1;}
       if (max) { bar_renderer.setSeriesPaint( rbSerije, Color.red );		rbSerije=rbSerije+1;}
       if (min) { bar_renderer.setSeriesPaint( rbSerije, Color.CYAN );		rbSerije=rbSerije+1;}

       if (avg) { bar_renderer.setSeriesPaint( rbSerije, Color.orange );	rbSerije=rbSerije+1;}
       if (max) { bar_renderer.setSeriesPaint( rbSerije, Color.red );		rbSerije=rbSerije+1;}
       if (min) { bar_renderer.setSeriesPaint( rbSerije, Color.CYAN );		rbSerije=rbSerije+1;}

		if (ref) {
			if (avg) { bar_renderer.setSeriesPaint( rbSerije, Color.lightGray );	rbSerije=rbSerije+1;}
			if (max) { bar_renderer.setSeriesPaint( rbSerije, Color.gray );			rbSerije=rbSerije+1;}
			if (min) { bar_renderer.setSeriesPaint( rbSerije, Color.darkGray );		rbSerije=rbSerije+1;}
		}

		File chartFile = new File("dynamic_grafH1.PNG");
		ChartUtilities.saveChartAsPNG(chartFile, c1, 1000, 600 );

		grafH1 = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");
		
		logger.debug("drawGrafH1 end ok");
		
		return "statisticsPage";
		
	}
		
	
	private double getMin(double[] x) {

	    Arrays.sort(x);

		return x[0];
	}




	private double getMax(double[] x) {

	    Arrays.sort(x);

		return x[x.length-1];
	}




	private List<PodaciAnal> getData(Date d1, Date d2) {
		
		logger.debug("getData , za datume " + d1 + " - " + d2);

		return meteoConfigBean.getLogin().getEm()
				.createQuery(
						"from PodaciAnal p where p.datum>= :datumOd and p.datum<= :datumDo  and p.grad= :grad order by p.datum asc",
						PodaciAnal.class)
				.setParameter("datumOd", d1)
				.setParameter("datumDo", d2)
				.setParameter("grad", meteoConfigBean.getSelectedLokacija() )
				.getResultList();
	}




	public MeteoConfigBean getMeteoConfigBean() {
		return meteoConfigBean;
	}

	public void setMeteoConfigBean(MeteoConfigBean meteoConfigBean) {
		this.meteoConfigBean = meteoConfigBean;
	}



	public StreamedContent getGrafH1() {
		return grafH1;
	}



	public void setGrafH1(StreamedContent grafH1) {
		this.grafH1 = grafH1;
	}




	public boolean isRef() {
		return ref;
	}




	public void setRef(boolean ref) {
		this.ref = ref;
	}





	public boolean isMin() {
		return min;
	}




	public void setMin(boolean min) {
		this.min = min;
	}




	public boolean isMax() {
		return max;
	}




	public void setMax(boolean max) {
		this.max = max;
	}




	public boolean isAvg() {
		return avg;
	}




	public void setAvg(boolean avg) {
		this.avg = avg;
	} 
    	

}