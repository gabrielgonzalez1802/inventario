package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Taller;

public interface TalleresRepository extends JpaRepository<Taller, Integer> {
	List<Taller> findByAlmacen(Almacen almacen);
}
