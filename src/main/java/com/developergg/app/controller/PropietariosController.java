package com.developergg.app.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.developergg.app.model.Propietario;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IPropietariosService;

@Controller
@RequestMapping(value = "/propietarios")
public class PropietariosController {
	
	@Autowired
	private IPropietariosService servicePropietarios;
	
	@GetMapping("/")
	public String listaPropietarios(Model model) {
		List<Propietario> lista = servicePropietarios.buscarTodos();
		model.addAttribute("propietarios", lista);
		return "propietarios/listaPropietarios";
	}
	
	@GetMapping("/create")
	public String crear(HttpSession session, Model model) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		model.addAttribute("usuario", usuario);
		return "propietarios/formularioCrear";
	}
	
	@GetMapping("/edit/{id}")
	public String modificar(@PathVariable("id") Integer idPropietario,HttpSession session, Model model) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Propietario propietario = servicePropietarios.buscarPorId(idPropietario);
		model.addAttribute("usuario", usuario);
		model.addAttribute("propietario", propietario);
		return "propietarios/formularioEdit";
	}
	
	@GetMapping("/delete/{id}")
	public String eliminar(@PathVariable("id") Integer idPropietario) {
		Propietario propietario = servicePropietarios.buscarPorId(idPropietario);
		if(propietario==null) {
			return "redirect:/propietarios/create";
		}
		servicePropietarios.eliminar(idPropietario);
		return "redirect:/propietarios/";
	}
	
	@PostMapping("/save")
	public String guardar(HttpSession session, Model model, Propietario propietario) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		String option = "CREATE";
		if(propietario.getId() != null) {
			option = "UDPATE";
		}
		propietario.setFecha(new Date());
		propietario.setUsuario(usuario);
		servicePropietarios.guardar(propietario);
		if(option.equals("UPDATE")) {
			model.addAttribute("msg2", "Propietario modificado");
		}
		return "redirect:/propietarios/";
	}
	
}
