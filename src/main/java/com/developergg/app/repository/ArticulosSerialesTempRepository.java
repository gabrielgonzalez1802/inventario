package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloSerialTemp;

public interface ArticulosSerialesTempRepository extends JpaRepository<ArticuloSerialTemp, Integer> {
	List<ArticuloSerialTemp> findByAlmacen(Almacen almacen);
	
	@Query("FROM ArticuloSerialTemp WHERE id_almacen = :idAlmacen ORDER BY fecha DESC")
	List<ArticuloSerialTemp> buscarPorAlmacenYFechaDesc(@Param("idAlmacen") Integer idAlmacen);
	
	List<ArticuloSerialTemp> findByArticuloAndAlmacen(Articulo articulo, Almacen almacen);
	
	ArticuloSerialTemp findBySerial(String serial);
	
	List<ArticuloSerialTemp> findBySerialAndAlmacen(String serial,Almacen almacen);
		
}
