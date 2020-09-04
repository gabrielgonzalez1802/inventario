package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetalleTaller;
import com.developergg.app.repository.FacturasDetallesTallerRepository;
import com.developergg.app.service.IFacturasDetallesTallerService;

@Service
public class FacturasDetallesTallerServiceJpa implements IFacturasDetallesTallerService{
	
	@Autowired
	private FacturasDetallesTallerRepository repo;

	@Override
	public FacturaDetalleTaller buscarPorId(Integer id) {
		Optional<FacturaDetalleTaller> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<FacturaDetalleTaller> buscarPorFactura(Factura factura) {
		return repo.findByFactura(factura);
	}

	@Override
	public void guardar(FacturaDetalleTaller facturaDetalleTaller) {
		repo.save(facturaDetalleTaller);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<FacturaDetalleTaller> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public void eliminar(FacturaDetalleTaller facturaDetalleTaller) {
		repo.delete(facturaDetalleTaller);
	}

}
