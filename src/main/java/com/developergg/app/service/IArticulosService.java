package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Articulo;
import com.developergg.app.model.Propietario;

public interface IArticulosService {
	List<Articulo> buscarTodos();
	Articulo buscarPorId(Integer idArticulo);
	void guardar(Articulo articulo);
	void eliminar(Integer idArticulo);
	List<Articulo> buscarPorTienda(Propietario tienda);
	void save(Articulo articulo);
	List<Articulo> buscarPorNombreOrCodigo(String txt, Propietario tienda);
}
