package com.developergg.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.developergg.app.model.Propietario;

public interface PropietariosRepository extends JpaRepository<Propietario, Integer>{
	@Query("FROM Propietario WHERE id_usuario = :idUsuario")
	Propietario buscarPorIdUsuario(Integer idUsuario);
}
