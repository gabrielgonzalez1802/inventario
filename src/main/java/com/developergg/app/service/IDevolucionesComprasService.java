package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Compra;
import com.developergg.app.model.DevolucionCompra;

public interface IDevolucionesComprasService {
	DevolucionCompra buscarPorId(Integer id);
	List<DevolucionCompra> buscarPorAlmacen(Almacen almacen);
	List<DevolucionCompra> buscarPorCompra(Compra compra);
	void guardar(DevolucionCompra devolucionCompra);
	void eliminar(DevolucionCompra devolucionCompra);
}
