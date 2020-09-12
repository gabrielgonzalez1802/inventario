package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Compra;
import com.developergg.app.model.CompraDetalle;

public interface IComprasDetallesService {
	CompraDetalle buscarPorId(Integer id);
	List<CompraDetalle> buscarPorCompra(Compra compra);
	void guardar(CompraDetalle compraDetalle);
	void eliminar(Integer id);
	void elminar(CompraDetalle compraDetalle);
	void eliminar(List<CompraDetalle> listaCompraDetalle);
}
