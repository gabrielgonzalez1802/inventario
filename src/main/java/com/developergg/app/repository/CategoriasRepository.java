package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Categoria;
import com.developergg.app.model.Propietario;

public interface CategoriasRepository extends JpaRepository<Categoria, Integer>{
	List<Categoria> findByTienda(Propietario tienda);
}
