package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetalleTaller;

public interface IFacturasDetallesTallerService {
	FacturaDetalleTaller buscarPorId(Integer id);
	List<FacturaDetalleTaller> buscarPorFactura(Factura factura);
	void guardar(FacturaDetalleTaller facturaDetalleTaller);
	void eliminar(Integer id);
	void eliminar(FacturaDetalleTaller facturaDetalleTaller);
}
