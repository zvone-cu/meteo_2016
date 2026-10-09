package beans;
	 

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import dao.RunConfig;
	 
 

	@ManagedBean(name="runConfigConverter", eager=false  )
	@RequestScoped	
	//@FacesConverter("runConfigConverter")
	public class RunConfigConverter implements Converter {
		
		
  //@Inject 
  @ManagedProperty(value="#{meteoConfigBean}")
  private MeteoConfigBean meteoConfigBean;

		
		
		@Override
	    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
	        if(value != null && value.trim().length() > 0  ) {
	            try {
//	                return meteoConfigBean.getSelectedRunConfig();
	            	
	            	for ( RunConfig conf : meteoConfigBean.getRunConfigList() )
	            		if (Integer.parseInt(value) == conf.getId() ) return conf;
	            	
	            } catch(NumberFormatException e) {
	                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
	            }
	        }

	        return null;

	    }
	 
	    @Override
	    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
	        if(object != null) {
	            return String.valueOf(((RunConfig) object).getId());
	        }
	        else {
	            return null;
	        }
	    }

	    
	    
	    
		public MeteoConfigBean getMeteoConfigBean() {
			return meteoConfigBean;
		}

		public void setMeteoConfigBean(MeteoConfigBean meteoConfigBean) {
			this.meteoConfigBean = meteoConfigBean;
		} 
	    
	    
	    
	    
	    
	} 