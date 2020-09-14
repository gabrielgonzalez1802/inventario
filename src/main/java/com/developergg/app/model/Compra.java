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
@Table(name = "compras")
public class Compra {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_compra")
	private Integer id;
	
	private Date fecha;
	private String no_factura;
	
	@OneToOne
	@JoinColumn(name = "id_suplidor")
	private Suplidor suplidor;
	
	@OneToOne
	@JoinColumn(name = "id_condicion_pago")
	private CondicionPago condicion;
	
	@OneToOne
	@JoinColumn(name = "id_comprobante")
	private ComprobanteFiscal comprobanteFiscal;
	
	private String ncf;
	
	private Integer pagada = 0;
	
	private Integer en_proceso = 0;
	
	@OneToOne
	@JoinColumn(name = "id_vendedor")
	private Vendedor vendedor;
	
	private String observacion;
	
	@Column(name = "compra_subtotal")
	private Double subTotal=0.0;
	
	@Column(name = "compra_itbis")
	private Double itBis=0.0;
	
	@Column(name = "compra_total_neto")
	private Double totalNeto=0.0;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;
	
	@Column(name = "total_pagado")
	private Double totalPagado = 0.0;
	
	@Column(name = "total_restante")
	private Double totalRestante = 0.0;
	
	@Column(name = "total_devuelto")
	private Double totalDevuelto = 0.0;
	
	@Column(name = "total_compra")
	private Double totalCompra = 0.0;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getNo_factura() {
		return no_factura;
	}
	public void setNo_factura(String no_factura) {
		this.no_factura = no_factura;
	}
	public Suplidor getSuplidor() {
		return suplidor;
	}
	public void setSuplidor(Suplidor suplidor) {
		this.suplidor = suplidor;
	}
	public ComprobanteFiscal getComprobanteFiscal() {
		return comprobanteFiscal;
	}
	public void setComprobanteFiscal(ComprobanteFiscal comprobanteFiscal) {
		this.comprobanteFiscal = comprobanteFiscal;
	}
	public String getNcf() {
		return ncf;
	}
	public void setNcf(String ncf) {
		this.ncf = ncf;
	}
	public Vendedor getVendedor() {
		return vendedor;
	}
	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public Double getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(Double subTotal) {
		this.subTotal = subTotal;
	}
	public Double getItBis() {
		return itBis;
	}
	public void setItBis(Double itBis) {
		this.itBis = itBis;
	}
	public Double getTotalNeto() {
		return totalNeto;
	}
	public void setTotalNeto(Double totalNeto) {
		this.totalNeto = totalNeto;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Almacen getAlmacen() {
		return almacen;
	}
	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}
	public CondicionPago getCondicion() {
		return condicion;
	}
	public void setCondicion(CondicionPago condicion) {
		this.condicion = condicion;
	}
	public Integer getEn_proceso() {
		return en_proceso;
	}
	public void setEn_proceso(Integer en_proceso) {
		this.en_proceso = en_proceso;
	}
	public Integer getPagada() {
		return pagada;
	}
	public void setPagada(Integer pagada) {
		this.pagada = pagada;
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
	public Double getTotalCompra() {
		return totalCompra;
	}
	public void setTotalCompra(Double totalCompra) {
		this.totalCompra = totalCompra;
	}
	@Override
	public String toString() {
		return "Compra [id=" + id + ", fecha=" + fecha + ", no_factura=" + no_factura + ", suplidor=" + suplidor
				+ ", condicion=" + condicion + ", comprobanteFiscal=" + comprobanteFiscal + ", ncf=" + ncf + ", pagada="
				+ pagada + ", en_proceso=" + en_proceso + ", vendedor=" + vendedor + ", observacion=" + observacion
				+ ", subTotal=" + subTotal + ", itBis=" + itBis + ", totalNeto=" + totalNeto + ", usuario=" + usuario
				+ ", almacen=" + almacen + ", totalPagado=" + totalPagado + ", totalRestante=" + totalRestante
				+ ", totalDevuelto=" + totalDevuelto + ", totalCompra=" + totalCompra + "]";
	}
}
