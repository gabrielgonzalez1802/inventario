package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.Taller;
import com.developergg.app.model.TallerArticulo;
import com.developergg.app.repository.TalleresArticulosRepository;
import com.developergg.app.service.ITalleresArticulosService;

@Service
public class TalleresArticulosServiceJpa implements ITalleresArticulosService{
	
	@Autowired
	private TalleresArticulosRepository repo;

	@Override
	public TallerArticulo buscarPorId(Integer id) {
		Optional<TallerArticulo> optional = repo.findById(id);
		if(optional.isPresent()){
			return optional.get();
		}
		return null;
	}

	@Override
	public List<TallerArticulo> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

	@Override
	public List<TallerArticulo> buscarPorArticuloAlmacen(Articulo articulo, Almacen almacen) {
		return repo.findByArticuloAndAlmacen(articulo, almacen);
	}

	@Override
	public List<TallerArticulo> buscarPorTaller(Taller taller) {
		return repo.findByTaller(taller);
	}

	@Override
	public void guardar(TallerArticulo tallerArticulo) {
		repo.save(tallerArticulo);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<TallerArticulo> optional = repo.findById(id);
		if(optional.isPresent()){
			repo.delete(optional.get());
		}
	}

}
