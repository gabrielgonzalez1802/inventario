package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.CondicionPago;
import com.developergg.app.repository.CondicionesPagosRepository;
import com.developergg.app.service.ICondicionesPagoService;

@Service
public class CondicionesPagosServiceJpa implements ICondicionesPagoService {
	
	@Autowired
	private CondicionesPagosRepository repo;

	@Override
	public List<CondicionPago> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public CondicionPago buscarPorId(Integer idCondicionPago) {
		Optional<CondicionPago> optional = repo.findById(idCondicionPago);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void guardar(CondicionPago condicionPago) {
		repo.save(condicionPago);
	}

	@Override
	public void eliminar(Integer idCondicionPago) {
		Optional<CondicionPago> optional = repo.findById(idCondicionPago);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

}
