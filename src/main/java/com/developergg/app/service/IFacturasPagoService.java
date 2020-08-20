package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaPago;

public interface IFacturasPagoService {
	FacturaPago buscarPorId(Integer id);
	List<FacturaPago> buscarPorFactura(Factura factura);
	void guardar(FacturaPago facturaPago);
	void eliminar(Integer id);
}
