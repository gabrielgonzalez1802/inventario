package com.developergg.app.service;

import java.util.Date;
import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Compra;

public interface IComprasService {
	Compra buscarPorId(Integer id);
	List<Compra> buscarPorAlmacen(Almacen almacen);
	List<Compra> buscarPorAlmacenFechas(Almacen almacen, Date desde, Date hasta);
	void guardar(Compra compra);
	void eliminar(Integer id);
}
