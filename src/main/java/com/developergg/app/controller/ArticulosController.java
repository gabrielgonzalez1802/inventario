package com.developergg.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.developergg.app.model.Articulo;
import com.developergg.app.service.IArticulosService;

@Controller
@RequestMapping(value = "/articulos")
public class ArticulosController {
	
	@Autowired
	IArticulosService serviceArticulos;
	
	@GetMapping("/")
	public String mostrarArticulos(Model model) {
		List<Articulo> lista = serviceArticulos.buscarTodos();
		model.addAttribute("articulos", lista);
		return "articulos/listaArticulos";
	}
}
