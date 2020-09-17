package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.FacturaDetalle;
import com.developergg.app.model.FacturaDetalleSerial;

public interface IFacturasDetallesSerialesService {
	FacturaDetalleSerial buscarPorId(Integer id);
	List<FacturaDetalleSerial> buscarPorDetalle(FacturaDetalle facturaDetalle);
	void guardar(FacturaDetalleSerial facturaDetalleSerial);
	void eliminar(Integer id);
}
