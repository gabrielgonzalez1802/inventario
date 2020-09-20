package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.DevolucionFactura;
import com.developergg.app.model.Factura;

public interface IDevolucionesFacturasService {
	DevolucionFactura buscarPorId(Integer id);
	List<DevolucionFactura> buscarPorAlmacen(Almacen almacen);
	List<DevolucionFactura> buscarPorFactura(Factura factura);
	void guardar(DevolucionFactura devolucionFactura);
	void eliminar(DevolucionFactura devolucionFactura);
}
