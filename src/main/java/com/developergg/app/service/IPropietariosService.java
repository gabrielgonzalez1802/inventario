package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Propietario;

public interface IPropietariosService {
	List<Propietario> buscarTodos();
	Propietario buscarPorId(Integer idPropietario);
	Propietario buscarPorIdUsuario(Integer idUsuario);
	void guardar(Propietario propietario);
}
