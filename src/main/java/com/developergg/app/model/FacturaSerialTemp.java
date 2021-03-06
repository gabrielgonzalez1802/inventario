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
@Table(name = "temp_facturas_serial")
public class FacturaSerialTemp {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_detalle_serial")
	private Integer id;
	
	private Integer id_serial; //preguntar. esto es el serial

	@OneToOne
	@JoinColumn(name = "id_factura_temp")
	private FacturaTemp facturaTemp;
	
	@OneToOne
	@JoinColumn(name = "id_detalle_factura")
	private FacturaDetalleTemp idDetalle;
	
	@OneToOne
	@JoinColumn(name = "id_articulo_serial")
	private ArticuloSerial articuloSerial;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId_serial() {
		return id_serial;
	}

	public void setId_serial(Integer id_serial) {
		this.id_serial = id_serial;
	}

	public FacturaDetalleTemp getIdDetalle() {
		return idDetalle;
	}

	public void setIdDetalle(FacturaDetalleTemp idDetalle) {
		this.idDetalle = idDetalle;
	}
	
	public FacturaTemp getFacturaTemp() {
		return facturaTemp;
	}

	public void setFacturaTemp(FacturaTemp facturaTemp) {
		this.facturaTemp = facturaTemp;
	}

	public ArticuloSerial getArticuloSerial() {
		return articuloSerial;
	}

	public void setArticuloSerial(ArticuloSerial articuloSerial) {
		this.articuloSerial = articuloSerial;
	}

	@Override
	public String toString() {
		return "FacturaSerialTemp [id=" + id + ", id_serial=" + id_serial + ", facturaTemp=" + facturaTemp
				+ ", idDetalle=" + idDetalle + ", articuloSerial=" + articuloSerial + "]";
	}
}
