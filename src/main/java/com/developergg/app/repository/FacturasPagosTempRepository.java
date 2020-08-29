package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaPagoTemp;

public interface FacturasPagosTempRepository extends JpaRepository<FacturaPagoTemp, Integer> {
	List<FacturaPagoTemp> findByFactura(Factura factura);
}
