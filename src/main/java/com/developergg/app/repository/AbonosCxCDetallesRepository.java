package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.AbonoCxC;
import com.developergg.app.model.AbonoCxCDetalle;

public interface AbonosCxCDetallesRepository extends JpaRepository<AbonoCxCDetalle, Integer> {
	List<AbonoCxCDetalle> findByIngreso(AbonoCxC ingreso);
}
