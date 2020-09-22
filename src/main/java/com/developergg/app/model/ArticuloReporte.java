package com.developergg.app.model;

public class ArticuloReporte {
	
	private Integer idArticulo;
	private String codigo;
	private String nombre;
	private Double costo = 0.0;
	private String categoria;
	private Double precio = 0.0;
	private String conImei;
	private Integer disponible = 0;
	private Integer vercosto = 0;
	
	public Integer getIdArticulo() {
		return idArticulo;
	}
	public void setIdArticulo(Integer idArticulo) {
		this.idArticulo = idArticulo;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Double getCosto() {
		return costo;
	}
	public void setCosto(Double costo) {
		this.costo = costo;
	}
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public String getConImei() {
		return conImei;
	}
	public void setConImei(String conImei) {
		this.conImei = conImei;
	}
	public Integer getDisponible() {
		return disponible;
	}
	public void setDisponible(Integer disponible) {
		this.disponible = disponible;
	}
	public Integer getVercosto() {
		return vercosto;
	}
	public void setVercosto(Integer vercosto) {
		this.vercosto = vercosto;
	}
	@Override
	public String toString() {
		return "ArticuloReporte [idArticulo=" + idArticulo + ", codigo=" + codigo + ", nombre=" + nombre + ", costo="
				+ costo + ", categoria=" + categoria + ", precio=" + precio + ", conImei=" + conImei + ", disponible="
				+ disponible + ", vercosto=" + vercosto + "]";
	}
	
}
