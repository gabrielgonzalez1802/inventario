package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.FormaPago;

public interface IFormasPagoService {
	List<FormaPago> buscarTodas();
	List<FormaPago> buscarPorAlmacen(Almacen almacen);
	FormaPago buscarPorId(Integer idFormaPago);
	void guardar(FormaPago formaPago);
	void eliminar(Integer idFormaPago);
}
