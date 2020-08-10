package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.FacturaServicioTemp;
import com.developergg.app.model.Usuario;
import com.developergg.app.repository.FacturasServiciosTempRepository;
import com.developergg.app.service.IFacturasServiciosTempService;

@Service
public class FacturasServiciosTempServiceJpa implements IFacturasServiciosTempService {
	
	@Autowired
	private FacturasServiciosTempRepository repo;

	@Override
	public void guardar(FacturaServicioTemp servicio) {
		repo.save(servicio);
	}

	@Override
	public List<FacturaServicioTemp> buscarPorUsuarioAlmacen(Usuario usuario, Almacen almacen) {
		return repo.findByUsuarioAndAlmacen(usuario, almacen);
	}

	@Override
	public void eliminar(Integer idServicio) {
		Optional<FacturaServicioTemp> optional = repo.findById(idServicio);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

}
