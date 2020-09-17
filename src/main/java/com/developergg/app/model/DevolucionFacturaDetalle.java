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
@Table(name = "devolucion_factura_detalle")
public class DevolucionFacturaDetalle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_devolucion_detalle")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_devolucion")
	private DevolucionFactura devolucionFactura;
	
	@OneToOne
	@JoinColumn(name = "id_factura_detalle")
	private FacturaDetalle facturaDetalle;
	
	@OneToOne
	@JoinColumn(name = "id_factura_detalle_taller")
	private FacturaDetalleTaller facturaDetalleTaller;
	
	private Integer cantidad=0;
	private Integer cantidad_factura=0;
	private Integer cantidad_restante=0;
	
	private Double precio = 0.0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public DevolucionFactura getDevolucionFactura() {
		return devolucionFactura;
	}

	public void setDevolucionFactura(DevolucionFactura devolucionFactura) {
		this.devolucionFactura = devolucionFactura;
	}

	public FacturaDetalle getFacturaDetalle() {
		return facturaDetalle;
	}

	public void setFacturaDetalle(FacturaDetalle facturaDetalle) {
		this.facturaDetalle = facturaDetalle;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public Integer getCantidad_factura() {
		return cantidad_factura;
	}

	public void setCantidad_factura(Integer cantidad_factura) {
		this.cantidad_factura = cantidad_factura;
	}

	public Integer getCantidad_restante() {
		return cantidad_restante;
	}

	public void setCantidad_restante(Integer cantidad_restante) {
		this.cantidad_restante = cantidad_restante;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public FacturaDetalleTaller getFacturaDetalleTaller() {
		return facturaDetalleTaller;
	}

	public void setFacturaDetalleTaller(FacturaDetalleTaller facturaDetalleTaller) {
		this.facturaDetalleTaller = facturaDetalleTaller;
	}

	@Override
	public String toString() {
		return "DevolucionFacturaDetalle [id=" + id + ", devolucionFactura=" + devolucionFactura + ", facturaDetalle="
				+ facturaDetalle + ", facturaDetalleTaller=" + facturaDetalleTaller + ", cantidad=" + cantidad
				+ ", cantidad_factura=" + cantidad_factura + ", cantidad_restante=" + cantidad_restante + ", precio="
				+ precio + "]";
	}
}
