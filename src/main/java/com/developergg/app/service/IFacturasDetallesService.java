package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetalle;

public interface IFacturasDetallesService {
	FacturaDetalle buscarPorId(Integer id);
	List<FacturaDetalle> buscarPorFactura(Factura factura);
	void guardar(FacturaDetalle facturaDetalle);
	void eliminar(Integer id);
}
