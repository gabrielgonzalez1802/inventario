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
import com.developergg.app.model.Compra;
import com.developergg.app.model.CompraDetalle;
import com.developergg.app.model.CompraDetalleSerial;
import com.developergg.app.model.DevolucionCompra;
import com.developergg.app.model.DevolucionFactura;
import com.developergg.app.model.DevolucionFacturaDetalle;
import com.developergg.app.model.DevolucionFacturaSerial;
import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetalle;
import com.developergg.app.model.FacturaDetalleSerial;
import com.developergg.app.model.FacturaDetalleTaller;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IArticulosAjustesService;
import com.developergg.app.service.IArticulosSeriales;
import com.developergg.app.service.IComprasDetallesSerialesService;
import com.developergg.app.service.IComprasDetallesService;
import com.developergg.app.service.IComprasService;
import com.developergg.app.service.IDevolucionesComprasService;
import com.developergg.app.service.IDevolucionesFacturasDetallesService;
import com.developergg.app.service.IDevolucionesFacturasSerialesService;
import com.developergg.app.service.IDevolucionesFacturasService;
import com.developergg.app.service.IFacturasDetallesSerialesService;
import com.developergg.app.service.IFacturasDetallesService;
import com.developergg.app.service.IFacturasDetallesTallerService;
import com.developergg.app.service.IFacturasService;

@Controller
@RequestMapping("/devolucionesCompras")
public class DevolucionesComprasController {
	
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
	private IFacturasDetallesSerialesService serviceFacturasDetallesSeriales;
	
	@Autowired
	private IFacturasDetallesTallerService serviceFacturasDetallesTalleres;
	
	@Autowired
	private IDevolucionesFacturasService serviceDevolucionesFacturas;
	
	@Autowired
	private IDevolucionesFacturasDetallesService serviceDevolucionesFacturasDetalles;
	
	@Autowired
	private IDevolucionesFacturasSerialesService serviceDevolucionesFacturasSeriales;
	
	@Autowired
	private IDevolucionesComprasService serviceDevolucionesCompras;
	
	@Autowired
	private IComprasService serviceCompras;
	
	@Autowired
	private IComprasDetallesSerialesService serviceComprasDetallesSeriales;
	
	@Autowired
	private IComprasDetallesService serviceComprasDetalles;
	
	@GetMapping("/")
	public String listaDevoluciones(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<DevolucionCompra> devoluciones = serviceDevolucionesCompras.buscarPorAlmacen(usuario.getAlmacen());
		model.addAttribute("devoluciones", devoluciones);
		return "devoluciones/compras/listaDevoluciones";
	}
	
	@GetMapping("/devolucionCompra")
	public String devolucionCompra(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Compra> compras = serviceCompras.buscarPorAlmacen(usuario.getAlmacen());
		List<Compra> listaCompras = new LinkedList<>();
		for (Compra compra : compras) {
			int cargarDatos = 0;
			//verificamos el detalle de la factura articulos sin serial
			List<CompraDetalle> detalles = serviceComprasDetalles.buscarPorCompra(compra);
			if(!detalles.isEmpty()) {
				cargarDatos = 1;
			}
			
			if(cargarDatos == 1) {
				listaCompras.add(compra);
			}
		}
		model.addAttribute("compras", listaCompras);
		return "devoluciones/compras/listaCompras";
	}
	
	@GetMapping("/devolver/{id}")
	public String hacerDevolucion(Model model, @PathVariable("id") Integer id) {
		Compra compra = serviceCompras.buscarPorId(id);
		List<CompraDetalle> detalles = serviceComprasDetalles.buscarPorCompra(compra);
		model.addAttribute("compra", compra);
		model.addAttribute("compraDetalles", detalles);
		return "devoluciones/compras/devolucion";
	}
	
