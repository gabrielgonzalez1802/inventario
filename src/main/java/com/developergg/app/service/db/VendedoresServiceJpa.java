package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Vendedor;
import com.developergg.app.repository.VendedoresRepository;
import com.developergg.app.service.IVendedoresService;

@Service
public class VendedoresServiceJpa implements IVendedoresService {
	
	@Autowired
	private VendedoresRepository repo;

	@Override
	public List<Vendedor> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public List<Vendedor> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

	@Override
	public Vendedor buscarPorIdVendedor(Integer idVendedor) {
		Optional<Vendedor> optional = repo.findById(idVendedor);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void guardar(Vendedor vendedor) {
		repo.save(vendedor);
	}

}
