package com.developergg.app.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.developergg.app.model.ArticuloAjuste;
import com.developergg.app.model.ArticuloSerial;
import com.developergg.app.model.DevolucionFactura;
import com.developergg.app.model.DevolucionFacturaDetalle;
import com.developergg.app.model.DevolucionFacturaSerial;
import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetalle;
import com.developergg.app.model.FacturaDetalleSerial;
import com.developergg.app.model.FacturaDetalleServicio;
import com.developergg.app.model.FacturaDetalleTaller;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IArticulosAjustesService;
import com.developergg.app.service.IArticulosSeriales;
import com.developergg.app.service.IDevolucionesFacturasDetallesService;
import com.developergg.app.service.IDevolucionesFacturasSerialesService;
import com.developergg.app.service.IDevolucionesFacturasService;
import com.developergg.app.service.IFacturasDetallesSerialesService;
import com.developergg.app.service.IFacturasDetallesService;
import com.developergg.app.service.IFacturasDetallesServiciosService;
import com.developergg.app.service.IFacturasDetallesTallerService;
import com.developergg.app.service.IFacturasService;

@Controller
@RequestMapping("/devolucionesFacturas")
public class DevolucionesFacturasController {
	
	@Autowired
	private IArticulosSeriales serviceArticulosSeriales;
	
	@Autowired
	private IArticulosAjustesService serviceArticulosAjustes;
	
	@Autowired
	private IFacturasService serviceFacturas;
	
	@Autowired
	private IFacturasDetallesService serviceFacturasDetalles;
	
	@Autowired
	private IFacturasDetallesTallerService serviceFacturasTaller;
	
	@Autowired
	private IFacturasDetallesServiciosService serviceFacturasDetallesServicios;
	
	@Autowired
	private IFacturasDetallesSerialesService serviceFacturasDetallesSeriales;
	
	@Autowired
	private IFacturasDetallesTallerService serviceFacturasDetallesTalleres;
	
	@Autowired
	private IDevolucionesFacturasService serviceDevolucionesFacturas;
	
	@Autowired
	private IDevolucionesFacturasDetallesService serviceDevolucionesFacturasDetalles;
	
	@Autowired
	private IDevolucionesFacturasSerialesService serviceDevolucionesFacturasSeriales;
	
	@GetMapping("/")
	public String listaDevoluciones(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<DevolucionFactura> devoluciones = serviceDevolucionesFacturas.buscarPorAlmacen(usuario.getAlmacen());
		model.addAttribute("devoluciones", devoluciones);
		return "devoluciones/listaDevoluciones";
	}
	
	@GetMapping("/devolucionFactura")
	public String devolucionFactura(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Factura> facturas = serviceFacturas.buscarPorAlmacen(usuario.getAlmacen());
		List<Factura> listaFacturas = new LinkedList<>();
		for (Factura factura : facturas) {
			int cargarDatos = 0;
			//verificamos el detalle de la factura articulos sin serial
			List<FacturaDetalle> detalles = serviceFacturasDetalles.buscarPorFactura(factura);
			if(!detalles.isEmpty()) {
				cargarDatos = 1;
			}else {
				List<FacturaDetalleTaller> talleres = serviceFacturasTaller.buscarPorFactura(factura);
				for (FacturaDetalleTaller taller : talleres) {
					if(taller.getArticulo()!=null) {
						cargarDatos = 1;
						continue;
					}
				}
			}
			if(cargarDatos == 1) {
				listaFacturas.add(factura);
			}
		}
		model.addAttribute("facturas", listaFacturas);
		return "devoluciones/facturas/listaFacturas";
	}
	
	@GetMapping("/devolver/{id}")
	public String hacerDevolucion(Model model, @PathVariable("id") Integer id) {
		Factura factura = serviceFacturas.buscarPorId(id);
		List<FacturaDetalle> detalles = serviceFacturasDetalles.buscarPorFactura(factura);
		List<FacturaDetalleTaller> talleres = serviceFacturasTaller.buscarPorFactura(factura);
		List<FacturaDetalleServicio> servicios = serviceFacturasDetallesServicios.buscarPorFactura(factura);
		model.addAttribute("factura", factura);
		model.addAttribute("facturaServicios",servicios);
		model.addAttribute("facturaDetalles", detalles);
		model.addAttribute("facturaTalleres", talleres);
		return "devoluciones/facturas/devolucion";
	}
	
