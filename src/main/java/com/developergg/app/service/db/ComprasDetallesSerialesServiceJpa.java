package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.CompraDetalle;
import com.developergg.app.model.CompraDetalleSerial;
import com.developergg.app.repository.ComprasDetallesSerialesRepository;
import com.developergg.app.service.IComprasDetallesSerialesService;

@Service
public class ComprasDetallesSerialesServiceJpa implements IComprasDetallesSerialesService{
	
	@Autowired
	private ComprasDetallesSerialesRepository repo;

	@Override
	public CompraDetalleSerial buscarPorId(Integer id) {
		Optional<CompraDetalleSerial> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<CompraDetalleSerial> buscarPorCompraDetalle(CompraDetalle compraDetalle) {
		return repo.findByCompraDetalle(compraDetalle);
	}

	@Override
	public void guardar(CompraDetalleSerial compraDetalleSerial) {
		repo.save(compraDetalleSerial);
	}

	@Override
	public void eliminar(Integer CompraDetalleSerial) {
		Optional<CompraDetalleSerial> optional = repo.findById(CompraDetalleSerial);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public void eliminar(CompraDetalleSerial compraDetalleSerial) {
		repo.delete(compraDetalleSerial);
	}

	@Override
	public void eliminar(List<CompraDetalleSerial> listaCompraDetalleSerials) {
		repo.deleteAll(listaCompraDetalleSerials);
	}


}
