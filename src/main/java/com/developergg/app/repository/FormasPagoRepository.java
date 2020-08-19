package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.FormaPago;

public interface FormasPagoRepository extends JpaRepository<FormaPago, Integer> {
	List<FormaPago> findByAlmacen(Almacen almacen);
}
