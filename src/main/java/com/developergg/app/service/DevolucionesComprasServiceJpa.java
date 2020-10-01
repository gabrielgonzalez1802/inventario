package com.developergg.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Compra;
import com.developergg.app.model.DevolucionCompra;
import com.developergg.app.repository.DevolucionesComprasRepository;

@Service
public class DevolucionesComprasServiceJpa implements IDevolucionesComprasService{
	
	@Autowired
	private DevolucionesComprasRepository repo;

	@Override
	public DevolucionCompra buscarPorId(Integer id) {
		Optional<DevolucionCompra> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<DevolucionCompra> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

	@Override
	public List<DevolucionCompra> buscarPorCompra(Compra compra) {
		return repo.findByCompra(compra);
	}

	@Override
	public void guardar(DevolucionCompra devolucionCompra) {
		repo.save(devolucionCompra);
	}

	@Override
	public void eliminar(DevolucionCompra devolucionCompra) {
		repo.delete(devolucionCompra);
	}

}
