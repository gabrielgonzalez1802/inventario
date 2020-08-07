package com.developergg.app.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "articulos_ajuste")
public class ArticuloAjuste {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_ajuste")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_articulo")
	private Articulo articulo;
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;
	
	@OneToOne
	@JoinColumn(name = "id_suplidor")
	private Suplidor suplidor;
	
	private Integer existencia;
	
	@Column(name = "existencia_disponible")
	private Integer disponible;
	
	private String no_factura;
	private Integer cantidad;
	
	private String serial;
	
	@Column(name = "tipo_movimiento")
	private String tipoMovimiento;
	
	private Double costo;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	private Date fecha;

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

	public Almacen getAlmacen() {
		return almacen;
	}

	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}

	public Suplidor getSuplidor() {
		return suplidor;
	}

	public void setSuplidor(Suplidor suplidor) {
		this.suplidor = suplidor;
	}

	public String getNo_factura() {
		return no_factura;
	}

	public void setNo_factura(String no_factura) {
		this.no_factura = no_factura;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(String tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Integer getExistencia() {
		return existencia;
	}

	public void setExistencia(Integer existencia) {
		this.existencia = existencia;
	}

	public Integer getDisponible() {
		return disponible;
	}

	public void setDisponible(Integer disponible) {
		this.disponible = disponible;
	}

	@Override
	public String toString() {
		return "ArticuloAjuste [id=" + id + ", articulo=" + articulo + ", almacen=" + almacen + ", suplidor=" + suplidor
				+ ", existencia=" + existencia + ", disponible=" + disponible + ", no_factura=" + no_factura
				+ ", cantidad=" + cantidad + ", serial=" + serial + ", tipoMovimiento=" + tipoMovimiento + ", costo="
				+ costo + ", usuario=" + usuario + ", fecha=" + fecha + "]";
	}
}
