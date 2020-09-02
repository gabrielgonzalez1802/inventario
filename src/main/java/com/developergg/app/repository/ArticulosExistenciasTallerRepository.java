package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloExistenciaTaller;

public interface ArticulosExistenciasTallerRepository extends JpaRepository<ArticuloExistenciaTaller, Integer> {
	List<ArticuloExistenciaTaller> findByArticuloAndAlmacen(Articulo articulo, Almacen almacen);
	List<ArticuloExistenciaTaller> findByAlmacen(Almacen almacen);
}
