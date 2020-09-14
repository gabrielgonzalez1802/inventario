package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.AbonoCxP;
import com.developergg.app.model.AbonoCxPDetalle;

public interface IAbonosCxPDetallesService {
	AbonoCxPDetalle buscarPorId(Integer id);
	List<AbonoCxPDetalle> buscarPorIngreso(AbonoCxP ingreso);
	void guardar(AbonoCxPDetalle abonoCxPDetalle);
	void eliminar(Integer id);
	void eliminar(AbonoCxPDetalle abonoCxPDetalle);
	void eliminar(List<AbonoCxPDetalle> lista);
}
