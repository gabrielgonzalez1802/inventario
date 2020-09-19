package com.developergg.app.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Ingreso;
import com.developergg.app.model.Usuario;

public interface IngresosRepository extends JpaRepository<Ingreso, Integer> {
	List<Ingreso> findByAlmacen(Almacen almacen);
	List<Ingreso> findByUsuarioInAndAlmacenAndFechaBetween(List<Usuario> usuarios, 
			Almacen almacen, Date desde, Date hasta);
}
