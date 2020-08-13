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
@Table(name = "articulos_seriales")
public class ArticuloSerial {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_serial")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_articulo")
	private Articulo articulo;
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;
	
	private String serial;
	private Date fecha;
	
	@OneToOne
	@JoinColumn(name = "id_suplidor")
	private Suplidor suplidor;
	
	//fixme luego se llevaran a objetos relacionados
	private Integer id_cliente;
	private Integer id_ajuste;
	private Integer id_compra;
	private Integer id_factura;
	
	private String estado = "Disponible";
	
	//fixme luego se llevaran a objetos relacionados
	private Integer id_usuario;
	private Double costo;
	
	private String no_factura;
	private Integer eliminado = 0;
	
	private Integer id_usuario_eliminar;
	private Date fecha_eliminado;
	
	private Integer id_envio;
	private Date fecha_envio;
	
	private Double precio_maximo;
	private Double precio_minimo;
	private Double precio_mayor;
	
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
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Suplidor getSuplidor() {
		return suplidor;
	}
	public void setSuplidor(Suplidor suplidor) {
		this.suplidor = suplidor;
	}
	public Integer getId_cliente() {
		return id_cliente;
	}
	public void setId_cliente(Integer id_cliente) {
		this.id_cliente = id_cliente;
	}
	public Integer getId_ajuste() {
		return id_ajuste;
	}
	public void setId_ajuste(Integer id_ajuste) {
		this.id_ajuste = id_ajuste;
	}
	public Integer getId_compra() {
		return id_compra;
	}
	public void setId_compra(Integer id_compra) {
		this.id_compra = id_compra;
	}
	public Integer getId_factura() {
		return id_factura;
	}
	public void setId_factura(Integer id_factura) {
		this.id_factura = id_factura;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public Integer getId_usuario() {
		return id_usuario;
	}
	public void setId_usuario(Integer id_usuario) {
		this.id_usuario = id_usuario;
	}
	public Double getCosto() {
		return costo;
	}
	public void setCosto(Double costo) {
		this.costo = costo;
	}
	public String getNo_factura() {
		return no_factura;
	}
	public void setNo_factura(String no_factura) {
		this.no_factura = no_factura;
	}
	public Integer getEliminado() {
		return eliminado;
	}
	public void setEliminado(Integer eliminado) {
		this.eliminado = eliminado;
	}
	public Integer getId_usuario_eliminar() {
		return id_usuario_eliminar;
	}
	public void setId_usuario_eliminar(Integer id_usuario_eliminar) {
		this.id_usuario_eliminar = id_usuario_eliminar;
	}
	public Date getFecha_eliminado() {
		return fecha_eliminado;
	}
	public void setFecha_eliminado(Date fecha_eliminado) {
		this.fecha_eliminado = fecha_eliminado;
	}
	public Integer getId_envio() {
		return id_envio;
	}
	public void setId_envio(Integer id_envio) {
		this.id_envio = id_envio;
	}
	public Date getFecha_envio() {
		return fecha_envio;
	}
	public void setFecha_envio(Date fecha_envio) {
		this.fecha_envio = fecha_envio;
	}
	public Double getPrecio_maximo() {
		return precio_maximo;
	}
	public void setPrecio_maximo(Double precio_maximo) {
		this.precio_maximo = precio_maximo;
	}
	public Double getPrecio_minimo() {
		return precio_minimo;
	}
	public void setPrecio_minimo(Double precio_minimo) {
		this.precio_minimo = precio_minimo;
	}
	public Double getPrecio_mayor() {
		return precio_mayor;
	}
	public void setPrecio_mayor(Double precio_mayor) {
		this.precio_mayor = precio_mayor;
	}
	
	@Override
	public String toString() {
		return "ArticuloSerial [id=" + id + ", articulo=" + articulo + ", almacen=" + almacen + ", serial=" + serial
				+ ", fecha=" + fecha + ", suplidor=" + suplidor + ", id_cliente=" + id_cliente + ", id_ajuste="
				+ id_ajuste + ", id_compra=" + id_compra + ", id_factura=" + id_factura + ", estado=" + estado
				+ ", id_usuario=" + id_usuario + ", costo=" + costo + ", no_factura=" + no_factura + ", eliminado="
				+ eliminado + ", id_usuario_eliminar=" + id_usuario_eliminar + ", fecha_eliminado=" + fecha_eliminado
				+ ", id_envio=" + id_envio + ", fecha_envio=" + fecha_envio + ", precio_maximo=" + precio_maximo
				+ ", precio_minimo=" + precio_minimo + ", precio_mayor=" + precio_mayor + "]";
	}
}
