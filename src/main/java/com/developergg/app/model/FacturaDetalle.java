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
@Table(name = "facturas_detalle")
public class FacturaDetalle {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_factura_detalle")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_factura")
	private Factura factura;
	
	@OneToOne
	@JoinColumn(name = "id_articulo")
	private Articulo articulo;
	
	private Integer cantidad;
	private Integer cantidad_devuelta = 0;
	private Double precio;
	private Double itbis;
	private String imei;
	private String paga_itbis;
	private Integer existencia;
	private Double precio_maximo;
	private Double precio_minimo;
	private Double precio_mayor;
	private Double costo;
	private Double subtotal;
	
	private Integer temp_devolver = 0;
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
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

	public Integer getCantidad_devuelta() {
		return cantidad_devuelta;
	}

	public void setCantidad_devuelta(Integer cantidad_devuelta) {
		this.cantidad_devuelta = cantidad_devuelta;
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

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getPaga_itbis() {
		return paga_itbis;
	}

	public void setPaga_itbis(String paga_itbis) {
		this.paga_itbis = paga_itbis;
	}

	public Integer getExistencia() {
		return existencia;
	}

	public void setExistencia(Integer existencia) {
		this.existencia = existencia;
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

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}

	public Double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}

	public Integer getTemp_devolver() {
		return temp_devolver;
	}

	public void setTemp_devolver(Integer temp_devolver) {
		this.temp_devolver = temp_devolver;
	}

	public Almacen getAlmacen() {
		return almacen;
	}

	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}

	@Override
	public String toString() {
		return "FacturaDetalle [id=" + id + ", factura=" + factura + ", articulo=" + articulo + ", cantidad=" + cantidad
				+ ", cantidad_devuelta=" + cantidad_devuelta + ", precio=" + precio + ", itbis=" + itbis + ", imei="
				+ imei + ", paga_itbis=" + paga_itbis + ", existencia=" + existencia + ", precio_maximo="
				+ precio_maximo + ", precio_minimo=" + precio_minimo + ", precio_mayor=" + precio_mayor + ", costo="
				+ costo + ", subtotal=" + subtotal + ", temp_devolver=" + temp_devolver + ", almacen=" + almacen + "]";
	}
}
