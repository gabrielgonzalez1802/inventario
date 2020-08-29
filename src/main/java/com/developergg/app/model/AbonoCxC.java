package com.developergg.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "abono_cxc")
public class AbonoCxC {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_ingreso")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_factura")
	private Factura factura;
	
	@OneToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	private Integer codigo;
	
	@Column(name = "total_abono")
	private Double totalAbono = 0.0;

	@Column(name = "total_pagado")
	private Double totalPagado = 0.0;
	
	@Column(name = "total_restante")
	private Double totalRestante = 0.0;
	
	@Column(name = "total_devuelto")
	private Double totalDevuelto = 0.0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
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

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
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

	@Override
	public String toString() {
		return "AbonoCxC [id=" + id + ", factura=" + factura + ", cliente=" + cliente + ", almacen=" + almacen
				+ ", usuario=" + usuario + ", codigo=" + codigo + ", totalAbono=" + totalAbono + ", totalPagado="
				+ totalPagado + ", totalRestante=" + totalRestante + ", totalDevuelto=" + totalDevuelto + "]";
	}
}
