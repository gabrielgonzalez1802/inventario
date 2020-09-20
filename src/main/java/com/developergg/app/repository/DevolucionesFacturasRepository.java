package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.DevolucionFactura;
import com.developergg.app.model.Factura;

public interface DevolucionesFacturasRepository extends JpaRepository<DevolucionFactura, Integer> {
	List<DevolucionFactura> findByAlmacen(Almacen almacen);
	List<DevolucionFactura> findByFactura(Factura factura);
}
