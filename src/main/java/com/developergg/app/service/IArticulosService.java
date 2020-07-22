package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Articulo;

public interface IArticulosService {
	List<Articulo> buscarTodos();
	Articulo buscarPorId(Integer idArticulo);
	void guardar(Articulo articulo);
	void eliminar(Integer idArticulo);
}
