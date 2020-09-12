package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.CompraDetalle;
import com.developergg.app.model.CompraDetalleSerial;

public interface IComprasDetallesSerialesService {
	CompraDetalleSerial buscarPorId(Integer id);
	List<CompraDetalleSerial> buscarPorCompraDetalle(CompraDetalle compraDetalle);
	void guardar(CompraDetalleSerial compraDetalleSerial);
	void eliminar(Integer CompraDetalleSerial);
	void eliminar(CompraDetalleSerial compraDetalleSerial);
	void eliminar(List<CompraDetalleSerial> listaCompraDetalleSerials);
}
