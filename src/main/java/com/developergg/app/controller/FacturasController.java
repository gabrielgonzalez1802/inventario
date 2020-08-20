package com.developergg.app.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.developergg.app.model.ArticuloAjuste;
import com.developergg.app.model.ArticuloSerial;
import com.developergg.app.model.Cliente;
import com.developergg.app.model.ComprobanteFiscal;
import com.developergg.app.model.CondicionPago;
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
	
	@GetMapping("/")
	public String mostrarFacturas(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
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

		model.addAttribute("condicionesPago", condicionesPago);
		model.addAttribute("formaPagos", formaPagos);
		model.addAttribute("clientes", clientes);
		model.addAttribute("facturaTemp", facturaTemp);
		model.addAttribute("comprobantesFiscales", comprobantesFiscales);
		model.addAttribute("tiposEquipo", tiposEquipo);
		model.addAttribute("taller", new Taller());
		return "facturas/factura";
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
		
		// TODO: GGONZALEZ verificar actualizacion de secuencia en comprobante fiscal

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

		// Creamos la factura
		Factura factura = new Factura();
		factura.setCodigo(tempCode);
		factura.setCliente(facturaTemp.getCliente());
		factura.setComprobanteFiscal(facturaTemp.getComprobanteFiscal());
		// factura.setNcf($prefijo.$secuencia_actual); //validar
		factura.setFecha(new Date());
		// factura.setCondicion(); //validar
		factura.setTotal_venta(total_venta);
		factura.setNombre_cliente(nombreCliente);
		factura.setTelefono_cliente(telefonoCliente);
		factura.setRnc_cliente(rncCliente);
		//factura.setNota_factura(); //validar
		factura.setUsuario(facturaTemp.getUsuario());
		factura.setVendedor(facturaTemp.getVendedor()); //TODO:GGONZALEZ AGREGAR LOGICA
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
			facturaDetalleServicio.setFactura(factura);
			facturasDetallesServiciosService.guardar(facturaDetalleServicio);
		}
		//borramos registros temporales
		
		return "redirect:/";
	}

}
