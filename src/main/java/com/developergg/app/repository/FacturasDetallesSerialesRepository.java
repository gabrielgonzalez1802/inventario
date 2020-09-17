package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.FacturaDetalle;
import com.developergg.app.model.FacturaDetalleSerial;

public interface FacturasDetallesSerialesRepository extends JpaRepository<FacturaDetalleSerial, Integer>{
	List<FacturaDetalleSerial> findByFacturaDetalle(FacturaDetalle facturaDetalle);
}
