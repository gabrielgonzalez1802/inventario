package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Perfil;
import com.developergg.app.repository.PerfilesRepository;
import com.developergg.app.service.IPerfilesService;

@Service
public class PerfilesServiceJpa implements IPerfilesService {
	
	@Autowired
	PerfilesRepository repo;

	@Override
	public List<Perfil> buscarTodos() {
		List<Perfil> perfiles = repo.findAll();
		return perfiles;
	}

	@Override
	public Perfil buscarPorIdPerfil(Integer idPerfil) {
		Optional<Perfil> optional = repo.findById(idPerfil);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

}
