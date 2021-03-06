package com.developergg.app.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Cliente;
import com.developergg.app.model.ComprobanteFiscal;
import com.developergg.app.model.Factura;
import com.developergg.app.model.Taller;
import com.developergg.app.model.Usuario;
import com.developergg.app.model.Vendedor;

public interface FacturasRepository extends JpaRepository<Factura, Integer> {
	List<Factura> findByAlmacen(Almacen almacen);
	Factura findByTaller(Taller taller);
	List<Factura> findByAbono(Double abono);
	List<Factura> findByUsuarioAndAlmacenAndCreditoAndTallerAndFechaBetween(Usuario usuario, Almacen almacen,
			Integer credito, Taller taller, Date desde, Date hasta);
	List<Factura> findByAlmacenAndCreditoAndTallerAndFechaBetweenAndUsuarioIn(Almacen almacen, Integer credito, Taller taller,
			Date desde, Date hasta, List<Usuario> usuarios);
	List<Factura> findByAlmacenAndCreditoAndFechaBetweenAndUsuarioInAndTallerIsNotNull(Almacen almacen,
			Integer credito, Date desde, Date hasta, List<Usuario> usuarios);
	List<Factura> findByAlmacenAndCreditoAndFechaBetweenAndUsuarioIn(Almacen almacen,
			Integer credito, Date desde, Date hasta, List<Usuario> usuarios);
	List<Factura> findByAlmacenAndFechaBetweenAndUsuarioIn(Almacen almacen, Date desde, Date hasta, List<Usuario> usuarios);
	List<Factura> findByAlmacenAndFechaBetweenAndUsuarioInAndTallerIsNotNull(Almacen almacen, Date desde, Date hasta, List<Usuario> usuarios);
	List<Factura> findByAlmacenAndFechaBetween(Almacen almacen, Date desde, Date hasta);
	List<Factura> findByAlmacenAndFechaBetweenAndClienteIn(Almacen almacen, Date desde, Date hasta, List<Cliente> cliente);
	List<Factura> findByAlmacenAndFechaBetweenAndVendedorIn(Almacen almacen, Date desde, Date hasta, List<Vendedor> vendedor);
	List<Factura> findByAlmacenAndFechaBetweenAndTallerIsNotNull(Almacen almacen, Date desde, Date hasta);
	List<Factura> findByAlmacenAndFechaBetweenAndComprobanteFiscalIn(Almacen almacen, Date desde,
			Date hasta, List<ComprobanteFiscal> comprobanteFiscal);
}
