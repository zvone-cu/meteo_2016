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


@ManagedBean(name="schedulerJob", eager=false  )
public class SchedulerJob implements Job {
	
	  @ManagedProperty(value="#{meteoConfigBean}")
	  private MeteoConfigBean meteoConfigBean;	
	  
	  
      static  Logger logger = Logger.getLogger(SchedulerJob.class);
      
   	

	@Override
	public void execute(JobExecutionContext context)
		throws JobExecutionException {
		
		logger.debug("Starting SchedulerJob - hourlyDataJob ");
		
		
		try {
			if (meteoConfigBean==null)  {
				
				logger.debug("meteoConfigBean==null, init");
				
				meteoConfigBean= new MeteoConfigBean();
				meteoConfigBean.initData();
				
			}
			
			meteoConfigBean.callMeteoData();
			
		} catch (Exception e) {
			
			logger.error("callMeteoData error", e);
			//e.printStackTrace();
		}
		
		logger.debug("Ending ok, SchedulerJob - hourlyDataJob ");
		

	}

}
