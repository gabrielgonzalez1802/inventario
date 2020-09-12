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
@Table(name = "compras")
public class Compra {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_compra")
	private Integer id;
	
	private Date fecha;
	private String no_factura;
	
	@OneToOne
	@JoinColumn(name = "id_suplidor")
	private Suplidor suplidor;
	
	@OneToOne
	@JoinColumn(name = "id_condicion_pago")
	private CondicionPago condicion;
	
	@OneToOne
	@JoinColumn(name = "id_comprobante")
	private ComprobanteFiscal comprobanteFiscal;
	
	private String ncf;
	
	private Integer en_proceso = 0;
	
	@OneToOne
	@JoinColumn(name = "id_vendedor")
	private Vendedor vendedor;
	
	private String observacion;
	
	@Column(name = "compra_subtotal")
	private Double subTotal=0.0;
	
	@Column(name = "compra_itbis")
	private Double itBis=0.0;
	
	@Column(name = "compra_total_neto")
	private Double totalNeto;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@OneToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;
	
	private Double compra_pago_caja;
	private Double compra_efectivo;
	private Double compra_deposito;
	private Double compra_transferencia;
	private Double compra_cheque;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getNo_factura() {
		return no_factura;
	}
	public void setNo_factura(String no_factura) {
		this.no_factura = no_factura;
	}
	public Suplidor getSuplidor() {
		return suplidor;
	}
	public void setSuplidor(Suplidor suplidor) {
		this.suplidor = suplidor;
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
	public Vendedor getVendedor() {
		return vendedor;
	}
	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public Double getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(Double subTotal) {
		this.subTotal = subTotal;
	}
	public Double getItBis() {
		return itBis;
	}
	public void setItBis(Double itBis) {
		this.itBis = itBis;
	}
	public Double getTotalNeto() {
		return totalNeto;
	}
	public void setTotalNeto(Double totalNeto) {
		this.totalNeto = totalNeto;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Almacen getAlmacen() {
		return almacen;
	}
	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}
	public Double getCompra_pago_caja() {
		return compra_pago_caja;
	}
	public void setCompra_pago_caja(Double compra_pago_caja) {
		this.compra_pago_caja = compra_pago_caja;
	}
	public Double getCompra_efectivo() {
		return compra_efectivo;
	}
	public void setCompra_efectivo(Double compra_efectivo) {
		this.compra_efectivo = compra_efectivo;
	}
	public Double getCompra_deposito() {
		return compra_deposito;
	}
	public void setCompra_deposito(Double compra_deposito) {
		this.compra_deposito = compra_deposito;
	}
	public Double getCompra_transferencia() {
		return compra_transferencia;
	}
	public void setCompra_transferencia(Double compra_transferencia) {
		this.compra_transferencia = compra_transferencia;
	}
	public Double getCompra_cheque() {
		return compra_cheque;
	}
	public void setCompra_cheque(Double compra_cheque) {
		this.compra_cheque = compra_cheque;
	}
	public CondicionPago getCondicion() {
		return condicion;
	}
	public void setCondicion(CondicionPago condicion) {
		this.condicion = condicion;
	}
	public Integer getEn_proceso() {
		return en_proceso;
	}
	public void setEn_proceso(Integer en_proceso) {
		this.en_proceso = en_proceso;
	}
	@Override
	public String toString() {
		return "Compra [id=" + id + ", fecha=" + fecha + ", no_factura=" + no_factura + ", suplidor=" + suplidor
				+ ", condicion=" + condicion + ", comprobanteFiscal=" + comprobanteFiscal + ", ncf=" + ncf
				+ ", vendedor=" + vendedor + ", observacion=" + observacion + ", subTotal=" + subTotal + ", itBis="
				+ itBis + ", totalNeto=" + totalNeto + ", usuario=" + usuario + ", almacen=" + almacen
				+ ", compra_pago_caja=" + compra_pago_caja + ", compra_efectivo=" + compra_efectivo
				+ ", compra_deposito=" + compra_deposito + ", compra_transferencia=" + compra_transferencia
				+ ", compra_cheque=" + compra_cheque + "]";
	}
}
