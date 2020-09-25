package com.developergg.app.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Compra;

public interface ComprasRepository extends JpaRepository<Compra, Integer> {
	List<Compra> findByAlmacen(Almacen almacen);
	List<Compra> findByAlmacenAndFechaBetween(Almacen almacen, Date desde, Date hasta);
}
