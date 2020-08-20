package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetalleServicio;
import com.developergg.app.repository.FacturasDetallesServicioRepository;
import com.developergg.app.service.IFacturasDetallesServiciosService;

@Service
public class FacturasDetallesServiciosServiceJpa implements IFacturasDetallesServiciosService {

	@Autowired
	private FacturasDetallesServicioRepository repo;
	
	@Override
	public FacturaDetalleServicio buscarPorId(Integer id) {
		Optional<FacturaDetalleServicio> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<FacturaDetalleServicio> buscarPorFactura(Factura factura) {
		return repo.findByFactura(factura);
	}

	@Override
	public void guardar(FacturaDetalleServicio facturaDetalleServicio) {
		repo.save(facturaDetalleServicio);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<FacturaDetalleServicio> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

}
