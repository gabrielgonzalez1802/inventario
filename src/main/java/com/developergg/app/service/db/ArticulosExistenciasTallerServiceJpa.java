package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloExistenciaTaller;
import com.developergg.app.repository.ArticulosExistenciasTallerRepository;
import com.developergg.app.service.IArticulosExistenciasTallerService;

@Service
public class ArticulosExistenciasTallerServiceJpa implements IArticulosExistenciasTallerService{
	
	@Autowired
	private ArticulosExistenciasTallerRepository repo;

	@Override
	public List<ArticuloExistenciaTaller> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

	@Override
	public List<ArticuloExistenciaTaller> buscarPorArticuloAndAlmacen(Articulo articulo, Almacen almacen) {
		return repo.findByArticuloAndAlmacen(articulo, almacen);
	}

	@Override
	public List<ArticuloExistenciaTaller> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public ArticuloExistenciaTaller buscarPorId(Integer id) {
		Optional<ArticuloExistenciaTaller> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void guardar(ArticuloExistenciaTaller articuloExistenciaTaller) {
		repo.save(articuloExistenciaTaller);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<ArticuloExistenciaTaller> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

}
