package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Taller;
import com.developergg.app.model.TallerArticulo;
import com.developergg.app.model.TallerDetalle;

public interface TalleresDetallesRepository extends JpaRepository<TallerDetalle, Integer> {
	List<TallerDetalle> findByTaller(Taller taller);
	List<TallerDetalle> findByTallerArticulo(TallerArticulo tallerArticulo);
}
