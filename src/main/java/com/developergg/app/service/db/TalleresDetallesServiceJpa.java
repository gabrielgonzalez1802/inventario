package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Taller;
import com.developergg.app.model.TallerDetalle;
import com.developergg.app.repository.TalleresDetallesRepository;
import com.developergg.app.service.ITalleresDetallesService;

@Service
public class TalleresDetallesServiceJpa implements ITalleresDetallesService{
	
	@Autowired
	private TalleresDetallesRepository repo;

	@Override
	public TallerDetalle buscarPorId(Integer id) {
		Optional<TallerDetalle> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<TallerDetalle> buscarPorTaller(Taller taller) {
		return repo.findByTaller(taller);
	}

	@Override
	public void guardar(TallerDetalle tallerDetalle) {
		repo.save(tallerDetalle);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<TallerDetalle> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public void eliminar(TallerDetalle tallerDetalle) {
		repo.delete(tallerDetalle);
	}

}
