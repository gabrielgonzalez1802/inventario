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
@Table(name = "facturas_detalle_talleres")
public class FacturaDetalleTaller {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_factura_detalle_taller")
	private Integer id;
	
	private String descripcion;
	private Double costo;
	private Integer cantidad;
	private Double precio;
	private Integer temp_devolver = 0;
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;
	
	private Double itbis;
	private Double subtotal;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@OneToOne
	@JoinColumn(name = "id_comprobanteFiscal")
	private ComprobanteFiscal comprobanteFiscal;
	
	@OneToOne
	@JoinColumn(name = "id_factura")
	private Factura factura;
	
	@OneToOne
	@JoinColumn(name = "id_articulo")
	private Articulo articulo;
	
	@OneToOne
	@JoinColumn(name = "id_taller_articulo")
	private TallerArticulo tallerArticulo;

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

	public Almacen getAlmacen() {
		return almacen;
	}

	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
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

	public TallerArticulo getTallerArticulo() {
		return tallerArticulo;
	}

	public void setTallerArticulo(TallerArticulo tallerArticulo) {
		this.tallerArticulo = tallerArticulo;
	}

	public Integer getTemp_devolver() {
		return temp_devolver;
	}

	public void setTemp_devolver(Integer temp_devolver) {
		this.temp_devolver = temp_devolver;
	}

	@Override
	public String toString() {
		return "FacturaDetalleTaller [id=" + id + ", descripcion=" + descripcion + ", costo=" + costo + ", cantidad="
				+ cantidad + ", precio=" + precio + ", temp_devolver=" + temp_devolver + ", almacen=" + almacen
				+ ", itbis=" + itbis + ", subtotal=" + subtotal + ", usuario=" + usuario + ", comprobanteFiscal="
				+ comprobanteFiscal + ", factura=" + factura + ", articulo=" + articulo + ", tallerArticulo="
				+ tallerArticulo + "]";
	}
}
