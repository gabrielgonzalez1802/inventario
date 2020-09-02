package com.developergg.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Perfil;

public interface PerfilesRepository extends JpaRepository<Perfil, Integer> {
	Perfil findByPerfil(String perfil);
}
