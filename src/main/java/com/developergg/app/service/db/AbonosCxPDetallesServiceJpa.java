package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.AbonoCxP;
import com.developergg.app.model.AbonoCxPDetalle;
import com.developergg.app.repository.AbonosCxPDetallesRepository;
import com.developergg.app.service.IAbonosCxPDetallesService;

@Service
public class AbonosCxPDetallesServiceJpa implements IAbonosCxPDetallesService{
	
	@Autowired
	private AbonosCxPDetallesRepository repo;

	@Override
	public AbonoCxPDetalle buscarPorId(Integer id) {
		Optional<AbonoCxPDetalle> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<AbonoCxPDetalle> buscarPorIngreso(AbonoCxP ingreso) {
		return repo.findByIngreso(ingreso);
	}

	@Override
	public void guardar(AbonoCxPDetalle abonoCxPDetalle) {
		repo.save(abonoCxPDetalle);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<AbonoCxPDetalle> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public void eliminar(AbonoCxPDetalle abonoCxPDetalle) {
		repo.delete(abonoCxPDetalle);
	}

	@Override
	public void eliminar(List<AbonoCxPDetalle> lista) {
		repo.deleteAll(lista);
	}

}
