package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.AbonoCxP;
import com.developergg.app.model.Almacen;
import com.developergg.app.model.Compra;
import com.developergg.app.model.Suplidor;

public interface AbonosCxPRepository extends JpaRepository<AbonoCxP, Integer> {
	AbonoCxP findByCompra(Compra compra);
	List<AbonoCxP> findByAlmacen(Almacen almacen);
	List<AbonoCxP> findBySuplidor(Suplidor suplidor);
}
