package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloAjuste;

public interface ArticulosAjustesRepository extends JpaRepository<ArticuloAjuste, Integer> {
	List<ArticuloAjuste> findByArticuloAndAlmacen(Articulo articulo, Almacen almacen);
}
