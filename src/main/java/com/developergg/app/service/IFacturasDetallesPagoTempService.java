package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.FacturaDetallePagoTemp;
import com.developergg.app.model.FacturaTemp;

public interface IFacturasDetallesPagoTempService {
	List<FacturaDetallePagoTemp> buscarPorFacturaTemp(FacturaTemp facturaTemp);
	FacturaDetallePagoTemp buscarPorId(Integer facturaDetallePagoTempId);
	void guardar(FacturaDetallePagoTemp facturaDetallePagoTemp);
	void eliminarListaPagos(List<FacturaDetallePagoTemp> lista);
	void eliminar(Integer facturaDetallePagoTempId);
}
