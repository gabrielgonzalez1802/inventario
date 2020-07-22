package com.developergg.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Articulo;

public interface ArticulosRepository extends JpaRepository<Articulo, Integer>{
}