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
@Table(name = "devolucion_factura_serial")
public class DevolucionFacturaSerial {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_devolucion_serial_detalle")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_detalle_factura")
	private FacturaDetalle facturaDetalle;
	
	@OneToOne
	@JoinColumn(name = "id_detalle_factura_serial")
	private FacturaDetalleSerial facturaDetalleSerial;
	
	@OneToOne
	@JoinColumn(name = "id_articulo_serial")
	private ArticuloSerial articuloSerial;
	
	@OneToOne
	@JoinColumn(name = "id_devolucion_factura")
	private DevolucionFactura devolucionFactura;
	
	private String serial;
	private Double precio = 0.0;
	private Double itbis = 0.0;
	private Double subTotal = 0.0;
	
	private String nombreArticulo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public FacturaDetalle getFacturaDetalle() {
		return facturaDetalle;
	}

	public void setFacturaDetalle(FacturaDetalle facturaDetalle) {
		this.facturaDetalle = facturaDetalle;
	}

	public FacturaDetalleSerial getFacturaDetalleSerial() {
		return facturaDetalleSerial;
	}

	public void setFacturaDetalleSerial(FacturaDetalleSerial facturaDetalleSerial) {
		this.facturaDetalleSerial = facturaDetalleSerial;
	}

	public ArticuloSerial getArticuloSerial() {
		return articuloSerial;
	}

	public void setArticuloSerial(ArticuloSerial articuloSerial) {
		this.articuloSerial = articuloSerial;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public DevolucionFactura getDevolucionFactura() {
		return devolucionFactura;
	}

	public void setDevolucionFactura(DevolucionFactura devolucionFactura) {
		this.devolucionFactura = devolucionFactura;
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

	public String getNombreArticulo() {
		return nombreArticulo;
	}

	public void setNombreArticulo(String nombreArticulo) {
		this.nombreArticulo = nombreArticulo;
	}

	@Override
	public String toString() {
		return "DevolucionFacturaSerial [id=" + id + ", facturaDetalle=" + facturaDetalle + ", facturaDetalleSerial="
				+ facturaDetalleSerial + ", articuloSerial=" + articuloSerial + ", devolucionFactura="
				+ devolucionFactura + ", serial=" + serial + ", precio=" + precio + ", itbis=" + itbis + ", subTotal="
				+ subTotal + ", nombreArticulo=" + nombreArticulo + "]";
	}
}
