package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Perfil;
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

	@Override
	public Usuario buscarPorId(Integer idUsuario) {
		Optional<Usuario> optional = repo.findById(idUsuario);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Usuario> buscarPorAlmacenAndPerfil(Almacen almacen, Perfil privilegio) {
		return repo.findByAlmacenAndPrivilegio(almacen, privilegio);
	}

}
