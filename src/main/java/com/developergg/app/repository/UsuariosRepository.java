package com.developergg.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Usuario;

public interface UsuariosRepository extends JpaRepository<Usuario, Integer> {
	Usuario findByUsername(String username);
}
