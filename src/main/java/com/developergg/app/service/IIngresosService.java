package com.developergg.app.service;

import java.util.Date;
import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Ingreso;
import com.developergg.app.model.Usuario;

public interface IIngresosService {
	Ingreso buscarPorId(Integer id);
	List<Ingreso> buscarPorAlmacen(Almacen almacen);
	void guardar(Ingreso ingreso);
	void eliminar(Integer id);
	List<Ingreso> buscarPorUsuariosAlmacenFechas(List<Usuario> usuarios, 
			Almacen almacen, Date desde, Date hasta);
}
