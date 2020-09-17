package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.DevolucionFactura;

public interface IDevolucionesFacturasService {
	DevolucionFactura buscarPorId(Integer id);
	List<DevolucionFactura> buscarPorAlmacen(Almacen almacen);
	void guardar(DevolucionFactura devolucionFactura);
}
