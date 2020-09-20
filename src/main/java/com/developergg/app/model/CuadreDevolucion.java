package com.developergg.app.model;

public class CuadreDevolucion {
	
	private Integer id;
	private String fecha;
	private String factura;
	private String articulo;
	private Integer cantidad;
	private Double precio;
	private Double subTotal;
	private String tabla;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
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
	public String getArticulo() {
		return articulo;
	}
	public void setArticulo(String articulo) {
		this.articulo = articulo;
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
	public Double getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(Double subTotal) {
		this.subTotal = subTotal;
	}
	public String getTabla() {
		return tabla;
	}
	public void setTabla(String tabla) {
		this.tabla = tabla;
	}
	@Override
	public String toString() {
		return "CuadreDevolucion [id=" + id + ", fecha=" + fecha + ", factura=" + factura + ", articulo=" + articulo
				+ ", cantidad=" + cantidad + ", precio=" + precio + ", subTotal=" + subTotal + ", tabla=" + tabla + "]";
	}
}
