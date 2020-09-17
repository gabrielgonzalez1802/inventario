package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.DevolucionFactura;
import com.developergg.app.model.DevolucionFacturaDetalle;
import com.developergg.app.model.FacturaDetalle;
import com.developergg.app.model.FacturaDetalleTaller;

public interface DevolucionesFacturasDetallesRepository extends JpaRepository<DevolucionFacturaDetalle, Integer> {
	List<DevolucionFacturaDetalle> findByDevolucionFactura(DevolucionFactura devolucionFactura);
	List<DevolucionFacturaDetalle> findByFacturaDetalle(FacturaDetalle facturaDetalle);
	List<DevolucionFacturaDetalle> findByFacturaDetalleTaller(FacturaDetalleTaller facturaDetalleTaller);
}
