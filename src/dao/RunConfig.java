package dao;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the runconfig database table.
 * 
 */
@Entity
@Table(name="runconfig")
@NamedQuery(name="RunConfig.findAll", query="SELECT r FROM RunConfig r")
public class RunConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)

	private int id;

	private String host;

	private String naziv;

	private int port;

	private int sampleperiod;

	private String sampletime;

	private String target;

	private String uri;

	public RunConfig() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getNaziv() {
		return this.naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getSampleperiod() {
		return this.sampleperiod;
	}

	public void setSampleperiod(int sampleperiod) {
		this.sampleperiod = sampleperiod;
	}

	public String getSampletime() {
		return this.sampletime;
	}

	public void setSampletime(String sampletime) {
		this.sampletime = sampletime;
	}

	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getUri() {
		return this.uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	@Override
    public String toString() {
        return this.naziv;
    }

}