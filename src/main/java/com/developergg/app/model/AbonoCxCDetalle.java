package com.developergg.app.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "abono_cxc_detalle")
public class AbonoCxCDetalle {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_ingreso_detalle")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_ingreso")
	private AbonoCxC ingreso;
	
	@OneToOne
	@JoinColumn(name = "forma_pago_id")
	private FormaPago formaPago;
	
	private Date fecha;
	private String hora;
	private Double abono = 0.0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public AbonoCxC getIngreso() {
		return ingreso;
	}

	public void setIngreso(AbonoCxC ingreso) {
		this.ingreso = ingreso;
	}

	public FormaPago getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(FormaPago formaPago) {
		this.formaPago = formaPago;
	}

	public Double getAbono() {
		return abono;
	}

	public void setAbono(Double abono) {
		this.abono = abono;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	@Override
	public String toString() {
		return "AbonoCxCDetalle [id=" + id + ", ingreso=" + ingreso + ", formaPago=" + formaPago + ", fecha=" + fecha
				+ ", hora=" + hora + ", abono=" + abono + "]";
	}
}
