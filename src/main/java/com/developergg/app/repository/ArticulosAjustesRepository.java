package com.developergg.app.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloAjuste;

public interface ArticulosAjustesRepository extends JpaRepository<ArticuloAjuste, Integer> {
	List<ArticuloAjuste> findByArticuloAndAlmacen(Articulo articulo, Almacen almacen);
	List<ArticuloAjuste> findByAlmacen(Almacen almacen);
	List<ArticuloAjuste> findByAlmacenAndFechaBetween(Almacen almacen, Date desde, Date hasta);
	List<ArticuloAjuste> findByAlmacenAndTipoMovimientoAndArticuloInAndFechaBetween(Almacen almacen, 
			String tipoMovimiento, List<Articulo> articulos, Date desde, Date hasta);
	
}
