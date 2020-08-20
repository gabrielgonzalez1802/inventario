package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetalleServicio;

public interface IFacturasDetallesServiciosService {
	FacturaDetalleServicio buscarPorId(Integer id);
	List<FacturaDetalleServicio> buscarPorFactura(Factura factura);
	void guardar(FacturaDetalleServicio facturaDetalleServicio);
	void eliminar(Integer id);
}
