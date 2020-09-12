package com.developergg.app.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloAjuste;
import com.developergg.app.model.ArticuloSerial;
import com.developergg.app.model.Compra;
import com.developergg.app.model.CompraDetalle;
import com.developergg.app.model.CompraDetalleSerial;
import com.developergg.app.model.ComprobanteFiscal;
import com.developergg.app.model.CondicionPago;
import com.developergg.app.model.Suplidor;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IArticulosAjustesService;
import com.developergg.app.service.IArticulosSeriales;
import com.developergg.app.service.IArticulosService;
import com.developergg.app.service.IComprasDetallesSerialesService;
import com.developergg.app.service.IComprasDetallesService;
import com.developergg.app.service.IComprasService;
import com.developergg.app.service.IComprobantesFiscalesService;
import com.developergg.app.service.ICondicionesPagoService;
import com.developergg.app.service.ISuplidoresService;

@Controller
@RequestMapping("/compras")
public class ComprasController {
	
	@Autowired
	private IComprasService serviceCompras;
	
	@Autowired
	private IComprasDetallesService serviceComprasDetalles;
	
	@Autowired
	private IComprasDetallesSerialesService serviceComprasDetallesSeriales;
		
	@Autowired
	private ISuplidoresService serviceSuplidores;
	
	@Autowired
	private ICondicionesPagoService serviceCondicionesPago;
	
	@Autowired
	private IComprobantesFiscalesService serviceComprobantesFiscales;
	
	@Autowired
	private IArticulosService serviceArticulos;
	
	@Autowired
	private IArticulosAjustesService serviceArticulosAjustes;
	
	@Autowired
	private IArticulosSeriales serviceArticulosSeriales;

	@GetMapping("/")
	public String mostrarCompras(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Compra> compras =  serviceCompras.buscarPorAlmacen(usuario.getAlmacen()).stream().filter(c -> c.getEn_proceso() == 0).collect(Collectors.toList());
		model.addAttribute("compras", compras);
		return "compras/listaCompras";
	}
	
	@GetMapping("/create")
	public String crear(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Suplidor> suplidores = serviceSuplidores.buscarPorAlmacenDisponible(usuario.getAlmacen());
		List<CondicionPago> condicionesPago = serviceCondicionesPago.buscarTodos();
		List<ComprobanteFiscal> comprobantesFiscales = serviceComprobantesFiscales.buscarPorTienda(usuario.getAlmacen().getPropietario());
	
		//articulos por tienda que no esten eliminados
		List<Articulo> articulos = serviceArticulos.buscarPorTienda(usuario.getAlmacen().getPropietario())
				.stream().filter(p -> p.getEliminado() == 0).collect(Collectors.toList());
		articulos.forEach((a)-> {
				//si tiene no tiene serial
				if(a.getImei().equalsIgnoreCase("0") || a.getImei().equalsIgnoreCase("NO")) {
					List<ArticuloAjuste> articulosAjustes = serviceArticulosAjustes.buscarPorArticuloYAlmacen(a, usuario.getAlmacen());
					if(articulosAjustes.isEmpty()) {
						a.setCantidad(0);
					}else {
						ArticuloAjuste newArticuloAjuste = articulosAjustes.get(articulosAjustes.size()-1);
						a.setCantidad(newArticuloAjuste.getDisponible());
					}
				}else {
					//Validacion para articulos con serial
					List<ArticuloSerial> articuloSerials = serviceArticulosSeriales
							.buscarPorArticuloAlmacen(a, usuario.getAlmacen()).stream()
							.filter(s -> s.getEstado().equalsIgnoreCase("Disponible"))
							.collect(Collectors.toList());
					if(!articuloSerials.isEmpty()) {
						a.setCantidad(articuloSerials.size());
					}else {
						a.setCantidad(0);
					}
					for (ArticuloSerial articuloSerial : articuloSerials) {
						//buscamos el articulo
						if(articuloSerial.getArticulo().getId() == a.getId()) {
							a.setPrecio_maximo(articuloSerial.getPrecio_maximo());
							a.setPrecio_mayor(articuloSerial.getPrecio_mayor());
							a.setPrecio_minimo(articuloSerial.getPrecio_minimo());
						}
					}
				}
			});
		//Verificamos si hay compra en proceso, de lo contrario se crea una nueva
		List<Compra> compras = serviceCompras.buscarPorAlmacen(usuario.getAlmacen()).stream().filter(c -> c.getEn_proceso() == 1).collect(Collectors.toList());
		Compra compra = null;
		if(compras.isEmpty()) {
			compra = new Compra();
			compra.setFecha(new Date());
			compra.setAlmacen(usuario.getAlmacen());
			compra.setUsuario(usuario);
			compra.setEn_proceso(1);
			serviceCompras.guardar(compra);
		}else {
			compra = compras.get(0);
		}
		model.addAttribute("suplidores", suplidores);
		model.addAttribute("condicionesPago", condicionesPago);
		model.addAttribute("comprobantesFiscales", comprobantesFiscales);
		model.addAttribute("compra", compra);
		model.addAttribute("articulos", articulos);
		return "compras/compra";
	}
	
