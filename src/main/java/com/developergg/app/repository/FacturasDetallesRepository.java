package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetalle;

public interface FacturasDetallesRepository extends JpaRepository<FacturaDetalle, Integer> {
	List<FacturaDetalle> findByFactura(Factura factura);
}
