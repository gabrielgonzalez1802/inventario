package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.AbonoCxP;
import com.developergg.app.model.AbonoCxPDetalle;

public interface AbonosCxPDetallesRepository extends JpaRepository<AbonoCxPDetalle, Integer> {
	List<AbonoCxPDetalle> findByIngreso(AbonoCxP ingreso);
}
