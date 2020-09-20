package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.DevolucionFactura;
import com.developergg.app.model.DevolucionFacturaSerial;
import com.developergg.app.model.FacturaDetalle;
import com.developergg.app.model.FacturaDetalleSerial;

public interface DevolucionesFacturasSerialsRepository extends JpaRepository<DevolucionFacturaSerial, Integer> {
	List<DevolucionFacturaSerial> findByFacturaDetalle(FacturaDetalle facturaDetalle);
	List<DevolucionFacturaSerial> findByFacturaDetalleSerial(FacturaDetalleSerial facturaDetalleSerial);
	List<DevolucionFacturaSerial> findByDevolucionFactura(DevolucionFactura devolucionFactura);
}
