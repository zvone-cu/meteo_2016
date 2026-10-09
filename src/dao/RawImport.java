package dao;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the raw_import database table.
 * 
 */
@Entity
@Table(name="raw_import")
@NamedQuery(name="RawImport.findAll", query="SELECT r FROM RawImport r")
public class RawImport implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String conditions;

	private String dateUTC;

	private String events;

	private String padaline;

	private BigDecimal rosiste;

	private BigDecimal temp;

	private String timeCEST;

	private String tlak;

	private String udari;

	private String vidljivost;

	@Column(name="vjetar_brzina")
	private String vjetarBrzina;

	@Column(name="vjetar_smjer")
	private String vjetarSmjer;

	private int vlaga;

	private String windDirDegrees;

	public RawImport() {
	}

	public String getConditions() {
		return this.conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public String getDateUTC() {
		return this.dateUTC;
	}

	public void setDateUTC(String dateUTC) {
		this.dateUTC = dateUTC;
	}

	public String getEvents() {
		return this.events;
	}

	public void setEvents(String events) {
		this.events = events;
	}

	public String getPadaline() {
		return this.padaline;
	}

	public void setPadaline(String padaline) {
		this.padaline = padaline;
	}

	public BigDecimal getRosiste() {
		return this.rosiste;
	}

	public void setRosiste(BigDecimal rosiste) {
		this.rosiste = rosiste;
	}

	public BigDecimal getTemp() {
		return this.temp;
	}

	public void setTemp(BigDecimal temp) {
		this.temp = temp;
	}

	public String getTimeCEST() {
		return this.timeCEST;
	}

	public void setTimeCEST(String timeCEST) {
		this.timeCEST = timeCEST;
	}

	public String getTlak() {
		return this.tlak;
	}

	public void setTlak(String tlak) {
		this.tlak = tlak;
	}

	public String getUdari() {
		return this.udari;
	}

	public void setUdari(String udari) {
		this.udari = udari;
	}

	public String getVidljivost() {
		return this.vidljivost;
	}

	public void setVidljivost(String vidljivost) {
		this.vidljivost = vidljivost;
	}

	public String getVjetarBrzina() {
		return this.vjetarBrzina;
	}

	public void setVjetarBrzina(String vjetarBrzina) {
		this.vjetarBrzina = vjetarBrzina;
	}

	public String getVjetarSmjer() {
		return this.vjetarSmjer;
	}

	public void setVjetarSmjer(String vjetarSmjer) {
		this.vjetarSmjer = vjetarSmjer;
	}

	public int getVlaga() {
		return this.vlaga;
	}

	public void setVlaga(int vlaga) {
		this.vlaga = vlaga;
	}

	public String getWindDirDegrees() {
		return this.windDirDegrees;
	}

	public void setWindDirDegrees(String windDirDegrees) {
		this.windDirDegrees = windDirDegrees;
	}
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

}