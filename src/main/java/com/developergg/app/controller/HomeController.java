package com.developergg.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	@GetMapping("/login" )
	public String mostrarLogin() {
		return "login";
	}
	
	@GetMapping("/registro" )
	public String mostrarRegistroEquipos() {
		return "registro";
	}
	
	@GetMapping("/")
	public String mostrarHome(Model model) {
		return "home";
	}

}
