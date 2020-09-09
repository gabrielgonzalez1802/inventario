package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Factura;
import com.developergg.app.model.Taller;
import com.developergg.app.repository.FacturasRepository;
import com.developergg.app.service.IFacturasService;

@Service
public class FacturasServiceJpa implements IFacturasService {
	
	@Autowired
	private FacturasRepository repo;

	@Override
	public Factura buscarPorId(Integer id) {
		Optional<Factura> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void guardar(Factura factura) {
		repo.save(factura);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<Factura> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public List<Factura> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

	@Override
	public Factura buscarPorTaller(Taller taller) {
		return repo.findByTaller(taller);
	}

}
