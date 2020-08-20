package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Factura;

public interface FacturasRepository extends JpaRepository<Factura, Integer> {
	List<Factura> findByAlmacen(Almacen almacen);
}
