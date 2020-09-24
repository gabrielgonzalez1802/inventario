package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.Taller;
import com.developergg.app.model.Usuario;

public interface ITalleresService {
	List<Taller> buscarPorAlmacen(Almacen almacen);
	List<Taller> buscarPorFacturaTemp(FacturaTemp facturaTemp);
	List<Taller> buscarPorAlmacenUsuario(Almacen almacen, List<Usuario> usuario);
	Taller buscarPorId(Integer id);
	void guardar(Taller taller);
	void eliminar(Integer id);
	void eliminar(Taller taller);
}
