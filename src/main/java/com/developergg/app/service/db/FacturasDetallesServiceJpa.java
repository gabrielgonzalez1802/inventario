package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetalle;
import com.developergg.app.repository.FacturasDetallesRepository;
import com.developergg.app.service.IFacturasDetallesService;

@Service
public class FacturasDetallesServiceJpa implements IFacturasDetallesService{
	
	@Autowired
	private FacturasDetallesRepository repo;

	@Override
	public FacturaDetalle buscarPorId(Integer id) {
		Optional<FacturaDetalle> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<FacturaDetalle> buscarPorFactura(Factura factura) {
		return repo.findByFactura(factura);
	}

	@Override
	public void guardar(FacturaDetalle facturaDetalle) {
		repo.save(facturaDetalle);
	}

	@Override	
	public void eliminar(Integer id) {
		Optional<FacturaDetalle> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

}
