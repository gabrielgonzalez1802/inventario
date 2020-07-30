package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Categoria;
import com.developergg.app.model.Propietario;

public interface ICategoriasService {
	List<Categoria> buscarTodas();
	Categoria buscarPorId(Integer idCategoria); 
	List<Categoria> buscarPorPropietario(Propietario propietario);
	void guardar(Categoria categoria);
}
