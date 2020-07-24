package com.developergg.app.model;

import java.util.Date;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "articulos")
public class Articulo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_articulo")
	private Integer id;
	
	private String nombre;
	private String codigo;
	private String codigo_barras;
	
	@OneToOne
	@JoinColumn(name = "id_categoria")
	private Categoria categoria;
	
	private Double costo;
	private Double precio_maximo;
	private Double precio_minimo;
	private Double precio_mayor;
	private String imei;
	private String itbis;
	private Integer minimo;
	private Integer gama;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="oferta_articulo",
			   joinColumns = @JoinColumn(name="id_articulo"),
			   inverseJoinColumns = @JoinColumn(name="id_oferta")			
			)
	private List<Oferta> ofertas;
	
	public void agregar(Oferta tempOferta) {
		if (ofertas == null) {
			ofertas = new LinkedList<Oferta>();
		}
		ofertas.add(tempOferta);
	}
	
	//verificar
	@Column(name = "id_usuario")
	private Integer idUsuario;
	
	@OneToOne
	@JoinColumn(name = "id_tienda")
	private Propietario tienda;
	
	private Double size;
	private String color;
	private Integer eliminado;
	private Date fecha_eliminado;
	
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
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getCodigo_barras() {
		return codigo_barras;
	}
	public void setCodigo_barras(String codigo_barras) {
		this.codigo_barras = codigo_barras;
	}
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	public Double getCosto() {
		return costo;
	}
	public void setCosto(Double costo) {
		this.costo = costo;
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
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getItbis() {
		return itbis;
	}
	public void setItbis(String itbis) {
		this.itbis = itbis;
	}
	public Integer getMinimo() {
		return minimo;
	}
	public void setMinimo(Integer minimo) {
		this.minimo = minimo;
	}
	public Integer getGama() {
		return gama;
	}
	public void setGama(Integer gama) {
		this.gama = gama;
	}
	public Integer getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	public Propietario getTienda() {
		return tienda;
	}
	public void setTienda(Propietario tienda) {
		this.tienda = tienda;
	}
	public Double getSize() {
		return size;
	}
	public void setSize(Double size) {
		this.size = size;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public Integer getEliminado() {
		return eliminado;
	}
	public void setEliminado(Integer eliminado) {
		this.eliminado = eliminado;
	}
	public Date getFecha_eliminado() {
		return fecha_eliminado;
	}
	public void setFecha_eliminado(Date fecha_eliminado) {
		this.fecha_eliminado = fecha_eliminado;
	}
}
