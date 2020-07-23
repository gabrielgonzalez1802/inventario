package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;

public interface IAlmacenesService {
	List<Almacen> buscarTodos();
	Almacen buscarPorId(Integer idAlmacen); 
	List<Almacen> buscarPorIdTienda(Integer idTienda);
	void guardar(Almacen almacen);
	void eliminar(Integer idAlmacen);
}
