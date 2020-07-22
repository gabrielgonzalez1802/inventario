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
@Table(name ="codigo_activacion")
public class CodigoActivacion {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_codigo")
	private Integer id;
	
	@Column(name = "id_almacen")
	private Integer idAlmacen;

	private String codigo;
	private Date fecha;
	private String hora;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	private Integer estado;
	
	@Column(name = "ip_equipo")
	private String ipEquipo;
	
	private String mac;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdAlmacen() {
		return idAlmacen;
	}

	public void setIdAlmacen(Integer idAlmacen) {
		this.idAlmacen = idAlmacen;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}

	public String getIpEquipo() {
		return ipEquipo;
	}

	public void setIpEquipo(String ipEquipo) {
		this.ipEquipo = ipEquipo;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	@Override
	public String toString() {
		return "CodigoActivacion [id=" + id + ", idAlmacen=" + idAlmacen + ", codigo=" + codigo + ", fecha=" + fecha
				+ ", hora=" + hora + ", usuario=" + usuario + ", estado=" + estado + ", ipEquipo=" + ipEquipo + ", mac="
				+ mac + "]";
	}
}
