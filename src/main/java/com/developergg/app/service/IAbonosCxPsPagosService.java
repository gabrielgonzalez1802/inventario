package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.AbonoCxPDetalle;
import com.developergg.app.model.AbonoCxPPago;
import com.developergg.app.model.Compra;

public interface IAbonosCxPsPagosService {
	AbonoCxPPago buscarPorId(Integer id);
	List<AbonoCxPPago> buscarPorCompra(Compra compra);
	List<AbonoCxPPago> buscarPorDetalle(AbonoCxPDetalle detalle);
	void guardar(AbonoCxPPago abonoCxPPago);
	void eliminar(Integer id);
}
