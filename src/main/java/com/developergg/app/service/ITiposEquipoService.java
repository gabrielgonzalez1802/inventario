package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.TipoEquipo;

public interface ITiposEquipoService {
	List<TipoEquipo> buscarTodos();
	TipoEquipo buscarPorId(Integer id);
	void guardar(TipoEquipo tipoEquipo);
	void eliminar(Integer id);
}
