package com.developergg.app.model;

public class DetalleFactura {
	private Integer id;
	private String codigo;
	private String originalTable;
	private Integer cantidad;
	private String descripcion;
	private Double precio;
	private Double itbis;
	private Double subtotal;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getOriginalTable() {
		return originalTable;
	}
	public void setOriginalTable(String originalTable) {
		this.originalTable = originalTable;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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
	public Double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	@Override
	public String toString() {
		return "DetalleFactura [id=" + id + ", codigo=" + codigo + ", originalTable=" + originalTable + ", cantidad="
				+ cantidad + ", descripcion=" + descripcion + ", precio=" + precio + ", itbis=" + itbis + ", subtotal="
				+ subtotal + "]";
	}
}
