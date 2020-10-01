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
@Table(name = "devolucion_compra")
public class DevolucionCompra {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_devolucion")
	private Integer id;
	
	private Integer codigo;
	private Date fecha;
	private String hora;
	
	@OneToOne
	@JoinColumn(name = "id_compra")
	private Compra compra;
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	private Double precio = 0.0;
	private Double itbis = 0.0;
	private Double total_devolucion = 0.0;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
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
	public Compra getCompra() {
		return compra;
	}
	public void setCompra(Compra compra) {
		this.compra = compra;
	}
	public Almacen getAlmacen() {
		return almacen;
	}
	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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
	public Double getTotal_devolucion() {
		return total_devolucion;
	}
	public void setTotal_devolucion(Double total_devolucion) {
		this.total_devolucion = total_devolucion;
	}
	@Override
	public String toString() {
		return "DevolucionCompra [id=" + id + ", codigo=" + codigo + ", fecha=" + fecha + ", hora=" + hora + ", compra="
				+ compra + ", almacen=" + almacen + ", usuario=" + usuario + ", precio=" + precio + ", itbis=" + itbis
				+ ", total_devolucion=" + total_devolucion + "]";
	}
}
