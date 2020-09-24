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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.developergg.app.model.AbonoCxC;
import com.developergg.app.model.AbonoCxCDetalle;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloAjuste;
import com.developergg.app.model.ArticuloSerial;
import com.developergg.app.model.Categoria;
import com.developergg.app.model.Cliente;
import com.developergg.app.model.ComprobanteFiscal;
import com.developergg.app.model.CondicionPago;
import com.developergg.app.model.DetalleFactura;
import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetalle;
import com.developergg.app.model.FacturaDetallePagoTemp;
import com.developergg.app.model.FacturaDetalleSerial;
import com.developergg.app.model.FacturaDetalleServicio;
import com.developergg.app.model.FacturaDetalleTaller;
import com.developergg.app.model.FacturaDetalleTemp;
import com.developergg.app.model.FacturaPago;
import com.developergg.app.model.FacturaPagoTemp;
import com.developergg.app.model.FacturaSerialTemp;
import com.developergg.app.model.FacturaServicioTemp;
import com.developergg.app.model.FacturaTallerTemp;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.FormaPago;
import com.developergg.app.model.Perfil;
import com.developergg.app.model.ReporteVenta;
import com.developergg.app.model.Taller;
import com.developergg.app.model.TallerArticulo;
import com.developergg.app.model.TallerDetalle;
import com.developergg.app.model.TipoEquipo;
import com.developergg.app.model.Usuario;
import com.developergg.app.model.Vendedor;
import com.developergg.app.service.IAbonosCxCDetallesService;
import com.developergg.app.service.IAbonosCxCService;
import com.developergg.app.service.IArticulosAjustesService;
import com.developergg.app.service.IArticulosSeriales;
import com.developergg.app.service.IArticulosService;
import com.developergg.app.service.ICategoriasService;
import com.developergg.app.service.IClientesService;
import com.developergg.app.service.IComprobantesFiscalesService;
import com.developergg.app.service.ICondicionesPagoService;
import com.developergg.app.service.IFacturasDetallesPagoTempService;
import com.developergg.app.service.IFacturasDetallesSerialesService;
import com.developergg.app.service.IFacturasDetallesService;
import com.developergg.app.service.IFacturasDetallesServiciosService;
import com.developergg.app.service.IFacturasDetallesTallerService;
import com.developergg.app.service.IFacturasDetallesTempService;
import com.developergg.app.service.IFacturasPagoService;
import com.developergg.app.service.IFacturasPagoTempService;
import com.developergg.app.service.IFacturasSerialesTempService;
import com.developergg.app.service.IFacturasService;
import com.developergg.app.service.IFacturasServiciosTempService;
import com.developergg.app.service.IFacturasTalleresTempService;
import com.developergg.app.service.IFacturasTempService;
import com.developergg.app.service.IFormasPagoService;
import com.developergg.app.service.IPerfilesService;
import com.developergg.app.service.ITalleresService;
import com.developergg.app.service.ITiposEquipoService;
import com.developergg.app.service.IUsuariosService;
import com.developergg.app.service.IVendedoresService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
@RequestMapping("/facturas")
public class FacturasController {

	@Autowired
	private IFacturasTempService serviceFacturasTemp;

	@Autowired
	private IComprobantesFiscalesService serviceComprobantesFiscales;

	@Autowired
	private ITiposEquipoService serviceTiposEquipos;

	@Autowired
	private IClientesService serviceClientes;

	@Autowired
	private IFormasPagoService serviceFormasPagos;

	@Autowired
	private ICondicionesPagoService serviceCondicionesPago;

	@Autowired
	private IFacturasService serviceFacturas;
	
	@Autowired
	private IFacturasDetallesPagoTempService serviceFacturaDetallesPagosTemp;
	
	@Autowired
	private IFacturasPagoService serviceFacturasPagosService;
	
	@Autowired
	private IFacturasPagoTempService serviceFacturasPagoTemp;
	
	@Autowired
	private IFacturasDetallesTempService serviceFacturasDetallesTemp;
	
	@Autowired
	private IFacturasDetallesService serviceFacturasDetalles;
	
	@Autowired
	private IFacturasDetallesServiciosService facturasDetallesServiciosService;
	
	@Autowired
	private IFacturasServiciosTempService serviceFacturasServiciosTemp;
	
	@Autowired
	private IArticulosSeriales serviceArticulosSeriales;
	
	@Autowired
	private IFacturasSerialesTempService serviceFacturasSerialesTemp;
	
	@Autowired
	private IArticulosAjustesService serviceArticulosAjuste;
	
	@Autowired
	private IVendedoresService serviceVendedores;
	
	@Autowired
	private IArticulosService serviceArticulos;
	
	@Autowired
	private IArticulosAjustesService serviceArticulosAjustes;
	
	@Autowired
	private IAbonosCxCService serviceAbonos;
	
	@Autowired
	private IAbonosCxCDetallesService serviceAbonosDetalles;
	
	@Autowired
	private ITalleresService serviceTalleres;
	
	@Autowired
	private IFacturasTalleresTempService serviceFacturasTalleresTemp;
	
	@Autowired
	private IFacturasDetallesTallerService serviceFacturasDetallesTaller;
	
	@Autowired
	private IAbonosCxCService serviceAbonosCxC;
	
	@Autowired
	private IAbonosCxCDetallesService serviceDetallesAbonosCxC;
	
	@Autowired
	private IFacturasDetallesSerialesService serviceFacturasDetallesSeriales;
	
	@Autowired
	private ICategoriasService serviceCategorias;
	
	@Autowired
	private IUsuariosService serviceUsuarios;
	
	@Autowired
	private IPerfilesService servicePerfiles;
		
	@Autowired
	private DataSource dataSource;
	
	@Value("${inventario.ruta.imagenes}")
	private String rutaImagenes;
	
	@Value("${inventario.ruta.reporte.factura}")
	private String rutaJreport;
	
	@Value("${inventario.ruta.reporte.ventaCategoria}")
	private String rutaJreportVentaCategoria;
	
	@Value("${inventario.ruta.reporte.ventaCliente}")
	private String rutaJreportVentaCliente;
	
	@Value("${inventario.ruta.reporte.ventaUsuario}")
	private String rutaJreportVentaUsuario;
	
	@Value("${inventario.ruta.reporte.ventaVendedor}")
	private String rutaJreportVentaVendedor;
	
	@Value("${inventario.ruta.reporte.ventaTecnico}")
	private String rutaJreportVentaTecnico;
	

	private String tempFolder =  System.getProperty("java.io.tmpdir");
	private String pathSeparator = System.getProperty("file.separator");
	
	DecimalFormat df2 = new DecimalFormat("###.##");
	
	@GetMapping("/")
	public String mostrarFacturas(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Factura> facturas = serviceFacturas.buscarPorAlmacen(usuario.getAlmacen());
		model.addAttribute("facturas", facturas);
		return "facturas/listaFacturas";
	}

	@GetMapping("/create")
	public String create(HttpSession session, Model model) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		// verificamos si existe una factura temporal
		FacturaTemp facturaTemp = serviceFacturasTemp.buscarPorUsuario(usuario);
		List<ComprobanteFiscal> comprobantesFiscales = serviceComprobantesFiscales
				.buscarPorTienda(usuario.getAlmacen().getPropietario());
		List<TipoEquipo> tiposEquipo = serviceTiposEquipos.buscarTodos();
		//Lista de vendedores que no esten eliminados
		List<Vendedor> listaVendedores = serviceVendedores.buscarPorAlmacen(usuario.getAlmacen()).
				stream().filter(v -> v.getEliminado() == 0).collect(Collectors.toList());
		// Clientes que no esten eliminados y pertenezcan al almacen
		List<Cliente> clientes = serviceClientes.buscarPorAlmacen(usuario.getAlmacen()).stream()
				.filter(c -> c.getEliminado() == 0).collect(Collectors.toList());
		List<FormaPago> formaPagos = serviceFormasPagos.buscarPorAlmacen(usuario.getAlmacen());
		List<CondicionPago> condicionesPago = serviceCondicionesPago.buscarTodos();
		ComprobanteFiscal comprobanteFiscal = null;
		for (ComprobanteFiscal comprobanteF : comprobantesFiscales) {
			if (comprobanteF.getNombre().equalsIgnoreCase("Consumidor final")) {
				comprobanteFiscal = comprobanteF;
				break;
			}
		}
		if (facturaTemp == null) {
			CondicionPago condicionPago = serviceCondicionesPago.buscarPorCondicionPago("Contado");
			facturaTemp = new FacturaTemp();
			facturaTemp.setUsuario(usuario);
			facturaTemp.setComprobanteFiscal(comprobanteFiscal);
			facturaTemp.setCondicionPago(condicionPago);
			serviceFacturasTemp.guardar(facturaTemp);
		}
		
		if(facturaTemp.getCliente()==null) {
			facturaTemp.setCliente(new Cliente());
		}
		
		//Lista de articulos - codigo - seriales
		List<Articulo> lista = serviceArticulos.buscarPorTienda(usuario.getAlmacen().getPropietario())
				.stream().filter(p -> p.getEliminado() == 0).collect(Collectors.toList());
		
		List<Articulo> listaDefinitive = new LinkedList<>();
				
		for (Articulo articulo : lista) {
			//verificamos si el articulo no tiene serial
			if(articulo.getImei().equalsIgnoreCase("NO")||(articulo.getImei().equals("0"))) {
				//Verificamos que tenga inventario en la tienda
				List<ArticuloAjuste> articulosAjustes = serviceArticulosAjustes.buscarPorArticuloYAlmacen(articulo, usuario.getAlmacen());
				if(articulosAjustes.isEmpty()) {
					articulo.setCantidad(0);
				}else {
					ArticuloAjuste newArticuloAjuste = articulosAjustes.get(articulosAjustes.size()-1);
					articulo.setCantidad(newArticuloAjuste.getDisponible());
				}
				articulo.setNombre(articulo.getNombre()+" - "+articulo.getCodigo());
				if(articulo.getCantidad()>0) {
					listaDefinitive.add(articulo);
				}
			}else {
				String tempSerials = "";
				//verificamos los seriales del articulo
				List<ArticuloSerial> articuloSerials = serviceArticulosSeriales
						.buscarPorArticuloAlmacen(articulo, usuario.getAlmacen()).stream()
						.filter(s -> s.getEstado().equalsIgnoreCase("Disponible"))
						.collect(Collectors.toList());
				if(!articuloSerials.isEmpty()) {
					for (ArticuloSerial serial : articuloSerials) {
						tempSerials+=serial.getSerial()+" - ";
					}
					tempSerials = tempSerials.substring(0, tempSerials.length() - 2);
					articulo.setNombre(articulo.getNombre()+" - "+articulo.getCodigo() + " - "+tempSerials);
					listaDefinitive.add(articulo);
				}
			}
		}
		
