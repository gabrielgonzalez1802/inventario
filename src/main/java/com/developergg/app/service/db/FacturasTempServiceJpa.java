package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.Usuario;
import com.developergg.app.repository.FacturasTempRepository;
import com.developergg.app.service.IFacturasTempService;

@Service
public class FacturasTempServiceJpa implements IFacturasTempService {
	
	@Autowired
	private FacturasTempRepository repo;

	@Override
	public List<FacturaTemp> buscarTodas() {
		return repo.findAll();
	}

	@Override
	public FacturaTemp buscarPorId(Integer idFacturaTemp) {
		Optional<FacturaTemp> optional = repo.findById(idFacturaTemp);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public FacturaTemp buscarPorUsuario(Usuario usuario) {
		return repo.findByUsuario(usuario);
	}

	@Override
	public void guardar(FacturaTemp facturaTemp) {
		repo.save(facturaTemp);
	}

	@Override
	public void eliminar(Integer idFacturaTemp) {
		Optional<FacturaTemp> optional = repo.findById(idFacturaTemp);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

}
