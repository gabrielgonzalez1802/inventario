package com.developergg.app.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.developergg.app.model.Articulo;
import com.developergg.app.model.Taller;
import com.developergg.app.model.TallerArticulo;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.ITalleresArticulosService;

@Controller
@RequestMapping("/tallerArticulos")
public class TallerArticulosController {
	
	@Autowired
	private ITalleresArticulosService serviceTalleresArticulos;
	
	@GetMapping("/")
	public String listaTallerArticulos(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		TallerArticulo tallerArticulo = new TallerArticulo();
		List<TallerArticulo> listaTalleresArticulos = serviceTalleresArticulos.buscarPorAlmacen(usuario.getAlmacen());
		if(!listaTalleresArticulos.isEmpty()) {
			for (TallerArticulo item : listaTalleresArticulos) {
				if(item.getArticulo() == null) {
					item.setArticulo(new Articulo());
				}
				if(item.getTaller() == null) {
					item.setTaller(new Taller());
				}
			}
		}
		model.addAttribute("listaTalleresArticulos", listaTalleresArticulos);
		model.addAttribute("tallerArticulo", tallerArticulo);
		return "talleres/listaArticulos";
	}
	
	@PostMapping("/ajax/addTallerArticulo/")
	public String addTaller(Model model, @ModelAttribute("tallerArticulo") TallerArticulo tallerArticulo, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
	    String now = LocalDateTime.now().format(formatter);
		tallerArticulo.setFecha(new Date());
		tallerArticulo.setHora(now);
		tallerArticulo.setUsuario(usuario);
		tallerArticulo.setAlmacen(usuario.getAlmacen());
		serviceTalleresArticulos.guardar(tallerArticulo);
		model.addAttribute("responseAddRecepcion", tallerArticulo.getId()!=null?1:0);
		return "talleres/listaArticulos :: #responseAddRecepcion";
	}
	
	@GetMapping("/ajax/getInfo/{tallerArticuloId}")
	public String getIngreso(Model model, @PathVariable(name = "tallerArticuloId") Integer tallerArticuloId) {
		TallerArticulo item = serviceTalleresArticulos.buscarPorId(tallerArticuloId);
		if(item==null) {
			item = new TallerArticulo();
			model.addAttribute("disponible", 0);
			model.addAttribute("precioArticulo", 0);
			model.addAttribute("costoArticulo", 0);
			model.addAttribute("idArticulo", 0);
		}else {
			model.addAttribute("disponible", item.getCantidad());
			model.addAttribute("precioArticulo", item.getPrecio());
			if(item.getArticulo()==null) {
				model.addAttribute("costoArticulo", 0);
				model.addAttribute("idArticulo", 0);
			}else {
				model.addAttribute("costoArticulo", item.getArticulo().getCosto());
				model.addAttribute("idArticulo", item.getArticulo().getId());
			}
		}
		return "talleres/taller :: #infoSelection";
	}
}
