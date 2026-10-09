package beans;

import java.io.Serializable;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import dao.Korisnici;
import security.HelperFunctions;
import security.SessionUtils;

@ManagedBean(name = "login")
@SessionScoped
public class Login implements Serializable {
	
	   // @PersistenceContext(unitName ="meteo")
	    private EntityManager em;
	    
		private Korisnici korisnik;


		private static final long serialVersionUID = 1094801825228386363L;
		
		static Logger logger = Logger.getLogger(Login.class);
		
		private String pwd;
		private String msg;
		private String user;

		public String getPwd() {
			return pwd;
		}

		public void setPwd(String pwd) {
			this.pwd = pwd;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		//validate login
		public String validateUsernamePassword()  {

			HttpSession session = SessionUtils.getSession();
			Integer failAtempts = (session!=null && session.getAttribute("failAtempts")!=null) ? (Integer)session.getAttribute("failAtempts"):0;
			if (failAtempts<10)
				korisnik = findUser(user, pwd);
			
			if (korisnik!=null && korisnik.getUsername()!=null ) {
				
				updateUspjesanLogin();
				
				session.setAttribute("username", korisnik.getUsername() );
				session.setAttribute("userid", korisnik.getId());
				
				session.removeAttribute("failAtempts");
				
				Log.info("Dana: "+ korisnik.getUspjesnoDatum()+" korisnik "+korisnik.getUsername()  +" uspješno se ulogirao "+ korisnik.getUspjesno() +". puta!");
				
				return "index?faces-redirect=true";
				
			} else {
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Autentikacija nije uspjela!",	"Pokušajte ponovo.."));
				
				Log.info("Korisnik "+ user  +" neuspješno se pokušao logirati s lozinkom "+ pwd);
				
				
				failAtempts=failAtempts+1;
				
				session.setAttribute("failAtempts", failAtempts );
				
				return "login";
			}
		}

		private Korisnici findUser(String user, String pwd) {
			
					Korisnici u;
					try {
						u =  getEm().createQuery("select k from Korisnici k where k.username= :user1 and k.password= :pwd1",Korisnici.class)
							   .setParameter("user1", user)
							   .setParameter("pwd1", HelperFunctions.sha2(pwd) )
							   .getSingleResult();
					} catch (NoResultException e) {

						return null;
					}

					
					return u;
		}
		
		
		
		private void updateUspjesanLogin() {
			
			Date now = new Date();
			korisnik.setUspjesnoDatum(now);
			
			korisnik.setUspjesno(korisnik.getUspjesno()+1);
			
			
			EntityTransaction tx =  getEm().getTransaction();
			tx.begin();
			
			getEm().persist(korisnik);
			
			tx.commit();
			
//			getEm().flush();
			
			
//			try {
//				getEm().createQuery("update korisnici k set k.zadnjiLogin=:d1 where k.id=:uid", Korisnici.class)
//						.setParameter("d1", now)
//						.setParameter("uid", u.getId())
//						.executeUpdate();
//				
//			} catch (Exception e) {
//
//				return null;
//			}
			
		}

		public String logout() {
			
		    Log.info("Dana: "+ (new Date()) +" korisnik "+user +" uspješno se odlogirao!");

		    FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

			return "/login.xhtml?faces-redirect=true";

			
		}

		public Korisnici getKorisnik() {
			return korisnik;
		}

		public void setKorisnik(Korisnici korisnik) {
			this.korisnik = korisnik;
		} 
		
		public EntityManager getEm() {
			if (em == null)
				em = EMF.createEntityManager();
			return em;
		}
	    	
}
