package com.developergg.app.service;

import java.util.Date;
import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloAjuste;

public interface IArticulosAjustesService {
	List<ArticuloAjuste> buscarTodos();
	ArticuloAjuste buscarPorId(Integer idArticuloAjuste);
	void guardar(ArticuloAjuste articuloAjuste);
	List<ArticuloAjuste> buscarPorArticuloYAlmacen(Articulo articulo, Almacen almacen);
	List<ArticuloAjuste> buscarPorAlmacen(Almacen almacen);
	List<ArticuloAjuste> buscarPorAlmacenTipoMovimientoArticulos(Almacen almacen, String movimiento,
			List<Articulo> articulos);
	List<ArticuloAjuste> buscarPorAlmacenTipoMovimientoArticulosFechas(Almacen almacen, String movimiento,
			List<Articulo> articulos, Date desde, Date hasta);
	List<ArticuloAjuste> buscarPorAlmacenFechas(Almacen almacen, Date desde, Date hasta);
}
