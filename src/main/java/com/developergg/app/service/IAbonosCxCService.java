package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.AbonoCxC;
import com.developergg.app.model.Factura;

public interface IAbonosCxCService {
	AbonoCxC buscarPorId(Integer id);
	List<AbonoCxC> buscarPorFactura(Factura factura);
	void guardar(AbonoCxC abonoCxC);
	void eliminar(Integer id);
}
