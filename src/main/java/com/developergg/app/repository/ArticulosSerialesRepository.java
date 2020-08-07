package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloSerial;

public interface ArticulosSerialesRepository extends JpaRepository<ArticuloSerial, Integer> {
	List<ArticuloSerial> findByAlmacen(Almacen almacen);
	
	@Query("FROM ArticuloSerial WHERE id_almacen = :idAlmacen ORDER BY fecha DESC")
	List<ArticuloSerial> buscarPorAlmacenYFechaDesc(@Param("idAlmacen") Integer idAlmacen);
	
	List<ArticuloSerial> findByArticuloAndAlmacen(Articulo articulo, Almacen almacen);
	
	ArticuloSerial findBySerial(String serial);
	
	List<ArticuloSerial> findBySerialAndAlmacen(String serial,Almacen almacen);
		
}
