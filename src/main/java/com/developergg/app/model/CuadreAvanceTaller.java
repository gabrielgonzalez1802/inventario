package com.developergg.app.model;

public class CuadreAvanceTaller {
	private Integer id;
	private String fecha;
	private String tipo;
	private String marca;
	private String Modelo;
	private Double monto;
	
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
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public String getModelo() {
		return Modelo;
	}
	public void setModelo(String modelo) {
		Modelo = modelo;
	}
	public Double getMonto() {
		return monto;
	}
	public void setMonto(Double monto) {
		this.monto = monto;
	}
	
	@Override
	public String toString() {
		return "CuadreAvanceTaller [id=" + id + ", fecha=" + fecha + ", tipo=" + tipo + ", marca=" + marca + ", Modelo="
				+ Modelo + ", monto=" + monto + "]";
	}
}
