package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Compra;
import com.developergg.app.model.CompraDetalle;
import com.developergg.app.repository.ComprasDetallesRepository;
import com.developergg.app.service.IComprasDetallesService;

@Service
public class ComprasDetallesServiceJpa implements IComprasDetallesService{
	
	@Autowired
	private ComprasDetallesRepository repo;

	@Override
	public CompraDetalle buscarPorId(Integer id) {
		Optional<CompraDetalle> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<CompraDetalle> buscarPorCompra(Compra compra) {
		return repo.findByCompra(compra);
	}

	@Override
	public void guardar(CompraDetalle compraDetalle) {
		repo.save(compraDetalle);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<CompraDetalle> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public void elminar(CompraDetalle compraDetalle) {
		repo.delete(compraDetalle);
	}

	@Override
	public void eliminar(List<CompraDetalle> listaCompraDetalle) {
		repo.deleteAll(listaCompraDetalle);
	}

}
