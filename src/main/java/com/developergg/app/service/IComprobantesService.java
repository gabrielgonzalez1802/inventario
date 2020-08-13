package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Comprobante;
import com.developergg.app.model.Propietario;

public interface IComprobantesService {
	List<Comprobante> buscarPorTienda(Propietario tienda);
	Comprobante buscarPorId(Integer idSecuencia);
	void guardar(Comprobante comprobante);
	void eliminar(Integer idSecuencia);
}
