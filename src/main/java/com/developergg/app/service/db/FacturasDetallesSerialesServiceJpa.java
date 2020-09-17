package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.FacturaDetalle;
import com.developergg.app.model.FacturaDetalleSerial;
import com.developergg.app.repository.FacturasDetallesSerialesRepository;
import com.developergg.app.service.IFacturasDetallesSerialesService;

@Service
public class FacturasDetallesSerialesServiceJpa implements IFacturasDetallesSerialesService{

	@Autowired
	private FacturasDetallesSerialesRepository repo;
	
	@Override
	public FacturaDetalleSerial buscarPorId(Integer id) {
		Optional<FacturaDetalleSerial> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<FacturaDetalleSerial> buscarPorDetalle(FacturaDetalle facturaDetalle) {
		return repo.findByFacturaDetalle(facturaDetalle);
	}

	@Override
	public void guardar(FacturaDetalleSerial facturaDetalleSerial) {
		repo.save(facturaDetalleSerial);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<FacturaDetalleSerial> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

}
