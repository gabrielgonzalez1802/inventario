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
@Table(name = "devolucion_compra_serial")
public class DevolucionCompraSerial {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_devolucion_serial_detalle")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_devolucion_compra")
	private DevolucionCompra devolucionCompra;
	
	@OneToOne
	@JoinColumn(name = "id_detalle_compra")
	private CompraDetalle compraDetalle;
	
	@OneToOne
	@JoinColumn(name = "id_articulo_serial")
	private ArticuloSerial articuloSerial;
	
	private String nombreArticulo;
	
	private Double precio = 0.0;
	private Double itbis = 0.0;
	private Double subTotal = 0.0;
	
	private String serial;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public DevolucionCompra getDevolucionCompra() {
		return devolucionCompra;
	}

	public void setDevolucionCompra(DevolucionCompra devolucionCompra) {
		this.devolucionCompra = devolucionCompra;
	}

	public CompraDetalle getCompraDetalle() {
		return compraDetalle;
	}

	public void setCompraDetalle(CompraDetalle compraDetalle) {
		this.compraDetalle = compraDetalle;
	}

	public ArticuloSerial getArticuloSerial() {
		return articuloSerial;
	}

	public void setArticuloSerial(ArticuloSerial articuloSerial) {
		this.articuloSerial = articuloSerial;
	}

	public String getNombreArticulo() {
		return nombreArticulo;
	}

	public void setNombreArticulo(String nombreArticulo) {
		this.nombreArticulo = nombreArticulo;
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

	public Double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(Double subTotal) {
		this.subTotal = subTotal;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	@Override
	public String toString() {
		return "DevolucionCompraSerial [id=" + id + ", devolucionCompra=" + devolucionCompra + ", compraDetalle="
				+ compraDetalle + ", articuloSerial=" + articuloSerial + ", nombreArticulo=" + nombreArticulo
				+ ", precio=" + precio + ", itbis=" + itbis + ", subTotal=" + subTotal + ", serial=" + serial + "]";
	}
}
