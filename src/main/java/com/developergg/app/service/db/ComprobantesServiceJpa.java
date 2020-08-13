package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Comprobante;
import com.developergg.app.model.Propietario;
import com.developergg.app.repository.ComprobantesRepository;
import com.developergg.app.service.IComprobantesService;

@Service
public class ComprobantesServiceJpa implements IComprobantesService {
	
	@Autowired
	private ComprobantesRepository repo;

	@Override
	public List<Comprobante> buscarPorTienda(Propietario tienda) {
		return repo.findByPropietario(tienda);
	}

	@Override
	public Comprobante buscarPorId(Integer idSecuencia) {
		Optional<Comprobante> optional = repo.findById(idSecuencia);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void guardar(Comprobante comprobante) {
		repo.save(comprobante);
	}

	@Override
	public void eliminar(Integer idSecuencia) {
		Optional<Comprobante> optional = repo.findById(idSecuencia);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

}
