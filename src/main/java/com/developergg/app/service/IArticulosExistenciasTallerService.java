package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloExistenciaTaller;

public interface IArticulosExistenciasTallerService {
	List<ArticuloExistenciaTaller> buscarPorAlmacen(Almacen almacen);
	List<ArticuloExistenciaTaller> buscarPorArticuloAndAlmacen(Articulo articulo, Almacen almacen);
	List<ArticuloExistenciaTaller> buscarTodos();
	ArticuloExistenciaTaller buscarPorId(Integer id);
	void guardar(ArticuloExistenciaTaller articuloExistenciaTaller);
	void eliminar(Integer id);
}
