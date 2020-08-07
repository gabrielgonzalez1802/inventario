package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloAjuste;

public interface IArticulosAjustesService {
	List<ArticuloAjuste> buscarTodos();
	ArticuloAjuste buscarPorId(Integer idArticuloAjuste);
	void guardar(ArticuloAjuste articuloAjuste);
	List<ArticuloAjuste> buscarPorArticuloYAlmacen(Articulo articulo, Almacen almacen);
}
