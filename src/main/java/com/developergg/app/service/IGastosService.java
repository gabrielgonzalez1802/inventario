package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Gasto;

public interface IGastosService {
	Gasto buscarPorId(Integer id);
	List<Gasto> buscarPorAlmacen(Almacen almacen);
	void guardar(Gasto gasto);
	void eliminar(Integer id);
}
