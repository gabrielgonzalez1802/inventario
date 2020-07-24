package com.developergg.app.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ofertas")
public class Oferta {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_oferta")
	private Integer id;
	
	private String nombre;
	
	@Column(name = "fecha_creado")
	private Date creado;
	
	@Column(name = "fecha_vencimiento")
	private Date vencimiento;
	
	private int eliminado;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Date getCreado() {
		return creado;
	}

	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public Date getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}

	public int getEliminado() {
		return eliminado;
	}

	public void setEliminado(int eliminado) {
		this.eliminado = eliminado;
	}

	@Override
	public String toString() {
		return "Oferta [id=" + id + ", nombre=" + nombre + ", creado=" + creado + ", vencimiento=" + vencimiento
				+ ", eliminado=" + eliminado + "]";
	}
}
