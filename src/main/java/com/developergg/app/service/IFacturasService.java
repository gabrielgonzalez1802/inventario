package com.developergg.app.service;

import java.util.Date;
import java.util.List;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Factura;
import com.developergg.app.model.Taller;
import com.developergg.app.model.Usuario;

public interface IFacturasService {
	Factura buscarPorId(Integer id);
	List<Factura> buscarPorAlmacen(Almacen almacen);
	Factura buscarPorTaller(Taller taller);
	void guardar(Factura factura);
	void eliminar(Integer id);
	List<Factura> buscarFacturaCuadre(Usuario usuario, Almacen almacen,
			Integer credito, Taller taller, Date desde, Date hasta);
	List<Factura> buscarFacturaCuadreMultiUsuario(Almacen almacen, Integer credito, Taller taller,
			Date desde, Date hasta, List<Usuario> usuarios);
	List<Factura> buscarFacturaCuadreTallerMultiUsuario(Almacen almacen,
			Integer credito, Date desde, Date hasta, List<Usuario> usuarios);
	List<Factura> buscarFacturaCuadreMultiUsuario(Almacen almacen,
			Integer credito, Date desde, Date hasta, List<Usuario> usuarios);
	List<Factura> buscarFacturaCuadreMultiUsuario(Almacen almacen, Date desde, 
			Date hasta, List<Usuario> usuarios);
	List<Factura> buscarFacturaCuadreMultiUsuarioConTaller(Almacen almacen, Date desde, 
			Date hasta, List<Usuario> usuarios);
	List<Factura> buscarFacturaAlmacenFechas(Almacen almacen, Date desde, 
			Date hasta);
}
