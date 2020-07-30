package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Suplidor;

public interface SuplidoresRepository extends JpaRepository<Suplidor, Integer> {
	List<Suplidor> findByAlmacen(Almacen almacen);
}
