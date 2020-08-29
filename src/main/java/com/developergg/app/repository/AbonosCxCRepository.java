package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.AbonoCxC;
import com.developergg.app.model.Factura;

public interface AbonosCxCRepository extends JpaRepository<AbonoCxC, Integer> {
	List<AbonoCxC> findByFactura(Factura factura);
}
