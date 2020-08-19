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
@Table(name = "temp_facturas")
public class FacturaTemp {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_factura")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@OneToOne
	@JoinColumn(name = "id_comprobante")
	private ComprobanteFiscal comprobanteFiscal;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@OneToOne
	@JoinColumn(name = "id_condicion_pago")
	private CondicionPago condicionPago;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public ComprobanteFiscal getComprobanteFiscal() {
		return comprobanteFiscal;
	}

	public void setComprobanteFiscal(ComprobanteFiscal comprobanteFiscal) {
		this.comprobanteFiscal = comprobanteFiscal;
	}

	public CondicionPago getCondicionPago() {
		return condicionPago;
	}

	public void setCondicionPago(CondicionPago condicionPago) {
		this.condicionPago = condicionPago;
	}

	@Override
	public String toString() {
		return "FacturaTemp [id=" + id + ", cliente=" + cliente + ", comprobanteFiscal=" + comprobanteFiscal
				+ ", usuario=" + usuario + ", condicionPago=" + condicionPago + "]";
	}
}