	@GetMapping("/cuerpoDevolucion/{id}")
	public String cargarCuerpoDevolucion(Model model, @PathVariable("id") Integer id) {
		Compra compra = serviceCompras.buscarPorId(id);
		List<CompraDetalle> detalles = serviceComprasDetalles.buscarPorCompra(compra);
		
		//verificamos si el detalle tiene una devolucion temporal
		for (CompraDetalle detalle : detalles) {
			//buscamos la lista de seriales
			List<CompraDetalleSerial> seriales = serviceComprasDetallesSeriales.buscarPorCompraDetalle(detalle);
			if(!seriales.isEmpty()) {
				int cantTempDevlover = 0;
				for (CompraDetalleSerial serial : seriales) {
					if(serial.getTempDevuelto() == 1) {
						cantTempDevlover++;
					}
				}
				detalle.setTemp_devolver(cantTempDevlover);
			}
		}
		
		model.addAttribute("detalles", detalles);
		return "devoluciones/compras/cuerpoDevolucion :: cuerpoDevolucion";
	}
	
	@GetMapping("/verificarTipoArticulo/{id}")
	public String verificarTipoArticulo(Model model, @PathVariable("id") Integer id) {
		int conSerial = 0;
		CompraDetalle detalle = serviceComprasDetalles.buscarPorId(id);
		if(detalle.getArticulo().getImei().equals("SI") || detalle.getArticulo().getImei().equals("1")) {
			conSerial = 1;
		}
		model.addAttribute("idArticulo", detalle.getArticulo().getId());
		model.addAttribute("conSerial", conSerial);
		model.addAttribute("nombreArticulo", detalle.getArticulo().getNombre());
		model.addAttribute("costoArticulo", detalle.getCosto());
		model.addAttribute("cantidadArticulo", detalle.getCantidad());
		model.addAttribute("cantidadDevuelta", detalle.getCantidad_devuelta());
		return "devoluciones/compras/devolucion :: #tipoArticulo";
	}
	
	@GetMapping("/ajax/obtenerSeriales/{id}")
	public String obtenerSeriales(Model model, @PathVariable("id") Integer IdcompraDetalle) {
		CompraDetalle compraDetalle = serviceComprasDetalles.buscarPorId(IdcompraDetalle);
		List<CompraDetalleSerial> seriales = new LinkedList<>();
		if(compraDetalle.getCon_imei() == 1){
			seriales = serviceComprasDetallesSeriales.buscarPorCompraDetalle(compraDetalle);
		}
		model.addAttribute("listaSeriales", seriales);
		model.addAttribute("idDetalleArticulo", compraDetalle.getId());
		model.addAttribute("detalleArticulo", compraDetalle.getArticulo().getNombre());
		return "devoluciones/compras/devolucion :: infoSeriales";
	}
	
	@GetMapping("/ajax/infoSerialesADevolver/{id}")
	public String infoSerialesADevolver(Model model, @PathVariable("id") Integer idCompraDetalle) {
		CompraDetalle compraDetalle = serviceComprasDetalles.buscarPorId(idCompraDetalle);
		List<CompraDetalleSerial> seriales = new LinkedList<>();
		if(compraDetalle.getCon_imei() == 1){
			//Seriales que no esten devueltos
			seriales = serviceComprasDetallesSeriales.buscarPorCompraDetalle(compraDetalle).
					stream().filter(s -> s.getDevuelto() == 0).collect(Collectors.toList());
		}
		model.addAttribute("listaSeriales", seriales);
		model.addAttribute("idDetalleArticulo", compraDetalle.getArticulo().getId());
		model.addAttribute("detalleArticulo", compraDetalle.getArticulo().getNombre());
		model.addAttribute("itbisArticuloConSerial", 0);
		return "devoluciones/compras/devolucion :: #serialesADevolver";
	}
	
