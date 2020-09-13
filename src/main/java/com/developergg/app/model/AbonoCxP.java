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
@Table(name = "abono_cxp")
public class AbonoCxP {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_ingreso")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_suplidor")
	private Suplidor suplidor;
	
	@OneToOne
	@JoinColumn(name = "id_compra")
	private Compra compra;

	private Date fecha;
	private String hora;
	
	@Column(name = "total_abono")
	private Double totalAbono = 0.0;

	@Column(name = "total_pagado")
	private Double totalPagado = 0.0;
	
	@Column(name = "total_restante")
	private Double totalRestante = 0.0;
	
	@Column(name = "total_devuelto")
	private Double totalDevuelto = 0.0;
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Almacen getAlmacen() {
		return almacen;
	}

	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Double getTotalAbono() {
		return totalAbono;
	}

	public void setTotalAbono(Double totalAbono) {
		this.totalAbono = totalAbono;
	}

	public Double getTotalPagado() {
		return totalPagado;
	}

	public void setTotalPagado(Double totalPagado) {
		this.totalPagado = totalPagado;
	}

	public Double getTotalRestante() {
		return totalRestante;
	}

	public void setTotalRestante(Double totalRestante) {
		this.totalRestante = totalRestante;
	}

	public Double getTotalDevuelto() {
		return totalDevuelto;
	}

	public void setTotalDevuelto(Double totalDevuelto) {
		this.totalDevuelto = totalDevuelto;
	}

	public Suplidor getSuplidor() {
		return suplidor;
	}

	public void setSuplidor(Suplidor suplidor) {
		this.suplidor = suplidor;
	}

	public Compra getCompra() {
		return compra;
	}

	public void setCompra(Compra compra) {
		this.compra = compra;
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
		return "AbonoCxP [id=" + id + ", suplidor=" + suplidor + ", compra=" + compra + ", fecha=" + fecha + ", hora="
				+ hora + ", totalAbono=" + totalAbono + ", totalPagado=" + totalPagado + ", totalRestante="
				+ totalRestante + ", totalDevuelto=" + totalDevuelto + ", almacen=" + almacen + ", usuario=" + usuario
				+ "]";
	}
}