		//Verificamos el listado en taller
		List<Taller> listaTalleres = serviceTalleres.buscarPorAlmacen(usuario.getAlmacen()).
				stream().filter(t -> t.getFacturaTemp() == null && t.getCompletado() == 0).collect(Collectors.toList());
		
		Taller taller = facturaTemp.getTaller();
		if(taller==null) {
			taller = new Taller();
		}
		
		Cliente cliente = facturaTemp.getCliente();
		if(cliente!=null) {
			model.addAttribute("infoCliente", cliente);
		}else {
			model.addAttribute("infoCliente", new Cliente());
		}
		
		model.addAttribute("infoTaller", taller);
		model.addAttribute("listaTalleres", listaTalleres);
		model.addAttribute("listaArticulos", listaDefinitive);
		model.addAttribute("vendedores", listaVendedores);
		model.addAttribute("condicionesPago", condicionesPago);
		model.addAttribute("formaPagos", formaPagos);
		model.addAttribute("clientes", clientes);
		model.addAttribute("facturaTemp", facturaTemp);
		model.addAttribute("comprobantesFiscales", comprobantesFiscales);
		model.addAttribute("tiposEquipo", tiposEquipo);
		model.addAttribute("taller", new Taller());
		return "facturas/factura";
	}

	@PostMapping("/ajax/updateVendedorFactura")
	public String modificarVendedor(Model model, HttpSession session,
			@RequestParam("vendedorID") Integer vendedorID) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaTemp facturaTemp = serviceFacturasTemp.buscarPorUsuario(usuario);
		Vendedor vendedor = serviceVendedores.buscarPorIdVendedor(vendedorID);
		facturaTemp.setVendedor(vendedor);
		serviceFacturasTemp.guardar(facturaTemp);
		return "facturas/factura :: #responseUpdateVendedor";
	}
	
	@PostMapping("/ajax/updateCondicionPagoFactura")
	public String modificarCondicionPago(Model model, HttpSession session,
			@RequestParam("condicionPagoID") Integer condicionPagoID) {
		CondicionPago condicionPago = serviceCondicionesPago.buscarPorId(condicionPagoID);
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaTemp facturaTemp = serviceFacturasTemp.buscarPorUsuario(usuario);
		facturaTemp.setCondicionPago(condicionPago);
		serviceFacturasTemp.guardar(facturaTemp);
		model.addAttribute("idCondicionPago", facturaTemp.getCondicionPago().getId());
		model.addAttribute("tipoCondicionPago",facturaTemp.getCondicionPago().getNombre());
		return "facturas/factura :: #condicionPagoInfo";
	}

	@PostMapping("/ajax/guardarFactura")
	public String guardarFactura(Model model, HttpSession session,
			@RequestParam(name = "total_venta") Double total_venta,@RequestParam(name = "nombreCliente") String nombreCliente,
			@RequestParam(name = "telefonoCliente") String telefonoCliente, @RequestParam(name = "rncCliente") String rncCliente,
			@RequestParam("total_itbis") Double total_itbis, @RequestParam("total_precio") Double total_precio) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaTemp facturaTemp = serviceFacturasTemp.buscarPorUsuario(usuario);
		List<FacturaDetalleTemp> facturaDetallesTemp = serviceFacturasDetallesTemp.buscarPorUsuarioAlmacen(usuario, usuario.getAlmacen());
		
		int tempCode = 1;
		int credito = 0;
		
		// validacion para condicion de pago contado
		if (!facturaTemp.getCondicionPago().getNombre().equalsIgnoreCase("contado")) {
			credito = 1;
		}
		// Lista de facturas por almacen
		List<Factura> listaFacturas = serviceFacturas.buscarPorAlmacen(usuario.getAlmacen());
		if (!listaFacturas.isEmpty()) {
			Factura ultimaFactura = listaFacturas.get(listaFacturas.size() - 1);
			tempCode = ultimaFactura.getCodigo() + 1;
		}
		
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
        String now = LocalDateTime.now().format(formatter);
        
        //actualizamos secuencia del comprobante fiscal
		ComprobanteFiscal comprobanteFiscal = facturaTemp.getComprobanteFiscal();
        try {
			comprobanteFiscal.setSecuencia_actual(String.valueOf(Integer.parseInt(comprobanteFiscal.getSecuencia_actual())+1));
			serviceComprobantesFiscales.guardar(comprobanteFiscal);
		} catch (NumberFormatException e) {
			//NumberFormatException
		}
        
		// Creamos la factura
		Factura factura = new Factura();
		factura.setCodigo(tempCode);
		factura.setCliente(facturaTemp.getCliente());
		factura.setComprobanteFiscal(comprobanteFiscal);
		factura.setFecha(new Date());
		factura.setHora(now);
		factura.setCondicionPago(facturaTemp.getCondicionPago());
		factura.setCondicion(facturaTemp.getCondicionPago().getNombre()); 
		factura.setTotal_venta(total_venta);
		factura.setNombre_cliente(nombreCliente);
		factura.setTelefono_cliente(telefonoCliente);
		factura.setRnc_cliente(rncCliente);
		//factura.setNota_factura(); //validar
		factura.setNcf(factura.getComprobanteFiscal().getPrefijo()+factura.getComprobanteFiscal().getSecuencia_actual());
		factura.setUsuario(facturaTemp.getUsuario());
		factura.setVendedor(facturaTemp.getVendedor());
		factura.setAlmacen(usuario.getAlmacen());
		//factura.setAbono(abono); //validar
		factura.setCredito(credito); 
		//factura.setCuotas(); //validar
		if(facturaTemp.getTaller()!=null) {
			factura.setTaller(facturaTemp.getTaller());
			factura.setAvance_taller(facturaTemp.getTaller().getAvance());
		}
		//forma_pago,avance_taller,total_itbis
		factura.setTotal_itbis(total_itbis);
		factura.setTotal_precio(total_precio);
		
		if(credito==1) {
			//default time zone
			ZoneId defaultZoneId = ZoneId.systemDefault();
			LocalDate date = LocalDate.now();
			LocalDate newDate = date.plusDays(facturaTemp.getCondicionPago().getDia());
			Date vencimiento = Date.from(newDate.atStartOfDay(defaultZoneId).toInstant());
			factura.setVencimiento(vencimiento);
		}
		
		serviceFacturas.guardar(factura);
		
		AbonoCxC abono = null;
		
		if(credito == 1){
			//creamos el abono
			abono = new AbonoCxC();
			abono.setAlmacen(usuario.getAlmacen());
			abono.setCliente(factura.getCliente());
			abono.setFactura(factura);
			abono.setFecha(factura.getFecha());
			abono.setUsuario(usuario);
			abono.setCodigo(factura.getCodigo());
			serviceAbonosCxC.guardar(abono);
		}
		
		//Logica para facturas con taller y abono
		if(credito == 1 && factura.getTaller()!=null && factura.getAvance_taller().doubleValue()>0) {

			Double restante = formato2d(factura.getTotal_venta()-factura.getAvance_taller());
			
			AbonoCxCDetalle detalle = new AbonoCxCDetalle();
			FormaPago formaPago = serviceFormasPagos.buscarPorId(0);
			detalle.setFormaPago(formaPago);
			detalle.setIngreso(abono);
			detalle.setAbono(factura.getAvance_taller());
			detalle.setFecha(new Date());
			detalle.setHora(now);
			Double abonado =detalle.getAbono();
			serviceDetallesAbonosCxC.guardar(detalle);
			
			abono.setTotalAbono(abonado);
			abono.setTotalRestante(restante<0?0:restante);
			abono.setTotalPagado(abonado);
			if(restante<0) {
				abono.setTotalDevuelto(restante*-1);
			}
			serviceAbonosCxC.guardar(abono);
			factura.setAbono(factura.getAbono()+abono.getTotalAbono());
			serviceFacturas.guardar(factura);
		}
		
		//detalles del pago
		List<FacturaDetallePagoTemp> detallesPagosTemp = serviceFacturaDetallesPagosTemp.buscarPorFacturaTemp(facturaTemp); 
		
		if(credito==0) {
			for (FacturaDetallePagoTemp facturaDetallePagoTemp : detallesPagosTemp) {
				FacturaPago facturaPago = new FacturaPago();
				facturaPago.setFactura(factura);
				facturaPago.setFormaPago(facturaDetallePagoTemp.getFormaPago());
				facturaPago.setCantidad(facturaDetallePagoTemp.getMonto());
				facturaPago.setFecha(new Date());
				//facturaPago.setAplicado(); //verificar
				serviceFacturasPagosService.guardar(facturaPago);
			}
		}

		//detalles de la factura
		for (FacturaDetalleTemp facturaDetalleTemp : facturaDetallesTemp) {
			FacturaDetalle facturaDetalle = new FacturaDetalle();
			facturaDetalle.setAlmacen(facturaDetalleTemp.getAlmacen());
			facturaDetalle.setArticulo(facturaDetalleTemp.getArticulo());
			facturaDetalle.setCantidad(facturaDetalleTemp.getCantidad());
			facturaDetalle.setCosto(facturaDetalleTemp.getArticulo().getCosto());
			facturaDetalle.setExistencia(facturaDetalleTemp.getExistencia());
			facturaDetalle.setFactura(factura);
			facturaDetalle.setImei(facturaDetalleTemp.getImei());
			facturaDetalle.setItbis(facturaDetalleTemp.getItbis());
			facturaDetalle.setPaga_itbis(facturaDetalleTemp.getConItbis());
			facturaDetalle.setPrecio(facturaDetalleTemp.getPrecio());
			facturaDetalle.setSubtotal(facturaDetalleTemp.getSubtotal());
			
			//obtenemos la lista de seriales en la factura
//			List<FacturaSerialTemp> facturasSerialesTemp = serviceFacturasSerialesTemp.buscarPorFacturaTemp(facturaTemp);
			List<FacturaSerialTemp> seriales = serviceFacturasSerialesTemp.buscarPorDetalleTemp(facturaDetalleTemp);
			if(facturaDetalle.getArticulo().getImei().equalsIgnoreCase("NO") || facturaDetalle.getArticulo().getImei().equalsIgnoreCase("0")) {
				facturaDetalle.setPrecio_minimo(facturaDetalleTemp.getArticulo().getPrecio_minimo()); //validar el precio por serial
				facturaDetalle.setPrecio_mayor(facturaDetalleTemp.getArticulo().getPrecio_mayor()); //validar el precio por serial
				facturaDetalle.setPrecio_maximo(facturaDetalleTemp.getArticulo().getPrecio_maximo());
			}else {
				for (FacturaSerialTemp fs : seriales) {
					facturaDetalle.setPrecio_minimo(fs.getArticuloSerial().getPrecio_minimo());
					facturaDetalle.setPrecio_mayor(fs.getArticuloSerial().getPrecio_mayor());
					facturaDetalle.setPrecio_maximo(fs.getArticuloSerial().getPrecio_maximo());
				}
			}

			serviceFacturasDetalles.guardar(facturaDetalle);
			
			//Guardamos los seriales en la factura
			for (FacturaSerialTemp serialesTemp : seriales) {
				FacturaDetalleSerial facturaDetalleSerial = new FacturaDetalleSerial();
				facturaDetalleSerial.setArticuloSerial(serialesTemp.getArticuloSerial());
				facturaDetalleSerial.setFacturaDetalle(facturaDetalle);
				facturaDetalleSerial.setPrecio(facturaDetalleTemp.getPrecio());
				facturaDetalleSerial.setSerial(serialesTemp.getArticuloSerial().getSerial());
				serviceFacturasDetallesSeriales.guardar(facturaDetalleSerial);
			}

			//Proceso para crear una salida con o sin serial
			//verificamos si el producto tiene serial
			if(facturaDetalle.getArticulo().getImei().equalsIgnoreCase("SI") || facturaDetalle.getArticulo().getImei().equalsIgnoreCase("1")) {
				for (FacturaSerialTemp facturaSerialTemporal : seriales) {
					ArticuloSerial articuloSerial = facturaSerialTemporal.getArticuloSerial();
					articuloSerial.setEstado("Vendido");
					serviceArticulosSeriales.guardar(articuloSerial);
				}
			}else {
				//articulo sin serial
				List<ArticuloAjuste> lista = serviceArticulosAjuste.buscarPorArticuloYAlmacen(facturaDetalleTemp.getArticulo(), usuario.getAlmacen());
				ArticuloAjuste newArticuloAjuste = lista.get(lista.size()-1);
				ArticuloAjuste newArticuloAjusteDefinitive = new ArticuloAjuste();
				newArticuloAjusteDefinitive.setAlmacen(usuario.getAlmacen());
				newArticuloAjusteDefinitive.setArticulo(facturaDetalleTemp.getArticulo());
				newArticuloAjusteDefinitive.setAlmacen(usuario.getAlmacen());
				newArticuloAjusteDefinitive.setFecha(new Date());
				newArticuloAjusteDefinitive.setUsuario(usuario);
				newArticuloAjusteDefinitive.setTipoMovimiento("salida");
				newArticuloAjusteDefinitive.setCantidad(facturaDetalleTemp.getCantidad());
				newArticuloAjusteDefinitive.setCosto(facturaDetalleTemp.getArticulo().getCosto());
				newArticuloAjusteDefinitive.setExistencia(newArticuloAjuste.getDisponible());
//				newArticuloAjusteDefinitive.setNo_factura(articuloAjuste.getNo_factura()); //validar
				newArticuloAjusteDefinitive.setUsuario(usuario);
				newArticuloAjusteDefinitive.setDisponible(newArticuloAjusteDefinitive.getExistencia()-newArticuloAjusteDefinitive.getCantidad());
				serviceArticulosAjuste.guardar(newArticuloAjusteDefinitive);
			}
		}
		
		//servicios
		List<FacturaServicioTemp> facturasServicioTemp = serviceFacturasServiciosTemp.buscarPorUsuarioAlmacen(usuario, usuario.getAlmacen());
		for (FacturaServicioTemp facturaServicioTemp : facturasServicioTemp) {
			FacturaDetalleServicio facturaDetalleServicio = new FacturaDetalleServicio();
			facturaDetalleServicio.setCantidad(facturaServicioTemp.getCantidad());
			facturaDetalleServicio.setCosto(facturaServicioTemp.getCosto());
			facturaDetalleServicio.setDescripcion(facturaServicioTemp.getDescripcion());
			facturaDetalleServicio.setPrecio(facturaServicioTemp.getPrecio());
			facturaDetalleServicio.setSubtotal(facturaServicioTemp.getSubtotal());
			facturaDetalleServicio.setItbis(facturaServicioTemp.getItbis());
			facturaDetalleServicio.setFactura(factura);
			facturasDetallesServiciosService.guardar(facturaDetalleServicio);
		}
		
		//talleres
		List<FacturaTallerTemp> facturasTalleresTemp = serviceFacturasTalleresTemp.buscarPorFacturaTemp(facturaTemp);
		for (FacturaTallerTemp facturaTallerTemp : facturasTalleresTemp) {
			FacturaDetalleTaller facturaTaller = new FacturaDetalleTaller();
			facturaTaller.setAlmacen(usuario.getAlmacen());
			
			if(facturaTallerTemp.getArticulo()!=null) {
				facturaTaller.setArticulo(facturaTallerTemp.getArticulo());
				//genera la salida del articulo en el taller
				List<ArticuloAjuste> lista = serviceArticulosAjuste.buscarPorArticuloYAlmacen(facturaTallerTemp.getArticulo(), usuario.getAlmacen());
				ArticuloAjuste newArticuloAjuste = lista.get(lista.size()-1);
				ArticuloAjuste newArticuloAjusteDefinitive = new ArticuloAjuste();
				newArticuloAjusteDefinitive.setAlmacen(usuario.getAlmacen());
				newArticuloAjusteDefinitive.setArticulo(facturaTallerTemp.getArticulo());
				newArticuloAjusteDefinitive.setAlmacen(usuario.getAlmacen());
				newArticuloAjusteDefinitive.setFecha(new Date());
				newArticuloAjusteDefinitive.setUsuario(usuario);
				newArticuloAjusteDefinitive.setTipoMovimiento("salida");
				newArticuloAjusteDefinitive.setCantidad(facturaTallerTemp.getCantidad());
				newArticuloAjusteDefinitive.setCosto(facturaTallerTemp.getCosto());
				newArticuloAjusteDefinitive.setExistencia(newArticuloAjuste.getDisponible());
				newArticuloAjusteDefinitive.setNo_factura(String.valueOf(factura.getCodigo()));
				newArticuloAjusteDefinitive.setDisponible(newArticuloAjusteDefinitive.getExistencia());
				serviceArticulosAjuste.guardar(newArticuloAjusteDefinitive);
			}
			
			facturaTaller.setCantidad(facturaTallerTemp.getCantidad());
			facturaTaller.setComprobanteFiscal(facturaTallerTemp.getComprobanteFiscal());
			facturaTaller.setCosto(facturaTallerTemp.getCosto());
			facturaTaller.setDescripcion(facturaTallerTemp.getDescripcion());
			facturaTaller.setFactura(factura);
			facturaTaller.setItbis(facturaTallerTemp.getItbis());
			facturaTaller.setPrecio(facturaTallerTemp.getPrecio());
			facturaTaller.setSubtotal(facturaTallerTemp.getSubtotal());
			facturaTaller.setTallerArticulo(facturaTallerTemp.getTallerArticulo());
			facturaTaller.setUsuario(usuario);
			serviceFacturasDetallesTaller.guardar(facturaTaller);
		}
		
		//obtenemos los seriales temporales de la factura
		List<FacturaSerialTemp> listaSerialesTemp = serviceFacturasSerialesTemp.buscarPorFacturaTemp(facturaTemp);
		//borramos registros temporales
		if(!listaSerialesTemp.isEmpty()) {
			//borramos los articulos con serial
			serviceFacturasSerialesTemp.eliminarListaSeriales(listaSerialesTemp);
		}
		if(!facturaDetallesTemp.isEmpty()) {
			//borramos detalles de articulos
			serviceFacturasDetallesTemp.eliminarListadoDetalles(facturaDetallesTemp);
		}
		if(!facturasServicioTemp.isEmpty()) {
			//borramos los servicios
			serviceFacturasServiciosTemp.eliminarListaServicios(facturasServicioTemp);
		}
		if(!detallesPagosTemp.isEmpty()) {
			//borramos los pagos
			serviceFacturaDetallesPagosTemp.eliminarListaPagos(detallesPagosTemp);
		}
		if(!facturasTalleresTemp.isEmpty()) {
			//borramos lostalleres
			serviceFacturasTalleresTemp.elminar(facturasTalleresTemp);
		}
		
		if(facturaTemp.getTaller()!=null) {
			facturaTemp.getTaller().setCompletado(1);
			facturaTemp.getTaller().setFacturaTemp(null);
			serviceTalleres.guardar(facturaTemp.getTaller());
		}
		
		//Borramos la factura temporal
		serviceFacturasTemp.eliminar(facturaTemp); 
				
		model.addAttribute("response", factura.getId());
		return "facturas/factura :: #responseGeneratedInvoice";
	}
	
	@GetMapping("/download/{id}")
	public void descargarFactura(@PathVariable("id") Integer idFactura,
			HttpServletRequest request, 
            HttpServletResponse response
            ) throws JRException, SQLException {

		Factura factura = serviceFacturas.buscarPorId(idFactura);
		
		if(factura!=null) {
			List<DetalleFactura> listaDetalle = new LinkedList<>();
			
			//verificamos los detalles en los articulos
			List<FacturaDetalle> serviFacturaDetalles = serviceFacturasDetalles.buscarPorFactura(factura);
			for (FacturaDetalle facturaDetalle : serviFacturaDetalles) {
				DetalleFactura detalleFacturaArticulo = new DetalleFactura();
				detalleFacturaArticulo.setId(facturaDetalle.getId());
				detalleFacturaArticulo.setCodigo(facturaDetalle.getArticulo().getCodigo());
				detalleFacturaArticulo.setOriginalTable("factura_detalle");
				detalleFacturaArticulo.setCantidad(facturaDetalle.getCantidad());
				detalleFacturaArticulo.setDescripcion(facturaDetalle.getArticulo().getNombre());
				detalleFacturaArticulo.setItbis(facturaDetalle.getItbis());
				detalleFacturaArticulo.setPrecio(facturaDetalle.getPrecio());
				detalleFacturaArticulo.setSubtotal(facturaDetalle.getSubtotal());
				listaDetalle.add(detalleFacturaArticulo);
			}
			
			//verificamos los servicios
			List<FacturaDetalleServicio> detalleServicios = facturasDetallesServiciosService.buscarPorFactura(factura);
			for (FacturaDetalleServicio facturaDetalleServicio : detalleServicios) {
				DetalleFactura detalleFacturaServicio = new DetalleFactura();
				detalleFacturaServicio.setId(facturaDetalleServicio.getId());
				detalleFacturaServicio.setCodigo("");
				detalleFacturaServicio.setOriginalTable("factura_detalle_servicio");
				detalleFacturaServicio.setCantidad(facturaDetalleServicio.getCantidad());
				detalleFacturaServicio.setDescripcion(facturaDetalleServicio.getDescripcion());
				detalleFacturaServicio.setItbis(facturaDetalleServicio.getItbis());
				detalleFacturaServicio.setPrecio(facturaDetalleServicio.getPrecio());
				detalleFacturaServicio.setSubtotal(facturaDetalleServicio.getSubtotal());
				listaDetalle.add(detalleFacturaServicio);
			}
			
			JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreport);
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			
			//convertimos la lista a JRBeanCollectionDataSource
			JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(listaDetalle);
			
			parameters.put("idFactura", idFactura); 
			parameters.put("imagen", rutaImagenes+factura.getAlmacen().getImagen());
			parameters.put("detalleFactura", itemsJRBean);
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());
			
			String dataDirectory = tempFolder + pathSeparator + "factura"+factura.getId()+".pdf";
			
			tempFolder += pathSeparator;
			
			JasperExportManager.exportReportToPdfFile(jasperPrint,dataDirectory);
		
			 //If user is not authorized - he should be thrown out from here itself
	         
	        //Authorized user will download the file
