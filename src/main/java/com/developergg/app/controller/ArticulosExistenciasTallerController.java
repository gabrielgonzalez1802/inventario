package com.developergg.app.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.developergg.app.model.ArticuloExistenciaTaller;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IArticulosExistenciasTallerService;

@Controller
@RequestMapping(value = "/articulosTaller")
public class ArticulosExistenciasTallerController {
	
	@Autowired
	IArticulosExistenciasTallerService serviceArticulosTaller;
	
	public String listaArticulosTaller(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<ArticuloExistenciaTaller> listaArticulosTaller = serviceArticulosTaller.
					buscarPorAlmacen(usuario.getAlmacen());
		model.addAttribute("listaArticulosTaller", listaArticulosTaller);
		return "taller/lista";
	}

}
