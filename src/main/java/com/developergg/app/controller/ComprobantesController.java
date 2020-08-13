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
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IComprobantesService;

@Controller
@RequestMapping("/comprobantes")
public class ComprobantesController {
	
	@Autowired
	private IComprobantesService serviceComprobantes;
	
	@GetMapping("/")
	public String listaComprobantes(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Comprobante> listaComprobantes = serviceComprobantes.buscarPorTienda(usuario.getAlmacen().getPropietario());
		model.addAttribute("listaComprobantes", listaComprobantes);
		return "comprobantes/listaComprobantes";
	}
	
	@GetMapping("/create")
	public String crear(HttpSession session, Model model) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Comprobante comprobante = new Comprobante();
		comprobante.setPropietario(usuario.getAlmacen().getPropietario());
		model.addAttribute("comprobante", comprobante);
		return "comprobantes/formulario";
	}
	
	@GetMapping("/edit/{id}")
	public String modificar(HttpSession session, Model model, @PathVariable("id") Integer id,
			RedirectAttributes attributes) {
		Comprobante comprobante = serviceComprobantes.buscarPorId(id);
		if(comprobante==null) {
			attributes.addFlashAttribute("msg4", "No existe");
			return "redirect:/comprobantes/";
		}
		model.addAttribute("comprobante", comprobante);
		return "comprobantes/formulario";
	}
	
	@PostMapping("/save")
	public String guardar(Comprobante comprobante, Model model, HttpSession session, RedirectAttributes attributes) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		String option = "CREATE";
		if(comprobante.getId()!=null) {
			option="UPDATE";
		}
		comprobante.setPropietario(usuario.getAlmacen().getPropietario());
		serviceComprobantes.guardar(comprobante);
		if(option.equals("CREATE")) {
			attributes.addFlashAttribute("msg", "Registro guardado");
		}else {
			attributes.addFlashAttribute("msg2", "Registro modificado");
		}
		return "redirect:/comprobantes/";
	}
	
	@GetMapping("/delete/{id}")
	public String eliminar(@PathVariable("id") Integer id, RedirectAttributes attributes) {
		serviceComprobantes.eliminar(id);
		attributes.addFlashAttribute("msg3", "Registro modificado");
		return "redirect:/comprobantes/";
	}
}
