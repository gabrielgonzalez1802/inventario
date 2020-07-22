package com.developergg.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	
	@PostMapping("/registro")
	public void registrarDispositivo(@RequestParam("codigo") String codigo) {
		
	}
	
	@GetMapping("/")
	public String mostrarHome(Model model) {
		return "home";
	}
	


}
