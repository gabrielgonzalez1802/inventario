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
@Table(name = "facturas")
public class Factura {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_factura")
	private Integer id;
	
	private Integer codigo;
	
	@OneToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@OneToOne
	@JoinColumn(name = "id_comprobante")
	private ComprobanteFiscal comprobanteFiscal;
	
	@OneToOne
	@JoinColumn(name = "id_condicion_pago")
	private CondicionPago condicionPago;
	
	private String ncf;
	private Date fecha;
	private Date vencimiento;
	private String hora;
	
	private String condicion; //validar
	
	private Double total_venta;
	private String nombre_cliente;
	private String telefono_cliente;
	private String rnc_cliente;
	private String nota_factura;
	
	private Double total_itbis;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@OneToOne
	@JoinColumn(name = "id_vendedor")
	private Vendedor vendedor;
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;
	
	private Double abono = 0.0;
	private Integer credito = 0;
	
	private Integer cuotas;
	
	@OneToOne
	@JoinColumn(name = "forma_pago")
	private FormaPago formaPago;
	
	private Double avance_taller;
	private Integer comision = 0;
	
	private Integer id_comision; //Se incorporara cuando se agregue la logica del taller
	
	private Integer paga = 0;

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

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public ComprobanteFiscal getComprobanteFiscal() {
		return comprobanteFiscal;
	}

	public void setComprobanteFiscal(ComprobanteFiscal comprobanteFiscal) {
		this.comprobanteFiscal = comprobanteFiscal;
	}

	public String getNcf() {
		return ncf;
	}

	public void setNcf(String ncf) {
		this.ncf = ncf;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public String getCondicion() {
		return condicion;
	}

	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}

	public Double getTotal_venta() {
		return total_venta;
	}

	public void setTotal_venta(Double total_venta) {
		this.total_venta = total_venta;
	}

	public String getNombre_cliente() {
		return nombre_cliente;
	}

	public void setNombre_cliente(String nombre_cliente) {
		this.nombre_cliente = nombre_cliente;
	}

	public String getTelefono_cliente() {
		return telefono_cliente;
	}

	public void setTelefono_cliente(String telefono_cliente) {
		this.telefono_cliente = telefono_cliente;
	}

	public String getRnc_cliente() {
		return rnc_cliente;
	}

	public void setRnc_cliente(String rnc_cliente) {
		this.rnc_cliente = rnc_cliente;
	}

	public String getNota_factura() {
		return nota_factura;
	}

	public void setNota_factura(String nota_factura) {
		this.nota_factura = nota_factura;
	}

	public Double getTotal_itbis() {
		return total_itbis;
	}

	public void setTotal_itbis(Double total_itbis) {
		this.total_itbis = total_itbis;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Vendedor getVendedor() {
		return vendedor;
	}

	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}

	public Almacen getAlmacen() {
		return almacen;
	}

	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}

	public Double getAbono() {
		return abono;
	}

	public void setAbono(Double abono) {
		this.abono = abono;
	}

	public Integer getCredito() {
		return credito;
	}

	public void setCredito(Integer credito) {
		this.credito = credito;
	}

	public Integer getCuotas() {
		return cuotas;
	}

	public void setCuotas(Integer cuotas) {
		this.cuotas = cuotas;
	}

	public FormaPago getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(FormaPago formaPago) {
		this.formaPago = formaPago;
	}

	public Double getAvance_taller() {
		return avance_taller;
	}

	public void setAvance_taller(Double avance_taller) {
		this.avance_taller = avance_taller;
	}

	public Integer getComision() {
		return comision;
	}

	public void setComision(Integer comision) {
		this.comision = comision;
	}

	public Integer getId_comision() {
		return id_comision;
	}

	public void setId_comision(Integer id_comision) {
		this.id_comision = id_comision;
	}

	public Integer getPaga() {
		return paga;
	}

	public void setPaga(Integer paga) {
		this.paga = paga;
	}

	public CondicionPago getCondicionPago() {
		return condicionPago;
	}

	public void setCondicionPago(CondicionPago condicionPago) {
		this.condicionPago = condicionPago;
	}

	@Override
	public String toString() {
		return "Factura [id=" + id + ", codigo=" + codigo + ", cliente=" + cliente + ", comprobanteFiscal="
				+ comprobanteFiscal + ", condicionPago=" + condicionPago + ", ncf=" + ncf + ", fecha=" + fecha
				+ ", vencimiento=" + vencimiento + ", hora=" + hora + ", condicion=" + condicion + ", total_venta="
				+ total_venta + ", nombre_cliente=" + nombre_cliente + ", telefono_cliente=" + telefono_cliente
				+ ", rnc_cliente=" + rnc_cliente + ", nota_factura=" + nota_factura + ", total_itbis=" + total_itbis
				+ ", usuario=" + usuario + ", vendedor=" + vendedor + ", almacen=" + almacen + ", abono=" + abono
				+ ", credito=" + credito + ", cuotas=" + cuotas + ", formaPago=" + formaPago + ", avance_taller="
				+ avance_taller + ", comision=" + comision + ", id_comision=" + id_comision + ", paga=" + paga + "]";
	}
}
