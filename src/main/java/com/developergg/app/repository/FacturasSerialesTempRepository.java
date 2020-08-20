package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.FacturaDetalleTemp;
import com.developergg.app.model.FacturaSerialTemp;
import com.developergg.app.model.FacturaTemp;

public interface FacturasSerialesTempRepository extends JpaRepository<FacturaSerialTemp, Integer>{
	List<FacturaSerialTemp> findByIdDetalle(FacturaDetalleTemp idDetalle);
	List<FacturaSerialTemp> findByFacturaTemp(FacturaTemp facturaTemp);
}
