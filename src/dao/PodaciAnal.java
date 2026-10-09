package dao;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the podaci_anal database table.
 * 
 */
@Entity
@Table(name="podaci_anal")
@NamedQuery(name="PodaciAnal.findAll", query="SELECT p FROM PodaciAnal p")
public class PodaciAnal implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Temporal(TemporalType.DATE)
	private Date datum;

	private String grad;

	@Column(name="temp_max")
	private BigDecimal tempMax;

	@Column(name="temp_min")
	private BigDecimal tempMin;

	@Column(name="temp_sred")
	private BigDecimal tempSred;

	public PodaciAnal() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDatum() {
		return this.datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public String getGrad() {
		return this.grad;
	}

	public void setGrad(String grad) {
		this.grad = grad;
	}

	public BigDecimal getTempMax() {
		return this.tempMax;
	}

	public void setTempMax(BigDecimal tempMax) {
		this.tempMax = tempMax;
	}

	public BigDecimal getTempMin() {
		return this.tempMin;
	}

	public void setTempMin(BigDecimal tempMin) {
		this.tempMin = tempMin;
	}

	public BigDecimal getTempSred() {
		return this.tempSred;
	}

	public void setTempSred(BigDecimal tempSred) {
		this.tempSred = tempSred;
	}

}