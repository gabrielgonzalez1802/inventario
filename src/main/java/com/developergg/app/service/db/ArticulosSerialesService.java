package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.ArticuloSerial;
import com.developergg.app.repository.ArticulosSerialesRepository;
import com.developergg.app.service.IArticulosSeriales;

@Service
public class ArticulosSerialesService implements IArticulosSeriales {
	
	@Autowired
	private ArticulosSerialesRepository repo;

	@Override
	public List<ArticuloSerial> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public ArticuloSerial buscarPorIdArticuloSerial(Integer idArticuloSerial) {
		Optional<ArticuloSerial> optional = repo.findById(idArticuloSerial);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<ArticuloSerial> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

	@Override
	public List<ArticuloSerial> buscarPorIdAlmacenDesc(Integer idAlmacen) {
		return repo.buscarPorAlmacenYFechaDesc(idAlmacen);
	}

	@Override
	public void guardar(ArticuloSerial articuloSerial) {
		repo.save(articuloSerial);
	}
}
