package beans;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.LineBorder;
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
import org.jfree.ui.VerticalAlignment;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.LinearAxis;

import com.bdb.weather.display.windrose.WindRoseData;
import com.bdb.weather.display.windrose.WindRosePlot;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;

import jaxb.Hrvatska;
import jaxb.Hrvatska.Grad;
import dao.Korisnici;
import dao.Podaci;
import dao.RunConfig;
import dao.SelektiraniGradovi;
import dao.Prosjeci;


@ManagedBean(name = "meteoConfigBean")
@SessionScoped
// @ApplicationScoped
public class MeteoConfigBean {

	//@Inject 
    @ManagedProperty(value="#{login}")
    private Login login;	

	static Logger logger = null;

	private List<RunConfig> runConfigList = new ArrayList<RunConfig>();
	private RunConfig selectedRunConfig;

	private List<String> gradoviPickList = new ArrayList<String>();
	private List<String> selektiraniGradoviList = new ArrayList<String>(); // String[]
    private DualListModel<String> gradovi;

	private StreamedContent grafTemp1Png; // za preko JFreeChart
	private StreamedContent grafWindRose1Png; // za preko JFreeChart

	private LineChartModel grafTemp1v2 = new LineChartModel(); // za direktno
																// preko PF

	private String selectedPeriod = "D";

	private String selectedTip = "T";
	private String selectedLokacija;
	
	private boolean splajn=true;

	private Date dOd;
	private Date dDo;
	
	private enum Sun { DAWN, DUSK	};

	// pomoćne mape
	private HashMap<String, String> nazivTipa = new HashMap();
	private HashMap<String, Integer> kut = new HashMap();
	
	private List<Prosjeci> meanTempList;
	
	private HashMap<String, int[]> dimenzije= new HashMap();
	
	
	private boolean testEnvironment=false;
	

	//ako se �eli vjetar u m/s staviti to na 1
	//public static Double     dMsToKmhFactor = 1.0;
	public static Double     dMsToKmhFactor = 3.6;	
	

		public enum StatTip { 
		
		MIN("Min"), AVG("Avg"), MAX("Max")	;
		
	    private final String txt;       

	    private StatTip (String s) {
	        txt = s;
	    }
	    public String toString() {
	        return this.txt;
	    }	
	}
	

	@PostConstruct
	public void initData() {

		// PropertyConfigurator.configure("classes/log4j.properties");
		logger = Logger.getLogger(MeteoConfigBean.class);

		logger.debug("MeteoConfigBean.initData()");

		if (login==null) login = new Login();
		setRunConfigList(login.getEm().createQuery("select r from RunConfig r").getResultList());

		// postavi defaultini odabir
		setSelectedRunConfig(getRunConfigList().get(1));





		// postavi tipove:
		if (selectedTip == null) selectedTip = "T";
		nazivTipa.put("T", "Temp \u00b0C");
		nazivTipa.put("P", "Tlak hPa");
		nazivTipa.put("H", "Vlaga %");
		nazivTipa.put("V", dMsToKmhFactor==1?"Vjetar m/s":"Vjetar km/h" );
		nazivTipa.put("V.J", dMsToKmhFactor==1?"Vjetar m/s":"Vjetar km/h" );
		nazivTipa.put("V.S", "Vjetar \u00b0");

		// postavio kuteve
		kut.put("N", 0);
		kut.put("NE", 45);
		kut.put("E", 90);
		kut.put("SE", 135);
		kut.put("S", 180);
		kut.put("SW", 225);
		kut.put("W", 270);
		kut.put("NW", 315);
		kut.put("C", null);  //null


		dimenzije.put("D", new int[] { 1200, 700 });
		dimenzije.put("1D", new int[] { 1200, 700 });
		dimenzije.put("2D", new int[] { 1400, 700 });
		dimenzije.put("3D", new int[] { 1400, 700 });
		dimenzije.put("7D", new int[] { 1400, 700 });
		
		dimenzije.put("M", new int[] { 2200, 700 });
		dimenzije.put("30D", new int[] { 2200, 700 });
		
		try {
			postaviGradove();
		} catch (ParseException e) {
			logger.error("initData postaviGradove:", e);
		}
		
		
		//prosjeci:
		loadProsjeci();



		// postavi vrijeme:
		try {
			promjenaPerioda();
		} catch (ParseException e1) {
			logger.error("initData promjenaPerioda:", e1);
		}
		
		
		
		String envName=System.getProperty("ENV_NAME");  
		if (envName==null) envName="PROD";
		
		logger.debug("ENV_NAME=" +envName);
		
		setTestEnvironment(envName.equals("TEST")?true:false);

	}

	private void loadProsjeci() {
		
		meanTempList= login.getEm().createQuery("select p from Prosjeci p where p.grad= :grad1 order by p.mjesec")
					.setParameter("grad1", selectedLokacija )
					.getResultList();		
	}

	
	
