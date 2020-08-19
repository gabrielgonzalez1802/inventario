package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.TipoEquipo;
import com.developergg.app.repository.TiposEquipoRepository;
import com.developergg.app.service.ITiposEquipoService;

@Service
public class TiposEquipoServiceJpa implements ITiposEquipoService {
	
	@Autowired
	private TiposEquipoRepository repo;

	@Override
	public List<TipoEquipo> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public TipoEquipo buscarPorId(Integer id) {
		Optional<TipoEquipo> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void guardar(TipoEquipo tipoEquipo) {
		repo.save(tipoEquipo);
	}

	@Override
	public void eliminar(Integer id) {		
		Optional<TipoEquipo> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

}
