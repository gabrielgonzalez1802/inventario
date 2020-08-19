package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.FormaPago;
import com.developergg.app.repository.FormasPagoRepository;
import com.developergg.app.service.IFormasPagoService;

@Service
public class FormasPagoServiceJpa implements IFormasPagoService {
	
	@Autowired
	private FormasPagoRepository repo;

	@Override
	public List<FormaPago> buscarTodas() {
		return repo.findAll();
	}

	@Override
	public FormaPago buscarPorId(Integer idFormaPago) {
		Optional<FormaPago> optional = repo.findById(idFormaPago);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void guardar(FormaPago formaPago) {
		repo.save(formaPago);
	}

	@Override
	public void eliminar(Integer idFormaPago) {
		Optional<FormaPago> optional = repo.findById(idFormaPago);
		if(optional.isPresent()) {
			repo.save(optional.get());
		}
	}

	@Override
	public List<FormaPago> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

}
