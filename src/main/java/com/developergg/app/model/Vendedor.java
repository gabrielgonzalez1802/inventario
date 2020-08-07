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
@Table(name = "vendedores")
public class Vendedor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_vendedor")
	private Integer id;
	
	private String codigo;
	private String nombre;
	private String cedula;
	private String direccion;
	private String telefono;
	private String celular;
	private String email;
	private Integer eliminado = 0;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario; //usuario que elimine el registro
	
	private Date fecha_eliminado;
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getEliminado() {
		return eliminado;
	}

	public void setEliminado(Integer eliminado) {
		this.eliminado = eliminado;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Date getFecha_eliminado() {
		return fecha_eliminado;
	}

	public void setFecha_eliminado(Date fecha_eliminado) {
		this.fecha_eliminado = fecha_eliminado;
	}

	public Almacen getAlmacen() {
		return almacen;
	}

	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}

	@Override
	public String toString() {
		return "Vendedor [id=" + id + ", codigo=" + codigo + ", nombre=" + nombre + ", cedula=" + cedula
				+ ", direccion=" + direccion + ", telefono=" + telefono + ", celular=" + celular + ", email=" + email
				+ ", eliminado=" + eliminado + ", usuario=" + usuario + ", fecha_eliminado=" + fecha_eliminado
				+ ", almacen=" + almacen + "]";
	}
}
