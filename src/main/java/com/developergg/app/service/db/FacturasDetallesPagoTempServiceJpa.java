package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.FacturaDetallePagoTemp;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.repository.FacturasDetallesPagoTempRepository;
import com.developergg.app.service.IFacturasDetallesPagoTempService;

@Service
public class FacturasDetallesPagoTempServiceJpa implements IFacturasDetallesPagoTempService {
	
	@Autowired
	private FacturasDetallesPagoTempRepository repo;

	@Override
	public List<FacturaDetallePagoTemp> buscarPorFacturaTemp(FacturaTemp facturaTemp) {
		return repo.findByFacturaTemp(facturaTemp);
	}

	@Override
	public FacturaDetallePagoTemp buscarPorId(Integer facturaDetallePagoTempId) {
		Optional<FacturaDetallePagoTemp> optional = repo.findById(facturaDetallePagoTempId);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void guardar(FacturaDetallePagoTemp facturaDetallePagoTemp) {
		repo.save(facturaDetallePagoTemp);
	}

	@Override
	public void eliminar(Integer facturaDetallePagoTempId) {
		Optional<FacturaDetallePagoTemp> optional = repo.findById(facturaDetallePagoTempId);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public void eliminarListaPagos(List<FacturaDetallePagoTemp> lista) {
		repo.deleteAll(lista);
	}

}