	@GetMapping("/ajax/getInfoArticulo/{id}")
	public String getInfoArticulo(Model model, HttpSession session, 
			@PathVariable(name = "id") Integer idArticulo) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		int conSerial = 0;
		//no tiene serial
		Articulo a = serviceArticulos.buscarPorId(idArticulo);
		if(a.getImei().equalsIgnoreCase("0") || a.getImei().equalsIgnoreCase("NO")) {
			List<ArticuloAjuste> articulosAjustes = serviceArticulosAjustes.buscarPorArticuloYAlmacen(a, usuario.getAlmacen());
			if(articulosAjustes.isEmpty()) {
				a.setCantidad(0);
			}else {
				ArticuloAjuste newArticuloAjuste = articulosAjustes.get(articulosAjustes.size()-1);
				a.setCantidad(newArticuloAjuste.getDisponible());
			}
		}else {
			conSerial = 1;
			//Validacion para articulos con serial
			List<ArticuloSerial> articuloSerials = serviceArticulosSeriales
					.buscarPorArticuloAlmacen(a, usuario.getAlmacen()).stream()
					.filter(s -> s.getEstado().equalsIgnoreCase("Disponible"))
					.collect(Collectors.toList());
			if(!articuloSerials.isEmpty()) {
				a.setCantidad(articuloSerials.size());
			}else {
				a.setCantidad(0);
			}
			for (ArticuloSerial articuloSerial : articuloSerials) {
				//buscamos el articulo
				if(articuloSerial.getArticulo().getId() == a.getId()) {
					a.setPrecio_maximo(articuloSerial.getPrecio_maximo());
					a.setPrecio_mayor(articuloSerial.getPrecio_mayor());
					a.setPrecio_minimo(articuloSerial.getPrecio_minimo());
				}
			}
		}
		
