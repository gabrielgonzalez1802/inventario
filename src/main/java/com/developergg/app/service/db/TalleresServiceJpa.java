package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Taller;
import com.developergg.app.repository.TalleresRepository;
import com.developergg.app.service.ITalleresService;

@Service
public class TalleresServiceJpa implements ITalleresService {
	
	@Autowired
	private TalleresRepository repo;

	@Override
	public List<Taller> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

	@Override
	public Taller buscarPorId(Integer id) {
		Optional<Taller> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void guardar(Taller taller) {
		repo.save(taller);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<Taller> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

}
