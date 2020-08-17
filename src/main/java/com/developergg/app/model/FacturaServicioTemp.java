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
@Table(name = "temp_facturas_servicios")
public class FacturaServicioTemp {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_servicio")
	private Integer id;
	
	private String descripcion;
	private Double costo;
	private Integer cantidad;
	private Double precio;
	private Double itbis;
	private Double subtotal;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario; //usuario que creo el servicio
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;
	
	@OneToOne
	@JoinColumn(name = "id_comprobanteFiscal")
	private ComprobanteFiscal comprobanteFiscal;
	
	private Integer taller = 0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Almacen getAlmacen() {
		return almacen;
	}

	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}

	public Integer getTaller() {
		return taller;
	}

	public void setTaller(Integer taller) {
		this.taller = taller;
	}
	
	public ComprobanteFiscal getComprobanteFiscal() {
		return comprobanteFiscal;
	}

	public void setComprobanteFiscal(ComprobanteFiscal comprobanteFiscal) {
		this.comprobanteFiscal = comprobanteFiscal;
	}

	public Double getItbis() {
		return itbis;
	}

	public void setItbis(Double itbis) {
		this.itbis = itbis;
	}

	public Double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}

	@Override
	public String toString() {
		return "FacturaServicioTemp [id=" + id + ", descripcion=" + descripcion + ", costo=" + costo + ", cantidad="
				+ cantidad + ", precio=" + precio + ", itbis=" + itbis + ", subtotal=" + subtotal + ", usuario="
				+ usuario + ", almacen=" + almacen + ", comprobanteFiscal=" + comprobanteFiscal + ", taller=" + taller
				+ "]";
	}
}
