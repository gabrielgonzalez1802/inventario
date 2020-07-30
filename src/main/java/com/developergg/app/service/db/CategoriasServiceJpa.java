package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Categoria;
import com.developergg.app.model.Propietario;
import com.developergg.app.repository.CategoriasRepository;
import com.developergg.app.service.ICategoriasService;

@Service
public class CategoriasServiceJpa implements ICategoriasService {
	
	@Autowired
	private CategoriasRepository repo;

	@Override
	public List<Categoria> buscarTodas() {
		return repo.findAll();
	}

	@Override
	public Categoria buscarPorId(Integer idCategoria) {
		Optional<Categoria> optional = repo.findById(idCategoria);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void guardar(Categoria categoria) {
		repo.save(categoria);
	}

	@Override
	public List<Categoria> buscarPorPropietario(Propietario propietario) {
		return repo.findByTienda(propietario);
	}
}
