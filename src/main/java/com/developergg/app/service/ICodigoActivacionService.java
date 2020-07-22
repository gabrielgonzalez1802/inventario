package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.CodigoActivacion;

public interface ICodigoActivacionService {
	List<CodigoActivacion> buscarTodos();
	CodigoActivacion buscarPorCodigo(String codigo);
}