	@GetMapping("/cuerpoDevolucion/{id}")
	public String cargarCuerpoDevolucion(Model model, @PathVariable("id") Integer id) {
		Factura factura = serviceFacturas.buscarPorId(id);
		List<FacturaDetalle> detalles = serviceFacturasDetalles.buscarPorFactura(factura);
		List<FacturaDetalleTaller> talleres = serviceFacturasTaller.buscarPorFactura(factura);
		List<FacturaDetalleServicio> servicios = serviceFacturasDetallesServicios.buscarPorFactura(factura);
		
		//verificamos si el detalle tiene una devolucion temporal
		for (FacturaDetalle detalle : detalles) {
			//buscamos la lista de seriales
			List<FacturaDetalleSerial> seriales = serviceFacturasDetallesSeriales.buscarPorDetalle(detalle);
			if(!seriales.isEmpty()) {
				int cantTempDevlover = 0;
				for (FacturaDetalleSerial serial : seriales) {
					if(serial.getTempDevuelto() == 1) {
						cantTempDevlover++;
					}
				}
				detalle.setTemp_devolver(cantTempDevlover);
			}
		}
		
		model.addAttribute("facturaServicios",servicios);
		model.addAttribute("facturaDetalles", detalles);
		model.addAttribute("facturaTalleres", talleres);
		return "devoluciones/facturas/cuerpoDevolucion :: cuerpoDevolucion";
	}
	
	@GetMapping("/verificarTipoArticulo/{id}")
	public String verificarTipoArticulo(Model model, @PathVariable("id") Integer id) {
		int conSerial = 0;
		FacturaDetalle detalle = serviceFacturasDetalles.buscarPorId(id);
		if(detalle.getArticulo().getImei().equals("SI") || detalle.getArticulo().getImei().equals("1")) {
			conSerial = 1;
		}
		model.addAttribute("idArticulo", detalle.getArticulo().getId());
		model.addAttribute("conSerial", conSerial);
		model.addAttribute("nombreArticulo", detalle.getArticulo().getNombre());
		model.addAttribute("precioArticulo", detalle.getPrecio());
		model.addAttribute("cantidadArticulo", detalle.getCantidad());
		model.addAttribute("cantidadDevuelta", detalle.getCantidad_devuelta());
		return "devoluciones/facturas/devolucion :: #tipoArticulo";
	}
	
	@GetMapping("/ajax/obtenerSeriales/{id}")
	public String obtenerSeriales(Model model, @PathVariable("id") Integer idFacturaDetalle) {
		FacturaDetalle facturaDetalle = serviceFacturasDetalles.buscarPorId(idFacturaDetalle);
		List<FacturaDetalleSerial> seriales = new LinkedList<>();
		if(facturaDetalle.getImei().equals("SI") || facturaDetalle.getImei().equals("1")){
			seriales = serviceFacturasDetallesSeriales.buscarPorDetalle(facturaDetalle);
		}
		model.addAttribute("listaSeriales", seriales);
		model.addAttribute("idDetalleArticulo", facturaDetalle.getId());
		model.addAttribute("detalleArticulo", facturaDetalle.getArticulo().getNombre());
		return "devoluciones/facturas/devolucion :: infoSeriales";
	}
	
	@GetMapping("/ajax/infoSerialesADevolver/{id}")
	public String infoSerialesADevolver(Model model, @PathVariable("id") Integer idFacturaDetalle) {
		FacturaDetalle facturaDetalle = serviceFacturasDetalles.buscarPorId(idFacturaDetalle);
		List<FacturaDetalleSerial> seriales = new LinkedList<>();
		if(facturaDetalle.getImei().equals("SI") || facturaDetalle.getImei().equals("1")){
			//Seriales que no esten devueltos
			seriales = serviceFacturasDetallesSeriales.buscarPorDetalle(facturaDetalle).
					stream().filter(s -> s.getDevuelto() == 0).collect(Collectors.toList());
		}
		model.addAttribute("listaSeriales", seriales);
		model.addAttribute("idDetalleArticulo", facturaDetalle.getId());
		model.addAttribute("detalleArticulo", facturaDetalle.getArticulo().getNombre());
		model.addAttribute("itbisArticuloConSerial", facturaDetalle.getFactura().getComprobanteFiscal().getValor_itbis());
		return "devoluciones/facturas/devolucion :: #serialesADevolver";
	}
	
