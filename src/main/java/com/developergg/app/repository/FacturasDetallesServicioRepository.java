package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetalleServicio;

public interface FacturasDetallesServicioRepository extends JpaRepository<FacturaDetalleServicio, Integer> {
	List<FacturaDetalleServicio> findByFactura(Factura factura);
}
