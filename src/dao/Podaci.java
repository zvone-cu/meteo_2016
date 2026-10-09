package dao;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * The persistent class for the podaci database table.
 * 
 */
@Entity
@Table(name="podaci")
@NamedQuery(name="Podaci.findAll", query="SELECT p FROM Podaci p")
public class Podaci implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) //kolona mora imati AUTO_INCREMENT 
	private int id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date datum;
	
	private int termin;
	
	@Column(name = "autom", columnDefinition="BIT")
	private boolean autom;

	private String grad;

	private BigDecimal temp;

	private BigDecimal tlak;

	@Column(name="tlak_tend")
	private String tlakTend;

	@Column(name="vjetar_brzina")
	private BigDecimal vjetarBrzina;

	@Column(name="vjetar_smjer")
	private String vjetarSmjer;

	private Integer vlaga;

	private String vrijeme;

	@Column(name="vrijeme_znak")
	private String vrijemeZnak;


	public Podaci() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean getAutom() {
		return this.autom;
	}

	public void setAutom(boolean autom) {
		this.autom = autom;
	}

	public String getGrad() {
		return this.grad;
	}

	public void setGrad(String grad) {
		this.grad = grad;
	}

	public BigDecimal getTemp() {
		return this.temp;
	}

	public void setTemp(BigDecimal temp) {
		this.temp = temp;
	}

	public BigDecimal getTlak() {
		return this.tlak;
	}

	public void setTlak(BigDecimal tlak) {
		this.tlak = tlak;
	}

	public String getTlakTend() {
		return this.tlakTend;
	}

	public void setTlakTend(String tlakTend) {
		this.tlakTend = tlakTend;
	}

	public BigDecimal getVjetarBrzina() {
		return this.vjetarBrzina;
	}

	public void setVjetarBrzina(BigDecimal vjetarBrzina) {
		this.vjetarBrzina = vjetarBrzina;
	}

	public String getVjetarSmjer() {
		return this.vjetarSmjer;
	}

	public void setVjetarSmjer(String vjetarSmjer) {
		this.vjetarSmjer = vjetarSmjer;
	}

	public Integer getVlaga() {
		return this.vlaga;
	}

	public void setVlaga(Integer vlaga) {
		this.vlaga = vlaga;
	}

	public String getVrijeme() {
		return this.vrijeme;
	}

	public void setVrijeme(String vrijeme) {
		this.vrijeme = vrijeme;
	}

	public String getVrijemeZnak() {
		return this.vrijemeZnak;
	}

	public void setVrijemeZnak(String vrijemeZnak) {
		this.vrijemeZnak = vrijemeZnak;
	}


	public Date getDatum() {
		return this.datum;
	}
	
	@Transient
	  public  String getDatumJulian () {

	    int year = this.datum.getYear();
	    int month = this.datum.getMonth()+1;
	    int day = this.datum.getDay();
	    int hour = this.datum.getHours(); 
	    int minute = this.datum.getMinutes();
	    int second = this.datum.getSeconds();

	    double extra = (100.0 * year) + month - 190002.5;
	    return String.format("%.2f", (367.0 * year) -
	      (Math.floor(7.0 * (year + Math.floor((month + 9.0) / 12.0)) / 4.0)) + 
	      Math.floor((275.0 * month) / 9.0) +  
	      day + ((hour + ((minute + (second / 60.0)) / 60.0)) / 24.0) +
	      1721013.5 - ((0.5 * extra) / Math.abs(extra)) + 0.5).replace(',',  '.');
	  }
	
	@Transient
	public String getDatumString() {
		
		SimpleDateFormat f= new SimpleDateFormat("yyyy-MM-dd HH:mm");
		//SimpleDateFormat f= new SimpleDateFormat("www EEE");
		return f.format(this.datum); 
	}
	@Transient
	public String getDatumStringDate() {
		
		SimpleDateFormat f= new SimpleDateFormat("yyyy-MM-dd");
		//SimpleDateFormat f= new SimpleDateFormat("www EEE");
		return f.format(this.datum); 
	}
	
	
	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public int getTermin() {
		return termin;
	}

	public void setTermin(int termin) {
		this.termin = termin;
	}
	
	
	@Transient
	public Integer getVjetarSmjerDeg() {
		
		if (vjetarSmjer==null ) return null;
		else if (vjetarSmjer.equals("N")) return 0;
		else if (vjetarSmjer.equals("NE")) return 45;
		else if (vjetarSmjer.equals("E")) return 90;
		else if (vjetarSmjer.equals("SE")) return 135;
		else if (vjetarSmjer.equals("S")) return 180;
		else if (vjetarSmjer.equals("SW")) return 225;
		else if (vjetarSmjer.equals("W")) return 270;
		else if (vjetarSmjer.equals("NW")) return 315;
		else return null;
		
	}

}