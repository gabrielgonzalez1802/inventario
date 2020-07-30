package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Propietario;
import com.developergg.app.repository.PropietariosRepository;
import com.developergg.app.service.IPropietariosService;

@Service
public class PropietariosServiceJpa implements IPropietariosService{
	
	@Autowired
	private PropietariosRepository repo;

	@Override
	public List<Propietario> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public Propietario buscarPorId(Integer idPropietario) {
		Optional<Propietario> optional = repo.findById(idPropietario);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Propietario buscarPorIdUsuario(Integer idUsuario) {
		return repo.buscarPorIdUsuario(idUsuario);
	}

	@Override
	public void guardar(Propietario propietario) {
		repo.save(propietario);
	}
}
