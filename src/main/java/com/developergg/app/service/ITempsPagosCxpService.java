package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Compra;
import com.developergg.app.model.TempPagoCxp;

public interface ITempsPagosCxpService {	
	TempPagoCxp buscarPorId(Integer id);
	List<TempPagoCxp> buscarPorCompra(Compra compra);
	void guardar (TempPagoCxp tempPagoCxp);
	void eliminar (Integer id);
	void eliminar (List<TempPagoCxp> lista);
}
