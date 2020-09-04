package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.Taller;

public interface ITalleresService {
	List<Taller> buscarPorAlmacen(Almacen almacen);
	List<Taller> buscarPorFacturaTemp(FacturaTemp facturaTemp);
	Taller buscarPorId(Integer id);
	void guardar(Taller taller);
	void eliminar(Integer id);
	void eliminar(Taller taller);
}
