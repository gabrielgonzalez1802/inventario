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
@Table(name = "facturas_detalle_servicio")
public class FacturaDetalleServicio {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_detalle_servicio")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_factura")
	private Factura factura;
	
	private String descripcion;
	private Double costo;
	private Integer cantidad;
	private Double precio;
	private Double subtotal;
	private Double itbis;
	
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
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Double getCosto() {
		return costo;
	}
	public void setCosto(Double costo) {
		this.costo = costo;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public Double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}
	public Double getItbis() {
		return itbis;
	}
	public void setItbis(Double itbis) {
		this.itbis = itbis;
	}
	@Override
	public String toString() {
		return "FacturaDetalleServicio [id=" + id + ", factura=" + factura + ", descripcion=" + descripcion + ", costo="
				+ costo + ", cantidad=" + cantidad + ", precio=" + precio + ", subtotal=" + subtotal + ", itbis="
				+ itbis + "]";
	}
}
