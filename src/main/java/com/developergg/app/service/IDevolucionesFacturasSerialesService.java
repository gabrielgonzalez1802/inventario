package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.DevolucionFactura;
import com.developergg.app.model.DevolucionFacturaSerial;
import com.developergg.app.model.FacturaDetalle;
import com.developergg.app.model.FacturaDetalleSerial;

public interface IDevolucionesFacturasSerialesService {
	DevolucionFacturaSerial buscarPorId(Integer id);
	List<DevolucionFacturaSerial> buscarPorFacturaDetalle(FacturaDetalle facturaDetalle);
	List<DevolucionFacturaSerial> buscarPorFacturaDetalleSerial(FacturaDetalleSerial facturaDetalleSerial);
	List<DevolucionFacturaSerial> buscarPorDevolucionFactura(DevolucionFactura devolucionFactura);
	void guardar(DevolucionFacturaSerial devolucionFacturaSerial);
	void eliminar(Integer id);
}
