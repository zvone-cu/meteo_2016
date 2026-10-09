package scheduler;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.component.FacesComponent;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import beans.MeteoConfigBean;


@ManagedBean(name="schedulerJob2", eager=false  )
public class SchedulerJob2 implements Job {
	
	  @ManagedProperty(value="#{meteoConfigBean}")
	  private MeteoConfigBean meteoConfigBean;	
	  
	  
      static  Logger logger = Logger.getLogger(SchedulerJob2.class);
      
   	

	@Override
	public void execute(JobExecutionContext context)
		throws JobExecutionException {
		
		logger.debug("Starting SchedulerJob2 - monthlyAnalysisJob ");
		
		
		try {
			if (meteoConfigBean==null)  {
				
				logger.debug("meteoConfigBean==null, init");
				
				meteoConfigBean= new MeteoConfigBean();
				meteoConfigBean.initData();
				
			}
			
			meteoConfigBean.callMonthlyAnalysis();
			
		} catch (Exception e) {
			
			logger.error("callMeteoData error", e);
			//e.printStackTrace();
		}
		
		logger.debug("Ending ok, SchedulerJob2 - monthlyAnalysisJob ");
		

	}

}
