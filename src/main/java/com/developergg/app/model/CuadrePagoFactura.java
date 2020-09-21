package com.developergg.app.model;

public class CuadrePagoFactura {
	
	private Integer id;
	private String fecha;
	private String factura;
	private String formaPago;
	private Double cantidad;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFactura() {
		return factura;
	}
	public void setFactura(String factura) {
		this.factura = factura;
	}
	public String getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}
	public Double getCantidad() {
		return cantidad;
	}
	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	@Override
	public String toString() {
		return "CuadrePagoFactura [id=" + id + ", fecha=" + fecha + ", factura=" + factura + ", formaPago=" + formaPago
				+ ", cantidad=" + cantidad + "]";
	}
}
