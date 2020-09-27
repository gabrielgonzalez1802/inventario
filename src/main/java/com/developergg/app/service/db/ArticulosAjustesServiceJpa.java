package com.developergg.app.service.db;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloAjuste;
import com.developergg.app.repository.ArticulosAjustesRepository;
import com.developergg.app.service.IArticulosAjustesService;

@Service
public class ArticulosAjustesServiceJpa implements IArticulosAjustesService{
	
	@Autowired
	private ArticulosAjustesRepository repo;

	@Override
	public List<ArticuloAjuste> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public ArticuloAjuste buscarPorId(Integer idArticuloAjuste) {
		Optional<ArticuloAjuste> optional = repo.findById(idArticuloAjuste);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void guardar(ArticuloAjuste articuloAjuste) {
		repo.save(articuloAjuste);
	}

	@Override
	public List<ArticuloAjuste> buscarPorArticuloYAlmacen(Articulo articulo, Almacen almacen) {
		return repo.findByArticuloAndAlmacen(articulo, almacen);
	}

	@Override
	public List<ArticuloAjuste> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

	@Override
	public List<ArticuloAjuste> buscarPorAlmacenTipoMovimientoArticulos(Almacen almacen, String movimiento,
			List<Articulo> articulos) {
		return null;
	}

	@Override
	public List<ArticuloAjuste> buscarPorAlmacenTipoMovimientoArticulosFechas(Almacen almacen, String movimiento,
			List<Articulo> articulos, Date desde, Date hasta) {
		return repo.findByAlmacenAndTipoMovimientoAndArticuloInAndFechaBetween(almacen, movimiento, articulos, desde, hasta);
	}

	@Override
	public List<ArticuloAjuste> buscarPorAlmacenFechas(Almacen almacen, Date desde, Date hasta) {
		return repo.findByAlmacenAndFechaBetween(almacen, desde, hasta);
	} 

}
