package com.developergg.app.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Gasto;
import com.developergg.app.model.Usuario;

public interface GastosRepository extends JpaRepository<Gasto, Integer> {
	List<Gasto> findByAlmacen(Almacen almacen);
	List<Gasto> findByUsuarioInAndAlmacenAndFechaBetween(List<Usuario> usuarios, 
			Almacen almacen, Date desde, Date hasta);
}
