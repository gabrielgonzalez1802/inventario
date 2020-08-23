package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.FacturaDetalleTemp;
import com.developergg.app.model.Usuario;
import com.developergg.app.repository.FacturasDetallesTempRepository;
import com.developergg.app.service.IFacturasDetallesTempService;

@Service
public class FacturasDetallesTempServiceJpa implements IFacturasDetallesTempService{
	
	@Autowired
	private FacturasDetallesTempRepository repo;

	@Override
	public void guardar(FacturaDetalleTemp facturaDetalle) {
		repo.save(facturaDetalle);
	}

	@Override
	public List<FacturaDetalleTemp> buscarPorUsuarioAlmacen(Usuario usuario, Almacen almacen) {
		return repo.findByUsuarioAndAlmacen(usuario, almacen);
	}

	@Override
	public FacturaDetalleTemp buscarPorId(Integer idFacturaDetalle) {
		Optional<FacturaDetalleTemp> optional = repo.findById(idFacturaDetalle);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void eliminar(Integer idFacturaDetalle) {
		Optional<FacturaDetalleTemp> optional = repo.findById(idFacturaDetalle);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public void eliminarListadoDetalles(List<FacturaDetalleTemp> lista) {
		repo.deleteAll(lista);
	}

}
