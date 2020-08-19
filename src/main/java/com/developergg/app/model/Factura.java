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
	
	
}
