package com.developergg.app.controller;

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

import com.developergg.app.model.Cliente;
import com.developergg.app.model.ComprobanteFiscal;
import com.developergg.app.model.CondicionPago;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.FormaPago;
import com.developergg.app.model.Taller;
import com.developergg.app.model.TipoEquipo;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IClientesService;
import com.developergg.app.service.IComprobantesFiscalesService;
import com.developergg.app.service.ICondicionesPagoService;
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

	@GetMapping("/create")
	public String create(HttpSession session, Model model) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//verificamos si existe una factura temporal
		FacturaTemp facturaTemp = serviceFacturasTemp.buscarPorUsuario(usuario);
		List<ComprobanteFiscal> comprobantesFiscales = serviceComprobantesFiscales.buscarPorTienda(usuario.getAlmacen().getPropietario());
		List<TipoEquipo> tiposEquipo = serviceTiposEquipos.buscarTodos();
		//Clientes que no esten eliminados y pertenezcan al almacen
		List<Cliente> clientes = serviceClientes.buscarPorAlmacen(usuario.getAlmacen()).
				stream().filter(c -> c.getEliminado() == 0).
				collect(Collectors.toList());
		List<FormaPago> formaPagos = serviceFormasPagos.buscarPorAlmacen(usuario.getAlmacen());
		List<CondicionPago> condicionesPago = serviceCondicionesPago.buscarTodos();
		ComprobanteFiscal comprobanteFiscal = null;
		for (ComprobanteFiscal comprobanteF : comprobantesFiscales) {
			if(comprobanteF.getNombre().equalsIgnoreCase("Consumidor final")) {
				comprobanteFiscal = comprobanteF;
				break;
			}
		}
		if(facturaTemp==null) {
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
	public String guardarFactura(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaTemp facturaTemp = serviceFacturasTemp.buscarPorUsuario(usuario);
		
		//TODO: GGONZALEZ verificar actualizacion de secuencia en comprobante fiscal
		
		//validacion para condicion de pago contado
		if(facturaTemp.getCondicionPago().getNombre().equalsIgnoreCase("contado")) {
			
		}
		
		//$sql="INSERT INTO  facturas (codigo,id_cliente,id_comprobante,ncf,fecha,hora,condicion,total_venta,nombre_cliente,telefono_cliente,rnc_cliente,nota_factura,id_usuario,id_vendedor,id_almacen,abono,credito,cuotas,id_taller,forma_pago,avance_taller,total_itbis)
		//VALUES('$codigo_factura','$id_cliente','$id_comprobante','$ncf','$fecha','$hora','$condicion','$total_venta','$nombre_cliente','$telefono_cliente','$rnc_cliente','$nota_factura','$id_usuario','$id_vendedor','$id_almacen','$abono','$credito','$cantidad_cuotas','$id_taller_factura','$forma_pago','$avance_taller','$total_itbis_factura');";
		//$consulta= mysql_query($sql);
		
		
		
		return "facturas/factura :: #responseUpdateCondicion";
	}
	
}
