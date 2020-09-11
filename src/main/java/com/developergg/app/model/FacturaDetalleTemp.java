package com.developergg.app.model;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "temp_facturas_detalles")
public class FacturaDetalleTemp {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_factura_detalle")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_articulo")
	private Articulo articulo;
	
	private Integer cantidad;
	private Double precio;
	private Double itbis;
	
	private String imei = "NO";
	
	@Column(name = "paga_itbis")
	private String conItbis;
	
	private Integer existencia;
	private Double subtotal;
	private Double precio_maximo;
	
	private Double initialPrice;
	
	@OneToOne
	@JoinColumn(name = "id_comprobanteFiscal")
	private ComprobanteFiscal comprobanteFiscal;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;
	
	private Integer taller = 0;
	
	@Transient
	private List<FacturaSerialTemp> seriales;
	
	public void agregar(FacturaSerialTemp tempSerial) {
		if (seriales == null) {
			seriales = new LinkedList<FacturaSerialTemp>();
		}
		seriales.add(tempSerial);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getConItbis() {
		return conItbis;
	}

	public void setConItbis(String conItbis) {
		this.conItbis = conItbis;
	}

	public Integer getExistencia() {
		return existencia;
	}

	public void setExistencia(Integer existencia) {
		this.existencia = existencia;
	}

	public Double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}

	public Double getPrecio_maximo() {
		return precio_maximo;
	}

	public void setPrecio_maximo(Double precio_maximo) {
		this.precio_maximo = precio_maximo;
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

	public List<FacturaSerialTemp> getSeriales() {
		return seriales;
	}

	public void setSeriales(List<FacturaSerialTemp> seriales) {
		this.seriales = seriales;
	}
	
	public ComprobanteFiscal getComprobanteFiscal() {
		return comprobanteFiscal;
	}

	public void setComprobanteFiscal(ComprobanteFiscal comprobanteFiscal) {
		this.comprobanteFiscal = comprobanteFiscal;
	}

	public Double getInitialPrice() {
		return initialPrice;
	}

	public void setInitialPrice(Double initialPrice) {
		this.initialPrice = initialPrice;
	}

	@Override
	public String toString() {
		return "FacturaDetalleTemp [id=" + id + ", articulo=" + articulo + ", cantidad=" + cantidad + ", precio="
				+ precio + ", itbis=" + itbis + ", imei=" + imei + ", conItbis=" + conItbis + ", existencia="
				+ existencia + ", subtotal=" + subtotal + ", precio_maximo=" + precio_maximo + ", initialPrice="
				+ initialPrice + ", comprobanteFiscal=" + comprobanteFiscal + ", usuario=" + usuario + ", almacen="
				+ almacen + ", taller=" + taller + ", seriales=" + seriales + "]";
	}
}