	public void callMeteoDataWholeDay()  {
		
		List<RunConfig> rcl = login.getEm().createQuery("select r from RunConfig r where r.uri like '/tablice%' order by r.uri").getResultList();

		String msg="";
		int sat= new Date().getHours();
		
		for (RunConfig x : rcl) {
			
			try {
				if (new Integer(x.getNaziv().substring(x.getNaziv().indexOf("-")+1))<sat ) callMeteoData2(x);
				
				logger.debug("callMeteoDataWholeDay -pozvan  target, ok : " + x.getNaziv());
				
				msg+= (msg.equals("")?x.getNaziv():","+x.getNaziv()  ); 
				

			} catch (Exception e) {
				logger.error("callMeteoDataWholeDay -za   target: " + x.getNaziv()+ " greška ", e);
			}
			
		}
		logger.debug("callMeteoDataWholeDay -uspješno pozvani targeti: " + msg);
		
		try {
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage("Successful", "callMeteoDataWholeDay -uspješno pozvani targeti: " + msg));
		} catch (Exception e) {
		}
		
		
	}
	
	/**
	 * Glavni handler quartz joba za snimanje podataka
	 */
	public void callMeteoData() {	
		
		try {
			callMeteoData2(selectedRunConfig);


			logger.info("callMeteoData ok ");
			
			try {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Successful", "callMeteoData ok"));
			} catch (Exception e) {	}
			
		} catch (Exception e) {
			
			logger.error("callMeteoData - greška ", e);
			try {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("callMeteoData error", e.getMessage() ));
			} catch (Exception e2) {	}

		}

	}	
	
	public void callMeteoData2(RunConfig runConf1) throws Exception {


			URL url = new URL("https://" + runConf1.getHost() + runConf1.getUri());

			logger.debug("callMeteoData - target: " + url.getHost() +  url.getPath());

			InputStream in = url.openStream();

			JAXBContext jc = JAXBContext.newInstance(Hrvatska.class);

			Unmarshaller unmarshaller = jc.createUnmarshaller();
			Hrvatska data;

			data = (Hrvatska) unmarshaller.unmarshal(in);

			// Marshaller marshaller = jc.createMarshaller();
			// marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// marshaller.marshal(data, System.out);

			in.close();

			// System.out.println( data.getGrad().get(0).getGradIme() );

			EntityTransaction tx = login.getEm().getTransaction();
			tx.begin();

			Date sada = (new SimpleDateFormat("dd.MM.yyyy HH"))
					.parse(data.getDatumTermin().getDatum() + " " + data.getDatumTermin().getTermin());

			List<Podaci> x = login.getEm().createQuery("from Podaci p where p.datum= :datum ", Podaci.class)
					.setParameter("datum", sada).getResultList();

			if (!x.isEmpty()) {

				logger.error("callMeteoData - duplikat za  datum:" + sada);







				tx.rollback();
				return;

			}

			for (Grad g : data.getGrad()) {

				if (selektiraniGradoviList.contains(g.getGradIme()))
					logger.debug("callMeteoData - pohrana za grad:" + g.getGradIme());
				else
					continue;

				Podaci p = new Podaci();
				p.setDatum(sada);
				p.setTermin(data.getDatumTermin().getTermin());
				p.setGrad(g.getGradIme());
				p.setAutom(g.getAutom().equals(1) ? true : false);
				p.setTemp(BigDecimal.valueOf(g.getPodatci().getTemp()));

				p.setVlaga(new Integer(g.getPodatci().getVlaga()));
				try {
					p.setTlak(new BigDecimal(g.getPodatci().getTlak().replaceAll("\\*", "").trim()));
				} catch (Exception e) {
					logger.error("puklo:>" + g.getPodatci().getTlak() + "<");
				}

				p.setTlakTend(String.valueOf(g.getPodatci().getTlakTend()));
				p.setVjetarSmjer(g.getPodatci().getVjetarSmjer());
				p.setVjetarBrzina(BigDecimal.valueOf(g.getPodatci().getVjetarBrzina()));
				p.setVrijeme(g.getPodatci().getVrijeme());
				p.setVrijemeZnak(String.valueOf(g.getPodatci().getVrijemeZnak()));

				login.getEm().persist(p);

				// break;

			}

			tx.commit();





	}

	/** za preth. mjesec, okida se 1. u mjesecu
	 * @throws ParseException
	 */
	public void callMonthlyAnalysis() throws ParseException {

		Date d2 = pocetakMjeseca(danasnjiDan(false));
		Date d1 = addMonths(d2,-1);
		
		callMonthlyAnalysis(d1,d2);
	}	
	
	/** za selektirani mjesec
	 * @throws ParseException
	 */
	public void callMonthlyAnalysisSelektirani() throws ParseException {

		Date d1 = pocetakMjeseca(dOd);
		Date d2 = pocetakMjeseca(addMonths(dOd,  1));
		
		callMonthlyAnalysis(d1,d2);
	}	
	
	public void callMonthlyAnalysis(Date d1, Date d2) throws ParseException {
		
		EntityTransaction tx = login.getEm().getTransaction();
		tx.begin();
		

		
		try {
		login.getEm().createNativeQuery("delete from meteo_dhmz.podaci_anal where datum >= ?1 and datum< ?2")
		.setParameter(1, d1)
		.setParameter(2, d2)
		.executeUpdate();
		
		
		login.getEm().createNativeQuery("insert into meteo_dhmz.podaci_anal (grad, datum, temp_min, temp_max, temp_sred) "+
		" select grad, date(datum), min(temp) as min1, max(temp) as max1, avg(temp) as avg1" + 
		" from meteo_dhmz.podaci where datum >= ?1 and datum < ?2 " +
		" group by grad, date(datum) order by grad , date(datum)")
		.setParameter(1, d1)
		.setParameter(2, d2)
		.executeUpdate();

		
		tx.commit();
		
		logger.debug("callMonthlyAnalysis ok, za razdoblje "+getPeriod(d1,d2) );
		
		try {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("callMonthlyAnalysis ok, za razdoblje "+getPeriod(d1,d2) ));
		} catch (Exception e2) {	}
		
		
		
		} catch (Exception e) {	
			
			logger.error("callMonthlyAnalysis error, za razdoblje "+getPeriod(d1,d2), e );
			tx.rollback();
			
		}		
	}




	/**
	 * Crtač grafa, preko JFreeChart
	 */
	public String grafJFreeChart() throws ParseException, IOException {

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		TimeSeriesCollection dataset2 = new TimeSeriesCollection();
		TimeSeriesCollection dataset3 = new TimeSeriesCollection();
		
		TimeSeriesCollection dataset4 = new TimeSeriesCollection();
		
		TimeSeries tSeries1 = new TimeSeries(nazivTipa.get(selectedTip), Hour.class);
		TimeSeries tSeries2 = new TimeSeries("Smjer (N=0\u00b0, E=90\u00b0, S=180\u00b0, W=270\u00b0)", Hour.class);
		TimeSeries tSeries3SrVr = new TimeSeries("Sr.vr.dnevna", Hour.class);
		TimeSeries tSeries3SrVrD = new TimeSeries("Sr.vr.dnevna dan", Hour.class);
		TimeSeries tSeries3SrVrN = new TimeSeries("Sr.vr.dnevna noć", Hour.class);
		TimeSeries tSeries3SrVrUk = new TimeSeries("Sr.razdob.", Hour.class);
		TimeSeries tSeries3SrKat = new TimeSeries("Sr.dnevna.povijesno", Hour.class);     //prosj. mjes. vrijednost (statistička) prema povijesnim podacima
		
		TimeSeries tSeries4Dan = new TimeSeries("izl./zal.", Hour.class);
		

		List<Podaci> data = null;

		data = getData_Day(selectedLokacija, dOd, dDo);

	    HashMap<String,Double > srVrMap = new HashMap<String,Double >();
	    HashMap<String,Double > srVrDMap = new HashMap<String,Double >();
	    HashMap<String,Double > srVrNMap = new HashMap<String,Double >();
	        
		Double srVr = 0.0;
		Double srVrD = 0.0;
		Double srVrN = 0.0;
		Double srVrUk =0.0;
		int nD = 0;
		int nN = 0;
		int nUk =0;
		String oldDatum=null;
		Integer kutOld=null;
		
		BigDecimal bdMsToKmhFactor = new BigDecimal(dMsToKmhFactor);
		

	
		SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		if (!data.isEmpty()) {

			logger.debug("grafTemperatureDnevni, data:" + data.size());

			for (Podaci p : data) {

				Hour h = new Hour(f2.parse(p.getDatumString()));
				
				
				if (!p.getDatumStringDate().equals(oldDatum))	{
					//nova sr.series3SrVr.
					if (oldDatum!=null)  {
			
						srVrMap.put(oldDatum, srVr/(nD+nN) );
						srVrDMap.put(oldDatum, srVrD/nD);
						srVrNMap.put(oldDatum, srVrN/nN);
			
						nUk+=nD+nN;
						srVr=0.0; srVrD=0.0; srVrN=0.0; 
						nD=0; nN=0;
					}
					oldDatum=p.getDatumStringDate();
		
				}
				
			

				if (selectedTip.equals("T")) {
					if (p.getTemp() != null)
						tSeries1.add(h, p.getTemp());
				} else if (selectedTip.equals("P")) {
					if (p.getTlak() != null)
						tSeries1.add(h, p.getTlak());
				} else if (selectedTip.equals("H")) {
					if (p.getVlaga() != null)
						tSeries1.add(h, p.getVlaga());
				} else if (selectedTip.equals("V") || selectedTip.equals("V.J") ) {
					if (p.getVjetarBrzina() != null)
						tSeries1.add(h, p.getVjetarBrzina().multiply(bdMsToKmhFactor));
				}
				
				Integer kutNew = kut.get(p.getVjetarSmjer());
				if (kutOld!=null && kutNew!=null && kutNew.equals(0) && kutOld>=225 ) kutNew=360;
				if (kutNew!=null) kutOld=kutNew;
				
				if (p.getVjetarSmjer() != null && kut.get(p.getVjetarSmjer()) != null)
					tSeries2.add(h, kutNew);

				// srednja vrijednost:
				if (selectedTip.equals("T")) {
					srVr = srVr + p.getTemp().doubleValue();
					srVrUk = srVrUk + p.getTemp().doubleValue();	}
				else if (selectedTip.equals("P")) {
					if (p.getTlak()!=null) {
					srVr = srVr + p.getTlak().doubleValue();
					srVrUk = srVrUk + p.getTlak().doubleValue();	}}
				else if (selectedTip.equals("H")) {
					srVr = srVr + p.getVlaga();
					srVrUk = srVrUk + p.getVlaga();	}
				else if ( ( selectedTip.equals("V") || selectedTip.equals("V.J") )  && p.getVjetarBrzina()!=null ) {
					srVr = srVr + p.getVjetarBrzina().doubleValue()*dMsToKmhFactor;
					srVrUk = srVrUk + p.getVjetarBrzina().doubleValue()*dMsToKmhFactor;	}
				
				else if ( selectedTip.equals("V.S")   && p.getVjetarSmjer()!=null && kut.get(p.getVjetarSmjer())!=null ) {
					srVr = srVr + kutNew.doubleValue();
					srVrUk = srVrUk + kutNew.doubleValue();	}


				double izlazak = getIzlazakSunca(p.getDatum()); 
				double zalazak = getZalazakSunca(p.getDatum()); 
				
				//logger.debug(p.getDatum() +": izlazak:"+izlazak+ ", zalazak="+zalazak);
				// sr.vr.Dan
				if (p.getTermin() >= izlazak && p.getTermin() <= zalazak ) {
					
					//dan
					 tSeries4Dan.add(h, 360);
					 
					
					if (selectedTip.equals("T")) {
						nD++;
						srVrD = srVrD + p.getTemp().doubleValue();
					} else if (selectedTip.equals("P")) {
						if (p.getTlak()!=null) {
							nD++;
							srVrD = srVrD + p.getTlak().doubleValue();}
					} else if (selectedTip.equals("H")) {
						nD++;
						srVrD = srVrD + p.getVlaga();
					} else if ((selectedTip.equals("V") || selectedTip.equals("V.J"))  && p.getVjetarBrzina()!=null ) {
						nD++;
						srVrD = srVrD + p.getVjetarBrzina().doubleValue()*dMsToKmhFactor;
					} else if (selectedTip.equals("V.S")  && p.getVjetarSmjer()!=null && kut.get(p.getVjetarSmjer())!=null) {
						nD++;
						srVrD = srVrD +  kutNew.doubleValue();
					}

					
				}
				// sr.vr.Noc
				else {
					
					//noć
					 tSeries4Dan.add(h, 0);
					
					
					if (selectedTip.equals("T")) {
						nN++;
						srVrN = srVrN + p.getTemp().doubleValue();
					} else if (selectedTip.equals("P")) {
						if (p.getTlak()!=null) {
						nN++;
						srVrN = srVrN + p.getTlak().doubleValue();}
					} else if (selectedTip.equals("H")) {
						nN++;
						srVrN = srVrN + p.getVlaga();
					} else if ( (selectedTip.equals("V") || selectedTip.equals("V.J"))  && p.getVjetarBrzina()!=null ) {
						nN++;
						srVrN = srVrN + p.getVjetarBrzina().doubleValue()*dMsToKmhFactor;
					} else if (selectedTip.equals("V.S")  && p.getVjetarSmjer()!=null && kut.get(p.getVjetarSmjer())!=null) {
						nN++;
						srVrN = srVrN +  kutNew.doubleValue();
					}

					
				}

			}
			
			//za zadnji dan nije okinuto
			nUk+=nD+nN;
			srVrMap.put(oldDatum, srVr/(nD+nN) );
			srVrDMap.put(oldDatum, srVrD/nD);
			srVrNMap.put(oldDatum, srVrN/nN);
			srVrUk = srVrUk/nUk;

			for (Podaci p : data) {
				Hour h = new Hour(f2.parse(p.getDatumString()));
				tSeries3SrVr.add(h, srVrMap.get(p.getDatumStringDate()));
				tSeries3SrVrD.add(h, srVrDMap.get(p.getDatumStringDate()));
				tSeries3SrVrN.add(h, srVrNMap.get(p.getDatumStringDate()));
				tSeries3SrVrUk.add(h, srVrUk);
				if (! meanTempList.isEmpty() ) tSeries3SrKat.add(h, meanTempKorigirana(p.getDatum(), StatTip.AVG ) );
			}

		} else {

			logger.error("grafTemperatureDnevni - nema podataka za datum :" + dOd + "-" + dDo);

			return "index";

		}

		
		
		if (!selectedTip.equals("V.S")) dataset.addSeries(tSeries1);

		if (selectedTip.equals("V") || selectedTip.equals("V.S") )
			dataset2.addSeries(tSeries2);
		 
		if (! selectedTip.equals("V")  )	dataset3.addSeries(tSeries3SrVr);
			
			if (selectedTip.equals("T") || selectedTip.equals("H") || selectedTip.equals("V.J") || selectedTip.equals("V.S")  ) {

				dataset3.addSeries(tSeries3SrVrD);
				dataset3.addSeries(tSeries3SrVrN);
			}
			
			if (! selectedTip.equals("V")  && !selectedPeriod.equals("D") && !selectedPeriod.equals("1D"))
				dataset3.addSeries(tSeries3SrVrUk);
			
		if (selectedTip.equals("T") && !meanTempList.isEmpty() )dataset3.addSeries(tSeries3SrKat);
			
		
		dataset4.addSeries(tSeries4Dan);
		
		
		

		JFreeChart c1 = ChartFactory.createTimeSeriesChart(selectedLokacija + ", " + getPeriod(), "",
				nazivTipa.get(selectedTip), (XYDataset) dataset, false, true, false);

		ValueAxis rangeAxis = c1.getXYPlot().getRangeAxis();
		rangeAxis.setLabelFont(new Font(rangeAxis.getLabelFont().getName(), Font.PLAIN, rangeAxis.getLabelFont().getSize()));

		c1.getXYPlot().getRangeAxis().setAutoRange(true);
		((NumberAxis) c1.getXYPlot().getRangeAxis()).setAutoRangeIncludesZero(false);

		// ((NumberAxis)c1.getCategoryPlot().getRangeAxis()).setTickUnit(new
		// NumberTickUnit(5));

		c1.getXYPlot().getDomainAxis().setVerticalTickLabels(true);
		c1.getXYPlot().getDomainAxis().setAutoTickUnitSelection(true);

		// c1.getXYPlot().setColumnRenderingOrder(SortOrder.ASCENDING);

		c1.getXYPlot().setBackgroundPaint(Color.decode("0XE0E0E0"));

		XYLineAndShapeRenderer renderer0;
		if (splajn)  {  renderer0 = new XYSplineRenderer(); ((XYSplineRenderer)renderer0).setPrecision(3);}
		else 			renderer0 = new XYLineAndShapeRenderer();
		
		

		c1.getXYPlot().setRenderer(0, renderer0);
		c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(0)).setSeriesPaint(0, Color.decode("0X43B7BA"));
		c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(0)).setSeriesStroke(0, new BasicStroke(
				3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 6.0f, 0.0f }, 0.0f));

		// dvije y-koord.
		if (selectedTip.equals("V") || selectedTip.equals("V.S") ) {
			final NumberAxis y2 = new NumberAxis("Smjer (N=0\u00b0, E=90\u00b0, S=180\u00b0, W=270\u00b0)");
			y2.setRange(0,360);
			
			c1.getXYPlot().setRangeAxis(1, y2);
			c1.getXYPlot().setDataset(1, dataset2);
			c1.getXYPlot().mapDatasetToRangeAxis(1, 1);

			XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer();
			c1.getXYPlot().setRenderer(1, renderer1);
			c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(1)).setSeriesPaint(0,Color.decode("0XFA9B17"));
			c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(1)).setSeriesStroke(0, new BasicStroke(
					3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 6.0f, 0.0f }, 0.0f));
		}

		// dvije y-koord.
		//else if (!selectedTip.equals("V")) {

			XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer();
			c1.getXYPlot().setDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1, dataset3);
			c1.getXYPlot().setRenderer(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1, renderer2);
			
			c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1)).setSeriesPaint(0,	selectedTip.equals("V.S")?Color.decode("0XFA9B17"): Color.decode("0X43B7BA"));
			c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1)).setSeriesStroke(0, new BasicStroke(1.0f,
					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 6.0f, 6.0f }, 0.0f));
			c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1)).setSeriesShape(0,
					new Rectangle(0, 0, 0, 0));

			if (selectedTip.equals("T") || selectedTip.equals("H") || selectedTip.equals("V.J") || selectedTip.equals("V.S") ) {
				c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1)).setSeriesShape(1,	new Rectangle(0, 0, 0, 0));
				c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1)).setSeriesShape(2,	new Rectangle(0, 0, 0, 0));
				c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1)).setSeriesStroke(1, new BasicStroke(
						1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 6.0f, 6.0f }, 0.0f));
				c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1)).setSeriesStroke(2, new BasicStroke(
						1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 6.0f, 6.0f }, 0.0f));
				
				c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1)).setSeriesPaint(1,	Color.decode("0XFFFFFF"));
				c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1)).setSeriesPaint(2,	Color.decode("0X000000"));
				
			}
			
			//srvr ukupno
			int jeSrvr=0;
			if (!selectedPeriod.equals("D") && !selectedPeriod.equals("1D")) {
				jeSrvr++;
				c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1)).setSeriesPaint( (selectedTip.equals("P") ?1:3),	Color.decode("0XE473E7"));
				c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1)).setSeriesStroke((selectedTip.equals("P") ?1:3), new BasicStroke(1.0f,
					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 6.0f, 6.0f }, 0.0f));
				c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1)).setSeriesShape((selectedTip.equals("P")?1:3),
					new Rectangle(0, 0, 0, 0));

			}
			
			//srvr temp. kat.
			if (selectedTip.equals("T") && !meanTempList.isEmpty()  ) {
				c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1)).setSeriesPaint(jeSrvr+ (selectedTip.equals("P")?1:3),	Color.decode("0XFFFC17"));
				c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1)).setSeriesStroke(jeSrvr+(selectedTip.equals("P") ?1:3), new BasicStroke(1.0f,
					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 6.0f, 6.0f }, 0.0f));
				c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?2:1)).setSeriesShape(jeSrvr+(selectedTip.equals("P") ?1:3),
					new Rectangle(0, 0, 0, 0));

			}	
			
			
		//}			
			//dan

			c1.getXYPlot().setDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?3:2, dataset4);
			XYStepRenderer renderer3 = new XYStepRenderer() ; // XYLineAndShapeRenderer();
			c1.getXYPlot().setRenderer(selectedTip.equals("V") || selectedTip.equals("V.S")?3:2, renderer3);
			c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?3:2)).setSeriesShape(0,	new Rectangle(0, 0, 0, 0));
			c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?3:2)).setSeriesStroke(0, new BasicStroke(
					1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 6.0f, 0.0f }, 0.0f));
			c1.getXYPlot().getRendererForDataset(c1.getXYPlot().getDataset(selectedTip.equals("V") || selectedTip.equals("V.S")?3:2)).setSeriesPaint(0,	Color.decode("0XBCC7C7"));
			
			
			if (!selectedTip.equals("V") && !selectedTip.equals("V.S")) {
				final NumberAxis y23 = new NumberAxis("");
				y23.setRange(0,360);
				y23.setVisible(false);
				c1.getXYPlot().setRangeAxis(1, y23);
	
			}
			
			if (selectedTip.equals("V.S")) {
				c1.getXYPlot().getRangeAxis(0).setRange(0,360);
				c1.getXYPlot().getRangeAxis(0).setVisible(false);

			}
			c1.getXYPlot().mapDatasetToRangeAxis(selectedTip.equals("V") || selectedTip.equals("V.S")?3:2, 1 );		
			

		
		
		//tck labels

		DateFormat formatter = new SimpleDateFormat("dd.MMM, HH:mm");
		//((org.jfree.chart.axis.DateAxis)(c1.getXYPlot().getDomainAxis())).setAutoTickUnitSelection(false);
		((org.jfree.chart.axis.DateAxis)(c1.getXYPlot().getDomainAxis())).setDateFormatOverride(formatter);

	
		// legend
		LegendTitle lt = new LegendTitle(c1.getXYPlot());
		lt.setFrame(new BlockBorder(Color.white));
		lt.setPosition(RectangleEdge.BOTTOM);
		XYTitleAnnotation ta = new XYTitleAnnotation(0.98, 0.02, lt, RectangleAnchor.BOTTOM_RIGHT);
		c1.getXYPlot().addAnnotation(ta);
		String username= login.getKorisnik()!=null && login.getKorisnik().getUsername()!=null?login.getKorisnik().getUsername():"";
		File chartFile = new File("/opt/tomcat/webapps/meteo_2016/graf1_"+username);
		ChartUtilities.saveChartAsPNG(chartFile, c1, dimenzije.get(selectedPeriod)[0], dimenzije.get(selectedPeriod)[1], null, false,9);

		grafTemp1Png = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");

		logger.debug("grafTemperatureDnevni end ok");
		
		return "index";

	}
	
	
	/**
	 * Crtač grafa, preko JFreeChart
	 */
	public String grafWindRose1() throws ParseException, IOException {
		
		WindRosePlot p1 = new WindRosePlot();
		
		List<Podaci> data = getData_Day(selectedLokacija, dOd, dDo);
		
		WindRoseData wd= new WindRoseData(data, 8);
		
		p1.setDataset(wd);
        JFreeChart c1 = new JFreeChart("Ruža vjetrova-"+selectedLokacija + ", " + getPeriodShort(), p1);
        c1.getLegend().setPosition(RectangleEdge.RIGHT);
        c1.getLegend().setVerticalAlignment(VerticalAlignment.BOTTOM );
        c1.getLegend().setBorder(0, 0, 0, 0);
        c1.getLegend().setMargin(0, 0, 5, 5); 

        String username= login.getKorisnik()!=null && login.getKorisnik().getUsername()!=null?login.getKorisnik().getUsername():"";
        
		File chartFile = new File("/opt/tomcat/webapps/meteo_2016/graf2_"+username );
		ChartUtilities.saveChartAsPNG(chartFile, c1, 800,600, null, false,5);

		grafWindRose1Png = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");

		logger.debug("grafWindRose1 end ok");
			
		return "index";
	}


	


	private double getIzlazakSunca(Date datum) {
		
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(datum);
		double x = getDawnOrDusk(cal, Sun.DAWN);
		
		x += ljetnoVrijeme(cal);
		
		return x;
	}

	private double getZalazakSunca(Date datum) {
		
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(datum);

		double x = getDawnOrDusk(cal, Sun.DUSK);

		//korekcija:
		x += ljetnoVrijeme(cal);
		
		return x;
	}
	
	
	
	
	/** Vraća prosječnu god. temp. na traženi dan, iz kataloga
	 * @param datum
	 * @return
	 */
	public double meanTempKorigirana(Date datum, StatTip t ) {
		
		double x=0;
		if (t==StatTip.AVG)  x = meanTempList.get( datum.getMonth() ).getTempSred() .doubleValue() ;
		else if (t==StatTip.MAX)  x = meanTempList.get( datum.getMonth() ).getTempMax() .doubleValue() ;
		else if (t==StatTip.MIN)  x = meanTempList.get( datum.getMonth() ).getTempMin() .doubleValue() ;
		
		//korekcija:
		double korekcija=0;
		
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(datum);
		int day=   cal.get(Calendar.DAY_OF_MONTH);
		
		if (t==StatTip.AVG){
			if (day>15) korekcija =  (day-15)/30.*( meanTempList.get( (datum.getMonth() +1) %12  ).getTempSred() .doubleValue() -x);
			else if (day<15) korekcija =  (15-day)/30.*( meanTempList.get( (datum.getMonth() -1)>=0?datum.getMonth() -1:11  ).getTempSred() .doubleValue() -x);
		}		
		if (t==StatTip.MAX){
			if (day>15) korekcija =  (day-15)/30.*( meanTempList.get( (datum.getMonth() +1) %12  ).getTempMax() .doubleValue() -x);
			else if (day<15) korekcija =  (15-day)/30.*( meanTempList.get( (datum.getMonth() -1)>=0?datum.getMonth() -1:11  ).getTempMax() .doubleValue() -x);
		}		
		if (t==StatTip.MIN){
			if (day>15) korekcija =  (day-15)/30.*( meanTempList.get( (datum.getMonth() +1) %12  ).getTempMin() .doubleValue() -x);
			else if (day<15) korekcija =  (15-day)/30.*( meanTempList.get( (datum.getMonth() -1)>=0?datum.getMonth() -1:11  ).getTempMin() .doubleValue() -x);
		}		
			
		x += korekcija;
		

		//logger.debug("meanTempKorigirana za "+datum+", dan :"+day +": "+meanTempList.get( datum.getMonth() ).getTempSred() .doubleValue()+", korekcija:"+ korekcija+", konačno:"+ x );
		
		return x;
	}

	

	/**
	 * Crtač grafa, preko PrimeFacesa
	 * 
	 * @throws ParseException
	 */
	public void grafPF() throws ParseException {

		List<Podaci> data = getData_Day(selectedLokacija, dOd, dDo);
		if (data.isEmpty()) {

			logger.error("grafTemperatureDnevni - nema podataka za datum :" + dOd + "-" + dDo);
			try {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("nema podataka", "za " + getPeriod()));

			} catch (Exception e) {
			}
			return;
		}

		// grafTemp1v2.clear();
		grafTemp1v2 = new LineChartModel();
		grafTemp1v2.setTitle(selectedLokacija + ", " + getPeriod());

		grafTemp1v2.setLegendPosition("e");
		grafTemp1v2.setLegendPlacement(LegendPlacement.INSIDE);

		// grafTemp1v2.setZoom(true);
		DateAxis xAxis = new DateAxis("");
		xAxis.setTickAngle(-90);
		xAxis.setTickFormat("%d.%b, %H:%#M");
		
		if (selectedPeriod.equals("1D") || selectedPeriod.equals("D")) {
			xAxis.setTickInterval("3600000");
			xAxis.setTickCount(data.size());
		}

		
		grafTemp1v2.getAxes().put(AxisType.X, xAxis);

		ChartSeries series1 = new ChartSeries();
		series1.setLabel(nazivTipa.get(selectedTip));
		ChartSeries series2 = new ChartSeries();
		series2.setLabel("Smjer (N=0\u00b0, E=90\u00b0, S=180\u00b0, W=270\u00b0)");

		LineChartSeries series3SrVr = new LineChartSeries();
		series3SrVr.setLabel("Sr.vr.dnevna");
		series3SrVr.setShowMarker(false);

		LineChartSeries series3SrVrD = new LineChartSeries();
		series3SrVrD.setLabel("Sr.vr.dnevna dan");
		series3SrVrD.setShowMarker(false);

		LineChartSeries series3SrVrN = new LineChartSeries();
		series3SrVrN.setLabel("Sr.vr.dnevna noć");
		series3SrVrN.setShowMarker(false);

		LineChartSeries series3SrVrUk = new LineChartSeries();
		series3SrVrUk.setLabel("Sr.razdob.");
		series3SrVrUk.setShowMarker(false);
		
		LineChartSeries series3SrVrKat = new LineChartSeries();
		series3SrVrKat.setLabel("Sr.dnevna povijesno");
		series3SrVrKat.setShowMarker(false);

		series2.setYaxis(AxisType.Y2);
		
		//grafTemp1v2.setExtender("chartExtender");
		
		if ( selectedTip.equals("V.S" )) {
			series3SrVr.setYaxis(AxisType.Y2);
			series3SrVrD.setYaxis(AxisType.Y2);
			series3SrVrN.setYaxis(AxisType.Y2);
			series3SrVrUk.setYaxis(AxisType.Y2);
			
		}
					
		
        //Axis y1Axis = grafTemp1v2.getAxis(AxisType.Y);
        //yAxis.setMin(0);
        //yAxis.setMax(200);
                 
        Axis y1Axis = new LinearAxis(nazivTipa.get(selectedTip));
        grafTemp1v2.getAxes().put(AxisType.Y, y1Axis);
                 
        Axis y2Axis = new LinearAxis("Smjer (N=0\u00b0, E=90\u00b0, S=180\u00b0, W=270\u00b0)");
        y2Axis.setMin(0);
        y2Axis.setMax(360);
         
        grafTemp1v2.getAxes().put(AxisType.Y2, y2Axis);
		

        HashMap<String,Double > srVrMap = new HashMap<String,Double >();
        HashMap<String,Double > srVrDMap = new HashMap<String,Double >();
        HashMap<String,Double > srVrNMap = new HashMap<String,Double >();
        
		Double srVr = 0.0;
		Double srVrD = 0.0;
		Double srVrN = 0.0;
		Double srVrUk =0.0;
		int nD = 0;
		int nN = 0, nUk=0;
		String oldDatum=null;
		Integer kutOld=null;

		BigDecimal bdMsToKmhFactor = new BigDecimal(dMsToKmhFactor);
		
		
		Date minDatum=null, maxDatum=null;

		logger.debug("grafTemperatureDnevniV2, data:" + data.size());
		
		int i=0;
		for (Podaci p : data) {
  	        
			if (i++==0) minDatum= p.getDatum();		
						maxDatum= p.getDatum();	
						
						
			if (!p.getDatumStringDate().equals(oldDatum))	{
				//nova sr.series3SrVr.
				if (oldDatum!=null)  {
					
					srVrMap.put(oldDatum, srVr/(nD+nN) );
					srVrDMap.put(oldDatum, srVrD/nD);
					srVrNMap.put(oldDatum, srVrN/nN);
					
					nUk+=nD+nN;
					srVr=0.0; srVrD=0.0; srVrN=0.0; 
					nD=0; nN=0;
				}
				oldDatum=p.getDatumStringDate();
				
			}
						

			if (selectedTip.equals("T"))
				series1.set(p.getDatumString(), p.getTemp());
			else if (selectedTip.equals("P"))
				series1.set(p.getDatumString(), p.getTlak());
			else if (selectedTip.equals("H"))
				series1.set(p.getDatumString(), p.getVlaga());
			else if (selectedTip.equals("V") || selectedTip.equals("V.J"))
				series1.set(p.getDatumString(), p.getVjetarBrzina().multiply(bdMsToKmhFactor));
			
			
			//glačanje vjetra
			Integer kutNew = kut.get(p.getVjetarSmjer());
			if (kutOld!=null && kutNew!=null && kutNew.equals(0) && kutOld>=225 ) kutNew=360;
			if (kutNew!=null) kutOld=kutNew;
			
			if (p.getVjetarSmjer() != null && kut.get(p.getVjetarSmjer()) != null)
				series2.set(p.getDatumString(), kutNew );


			// xAxis.setMax( p.getDatumString() );

			// srednja vrijednost:
			if (selectedTip.equals("T")){
				srVr = srVr + p.getTemp().doubleValue();
				srVrUk = srVrUk + p.getTemp().doubleValue();}
			else if (selectedTip.equals("P")){
				if (p.getTlak()!=null) {
				srVr = srVr + p.getTlak().doubleValue();
				srVrUk = srVrUk + p.getTlak().doubleValue();
				}}
			else if (selectedTip.equals("H")){
				srVr = srVr + p.getVlaga();
				srVrUk = srVrUk + p.getVlaga();}
			else if ( (selectedTip.equals("V") || selectedTip.equals("V.J") ) &&  p.getVjetarBrzina() != null ){
				srVr = srVr + p.getVjetarBrzina().doubleValue()*dMsToKmhFactor;
				srVrUk = srVrUk + p.getVjetarBrzina().doubleValue()*dMsToKmhFactor;}
			else if ( selectedTip.equals("V.S" ) &&  p.getVjetarSmjer() != null && kut.get(p.getVjetarSmjer() )!=null){
				srVr = srVr + kutNew.doubleValue();
				srVrUk = srVrUk + kutNew.doubleValue();}
			// sr.vr.Dan
			double izlazak = getIzlazakSunca(p.getDatum()); 
			double zalazak = getZalazakSunca(p.getDatum()); 
			
			
			if (p.getTermin() >= izlazak  && p.getTermin() <= zalazak ) {
				if (selectedTip.equals("T")) {
					nD++;
					srVrD = srVrD + p.getTemp().doubleValue();
				} else if (selectedTip.equals("P")) {
					if (p.getTlak()!=null) {
					nD++;
					srVrD = srVrD + p.getTlak().doubleValue();}
				} else if (selectedTip.equals("H")) {
					nD++;
					srVrD = srVrD + p.getVlaga();
				} else if ((selectedTip.equals("V") || selectedTip.equals("V.J")) && p.getVjetarBrzina() != null) {
					nD++;
					srVrD = srVrD + p.getVjetarBrzina().doubleValue()*dMsToKmhFactor;
				} else if ( selectedTip.equals("V.S") && p.getVjetarSmjer() != null && kut.get(p.getVjetarSmjer() )!=null ) {
					nD++;
					srVrD = srVrD + kutNew.doubleValue();
				}

			}
			// sr.vr.Noc
			else  {
				if (selectedTip.equals("T")) {
					nN++;
					srVrN = srVrN + p.getTemp().doubleValue();
				} else if (selectedTip.equals("P")) {
					if (p.getTlak()!=null) {
					nN++;
					srVrN = srVrN + p.getTlak().doubleValue();}
				} else if (selectedTip.equals("H")) {
					nN++;
					srVrN = srVrN + p.getVlaga();
				} else if ((selectedTip.equals("V") || selectedTip.equals("V.J")) && p.getVjetarBrzina() != null ) {
					nN++;
					srVrN = srVrN + p.getVjetarBrzina().doubleValue()*dMsToKmhFactor;
				} else if ( selectedTip.equals("V.S") && p.getVjetarSmjer() != null && kut.get(p.getVjetarSmjer() )!=null ) {
					nN++;
					srVrN = srVrN + kutNew.doubleValue();
				}
			}

		}
		
		//za zadnji dan nije okinuto		
		srVrMap.put(oldDatum, srVr/(nD+nN) );
		srVrDMap.put(oldDatum, srVrD/nD);
		srVrNMap.put(oldDatum, srVrN/nN);
		nUk+=nD+nN;
		srVrUk = srVrUk / nUk;		



		for (Podaci p : data) {
			series3SrVr.set(p.getDatumString(), srVrMap.get(p.getDatumStringDate()));
			series3SrVrD.set(p.getDatumString(), srVrDMap.get(p.getDatumStringDate()));
			series3SrVrN.set(p.getDatumString(), srVrNMap.get(p.getDatumStringDate()));
			series3SrVrUk.set(p.getDatumString(), srVrUk);
			if (! meanTempList.isEmpty() )  series3SrVrKat.set(p.getDatumString(), meanTempKorigirana(p.getDatum(), StatTip.AVG  ) );
		}


		
		
		//minmax
		if ( selectedPeriod.equals("30D") || selectedPeriod.equals("M") ) { 
			
			minDatum= pocetakDana(minDatum); 
			maxDatum= pocetakDana(addDays(maxDatum,1)); 

			xAxis.setTickCount(((int)maxDatum.getTime()-(int)minDatum.getTime())/(60*60*24*1000) +1);
			xAxis.setTickInterval("864000000");
			
		}
		else if (selectedPeriod.equals("3D") || selectedPeriod.equals("7D")  ) { 
			
			minDatum= pocetakDana(minDatum); 
			maxDatum= pocetakDana(addDays(maxDatum,1)); 

			xAxis.setTickCount(((int)maxDatum.getTime()-(int)minDatum.getTime())/(60*60*6*1000) +1);
			xAxis.setTickInterval("216000000");
			
		}
		
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date datum = f.parse(f.format(new Date()));
		
		logger.debug("tick cnt="+xAxis.getTickCount()+ ",interval="+xAxis.getTickInterval());
		logger.debug("mind="+f.format(minDatum)+ ",maxd="+f.format(maxDatum) );

		grafTemp1v2.getAxis(AxisType.X).setMin(f.format(minDatum));		
		grafTemp1v2.getAxis(AxisType.X).setMax(f.format(maxDatum));		

		
		
		
		if (!selectedTip.equals("V.S") ) grafTemp1v2.addSeries(series1);
		if (selectedTip.equals("V") || selectedTip.equals("V.S") ){
			grafTemp1v2.addSeries(series2);
		}

		if (!selectedTip.equals("V") ) {
			
			grafTemp1v2.addSeries(series3SrVr);

			if (selectedTip.equals("T") || selectedTip.equals("H") || selectedTip.equals("V.J") || selectedTip.equals("V.S") )
				grafTemp1v2.addSeries(series3SrVrD);
			if (selectedTip.equals("T") || selectedTip.equals("H") || selectedTip.equals("V.J") || selectedTip.equals("V.S") )
				grafTemp1v2.addSeries(series3SrVrN);
			
			if (!selectedPeriod.equals("D") && !selectedPeriod.equals("1D")) 
				grafTemp1v2.addSeries(series3SrVrUk);
			
			if (! meanTempList.isEmpty() && selectedTip.equals("T") ) grafTemp1v2.addSeries(series3SrVrKat);
			
		
		}

			if (selectedTip.equals("V")   ) 
				grafTemp1v2.setSeriesColors("43B7BA,FA9B17");

			else if (selectedTip.equals("V.J")   ) 
				grafTemp1v2.setSeriesColors("43B7BA,43B7BA,FFFFFF,000000,E473E7");

			else if (selectedTip.equals("V.S")   ) 
				grafTemp1v2.setSeriesColors("FA9B17,FA9B17,FFFFFF,000000,E473E7");

			else if ((selectedTip.equals("T") ) && !selectedPeriod.equals("D") && !selectedPeriod.equals("1D")  ) 
				grafTemp1v2.setSeriesColors("43B7BA,43B7BA,FFFFFF,000000,E473E7,FFFC17");
			else if ((selectedTip.equals("T") ) && !(!selectedPeriod.equals("D") && !selectedPeriod.equals("1D"))  ) 
				grafTemp1v2.setSeriesColors("43B7BA,43B7BA,FFFFFF,000000,FFFC17");
			
			else if (selectedTip.equals("H") && !selectedPeriod.equals("D") && !selectedPeriod.equals("1D")  ) 
				grafTemp1v2.setSeriesColors("43B7BA,43B7BA,FFFFFF,000000,E473E7");
			else if (selectedTip.equals("H") && !(!selectedPeriod.equals("D") && !selectedPeriod.equals("1D"))  ) 
				grafTemp1v2.setSeriesColors("43B7BA,43B7BA,FFFFFF,000000");
			else 
				grafTemp1v2.setSeriesColors("43B7BA,43B7BA,E473E7");
			