	@GetMapping("/ajax/obtenerPrecios/{id}/{seriales}")
	public String obtenerPrecios(Model model, @PathVariable("id") Integer idFacturaDetalle,
			@PathVariable("seriales") String idSeriales) {
		String[] serialesArray = idSeriales.split(",");
		FacturaDetalle facturaDetalle = serviceFacturasDetalles.buscarPorId(idFacturaDetalle);
		List<FacturaDetalleSerial> listaSeriales = new LinkedList<>();
		List<FacturaDetalleSerial> listaSerialesnew = new LinkedList<>();
		Double precio = 0.0;
		if(facturaDetalle.getImei().equals("SI") || facturaDetalle.getImei().equals("1")){
			listaSeriales = serviceFacturasDetallesSeriales.buscarPorDetalle(facturaDetalle);
			for (FacturaDetalleSerial facturaDetalleSerial : listaSeriales) {
				for (String serialArray : serialesArray) {
					if(facturaDetalleSerial.getId().toString().equals(serialArray)) {
						listaSerialesnew.add(facturaDetalleSerial);
						precio+=facturaDetalleSerial.getPrecio();
					}
				}
			}
		}
		Integer itbis = facturaDetalle.getFactura().getComprobanteFiscal().getValor_itbis();
		Double itbisTemp = itbis.doubleValue()/100;
		itbisTemp*=precio;
		Double precioTotal = precio+itbisTemp;
		model.addAttribute("totalPrecioSerial", precio);
		model.addAttribute("totalPrecioSerialItbis", precioTotal);
		return "devoluciones/facturas/devolucion :: #precioArticuloConSerial";
	}
	
	@PostMapping("/ajax/devolverSeriales")
	public String devolverSeriales(@RequestParam(name = "facturaDetalle") Integer facturaDetalleId,
			@RequestParam(name = "seriales") String seriales) {
		FacturaDetalle facturaDetalle = serviceFacturasDetalles.buscarPorId(facturaDetalleId);
		List<FacturaDetalleSerial> serialesFactura = serviceFacturasDetallesSeriales.buscarPorDetalle(facturaDetalle);
		String[] serialesArray = seriales.split(",");
		List<FacturaDetalleSerial> newSeriales = new LinkedList<>();
		for (FacturaDetalleSerial serialFactura : serialesFactura) {
			for (String serialArray : serialesArray) {
				if(serialFactura.getId().toString().equals(serialArray)) {
					newSeriales.add(serialFactura);
				}else {
					serialFactura.setTempDevuelto(0);
					serviceFacturasDetallesSeriales.guardar(serialFactura);
				}
			}
		}
				
		if(!newSeriales.isEmpty()) {
			for (FacturaDetalleSerial facturaDetalleSerial : newSeriales) {
				facturaDetalleSerial.setTempDevuelto(1);
				serviceFacturasDetallesSeriales.guardar(facturaDetalleSerial);
			}
		}
		return "devoluciones/facturas/devolucion :: #temporal";
	}
	
	@PostMapping("/ajax/devolverSinSeriales")
	public String devolverSinSeriales(@RequestParam(name = "facturaDetalle") Integer facturaDetalleId,
			@RequestParam(name = "cantidad") Integer cantidad) {
		FacturaDetalle facturaDetalle = serviceFacturasDetalles.buscarPorId(facturaDetalleId);
		facturaDetalle.setTemp_devolver(cantidad);
		serviceFacturasDetalles.guardar(facturaDetalle);
		return "devoluciones/facturas/devolucion :: #temporal";
	}
	
