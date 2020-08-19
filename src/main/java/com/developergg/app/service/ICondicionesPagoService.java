package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.CondicionPago;

public interface ICondicionesPagoService {
	List<CondicionPago> buscarTodos();
	CondicionPago buscarPorId(Integer idCondicionPago);
	CondicionPago buscarPorCondicionPago(String condicionPago);
	void guardar(CondicionPago condicionPago);
	void eliminar(Integer idCondicionPago);
}
