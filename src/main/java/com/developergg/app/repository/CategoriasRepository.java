package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Categoria;

public interface CategoriasRepository extends JpaRepository<Categoria, Integer>{
	/**
	 * Devuelve la lista de categorias por estatus
	 * @param status 1 activo , 0 inactivo
	 * @return Lista de categorias segun estatus
	 */
	List<Categoria> findByStatus(Integer status);
}
