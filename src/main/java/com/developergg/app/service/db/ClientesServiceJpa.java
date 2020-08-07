package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Cliente;
import com.developergg.app.repository.ClientesRepository;
import com.developergg.app.service.IClientesService;

@Service
public class ClientesServiceJpa implements IClientesService {
	
	@Autowired
	private ClientesRepository repo;

	@Override
	public List<Cliente> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public List<Cliente> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

	@Override
	public Cliente buscarPorIdCliente(Integer idCliente) {
		Optional<Cliente> optional = repo.findById(idCliente);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void guardar(Cliente cliente) {
		repo.save(cliente);
	}

}
