package com.developergg.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.developergg.app.model.Cliente;
import com.developergg.app.model.ComprobanteFiscal;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.Taller;
import com.developergg.app.model.TipoEquipo;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IClientesService;
import com.developergg.app.service.IComprobantesFiscalesService;
import com.developergg.app.service.IFacturasTempService;
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
		ComprobanteFiscal comprobanteFiscal = null;
		for (ComprobanteFiscal comprobanteF : comprobantesFiscales) {
			if(comprobanteF.getNombre().equalsIgnoreCase("Consumidor final")) {
				comprobanteFiscal = comprobanteF;
				break;
			}
		}
		if(facturaTemp==null) {
			facturaTemp = new FacturaTemp();
			facturaTemp.setUsuario(usuario);
			facturaTemp.setComprobanteFiscal(comprobanteFiscal);
			serviceFacturasTemp.guardar(facturaTemp);
		}
		model.addAttribute("clientes", clientes);
		model.addAttribute("facturaTemp", facturaTemp);
		model.addAttribute("comprobantesFiscales", comprobantesFiscales);
		model.addAttribute("tiposEquipo", tiposEquipo);
		model.addAttribute("taller", new Taller());
		return "facturas/factura";
	}
	
}
