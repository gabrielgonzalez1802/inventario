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
@Table(name = "temp_facturas_detalles_pago")
public class FacturaDetallePagoTemp {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_factura_detalle_pago")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_factura_temp")
	private FacturaTemp facturaTemp;
	
	@OneToOne
	@JoinColumn(name = "id_forma_pago")
	private FormaPago formaPago;
	
	private Double monto = 0.0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public FacturaTemp getFacturaTemp() {
		return facturaTemp;
	}

	public void setFacturaTemp(FacturaTemp facturaTemp) {
		this.facturaTemp = facturaTemp;
	}

	public FormaPago getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(FormaPago formaPago) {
		this.formaPago = formaPago;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	@Override
	public String toString() {
		return "FacturaDetallePagoTemp [id=" + id + ", facturaTemp=" + facturaTemp + ", formaPago=" + formaPago
				+ ", monto=" + monto + "]";
	}
}
