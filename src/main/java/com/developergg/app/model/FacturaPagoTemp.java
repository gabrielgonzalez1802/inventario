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
@Table(name = "facturas_pago_temp")
public class FacturaPagoTemp {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_forma_pago_temp")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_factura")
	private Factura factura;
	
	@OneToOne
	@JoinColumn(name = "forma_pago")
	private FormaPago formaPago;
	
	private Double cantidad;
	private Double aplicado;
	
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
	public FormaPago getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(FormaPago formaPago) {
		this.formaPago = formaPago;
	}
	public Double getCantidad() {
		return cantidad;
	}
	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}
	public Double getAplicado() {
		return aplicado;
	}
	public void setAplicado(Double aplicado) {
		this.aplicado = aplicado;
	}
	@Override
	public String toString() {
		return "FacturaPago [id=" + id + ", factura=" + factura + ", formaPago=" + formaPago + ", cantidad=" + cantidad
				+ ", aplicado=" + aplicado + "]";
	}
}