	@GetMapping("/ajax/infoArticuloTaller/{id}")
	public String infoArticuloTaller(Model model, @PathVariable("id") Integer idFacturaTaller) {
		FacturaDetalleTaller facturaTaller = serviceFacturasTaller.buscarPorId(idFacturaTaller);
		model.addAttribute("articuloTaller", facturaTaller.getArticulo().getNombre());
		model.addAttribute("cantidadTaller", facturaTaller.getCantidad()-facturaTaller.getCantidad_devuelta());
		model.addAttribute("precioTaller", facturaTaller.getPrecio());
		model.addAttribute("itbisTaller", facturaTaller.getItbis());
		model.addAttribute("subTotalTaller", facturaTaller.getSubtotal());
		return "devoluciones/facturas/devolucion :: #infoTaller";
	}
	
	@PostMapping("/ajax/devolverArticuloTaller")
	public String devolverArticuloTaller(@RequestParam(name = "facturaDetalle") Integer facturaDetalleId,
			@RequestParam(name = "cantidad") Integer cantidad) {
		FacturaDetalleTaller facturaDetalleTaller = serviceFacturasDetallesTalleres.buscarPorId(facturaDetalleId);
		facturaDetalleTaller.setTemp_devolver(cantidad);
		serviceFacturasDetallesTalleres.guardar(facturaDetalleTaller);
		return "devoluciones/facturas/devolucion :: #temporal";
	}
	
