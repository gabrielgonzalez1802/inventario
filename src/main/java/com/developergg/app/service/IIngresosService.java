package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Ingreso;

public interface IIngresosService {
	Ingreso buscarPorId(Integer id);
	List<Ingreso> buscarPorAlmacen(Almacen almacen);
	void guardar(Ingreso ingreso);
	void eliminar(Integer id);
}
