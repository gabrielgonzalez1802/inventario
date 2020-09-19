package com.developergg.app.model;

public class CuadreAbonoCxC {
	public Integer id;
	public String fecha;
	public String factura;
	public Double monto;
	
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getFactura() {
		return factura;
	}
	public void setFactura(String factura) {
		this.factura = factura;
	}
	public Double getMonto() {
		return monto;
	}
	public void setMonto(Double monto) {
		this.monto = monto;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "CuadreAbonoCxC [id=" + id + ", fecha=" + fecha + ", factura=" + factura + ", monto=" + monto + "]";
	}
}
