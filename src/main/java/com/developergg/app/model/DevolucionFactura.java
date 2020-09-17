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
@Table(name = "devolucion_factura")
public class DevolucionFactura {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_devolucion")
	private Integer id;
	
	private Integer codigo;
	private Date fecha;
	private String hora;
	
	@OneToOne
	@JoinColumn(name = "id_factura")
	private Factura factura;
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@Column(name = "total_devolucion")
	private Double total;

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

	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
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

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "DevolucionFactura [id=" + id + ", codigo=" + codigo + ", fecha=" + fecha + ", hora=" + hora
				+ ", factura=" + factura + ", almacen=" + almacen + ", usuario=" + usuario + ", total=" + total + "]";
	}
}
