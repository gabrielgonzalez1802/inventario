package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Perfil;

public interface IPerfilesService{
	List<Perfil> buscarTodos();
	Perfil buscarPorPerfil(String perfil);
	Perfil buscarPorIdPerfil(Integer idPerfil);
}
