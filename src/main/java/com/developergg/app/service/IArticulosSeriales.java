package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloSerial;

public interface IArticulosSeriales {
	List<ArticuloSerial> buscarTodos();
	ArticuloSerial buscarPorIdArticuloSerial(Integer idArticuloSerial);
	List<ArticuloSerial> buscarPorAlmacen(Almacen almacen);
	List<ArticuloSerial> buscarPorIdAlmacenDesc(Integer idAlmacen);
	List<ArticuloSerial> buscarPorArticuloAlmacen(Articulo articulo, Almacen almacen);
	ArticuloSerial buscarPorSerial(String serial);
	List<ArticuloSerial> buscarPorSerialAndAlmacen(String serial, Almacen almacen);
	void guardar(ArticuloSerial articuloSerial);
}
