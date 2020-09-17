package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.DevolucionFactura;
import com.developergg.app.model.DevolucionFacturaDetalle;
import com.developergg.app.model.FacturaDetalle;
import com.developergg.app.model.FacturaDetalleTaller;
import com.developergg.app.repository.DevolucionesFacturasDetallesRepository;
import com.developergg.app.service.IDevolucionesFacturasDetallesService;

@Service
public class DevolucionesFacturasDetallesServiceJpa implements IDevolucionesFacturasDetallesService{

	@Autowired
	private DevolucionesFacturasDetallesRepository repo;
	
	@Override
	public DevolucionFacturaDetalle buscarPorId(Integer id) {
		Optional<DevolucionFacturaDetalle> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<DevolucionFacturaDetalle> buscarPorFacturaDetalle(FacturaDetalle facturaDetalle) {
		return repo.findByFacturaDetalle(facturaDetalle);
	}

	@Override
	public List<DevolucionFacturaDetalle> buscarPorDevolucionFactura(DevolucionFactura devolucionFactura) {
		return repo.findByDevolucionFactura(devolucionFactura);
	}

	@Override
	public void guardar(DevolucionFacturaDetalle devolucionFacturaDetalle) {
		repo.save(devolucionFacturaDetalle);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<DevolucionFacturaDetalle> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public List<DevolucionFacturaDetalle> buscarPorFacturaDetalleTaller(FacturaDetalleTaller facturaDetalleTaller) {
		return repo.findByFacturaDetalleTaller(facturaDetalleTaller);
	}

}
