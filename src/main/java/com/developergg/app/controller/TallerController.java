package com.developergg.app.controller;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.developergg.app.model.Taller;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.ITalleresService;

@Controller
@RequestMapping("/talleres")
public class TallerController {
	
	@Autowired
	private ITalleresService serviceTaller;
	
	@PostMapping("/ajax/addRecepcion/")
	public String addRecepcion(Model model, @ModelAttribute("taller") Taller taller, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		taller.setAlmacen(usuario.getAlmacen());
		taller.setFecha(new Date());
		taller.setUsuario(usuario);
		serviceTaller.guardar(taller);
		model.addAttribute("responseAddRecepcion", taller.getId()!=null?1:0);
		return "facturas/factura :: #responseAddRecepcion";
	}
}