//			grafTemp1v2.setSeriesColors("43B7BA,FA9B17");
//			grafTemp1v2.setSeriesColors("43B7BA,43B7BA,FFFFFF,000000,E473E7");

		logger.debug("grafTemperatureDnevniV2 end ok");

	}

	// ********************* pomoćne funkcije ********************************

	private Date pocetakMjeseca(Date datum) {

		Calendar C = new GregorianCalendar(1900 + datum.getYear(), datum.getMonth(), 1, 0, 0);
		return C.getTime();
	}

	private Date krajMjeseca(Date datum) {

		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, datum.getMonth());
		c.set(Calendar.YEAR, 1900 + datum.getYear());
		c.set(Calendar.DAY_OF_MONTH, 1);// This is necessary to get proper
										// results
		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		
		return c.getTime();

	}

	private Date pocetakDana(Date datum) {
		Date rd = new Date();
		rd.setTime(datum.getTime());
		rd.setHours(0);
		rd.setMinutes(0);
		return rd;
	}
	
	private Date krajDana(Date datum) {
		Date rd = new Date();
		rd.setTime(datum.getTime());
		rd.setHours(23);
		rd.setMinutes(59);
		return rd;
	}

	/**
	 * @param incomingDate
	 *            - ulazni datum
	 * @param dayCount
	 *            - br. dana za dodati, može biti i negativni broj
	 * @return Date
	 */
	public Date addDays(Date incomingDate, int dayCount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(incomingDate);
		cal.add(Calendar.DATE, dayCount); // minus number would decrement the
											// days
		return cal.getTime();

	}
	
	
	
	/**
	 * @param incomingDate
	 *            - ulazni datum
	 * @param dayCount
	 *            - br. mj. za dodati, može biti i negativni broj
	 * @return Date
	 */
	public Date addMonths(Date incomingDate, int monthCount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(incomingDate);
		cal.add(Calendar.MONTH, monthCount); // minus number would decrement the
											// days
		return cal.getTime();

	}

	/**
	 * @param vrijeme
	 *            - true -> i tren. vrijeme, false -> samo dan , vrijeme
	 *            =00:00:00
	 * @return Date
	 * @throws ParseException
	 */
	public Date danasnjiDan(boolean vrijeme) throws ParseException {

//		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
//		Date datum = f.parse(f.format(new Date()));
//		Date datumSat = new Date();
//
//		if (!vrijeme)
//			return datum;
//		else
//			return datumSat;
		
		return zadnjiDan(vrijeme);
		
	}
	
	/**
	 * zadnji postojeći dan u bazi
	 * @param vrijeme
	 * @return
	 * @throws ParseException
	 */
	public Date zadnjiDan(boolean vrijeme) throws ParseException {
		
		Date maxDatum= login.getEm().createQuery("select max(p.datum) from Podaci p",	Date.class).getSingleResult();
			
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		
		Date maxDatumShort = f.parse(f.format(maxDatum));
		
		if (!vrijeme)
			return maxDatumShort;
		else
			return maxDatum;
	}
	
	

	/**
	 * oku ugodan format datuma
	 */
	public String getPeriod() {	return getPeriodShort(dOd,dDo);  }
	public String getPeriodShort() {	return getPeriodShort(dOd,dDo);  }
	
	
	public String getPeriod(Date d1, Date d2) {

		SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm");

		return f.format(d1) + " - " + f.format(d2);

	}

	public String getPeriodShort(Date d1, Date d2) {

		SimpleDateFormat f1 = new SimpleDateFormat("dd.MM.");
		SimpleDateFormat f2 = new SimpleDateFormat("dd.MM.yyyy");
		if(f2.format(d1).equals(f2.format(d2)))
			return f2.format(d2);
		else
			return f1.format(d1) + " - " + f2.format(d2);

	}


	public void postaviGradove() throws ParseException {

		gradoviPickList= 
				login.getEm()	.createQuery("select distinct p.grad from Podaci p order by p.grad")
				//.setParameter("datumOd", dOd).setParameter("datumDo", dDo)
				.getResultList();

		selektiraniGradoviList = login.getEm().createQuery("select s.grad from SelektiraniGradovi s order by s.grad")
				.getResultList();
		

		for (String x : selektiraniGradoviList) gradoviPickList.remove(x);
		
		
		setGradovi(new DualListModel<String>(gradoviPickList, selektiraniGradoviList));	
		
		selectedLokacija=selektiraniGradoviList. get ( selektiraniGradoviList.indexOf("Zagreb-Maksimir") );
		
		logger.debug("postaviGradove PICKED size:" + gradoviPickList.size() +",SEL size:" + selektiraniGradoviList.size() );
	}

	/**
	 * @param grad
	 *            - lokacija podataka
	 * @param dOd
	 *            - datumski obuhvat podataka
	 * @param dDo
	 *            - datumski obuhvat podataka
	 * @return List<Podaci>
	 * @throws ParseException
	 */
	private List<Podaci> getData_Day(String grad, Date dOd, Date dDo) throws ParseException {

		logger.debug("getData , za datume " + dOd + " - " + dDo);

		return login.getEm()
				.createQuery(
						"from Podaci p where p.datum>= :datumOd and p.datum< :datumDo  and p.grad= :grad order by p.datum asc",
						Podaci.class)
				.setParameter("datumOd", dOd).setParameter("datumDo", dDo).setParameter("grad", grad).getResultList();

	}
	
	/** dali je na snazi ljetno vrijeme na traženi dan.
	 *  neprecizno vraća zadnje dane ožujka i zadnje dane listopada, ostalo ok.
	 * @param datum
	 * @return =1 za lj. vr, 0 za zimsko
	 */