	@PostMapping("/ajax/crearDevolucion")
	public String crearDevolucion(@RequestParam(name = "idFactura") Integer idFactura, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Factura factura = serviceFacturas.buscarPorId(idFactura);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
		String now = LocalDateTime.now().format(formatter);
		
		Double monto = 0.0;
		Integer itbis = factura.getComprobanteFiscal().getValor_itbis();
		
		//Creamos la devolucion
		DevolucionFactura devolucionFactura = new DevolucionFactura();
		devolucionFactura.setAlmacen(factura.getAlmacen());
		devolucionFactura.setCodigo(factura.getCodigo());
		devolucionFactura.setFactura(factura);
		devolucionFactura.setFecha(new Date());
		devolucionFactura.setHora(now);
		devolucionFactura.setUsuario(usuario);
		serviceDevolucionesFacturas.guardar(devolucionFactura);
		
		//Recorremos los detalles de la factura (Productos con serial y sin serial)
		List<FacturaDetalle> facturaDetalles = serviceFacturasDetalles.buscarPorFactura(factura);
		for (FacturaDetalle facturaDetalle : facturaDetalles) {
			//No tiene serial
			if(facturaDetalle.getImei().equals("NO") || facturaDetalle.getImei().equals("0")) {
				if(facturaDetalle.getTemp_devolver()>0) {
					//Verificamos si el detalle tiene devolucion para calcular la cantidad
					List<DevolucionFacturaDetalle> devolucionFacturaDetalles = serviceDevolucionesFacturasDetalles.buscarPorFacturaDetalle(facturaDetalle);
					int cantidadDisponible = 0;
					int cantidadDevuelta = 0;
					if(!devolucionFacturaDetalles.isEmpty()) {
						for (DevolucionFacturaDetalle devolucionFacturaDetalle : devolucionFacturaDetalles) {
							cantidadDevuelta += devolucionFacturaDetalle.getCantidad();
						}
					}else {
						cantidadDevuelta += 0;
					}
					cantidadDisponible = facturaDetalle.getCantidad()-cantidadDevuelta;
					
					if(cantidadDisponible > 0) {
						DevolucionFacturaDetalle devolucionFacturaDetalle = new DevolucionFacturaDetalle();
						devolucionFacturaDetalle.setCantidad(facturaDetalle.getTemp_devolver());
						devolucionFacturaDetalle.setCantidad_factura(cantidadDisponible);
						devolucionFacturaDetalle.setCantidad_restante(cantidadDisponible-facturaDetalle.getTemp_devolver());
						devolucionFacturaDetalle.setDevolucionFactura(devolucionFactura);
						devolucionFacturaDetalle.setFacturaDetalle(facturaDetalle);
						devolucionFacturaDetalle.setPrecio(facturaDetalle.getPrecio());
						serviceDevolucionesFacturasDetalles.guardar(devolucionFacturaDetalle);

						monto+=devolucionFacturaDetalle.getPrecio()*facturaDetalle.getTemp_devolver();
						
						//Vuelve al inventario
						//ordenamos el ultimo elemento de la lista
						List<ArticuloAjuste> lista = serviceArticulosAjustes.buscarPorArticuloYAlmacen(facturaDetalle.getArticulo(), usuario.getAlmacen());
						ArticuloAjuste newArticuloAjuste = lista.get(lista.size()-1);
						ArticuloAjuste newArticuloAjusteDefinitive = new ArticuloAjuste();
						newArticuloAjusteDefinitive.setAlmacen(usuario.getAlmacen());
						newArticuloAjusteDefinitive.setFecha(new Date());
						newArticuloAjusteDefinitive.setUsuario(usuario);
						newArticuloAjusteDefinitive.setTipoMovimiento("Entrada");
						newArticuloAjusteDefinitive.setCantidad(facturaDetalle.getTemp_devolver());
						newArticuloAjusteDefinitive.setCosto(facturaDetalle.getCosto());
						newArticuloAjusteDefinitive.setExistencia(newArticuloAjuste.getDisponible());
						newArticuloAjusteDefinitive.setArticulo(facturaDetalle.getArticulo());
						newArticuloAjusteDefinitive.setNo_factura(facturaDetalle.getFactura().getCodigo().toString());
						newArticuloAjusteDefinitive.setDisponible(newArticuloAjusteDefinitive.getExistencia()+newArticuloAjusteDefinitive.getCantidad());
						serviceArticulosAjustes.guardar(newArticuloAjusteDefinitive);
						
						facturaDetalle.setCantidad_devuelta(facturaDetalle.getCantidad_devuelta()+facturaDetalle.getTemp_devolver());
						facturaDetalle.setTemp_devolver(0);
						serviceFacturasDetalles.guardar(facturaDetalle);
					}
				}
			}else {
				//Tiene serial
				int tempDevuelto = 0;
				List<FacturaDetalleSerial> facturaDetalleSeriales = serviceFacturasDetallesSeriales.buscarPorDetalle(facturaDetalle);
				for (FacturaDetalleSerial facturaDetalleSerial : facturaDetalleSeriales) {
					if(facturaDetalleSerial.getTempDevuelto()==1 && facturaDetalleSerial.getDevuelto() == 0) {
						DevolucionFacturaSerial devolucionFacturaSerial = new DevolucionFacturaSerial();
						devolucionFacturaSerial.setArticuloSerial(facturaDetalleSerial.getArticuloSerial());
						devolucionFacturaSerial.setFacturaDetalle(facturaDetalle);
						devolucionFacturaSerial.setFacturaDetalleSerial(facturaDetalleSerial);
						devolucionFacturaSerial.setSerial(facturaDetalleSerial.getSerial());
						serviceDevolucionesFacturasSeriales.guardar(devolucionFacturaSerial);
						facturaDetalleSerial.setTempDevuelto(0);
						facturaDetalleSerial.setDevuelto(1);
						serviceFacturasDetallesSeriales.guardar(facturaDetalleSerial);
						tempDevuelto++;
						//Vuelve al inventario
						ArticuloSerial articuloSerial = facturaDetalleSerial.getArticuloSerial();
						articuloSerial.setEstado("Disponible");
						articuloSerial.setDevuelto(1); 
						articuloSerial.setEn_uso(0);
						serviceArticulosSeriales.guardar(articuloSerial);
						
						monto+=facturaDetalleSerial.getPrecio();
					}
					facturaDetalle.setCantidad_devuelta(tempDevuelto);
					serviceFacturasDetalles.guardar(facturaDetalle);
				}
			}
		}
		
		//talleres
		List<FacturaDetalleTaller> facturaDetalleTalleres = serviceFacturasDetallesTalleres.buscarPorFactura(factura);
		for (FacturaDetalleTaller facturaDetalleTaller : facturaDetalleTalleres) {
			if(facturaDetalleTaller.getArticulo()!=null && facturaDetalleTaller.getTemp_devolver()>0) {
				
				//Verificamos si el detalle tiene devolucion para calcular la cantidad
				List<DevolucionFacturaDetalle> devolucionFacturaDetalles = serviceDevolucionesFacturasDetalles.buscarPorFacturaDetalleTaller(facturaDetalleTaller);
				int cantidadDisponible = 0;
				int cantidadDevuelta = 0;
				if(!devolucionFacturaDetalles.isEmpty()) {
					for (DevolucionFacturaDetalle devolucionFacturaDetalle : devolucionFacturaDetalles) {
						cantidadDevuelta += devolucionFacturaDetalle.getCantidad();
					}
				}else {
					cantidadDevuelta += 0;
				}
				cantidadDisponible = facturaDetalleTaller.getCantidad()-cantidadDevuelta;
				
				if(cantidadDisponible > 0) {
					DevolucionFacturaDetalle devolucionFacturaDetalle = new DevolucionFacturaDetalle();
					devolucionFacturaDetalle.setCantidad(facturaDetalleTaller.getTemp_devolver());
					devolucionFacturaDetalle.setCantidad_factura(cantidadDisponible);
					devolucionFacturaDetalle.setCantidad_restante(cantidadDisponible-facturaDetalleTaller.getTemp_devolver());
					devolucionFacturaDetalle.setDevolucionFactura(devolucionFactura);
					devolucionFacturaDetalle.setFacturaDetalleTaller(facturaDetalleTaller);
					devolucionFacturaDetalle.setPrecio(facturaDetalleTaller.getPrecio());
					serviceDevolucionesFacturasDetalles.guardar(devolucionFacturaDetalle);
					facturaDetalleTaller.setCantidad_devuelta(facturaDetalleTaller.getCantidad_devuelta()+facturaDetalleTaller.getTemp_devolver());

					//Vuelve al inventario
					//ordenamos el ultimo elemento de la lista
					List<ArticuloAjuste> lista = serviceArticulosAjustes.buscarPorArticuloYAlmacen(facturaDetalleTaller.getArticulo(), usuario.getAlmacen());
					ArticuloAjuste newArticuloAjuste = lista.get(lista.size()-1);
					ArticuloAjuste newArticuloAjusteDefinitive = new ArticuloAjuste();
					newArticuloAjusteDefinitive.setAlmacen(usuario.getAlmacen());
					newArticuloAjusteDefinitive.setFecha(new Date());
					newArticuloAjusteDefinitive.setUsuario(usuario);
					newArticuloAjusteDefinitive.setTipoMovimiento("Entrada");
					newArticuloAjusteDefinitive.setCantidad(facturaDetalleTaller.getTemp_devolver());
					newArticuloAjusteDefinitive.setCosto(facturaDetalleTaller.getArticulo().getCosto());
					newArticuloAjusteDefinitive.setExistencia(newArticuloAjuste.getDisponible());
					newArticuloAjusteDefinitive.setArticulo(facturaDetalleTaller.getArticulo());
					newArticuloAjusteDefinitive.setNo_factura(facturaDetalleTaller.getFactura().getCodigo().toString());
					newArticuloAjusteDefinitive.setUsuario(usuario);
					newArticuloAjusteDefinitive.setDisponible(newArticuloAjusteDefinitive.getExistencia()+newArticuloAjusteDefinitive.getCantidad());
					serviceArticulosAjustes.guardar(newArticuloAjusteDefinitive);
					facturaDetalleTaller.setTemp_devolver(0);
					serviceFacturasDetallesTalleres.guardar(facturaDetalleTaller);
					monto+=devolucionFacturaDetalle.getPrecio();
				}
			}
		}
		
		Double itbisTemp = itbis.doubleValue()/100;
		itbisTemp*=monto;
		//Actualizamos precios de devolucion
		devolucionFactura.setPrecio(monto);
		devolucionFactura.setItbis(itbisTemp);
		devolucionFactura.setTotal(monto+itbisTemp);
		serviceDevolucionesFacturas.guardar(devolucionFactura);
		
		//Si la devolucion se creo vacia se elimina
		if(devolucionFactura.getPrecio()==0) {
			serviceDevolucionesFacturas.eliminar(devolucionFactura);
		}
		
		return "devoluciones/facturas/devolucion :: #temporal";
	}
}
