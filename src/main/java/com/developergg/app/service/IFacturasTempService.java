package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.Usuario;

public interface IFacturasTempService {
	List<FacturaTemp> buscarTodas();
	FacturaTemp buscarPorId(Integer idFacturaTemp);
	FacturaTemp buscarPorUsuario(Usuario usuario);
	void guardar(FacturaTemp facturaTemp);
	void eliminar(Integer idFacturaTemp);
}
