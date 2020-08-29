package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.AbonoCxC;
import com.developergg.app.model.Factura;
import com.developergg.app.repository.AbonosCxCRepository;
import com.developergg.app.service.IAbonosCxCService;

@Service
public class AbonosCxCServiceJpa implements IAbonosCxCService{
	
	@Autowired
	private AbonosCxCRepository repo;

	@Override
	public AbonoCxC buscarPorId(Integer id) {
		Optional<AbonoCxC> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<AbonoCxC> buscarPorFactura(Factura factura) {
		return repo.findByFactura(factura);
	}

	@Override
	public void guardar(AbonoCxC abonoCxC) {
		repo.save(abonoCxC);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<AbonoCxC> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

}
