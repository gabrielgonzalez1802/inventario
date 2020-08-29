package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaPagoTemp;

public interface IFacturasPagoTempService {
	FacturaPagoTemp buscarPorId(Integer id);
	List<FacturaPagoTemp> buscarPorFactura(Factura factura);
	void guardar(FacturaPagoTemp facturaPago);
	void eliminar(Integer id);
	void eliminarAll(List<FacturaPagoTemp> pagosTemp);
}
