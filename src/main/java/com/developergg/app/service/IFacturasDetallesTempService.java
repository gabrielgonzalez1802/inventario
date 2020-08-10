package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.FacturaDetalleTemp;
import com.developergg.app.model.Usuario;

public interface IFacturasDetallesTempService {
	void guardar(FacturaDetalleTemp facturaDetalle);
	List<FacturaDetalleTemp> buscarPorUsuarioAlmacen(Usuario usuario, Almacen almacen);
	FacturaDetalleTemp buscarPorId(Integer idFacturaDetalle);
	void eliminar(Integer idFacturaDetalle);
}
