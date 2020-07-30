package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Articulo;
import com.developergg.app.model.Propietario;

public interface ArticulosRepository extends JpaRepository<Articulo, Integer>{
	List<Articulo> findByTienda(Propietario tienda);
}