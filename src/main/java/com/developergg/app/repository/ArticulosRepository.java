package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.developergg.app.model.Articulo;
import com.developergg.app.model.Categoria;
import com.developergg.app.model.Propietario;

public interface ArticulosRepository extends JpaRepository<Articulo, Integer>{
	List<Articulo> findByTienda(Propietario tienda);
	
	@Query("FROM Articulo WHERE nombre LIKE '%:articulo%' AND eliminado = 0")
	List<Articulo> BuscarPorNombreArticulo(@Param("articulo") String articulo);
	List<Articulo> findByCategoria(Categoria categoria);
	List<Articulo> findByNombreLike(String articulo);
	List<Articulo> findByTiendaAndNombreContainingOrCodigoContaining(Propietario tienda, String nombre, String nombrecodigo);
}