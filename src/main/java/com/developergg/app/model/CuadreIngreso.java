package com.developergg.app.model;

public class CuadreIngreso {
	private Integer id;
	private String nombre;
	private String tipo;
	private String fecha;
	private Double monto;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public Double getMonto() {
		return monto;
	}
	public void setMonto(Double monto) {
		this.monto = monto;
	}
	@Override
	public String toString() {
		return "CuadreIngreso [id=" + id + ", nombre=" + nombre + ", tipo=" + tipo + ", fecha=" + fecha + ", monto="
				+ monto + "]";
	}
}
