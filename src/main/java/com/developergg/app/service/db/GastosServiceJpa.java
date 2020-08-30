package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Gasto;
import com.developergg.app.repository.GastosRepository;
import com.developergg.app.service.IGastosService;

@Service
public class GastosServiceJpa implements IGastosService{
	
	@Autowired
	private GastosRepository repo;

	@Override
	public Gasto buscarPorId(Integer id) {
		Optional<Gasto> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Gasto> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

	@Override
	public void guardar(Gasto gasto) {
		repo.save(gasto);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<Gasto> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}
	
}
