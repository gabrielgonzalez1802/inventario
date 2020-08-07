package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloAjuste;
import com.developergg.app.repository.ArticulosAjustesRepository;
import com.developergg.app.service.IArticulosAjustesService;

@Service
public class ArticulosAjustesServiceJpa implements IArticulosAjustesService{
	
	@Autowired
	private ArticulosAjustesRepository repo;

	@Override
	public List<ArticuloAjuste> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public ArticuloAjuste buscarPorId(Integer idArticuloAjuste) {
		Optional<ArticuloAjuste> optional = repo.findById(idArticuloAjuste);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void guardar(ArticuloAjuste articuloAjuste) {
		repo.save(articuloAjuste);
	}

	@Override
	public List<ArticuloAjuste> buscarPorArticuloYAlmacen(Articulo articulo, Almacen almacen) {
		return repo.findByArticuloAndAlmacen(articulo, almacen);
	} 

}
