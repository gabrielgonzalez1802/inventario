package com.developergg.app.model;

public class CuadreVentaContado{
	
	private Integer id;
	private String fecha;
	private Integer noFactura;
	private String nombreArticulo;
	private Integer cantidad = 0;
	private Double precio = 0.0;
	private Double subTotal = 0.0;
	private String table;
	
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
	public Integer getNoFactura() {
		return noFactura;
	}
	public void setNoFactura(Integer noFactura) {
		this.noFactura = noFactura;
	}
	public String getNombreArticulo() {
		return nombreArticulo;
	}
	public void setNombreArticulo(String nombreArticulo) {
		this.nombreArticulo = nombreArticulo;
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
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	@Override
	public String toString() {
		return "VentaContado [id=" + id + ", fecha=" + fecha + ", noFactura=" + noFactura + ", nombreArticulo="
				+ nombreArticulo + ", cantidad=" + cantidad + ", precio=" + precio + ", subTotal=" + subTotal
				+ ", table=" + table + "]";
	}
}
