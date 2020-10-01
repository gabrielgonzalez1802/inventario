package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Compra;
import com.developergg.app.model.DevolucionCompra;

public interface DevolucionesComprasRepository extends JpaRepository<DevolucionCompra, Integer> {
	List<DevolucionCompra> findByAlmacen(Almacen almacen);
	List<DevolucionCompra> findByCompra(Compra compra);
}
