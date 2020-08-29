package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.AbonoCxC;
import com.developergg.app.model.AbonoCxCDetalle;
import com.developergg.app.repository.AbonosCxCDetallesRepository;
import com.developergg.app.service.IAbonosCxCDetallesService;

@Service
public class AbonosCxCDetallesServiceJpa implements IAbonosCxCDetallesService{
	
	@Autowired
	private AbonosCxCDetallesRepository repo;

	@Override
	public AbonoCxCDetalle buscarPorId(Integer id) {
		Optional<AbonoCxCDetalle> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<AbonoCxCDetalle> buscarPorIngreso(AbonoCxC ingreso) {
		return repo.findByIngreso(ingreso);
	}

	@Override
	public void guardar(AbonoCxCDetalle abonoCxCDetalle) {
		repo.save(abonoCxCDetalle);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<AbonoCxCDetalle> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

}