//	public int ljetnoVrijeme(Date datum) {
//		if (datum.getMonth()>3-1 && datum.getMonth()<11-1) return +1;
//		return 0;
//	}
	public int ljetnoVrijeme(Calendar cal) {
	return cal.get(Calendar.DST_OFFSET) / (60* 60 * 1000);
	
}

	// ******************** handleri **********************************

	/**
	 * Handler gumba "period"
	 * 
	 * @throws ParseException
	 */
	public void promjenaPerioda() throws ParseException {

		Date datum = danasnjiDan(false);
		//Date datumSat = danasnjiDan(true);
		
		if (selectedPeriod.equals("1D")) {
			dOd = pocetakDana(dDo);
			dDo = krajDana(dDo);

		}	
		else if (selectedPeriod.equals("D")) {
			dDo = krajDana(datum);
			dOd = addDays(datum, -1);
	    	
	    	setSelectedPeriod("2D");
	    	
		}		

	    else if (selectedPeriod.equals("M")) {
			dOd = pocetakMjeseca(dDo);
			dDo = krajMjeseca(dDo);			
	    }
		else if (selectedPeriod.equals("2D")) {
			dDo = krajDana(dDo);
			dOd = addDays(pocetakDana(dDo), -1);
		}
		else if (selectedPeriod.equals("3D")) {
			dDo = krajDana(dDo);
			dOd = addDays(pocetakDana(dDo), -2);
		
		} else if (selectedPeriod.equals("7D")) {
			dDo = krajDana(dDo);
			dOd = addDays(pocetakDana(dDo), -6);
		} 
	    
	    
		logger.debug("promjenaPerioda: "+ selectedPeriod+ ",dOd="+dOd+",dDo="+dDo );
		
		
    	
			try {
				grafJFreeChart();
			} catch (Exception e) {
				logger.error("grafJFreeChart:promjenaPerioda:", e);
			}
			try {
				grafWindRose1();
			} catch (Exception e) {
				logger.error("grafWindRose1:promjenaPerioda:", e);
			}
			try {
				grafPF();
			} catch (Exception e) {
				logger.error("grafPF:promjenaPerioda:", e);
			}

	


	

	
	}

	/**
	 * Handler gumba "tip"
	 * 
	 * @throws ParseException
	 */
	public void promjenaTipa() throws ParseException {

		logger.debug("promjenaTipa:");

		try {
			grafJFreeChart();
		} catch (Exception e) {
			logger.error("grafJFreeChart:promjenaTipa:", e);
		}
		try {
			grafWindRose1();
		} catch (Exception e) {
			logger.error("grafWindRose1:promjenaTipa:", e);
		}
		try {
			grafPF();
		} catch (Exception e) {
			logger.error("grafPF:promjenaTipa:", e);
		}

	}

	/**
	 * Handler gumba "lokacija"
	 * 
	 * @throws ParseException
	 */
	public void promjenaLokacije() throws ParseException {

		logger.debug("promjenaLokacije:");
		
		loadProsjeci();

		try {
			grafJFreeChart();
		} catch (Exception e) {
			logger.error("grafJFreeChart:promjenaLokacije:", e);
		}
		try {
			grafWindRose1();
		} catch (Exception e) {
			logger.error("grafWindRose1:promjenaLokacije:", e);
		}
		try {
			grafPF();
		} catch (Exception e) {
			logger.error("grafPF:promjenaLokacije:", e);
		}

	}

	/**
	 * PF Hamdler gumba "jucer"
	 */
	public void promjenaPeriodaJucer() {
		
		if (selectedPeriod.equals("1D")) {
			dOd = addDays(dOd, -1);
			dDo = addDays(dDo, -1);

		}
		else if (selectedPeriod.equals("M")){ 
			
			dOd = addMonths(dOd, -1);
			dOd = pocetakMjeseca(dOd);
			dDo = krajMjeseca(dOd);			
				
		}
		else if (selectedPeriod.equals("2D")){ 
			
			dOd = addDays(dOd, -2);
			dDo = addDays(dDo, -2);

				
		}			
		else if (selectedPeriod.equals("3D")){ 
			
			dOd = addDays(dOd, -3);
			dDo = addDays(dDo, -3);

				
		}		
		else if (selectedPeriod.equals("7D")){ 
			
			dOd = addDays(dOd, -7);
			dDo = addDays(dDo, -7);

				
		}		
		else return;
		
		
		try {
			grafJFreeChart();
			} catch (Exception e) {
				logger.error("grafJFreeChart:promjenaPeriodaJucer:", e);
			}
		try {
			grafWindRose1();
			} catch (Exception e) {
				logger.error("grafWindRose1:promjenaPeriodaJucer:", e);
			}
		try {
			grafPF();
			} catch (Exception e) {
				logger.error("grafPF:promjenaPeriodaJucer:", e);
			}

		
	}

	/**
	 * PF Hamdler gumba "sutra"
	 * 
	 * @throws Exception
	 */
	public void promjenaPeriodaSutra() throws Exception {

		if (selectedPeriod.equals("1D") ) {
			
			if( dDo.getTime() > danasnjiDan(false).getTime() ) {

				logger.error("promjenaPeriodaSutra -zadnji dan podataka!");
				try {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("limit", "max.datum"));

				} catch (Exception e) {
				}
				throw new Exception ("limit max datum");
				//return;

			}

			dOd = addDays(dOd, 1);
			dDo = addDays(dDo, 1);
		}
		else if (selectedPeriod.equals("M") ) {
			
			dOd = addMonths(dOd, 1);
			dOd = pocetakMjeseca(dOd);
			dDo = krajMjeseca(dOd);		
			
		}
		else if (selectedPeriod.equals("2D") ) {
			
			if( addDays(dOd, 2).getTime() > danasnjiDan(false).getTime() ) {

				logger.error("promjenaPeriodaSutra -zadnji dan podataka!");
				try {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("limit", "max.datum"));

				} catch (Exception e) {
				}
				throw new Exception ("limit max datum");
				//return;

			}

			dOd = addDays(dOd, 2);
			dDo = addDays(dDo, 2);
		}

		
		
		
		else if (selectedPeriod.equals("3D") ) {
			
			if( addDays(dOd, 3).getTime() > danasnjiDan(false).getTime() ) {

				logger.error("promjenaPeriodaSutra -zadnji dan podataka!");
				try {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("limit", "max.datum"));

				} catch (Exception e) {
				}
				throw new Exception ("limit max datum");
				//return;

			}

			dOd = addDays(dOd, 3);
			dDo = addDays(dDo, 3);
		}
		
		else if (selectedPeriod.equals("7D") ) {
			
			if( addDays(dOd, 7).getTime() > danasnjiDan(false).getTime() ) {

				logger.error("promjenaPeriodaSutra -zadnji dan podataka!");
				try {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("limit", "max.datum"));

				} catch (Exception e) {
				}
				throw new Exception ("limit max datum");
				//return;

			}

			dOd = addDays(dOd, 7);
			dDo = addDays(dDo, 7);
		}

		else return;
			
		try {
			grafJFreeChart();
		} catch (Exception e) {
			logger.error("grafJFreeChart:promjenaPeriodaSutra:", e);
		}
		try {
			grafWindRose1();
		} catch (Exception e) {
			logger.error("grafWindRose1:promjenaPeriodaSutra:", e);
		}
		try {
			grafPF();
		} catch (Exception e) {
			logger.error("grafPF:promjenaPeriodaSutra:", e);
		}

	}

	/**
	 * Handler za snimanje konfig forme
	 * 
	 * @return
	 */
	public String saveConfigPage() {

		EntityTransaction tx = login.getEm().getTransaction();
		tx.begin();
		
		gradoviPickList= gradovi.getSource();
		selektiraniGradoviList= gradovi.getTarget();

		login.getEm().createQuery("DELETE FROM SelektiraniGradovi s").executeUpdate();
		
		for (String grad : selektiraniGradoviList) {

			SelektiraniGradovi g = new SelektiraniGradovi(grad);
			login.getEm().persist(g);

		}

		tx.commit();

		logger.debug("saveConfigPage ok.");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Successful", "saveConfigPage ok"));

		return "index";
	}

	// ******************** geteri i seteri **********************************

	public List<RunConfig> getRunConfigList() {
		return runConfigList;
	}

	public void setRunConfigList(List<RunConfig> runConfigList) {
		this.runConfigList = runConfigList;
	}

	public RunConfig getSelectedRunConfig() {
		return selectedRunConfig;
	}

	public void setSelectedRunConfig(RunConfig selectedRunConfig) {
		this.selectedRunConfig = selectedRunConfig;
	}

	public StreamedContent getGrafTemp1Png() {
		return grafTemp1Png;
	}

	public LineChartModel getGrafTemp1v2() {
		return grafTemp1v2;
	}

	public StreamedContent getGrafWindRose1Png() {
		return grafWindRose1Png;
	}

	public List<String> getSelektiraniGradoviList() {
		return selektiraniGradoviList;
	}

	public void setSelektiraniGradoviList(List<String> selektiraniGradoviList) {
		this.selektiraniGradoviList = selektiraniGradoviList;
	}


	public String getSelectedPeriod() {
		return selectedPeriod;
	}

	public void setSelectedPeriod(String selectedPeriod) {
		this.selectedPeriod = selectedPeriod;
	}

	public String getSelectedTip() {
		return selectedTip;
	}

	public void setSelectedTip(String selectedTip) {
		this.selectedTip = selectedTip;
	}

	public String getSelectedLokacija() {
		return selectedLokacija;
	}

	public void setSelectedLokacija(String selectedLokacija) {
		this.selectedLokacija = selectedLokacija;
	}

	public Date getdOd() {
		return dOd;
	}

	public void setdOd(Date dOd) {
		this.dOd = dOd;
	}

	public Date getdDo() {
		return dDo;
	}

	public void setdDo(Date dDo) {
		this.dDo = dDo;
	}

	public boolean isSplajn() {
		return splajn;
	}

	public void setSplajn(boolean splajn) {
		this.splajn = splajn;
	}

	public String  getpFxSize() {
		if (selectedPeriod==null) return "1400";
		return Integer.toString(dimenzije.get(selectedPeriod)[0]) ;
	}

	public DualListModel<String> getGradovi() {
		return gradovi;
	}

	public void setGradovi(DualListModel<String> gradovi) {
		this.gradovi = gradovi;
	}

	public List<String> getGradoviPickList() {
		return gradoviPickList;
	}

	public void setGradoviPickList(List<String> gradoviPickList) {
		this.gradoviPickList = gradoviPickList;
	}

	public boolean isTestEnvironment() {
		return testEnvironment;
	}

	public void setTestEnvironment(boolean testEnvironment) {
		this.testEnvironment = testEnvironment;
	}

	public 	double getDawnOrDusk(Calendar cal, Sun sun) {

		Double dawn, dusk ;		
		
		int dayOfYear= cal.get(Calendar.DAY_OF_YEAR);	
		
		//zagreb
		dawn =  (4+(7-4)/2) + Math.cos(dayOfYear/366.*2*Math.PI)*(7-4)/2;
		dusk =  (16.5+(22-16.5)/2) - Math.cos(dayOfYear/366.*2*Math.PI)*(22-16.5)/2;

		
//		logger.info("For dayOfYear="+ dayOfYear + " dawn="+dawn+", dusk=" + dusk);

		if (sun.equals(Sun.DAWN))
			return dawn;
		else
			return dusk;
	}

	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

}