package com.developergg.app.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.developergg.app.model.Suplidor;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.ISuplidoresService;

@Controller
@RequestMapping("/suplidores")
public class SuplidoresController {
	
	@Autowired
	private ISuplidoresService serviceSuplidores;
	
	@GetMapping("/")
	public String listaSuplidores(HttpSession session, Model model) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Suplidor> suplidores = serviceSuplidores.buscarPorAlmacenDisponible(usuario.getAlmacen());
		model.addAttribute("suplidores", suplidores);
		return "suplidores/listaSuplidores";
	}
	
	@GetMapping("/create")
	public String crearConSerial(HttpSession session, Model model, Authentication auth) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		model.addAttribute("suplidor", new Suplidor());
		model.addAttribute("usuario", usuario);
		return "suplidores/formulario";
	}
	
	@GetMapping("/edit/{id}")
	public String modificar(@PathVariable("id") Integer idSuplidor, HttpSession session,
			Model model, Authentication auth, RedirectAttributes attributes) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Suplidor suplidor = serviceSuplidores.buscarPorId(idSuplidor);
		//si el suplidor no existe retornara al formulario de crear con un error
		if(suplidor==null) {
			attributes.addFlashAttribute("msg3", "No existe");
			return "redirect:/suplidores/create";
		}
		model.addAttribute("suplidor", suplidor);
		model.addAttribute("usuario", usuario);
		return "suplidores/formulario";
	}
	
	@GetMapping("/delete/{id}")
	public String eliminar(@PathVariable("id") Integer idSuplidor, HttpSession session,
			Model model, Authentication auth, RedirectAttributes attributes) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Suplidor suplidor = serviceSuplidores.buscarPorId(idSuplidor);
		//si el suplidor no existe retornara al formulario de crear con un error
		if(suplidor==null) {
			attributes.addFlashAttribute("msg4", "No existe");
			return "redirect:/suplidores/create";
		}
		suplidor.setEliminado(1);
		suplidor.setUsuarioEliminado(usuario);
		suplidor.setFechaEliminado(new Date());
		serviceSuplidores.guardar(suplidor);
		attributes.addFlashAttribute("msg4", "Suplidor eliminado");
		return "redirect:/suplidores/create";
	}
	
	@PostMapping("/save") //borrado logico
	public String guardar(Suplidor suplidor, HttpSession session, Model model, 
			 RedirectAttributes attributes) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		String method = suplidor.getId()!=null?"UPDATE":"CREATE";
		suplidor.setAlmacen(suplidor.getAlmacen()==null?usuario.getAlmacen():suplidor.getAlmacen());
		serviceSuplidores.guardar(suplidor);
		if(method.equals("UPDATE")) {
			attributes.addFlashAttribute("msg2", "Suplido Modificador");
		}else {
			attributes.addFlashAttribute("msg", "Suplidor creado");
		}
		return "redirect:/suplidores/create";
	}
}
