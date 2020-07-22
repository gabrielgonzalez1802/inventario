package com.developergg.app.service.db;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.CodigoActivacion;
import com.developergg.app.repository.CodigoActivacionRepository;
import com.developergg.app.service.ICodigoActivacionService;

@Service
public class CodigoActivacionServiceJpa implements ICodigoActivacionService{
	
	@Autowired
	private CodigoActivacionRepository repo;
	
	@Override
	public List<CodigoActivacion> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public CodigoActivacion buscarPorCodigo(String codigo) {
		return repo.findByCodigo(codigo);
	}

}
