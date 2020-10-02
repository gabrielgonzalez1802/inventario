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
@Table(name = "compras_detalle_serial")
public class CompraDetalleSerial {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_detalle_serial")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_compra")
	private Compra compra;
	
	@OneToOne
	@JoinColumn(name = "id_detalle_compra")
	private CompraDetalle compraDetalle;
	
	@OneToOne
	@JoinColumn(name = "id_articulo")
	private Articulo articulo;
	
	private String serial;
	
	private Double costo;
	private Double precio_maximo;
	private Double precio_minimo;
	private Double precio_mayor;
	
	private Integer devuelto = 0;
	private Integer tempDevuelto = 0;

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

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

	public CompraDetalle getCompraDetalle() {
		return compraDetalle;
	}

	public void setCompraDetalle(CompraDetalle compraDetalle) {
		this.compraDetalle = compraDetalle;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
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

	public Integer getTempDevuelto() {
		return tempDevuelto;
	}

	public void setTempDevuelto(Integer tempDevuelto) {
		this.tempDevuelto = tempDevuelto;
	}

	public Integer getDevuelto() {
		return devuelto;
	}

	public void setDevuelto(Integer devuelto) {
		this.devuelto = devuelto;
	}

	@Override
	public String toString() {
		return "CompraDetalleSerial [id=" + id + ", compra=" + compra + ", compraDetalle=" + compraDetalle
				+ ", articulo=" + articulo + ", serial=" + serial + ", costo=" + costo + ", precio_maximo="
				+ precio_maximo + ", precio_minimo=" + precio_minimo + ", precio_mayor=" + precio_mayor + ", devuelto="
				+ devuelto + ", tempDevuelto=" + tempDevuelto + "]";
	}
}
