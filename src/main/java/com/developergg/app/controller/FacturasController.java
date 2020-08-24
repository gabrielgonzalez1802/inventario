package com.developergg.app.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.developergg.app.model.ArticuloAjuste;
import com.developergg.app.model.ArticuloSerial;
import com.developergg.app.model.Cliente;
import com.developergg.app.model.ComprobanteFiscal;
import com.developergg.app.model.CondicionPago;
import com.developergg.app.model.DetalleFactura;
import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetalle;
import com.developergg.app.model.FacturaDetallePagoTemp;
import com.developergg.app.model.FacturaDetalleServicio;
import com.developergg.app.model.FacturaDetalleTemp;
import com.developergg.app.model.FacturaPago;
import com.developergg.app.model.FacturaSerialTemp;
import com.developergg.app.model.FacturaServicioTemp;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.FormaPago;
import com.developergg.app.model.Taller;
import com.developergg.app.model.TipoEquipo;
import com.developergg.app.model.Usuario;
import com.developergg.app.model.Vendedor;
import com.developergg.app.service.IArticulosAjustesService;
import com.developergg.app.service.IArticulosSeriales;
import com.developergg.app.service.IClientesService;
import com.developergg.app.service.IComprobantesFiscalesService;
import com.developergg.app.service.ICondicionesPagoService;
import com.developergg.app.service.IFacturasDetallesPagoTempService;
import com.developergg.app.service.IFacturasDetallesService;
import com.developergg.app.service.IFacturasDetallesServiciosService;
import com.developergg.app.service.IFacturasDetallesTempService;
import com.developergg.app.service.IFacturasPagoService;
import com.developergg.app.service.IFacturasSerialesTempService;
import com.developergg.app.service.IFacturasService;
import com.developergg.app.service.IFacturasServiciosTempService;
import com.developergg.app.service.IFacturasTempService;
import com.developergg.app.service.IFormasPagoService;
import com.developergg.app.service.ITiposEquipoService;
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
	private IFacturasDetallesTempService facturasDetallesTempService;
	
	@Autowired
	private IFacturasDetallesService facturasDetallesService;
	
	@Autowired
	private IFacturasDetallesServiciosService facturasDetallesServiciosService;
	
	@Autowired
	private IFacturasServiciosTempService facturasServiciosTempService;
	
	@Autowired
	private IArticulosSeriales serviceArticulosSeriales;
	
	@Autowired
	private IFacturasSerialesTempService serviceFacturasSerialesTemp;
	
	@Autowired
	private IArticulosAjustesService serviceArticulosAjuste;
	
	@Autowired
	private IVendedoresService serviceVendedores;
	
	@Autowired
	private DataSource dataSource;
	
	@Value("${inventario.ruta.imagenes}")
	private String rutaImagenes;
	
	@Value("${inventario.ruta.reporte.factura}")
	private String rutaJreport;
	
	private String tempFolder =  System.getProperty("java.io.tmpdir");
	private String pathSeparator = System.getProperty("file.separator");
	
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
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		CondicionPago condicionPago = serviceCondicionesPago.buscarPorId(condicionPagoID);
		FacturaTemp facturaTemp = serviceFacturasTemp.buscarPorUsuario(usuario);
		facturaTemp.setCondicionPago(condicionPago);
		serviceFacturasTemp.guardar(facturaTemp);
		return "facturas/factura :: #responseUpdateCondicion";
	}

	@PostMapping("/ajax/guardarFactura")
	public String guardarFactura(Model model, HttpSession session,
			@RequestParam(name = "total_venta") Double total_venta,@RequestParam(name = "nombreCliente") String nombreCliente,
			@RequestParam(name = "telefonoCliente") String telefonoCliente, @RequestParam(name = "rncCliente") String rncCliente,
			@RequestParam("total_itbis") Double total_itbis) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaTemp facturaTemp = serviceFacturasTemp.buscarPorUsuario(usuario);
		List<FacturaDetalleTemp> facturaDetallesTemp = facturasDetallesTempService.buscarPorUsuarioAlmacen(usuario, usuario.getAlmacen());
		
		// validacion para condicion de pago contado