		model.addAttribute("infoCantidad", a.getCantidad());
		model.addAttribute("infoSerial", conSerial);
		model.addAttribute("infoNombre", a.getNombre());
		model.addAttribute("infoCosto", a.getCosto());
		model.addAttribute("infoIdArticulo", a.getId());
		return "compras/compra :: #infoArticulo";
	}
	
	@GetMapping("/ajax/getInfoArticuloSerial/{id}")
	public String getInfoArticuloSerial(Model model, HttpSession session, 
			@PathVariable(name = "id") Integer idArticulo) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//si tiene no tiene serial
		Articulo a = serviceArticulos.buscarPorId(idArticulo);
		// Validacion para articulos con serial
		List<ArticuloSerial> articuloSerials = serviceArticulosSeriales
				.buscarPorArticuloAlmacen(a, usuario.getAlmacen()).stream()
				.filter(s -> s.getEstado().equalsIgnoreCase("Disponible")).collect(Collectors.toList());
		if (!articuloSerials.isEmpty()) {
			a.setCantidad(articuloSerials.size());
		} else {
			a.setCantidad(0);
		}
		for (ArticuloSerial articuloSerial : articuloSerials) {
			// buscamos el articulo
			if (articuloSerial.getArticulo().getId() == a.getId()) {
				a.setPrecio_maximo(articuloSerial.getPrecio_maximo());
				a.setPrecio_mayor(articuloSerial.getPrecio_mayor());
				a.setPrecio_minimo(articuloSerial.getPrecio_minimo());
			}
		}

		model.addAttribute("costoSerial", a.getCosto());
		model.addAttribute("precioMaximo", a.getPrecio_maximo());
		model.addAttribute("precioMinimo", a.getPrecio_minimo());
		model.addAttribute("precioMayor", a.getPrecio_mayor());
		model.addAttribute("idSerial", a.getId());
		return "compras/compra :: #infoArticuloSerial";
	}
	
	@GetMapping("/ajax/loadCuerpo/{id}")
	public String cargarCuerpo(Model model, HttpSession session, 
			@PathVariable(name = "id") Integer idCompra) {
		Compra compra = serviceCompras.buscarPorId(idCompra);
		List<CompraDetalle> detalles = serviceComprasDetalles.buscarPorCompra(compra);
		Double total = 0.0;
		if(!detalles.isEmpty()) {
			for (CompraDetalle detalle : detalles) {
				total+=detalle.getCantidad()*detalle.getCosto();
			}
		}
		model.addAttribute("listaDetalles", detalles);
		model.addAttribute("total", total);
		return "compras/cuerpoCompras :: cuerpoCompra";
	}
	
	@GetMapping("/ajax/loadInfoDetailSerial/{idDetalle}")
	public String cargarInfoDetailSerial(Model model, HttpSession session, 
			@PathVariable(name = "idDetalle") Integer idDetalle) {
		CompraDetalle compraDetalle = serviceComprasDetalles.buscarPorId(idDetalle);
		List<CompraDetalleSerial> listaSeriales = serviceComprasDetallesSeriales.buscarPorCompraDetalle(compraDetalle);
		model.addAttribute("detalleArticulo", compraDetalle.getArticulo().getNombre());
		model.addAttribute("listaSeriales", listaSeriales);
		return "compras/compra :: infoSeriales";
	}
	
	@PostMapping("/ajax/addItemSinSerial/")
	public String agregarItemSinSerial(Model model, HttpSession session, 
			@RequestParam(name = "idCompra") Integer idCompra, @RequestParam(name = "idArticulo") Integer idArticulo,
			@RequestParam(name = "cantidad") Integer cantidad, @RequestParam(name = "costo") Double costo) {
		int response = 0;
		Compra compra = serviceCompras.buscarPorId(idCompra);
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		CompraDetalle compraDetalle = new CompraDetalle();
		compraDetalle.setArticulo(articulo);
		compraDetalle.setCompra(compra);
		compraDetalle.setCantidad(cantidad);
		compraDetalle.setCosto(costo);
		compraDetalle.setSubTotal(cantidad*costo);
		serviceComprasDetalles.guardar(compraDetalle);
		if(compraDetalle.getId()!=null) {
			response = 1;
		}
		model.addAttribute("responseAddItem", response);
		return "compras/compra :: #responseAddItem";
	}
	
	@PostMapping("/ajax/eliminarItemSinSerial/")
	public String agregarItemSinSerial(Model model, HttpSession session, 
			@RequestParam(name = "idDetalle") Integer idDetalle) {
		CompraDetalle compraDetalle = serviceComprasDetalles.buscarPorId(idDetalle);
		//Verificamos si tiene seriales asociados para eliminarlos
		if(compraDetalle.getCon_imei() == 1) {
			List<CompraDetalleSerial> listaSeriales = serviceComprasDetallesSeriales.buscarPorCompraDetalle(compraDetalle);
			if(!listaSeriales.isEmpty()) {
				for (CompraDetalleSerial compraDetalleSerial : listaSeriales) {
					serviceComprasDetallesSeriales.eliminar(compraDetalleSerial);
				}
			}
		}
		//Eliminamos el detalle
		serviceComprasDetalles.elminar(compraDetalle);
		return "compras/compra :: #responseDeleteItem";
	}
	
	@PostMapping("/ajax/guardarCompra/")
	public String guardarCompra(Model model, HttpSession session, 
			@RequestParam(name = "idCompra") Integer idCompra, @RequestParam(name = "fechaCompra") String fechaCompra,
			@RequestParam(name = "no_factura") String no_factura, @RequestParam(name = "idSuplidor") Integer idSuplidor,
			@RequestParam(name = "idcondicionPago") Integer idcondicionPago, @RequestParam(name = "tipoNCF") Integer tipoNCF,
			@RequestParam(name = "ncf") String ncf, @RequestParam(name = "observacion") String observacion,
			@RequestParam(name = "precioTotal") Double precioTotal) throws ParseException {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = formatter.parse(fechaCompra);
		Compra compra = serviceCompras.buscarPorId(idCompra);
		compra.setNo_factura(no_factura);
		compra.setFecha(date);
		Suplidor suplidor = new Suplidor();
		suplidor.setId(idSuplidor);
		compra.setSuplidor(suplidor);
		CondicionPago condicion = new CondicionPago();
		condicion.setId(idcondicionPago);
		compra.setCondicion(condicion);
		ComprobanteFiscal comprobanteFiscal = new ComprobanteFiscal();
		comprobanteFiscal.setId(tipoNCF);
		compra.setComprobanteFiscal(comprobanteFiscal);
		compra.setEn_proceso(0);
		compra.setObservacion(observacion);
		compra.setNcf(ncf);
		compra.setTotalNeto(precioTotal);
		serviceCompras.guardar(compra);
		
		//Movimiento de inventario
		List<CompraDetalle> compraDetalle = serviceComprasDetalles.buscarPorCompra(compra);
		for (CompraDetalle detalle : compraDetalle) {
			if(detalle.getCon_imei()==1) {
				List<CompraDetalleSerial> compraDetalleSeriales = serviceComprasDetallesSeriales.buscarPorCompraDetalle(detalle);
				//Verificamos los seriales
				for (CompraDetalleSerial serial : compraDetalleSeriales) {
					List<ArticuloSerial> lista = serviceArticulosSeriales
							.buscarPorIdAlmacenDesc(usuario.getAlmacen().getId())
							.stream().filter(s -> s.getEliminado() == 0).
					collect(Collectors.toList());
					//Verificamos si el serial existe
//					if(lista.stream().
//					filter(s -> s.getSerial().equalsIgnoreCase(serial.getSerial())).
//					collect(Collectors.toList()).size() == 0) {
						ArticuloSerial articuloSerial = new ArticuloSerial();
						articuloSerial.setAlmacen(usuario.getAlmacen());
						articuloSerial.setFecha(new Date());
						articuloSerial.setId_usuario(usuario.getId());
						articuloSerial.setCosto(serial.getCosto());
						articuloSerial.setArticulo(detalle.getArticulo());
						articuloSerial.setEstado("Disponible");
						articuloSerial.setFecha(new Date());
						articuloSerial.setId_compra(compra.getId());
						articuloSerial.setId_usuario(usuario.getId());
						articuloSerial.setSuplidor(compra.getSuplidor());
						articuloSerial.setSerial(serial.getSerial());
						articuloSerial.setPrecio_maximo(serial.getPrecio_maximo());
						articuloSerial.setPrecio_minimo(serial.getPrecio_minimo());
						articuloSerial.setPrecio_mayor(serial.getPrecio_mayor());
						articuloSerial.setNo_factura(no_factura);
						serviceArticulosSeriales.guardar(articuloSerial);
						lista.add(articuloSerial);
						//Actualizamos el articulo
						Articulo articulo = articuloSerial.getArticulo();
						articulo.setPrecio_maximo(serial.getPrecio_maximo());
						articulo.setPrecio_minimo(serial.getPrecio_minimo());
						articulo.setPrecio_mayor(serial.getPrecio_mayor());
						articulo.setCosto(serial.getCosto());
						serviceArticulos.guardar(articulo);
//					}else {
//						//serial existe
//					}
				}
			}else {
				//Articulo sin serial
				//ordenamos el ultimo elemento de la lista
				// verificar si el registro tiene inventario
				List<ArticuloAjuste> lista = serviceArticulosAjustes.buscarPorArticuloYAlmacen(detalle.getArticulo(), usuario.getAlmacen());
				ArticuloAjuste newArticuloAjuste = lista.get(lista.size()-1);
				ArticuloAjuste newArticuloAjusteDefinitive = new ArticuloAjuste();
				newArticuloAjusteDefinitive.setId(null);
				newArticuloAjusteDefinitive.setAlmacen(usuario.getAlmacen());
				newArticuloAjusteDefinitive.setFecha(new Date());
				newArticuloAjusteDefinitive.setUsuario(usuario);
				newArticuloAjusteDefinitive.setTipoMovimiento("Entrada");
				newArticuloAjusteDefinitive.setCantidad(detalle.getCantidad());
				newArticuloAjusteDefinitive.setCosto(detalle.getCosto());
				newArticuloAjusteDefinitive.setExistencia(newArticuloAjuste.getDisponible());
				newArticuloAjusteDefinitive.setArticulo(detalle.getArticulo());
				newArticuloAjusteDefinitive.setSuplidor(compra.getSuplidor());
				newArticuloAjusteDefinitive.setNo_factura(compra.getNo_factura());
				newArticuloAjusteDefinitive.setUsuario(usuario);
				newArticuloAjusteDefinitive.setDisponible(newArticuloAjusteDefinitive.getExistencia()+newArticuloAjusteDefinitive.getCantidad());
				serviceArticulosAjustes.guardar(newArticuloAjusteDefinitive);
			}

		}
		model.addAttribute("responseSave", 1);
		return "compras/compra :: #responseSave";
	}
	
	@PostMapping("/ajax/addItemConSerial/")
	public String agregarItemConSerial(Model model, HttpSession session, 
			@RequestParam(name = "idCompra") Integer idCompra, @RequestParam(name = "idArticulo") Integer idArticulo,
			@RequestParam(name = "costo") Double costo, @RequestParam(name = "serial") String serial,
			@RequestParam(name = "precioMaximo") Double precioMaximo, @RequestParam(name = "precioMinimo") Double precioMinimo,
			@RequestParam(name = "precioMayor") Double precioMayor) {
		int response = 0;
		Compra compra = serviceCompras.buscarPorId(idCompra);
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		
		//Verificamos si el articulo esta presente en el detalle
		List<CompraDetalle> listaDetalle = serviceComprasDetalles.buscarPorCompra(compra).stream().filter(d -> d.getArticulo().getId() == idArticulo).collect(Collectors.toList());
		CompraDetalle compraDetalle = null;
		int distinto = 0;
		if(listaDetalle.isEmpty()) {
			compraDetalle = new CompraDetalle();
			compraDetalle.setArticulo(articulo);
			compraDetalle.setCompra(compra);
			compraDetalle.setCosto(costo);
			compraDetalle.setCon_imei(1);
			serviceComprasDetalles.guardar(compraDetalle);
		}else {
			compraDetalle = listaDetalle.get(0);
			//Verificamos que el costo sea igual
			for (CompraDetalle detail : listaDetalle) {
				if(detail.getCosto().doubleValue() != costo) {
					distinto = 1;
				}
			}
		}
		
		//Verificamos que el serial no este agregado en el detalle
		List<CompraDetalleSerial> compraDetalleSeriales = serviceComprasDetallesSeriales.
				buscarPorCompraDetalle(compraDetalle).stream().
				filter(s -> s.getSerial().equalsIgnoreCase(serial)).
				collect(Collectors.toList());
		
		if(distinto == 0) {
			if(compraDetalleSeriales.isEmpty()) {
				CompraDetalleSerial compraDetalleSerial = new CompraDetalleSerial();
				compraDetalleSerial.setArticulo(articulo);
				compraDetalleSerial.setCompra(compra);
				compraDetalleSerial.setCompraDetalle(compraDetalle);
				compraDetalleSerial.setSerial(serial);
				compraDetalleSerial.setCosto(costo);
				compraDetalleSerial.setPrecio_maximo(precioMaximo);
				compraDetalleSerial.setPrecio_minimo(precioMinimo);
				compraDetalleSerial.setPrecio_mayor(precioMayor);
				serviceComprasDetallesSeriales.guardar(compraDetalleSerial);
				
				if(compraDetalleSerial.getId()!=null) {
					compraDetalle.setCantidad(compraDetalle.getCantidad()+1);
					compraDetalle.setSubTotal(compraDetalle.getCantidad()*compraDetalle.getCosto());
					serviceComprasDetalles.guardar(compraDetalle);
					response = 1;
				}
			}
		}else {
			if(compraDetalleSeriales.isEmpty()) {
				CompraDetalle compraDetalle2 = new CompraDetalle();
				compraDetalle2.setArticulo(articulo);
				compraDetalle2.setCompra(compra);
				compraDetalle2.setCosto(costo);
				compraDetalle2.setCon_imei(1);
				serviceComprasDetalles.guardar(compraDetalle2);
				CompraDetalleSerial compraDetalleSerial = new CompraDetalleSerial();
				compraDetalleSerial.setArticulo(articulo);
				compraDetalleSerial.setCompra(compra);
				compraDetalleSerial.setCompraDetalle(compraDetalle2);
				compraDetalleSerial.setSerial(serial);
				compraDetalleSerial.setCosto(costo);
				compraDetalleSerial.setPrecio_maximo(precioMaximo);
				compraDetalleSerial.setPrecio_minimo(precioMinimo);
				compraDetalleSerial.setPrecio_mayor(precioMayor);
				serviceComprasDetallesSeriales.guardar(compraDetalleSerial);
				
				if(compraDetalleSerial.getId()!=null) {
					compraDetalle2.setCantidad(compraDetalle2.getCantidad()+1);
					compraDetalle2.setSubTotal(compraDetalle2.getCantidad()*compraDetalle2.getCosto());
					serviceComprasDetalles.guardar(compraDetalle2);
					response = 1;
				}
			}
		}
		
		model.addAttribute("responseAddItem", response);
		return "compras/compra :: #responseAddItem";
	}
	
	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
}
