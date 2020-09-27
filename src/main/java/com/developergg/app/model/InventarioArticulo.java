package com.developergg.app.model;

public class InventarioArticulo {
	
	private Integer id;
	private String fecha;
	private Integer cantidad;
	private String procedencia; //inventario compras, ventas o devolución
	private String movimiento;
	private String tabla;
	private String articulo;
	
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
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	public String getProcedencia() {
		return procedencia;
	}
	public void setProcedencia(String procedencia) {
		this.procedencia = procedencia;
	}
	public String getMovimiento() {
		return movimiento;
	}
	public void setMovimiento(String movimiento) {
		this.movimiento = movimiento;
	}
	public String getTabla() {
		return tabla;
	}
	public void setTabla(String tabla) {
		this.tabla = tabla;
	}
	public String getArticulo() {
		return articulo;
	}
	public void setArticulo(String articulo) {
		this.articulo = articulo;
	}
	@Override
	public String toString() {
		return "InventarioArticulo [id=" + id + ", fecha=" + fecha + ", cantidad=" + cantidad + ", procedencia="
				+ procedencia + ", movimiento=" + movimiento + ", tabla=" + tabla + ", articulo=" + articulo + "]";
	}
}
