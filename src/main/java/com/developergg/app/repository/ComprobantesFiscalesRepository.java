package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.ComprobanteFiscal;
import com.developergg.app.model.Propietario;

public interface ComprobantesFiscalesRepository extends JpaRepository<ComprobanteFiscal, Integer> {
	List<ComprobanteFiscal> findByTienda(Propietario tienda);
}
