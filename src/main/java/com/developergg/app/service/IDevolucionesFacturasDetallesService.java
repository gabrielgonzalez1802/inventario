package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.DevolucionFactura;
import com.developergg.app.model.DevolucionFacturaDetalle;
import com.developergg.app.model.FacturaDetalle;
import com.developergg.app.model.FacturaDetalleTaller;

public interface IDevolucionesFacturasDetallesService {
	DevolucionFacturaDetalle buscarPorId(Integer id);
	List<DevolucionFacturaDetalle> buscarPorFacturaDetalle(FacturaDetalle facturaDetalle);
	List<DevolucionFacturaDetalle> buscarPorDevolucionFactura(DevolucionFactura devolucionFactura);
	List<DevolucionFacturaDetalle> buscarPorFacturaDetalleTaller(FacturaDetalleTaller facturaDetalleTaller);
	void guardar(DevolucionFacturaDetalle devolucionFacturaDetalle);
	void eliminar(Integer id);
}
