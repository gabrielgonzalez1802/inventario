package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.AbonoCxPDetalle;
import com.developergg.app.model.AbonoCxPPago;
import com.developergg.app.model.Compra;

public interface AbonosCxPsPagoRepository extends JpaRepository<AbonoCxPPago, Integer> {
	List<AbonoCxPPago> findByCompra(Compra compra);
	List<AbonoCxPPago> findByDetalle(AbonoCxPDetalle detalle);
}
