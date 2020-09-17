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
@Table(name = "facturas_detalle_seriales")
public class FacturaDetalleSerial {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_detalle_factura_serial")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_detalle_factura")
	private FacturaDetalle facturaDetalle;
	
	@OneToOne
	@JoinColumn(name = "id_articulo_serial")
	private ArticuloSerial articuloSerial;
	
	private String serial;
	private Double precio = 0.0;
	private Integer devuelto = 0;
	private Integer tempDevuelto = 0;
	
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
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public Integer getDevuelto() {
		return devuelto;
	}
	public void setDevuelto(Integer devuelto) {
		this.devuelto = devuelto;
	}
	public ArticuloSerial getArticuloSerial() {
		return articuloSerial;
	}
	public void setArticuloSerial(ArticuloSerial articuloSerial) {
		this.articuloSerial = articuloSerial;
	}
	public Integer getTempDevuelto() {
		return tempDevuelto;
	}
	public void setTempDevuelto(Integer tempDevuelto) {
		this.tempDevuelto = tempDevuelto;
	}
	@Override
	public String toString() {
		return "FacturaDetalleSerial [id=" + id + ", facturaDetalle=" + facturaDetalle + ", articuloSerial="
				+ articuloSerial + ", serial=" + serial + ", precio=" + precio + ", devuelto=" + devuelto
				+ ", tempDevuelto=" + tempDevuelto + "]";
	}
}
