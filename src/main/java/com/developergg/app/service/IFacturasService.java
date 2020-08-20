package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Factura;

public interface IFacturasService {
	Factura buscarPorId(Integer id);
	List<Factura> buscarPorAlmacen(Almacen almacen);
	void guardar(Factura factura);
	void eliminar(Integer id);
}
