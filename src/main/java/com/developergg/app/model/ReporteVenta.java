package com.developergg.app.model;

public class ReporteVenta {
	
	private Integer id;
	private String factura;
	private String articulo;
	private Integer cantidad;
	private Double costo;
	private Double precio;
	private Double total;
	private String tabla;
	
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
	public Double getCosto() {
		return costo;
	}
	public void setCosto(Double costo) {
		this.costo = costo;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	public String getTabla() {
		return tabla;
	}
	public void setTabla(String tabla) {
		this.tabla = tabla;
	}
	@Override
	public String toString() {
		return "ReporteVenta [id=" + id + ", factura=" + factura + ", articulo=" + articulo + ", cantidad=" + cantidad
				+ ", costo=" + costo + ", precio=" + precio + ", total=" + total + ", tabla=" + tabla + "]";
	}
}
