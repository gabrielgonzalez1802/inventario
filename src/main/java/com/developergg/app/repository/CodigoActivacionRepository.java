package com.developergg.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.CodigoActivacion;

public interface CodigoActivacionRepository extends JpaRepository<CodigoActivacion,Integer> {
	public CodigoActivacion findByCodigo(String codigo);
}
