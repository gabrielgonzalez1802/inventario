package com.developergg.app.model;

public class CuadrePagoFactura {
	
	private String formaPago;
	private Double cantidad;
	
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
	@Override
	public String toString() {
		return "CuadrePagoFactura [formaPago=" + formaPago + ", cantidad=" + cantidad + "]";
	}
}
