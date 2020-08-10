package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.FacturaServicioTemp;
import com.developergg.app.model.Usuario;

public interface FacturasServiciosTempRepository extends JpaRepository<FacturaServicioTemp, Integer> {
	List<FacturaServicioTemp> findByUsuarioAndAlmacen(Usuario usuario, Almacen almacen);
}
