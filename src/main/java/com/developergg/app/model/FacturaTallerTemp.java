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
@Table(name = "temp_facturas_talleres")
public class FacturaTallerTemp {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_factura_taller_temp")
	private Integer id;
	
	private String descripcion;
	private Double costo = 0.0;
	private Integer cantidad = 0;
	private Double precio = 0.0;
	private Double itbis = 0.0;
	private Double subtotal = 0.0;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@OneToOne
	@JoinColumn(name = "id_taller")
	private Taller taller;
	
	@OneToOne
	@JoinColumn(name = "id_taller_detalle")
	private TallerDetalle tallerDetalle;

	@OneToOne
	@JoinColumn(name = "id_comprobanteFiscal")
	private ComprobanteFiscal comprobanteFiscal;
	
	@OneToOne
	@JoinColumn(name = "id_articulo")
	private Articulo articulo;
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;
	
	@OneToOne
	@JoinColumn(name = "id_factura_temp")
	private FacturaTemp facturaTemp;

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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public ComprobanteFiscal getComprobanteFiscal() {
		return comprobanteFiscal;
	}

	public void setComprobanteFiscal(ComprobanteFiscal comprobanteFiscal) {
		this.comprobanteFiscal = comprobanteFiscal;
	}

	public TallerDetalle getTallerDetalle() {
		return tallerDetalle;
	}

	public void setTallerDetalle(TallerDetalle tallerDetalle) {
		this.tallerDetalle = tallerDetalle;
	}

	public Taller getTaller() {
		return taller;
	}

	public void setTaller(Taller taller) {
		this.taller = taller;
	}

	public Almacen getAlmacen() {
		return almacen;
	}

	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}

	public FacturaTemp getFacturaTemp() {
		return facturaTemp;
	}

	public void setFacturaTemp(FacturaTemp facturaTemp) {
		this.facturaTemp = facturaTemp;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	@Override
	public String toString() {
		return "FacturaTallerTemp [id=" + id + ", descripcion=" + descripcion + ", costo=" + costo + ", cantidad="
				+ cantidad + ", precio=" + precio + ", itbis=" + itbis + ", subtotal=" + subtotal + ", usuario="
				+ usuario + ", taller=" + taller + ", tallerDetalle=" + tallerDetalle + ", comprobanteFiscal="
				+ comprobanteFiscal + ", articulo=" + articulo + ", almacen=" + almacen + ", facturaTemp=" + facturaTemp
				+ "]";
	}
}
