package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.DevolucionFactura;
import com.developergg.app.model.Factura;
import com.developergg.app.repository.DevolucionesFacturasRepository;
import com.developergg.app.service.IDevolucionesFacturasService;

@Service
public class DevolucionesFacturasServiceJpa implements IDevolucionesFacturasService{

	@Autowired
	private DevolucionesFacturasRepository repo;
	
	@Override
	public DevolucionFactura buscarPorId(Integer id) {
		Optional<DevolucionFactura> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<DevolucionFactura> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

	@Override
	public void guardar(DevolucionFactura devolucionFactura) {
		repo.save(devolucionFactura);
	}

	@Override
	public void eliminar(DevolucionFactura devolucionFactura) {
		repo.delete(devolucionFactura);
	}

	@Override
	public List<DevolucionFactura> buscarPorFactura(Factura factura) {
		return repo.findByFactura(factura);
	}

}
