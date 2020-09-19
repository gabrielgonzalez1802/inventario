package com.developergg.app.service;

import java.util.Date;
import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Gasto;
import com.developergg.app.model.Usuario;

public interface IGastosService {
	Gasto buscarPorId(Integer id);
	List<Gasto> buscarPorAlmacen(Almacen almacen);
	void guardar(Gasto gasto);
	void eliminar(Integer id);
	List<Gasto> buscarPorUsuariosAlmacenFechas(List<Usuario> usuarios, 
			Almacen almacen, Date desde, Date hasta);
}
