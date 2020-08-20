package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaPago;
import com.developergg.app.repository.FacturasPagosRepository;
import com.developergg.app.service.IFacturasPagoService;

@Service
public class FacturasPagosServiceJpa implements IFacturasPagoService{
	
	@Autowired
	private FacturasPagosRepository repo;

	@Override
	public FacturaPago buscarPorId(Integer id) {
		Optional<FacturaPago> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<FacturaPago> buscarPorFactura(Factura factura) {
		return repo.findByFactura(factura);
	}

	@Override
	public void guardar(FacturaPago facturaPago) {
		repo.save(facturaPago);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<FacturaPago> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}
	
}
