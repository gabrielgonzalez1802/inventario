package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Suplidor;
import com.developergg.app.repository.SuplidoresRepository;
import com.developergg.app.service.ISuplidoresService;

@Service
public class SuplidoresServiceJpa implements ISuplidoresService {
	
	@Autowired
	private SuplidoresRepository repo;

	@Override
	public List<Suplidor> buscarPorAlmacenDisponible(Almacen almacen) {
		List<Suplidor> lista = repo.findByAlmacen(almacen)
				.stream().filter(s -> s.getEliminado() == 0)
				.collect(Collectors.toList());
		return lista;
	}

	@Override
	public void guardar(Suplidor suplidor) {
		repo.save(suplidor);
	}

	@Override
	public Suplidor buscarPorId(Integer idSuplidor) {
		Optional<Suplidor> optional = repo.findById(idSuplidor);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

}
