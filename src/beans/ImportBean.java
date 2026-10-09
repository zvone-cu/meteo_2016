package beans;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.LinearAxis;
import org.primefaces.model.DefaultStreamedContent;

import jaxb.Hrvatska;
import jaxb.Hrvatska.Grad;
import dao.Podaci;
import dao.RunConfig;
import dao.SelektiraniGradovi;
import dao.Prosjeci;
import dao.RawImport;


@ManagedBean(name = "meteoImportBean", eager=false )
@RequestScoped
// @ApplicationScoped
public class ImportBean {

	static Logger logger = null;
	
	
	private String fajl ="D:\\\\tmp\\\\temp1\\\\new1.txt";
	private String grad ="Zagreb-Maksimir";
	
	@ManagedProperty(value="#{meteoConfigBean}")
	private MeteoConfigBean meteoConfigBean;
	
	private HashMap<String, String> vjetarMap = new HashMap();	
	
	
	
	@PostConstruct
	public void initData() {

		// PropertyConfigurator.configure("classes/log4j.properties");
		logger = Logger.getLogger(ImportBean.class);

		logger.debug("MeteoConfigBean.initData()");
		
		
		
		// postavio kuteve
		vjetarMap.put("Nor","N" );
		vjetarMap.put("Eas", "E");
		vjetarMap.put("Sou","S");
		vjetarMap.put("Wes","W");

		vjetarMap.put("North","N" );
		vjetarMap.put("East", "E");
		vjetarMap.put("South","S");
		vjetarMap.put("West","W");

		vjetarMap.put("NW", "NW");
		vjetarMap.put("NE", "NE");
		vjetarMap.put("SW", "SW");
		vjetarMap.put("SE","SE");
		
		vjetarMap.put("SSW", "S");
		vjetarMap.put("SSE", "S");
		vjetarMap.put("WSW", "W");
		
		vjetarMap.put("NNE", "N");
		vjetarMap.put("ENE", "E");
		vjetarMap.put("ESE", "E");
		vjetarMap.put("NNW", "N");
		vjetarMap.put("WNW", "W");
	
		vjetarMap.put("Var", null);
		vjetarMap.put("Variable", null);
		vjetarMap.put("Calm", null);
		
		

	}
	
	
	
	public String parseFajl() throws ParseException {

		
		pripremiTablicu();
		
		List<RawImport> data = 	meteoConfigBean.getLogin().getEm().createQuery("select r from RawImport r order by r.dateUTC")
								.getResultList();	
		
		EntityTransaction tx = meteoConfigBean.getLogin().getEm().getTransaction();
		tx.begin();
		
		SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Integer oldTermin=null;
		
		for (RawImport x : data ) {
			
			if (x.getTimeCEST().contains(":")) {
				logger.debug("sjebani zapis za morsko vrijeme="+ x.getTimeCEST()+", preskaèem.." ); 
				continue;
				
			}
		
			Podaci p = new Podaci();
			
			p.setTermin( new Integer(x.getTimeCEST().replaceAll("PM","").trim())+   (x.getTimeCEST().contains("PM")?12:0)    );
			
			if ( oldTermin!=null && p.getTermin()==oldTermin ) {
				logger.debug("dupliæ za termin="+ oldTermin ); 
				continue;
			}
			oldTermin=p.getTermin();
		
			p.setDatum( lokalnoVrijeme( f2.parse(x.getDateUTC() ))    ); 
			
			p.setGrad(grad);
			p.setTemp(x.getTemp());

			p.setVlaga(x.getVlaga());
			
			p.setTlak(x.getTlak()!=null && !x.getTlak().equals("")?new BigDecimal(x.getTlak()):null);
	

			p.setVjetarSmjer(vjetarMap.get(x.getVjetarSmjer()) );
			
			p.setVjetarBrzina(x.getVjetarBrzina().equals("Calm") ? null :new BigDecimal( x.getVjetarBrzina() ) );
			
			
			
			
			meteoConfigBean.getLogin().getEm().persist(p);

			logger.debug("ubaèen zapis za "+ grad +", termin="+ p.getTermin() ); 

			
		}
		
		
		tx.commit();

		logger.debug("parseFajl ok.");
		try {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Successful", "parseFajl ok"));
		} catch (Exception e) {} 
		
		
		
		
		
		
		
		
		
		
		return "importPage"; // "index";
		
	}


	private Date lokalnoVrijeme(Date incomingDate ) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(incomingDate);
		cal.add(Calendar.HOUR, 1+ meteoConfigBean.ljetnoVrijeme(cal));   //za zagreb i hrv.
		return cal.getTime();

	}


	private void pripremiTablicu() {
		
		EntityTransaction tx = meteoConfigBean.getLogin().getEm().getTransaction();
		tx.begin();

		meteoConfigBean.getLogin().getEm().createNativeQuery("DELETE FROM meteo_dhmz.raw_import").executeUpdate();

		meteoConfigBean.getLogin().getEm().createNativeQuery("LOAD DATA LOCAL INFILE '"+ fajl +"' INTO TABLE meteo_dhmz.raw_import  COLUMNS TERMINATED BY ',' ").executeUpdate();

		
		//obiši sve polsatne unose
		meteoConfigBean.getLogin().getEm().createNativeQuery("delete from meteo_dhmz.raw_import where timeCEST like '%:30%' ").executeUpdate();
		
		//obiši 24:00, to je slj.dan
		meteoConfigBean.getLogin().getEm().createNativeQuery("delete from meteo_dhmz.raw_import where timeCEST ='12:00 PM'").executeUpdate();
		
		//am pretvori u èisti sat
		meteoConfigBean.getLogin().getEm().createNativeQuery("update meteo_dhmz.raw_import set TimeCest = replace(TimeCest, '12:00 AM', '0')").executeUpdate();
		meteoConfigBean.getLogin().getEm().createNativeQuery("update meteo_dhmz.raw_import set TimeCest = replace(TimeCest, ':00 AM', '')").executeUpdate();
		
		//pm izbaci minute
		meteoConfigBean.getLogin().getEm().createNativeQuery("update meteo_dhmz.raw_import set TimeCest = replace(TimeCest, ':00 PM', ' PM')").executeUpdate();
		
		
		tx.commit();

		logger.debug("pripremiTablicu ok.");
		
	}


	public String getFajl() {
		return fajl;
	}


	public void setFajl(String fajl) {
		this.fajl = fajl;
	}


	public String getGrad() {
		return grad;
	}


	public void setGrad(String grad) {
		this.grad = grad;
	}
	
	
	public MeteoConfigBean getMeteoConfigBean() {
		return meteoConfigBean;
	}

	public void setMeteoConfigBean(MeteoConfigBean meteoConfigBean) {
		this.meteoConfigBean = meteoConfigBean;
	} 
    	

}