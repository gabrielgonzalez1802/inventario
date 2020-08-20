package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.FacturaDetalleTemp;
import com.developergg.app.model.FacturaSerialTemp;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.repository.FacturasSerialesTempRepository;
import com.developergg.app.service.IFacturasSerialesTempService;

@Service
public class FacturasSerialesTempServiceJpa implements IFacturasSerialesTempService {
	
	@Autowired
	private FacturasSerialesTempRepository repo;

	@Override
	public void guardar(FacturaSerialTemp serialTemp) {
		repo.save(serialTemp);
	}

	@Override
	public List<FacturaSerialTemp> buscarPorDetalleTemp(FacturaDetalleTemp facturaDetalleTemp) {
		return repo.findByIdDetalle(facturaDetalleTemp);
	}
	
	@Override
	public FacturaSerialTemp buscarPorId(Integer idSerial) {
		Optional<FacturaSerialTemp> optional = repo.findById(idSerial);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void eliminar(Integer idSerial) {
		Optional<FacturaSerialTemp> optional = repo.findById(idSerial);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public void eliminarListaSeriales(List<FacturaSerialTemp> lista) {
		repo.deleteAll(lista);
	}

	@Override
	public List<FacturaSerialTemp> buscarPorFacturaTemp(FacturaTemp facturaTemp) {
		return repo.findByFacturaTemp(facturaTemp);
	}
}
