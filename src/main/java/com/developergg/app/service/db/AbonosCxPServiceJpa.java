package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.AbonoCxP;
import com.developergg.app.model.Almacen;
import com.developergg.app.model.Compra;
import com.developergg.app.model.Suplidor;
import com.developergg.app.repository.AbonosCxPRepository;
import com.developergg.app.service.IAbonosCxPService;

@Service
public class AbonosCxPServiceJpa implements IAbonosCxPService{
	
	@Autowired
	private AbonosCxPRepository repo;

	@Override
	public AbonoCxP buscarPorId(Integer id) {
		Optional<AbonoCxP> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public AbonoCxP buscarPorCompra(Compra compra) {
		return repo.findByCompra(compra);
	}

	@Override
	public List<AbonoCxP> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

	@Override
	public List<AbonoCxP> buscarPorSuplidor(Suplidor suplidor) {
		return repo.findBySuplidor(suplidor);
	}

	@Override
	public void guardar(AbonoCxP abonoCxP) {
		repo.save(abonoCxP);
	}

	@Override
	public void eliminar(AbonoCxP abonoCxP) {
		repo.delete(abonoCxP);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<AbonoCxP> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

}
