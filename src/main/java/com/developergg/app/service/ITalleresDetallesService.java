package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Taller;
import com.developergg.app.model.TallerDetalle;

public interface ITalleresDetallesService {
	TallerDetalle buscarPorId(Integer id);
	List<TallerDetalle> buscarPorTaller(Taller taller);
	void guardar(TallerDetalle tallerDetalle);
	void eliminar(TallerDetalle tallerDetalle);
	void eliminar(Integer id);
}	
