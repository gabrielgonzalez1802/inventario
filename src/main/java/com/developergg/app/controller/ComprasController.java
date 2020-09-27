package com.developergg.app.controller;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.developergg.app.model.AbonoCxP;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloAjuste;
import com.developergg.app.model.ArticuloSerial;
import com.developergg.app.model.Categoria;
import com.developergg.app.model.Compra;
import com.developergg.app.model.CompraDetalle;
import com.developergg.app.model.CompraDetalleSerial;
import com.developergg.app.model.ComprobanteFiscal;
import com.developergg.app.model.CondicionPago;
import com.developergg.app.model.ReporteCompra;
import com.developergg.app.model.Suplidor;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IAbonosCxPService;
import com.developergg.app.service.IArticulosAjustesService;
import com.developergg.app.service.IArticulosSeriales;
import com.developergg.app.service.IArticulosService;
import com.developergg.app.service.ICategoriasService;
import com.developergg.app.service.IComprasDetallesSerialesService;
import com.developergg.app.service.IComprasDetallesService;
import com.developergg.app.service.IComprasService;
import com.developergg.app.service.IComprobantesFiscalesService;
import com.developergg.app.service.ICondicionesPagoService;
import com.developergg.app.service.ISuplidoresService;
import com.developergg.app.service.IUsuariosService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

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
	
	@Autowired
	private IAbonosCxPService serviceAbonosCxP;
	
	@Autowired
	private ICategoriasService serviceCategorias;
	
	@Autowired
	private IUsuariosService serviceUsuarios;
	
	private String tempFolder =  System.getProperty("java.io.tmpdir");
	private String pathSeparator = System.getProperty("file.separator");
	
	DecimalFormat df2 = new DecimalFormat("###.##");
	
	@Autowired
	private DataSource dataSource;
	
	@Value("${inventario.ruta.imagenes}")
	private String rutaImagenes;
	
	@Value("${inventario.ruta.reporte.compraArticulo}")
	private String rutaJreportCompraArticulo;
	
	@Value("${inventario.ruta.reporte.compraSuplidor}")
	private String rutaJreportCompraSuplidor;
	
	@Value("${inventario.ruta.reporte.compraUsuario}")
	private String rutaJreportCompraUsuario;
	
	@Value("${inventario.ruta.reporte.compraCategoria}")
	private String rutaJreportCompraCategoria;


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
		
		model.addAttribute("infoSerial", conSerial);
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
	
	@GetMapping("/ajax/getInfoArticuloSinSerial/{id}")
	public String getInfoArticuloSinSerial(Model model, HttpSession session, 
			@PathVariable(name = "id") Integer idArticulo) {
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		model.addAttribute("articuloSinSerial", articulo.getNombre());
		model.addAttribute("idArticuloSinSerial", articulo.getId());
		model.addAttribute("costoSinSerial", articulo.getCosto());
		model.addAttribute("sinSerialPrecioMaximo", articulo.getPrecio_maximo());
		model.addAttribute("sinSerialPrecioMinimo", articulo.getPrecio_minimo());
		model.addAttribute("sinSerialPrecioMayor", articulo.getPrecio_mayor());
		return "compras/compra :: #infoArticuloSinSerial";
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
			@RequestParam(name = "cantidad") Integer cantidad, @RequestParam(name = "costo") Double costo,
			@RequestParam(name = "precioMaximo") Double precioMaximo, @RequestParam(name = "precioMinimo") Double precioMinimo,
			@RequestParam(name = "precioMayor") Double precioMayor) {
		int response = 0;
		Compra compra = serviceCompras.buscarPorId(idCompra);
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		CompraDetalle compraDetalle = new CompraDetalle();
		compraDetalle.setArticulo(articulo);
		compraDetalle.setCompra(compra);
		compraDetalle.setCantidad(cantidad);
		compraDetalle.setCosto(costo);
		compraDetalle.setPrecio_maximo(precioMaximo);
		compraDetalle.setPrecio_minimo(precioMinimo);
		compraDetalle.setPrecio_mayor(precioMayor);
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
				newArticuloAjusteDefinitive.setTipoMovimiento("entrada");
				newArticuloAjusteDefinitive.setCantidad(detalle.getCantidad());
				newArticuloAjusteDefinitive.setCosto(detalle.getCosto());
				newArticuloAjusteDefinitive.setExistencia(newArticuloAjuste.getDisponible());
				newArticuloAjusteDefinitive.setArticulo(detalle.getArticulo());
				newArticuloAjusteDefinitive.setSuplidor(compra.getSuplidor());
				newArticuloAjusteDefinitive.setNo_factura(compra.getNo_factura());
				newArticuloAjusteDefinitive.setProcedencia("compras");
				newArticuloAjusteDefinitive.setUsuario(usuario);
				newArticuloAjusteDefinitive.setDisponible(newArticuloAjusteDefinitive.getExistencia()+newArticuloAjusteDefinitive.getCantidad());
				serviceArticulosAjustes.guardar(newArticuloAjusteDefinitive);
				
				//Actualizamos el articulo
				Articulo articulo = detalle.getArticulo();
				articulo.setPrecio_maximo(detalle.getPrecio_maximo());
				articulo.setPrecio_minimo(detalle.getPrecio_minimo());
				articulo.setPrecio_mayor(detalle.getPrecio_mayor());
				articulo.setCosto(detalle.getCosto());
				serviceArticulos.guardar(articulo);
			}

		}
		
		//Si la condicion es a contado se actualiza la informacion en la compra
        CondicionPago condicionDePago = serviceCondicionesPago.buscarPorId(condicion.getId());
		if(condicionDePago.getDia()==0) {
			compra.setPagada(1);
			compra.setTotalCompra(compra.getTotalNeto());
		}else {
			compra.setTotalCompra(compra.getTotalNeto());
			compra.setTotalRestante(compra.getTotalNeto());
	        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
	        String now = LocalDateTime.now().format(formatter2);
			AbonoCxP cxP = new AbonoCxP();
			cxP.setAlmacen(compra.getAlmacen());
			cxP.setCompra(compra);
			cxP.setFecha(new Date());
			cxP.setHora(now);
			cxP.setSuplidor(compra.getSuplidor());
			cxP.setTotalCompra(compra.getTotalCompra());
			cxP.setTotalDevuelto(compra.getTotalDevuelto());
			cxP.setTotalPagado(compra.getTotalPagado());
			cxP.setTotalRestante(compra.getTotalRestante());
			cxP.setUsuario(usuario);
			serviceAbonosCxP.guardar(cxP);
		}
		serviceCompras.guardar(compra);
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
	
	@GetMapping("/formularioReporteCompraSuplidor")
	public String formularioReporteCompraSuplidor(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Suplidor> suplidores = serviceSuplidores.buscarPorAlmacenDisponible(usuario.getAlmacen());
		model.addAttribute("suplidores", suplidores);
		model.addAttribute("dateAcct", new Date());
		return "compras/generarReporteCompraSuplidor";
	}
	
	@PostMapping("/reporteCompraXSuplidor")
	public void reporteCompraXSuplidor(HttpSession session, HttpServletRequest request, 
			HttpServletResponse response, Integer idSuplidor, String desde, String hasta) throws JRException, SQLException, ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<ReporteCompra> reporteCompras = new LinkedList<>();
		List<ReporteCompra> reporteCompraSeriales = new LinkedList<>();
		List<Suplidor> suplidores = new LinkedList<>();
		
		if(idSuplidor == 0) {
			suplidores = serviceSuplidores.buscarPorAlmacenDisponible(usuario.getAlmacen());
		}else {
			suplidores.add(serviceSuplidores.buscarPorId(idSuplidor));
		}
		
		//Compras finalizadas
		List<Compra> compras = serviceCompras.buscarPorAlmacenFechas(usuario.getAlmacen(), 
				formato.parse(desde),  formato.parse(hasta)).
				stream().filter(c -> c.getEn_proceso() == 0).collect(Collectors.toList());
		
		for (Compra compra : compras) {
			// detalles
			List<CompraDetalle> compraDetalles = serviceComprasDetalles.buscarPorCompra(compra);
			for (CompraDetalle compraDetalle : compraDetalles) {
				ReporteCompra reporteCompra = new ReporteCompra();
				reporteCompra.setCantidad(compraDetalle.getCantidad());
				reporteCompra.setFactura(compraDetalle.getCompra().getNo_factura());
				// verificamos si el articulo tiene serial
				if (compraDetalle.getCon_imei() == 0) {
					// Sin serial
					reporteCompra.setArticulo(compraDetalle.getArticulo().getNombre());
					reporteCompra.setCosto(compraDetalle.getCosto());
					reporteCompra.setId(compra.getId());
//					reporteCompra.setPrecio((compraDetalle.getCantidad()*compraDetalle.getCosto())+compra.getItBis());
					reporteCompra.setTabla("compras");
					reporteCompra.setTotal((compraDetalle.getCantidad() * compraDetalle.getCosto()) + compra.getItBis());

					if (idSuplidor == 0) {
						reporteCompras.add(reporteCompra);
					} else {
						if (compra.getSuplidor().getId() == idSuplidor) {
							reporteCompras.add(reporteCompra);
						}
					}
				} else {
					// Con serial
					List<CompraDetalleSerial> compraDetalleSeriales = serviceComprasDetallesSeriales
							.buscarPorCompraDetalle(compraDetalle);
					int count = 0;
					for (CompraDetalleSerial compraDetalleSerial : compraDetalleSeriales) {
						if (count == 0) {
							ReporteCompra reporteCompraSerial = new ReporteCompra();
							reporteCompraSerial.setArticulo(compraDetalleSerial.getArticulo().getNombre());
							reporteCompraSerial.setCantidad(1);
							reporteCompraSerial.setCosto(compraDetalleSerial.getCosto());
							reporteCompraSerial.setFactura(compraDetalleSerial.getCompra().getNo_factura());
							reporteCompraSerial.setId(compra.getId());
							reporteCompraSerial.setSerial(compraDetalleSerial.getSerial());
//							reporteCompraSerial.setPrecio(precio);
							reporteCompraSerial.setTabla("compras");
							reporteCompraSerial.setTotal(compraDetalleSerial.getCosto());

							if (idSuplidor == 0) {
								reporteCompraSeriales.add(reporteCompraSerial);
							} else {
								if (compra.getSuplidor().getId() == idSuplidor) {
									reporteCompraSeriales.add(reporteCompraSerial);
								}
							}
						} else {
							int coincidencia = 0;
							for (ReporteCompra compraSerial : reporteCompraSeriales) {
								// Verificamos articulos de igual nombre e igual costo
								if (compraSerial.getCosto().doubleValue() == compraSerial.getCosto().doubleValue()) {
									compraSerial.setSerial(
											compraSerial.getSerial() + "," + compraDetalleSerial.getSerial());
									coincidencia++;
								}
							}

							if (coincidencia == 0 && reporteCompraSeriales.size() > 1) {
								ReporteCompra reporteCompraSerial = new ReporteCompra();
								reporteCompraSerial.setArticulo(compraDetalleSerial.getArticulo().getNombre());
								reporteCompraSerial.setCantidad(1);
								reporteCompraSerial.setId(compra.getId());
								reporteCompraSerial.setCosto(compraDetalleSerial.getCosto());
								reporteCompraSerial.setFactura(compraDetalleSerial.getCompra().getNo_factura());
//								reporteCompraSerial.setPrecio(compraDetalleSerial.getPrecio_maximo());
								reporteCompraSerial.setTotal(compraDetalleSerial.getCosto());
								reporteCompraSerial.setSerial(compraDetalleSerial.getSerial());

								if (idSuplidor == 0) {
									reporteCompraSeriales.add(reporteCompraSerial);
								} else {
									if (compra.getSuplidor().getId() == idSuplidor) {
										reporteCompraSeriales.add(reporteCompraSerial);
									}
								}
							}
						}
						count++;
					}
				}
			}
		}
		
		for (ReporteCompra compraSerial : reporteCompraSeriales) {
			String[] tempSerials = compraSerial.getSerial().split(",");
			compraSerial.setCantidad(tempSerials.length);
			compraSerial.setTotal(compraSerial.getCantidad() * compraSerial.getCosto());
			compraSerial.setArticulo(compraSerial.getArticulo() + " - " + compraSerial.getSerial());
			reporteCompras.add(compraSerial);
		}
		
		//Compilamos el reporte
		JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreportCompraSuplidor);

		Map<String, Object> parameters = new HashMap<String, Object>();

		// convertimos la lista a JRBeanCollectionDataSource
		JRBeanCollectionDataSource articulosReporteJRBean = new JRBeanCollectionDataSource(reporteCompras);

		parameters.put("idUsuario", usuario.getId());
		parameters.put("imagen", rutaImagenes + usuario.getAlmacen().getImagen());
		parameters.put("total", reporteCompras.size());
		parameters.put("fechaDesde", desde);
		parameters.put("fechaHasta", hasta);
		parameters.put("author", usuario.getUsername());
		parameters.put("suplidor", idSuplidor == 0 ? "TODOS" : suplidores.get(0).getNombre());
		parameters.put("reporteCompras", articulosReporteJRBean);

		// Objeto de impresion jr
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

		String dataDirectory = tempFolder + pathSeparator + "reporteComprasSuplidor" + usuario.getId() + ".pdf";

		tempFolder += pathSeparator;

		JasperExportManager.exportReportToPdfFile(jasperPrint, dataDirectory);

		Path file = Paths.get(tempFolder, "reporteComprasSuplidor" + usuario.getId() + ".pdf");
		if (Files.exists(file)) {
			String mimeType = URLConnection
					.guessContentTypeFromName(tempFolder + "reporteComprasSuplidor" + usuario.getId() + ".pdf");
			if (mimeType == null)
				mimeType = "application/octet-stream";
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition",
					String.format("inline; filename=\"" + "reporteComprasSuplidor" + usuario.getId() + ".pdf" + "\""));
			try {
				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();
				Files.delete(file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	@GetMapping("/formularioReporteCompraArticulo")
	public String formularioReporteCompraArticulo(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Articulo> articulos = serviceArticulos.buscarPorTienda(usuario.getAlmacen().getPropietario());
		model.addAttribute("articulos", articulos);
		model.addAttribute("dateAcct", new Date());
		return "compras/generarReporteCompraArticulo";
	}
	
	@PostMapping("/reporteCompraXArticulo")
	public void reporteCompraXArticulo(HttpSession session, HttpServletRequest request, 
			HttpServletResponse response, Integer idArticulo, String desde, String hasta) throws JRException, SQLException, ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<ReporteCompra> reporteCompras = new LinkedList<>();
		List<ReporteCompra> reporteCompraSeriales = new LinkedList<>();
		List<Articulo> articulos = new LinkedList<>();
		
		if(idArticulo == 0) {
			articulos = serviceArticulos.buscarPorTienda(usuario.getAlmacen().getPropietario());
		}else {
			articulos.add(serviceArticulos.buscarPorId(idArticulo));
		}
		
		//Compras finalizadas
		List<Compra> compras = serviceCompras.buscarPorAlmacenFechas(usuario.getAlmacen(), 
				formato.parse(desde),  formato.parse(hasta)).
				stream().filter(c -> c.getEn_proceso() == 0).collect(Collectors.toList());
		
		for (Compra compra : compras) {
			//detalles
			List<CompraDetalle> compraDetalles = serviceComprasDetalles.buscarPorCompra(compra);
			for (CompraDetalle compraDetalle : compraDetalles) {
				ReporteCompra reporteCompra = new ReporteCompra();
				reporteCompra.setCantidad(compraDetalle.getCantidad());
				reporteCompra.setFactura(compraDetalle.getCompra().getNo_factura());
				//verificamos si el articulo tiene serial
				if(compraDetalle.getCon_imei()==0) {
					//Sin serial
					reporteCompra.setArticulo(compraDetalle.getArticulo().getNombre());
					reporteCompra.setCosto(compraDetalle.getCosto());
//					reporteCompra.setPrecio((compraDetalle.getCantidad()*compraDetalle.getCosto())+compra.getItBis());
					reporteCompra.setId(compra.getId());
					reporteCompra.setTabla("compras");
					reporteCompra.setTotal((compraDetalle.getCantidad()*compraDetalle.getCosto())+compra.getItBis());
					
					if(idArticulo == 0) {
						reporteCompras.add(reporteCompra);
					}else {
						if(compraDetalle.getArticulo().getId() == idArticulo) {
							reporteCompras.add(reporteCompra);
						}
					}
				}else{
					//Con serial
					List<CompraDetalleSerial> compraDetalleSeriales = serviceComprasDetallesSeriales.buscarPorCompraDetalle(compraDetalle);
					int count = 0;
					for (CompraDetalleSerial compraDetalleSerial : compraDetalleSeriales) {
						if(count == 0) {
							ReporteCompra reporteCompraSerial = new ReporteCompra();
							reporteCompraSerial.setArticulo(compraDetalleSerial.getArticulo().getNombre());
							reporteCompraSerial.setCantidad(1);
							reporteCompraSerial.setCosto(compraDetalleSerial.getCosto());
							reporteCompraSerial.setFactura(compraDetalleSerial.getCompra().getNo_factura());
							reporteCompraSerial.setId(compra.getId());
							reporteCompraSerial.setSerial(compraDetalleSerial.getSerial());
//							reporteCompraSerial.setPrecio(precio);
							reporteCompraSerial.setTabla("compras");
							reporteCompraSerial.setTotal(compraDetalleSerial.getCosto());
							
							if(idArticulo == 0) {
								reporteCompraSeriales.add(reporteCompraSerial);
							}else {
								if(compraDetalle.getArticulo().getId() == idArticulo) {
									reporteCompraSeriales.add(reporteCompraSerial);
								}
							}
						}else {
							int coincidencia = 0;
							for (ReporteCompra compraSerial : reporteCompraSeriales) {
								//Verificamos articulos de igual nombre e igual costo
								if (compraSerial.getCosto().doubleValue() == compraSerial.getCosto().doubleValue()) {
									compraSerial.setSerial(
											compraSerial.getSerial() + "," + compraDetalleSerial.getSerial());
									coincidencia++;
								}
							}
							
							if(coincidencia==0 && reporteCompraSeriales.size()>1) {
								ReporteCompra reporteCompraSerial = new ReporteCompra();
								reporteCompraSerial.setArticulo(compraDetalleSerial.getArticulo().getNombre());
								reporteCompraSerial.setCantidad(1);
								reporteCompraSerial.setId(compra.getId());
								reporteCompraSerial.setTabla("compras");
								reporteCompraSerial.setCosto(compraDetalleSerial.getCosto());
								reporteCompraSerial.setFactura(compraDetalleSerial.getCompra().getNo_factura());
//								reporteCompraSerial.setPrecio(compraDetalleSerial.getPrecio_maximo());
								reporteCompraSerial.setTotal(compraDetalleSerial.getCosto());
								reporteCompraSerial.setSerial(compraDetalleSerial.getSerial());
								
								if(idArticulo == 0) {
									reporteCompraSeriales.add(reporteCompraSerial);
								}else {
									if(compraDetalle.getArticulo().getId() == idArticulo) {
										reporteCompraSeriales.add(reporteCompraSerial);
									}
								}
							}
						}
						count++;
					}
				}
			}
		}
		
		for (ReporteCompra compraSerial : reporteCompraSeriales) {
			String[] tempSerials = compraSerial.getSerial().split(",");
			compraSerial.setCantidad(tempSerials.length);
			compraSerial.setTotal(compraSerial.getCantidad()*compraSerial.getCosto());
			compraSerial.setArticulo(compraSerial.getArticulo()+" - "+compraSerial.getSerial());
			reporteCompras.add(compraSerial);
		}
		
		//Compilamos el reporte
		JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreportCompraArticulo);

		Map<String, Object> parameters = new HashMap<String, Object>();

		// convertimos la lista a JRBeanCollectionDataSource
		JRBeanCollectionDataSource articulosReporteJRBean = new JRBeanCollectionDataSource(reporteCompras);

		parameters.put("idUsuario", usuario.getId());
		parameters.put("imagen", rutaImagenes + usuario.getAlmacen().getImagen());
		parameters.put("total", reporteCompras.size());
		parameters.put("fechaDesde", desde);
		parameters.put("fechaHasta", hasta);
		parameters.put("author", usuario.getUsername());
		parameters.put("articulo", idArticulo == 0 ? "TODOS" : articulos.get(0).getNombre());
		parameters.put("reporteCompras", articulosReporteJRBean);

		// Objeto de impresion jr
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

		String dataDirectory = tempFolder + pathSeparator + "reporteComprasArticulo" + usuario.getId() + ".pdf";

		tempFolder += pathSeparator;

		JasperExportManager.exportReportToPdfFile(jasperPrint, dataDirectory);

		Path file = Paths.get(tempFolder, "reporteComprasArticulo" + usuario.getId() + ".pdf");
		if (Files.exists(file)) {
			String mimeType = URLConnection
					.guessContentTypeFromName(tempFolder + "reporteComprasArticulo" + usuario.getId() + ".pdf");
			if (mimeType == null)
				mimeType = "application/octet-stream";
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition",
					String.format("inline; filename=\"" + "reporteComprasArticulo" + usuario.getId() + ".pdf" + "\""));
			try {
				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();
				Files.delete(file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@GetMapping("/formularioReporteCompraUsuario")
	public String formularioReporteCompraUsuario(Model model, HttpSession session) {
		Usuario usuarioAcct = (Usuario) session.getAttribute("usuario");
		List<Usuario> usuarios = serviceUsuarios.buscarPorAlmacen(usuarioAcct.getAlmacen());
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("dateAcct", new Date());
		return "compras/generarReporteCompraUsuario";
	}
	
	@PostMapping("/reporteCompraXUsuario")
	public void reporteCompraXUsuario(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			Integer idUsuario, String desde, String hasta) throws JRException, SQLException, ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Usuario usuarioAcct = (Usuario) session.getAttribute("usuario");
		List<ReporteCompra> reporteCompras = new LinkedList<>();
		List<ReporteCompra> reporteCompraSeriales = new LinkedList<>();
		List<Usuario> usuarios = new LinkedList<>();

		if (idUsuario == 0) {
			usuarios = serviceUsuarios.buscarPorAlmacen(usuarioAcct.getAlmacen());
		} else {
			usuarios.add(serviceUsuarios.buscarPorId(idUsuario));
		}

		// Compras finalizadas
		List<Compra> compras = serviceCompras.buscarPorAlmacenFechas(usuarioAcct.getAlmacen(), 
				formato.parse(desde), formato.parse(hasta))
				.stream().filter(c -> c.getEn_proceso() == 0).collect(Collectors.toList());
		
		for (Compra compra : compras) {
			// detalles
			List<CompraDetalle> compraDetalles = serviceComprasDetalles.buscarPorCompra(compra);
			for (CompraDetalle compraDetalle : compraDetalles) {
				ReporteCompra reporteCompra = new ReporteCompra();
				reporteCompra.setCantidad(compraDetalle.getCantidad());
				reporteCompra.setFactura(compraDetalle.getCompra().getNo_factura());
				// verificamos si el articulo tiene serial
				if (compraDetalle.getCon_imei() == 0) {
					// Sin serial
					reporteCompra.setArticulo(compraDetalle.getArticulo().getNombre());
					reporteCompra.setCosto(compraDetalle.getCosto());
//					reporteCompra.setPrecio((compraDetalle.getCantidad()*compraDetalle.getCosto())+compra.getItBis());
					reporteCompra.setId(compra.getId());
					reporteCompra.setTabla("compras");
					reporteCompra.setTotal((compraDetalle.getCantidad() * compraDetalle.getCosto()) + compra.getItBis());

					if (idUsuario == 0) {
						reporteCompras.add(reporteCompra);
					} else {
						if (compra.getUsuario().getId() == idUsuario) {
							reporteCompras.add(reporteCompra);
						}
					}
				} else {
					// Con serial
					List<CompraDetalleSerial> compraDetalleSeriales = serviceComprasDetallesSeriales.buscarPorCompraDetalle(compraDetalle);
					int count = 0;
					for (CompraDetalleSerial compraDetalleSerial : compraDetalleSeriales) {
						if (count == 0) {
							ReporteCompra reporteCompraSerial = new ReporteCompra();
							reporteCompraSerial.setArticulo(compraDetalleSerial.getArticulo().getNombre());
							reporteCompraSerial.setCantidad(1);
							reporteCompraSerial.setCosto(compraDetalleSerial.getCosto());
							reporteCompraSerial.setFactura(compraDetalleSerial.getCompra().getNo_factura());
							reporteCompraSerial.setId(compra.getId());
							reporteCompraSerial.setSerial(compraDetalleSerial.getSerial());
//							reporteCompraSerial.setPrecio(precio);
							reporteCompraSerial.setTabla("compras");
							reporteCompraSerial.setTotal(compraDetalleSerial.getCosto());

							if (idUsuario == 0) {
								reporteCompraSeriales.add(reporteCompraSerial);
							} else {
								if (compra.getUsuario().getId() == idUsuario) {
									reporteCompraSeriales.add(reporteCompraSerial);
								}
							}
						} else {
							int coincidencia = 0;
							for (ReporteCompra compraSerial : reporteCompraSeriales) {
								// Verificamos articulos de igual nombre e igual costo
								if (compraSerial.getCosto().doubleValue() == compraSerial.getCosto().doubleValue()) {
									compraSerial.setSerial(compraSerial.getSerial() + "," + compraDetalleSerial.getSerial());
									coincidencia++;
								}
							}

							if (coincidencia == 0 && reporteCompraSeriales.size() > 1) {
								ReporteCompra reporteCompraSerial = new ReporteCompra();
								reporteCompraSerial.setArticulo(compraDetalleSerial.getArticulo().getNombre());
								reporteCompraSerial.setCantidad(1);
								reporteCompraSerial.setId(compra.getId());
								reporteCompraSerial.setTabla("compras");
								reporteCompraSerial.setCosto(compraDetalleSerial.getCosto());
								reporteCompraSerial.setFactura(compraDetalleSerial.getCompra().getNo_factura());
//								reporteCompraSerial.setPrecio(compraDetalleSerial.getPrecio_maximo());
								reporteCompraSerial.setTotal(compraDetalleSerial.getCosto());
								reporteCompraSerial.setSerial(compraDetalleSerial.getSerial());

								if (idUsuario == 0) {
									reporteCompraSeriales.add(reporteCompraSerial);
								} else {
									if (compra.getUsuario().getId() == idUsuario) {
										reporteCompraSeriales.add(reporteCompraSerial);
									}
								}
							}
						}
						count++;
					}
				}
			}
		}
		
		for (ReporteCompra compraSerial : reporteCompraSeriales) {
			String[] tempSerials = compraSerial.getSerial().split(",");
			compraSerial.setCantidad(tempSerials.length);
			compraSerial.setTotal(compraSerial.getCantidad()*compraSerial.getCosto());
			compraSerial.setArticulo(compraSerial.getArticulo()+" - "+compraSerial.getSerial());
			reporteCompras.add(compraSerial);
		}
		
		//Compilamos el reporte
		JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreportCompraUsuario);

		Map<String, Object> parameters = new HashMap<String, Object>();

		// convertimos la lista a JRBeanCollectionDataSource
		JRBeanCollectionDataSource articulosReporteJRBean = new JRBeanCollectionDataSource(reporteCompras);

		parameters.put("idUsuario", usuarioAcct.getId());
		parameters.put("imagen", rutaImagenes + usuarioAcct.getAlmacen().getImagen());
		parameters.put("total", reporteCompras.size());
		parameters.put("fechaDesde", desde);
		parameters.put("fechaHasta", hasta);
		parameters.put("author", usuarioAcct.getUsername());
		parameters.put("usuario", idUsuario == 0 ? "TODOS" : usuarios.get(0).getNombre());
		parameters.put("reporteCompras", articulosReporteJRBean);

		// Objeto de impresion jr
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

		String dataDirectory = tempFolder + pathSeparator + "reporteComprasUsuario" + usuarioAcct.getId() + ".pdf";

		tempFolder += pathSeparator;

		JasperExportManager.exportReportToPdfFile(jasperPrint, dataDirectory);

		Path file = Paths.get(tempFolder, "reporteComprasUsuario" + usuarioAcct.getId() + ".pdf");
		if (Files.exists(file)) {
			String mimeType = URLConnection
					.guessContentTypeFromName(tempFolder + "reporteComprasUsuario" + usuarioAcct.getId() + ".pdf");
			if (mimeType == null)
				mimeType = "application/octet-stream";
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition",
					String.format("inline; filename=\"" + "reporteComprasUsuario" + usuarioAcct.getId() + ".pdf" + "\""));
			try {
				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();
				Files.delete(file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}
	
	@GetMapping("/formularioReporteCompraCategoria")
	public String formularioReporteCompraCategoria(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Categoria> categorias = serviceCategorias.buscarPorPropietario(usuario.getAlmacen().getPropietario());
		model.addAttribute("categorias", categorias);
		model.addAttribute("dateAcct", new Date());
		return "compras/generarReporteCompraCategoria";
	}
	
	@PostMapping("/reporteCompraXCategoria")
	public void reporteCompraXCategoria(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			Integer idCategoria, String desde, String hasta) throws JRException, SQLException, ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<ReporteCompra> reporteCompras = new LinkedList<>();
		List<ReporteCompra> reporteCompraSeriales = new LinkedList<>();
		List<Categoria> categorias = new LinkedList<>();

		if (idCategoria == 0) {
			categorias = serviceCategorias.buscarPorPropietario(usuario.getAlmacen().getPropietario());
		} else {
			categorias.add(serviceCategorias.buscarPorId(idCategoria));
		}
		
		//Compras finalizadas
		List<Compra> compras = serviceCompras.buscarPorAlmacenFechas(usuario.getAlmacen(), 
		formato.parse(desde),  formato.parse(hasta)).
		stream().filter(c -> c.getEn_proceso() == 0).collect(Collectors.toList());
		
		for (Compra compra : compras) {
			//detalles
			List<CompraDetalle> compraDetalles = serviceComprasDetalles.buscarPorCompra(compra);
			for (CompraDetalle compraDetalle : compraDetalles) {
				ReporteCompra reporteCompra = new ReporteCompra();
				reporteCompra.setCantidad(compraDetalle.getCantidad());
				reporteCompra.setFactura(compraDetalle.getCompra().getNo_factura());
				//verificamos si el articulo tiene serial
				if(compraDetalle.getCon_imei()==0) {
					//Sin serial
					reporteCompra.setArticulo(compraDetalle.getArticulo().getNombre());
					reporteCompra.setCosto(compraDetalle.getCosto());
//					reporteCompra.setPrecio((compraDetalle.getCantidad()*compraDetalle.getCosto())+compra.getItBis());
					reporteCompra.setId(compra.getId());
					reporteCompra.setTabla("compras");
					reporteCompra.setTotal((compraDetalle.getCantidad()*compraDetalle.getCosto())+compra.getItBis());
					
					if(idCategoria==0) {
						reporteCompras.add(reporteCompra);
					}else {
						if (compraDetalle.getArticulo().getCategoria().getId() == idCategoria) {
							reporteCompras.add(reporteCompra);
						}
					}
					
				}else{
					//Con serial
					List<CompraDetalleSerial> compraDetalleSeriales = serviceComprasDetallesSeriales.buscarPorCompraDetalle(compraDetalle);
					int count = 0;
					for (CompraDetalleSerial compraDetalleSerial : compraDetalleSeriales) {
						if(count == 0) {
							ReporteCompra reporteCompraSerial = new ReporteCompra();
							reporteCompraSerial.setArticulo(compraDetalleSerial.getArticulo().getNombre());
							reporteCompraSerial.setCantidad(1);
							reporteCompraSerial.setCosto(compraDetalleSerial.getCosto());
							reporteCompraSerial.setFactura(compraDetalleSerial.getCompra().getNo_factura());
							reporteCompraSerial.setId(compra.getId());
							reporteCompraSerial.setSerial(compraDetalleSerial.getSerial());
//							reporteCompraSerial.setPrecio(precio);
							reporteCompraSerial.setTabla("compras");
							reporteCompraSerial.setTotal(compraDetalleSerial.getCosto());
							
							if(idCategoria==0) {
								reporteCompraSeriales.add(reporteCompraSerial);
							}else {
								if (compraDetalle.getArticulo().getCategoria().getId() == idCategoria) {
									reporteCompraSeriales.add(reporteCompraSerial);
								}
							}
						}else {
							int coincidencia = 0;
							for (ReporteCompra compraSerial : reporteCompraSeriales) {
								//Verificamos articulos de igual nombre e igual costo
								if (compraSerial.getCosto().doubleValue() == compraSerial.getCosto().doubleValue()) {
									compraSerial.setSerial(
											compraSerial.getSerial() + "," + compraDetalleSerial.getSerial());
									coincidencia++;
								}
							}
							
							if(coincidencia==0 && reporteCompraSeriales.size()>1) {
								ReporteCompra reporteCompraSerial = new ReporteCompra();
								reporteCompraSerial.setArticulo(compraDetalleSerial.getArticulo().getNombre());
								reporteCompraSerial.setCantidad(1);
								reporteCompraSerial.setId(compra.getId());
								reporteCompraSerial.setTabla("compras");
								reporteCompraSerial.setCosto(compraDetalleSerial.getCosto());
								reporteCompraSerial.setFactura(compraDetalleSerial.getCompra().getNo_factura());
//								reporteCompraSerial.setPrecio(compraDetalleSerial.getPrecio_maximo());
								reporteCompraSerial.setTotal(compraDetalleSerial.getCosto());
								reporteCompraSerial.setSerial(compraDetalleSerial.getSerial());
								
								if(idCategoria==0) {
									reporteCompraSeriales.add(reporteCompraSerial);
								}else {
									if (compraDetalle.getArticulo().getCategoria().getId() == idCategoria) {
										reporteCompraSeriales.add(reporteCompraSerial);
									}
								}
							}
						}
						count++;
					}
				}
			}
		}

		for (ReporteCompra compraSerial : reporteCompraSeriales) {
			String[] tempSerials = compraSerial.getSerial().split(",");
			compraSerial.setCantidad(tempSerials.length);
			compraSerial.setTotal(compraSerial.getCantidad() * compraSerial.getCosto());
			compraSerial.setArticulo(compraSerial.getArticulo() + " - " + compraSerial.getSerial());
			reporteCompras.add(compraSerial);
		}
		
		//Compilamos el reporte
		JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreportCompraCategoria);

		Map<String, Object> parameters = new HashMap<String, Object>();

		// convertimos la lista a JRBeanCollectionDataSource
		JRBeanCollectionDataSource articulosReporteJRBean = new JRBeanCollectionDataSource(reporteCompras);

		parameters.put("idUsuario", usuario.getId());
		parameters.put("imagen", rutaImagenes + usuario.getAlmacen().getImagen());
		parameters.put("total", reporteCompras.size());
		parameters.put("fechaDesde", desde);
		parameters.put("fechaHasta", hasta);
		parameters.put("author", usuario.getUsername());
		parameters.put("categoria", idCategoria == 0 ? "TODAS" : categorias.get(0).getNombre());
		parameters.put("reporteCompras", articulosReporteJRBean);

		// Objeto de impresion jr
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

		String dataDirectory = tempFolder + pathSeparator + "reporteComprasCategoria" + usuario.getId() + ".pdf";

		tempFolder += pathSeparator;

		JasperExportManager.exportReportToPdfFile(jasperPrint, dataDirectory);

		Path file = Paths.get(tempFolder, "reporteComprasCategoria" + usuario.getId() + ".pdf");
		if (Files.exists(file)) {
			String mimeType = URLConnection
					.guessContentTypeFromName(tempFolder + "reporteComprasCategoria" + usuario.getId() + ".pdf");
			if (mimeType == null)
				mimeType = "application/octet-stream";
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition",
					String.format("inline; filename=\"" + "reporteComprasCategoria" + usuario.getId() + ".pdf" + "\""));
			try {
				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();
				Files.delete(file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
	
	}

	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
}
