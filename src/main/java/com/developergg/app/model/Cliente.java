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
@Table(name = "clientes")
public class Cliente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_cliente")
	private Integer id;
	
	private String codigo;
	private String nombre;
	private String rnc;
	private String telefono;
	private String celular;
	private String direccion;
	private String tncf;
	private String precio;
	
	private Integer id_vendedor;
	
	private String email;
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;
	
	private Integer eliminado = 0;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario; //usuario que elimine el registro
	
	private Date fecha_eliminado;

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

	public String getRnc() {
		return rnc;
	}

	public void setRnc(String rnc) {
		this.rnc = rnc;
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

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTncf() {
		return tncf;
	}

	public void setTncf(String tncf) {
		this.tncf = tncf;
	}

	public String getPrecio() {
		return precio;
	}

	public void setPrecio(String precio) {
		this.precio = precio;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Almacen getAlmacen() {
		return almacen;
	}

	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
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
	
	public Integer getId_vendedor() {
		return id_vendedor;
	}

	public void setId_vendedor(Integer id_vendedor) {
		this.id_vendedor = id_vendedor;
	}

	@Override
	public String toString() {
		return "Cliente [id=" + id + ", codigo=" + codigo + ", nombre=" + nombre + ", rnc=" + rnc + ", telefono="
				+ telefono + ", celular=" + celular + ", direccion=" + direccion + ", tncf=" + tncf + ", precio="
				+ precio + ", email=" + email + ", almacen=" + almacen + ", eliminado="
				+ eliminado + ", usuario=" + usuario + ", fecha_eliminado=" + fecha_eliminado + "]";
	}
}
