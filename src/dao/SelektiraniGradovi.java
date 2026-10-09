package dao;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Entity(name="SelektiraniGradovi" )
@Table(name="selektirani_gradovi")
@NamedQuery(name="SelektiraniGradovi.findAll", query="SELECT s FROM SelektiraniGradovi s")
public class SelektiraniGradovi implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="grad")
	private String grad;

	public SelektiraniGradovi() {
	}
	public SelektiraniGradovi(String grad) {
		this.grad=grad;
	}


	public String getGrad() {
		return this.grad;
	}

	public void setGrad(String g) {
		this.grad = g;
	}
}