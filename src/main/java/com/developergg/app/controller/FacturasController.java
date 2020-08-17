package com.developergg.app.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.developergg.app.model.ComprobanteFiscal;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IComprobantesFiscalesService;
import com.developergg.app.service.IFacturasTempService;

@Controller
@RequestMapping("/facturas")
public class FacturasController {
	
	@Autowired
	private IFacturasTempService serviceFacturasTemp;
	
	@Autowired
	private IComprobantesFiscalesService serviceComprobantesFiscales;

	@GetMapping("/create")
	public String create(HttpSession session, Model model) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//verificamos si existe una factura temporal
		FacturaTemp facturaTemp = serviceFacturasTemp.buscarPorUsuario(usuario);
		List<ComprobanteFiscal> comprobantesFiscales = serviceComprobantesFiscales.buscarPorTienda(usuario.getAlmacen().getPropietario());
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
		model.addAttribute("facturaTemp", facturaTemp);
		model.addAttribute("comprobantesFiscales", comprobantesFiscales);
		return "facturas/factura";
	}
	
}
