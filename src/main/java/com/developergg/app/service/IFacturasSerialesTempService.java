package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.FacturaDetalleTemp;
import com.developergg.app.model.FacturaSerialTemp;
import com.developergg.app.model.FacturaTemp;

public interface IFacturasSerialesTempService {
	void guardar(FacturaSerialTemp serialTemp);
	List<FacturaSerialTemp> buscarPorDetalleTemp(FacturaDetalleTemp facturaDetalleTemp);
	List<FacturaSerialTemp> buscarPorFacturaTemp(FacturaTemp facturaTemp);
	FacturaSerialTemp buscarPorId(Integer idSerial);
	void eliminar(Integer idSerial);
	void eliminarListaSeriales(List<FacturaSerialTemp> lista);
}
