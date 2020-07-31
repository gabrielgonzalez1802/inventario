package com.developergg.app.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.developergg.app.model.ArticuloSerial;
import com.developergg.app.model.Propietario;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IArticulosSeriales;

@Controller
@RequestMapping("/articulosSeriales")
public class ArticulosSerialesController {
	
	@Autowired
	private IArticulosSeriales serviceArticulosSeriales;

	
	@GetMapping("/ajax/getLast")
	public String listaUltimosSeriales(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//Buscar los 5 ultimos seriales que no esten eliminados ordenandolos por fecha descendente
		List<ArticuloSerial> lista = serviceArticulosSeriales
				.buscarPorIdAlmacenDesc(usuario.getAlmacen().getId())
				.stream().filter(s -> s.getEliminado() == 0).
		collect(Collectors.toList());
		//limitamos los ultimos 5 en caso de que exista mas objetos en la lista
		if(lista.size() > 4) {
			lista = lista.subList(0, 5);
		}
		model.addAttribute("listaUltimosSeriales", lista);
		return "articulos/formularioSerial :: listaUltimosSeriales";
	}
	
	@GetMapping("/ajax/serial")
	public String listaCategoriasAjax(Model model, @ModelAttribute("articuloSerial") ArticuloSerial articuloSerial, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Propietario tienda = usuario.getAlmacen().getPropietario();
		List<ArticuloSerial> lista = serviceArticulosSeriales
				.buscarPorIdAlmacenDesc(usuario.getAlmacen().getId())
				.stream().filter(s -> s.getEliminado() == 0).
		collect(Collectors.toList());
		//Verificamos si el serial existe
		if(lista.stream().
		filter(s -> s.getSerial().equalsIgnoreCase(articuloSerial.getSerial())).
		collect(Collectors.toList()).size() == 0) {
//			ArticuloSerial articuloSerial = new ArticuloSerial();
			articuloSerial.setAlmacen(usuario.getAlmacen());
			articuloSerial.setFecha(new Date());
			articuloSerial.setId_usuario(usuario.getId());
			serviceArticulosSeriales.guardar(articuloSerial);
			lista.add(articuloSerial);
		}else {
			//serial existe
		}
		model.addAttribute("listaUltimosSeriales", lista);
		return "articulos/formularioSerial :: listaUltimosSeriales";
	}
	
	@GetMapping("/ajax/serialDelete/{id}")
	public String listaCategoriasAjax(Model model, @PathVariable("id") Integer id, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		//verificamos is el serial existe
		ArticuloSerial articuloSerial = serviceArticulosSeriales.buscarPorIdArticuloSerial(id);
		if(articuloSerial==null || articuloSerial.getEliminado() == 1) {
			//ya esta eliminado no se puede eliminar
		}else {
			articuloSerial.setEliminado(1);
			articuloSerial.setId_usuario_eliminar(usuario.getId());
			articuloSerial.setFecha_eliminado(new Date());
			serviceArticulosSeriales.guardar(articuloSerial);
		}
		List<ArticuloSerial> lista = serviceArticulosSeriales
				.buscarPorIdAlmacenDesc(usuario.getAlmacen().getId())
				.stream().filter(s -> s.getEliminado() == 0).
		collect(Collectors.toList());
		model.addAttribute("listaUltimosSeriales", lista);
		return "articulos/formularioSerial :: listaUltimosSeriales";
	}
}
