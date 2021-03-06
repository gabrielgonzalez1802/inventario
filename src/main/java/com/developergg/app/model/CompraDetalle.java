package com.developergg.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "compras_detalle")
public class CompraDetalle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_detalle")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_compra")
	private Compra compra;
	
	@OneToOne
	@JoinColumn(name = "id_articulo")
	private Articulo articulo;
	
	private Integer cantidad = 0;
	private Double costo = 0.0;
	private Double itbis = 0.0;
	
	@Column(name = "sub_total")
	private Double subTotal = 0.0;
	
	private Integer con_imei = 0;
	
	private Double precio_maximo = 0.0;
	private Double precio_minimo = 0.0;
	private Double precio_mayor = 0.0;
	
	private Integer cantidad_devuelta = 0;
	private Integer temp_devolver = 0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Compra getCompra() {
		return compra;
	}

	public void setCompra(Compra compra) {
		this.compra = compra;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
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

	public Double getItbis() {
		return itbis;
	}

	public void setItbis(Double itbis) {
		this.itbis = itbis;
	}

	public Double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(Double subTotal) {
		this.subTotal = subTotal;
	}

	public Integer getCon_imei() {
		return con_imei;
	}

	public void setCon_imei(Integer con_imei) {
		this.con_imei = con_imei;
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

	public Integer getTemp_devolver() {
		return temp_devolver;
	}

	public void setTemp_devolver(Integer temp_devolver) {
		this.temp_devolver = temp_devolver;
	}

	public Integer getCantidad_devuelta() {
		return cantidad_devuelta;
	}

	public void setCantidad_devuelta(Integer cantidad_devuelta) {
		this.cantidad_devuelta = cantidad_devuelta;
	}

	@Override
	public String toString() {
		return "CompraDetalle [id=" + id + ", compra=" + compra + ", articulo=" + articulo + ", cantidad=" + cantidad
				+ ", costo=" + costo + ", itbis=" + itbis + ", subTotal=" + subTotal + ", con_imei=" + con_imei
				+ ", precio_maximo=" + precio_maximo + ", precio_minimo=" + precio_minimo + ", precio_mayor="
				+ precio_mayor + ", cantidad_devuelta=" + cantidad_devuelta + ", temp_devolver=" + temp_devolver + "]";
	}
}
