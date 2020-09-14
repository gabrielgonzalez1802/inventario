package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.AbonoCxPDetalle;
import com.developergg.app.model.AbonoCxPPago;
import com.developergg.app.model.Compra;
import com.developergg.app.repository.AbonosCxPsPagoRepository;
import com.developergg.app.service.IAbonosCxPsPagosService;

@Service
public class AbonosCxPsPagosServiceJpa implements IAbonosCxPsPagosService{
	
	@Autowired
	private AbonosCxPsPagoRepository repo;

	@Override
	public AbonoCxPPago buscarPorId(Integer id) {
		Optional<AbonoCxPPago> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<AbonoCxPPago> buscarPorCompra(Compra compra) {
		return repo.findByCompra(compra);
	}

	@Override
	public void guardar(AbonoCxPPago abonoCxPPago) {
		repo.save(abonoCxPPago);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<AbonoCxPPago> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public List<AbonoCxPPago> buscarPorDetalle(AbonoCxPDetalle detalle) {
		return repo.findByDetalle(detalle);
	}

}
