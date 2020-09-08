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
@Table(name = "taller")
public class Taller {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_reparacion")
	private Integer id;
	
	private Integer codigo;
	private Date fecha;
	private String hora;
	private String nombre;
	private String cedula;
	private String telefono;
	private String celular;
	private String marca;
	
	@OneToOne
	@JoinColumn(name = "id_tipo_equipo")
	private TipoEquipo tipoEquipo;

	@OneToOne
	@JoinColumn(name = "id_factura_temp")
	private FacturaTemp facturaTemp;
	
	private String modelo;
	private String serial;
	private String tipo_reparacion;
	private String estado = "Abierto";
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	private String problema;
	private Integer entregado = 0;
	private Double avance = 0.0;
	
	private Double total = 0.0; 
	
	@OneToOne
	@JoinColumn(name = "asignado")
	private Usuario asignado;
	
	private String motivo;
	private Integer entregado_por;
	private Date fecha_entrega;
	
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
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public TipoEquipo getTipoEquipo() {
		return tipoEquipo;
	}
	public void setTipoEquipo(TipoEquipo tipoEquipo) {
		this.tipoEquipo = tipoEquipo;
	}
	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public String getTipo_reparacion() {
		return tipo_reparacion;
	}
	public void setTipo_reparacion(String tipo_reparacion) {
		this.tipo_reparacion = tipo_reparacion;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
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
	public String getProblema() {
		return problema;
	}
	public void setProblema(String problema) {
		this.problema = problema;
	}
	public Integer getEntregado() {
		return entregado;
	}
	public void setEntregado(Integer entregado) {
		this.entregado = entregado;
	}
	public Double getAvance() {
		return avance;
	}
	public void setAvance(Double avance) {
		this.avance = avance;
	}
	public String getMotivo() {
		return motivo;
	}
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	public Integer getEntregado_por() {
		return entregado_por;
	}
	public void setEntregado_por(Integer entregado_por) {
		this.entregado_por = entregado_por;
	}
	public Date getFecha_entrega() {
		return fecha_entrega;
	}
	public void setFecha_entrega(Date fecha_entrega) {
		this.fecha_entrega = fecha_entrega;
	}
	public Usuario getAsignado() {
		return asignado;
	}
	public void setAsignado(Usuario asignado) {
		this.asignado = asignado;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	public FacturaTemp getFacturaTemp() {
		return facturaTemp;
	}
	public void setFacturaTemp(FacturaTemp facturaTemp) {
		this.facturaTemp = facturaTemp;
	}
	@Override
	public String toString() {
		return "Taller [id=" + id + ", codigo=" + codigo + ", fecha=" + fecha + ", hora=" + hora + ", nombre=" + nombre
				+ ", cedula=" + cedula + ", telefono=" + telefono + ", celular=" + celular + ", marca=" + marca
				+ ", tipoEquipo=" + tipoEquipo + ", facturaTemp=" + facturaTemp + ", modelo=" + modelo + ", serial="
				+ serial + ", tipo_reparacion=" + tipo_reparacion + ", estado=" + estado + ", almacen=" + almacen
				+ ", usuario=" + usuario + ", problema=" + problema + ", entregado=" + entregado + ", avance=" + avance
				+ ", total=" + total + ", asignado=" + asignado + ", motivo=" + motivo + ", entregado_por="
				+ entregado_por + ", fecha_entrega=" + fecha_entrega + "]";
	}
}
