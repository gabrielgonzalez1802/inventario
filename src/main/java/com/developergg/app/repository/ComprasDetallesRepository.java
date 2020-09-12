package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Compra;
import com.developergg.app.model.CompraDetalle;

public interface ComprasDetallesRepository extends JpaRepository<CompraDetalle, Integer>{
	List<CompraDetalle> findByCompra(Compra compra);
}
