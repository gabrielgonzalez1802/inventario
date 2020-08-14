package com.developergg.app.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	
	@GetMapping("/edit/{id}")
	public String crear(HttpSession session, Model model, @PathVariable("id") Integer idComprobanteFiscal,
			RedirectAttributes attributes) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		ComprobanteFiscal comprobanteFiscal = serviceComprobantesFiscales.buscarPorId(idComprobanteFiscal);
		if(comprobanteFiscal==null) {
			attributes.addFlashAttribute("msg4","NO EXISTE");
			return "redirect:/comprobantesFiscales/";
		}
		List<Comprobante> comprobantes = serviceComprobantes.buscarPorTienda(usuario.getAlmacen().getPropietario());
		model.addAttribute("comprobantes", comprobantes);
		model.addAttribute("comprobanteFiscal", comprobanteFiscal);
		return "comprobantesFiscales/formulario";
	}
	
	@GetMapping("/delete/{id}")
	public String eliminar(@PathVariable("id") Integer idComprobanteFiscal, RedirectAttributes attributes) {
		ComprobanteFiscal comprobanteFiscal = serviceComprobantesFiscales.buscarPorId(idComprobanteFiscal);
		if(comprobanteFiscal==null) {
			attributes.addFlashAttribute("msg4","NO EXISTE");
			return "redirect:/comprobantesFiscales/";
		}
		serviceComprobantesFiscales.eliminar(idComprobanteFiscal);
		attributes.addFlashAttribute("msg3","Borrado");
		return "redirect:/comprobantesFiscales/";
	}
	
	@PostMapping("/save")
	public String guardar(ComprobanteFiscal comprobanteFiscal, HttpSession session, RedirectAttributes attributes) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		String option="CREATE";
		if(comprobanteFiscal.getId()!=null) {
			option="UPDATE";
		}
		comprobanteFiscal.setTienda(usuario.getAlmacen().getPropietario());
		serviceComprobantesFiscales.guardar(comprobanteFiscal);
		if(option.equals("CREATE")) {
			attributes.addFlashAttribute("msg", "Creado");
		}else {
			attributes.addFlashAttribute("msg2", "Modificado");
		}
		return "redirect:/comprobantesFiscales/";
	}

}
