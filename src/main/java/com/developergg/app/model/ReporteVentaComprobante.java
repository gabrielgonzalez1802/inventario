package com.developergg.app.model;

public class ReporteVentaComprobante {
	
	private Integer id;
	private String factura;
	private String ncf;
	private String cliente;
	private String rnc;
	private Double subtotal;
	private Double itbis;
	private Double total;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFactura() {
		return factura;
	}
	public void setFactura(String factura) {
		this.factura = factura;
	}
	public String getNcf() {
		return ncf;
	}
	public void setNcf(String ncf) {
		this.ncf = ncf;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	public String getRnc() {
		return rnc;
	}
	public void setRnc(String rnc) {
		this.rnc = rnc;
	}
	public Double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}
	public Double getItbis() {
		return itbis;
	}
	public void setItbis(Double itbis) {
		this.itbis = itbis;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	@Override
	public String toString() {
		return "ReporteVentaComprobante [id=" + id + ", factura=" + factura + ", ncf=" + ncf + ", cliente=" + cliente
				+ ", rnc=" + rnc + ", subtotal=" + subtotal + ", itbis=" + itbis + ", total=" + total + "]";
	}
}
