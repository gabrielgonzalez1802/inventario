package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Perfil;
import com.developergg.app.model.Usuario;

public interface IUsuariosService {
	void guardar(Usuario usuario);
	void eliminar(Integer idUsuario);
	List<Usuario> buscarTodos();
	List<Usuario> buscarPorAlmacenAndPerfil(Almacen almacen, Perfil perfil);
	Usuario buscarPorUsername(String username);
	Usuario buscarPorId(Integer idUsuario);
}
