package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Vendedor;

public interface IVendedoresService {
	List<Vendedor> buscarTodos();
	List<Vendedor> buscarPorAlmacen(Almacen almacen);
	Vendedor buscarPorIdVendedor(Integer idVendedor);
	void guardar(Vendedor vendedor);
}
