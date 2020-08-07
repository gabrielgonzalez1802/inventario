package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Cliente;

public interface ClientesRepository extends JpaRepository<Cliente, Integer>{
	List<Cliente> findByAlmacen(Almacen almacen);
}
