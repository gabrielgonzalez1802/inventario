package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Gasto;

public interface GastosRepository extends JpaRepository<Gasto, Integer> {
	List<Gasto> findByAlmacen(Almacen almacen);
}
