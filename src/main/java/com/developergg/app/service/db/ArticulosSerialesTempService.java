package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloSerialTemp;
import com.developergg.app.repository.ArticulosSerialesTempRepository;
import com.developergg.app.service.IArticulosSerialesTemp;

@Service
public class ArticulosSerialesTempService implements IArticulosSerialesTemp {
	
	@Autowired
	private ArticulosSerialesTempRepository repo;

	@Override
	public List<ArticuloSerialTemp> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public ArticuloSerialTemp buscarPorIdArticuloSerialTemp(Integer idArticuloSerialTemp) {
		Optional<ArticuloSerialTemp> optional = repo.findById(idArticuloSerialTemp);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<ArticuloSerialTemp> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

	@Override
	public List<ArticuloSerialTemp> buscarPorIdAlmacenDesc(Integer idAlmacen) {
		return repo.buscarPorAlmacenYFechaDesc(idAlmacen);
	}

	@Override
	public void guardar(ArticuloSerialTemp ArticuloSerialTemp) {
		repo.save(ArticuloSerialTemp);
	}

	@Override
	public List<ArticuloSerialTemp> buscarPorArticuloAlmacen(Articulo articulo, Almacen almacen) {
		return repo.findByArticuloAndAlmacen(articulo, almacen);
	}

	@Override
	public ArticuloSerialTemp buscarPorSerial(String serial) {
		return repo.findBySerial(serial);
	}

	@Override
	public List<ArticuloSerialTemp> buscarPorSerialAndAlmacen(String serial, Almacen almacen) {
		return repo.findBySerialAndAlmacen(serial, almacen);
	}

	@Override
	public void eliminar(ArticuloSerialTemp ArticuloSerialTemp) {
		repo.delete(ArticuloSerialTemp);
	}

	@Override
	public void eliminar(List<ArticuloSerialTemp> seriales) {
		repo.deleteAll(seriales);
	}
}
