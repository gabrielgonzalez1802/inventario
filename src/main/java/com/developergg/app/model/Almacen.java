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
@Table(name = "almacenes")
public class Almacen {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_almacen")
	private Integer id;
	
	private String nombre;
	private String direccion;
	private String telefono;
	private String rnc;
	
	@OneToOne
	@JoinColumn(name = "id_tienda")
	private Propietario propietario;
	
	private Date vencimiento;
	
	@Column(name = "pie_pagina_taller")
	private String pieTaller;
	
	@Column(name = "pie_pagina_factura")
	private String pieFactura;
	
	@Column(name = "pie_pagina_abono")
	private String pieAbono;
	
	@Column(name = "id_secuencia")
	private Integer idSecuencia;
	
	private String imagen;
	private String encabezado;
	
	@Column(name = "precio_label")
	private Integer precioLabel;
	
	@Column(name = "codigo_label")
	private Integer codigoLabel;
	
	@Column(name = "referencia_label")
	private Integer referenciaLabel;
	
	@Column(name = "nombre_label")
	private Integer nombreLabel;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
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

	public String getRnc() {
		return rnc;
	}

	public void setRnc(String rnc) {
		this.rnc = rnc;
	}

	public Propietario getPropietario() {
		return propietario;
	}

	public void setPropietario(Propietario propietario) {
		this.propietario = propietario;
	}

	public Date getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}

	public String getPieTaller() {
		return pieTaller;
	}

	public void setPieTaller(String pieTaller) {
		this.pieTaller = pieTaller;
	}

	public String getPieFactura() {
		return pieFactura;
	}

	public void setPieFactura(String pieFactura) {
		this.pieFactura = pieFactura;
	}

	public String getPieAbono() {
		return pieAbono;
	}

	public void setPieAbono(String pieAbono) {
		this.pieAbono = pieAbono;
	}

	public Integer getIdSecuencia() {
		return idSecuencia;
	}

	public void setIdSecuencia(Integer idSecuencia) {
		this.idSecuencia = idSecuencia;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public String getEncabezado() {
		return encabezado;
	}

	public void setEncabezado(String encabezado) {
		this.encabezado = encabezado;
	}

	public Integer getPrecioLabel() {
		return precioLabel;
	}

	public void setPrecioLabel(Integer precioLabel) {
		this.precioLabel = precioLabel;
	}

	public Integer getCodigoLabel() {
		return codigoLabel;
	}

	public void setCodigoLabel(Integer codigoLabel) {
		this.codigoLabel = codigoLabel;
	}

	public Integer getReferenciaLabel() {
		return referenciaLabel;
	}

	public void setReferenciaLabel(Integer referenciaLabel) {
		this.referenciaLabel = referenciaLabel;
	}

	public Integer getNombreLabel() {
		return nombreLabel;
	}

	public void setNombreLabel(Integer nombreLabel) {
		this.nombreLabel = nombreLabel;
	}

	@Override
	public String toString() {
		return "Almacen [id=" + id + ", nombre=" + nombre + ", direccion=" + direccion + ", telefono=" + telefono
				+ ", rnc=" + rnc + ", propietario=" + propietario + ", vencimiento=" + vencimiento + ", pieTaller="
				+ pieTaller + ", pieFactura=" + pieFactura + ", pieAbono=" + pieAbono + ", idSecuencia=" + idSecuencia
				+ ", imagen=" + imagen + ", encabezado=" + encabezado + ", precioLabel=" + precioLabel
				+ ", codigoLabel=" + codigoLabel + ", referenciaLabel=" + referenciaLabel + ", nombreLabel="
				+ nombreLabel + "]";
	}
	
}
