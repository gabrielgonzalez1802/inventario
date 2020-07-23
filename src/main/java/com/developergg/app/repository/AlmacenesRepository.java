package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.developergg.app.model.Almacen;

public interface AlmacenesRepository extends JpaRepository<Almacen, Integer>{
	@Query("FROM Almacen WHERE id_tienda = :idTienda")
	List<Almacen> buscarPorIdTienda(@Param("idTienda") Integer idTienda);
}
