package dao;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
/**
 * The persistent class for the korisnici database table.
 * 
 */
@Entity
@Table(name="korisnici")
public class Korisnici implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="username")
	private String username;

	@Column(name="password")
	private String password;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="uspjesno_datum")
	private Date uspjesnoDatum;

	@Column(name="uspjesno")
	private int uspjesno;	

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}
	

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getUspjesnoDatum() {
		return uspjesnoDatum;
	}

	public void setUspjesnoDatum(Date uspjesnoDatum) {
		this.uspjesnoDatum = uspjesnoDatum;
	}


	public int getUspjesno() {
		return uspjesno;
	}

	public void setUspjesno(int uspjesno) {
		this.uspjesno = uspjesno;
	}



}