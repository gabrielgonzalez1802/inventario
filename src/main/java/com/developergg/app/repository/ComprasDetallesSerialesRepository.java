package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.CompraDetalle;
import com.developergg.app.model.CompraDetalleSerial;

public interface ComprasDetallesSerialesRepository extends JpaRepository<CompraDetalleSerial, Integer> {
	List<CompraDetalleSerial> findByCompraDetalle(CompraDetalle compraDetalle);
}