	@GetMapping("/ajax/obtenerCostos/{id}/{seriales}")
	public String obtenerCostos(Model model, @PathVariable("id") Integer idCompraDetalle,
			@PathVariable("seriales") String idSeriales) {
		String[] serialesArray = idSeriales.split(",");
		CompraDetalle compraDetalle = serviceComprasDetalles.buscarPorId(idCompraDetalle);
		List<CompraDetalleSerial> listaSeriales = new LinkedList<>();
		List<CompraDetalleSerial> listaSerialesnew = new LinkedList<>();
		Double costo = 0.0;
		if(compraDetalle.getCon_imei() == 1){
			listaSeriales = serviceComprasDetallesSeriales.buscarPorCompraDetalle(compraDetalle);
			for (CompraDetalleSerial compraDetalleSerial : listaSeriales) {
				for (String serialArray : serialesArray) {
					if(compraDetalleSerial.getId().toString().equals(serialArray)) {
						listaSerialesnew.add(compraDetalleSerial);
						costo+=compraDetalleSerial.getCosto();
					}
				}
			}
		}
		model.addAttribute("totalCostoSerial", costo);
		return "devoluciones/compras/devolucion :: #costoArticuloConSerial";
	}
	
	@PostMapping("/ajax/devolverSeriales")
	public String devolverSeriales(@RequestParam(name = "compraDetalle") Integer compraDetalleId,
			@RequestParam(name = "seriales") String seriales) {
		CompraDetalle compraDetalle = serviceComprasDetalles.buscarPorId(compraDetalleId);
		List<CompraDetalleSerial> serialesCompra = serviceComprasDetallesSeriales.buscarPorCompraDetalle(compraDetalle);
		String[] serialesArray = seriales.split(",");
		List<CompraDetalleSerial> newSeriales = new LinkedList<>();
		for (CompraDetalleSerial serialCompra : serialesCompra) {
			for (String serialArray : serialesArray) {
				if(serialCompra.getId().toString().equals(serialArray)) {
					newSeriales.add(serialCompra);
				}else {
					serialCompra.setTempDevuelto(0);
					serviceComprasDetallesSeriales.guardar(serialCompra);
				}
			}
		}
				
		if(!newSeriales.isEmpty()) {
			for (CompraDetalleSerial compraDetalleSerial : newSeriales) {
				compraDetalleSerial.setTempDevuelto(1);
				serviceComprasDetallesSeriales.guardar(compraDetalleSerial);
			}
		}
		return "devoluciones/compras/devolucion :: #temporal";
	}
	
	@PostMapping("/ajax/devolverSinSeriales")
	public String devolverSinSeriales(@RequestParam(name = "compraDetalle") Integer compraDetalleId,
			@RequestParam(name = "cantidad") Integer cantidad) {
		CompraDetalle compraDetalle = serviceComprasDetalles.buscarPorId(compraDetalleId);
		compraDetalle.setTemp_devolver(cantidad);
		serviceComprasDetalles.guardar(compraDetalle);
		return "devoluciones/compras/devolucion :: #temporal";
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
						devolucionFacturaDetalle.setItbis(facturaDetalle.getItbis());
						devolucionFacturaDetalle.setSubTotal(facturaDetalle.getSubtotal());
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
						newArticuloAjusteDefinitive.setTipoMovimiento("entrada");
						newArticuloAjusteDefinitive.setCantidad(facturaDetalle.getTemp_devolver());
						newArticuloAjusteDefinitive.setCosto(facturaDetalle.getCosto());
						newArticuloAjusteDefinitive.setProcedencia("devolucion");
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
						devolucionFacturaSerial.setDevolucionFactura(devolucionFactura);
						devolucionFacturaSerial.setPrecio(facturaDetalle.getPrecio());
						devolucionFacturaSerial.setItbis(facturaDetalle.getItbis());
						devolucionFacturaSerial.setSubTotal(facturaDetalle.getPrecio()+facturaDetalle.getItbis());
						devolucionFacturaSerial.setNombreArticulo(facturaDetalle.getArticulo().getNombre());
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
					devolucionFacturaDetalle.setItbis(facturaDetalleTaller.getItbis());
					devolucionFacturaDetalle.setSubTotal((facturaDetalleTaller.getTemp_devolver()*facturaDetalleTaller.getPrecio())+(facturaDetalleTaller.getTemp_devolver()*facturaDetalleTaller.getItbis()));
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
					newArticuloAjusteDefinitive.setTipoMovimiento("entrada");
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
