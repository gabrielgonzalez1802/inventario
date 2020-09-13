package com.developergg.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.developergg.app.model.AbonoCxP;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IAbonosCxPService;

@Controller
@RequestMapping("/cxp")
public class AbonosCXPController {
	
	@Autowired
	private IAbonosCxPService serviceAbonosCxP;

	@GetMapping("/")
	public String listaCxP(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<AbonoCxP> listaAbonoCxP = serviceAbonosCxP.buscarPorAlmacen(usuario.getAlmacen()).
				stream().filter(c -> c.getTotalRestante() > 0).collect(Collectors.toList());
		model.addAttribute("listaAbonoCxP", listaAbonoCxP);
		return "cxp/listaCxP";
	}
}
