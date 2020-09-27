package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloSerialTemp;

public interface IArticulosSerialesTemp {
	List<ArticuloSerialTemp> buscarTodos();
	ArticuloSerialTemp buscarPorIdArticuloSerialTemp(Integer idArticuloSerialTemp);
	List<ArticuloSerialTemp> buscarPorAlmacen(Almacen almacen);
	List<ArticuloSerialTemp> buscarPorIdAlmacenDesc(Integer idAlmacen);
	List<ArticuloSerialTemp> buscarPorArticuloAlmacen(Articulo articulo, Almacen almacen);
	ArticuloSerialTemp buscarPorSerial(String serial);
	List<ArticuloSerialTemp> buscarPorSerialAndAlmacen(String serial, Almacen almacen);
	void guardar(ArticuloSerialTemp ArticuloSerialTemp);
	void eliminar(ArticuloSerialTemp ArticuloSerialTemp);
	void eliminar(List<ArticuloSerialTemp> seriales);
}
