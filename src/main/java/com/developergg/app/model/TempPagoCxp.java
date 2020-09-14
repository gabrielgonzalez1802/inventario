package com.developergg.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "temp_pago_cxp")
public class TempPagoCxp {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_temporal_pago_cxp")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_forma_pago")
	private FormaPago formaPago;
	
	@OneToOne
	@JoinColumn(name = "id_compra")
	private Compra compra; 
	
	private Double monto=0.0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public FormaPago getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(FormaPago formaPago) {
		this.formaPago = formaPago;
	}
	
	public Compra getCompra() {
		return compra;
	}

	public void setCompra(Compra compra) {
		this.compra = compra;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	@Override
	public String toString() {
		return "TempPagoCxp [id=" + id + ", formaPago=" + formaPago + ", compra=" + compra + ", monto=" + monto + "]";
	}
}
