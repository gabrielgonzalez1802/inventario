package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.AbonoCxC;
import com.developergg.app.model.AbonoCxCDetalle;

public interface IAbonosCxCDetallesService {
	AbonoCxCDetalle buscarPorId(Integer id);
	List<AbonoCxCDetalle> buscarPorIngreso(AbonoCxC ingreso);
	void guardar(AbonoCxCDetalle abonoCxCDetalle);
	void eliminar(Integer id);
}
