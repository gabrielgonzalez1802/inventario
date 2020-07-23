package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.repository.AlmacenesRepository;
import com.developergg.app.service.IAlmacenesService;

@Service
public class AlmacenesServiceJpa implements IAlmacenesService{
	
	@Autowired
	private AlmacenesRepository repo;

	@Override
	public List<Almacen> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public Almacen buscarPorId(Integer idAlmacen) {
		Optional<Almacen> optional = repo.findById(idAlmacen);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
	@Override
	public List<Almacen> buscarPorIdTienda(Integer idTienda) {
		return repo.buscarPorIdTienda(idTienda);
	}


	@Override
	public void guardar(Almacen almacen) {
		repo.save(almacen);
	}

	@Override
	public void eliminar(Integer idAlmacen) {
		repo.deleteById(idAlmacen);
	}
}
