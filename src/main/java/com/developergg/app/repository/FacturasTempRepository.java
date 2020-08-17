package com.developergg.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.Usuario;

public interface FacturasTempRepository extends JpaRepository<FacturaTemp, Integer> {
	FacturaTemp findByUsuario(Usuario usuario);
}
