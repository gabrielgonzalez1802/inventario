package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Comprobante;
import com.developergg.app.model.Propietario;

public interface ComprobantesRepository extends JpaRepository<Comprobante, Integer>{
	List<Comprobante> findByPropietario(Propietario tienda);
}
