package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Cliente;

public interface IClientesService {
	List<Cliente> buscarTodos();
	List<Cliente> buscarPorAlmacen(Almacen almacen);
	Cliente buscarPorIdCliente(Integer idCliente);
	void guardar(Cliente cliente);
}
