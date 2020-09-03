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
@Table(name = "taller_detalles")
public class TallerDetalle {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_taller_detalle")
	private Integer id;
	
	private Date fecha;
	private String hora;
	
	@OneToOne
	@JoinColumn(name = "id_articulo_taller")
	private Articulo articulo;
	
	@OneToOne
	@JoinColumn(name = "id_taller_articulo")
	private TallerArticulo tallerArticulo;
	
	@OneToOne
	@JoinColumn(name = "id_reparacion")
	private Taller taller;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	private String item;
	private Integer cantidad = 0;
	private Double costo = 0.0;
	private Double precio = 0.0;

	private Double subtotal = 0.0;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getHora() {
		return hora;
	}
	public void setHora(String hora) {
		this.hora = hora;
	}
	public Taller getTaller() {
		return taller;
	}
	public void setTaller(Taller taller) {
		this.taller = taller;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	public Double getCosto() {
		return costo;
	}
	public void setCosto(Double costo) {
		this.costo = costo;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
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
	public Double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}
	@Override
	public String toString() {
		return "TallerDetalle [id=" + id + ", fecha=" + fecha + ", hora=" + hora + ", articulo=" + articulo
				+ ", tallerArticulo=" + tallerArticulo + ", taller=" + taller + ", usuario=" + usuario + ", item="
				+ item + ", cantidad=" + cantidad + ", costo=" + costo + ", precio=" + precio + ", subtotal=" + subtotal
				+ "]";
	}
}
