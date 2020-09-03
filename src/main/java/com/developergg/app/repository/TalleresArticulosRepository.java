package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.Taller;
import com.developergg.app.model.TallerArticulo;

public interface TalleresArticulosRepository extends JpaRepository<TallerArticulo, Integer>{
	List<TallerArticulo> findByAlmacen(Almacen almacen);
	List<TallerArticulo> findByArticuloAndAlmacen(Articulo articulo, Almacen almacen);
	List<TallerArticulo> findByTaller(Taller taller);
}
