package com.developergg.app.service.db;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Ingreso;
import com.developergg.app.model.Usuario;
import com.developergg.app.repository.IngresosRepository;
import com.developergg.app.service.IIngresosService;

@Service
public class IngresosServiceJpa implements IIngresosService{
	
	@Autowired
	private IngresosRepository repo;

	@Override
	public Ingreso buscarPorId(Integer id) {
		Optional<Ingreso> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Ingreso> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

	@Override
	public void guardar(Ingreso ingreso) {
		repo.save(ingreso);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<Ingreso> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public List<Ingreso> buscarPorUsuariosAlmacenFechas(List<Usuario> usuarios, Almacen almacen, Date desde,
			Date hasta) {
		return repo.findByUsuarioInAndAlmacenAndFechaBetween(usuarios, almacen, desde, hasta);
	}
	
}
