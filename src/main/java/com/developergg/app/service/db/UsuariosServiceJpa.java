package com.developergg.app.service.db;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Usuario;
import com.developergg.app.repository.UsuariosRepository;
import com.developergg.app.service.IUsuariosService;

@Service
public class UsuariosServiceJpa implements IUsuariosService{
	
	@Autowired
	private UsuariosRepository repo;

	@Override
	public void guardar(Usuario usuario) {
		repo.save(usuario);
	}

	@Override
	public void eliminar(Integer idUsuario) {
		repo.deleteById(idUsuario);
	}

	@Override
	public List<Usuario> buscarTodos() {
		List<Usuario> lista = repo.findAll();
		return lista;
	}

	@Override
	public Usuario buscarPorUsername(String username) {
		return repo.findByUsername(username);
	}

}
