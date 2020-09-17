package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.DevolucionFacturaSerial;
import com.developergg.app.model.FacturaDetalle;
import com.developergg.app.model.FacturaDetalleSerial;
import com.developergg.app.repository.DevolucionesFacturasSerialsRepository;
import com.developergg.app.service.IDevolucionesFacturasSerialesService;

@Service
public class DevolucionesFacturasSerialesServiceJpa implements IDevolucionesFacturasSerialesService{

	@Autowired
	private DevolucionesFacturasSerialsRepository repo;
	
	@Override
	public DevolucionFacturaSerial buscarPorId(Integer id) {
		Optional<DevolucionFacturaSerial> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<DevolucionFacturaSerial> buscarPorFacturaDetalle(FacturaDetalle facturaDetalle) {
		return repo.findByFacturaDetalle(facturaDetalle);
	}

	@Override
	public List<DevolucionFacturaSerial> buscarPorFacturaDetalleSerial(FacturaDetalleSerial facturaDetalleSerial) {
		return repo.findByFacturaDetalleSerial(facturaDetalleSerial);
	}
	
	@Override
	public void guardar(DevolucionFacturaSerial devolucionFacturaSerial) {
		repo.save(devolucionFacturaSerial);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<DevolucionFacturaSerial> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}
}
