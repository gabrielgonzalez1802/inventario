package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Vendedor;

public interface VendedoresRepository extends JpaRepository<Vendedor, Integer> {
	List<Vendedor> findByAlmacen(Almacen almacen);
}
