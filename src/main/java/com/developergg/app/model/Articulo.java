package com.developergg.app.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "articulos")
public class Articulo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String codigo;
	private String codigo_barras;
	private String nombre;
	//categoria object
	private Double costo;
	private Double precio_maximo;
	private Double precio_minimo;
	private Double precio_mayor;
	private Integer minimo;
	private String imei;
	private String itbis;
	
	public Articulo() {
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getCodigo_barras() {
		return codigo_barras;
	}
	public void setCodigo_barras(String codigo_barras) {
		this.codigo_barras = codigo_barras;
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
	public Double getPrecio_maximo() {
		return precio_maximo;
	}
	public void setPrecio_maximo(Double precio_maximo) {
		this.precio_maximo = precio_maximo;
	}
	public Double getPrecio_minimo() {
		return precio_minimo;
	}
	public void setPrecio_minimo(Double precio_minimo) {
		this.precio_minimo = precio_minimo;
	}
	public Double getPrecio_mayor() {
		return precio_mayor;
	}
	public void setPrecio_mayor(Double precio_mayor) {
		this.precio_mayor = precio_mayor;
	}
	public Integer getMinimo() {
		return minimo;
	}
	public void setMinimo(Integer minimo) {
		this.minimo = minimo;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getItbis() {
		return itbis;
	}
	public void setItbis(String itbis) {
		this.itbis = itbis;
	}

	@Override
	public String toString() {
		return "Articulo [id=" + id + ", codigo=" + codigo + ", codigo_barras=" + codigo_barras + ", nombre=" + nombre
				+ ", costo=" + costo + ", precio_maximo=" + precio_maximo + ", precio_minimo=" + precio_minimo
				+ ", precio_mayor=" + precio_mayor + ", minimo=" + minimo + ", imei=" + imei + ", itbis=" + itbis + "]";
	}
}
