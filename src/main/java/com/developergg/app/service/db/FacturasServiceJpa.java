package com.developergg.app.service.db;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Cliente;
import com.developergg.app.model.Factura;
import com.developergg.app.model.Taller;
import com.developergg.app.model.Usuario;
import com.developergg.app.model.Vendedor;
import com.developergg.app.repository.FacturasRepository;
import com.developergg.app.service.IFacturasService;

@Service
public class FacturasServiceJpa implements IFacturasService {
	
	@Autowired
	private FacturasRepository repo;

	@Override
	public Factura buscarPorId(Integer id) {
		Optional<Factura> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void guardar(Factura factura) {
		repo.save(factura);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<Factura> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public List<Factura> buscarPorAlmacen(Almacen almacen) {
		return repo.findByAlmacen(almacen);
	}

	@Override
	public Factura buscarPorTaller(Taller taller) {
		return repo.findByTaller(taller);
	}

	@Override
	public List<Factura> buscarFacturaCuadre(Usuario usuario, Almacen almacen, Integer credito, Taller taller,
			Date desde, Date hasta) {
		return repo.findByUsuarioAndAlmacenAndCreditoAndTallerAndFechaBetween(usuario,
				almacen, credito, taller, desde, hasta);
	}

	@Override
	public List<Factura> buscarFacturaCuadreMultiUsuario(Almacen almacen, Integer credito, Taller taller,
			Date desde, Date hasta, List<Usuario> usuarios) {
		return repo.findByAlmacenAndCreditoAndTallerAndFechaBetweenAndUsuarioIn(almacen, credito,
				taller, desde, hasta, usuarios);
	}

	@Override
	public List<Factura> buscarFacturaCuadreTallerMultiUsuario(Almacen almacen, Integer credito, Date desde,
			Date hasta, List<Usuario> usuarios) {
		return  repo.findByAlmacenAndCreditoAndFechaBetweenAndUsuarioInAndTallerIsNotNull(almacen, credito,
				desde, hasta, usuarios);
	}

	@Override
	public List<Factura> buscarFacturaCuadreMultiUsuario(Almacen almacen, Integer credito, Date desde, Date hasta,
			List<Usuario> usuarios) {
		return repo.findByAlmacenAndCreditoAndFechaBetweenAndUsuarioIn(almacen,
				credito, desde, hasta, usuarios);
	}

	@Override
	public List<Factura> buscarFacturaCuadreMultiUsuario(Almacen almacen, Date desde, Date hasta,
			List<Usuario> usuarios) {
		return repo.findByAlmacenAndFechaBetweenAndUsuarioIn(almacen, desde, hasta, usuarios);
	}

	@Override
	public List<Factura> buscarFacturaCuadreMultiUsuarioConTaller(Almacen almacen, Date desde, Date hasta,
			List<Usuario> usuarios) {
		return repo.findByAlmacenAndFechaBetweenAndUsuarioInAndTallerIsNotNull(almacen, desde, hasta, usuarios);
	}

	@Override
	public List<Factura> buscarFacturaAlmacenFechas(Almacen almacen, Date desde, Date hasta) {
		return repo.findByAlmacenAndFechaBetween(almacen, desde, hasta);
	}

	@Override
	public List<Factura> buscarFacturasAlmacenFechasCliente(Almacen almacen, Date desde, Date hasta,
			List<Cliente> cliente) {
		return repo.findByAlmacenAndFechaBetweenAndClienteIn(almacen, desde, hasta, cliente);
	}

	@Override
	public List<Factura> buscarFacturasAlmacenFechasVendedor(Almacen almacen, Date desde, Date hasta,
			List<Vendedor> vendedor) {
		return repo.findByAlmacenAndFechaBetweenAndVendedorIn(almacen, desde, hasta, vendedor);
	}

	@Override
	public List<Factura> buscarFacturaAlmacenFechasTallerIsNotNull(Almacen almacen, Date desde, Date hasta) {
		return repo.findByAlmacenAndFechaBetweenAndTallerIsNotNull(almacen, desde, hasta);
	}
}
