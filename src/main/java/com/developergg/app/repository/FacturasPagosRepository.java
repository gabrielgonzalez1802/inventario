package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaPago;

public interface FacturasPagosRepository extends JpaRepository<FacturaPago, Integer> {
	List<FacturaPago> findByFactura(Factura factura);
}
