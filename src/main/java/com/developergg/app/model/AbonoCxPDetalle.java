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
@Table(name = "abono_cxp_detalle")
public class AbonoCxPDetalle {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_ingreso_detalle")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_ingreso")
	private AbonoCxP ingreso;

	private Date fecha;
	private String hora;
	private Double abono = 0.0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public AbonoCxP getIngreso() {
		return ingreso;
	}

	public void setIngreso(AbonoCxP ingreso) {
		this.ingreso = ingreso;
	}
}
