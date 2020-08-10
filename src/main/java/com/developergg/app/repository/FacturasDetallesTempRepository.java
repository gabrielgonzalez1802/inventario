package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.FacturaDetalleTemp;
import com.developergg.app.model.Usuario;

public interface FacturasDetallesTempRepository extends JpaRepository<FacturaDetalleTemp, Integer> {
	List<FacturaDetalleTemp> findByUsuarioAndAlmacen(Usuario usuario, Almacen almacen);
	
}