//		if (facturaTemp.getCondicionPago().getNombre().equalsIgnoreCase("contado")) {
//
//		}

		int tempCode = 1;

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
		//factura.setCredito(credito); //validar
		//factura.setCuotas(); //validar
		//factura.setTaller(taller); //se agregara cuando se complete la parte del taller
		//forma_pago,avance_taller,total_itbis
		factura.setTotal_itbis(total_itbis);
		serviceFacturas.guardar(factura);
		
		//detalles del pago
		List<FacturaDetallePagoTemp> detallesPagosTemp = serviceFacturaDetallesPagosTemp.buscarPorFacturaTemp(facturaTemp); 
		for (FacturaDetallePagoTemp facturaDetallePagoTemp : detallesPagosTemp) {
			FacturaPago facturaPago = new FacturaPago();
			facturaPago.setFactura(factura);
			facturaPago.setFormaPago(facturaDetallePagoTemp.getFormaPago());
			facturaPago.setCantidad(facturaDetallePagoTemp.getMonto());
			//facturaPago.setAplicado(); //verificar
			serviceFacturasPagosService.guardar(facturaPago);
		}

		//TODO:GGONZALEZ logica del taller
		//if($id_taller_factura>0)
		 // $sql="UPDATE taller SET entregado=1,entregado_por ='$id_usuario',fecha_entrega='$fecha' WHERE id_reparacion = $id_taller_factura limit 1";
		 // mysql_query($sql);

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
			List<FacturaSerialTemp> facturasSerialesTemp = serviceFacturasSerialesTemp.buscarPorFacturaTemp(facturaTemp);
			
			if(facturaDetalle.getArticulo().getImei().equalsIgnoreCase("NO") || facturaDetalle.getArticulo().getImei().equalsIgnoreCase("0")) {
				facturaDetalle.setPrecio_minimo(facturaDetalleTemp.getArticulo().getPrecio_minimo()); //validar el precio por serial
				facturaDetalle.setPrecio_mayor(facturaDetalleTemp.getArticulo().getPrecio_mayor()); //validar el precio por serial
				facturaDetalle.setPrecio_maximo(facturaDetalleTemp.getArticulo().getPrecio_maximo());
			}else {
				for (FacturaSerialTemp fs : facturasSerialesTemp) {
					facturaDetalle.setPrecio_minimo(fs.getArticuloSerial().getPrecio_minimo());
					facturaDetalle.setPrecio_mayor(fs.getArticuloSerial().getPrecio_mayor());
					facturaDetalle.setPrecio_maximo(fs.getArticuloSerial().getPrecio_maximo());
				}
			}

			facturasDetallesService.guardar(facturaDetalle);
			
			//Proceso para crear una salida con o sin serial
			//verificamos si el producto tiene serial
			if(facturaDetalle.getArticulo().getImei().equalsIgnoreCase("SI") || facturaDetalle.getArticulo().getImei().equalsIgnoreCase("1")) {
				for (FacturaSerialTemp facturaSerialTemporal : facturasSerialesTemp) {
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
		List<FacturaServicioTemp> facturasServicioTemp = facturasServiciosTempService.buscarPorUsuarioAlmacen(usuario, usuario.getAlmacen());
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
		
		//obtenemos los seriales temporales de la factura
		List<FacturaSerialTemp> listaSerialesTemp = serviceFacturasSerialesTemp.buscarPorFacturaTemp(facturaTemp);
		//borramos registros temporales
		if(!listaSerialesTemp.isEmpty()) {
			//borramos los articulos con serial
			serviceFacturasSerialesTemp.eliminarListaSeriales(listaSerialesTemp);
		}
		if(!facturaDetallesTemp.isEmpty()) {
			//borramos detalles de articulos
			facturasDetallesTempService.eliminarListadoDetalles(facturaDetallesTemp);
		}
		if(!facturasServicioTemp.isEmpty()) {
			//borramos los servicios
			facturasServiciosTempService.eliminarListaServicios(facturasServicioTemp);
		}
		if(!detallesPagosTemp.isEmpty()) {
			//borramos los pagos
			serviceFacturaDetallesPagosTemp.eliminarListaPagos(detallesPagosTemp);
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
           // @RequestHeader String referer
            ) throws JRException, SQLException {
		
		//Check the renderer
//        if(referer != null && !referer.isEmpty()) {
//            //do nothing
//            //or send error
//        }
        
		Factura factura = serviceFacturas.buscarPorId(idFactura);
		
		if(factura!=null) {
			List<DetalleFactura> listaDetalle = new LinkedList<>();
			
			//verificamos los detalles en los articulos
			List<FacturaDetalle> serviFacturaDetalles = facturasDetallesService.buscarPorFactura(factura);
			for (FacturaDetalle facturaDetalle : serviFacturaDetalles) {
				DetalleFactura detalleFacturaArticulo = new DetalleFactura();
				detalleFacturaArticulo.setId(facturaDetalle.getId());
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

}
