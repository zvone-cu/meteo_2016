package test.java;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import beans.MeteoConfigBean;

public class Tests {

	private static final Logger log = Logger.getLogger(Tests.class);

	MeteoConfigBean bean;
	
	
	@Before 
	public  void init() {
		 bean = new MeteoConfigBean();
	}

//	@Ignore
	@Test 
	public void Test1() throws ParseException
    {

		Calendar cal= Calendar.getInstance();
	
		SimpleDateFormat f= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		
		Date d1 = f.parse("2023-01-01 06:00:00");
		Date d2 = f.parse("2023-06-01 06:00:00");
		
		cal.setTime(d1);
		log.info("dan1-cal:"+ f.format(cal.getTime()));
		log.info("dan1-date:"+ f.format(d1));
		log.info("dan1-DST:"+ bean.ljetnoVrijeme(cal));
		
		cal.setTime(d2);
		log.info("dan2-cal:"+ f.format(cal.getTime()));
		log.info("dan2-date:"+ f.format(d2));
		log.info("dan2-DST:"+ bean.ljetnoVrijeme(cal));
		
		
		
		
		
    }
	
}
