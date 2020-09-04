package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.Taller;
import com.developergg.app.model.TallerArticulo;

public interface ITalleresArticulosService {
	TallerArticulo buscarPorId(Integer id);
	List<TallerArticulo> buscarPorAlmacen(Almacen almacen);
	List<TallerArticulo> buscarPorArticuloAlmacen(Articulo articulo, Almacen almacen);
	List<TallerArticulo> buscarPorTaller(Taller taller);
	List<TallerArticulo> buscarPorFacturaTemp(FacturaTemp facturaTemp);
	void guardar(TallerArticulo tallerArticulo);
	void eliminar(Integer id);
}
