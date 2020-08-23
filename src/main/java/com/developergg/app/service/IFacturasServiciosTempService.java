package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.FacturaServicioTemp;
import com.developergg.app.model.Usuario;

public interface IFacturasServiciosTempService {
	void guardar(FacturaServicioTemp servicio);
	List<FacturaServicioTemp> buscarPorUsuarioAlmacen(Usuario usuario, Almacen almacen);
	void eliminarListaServicios(List<FacturaServicioTemp> lista);
	void eliminar(Integer idServicio);
}
