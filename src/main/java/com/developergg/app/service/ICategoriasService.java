package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Categoria;

public interface ICategoriasService {
	List<Categoria> buscarTodas();
	List<Categoria> buscarActivas();
	Categoria buscarPorId(Integer idCategoria); 
	void desactivarCategoria(Integer idCategoria);
	void guardar(Categoria categoria);
}
