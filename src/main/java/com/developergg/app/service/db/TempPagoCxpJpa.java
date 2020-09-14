package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Compra;
import com.developergg.app.model.TempPagoCxp;
import com.developergg.app.repository.TempPagoCxpRepository;
import com.developergg.app.service.ITempsPagosCxpService;

@Service
public class TempPagoCxpJpa implements ITempsPagosCxpService{

	@Autowired
	private TempPagoCxpRepository repo;
	
	@Override
	public TempPagoCxp buscarPorId(Integer id) {
		Optional<TempPagoCxp> optional =repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}	
		return null;
	}
	
	@Override
	public List<TempPagoCxp> buscarPorCompra(Compra compra) {
		return repo.findByCompra(compra);
	}

	@Override
	public void guardar(TempPagoCxp tempPagoCxp) {
		repo.save(tempPagoCxp);
		
	}

	@Override
	public void eliminar(Integer id) {
		Optional<TempPagoCxp> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
		
	}

	@Override
	public void eliminar(List<TempPagoCxp> lista) {
		repo.deleteAll(lista);
	}
}
