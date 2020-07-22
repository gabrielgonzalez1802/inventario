package com.developergg.app.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.developergg.app.model.CodigoActivacion;
import com.developergg.app.service.ICodigoActivacionService;

@Controller
public class HomeController {
	
	@Autowired
	private ICodigoActivacionService serviceCodigoActivacion;
	
	@GetMapping("/login")
	public String mostrarLogin() {
		return "login";
	}
	
	@GetMapping("/registro" )
	public String mostrarRegistroEquipos() {
		return "registro";
	}
	
	@PostMapping("/registro")
	public String registrarDispositivo(@RequestParam("codigo") String codigo, Model model,
			RedirectAttributes attributes) {
		//verificamos si el código coincide y esta activo
		CodigoActivacion codigoAct = serviceCodigoActivacion.buscarPorCodigo(codigo);
		if(codigoAct!=null) {
			if(codigoAct.getEstado()==1) {
				model.addAttribute("authority", 1);
				model.addAttribute("code", codigo);
			}
			return "/registro";
		}
		attributes.addFlashAttribute("msg", "Codigo Invalido");
		return "redirect:/registro";
	}
	
	@GetMapping("/")
	public String mostrarRegistro(Model model) {
		return "/home";
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request){
		SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
		logoutHandler.logout(request, null, null);
		return "redirect:/";
	}
	
	@GetMapping("/home")
	public String mostrarHome(Model model) {
		return "home";
	}
	
    @ModelAttribute
    public void setGenericos(Model model) {
    	model.addAttribute("authority", false);
    }

}
