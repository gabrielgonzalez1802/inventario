package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.ComprobanteFiscal;
import com.developergg.app.model.Propietario;
import com.developergg.app.repository.ComprobantesFiscalesRepository;
import com.developergg.app.service.IComprobantesFiscalesService;

@Service
public class ComprobantesFiscalesServiceJpa implements IComprobantesFiscalesService {
	
	@Autowired
	private ComprobantesFiscalesRepository repo;

	@Override
	public ComprobanteFiscal buscarPorId(Integer idComprobanteFiscal) {
		Optional<ComprobanteFiscal> optional = repo.findById(idComprobanteFiscal);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<ComprobanteFiscal> buscarPorTienda(Propietario tienda) {
		return repo.findByTienda(tienda);
	}

	@Override
	public void guardar(ComprobanteFiscal comprobanteFiscal) {
		repo.save(comprobanteFiscal);
	}

	@Override
	public void eliminar(Integer idComprobanteFiscal) {
		Optional<ComprobanteFiscal> optional = repo.findById(idComprobanteFiscal);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

}
