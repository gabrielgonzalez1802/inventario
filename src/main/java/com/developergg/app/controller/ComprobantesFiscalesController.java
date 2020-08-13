package com.developergg.app.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.developergg.app.model.Comprobante;
import com.developergg.app.model.ComprobanteFiscal;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IComprobantesFiscalesService;
import com.developergg.app.service.IComprobantesService;

@Controller
@RequestMapping("/comprobantesFiscales")
public class ComprobantesFiscalesController {
	
	@Autowired
	private IComprobantesFiscalesService serviceComprobantesFiscales;
	
	@Autowired
	private IComprobantesService serviceComprobantes;
			
	@GetMapping("/")
	public String listaDeComprobantesFiscales(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<ComprobanteFiscal> listaCf = serviceComprobantesFiscales.buscarPorTienda(usuario.getAlmacen().getPropietario());
		model.addAttribute("listaCf", listaCf);
		return "comprobantesFiscales/listaComprobantes";
	}
	
	@GetMapping("/create")
	public String crear(HttpSession session, Model model) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Comprobante> comprobantes = serviceComprobantes.buscarPorTienda(usuario.getAlmacen().getPropietario());
		model.addAttribute("comprobantes", comprobantes);
		model.addAttribute("comprobanteFiscal", new ComprobanteFiscal());
		return "comprobantesFiscales/formulario";
	}
	
	@PostMapping
	public String guardar(ComprobanteFiscal comprobanteFiscal ,HttpSession session, Model model) {
		serviceComprobantesFiscales.guardar(comprobanteFiscal);
		return "";
	}

}
