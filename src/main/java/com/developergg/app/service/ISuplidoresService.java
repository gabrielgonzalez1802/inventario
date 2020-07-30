package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Suplidor;

public interface ISuplidoresService {
	/**
	 * Retorna una lista de suplidores por almacen cuyo estatus no sea eliminado
	 * @param almacen
	 * @return List<Suplidor>
	 */
	List<Suplidor> buscarPorAlmacenDisponible(Almacen almacen);
	void guardar(Suplidor suplidor);
	Suplidor buscarPorId(Integer idSuplidor);
}