//	        String dataDirectory = request.getServletContext().getRealPath("/WEB-INF/downloads/pdf/");
	        Path file = Paths.get(tempFolder, "factura"+factura.getId()+".pdf");
	        if (Files.exists(file)) 
	        {
	            response.setContentType("application/pdf");
	            response.addHeader("Content-Disposition", "attachment; filename="+"factura"+factura.getId()+".pdf");
	            try
	            {
	                Files.copy(file, response.getOutputStream());
	                response.getOutputStream().flush();
	            } 
	            catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }
		}
	}
	
	@GetMapping("/print/{id}")
	public void imprimirFactura(@PathVariable("id") Integer idFactura,
			HttpServletRequest request, 
            HttpServletResponse response
            ) throws JRException, SQLException {
        
		Factura factura = serviceFacturas.buscarPorId(idFactura);
		
		if(factura!=null) {
			List<DetalleFactura> listaDetalle = new LinkedList<>();
			
			//verificamos los detalles en los articulos
			List<FacturaDetalle> serviFacturaDetalles = serviceFacturasDetalles.buscarPorFactura(factura);
			for (FacturaDetalle facturaDetalle : serviFacturaDetalles) {
				DetalleFactura detalleFacturaArticulo = new DetalleFactura();
				detalleFacturaArticulo.setId(facturaDetalle.getId());
				detalleFacturaArticulo.setCodigo(facturaDetalle.getArticulo().getCodigo());
				detalleFacturaArticulo.setOriginalTable("factura_detalle");
				detalleFacturaArticulo.setCantidad(facturaDetalle.getCantidad());
				detalleFacturaArticulo.setDescripcion(facturaDetalle.getArticulo().getNombre());
				detalleFacturaArticulo.setItbis(facturaDetalle.getItbis());
				detalleFacturaArticulo.setPrecio(facturaDetalle.getPrecio());
				detalleFacturaArticulo.setSubtotal(facturaDetalle.getSubtotal());
				listaDetalle.add(detalleFacturaArticulo);
			}
			
			//verificamos los servicios
			List<FacturaDetalleServicio> detalleServicios = facturasDetallesServiciosService.buscarPorFactura(factura);
			for (FacturaDetalleServicio facturaDetalleServicio : detalleServicios) {
				DetalleFactura detalleFacturaServicio = new DetalleFactura();
				detalleFacturaServicio.setId(facturaDetalleServicio.getId());
				detalleFacturaServicio.setOriginalTable("factura_detalle_servicio");
				detalleFacturaServicio.setCantidad(facturaDetalleServicio.getCantidad());
				detalleFacturaServicio.setDescripcion(facturaDetalleServicio.getDescripcion());
				detalleFacturaServicio.setItbis(facturaDetalleServicio.getItbis());
				detalleFacturaServicio.setPrecio(facturaDetalleServicio.getPrecio());
				detalleFacturaServicio.setSubtotal(facturaDetalleServicio.getSubtotal());
				listaDetalle.add(detalleFacturaServicio);
			}
			
			//verificamos si tiene taller
			List<FacturaDetalleTaller> detallesTaller = serviceFacturasDetallesTaller.buscarPorFactura(factura);
			for (FacturaDetalleTaller detalleTaller : detallesTaller) {
				DetalleFactura detalleFacturaTaller = new DetalleFactura();
				detalleFacturaTaller.setId(detalleTaller.getId());
				detalleFacturaTaller.setOriginalTable("facturas_detalle_talleres");
				detalleFacturaTaller.setCantidad(detalleTaller.getCantidad());
				detalleFacturaTaller.setDescripcion(detalleTaller.getDescripcion());
				detalleFacturaTaller.setItbis(detalleTaller.getItbis());
				detalleFacturaTaller.setPrecio(detalleTaller.getPrecio());
				detalleFacturaTaller.setSubtotal(detalleTaller.getSubtotal());
				listaDetalle.add(detalleFacturaTaller);
			}
			
			JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreport);
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			
			//convertimos la lista a JRBeanCollectionDataSource
			JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(listaDetalle);
			
			parameters.put("idFactura", idFactura); 
			parameters.put("imagen", rutaImagenes+factura.getAlmacen().getImagen());
			parameters.put("detalleFactura", itemsJRBean);
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());
			
			String dataDirectory = tempFolder + pathSeparator + "factura"+factura.getId()+".pdf";
			
			tempFolder += pathSeparator;
	
			JasperExportManager.exportReportToPdfFile(jasperPrint,dataDirectory);
			
			 //If user is not authorized - he should be thrown out from here itself
	         
	        //Authorized user will download the file
	        Path file = Paths.get(tempFolder, "factura"+factura.getId()+".pdf");
	        if (Files.exists(file)) 
	        {
	            String mimeType = URLConnection.guessContentTypeFromName(tempFolder+"factura"+factura.getId()+".pdf");
	            if (mimeType == null) mimeType = "application/octet-stream";
	            response.setContentType(mimeType);
	            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + "factura"+factura.getId()+".pdf" + "\""));
	            try
	            {
	                Files.copy(file, response.getOutputStream());
	                response.getOutputStream().flush();
					Files.delete(file);
	            } 
	            catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }
		}
	}
	
	@GetMapping("/cancelarFactura")
	public String cancelarFactura(HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaTemp facturaTemp = serviceFacturasTemp.buscarPorUsuario(usuario);
		List<FacturaDetalleTemp> facturaDetallesTemp = serviceFacturasDetallesTemp.buscarPorUsuarioAlmacen(usuario, usuario.getAlmacen());
		List<FacturaDetallePagoTemp> detallesPagosTemp = serviceFacturaDetallesPagosTemp.buscarPorFacturaTemp(facturaTemp); 
		List<FacturaServicioTemp> facturasServicioTemp = serviceFacturasServiciosTemp.buscarPorUsuarioAlmacen(usuario, usuario.getAlmacen());
		List<FacturaSerialTemp> listaSerialesTemp = serviceFacturasSerialesTemp.buscarPorFacturaTemp(facturaTemp);
		List<FacturaTallerTemp> facturasTallersTemp = serviceFacturasTalleresTemp.buscarPorFacturaTemp(facturaTemp);
		
		//borramos registros temporales
		if(!listaSerialesTemp.isEmpty()) {
			//borramos los articulos con serial
			for (FacturaSerialTemp seriales : listaSerialesTemp) {
				List<ArticuloSerial> serialesDelArticulo = serviceArticulosSeriales.buscarPorSerialAndAlmacen(String.valueOf(seriales.getId_serial()), usuario.getAlmacen());
				if(!serialesDelArticulo.isEmpty()) {
					ArticuloSerial temp = serialesDelArticulo.get(0);
					temp.setEn_uso(0);
					serviceArticulosSeriales.guardar(temp);
				}
			}
			serviceFacturasSerialesTemp.eliminarListaSeriales(listaSerialesTemp);
		}
		if(!facturaDetallesTemp.isEmpty()) {
			//borramos detalles de articulos
			serviceFacturasDetallesTemp.eliminarListadoDetalles(facturaDetallesTemp);
		}
		if(!facturasServicioTemp.isEmpty()) {
			//borramos los servicios
			serviceFacturasServiciosTemp.eliminarListaServicios(facturasServicioTemp);
		}
		if(!detallesPagosTemp.isEmpty()) {
			//borramos los pagos
			serviceFacturaDetallesPagosTemp.eliminarListaPagos(detallesPagosTemp);
		}
		if(!facturasTallersTemp.isEmpty()) {
			Taller taller = facturasTallersTemp.get(0).getTaller();
			//Los items volveran al taller
			for (FacturaTallerTemp facturaTallerTemp : facturasTallersTemp) {
				TallerDetalle detalle = facturaTallerTemp.getTallerDetalle();
				TallerArticulo tallerArticulo = detalle.getTallerArticulo();
				if(tallerArticulo!=null) {
					if(tallerArticulo.getArticulo()!=null) {
						//verificamos si el articulo viene de inventario
						if(tallerArticulo.getArticulo()!=null) {
//							tallerArticulo.setCantidad(tallerArticulo.getCantidad()+detalle.getCantidad()); 
//							serviceTalleresArticulos.guardar(tallerArticulo);
//							taller.setTotal(taller.getTotal()-detalle.getSubtotal());
						}
					}else {
//						tallerArticulo.setCantidad(tallerArticulo.getCantidad()+detalle.getCantidad()); 
//						serviceTalleresArticulos.guardar(tallerArticulo);
//						taller.setTotal(taller.getTotal()-detalle.getSubtotal());
					}
				}
			}
			taller.setEstado("Abierto");
			taller.setFacturaTemp(null);
			serviceTalleres.guardar(taller);
			serviceFacturasTalleresTemp.elminar(facturasTallersTemp);
		}
		//Verificamos si existe registros en taller que usen la factura temporal
		List<Taller> listaTaller = serviceTalleres.buscarPorFacturaTemp(facturaTemp);
		if(!listaTaller.isEmpty()) {
			for (Taller taller : listaTaller) {
				taller.setFacturaTemp(null);
				serviceTalleres.guardar(taller);
			}
		}
		//Borramos la factura temporal
		serviceFacturasTemp.eliminar(facturaTemp); 	
		return "redirect:/facturas/create";
	}
	
	@GetMapping("/cxc")
	public String listaFacturasPendientesPorPago(Model model,HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//Listamos las facturas de credito que esten pendientes por abono
		List<Factura> facturas = serviceFacturas.buscarPorAlmacen(usuario.getAlmacen()).
				stream().filter(f -> 
					f.getCondicionPago().getDia()>0 && f.getAbono()<f.getTotal_venta()
				).collect(Collectors.toList());
		List<FormaPago> formasPago = serviceFormasPagos.buscarPorAlmacen(usuario.getAlmacen());
		model.addAttribute("formasPago", formasPago);
		model.addAttribute("facturas", facturas);
		return "facturas/listaFacturasXC";
	}

	@GetMapping("/ajax/loadCuerpoFacturaPagoCxC/{idFactura}")
	public String getPagoTemp(Model model, HttpSession session, @PathVariable("idFactura") Integer idFactura) {
		Factura factura = serviceFacturas.buscarPorId(idFactura);
		//Verificamos los abonos de la factura
		List<AbonoCxC> abonos = serviceAbonos.buscarPorFactura(factura);
		List<AbonoCxCDetalle> abonoDetalles = null;
		if(!abonos.isEmpty()) {
			AbonoCxC abono = abonos.get(0);
			abonoDetalles = serviceAbonosDetalles.buscarPorIngreso(abono);
		}
		
		if(abonoDetalles == null) {
			abonoDetalles = new LinkedList<AbonoCxCDetalle>();
		}
		
		if(factura!=null) {
			List<FacturaPagoTemp> listaPagosTemp = serviceFacturasPagoTemp.buscarPorFactura(factura);
			
			Double precioTotal = factura.getTotal_venta();
			Double totalPagos = 0.0;
			Double totalRestaPagos = precioTotal-factura.getAbono();
			Double totalCambioPagos = 0.0;
			Integer mostrarCambio = 0;
			
			if(!listaPagosTemp.isEmpty()) {
				for (FacturaPagoTemp pago : listaPagosTemp) {
					totalPagos+=pago.getCantidad();
				}
				if(totalPagos<totalRestaPagos) {
					totalRestaPagos -= totalPagos;
				}else if(totalPagos>totalRestaPagos) {
					totalCambioPagos = totalPagos -totalRestaPagos;
				}else {
					totalRestaPagos = 0.0;
				}
				
				//si el ultimo pago es efectivo mostramos el cambio
				if(listaPagosTemp.get(listaPagosTemp.size()-1).getFormaPago().getNombre().equalsIgnoreCase("efectivo")) {
					mostrarCambio = 1;
				}
			}
			
			model.addAttribute("nombreCliente",factura.getNombre_cliente());
			model.addAttribute("numFactura", factura.getCodigo());
			model.addAttribute("detalles", abonoDetalles);
			model.addAttribute("mostrarCambio", mostrarCambio);
			model.addAttribute("listaPagosTemp", listaPagosTemp);
			model.addAttribute("totalPagos", df2.format(totalPagos));
			model.addAttribute("totalRestaPagos", totalCambioPagos>0? 0.0:df2.format(totalRestaPagos));
			model.addAttribute("totalCambioPagos", df2.format(totalCambioPagos));
		}
		return "facturas/cuerpoPagoXC :: cuerpoPago";
	}
	
	@PostMapping("/ajax/conversionDetalle")
	public String conversionDetalle(Model model, HttpSession session,
			@RequestParam("comprobanteFiscalID") Integer comprobanteFiscalID) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		ComprobanteFiscal comprobanteFiscal = serviceComprobantesFiscales.buscarPorId(comprobanteFiscalID);
		
		//Buscamos los detalles en la factura
		List<FacturaDetalleTemp> facturaDetallesTemp = serviceFacturasDetallesTemp.buscarPorUsuarioAlmacen(usuario, usuario.getAlmacen());
		FacturaTemp facturaTemp = serviceFacturasTemp.buscarPorUsuario(usuario);
		//Buscamos los servicios en la factura
		List<FacturaServicioTemp> facturaServiciosTemp = serviceFacturasServiciosTemp.buscarPorUsuarioAlmacen(usuario, usuario.getAlmacen());

		//Buscamos los talleres en la factura
		List<FacturaTallerTemp> facturaTalleresTemp = serviceFacturasTalleresTemp.buscarPorFacturaTemp(facturaTemp);
		
		Double total = 0.0;
		Double subTotalItbis = 0.0;
		double precio = 0.0;
		double itBis = 0.0;
		double valorItbis = 0.0;
		//Calculamos el total
		for (FacturaDetalleTemp facturaDetalleTemp : facturaDetallesTemp) {
			//Verificamos si el comprobante fiscal paga itbis
			if (comprobanteFiscal.getPaga_itbis() == 1) {
				// verificamos si el comprobante fiscal incluye el itbis en el precio
				if (comprobanteFiscal.getIncluye_itbis() == 1) {
					// realizamos las conversiones
					String temp = "1." + comprobanteFiscal.getValor_itbis().intValue();
					precio = formato2d(facturaDetalleTemp.getPrecio() / Double.parseDouble(temp));
					itBis = formato2d(facturaDetalleTemp.getPrecio() - precio);
				} else {
					precio = facturaDetalleTemp.getSubtotal()/facturaDetalleTemp.getCantidad();
					valorItbis = comprobanteFiscal.getValor_itbis().intValue();
					itBis= formato2d(precio * valorItbis/100); 
				}
			}else {
				itBis = 0.0;
				precio = facturaDetalleTemp.getInitialPrice();
			}
			int cant = facturaDetalleTemp.getCantidad();
			subTotalItbis+=facturaDetalleTemp.getItbis();
			total+=facturaDetalleTemp.getSubtotal();
			facturaDetalleTemp.setPrecio(precio);
			facturaDetalleTemp.setItbis(itBis);
			facturaDetalleTemp.setSubtotal((cant * precio) + (cant * itBis));
			serviceFacturasDetallesTemp.guardar(facturaDetalleTemp);
		}
		
		//Validaciones para servicios
		for (FacturaServicioTemp facturaServicioTemp : facturaServiciosTemp) {
			//Verificamos si el comprobante fiscal paga itbis
			if (comprobanteFiscal.getPaga_itbis() == 1) {
				// verificamos si el comprobante fiscal incluye el itbis en el precio
				if (comprobanteFiscal.getIncluye_itbis() == 1) {
					// realizamos las conversiones
					String temp = "1." + comprobanteFiscal.getValor_itbis().intValue();
					precio = formato2d(facturaServicioTemp.getPrecio() / Double.parseDouble(temp));
					itBis = formato2d(facturaServicioTemp.getPrecio() - precio);
				} else {
					precio = facturaServicioTemp.getSubtotal()/facturaServicioTemp.getCantidad();
					valorItbis = comprobanteFiscal.getValor_itbis().intValue();
					itBis= formato2d(precio * valorItbis/100); 
				}
			}else {
				itBis = 0.0;
				precio = facturaServicioTemp.getInitialPrice();
			}
			int cant = facturaServicioTemp.getCantidad();
			subTotalItbis+=facturaServicioTemp.getItbis();
			total+=facturaServicioTemp.getSubtotal();
			facturaServicioTemp.setPrecio(precio);
			facturaServicioTemp.setItbis(itBis);
			facturaServicioTemp.setSubtotal((cant * precio) + (cant * itBis));
			serviceFacturasServiciosTemp.guardar(facturaServicioTemp);
		}
		
		for (FacturaTallerTemp facturaTallerTemp : facturaTalleresTemp) {
			//Verificamos si el comprobante fiscal paga itbis
			if (comprobanteFiscal.getPaga_itbis() == 1) {
				// verificamos si el comprobante fiscal incluye el itbis en el precio
				if (comprobanteFiscal.getIncluye_itbis() == 1) {
					// realizamos las conversiones
					String temp = "1." + comprobanteFiscal.getValor_itbis().intValue();
					precio = formato2d(facturaTallerTemp.getPrecio() / Double.parseDouble(temp));
					itBis = formato2d(facturaTallerTemp.getPrecio() - precio);
				} else {
					precio = facturaTallerTemp.getSubtotal()/facturaTallerTemp.getCantidad();
					valorItbis = comprobanteFiscal.getValor_itbis().intValue();
					itBis= formato2d(precio * valorItbis/100); 
				}
			}else {
				itBis = 0.0;
				precio = facturaTallerTemp.getInitialPrice();
			}
			int cant = facturaTallerTemp.getCantidad();
			total+=facturaTallerTemp.getSubtotal();
			subTotalItbis+=facturaTallerTemp.getItbis();
			facturaTallerTemp.setPrecio(precio);
			facturaTallerTemp.setItbis(itBis);
			facturaTallerTemp.setSubtotal((cant * precio) + (cant * itBis));
			serviceFacturasTalleresTemp.guardar(facturaTallerTemp);
		}
		
		model.addAttribute("facturaTalleres", facturaTalleresTemp);
		model.addAttribute("facturaDetalles", facturaDetallesTemp);
		model.addAttribute("facturaServicios", facturaServiciosTemp);
		model.addAttribute("subTotalItbis",formato2d(subTotalItbis));
		model.addAttribute("total", formato2d(total));
		return "facturas/factura :: #responseConversion";
	}
	
	@GetMapping("/formularioReporteVentaCategoria")
	public String formularioReporteVentaCategoria(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Categoria> categorias = serviceCategorias.buscarPorPropietario(usuario.getAlmacen().getPropietario());
		model.addAttribute("categorias", categorias);
		model.addAttribute("dateAcct", new Date());
		return "facturas/generarReporteVentaCategoria";
	}
	
	@PostMapping("/reporteVentaXCategoria")
	public void valorInventario(HttpSession session, HttpServletRequest request, 
			HttpServletResponse response, Integer idCategoria, String desde, String hasta) throws JRException, SQLException, ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<ReporteVenta> reporteVentas = new LinkedList<>();
		List<Categoria> categorias = new LinkedList<>();
		
		if(idCategoria == 0) {
			categorias = serviceCategorias.buscarPorPropietario(usuario.getAlmacen().getPropietario());
		}else {
			categorias.add(serviceCategorias.buscarPorId(idCategoria));
		}
		
		List<Factura> facturas = serviceFacturas.buscarFacturaAlmacenFechas(usuario.getAlmacen(),
				formato.parse(desde), formato.parse(hasta));
		
		for (Factura factura : facturas) {
			//Detalles
			List<FacturaDetalle> facturaDetalles = serviceFacturasDetalles.buscarPorFactura(factura);
			for (FacturaDetalle facturaDetalle : facturaDetalles) {
				if(facturaDetalle.getImei().equals("NO") || facturaDetalle.getImei().equals("0")) {
					ReporteVenta reporteVenta = new ReporteVenta();
					reporteVenta.setArticulo(facturaDetalle.getArticulo().getNombre());
					reporteVenta.setCantidad(facturaDetalle.getCantidad());
					reporteVenta.setCosto(facturaDetalle.getCosto());
					reporteVenta.setFactura(factura.getCodigo().toString());
					reporteVenta.setId(facturaDetalle.getId());
					reporteVenta.setTabla("facturas_detalle");
					reporteVenta.setPrecio(facturaDetalle.getPrecio()+facturaDetalle.getItbis());
					reporteVenta.setTotal(facturaDetalle.getSubtotal());
					if(idCategoria == 0) {
						//Todas las categorias
						reporteVentas.add(reporteVenta);
					}else {
						//categoria especifica
						if(facturaDetalle.getArticulo().getCategoria().getId() == idCategoria) {
							reporteVentas.add(reporteVenta);
						}
					}
				}else {
					//Tiene serial
					ReporteVenta reporteVenta = new ReporteVenta();
					reporteVenta.setCantidad(facturaDetalle.getCantidad());
					reporteVenta.setCosto(facturaDetalle.getCosto());
					reporteVenta.setFactura(factura.getCodigo().toString());
					reporteVenta.setId(facturaDetalle.getId());
					reporteVenta.setTabla("facturas_detalle");
					reporteVenta.setPrecio(facturaDetalle.getPrecio()+facturaDetalle.getItbis());
					reporteVenta.setTotal(facturaDetalle.getSubtotal());
					List<FacturaDetalleSerial> facturaDetalleSeriales = serviceFacturasDetallesSeriales.buscarPorDetalle(facturaDetalle);
					String nombreTemp = facturaDetalle.getArticulo().getNombre()+" - ";
					for (FacturaDetalleSerial facturaDetalleSerial : facturaDetalleSeriales) {
						nombreTemp+=facturaDetalleSerial.getSerial()+",";
					}
					nombreTemp=nombreTemp.substring(0, nombreTemp.length()-1);
					reporteVenta.setArticulo(nombreTemp);
					if(idCategoria == 0) {
						//Todas las categorias
						reporteVentas.add(reporteVenta);
					}else {
						//categoria especifica
						if(facturaDetalle.getArticulo().getCategoria().getId() == idCategoria) {
							reporteVentas.add(reporteVenta);
						}
					}
				}
			}
			//talleres
			List<FacturaDetalleTaller> facturaDetalleTalleres = serviceFacturasDetallesTaller.buscarPorFactura(factura);
			for (FacturaDetalleTaller facturaDetalleTaller : facturaDetalleTalleres) {
				if(facturaDetalleTaller.getArticulo()!=null) {
					ReporteVenta reporteVenta = new ReporteVenta();
					reporteVenta.setCantidad(facturaDetalleTaller.getCantidad());
					reporteVenta.setCosto(facturaDetalleTaller.getArticulo().getCosto());
					reporteVenta.setFactura(factura.getCodigo().toString());
					reporteVenta.setId(facturaDetalleTaller.getId());
					reporteVenta.setTabla("facturas_detalle_talleres");
					reporteVenta.setPrecio(facturaDetalleTaller.getPrecio()+facturaDetalleTaller.getItbis());
					reporteVenta.setTotal(facturaDetalleTaller.getSubtotal());
					reporteVenta.setArticulo(facturaDetalleTaller.getArticulo().getNombre());
				
					if(idCategoria == 0) {
						//Todas las categorias
						reporteVentas.add(reporteVenta);
					}else {
						//categoria especifica
						if(facturaDetalleTaller.getArticulo().getCategoria().getId() == idCategoria) {
							reporteVentas.add(reporteVenta);
						}
					}
				}
			}
		}
		
		//Compilamos el reporte
		JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreportVentaCategoria);

		Map<String, Object> parameters = new HashMap<String, Object>();

		// convertimos la lista a JRBeanCollectionDataSource
		JRBeanCollectionDataSource articulosReporteJRBean = new JRBeanCollectionDataSource(reporteVentas);

		parameters.put("idUsuario", usuario.getId());
		parameters.put("imagen", rutaImagenes + usuario.getAlmacen().getImagen());
		parameters.put("total", reporteVentas.size());
		parameters.put("fechaDesde", desde);
		parameters.put("fechaHasta", hasta);
		parameters.put("author", usuario.getUsername());
		parameters.put("categoria", idCategoria == 0 ? "TODAS" : categorias.get(0).getNombre());
		parameters.put("reporteVentas", articulosReporteJRBean);

		// Objeto de impresion jr
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

		String dataDirectory = tempFolder + pathSeparator + "reporteVentas" + usuario.getId() + ".pdf";

		tempFolder += pathSeparator;

		JasperExportManager.exportReportToPdfFile(jasperPrint, dataDirectory);

		Path file = Paths.get(tempFolder, "reporteVentas" + usuario.getId() + ".pdf");
		if (Files.exists(file)) {
			String mimeType = URLConnection
					.guessContentTypeFromName(tempFolder + "reporteVentas" + usuario.getId() + ".pdf");
			if (mimeType == null)
				mimeType = "application/octet-stream";
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition",
					String.format("inline; filename=\"" + "reporteVentas" + usuario.getId() + ".pdf" + "\""));
			try {
				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();
				Files.delete(file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@GetMapping("/formularioReporteVentaCliente")
	public String formularioReporteVentaCliente(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Cliente> clientes = serviceClientes.buscarPorAlmacen(usuario.getAlmacen());
		model.addAttribute("clientes", clientes);
		model.addAttribute("dateAcct", new Date());
		return "facturas/generarReporteVentaCliente";
	}
	
	@PostMapping("/reporteVentaXCliente") 
	public void ventaCliente(HttpSession session, HttpServletRequest request, 
			HttpServletResponse response, Integer idCliente, String desde, String hasta) throws JRException, SQLException, ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		List<ReporteVenta> reporteVentas = new LinkedList<>();
		List<Cliente> clientes = new LinkedList<>();
		
		if(idCliente == 0) {
			clientes = serviceClientes.buscarPorAlmacen(usuario.getAlmacen());
		}else {
			clientes.add(serviceClientes.buscarPorIdCliente(idCliente));
		}
		
		List<Factura> facturas = serviceFacturas.buscarFacturasAlmacenFechasCliente(usuario.getAlmacen(), formato.parse(desde), 
				formato.parse(hasta), clientes);
		
		for (Factura factura : facturas) {
			//Detalles
			List<FacturaDetalle> facturaDetalles = serviceFacturasDetalles.buscarPorFactura(factura);
			for (FacturaDetalle facturaDetalle : facturaDetalles) {
				if(facturaDetalle.getImei().equals("NO") || facturaDetalle.getImei().equals("0")) {
					ReporteVenta reporteVenta = new ReporteVenta();
					reporteVenta.setArticulo(facturaDetalle.getArticulo().getNombre());
					reporteVenta.setCantidad(facturaDetalle.getCantidad());
					reporteVenta.setCosto(facturaDetalle.getCosto());
					reporteVenta.setFactura(factura.getCodigo().toString());
					reporteVenta.setId(facturaDetalle.getId());
					reporteVenta.setTabla("facturas_detalle");
					reporteVenta.setPrecio(facturaDetalle.getPrecio()+facturaDetalle.getItbis());
					reporteVenta.setTotal(facturaDetalle.getSubtotal());
					if(idCliente == 0) {
						//Todos los clientes
						reporteVentas.add(reporteVenta);
					}else {
						//cliente especifico
						if(facturaDetalle.getFactura().getCliente().getId() == idCliente) {
							reporteVentas.add(reporteVenta);
						}
					}
				}else {
					//Tiene serial
					ReporteVenta reporteVenta = new ReporteVenta();
					reporteVenta.setCantidad(facturaDetalle.getCantidad());
					reporteVenta.setCosto(facturaDetalle.getCosto());
					reporteVenta.setFactura(factura.getCodigo().toString());
					reporteVenta.setId(facturaDetalle.getId());
					reporteVenta.setTabla("facturas_detalle");
					reporteVenta.setPrecio(facturaDetalle.getPrecio()+facturaDetalle.getItbis());
					reporteVenta.setTotal(facturaDetalle.getSubtotal());
					List<FacturaDetalleSerial> facturaDetalleSeriales = serviceFacturasDetallesSeriales.buscarPorDetalle(facturaDetalle);
					String nombreTemp = facturaDetalle.getArticulo().getNombre()+" - ";
					for (FacturaDetalleSerial facturaDetalleSerial : facturaDetalleSeriales) {
						nombreTemp+=facturaDetalleSerial.getSerial()+",";
					}
					nombreTemp=nombreTemp.substring(0, nombreTemp.length()-1);
					reporteVenta.setArticulo(nombreTemp);
					if(idCliente == 0) {
						//Todos los clientes
						reporteVentas.add(reporteVenta);
					}else {
						//cliente especifico
						if(facturaDetalle.getFactura().getCliente().getId() == idCliente) {
							reporteVentas.add(reporteVenta);
						}
					}
				}
			}
			//Los talleres no tienen clientes registrados en el sistema
		}

		//Compilamos el reporte
		JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreportVentaCliente);

		Map<String, Object> parameters = new HashMap<String, Object>();

		// convertimos la lista a JRBeanCollectionDataSource
		JRBeanCollectionDataSource articulosReporteJRBean = new JRBeanCollectionDataSource(reporteVentas);

		parameters.put("idUsuario", usuario.getId());
		parameters.put("imagen", rutaImagenes + usuario.getAlmacen().getImagen());
		parameters.put("total", reporteVentas.size());
		parameters.put("fechaDesde", desde);
		parameters.put("fechaHasta", hasta);
		parameters.put("author", usuario.getUsername());
		parameters.put("cliente", idCliente == 0 ? "TODOS" : clientes.get(0).getNombre());
		parameters.put("reporteVentas", articulosReporteJRBean);

		// Objeto de impresion jr
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

		String dataDirectory = tempFolder + pathSeparator + "reporteVentasCliente" + usuario.getId() + ".pdf";

		tempFolder += pathSeparator;

		JasperExportManager.exportReportToPdfFile(jasperPrint, dataDirectory);

		Path file = Paths.get(tempFolder, "reporteVentasCliente" + usuario.getId() + ".pdf");
		if (Files.exists(file)) {
			String mimeType = URLConnection
					.guessContentTypeFromName(tempFolder + "reporteVentasCliente" + usuario.getId() + ".pdf");
			if (mimeType == null)
				mimeType = "application/octet-stream";
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition",
					String.format("inline; filename=\"" + "reporteVentasCliente" + usuario.getId() + ".pdf" + "\""));
			try {
				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();
				Files.delete(file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}
	
	@GetMapping("/formularioReporteVentaUsuario")
	public String formularioReporteVentaUsuario(Model model, HttpSession session) {
		Usuario usuarioAcct = (Usuario) session.getAttribute("usuario");
		List<Usuario> usuarios = serviceUsuarios.buscarPorAlmacen(usuarioAcct.getAlmacen());
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("dateAcct", new Date());
		return "facturas/generarReporteVentaUsuario";
	}
	
	@PostMapping("/reporteVentaXUsuario") 
	public void ventaUsuario(HttpSession session, HttpServletRequest request, 
			HttpServletResponse response, Integer idUsuario, String desde, String hasta) throws JRException, SQLException, ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Usuario usuarioAcct = (Usuario) session.getAttribute("usuario");
		
		List<ReporteVenta> reporteVentas = new LinkedList<>();
		List<Usuario> usuarios = new LinkedList<>();
		
		if(idUsuario == 0) {
			usuarios = serviceUsuarios.buscarPorAlmacen(usuarioAcct.getAlmacen());
		}else {
			usuarios.add(serviceUsuarios.buscarPorId(idUsuario));
		}
		
		List<Factura> facturas = serviceFacturas.buscarFacturaCuadreMultiUsuario(usuarioAcct.getAlmacen(), formato.parse(desde), 
				formato.parse(hasta), usuarios);
		
		for (Factura factura : facturas) {
			//Detalles
			List<FacturaDetalle> facturaDetalles = serviceFacturasDetalles.buscarPorFactura(factura);
			for (FacturaDetalle facturaDetalle : facturaDetalles) {
				if(facturaDetalle.getImei().equals("NO") || facturaDetalle.getImei().equals("0")) {
					ReporteVenta reporteVenta = new ReporteVenta();
					reporteVenta.setArticulo(facturaDetalle.getArticulo().getNombre());
					reporteVenta.setCantidad(facturaDetalle.getCantidad());
					reporteVenta.setCosto(facturaDetalle.getCosto());
					reporteVenta.setFactura(factura.getCodigo().toString());
					reporteVenta.setId(facturaDetalle.getId());
					reporteVenta.setTabla("facturas_detalle");
					reporteVenta.setPrecio(facturaDetalle.getPrecio()+facturaDetalle.getItbis());
					reporteVenta.setTotal(facturaDetalle.getSubtotal());
					if(idUsuario == 0) {
						//Todos usuarios
						reporteVentas.add(reporteVenta);
					}else {
						//usuario especifico
						if(factura.getUsuario().getId() == idUsuario) {
							reporteVentas.add(reporteVenta);
						}
					}
				}else {
					//Tiene serial
					ReporteVenta reporteVenta = new ReporteVenta();
					reporteVenta.setCantidad(facturaDetalle.getCantidad());
					reporteVenta.setCosto(facturaDetalle.getCosto());
					reporteVenta.setFactura(factura.getCodigo().toString());
					reporteVenta.setId(facturaDetalle.getId());
					reporteVenta.setTabla("facturas_detalle");
					reporteVenta.setPrecio(facturaDetalle.getPrecio()+facturaDetalle.getItbis());
					reporteVenta.setTotal(facturaDetalle.getSubtotal());
					List<FacturaDetalleSerial> facturaDetalleSeriales = serviceFacturasDetallesSeriales.buscarPorDetalle(facturaDetalle);
					String nombreTemp = facturaDetalle.getArticulo().getNombre()+" - ";
					for (FacturaDetalleSerial facturaDetalleSerial : facturaDetalleSeriales) {
						nombreTemp+=facturaDetalleSerial.getSerial()+",";
					}
					nombreTemp=nombreTemp.substring(0, nombreTemp.length()-1);
					reporteVenta.setArticulo(nombreTemp);
					if(idUsuario == 0) {
						//Todos los usuarios
						reporteVentas.add(reporteVenta);
					}else {
						//usuario especifico
						if(factura.getUsuario().getId() == idUsuario) {
							reporteVentas.add(reporteVenta);
						}
					}
				}
			}
			//talleres
			List<FacturaDetalleTaller> facturaDetalleTalleres = serviceFacturasDetallesTaller.buscarPorFactura(factura);
			for (FacturaDetalleTaller facturaDetalleTaller : facturaDetalleTalleres) {
				if(facturaDetalleTaller.getArticulo()!=null) {
					ReporteVenta reporteVenta = new ReporteVenta();
					reporteVenta.setCantidad(facturaDetalleTaller.getCantidad());
					reporteVenta.setCosto(facturaDetalleTaller.getArticulo().getCosto());
					reporteVenta.setFactura(factura.getCodigo().toString());
					reporteVenta.setId(facturaDetalleTaller.getId());
					reporteVenta.setTabla("facturas_detalle_talleres");
					reporteVenta.setPrecio(facturaDetalleTaller.getPrecio()+facturaDetalleTaller.getItbis());
					reporteVenta.setTotal(facturaDetalleTaller.getSubtotal());
					reporteVenta.setArticulo(facturaDetalleTaller.getArticulo().getNombre());
				
					if(idUsuario == 0) {
						//Todos los usuarios
						reporteVentas.add(reporteVenta);
					}else {
						//usuario especifico
						if(facturaDetalleTaller.getUsuario().getId()==idUsuario) {
							reporteVentas.add(reporteVenta);
						}
					}
				}
			}
		}
		
		//Compilamos el reporte
		JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreportVentaUsuario);

		Map<String, Object> parameters = new HashMap<String, Object>();

		// convertimos la lista a JRBeanCollectionDataSource
		JRBeanCollectionDataSource articulosReporteJRBean = new JRBeanCollectionDataSource(reporteVentas);

		parameters.put("idUsuario", usuarioAcct.getId());
		parameters.put("imagen", rutaImagenes + usuarioAcct.getAlmacen().getImagen());
		parameters.put("total", reporteVentas.size());
		parameters.put("fechaDesde", desde);
		parameters.put("fechaHasta", hasta);
		parameters.put("author", usuarioAcct.getUsername());
		parameters.put("usuario", idUsuario == 0 ? "TODOS" : usuarios.get(0).getUsername());
		parameters.put("reporteVentas", articulosReporteJRBean);

		// Objeto de impresion jr
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

		String dataDirectory = tempFolder + pathSeparator + "reporteVentasUsuario" + usuarioAcct.getId() + ".pdf";

		tempFolder += pathSeparator;

		JasperExportManager.exportReportToPdfFile(jasperPrint, dataDirectory);

		Path file = Paths.get(tempFolder, "reporteVentasUsuario" + usuarioAcct.getId() + ".pdf");
		if (Files.exists(file)) {
			String mimeType = URLConnection
					.guessContentTypeFromName(tempFolder + "reporteVentasUsuario" + usuarioAcct.getId() + ".pdf");
			if (mimeType == null)
				mimeType = "application/octet-stream";
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition",
					String.format("inline; filename=\"" + "reporteVentasUsuario" + usuarioAcct.getId() + ".pdf" + "\""));
			try {
				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();
				Files.delete(file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	
	}
	
	@GetMapping("/formularioReporteVentaVendedor")
	public String formularioReporteVentaVendedor(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Vendedor> vendedores = serviceVendedores.buscarPorAlmacen(usuario.getAlmacen());
		model.addAttribute("vendedores", vendedores);
		model.addAttribute("dateAcct", new Date());
		return "facturas/generarReporteVentaVendedor";
	}
	
	@PostMapping("/reporteVentaXVendedor") 
	public void ventaVendedor(HttpSession session, HttpServletRequest request, 
			HttpServletResponse response, Integer idVendedor, String desde, String hasta) throws JRException, SQLException, ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		List<ReporteVenta> reporteVentas = new LinkedList<>();
		List<Vendedor> vendedores = new LinkedList<>();
		
		if(idVendedor == 0) {
			vendedores = serviceVendedores.buscarPorAlmacen(usuario.getAlmacen());
		}else {
			vendedores.add(serviceVendedores.buscarPorIdVendedor(idVendedor));
		}
		
		List<Factura> facturas = serviceFacturas.buscarFacturasAlmacenFechasVendedor(usuario.getAlmacen(), formato.parse(desde), 
				formato.parse(hasta), vendedores);
		
		for (Factura factura : facturas) { 
			//Detalles
			List<FacturaDetalle> facturaDetalles = serviceFacturasDetalles.buscarPorFactura(factura);
			for (FacturaDetalle facturaDetalle : facturaDetalles) {
				if(facturaDetalle.getImei().equals("NO") || facturaDetalle.getImei().equals("0")) {
					ReporteVenta reporteVenta = new ReporteVenta();
					reporteVenta.setArticulo(facturaDetalle.getArticulo().getNombre());
					reporteVenta.setCantidad(facturaDetalle.getCantidad());
					reporteVenta.setCosto(facturaDetalle.getCosto());
					reporteVenta.setFactura(factura.getCodigo().toString());
					reporteVenta.setId(facturaDetalle.getId());
					reporteVenta.setTabla("facturas_detalle");
					reporteVenta.setPrecio(facturaDetalle.getPrecio()+facturaDetalle.getItbis());
					reporteVenta.setTotal(facturaDetalle.getSubtotal());
					if(factura.getVendedor() != null) {
						if(idVendedor == 0) {
							//Todos los vendedores
							reporteVentas.add(reporteVenta);
						}else {
							//vendedor especifico
							if(factura.getVendedor().getId() == idVendedor) {
								reporteVentas.add(reporteVenta);
							}
						}
					}
				}else {
					//Tiene serial
					ReporteVenta reporteVenta = new ReporteVenta();
					reporteVenta.setCantidad(facturaDetalle.getCantidad());
					reporteVenta.setCosto(facturaDetalle.getCosto());
					reporteVenta.setFactura(factura.getCodigo().toString());
					reporteVenta.setId(facturaDetalle.getId());
					reporteVenta.setTabla("facturas_detalle");
					reporteVenta.setPrecio(facturaDetalle.getPrecio()+facturaDetalle.getItbis());
					reporteVenta.setTotal(facturaDetalle.getSubtotal());
					List<FacturaDetalleSerial> facturaDetalleSeriales = serviceFacturasDetallesSeriales.buscarPorDetalle(facturaDetalle);
					String nombreTemp = facturaDetalle.getArticulo().getNombre()+" - ";
					for (FacturaDetalleSerial facturaDetalleSerial : facturaDetalleSeriales) {
						nombreTemp+=facturaDetalleSerial.getSerial()+",";
					}
					nombreTemp=nombreTemp.substring(0, nombreTemp.length()-1);
					reporteVenta.setArticulo(nombreTemp);
					if(factura.getVendedor() != null) {
						if(idVendedor == 0) {
							//Todos los vendedores
							reporteVentas.add(reporteVenta);
						}else {
							//vendedor especifico
							if(factura.getVendedor().getId() == idVendedor) {
								reporteVentas.add(reporteVenta);
							}
						}
					}
				}
			}
			//talleres
			List<FacturaDetalleTaller> facturaDetalleTalleres = serviceFacturasDetallesTaller.buscarPorFactura(factura);
			for (FacturaDetalleTaller facturaDetalleTaller : facturaDetalleTalleres) {
				if(facturaDetalleTaller.getArticulo()!=null) {
					ReporteVenta reporteVenta = new ReporteVenta();
					reporteVenta.setCantidad(facturaDetalleTaller.getCantidad());
					reporteVenta.setCosto(facturaDetalleTaller.getArticulo().getCosto());
					reporteVenta.setFactura(factura.getCodigo().toString());
					reporteVenta.setId(facturaDetalleTaller.getId());
					reporteVenta.setTabla("facturas_detalle_talleres");
					reporteVenta.setPrecio(facturaDetalleTaller.getPrecio()+facturaDetalleTaller.getItbis());
					reporteVenta.setTotal(facturaDetalleTaller.getSubtotal());
					reporteVenta.setArticulo(facturaDetalleTaller.getArticulo().getNombre());			
					
					if(factura.getVendedor() != null) {
						if(idVendedor == 0) {
							//Todos los vendedores
							reporteVentas.add(reporteVenta);
						}else {
							//vendedor especifico
							if(factura.getVendedor().getId() == idVendedor) {
								reporteVentas.add(reporteVenta);
							}
						}
					}
				}
			}
		}
		
		//Compilamos el reporte
		JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreportVentaVendedor);

		Map<String, Object> parameters = new HashMap<String, Object>();

		// convertimos la lista a JRBeanCollectionDataSource
		JRBeanCollectionDataSource articulosReporteJRBean = new JRBeanCollectionDataSource(reporteVentas);

		parameters.put("idUsuario", usuario.getId());
		parameters.put("imagen", rutaImagenes + usuario.getAlmacen().getImagen());
		parameters.put("total", reporteVentas.size());
		parameters.put("fechaDesde", desde);
		parameters.put("fechaHasta", hasta);
		parameters.put("author", usuario.getUsername());
		parameters.put("vendedor", idVendedor == 0 ? "TODOS" : vendedores.get(0).getNombre());
		parameters.put("reporteVentas", articulosReporteJRBean);

		// Objeto de impresion jr
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

		String dataDirectory = tempFolder + pathSeparator + "reporteVentasVendedor" + usuario.getId() + ".pdf";

		tempFolder += pathSeparator;

		JasperExportManager.exportReportToPdfFile(jasperPrint, dataDirectory);

		Path file = Paths.get(tempFolder, "reporteVentasVendedor" + usuario.getId() + ".pdf");
		if (Files.exists(file)) {
			String mimeType = URLConnection
					.guessContentTypeFromName(tempFolder + "reporteVentasVendedor" + usuario.getId() + ".pdf");
			if (mimeType == null)
				mimeType = "application/octet-stream";
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition",
					String.format("inline; filename=\"" + "reporteVentasVendedor" + usuario.getId() + ".pdf" + "\""));
			try {
				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();
				Files.delete(file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@GetMapping("/formularioReporteVentaTecnico")
	public String formularioReporteVentaTecnico(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Perfil perfil = servicePerfiles.buscarPorPerfil("Tecnico");
		
		//Usuarios con perfil tecnico activos
		List<Usuario> usuarios = serviceUsuarios.buscarPorAlmacenAndPerfil(usuario.getAlmacen(), perfil).
				stream().filter(u -> u.getEstatus()==1).
				collect(Collectors.toList());
		
		model.addAttribute("tecnicos", usuarios);
		model.addAttribute("dateAcct", new Date());
		return "facturas/generarReporteVentaTecnico";
	}
	
	@PostMapping("/reporteVentaTecnico") 
	public void reporteVentaTaller(HttpSession session, HttpServletRequest request, 
			HttpServletResponse response, Integer idTecnico, String desde, String hasta) throws JRException, SQLException, ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		List<ReporteVenta> reporteVentas = new LinkedList<>();
	
		Perfil perfil = servicePerfiles.buscarPorPerfil("Tecnico");
		List<Usuario> usuarios = new LinkedList<>();

		if (idTecnico == 0) {
			// Usuario con perfil tecnico activos
			usuarios = serviceUsuarios.buscarPorAlmacenAndPerfil(usuario.getAlmacen(), perfil).stream()
					.filter(u -> u.getEstatus() == 1).collect(Collectors.toList());
		} else {
			usuarios.add(serviceUsuarios.buscarPorId(idTecnico));
		}

		List<Factura> facturas = serviceFacturas.buscarFacturaAlmacenFechasTallerIsNotNull(usuario.getAlmacen(),
				formato.parse(desde), formato.parse(hasta));

		for (Factura factura : facturas) {
			if (factura.getTaller().getAsignado() != null) {
				List<FacturaDetalleTaller> facturaDetalleTalleres = serviceFacturasDetallesTaller.buscarPorFactura(factura);
				for (FacturaDetalleTaller facturaDetalleTaller : facturaDetalleTalleres) {
					ReporteVenta reporteVenta = new ReporteVenta();
					reporteVenta.setCantidad(facturaDetalleTaller.getCantidad());
					reporteVenta.setFactura(factura.getCodigo().toString());
					reporteVenta.setId(facturaDetalleTaller.getId());
					reporteVenta.setTabla("facturas_detalle_talleres");
					reporteVenta.setPrecio(facturaDetalleTaller.getPrecio()+facturaDetalleTaller.getItbis());
					reporteVenta.setTotal(facturaDetalleTaller.getSubtotal());
				
					if (facturaDetalleTaller.getArticulo() != null) {
						reporteVenta.setArticulo(facturaDetalleTaller.getArticulo().getNombre());
						reporteVenta.setCosto(facturaDetalleTaller.getArticulo().getCosto());
					}else {
						reporteVenta.setArticulo(facturaDetalleTaller.getDescripcion());
						reporteVenta.setCosto(facturaDetalleTaller.getCosto());
					}
					
					if (idTecnico == 0) {
						// Todos los vendedores
						reporteVentas.add(reporteVenta);
					} else {
						// vendedor especifico
						if (factura.getTaller().getAsignado().getId() == idTecnico) {
							reporteVentas.add(reporteVenta);
						}
					}
				}
			}
		}
		
		//Compilamos el reporte
				JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreportVentaTecnico);

				Map<String, Object> parameters = new HashMap<String, Object>();

				// convertimos la lista a JRBeanCollectionDataSource
				JRBeanCollectionDataSource articulosReporteJRBean = new JRBeanCollectionDataSource(reporteVentas);

				parameters.put("idUsuario", usuario.getId());
				parameters.put("imagen", rutaImagenes + usuario.getAlmacen().getImagen());
				parameters.put("total", reporteVentas.size());
				parameters.put("fechaDesde", desde);
				parameters.put("fechaHasta", hasta);
				parameters.put("author", usuario.getUsername());
				parameters.put("tecnico", idTecnico == 0 ? "TODOS" : usuarios.get(0).getNombre());
				parameters.put("reporteVentas", articulosReporteJRBean);

				// Objeto de impresion jr
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

				String dataDirectory = tempFolder + pathSeparator + "reporteVentasTecnico" + usuario.getId() + ".pdf";

				tempFolder += pathSeparator;

				JasperExportManager.exportReportToPdfFile(jasperPrint, dataDirectory);

				Path file = Paths.get(tempFolder, "reporteVentasTecnico" + usuario.getId() + ".pdf");
				if (Files.exists(file)) {
					String mimeType = URLConnection
							.guessContentTypeFromName(tempFolder + "reporteVentasTecnico" + usuario.getId() + ".pdf");
					if (mimeType == null)
						mimeType = "application/octet-stream";
					response.setContentType(mimeType);
					response.setHeader("Content-Disposition",
							String.format("inline; filename=\"" + "reporteVentasTecnico" + usuario.getId() + ".pdf" + "\""));
					try {
						Files.copy(file, response.getOutputStream());
						response.getOutputStream().flush();
						Files.delete(file);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
		
	}

	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}

}
