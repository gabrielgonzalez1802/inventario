package com.developergg.app.service.db;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Compra;
import com.developergg.app.repository.ComprasRepository;
import com.developergg.app.service.IComprasService;

@Service
public class ComprasServiceJpa implements IComprasService{
	
	@Autowired
	private ComprasRepository repo;

	@Override
	public Compra buscarPorId(Integer id) {
		Optional<Compra> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Compra> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

	@Override
	public void guardar(Compra compra) {
		repo.save(compra);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<Compra> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public List<Compra> buscarPorAlmacenFechas(Almacen almacen, Date desde, Date hasta) {
		return repo.findByAlmacenAndFechaBetween(almacen, desde, hasta);
	}
}
