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
@Table(name = "devolucion_compra_detalle")
public class DevolucionCompraDetalle {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_devolucion_detalle")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_devolucion_compra")
	private DevolucionCompra devolucionCompra;
	
	@OneToOne
	@JoinColumn(name = "id_compra_detalle")
	private CompraDetalle compraDetalle;
	
	private Integer cantidad =0;
	
	@Column(name = "cantidad_compra")
	private Integer cantidadCompra = 0;
	
	@Column(name = "cantidad_restante")
	private Integer cantidadRestante = 0;
	
	private Double precio = 0.0;
	private Double itbis = 0.0;
	private Double subTotal = 0.0;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public DevolucionCompra getDevolucionCompra() {
		return devolucionCompra;
	}
	public void setDevolucionCompra(DevolucionCompra devolucionCompra) {
		this.devolucionCompra = devolucionCompra;
	}
	public CompraDetalle getCompraDetalle() {
		return compraDetalle;
	}
	public void setCompraDetalle(CompraDetalle compraDetalle) {
		this.compraDetalle = compraDetalle;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	public Integer getCantidadCompra() {
		return cantidadCompra;
	}
	public void setCantidadCompra(Integer cantidadCompra) {
		this.cantidadCompra = cantidadCompra;
	}
	public Integer getCantidadRestante() {
		return cantidadRestante;
	}
	public void setCantidadRestante(Integer cantidadRestante) {
		this.cantidadRestante = cantidadRestante;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public Double getItbis() {
		return itbis;
	}
	public void setItbis(Double itbis) {
		this.itbis = itbis;
	}
	public Double getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(Double subTotal) {
		this.subTotal = subTotal;
	}
}
