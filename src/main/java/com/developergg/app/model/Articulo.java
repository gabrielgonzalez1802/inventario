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
import javax.persistence.Transient;

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
	private String gama;
	private String imagen = "no-image.png";
	
	@Transient
	private Integer cantidad = 0;
	
	private Integer rango_precio_maximo_desde;
	private Integer rango_precio_maximo_hasta;
	
	private Integer rango_precio_minimo_desde;
	private Integer rango_precio_minimo_hasta;
	
	private Integer rango_precio_mayor_desde;
	private Integer rango_precio_mayor_hasta;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="articulos_articulos",
			   joinColumns = @JoinColumn(name="articulo_id"),
			   inverseJoinColumns = @JoinColumn(name="articulo_id_ref")			
			)
	private List<Articulo> articulos;
	
	public void agregar(Articulo tempArticulo) {
		if (articulos == null) {
			articulos = new LinkedList<Articulo>();
		}
		articulos.add(tempArticulo);
	}
	
	public void limpiar() {
		articulos.clear();
	}
	
	@Column(name = "id_usuario")
	private Integer idUsuario;
	
	@OneToOne
	@JoinColumn(name = "id_tienda")
	private Propietario tienda;

	private Integer eliminado = 0;
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
	public String getGama() {
		return gama;
	}
	public void setGama(String gama) {
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
	public String getImagen() {
		return imagen;
	}
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
	public Integer getRango_precio_maximo_desde() {
		return rango_precio_maximo_desde;
	}
	public void setRango_precio_maximo_desde(Integer rango_precio_maximo_desde) {
		this.rango_precio_maximo_desde = rango_precio_maximo_desde;
	}
	public Integer getRango_precio_maximo_hasta() {
		return rango_precio_maximo_hasta;
	}
	public void setRango_precio_maximo_hasta(Integer rango_precio_maximo_hasta) {
		this.rango_precio_maximo_hasta = rango_precio_maximo_hasta;
	}
	public Integer getRango_precio_minimo_desde() {
		return rango_precio_minimo_desde;
	}
	public void setRango_precio_minimo_desde(Integer rango_precio_minimo_desde) {
		this.rango_precio_minimo_desde = rango_precio_minimo_desde;
	}
	public Integer getRango_precio_minimo_hasta() {
		return rango_precio_minimo_hasta;
	}
	public void setRango_precio_minimo_hasta(Integer rango_precio_minimo_hasta) {
		this.rango_precio_minimo_hasta = rango_precio_minimo_hasta;
	}
	public Integer getRango_precio_mayor_desde() {
		return rango_precio_mayor_desde;
	}
	public void setRango_precio_mayor_desde(Integer rango_precio_mayor_desde) {
		this.rango_precio_mayor_desde = rango_precio_mayor_desde;
	}
	public Integer getRango_precio_mayor_hasta() {
		return rango_precio_mayor_hasta;
	}
	public void setRango_precio_mayor_hasta(Integer rango_precio_mayor_hasta) {
		this.rango_precio_mayor_hasta = rango_precio_mayor_hasta;
	}
	public List<Articulo> getArticulos() {
		return articulos;
	}
	public void setArticulos(List<Articulo> articulos) {
		this.articulos = articulos;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	
	@Override
	public String toString() {
		return "Articulo [id=" + id + ", nombre=" + nombre + ", codigo=" + codigo + ", codigo_barras=" + codigo_barras
				+ ", categoria=" + categoria + ", costo=" + costo + ", precio_maximo=" + precio_maximo
				+ ", precio_minimo=" + precio_minimo + ", precio_mayor=" + precio_mayor + ", imei=" + imei + ", itbis="
				+ itbis + ", gama=" + gama + ", imagen=" + imagen + ", rango_precio_maximo_desde="
				+ rango_precio_maximo_desde + ", rango_precio_maximo_hasta=" + rango_precio_maximo_hasta
				+ ", rango_precio_minimo_desde=" + rango_precio_minimo_desde + ", rango_precio_minimo_hasta="
				+ rango_precio_minimo_hasta + ", rango_precio_mayor_desde=" + rango_precio_mayor_desde
				+ ", rango_precio_mayor_hasta=" + rango_precio_mayor_hasta + ", articulos=" + articulos + ", idUsuario="
				+ idUsuario + ", tienda=" + tienda + ", eliminado=" + eliminado + ", fecha_eliminado=" + fecha_eliminado
				+ "]";
	}
}
