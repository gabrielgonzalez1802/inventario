package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.Taller;
import com.developergg.app.model.TallerArticulo;
import com.developergg.app.model.TallerDetalle;

public interface ITalleresDetallesService {
	TallerDetalle buscarPorId(Integer id);
	List<TallerDetalle> buscarPorTaller(Taller taller);
	List<TallerDetalle> buscarPorTallerArticulo(TallerArticulo tallerArticulo);
	void guardar(TallerDetalle tallerDetalle);
	void eliminar(TallerDetalle tallerDetalle);
	void eliminar(List<TallerDetalle> listaTalleresDetalles);
	void eliminar(Integer id);
}	
