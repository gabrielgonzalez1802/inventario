package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.Taller;
import com.developergg.app.model.Usuario;

public interface TalleresRepository extends JpaRepository<Taller, Integer> {
	List<Taller> findByAlmacen(Almacen almacen);
	List<Taller> findByFacturaTemp(FacturaTemp facturaTemp);
	List<Taller> findByAlmacenAndUsuarioIn(Almacen almacen, List<Usuario> usuario);
}
