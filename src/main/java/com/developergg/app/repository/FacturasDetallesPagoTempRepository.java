package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.FacturaDetallePagoTemp;
import com.developergg.app.model.FacturaTemp;

public interface FacturasDetallesPagoTempRepository extends JpaRepository<FacturaDetallePagoTemp, Integer> {
	List<FacturaDetallePagoTemp> findByFacturaTemp(FacturaTemp facturaTemp);
}
