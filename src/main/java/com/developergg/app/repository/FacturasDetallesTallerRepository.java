package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetalleTaller;

public interface FacturasDetallesTallerRepository extends JpaRepository<FacturaDetalleTaller, Integer> {
	List<FacturaDetalleTaller> findByFactura(Factura factura);
}
