package dao;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the prosjeci database table.
 * 
 */
@Entity
@Table(name="prosjeci")
@NamedQuery(name="Prosjeci.findAll", query="SELECT p FROM Prosjeci p")
public class Prosjeci implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)

	private int id;

	private String grad;

	private int mjesec;

	private int padaline;

	@Column(name="temp_max")
	private BigDecimal tempMax;

	@Column(name="temp_min")
	private BigDecimal tempMin;

	@Column(name="temp_sred")
	private BigDecimal tempSred;

	public Prosjeci() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGrad() {
		return this.grad;
	}

	public void setGrad(String grad) {
		this.grad = grad;
	}

	public int getMjesec() {
		return this.mjesec;
	}

	public void setMjesec(int mjesec) {
		this.mjesec = mjesec;
	}

	public int getPadaline() {
		return this.padaline;
	}

	public void setPadaline(int padaline) {
		this.padaline = padaline;
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