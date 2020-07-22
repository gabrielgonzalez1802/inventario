/**
 * Esta clase representa una entidad (un registro) en la tabla de Usuarios de la base de datos
 */
package com.developergg.app.model;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment MySQL
	@Column(name = "id_usuario")
	private Integer id;
	
	private String username;
	private String nombre;
	private String password;
	private Integer estatus;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getEstatus() {
		return estatus;
	}
	public void setEstatus(Integer estatus) {
		this.estatus = estatus;
	}
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="usuarioperfil",
			   joinColumns = @JoinColumn(name="idUsuario"),
			   inverseJoinColumns = @JoinColumn(name="idPerfil")			
			)
	private List<Perfil> perfiles;

	public void agregar(Perfil tempPerfil) {
		if (perfiles == null) {
			perfiles = new LinkedList<Perfil>();
		}
		perfiles.add(tempPerfil);
	}

	public List<Perfil> getPerfiles() {
		return perfiles;
	}

	public void setPerfiles(List<Perfil> perfiles) {
		this.perfiles = perfiles;
	}
	@Override
	public String toString() {
		return "Usuario [id=" + id + ", username=" + username + ", nombre=" + nombre + ", password=" + password
				+ ", estatus=" + estatus + ", perfiles=" + perfiles + "]";
	}
}
