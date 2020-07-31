package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.ArticuloSerial;

public interface IArticulosSeriales {
	List<ArticuloSerial> buscarTodos();
	ArticuloSerial buscarPorIdArticuloSerial(Integer idArticuloSerial);
	List<ArticuloSerial> buscarPorAlmacen(Almacen almacen);
	List<ArticuloSerial> buscarPorIdAlmacenDesc(Integer idAlmacen);
	void guardar(ArticuloSerial articuloSerial);
}
