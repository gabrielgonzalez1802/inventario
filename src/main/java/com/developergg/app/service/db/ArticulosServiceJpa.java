package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Articulo;
import com.developergg.app.repository.ArticulosRepository;
import com.developergg.app.service.IArticulosService;

@Service
@Primary
public class ArticulosServiceJpa implements IArticulosService{
	
	@Autowired
	private ArticulosRepository repo;

	@Override
	public List<Articulo> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public Articulo buscarPorId(Integer idArticulo) {
		Optional<Articulo> optional = repo.findById(idArticulo);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void guardar(Articulo articulo) {
		repo.save(articulo);
	}

	@Override
	public void eliminar(Integer idArticulo) {
		Optional<Articulo> optional = repo.findById(idArticulo);
		if(optional.isPresent()) {
			 repo.delete(optional.get());
		}
	}

}
