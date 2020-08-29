package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaPagoTemp;
import com.developergg.app.repository.FacturasPagosTempRepository;
import com.developergg.app.service.IFacturasPagoTempService;

@Service
public class FacturasPagosTempServiceJpa implements IFacturasPagoTempService{
	
	@Autowired
	private FacturasPagosTempRepository repo;

	@Override
	public FacturaPagoTemp buscarPorId(Integer id) {
		Optional<FacturaPagoTemp> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<FacturaPagoTemp> buscarPorFactura(Factura factura) {
		return repo.findByFactura(factura);
	}

	@Override
	public void guardar(FacturaPagoTemp facturaPago) {
		repo.save(facturaPago);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<FacturaPagoTemp> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public void eliminarAll(List<FacturaPagoTemp> pagosTemp) {
		repo.deleteAll(pagosTemp);
	}
	
}
