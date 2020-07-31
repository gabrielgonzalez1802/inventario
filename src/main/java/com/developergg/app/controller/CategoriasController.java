package com.developergg.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.developergg.app.model.Categoria;
import com.developergg.app.model.Propietario;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.ICategoriasService;

@Controller
@RequestMapping("/categorias")
public class CategoriasController {
	
	@Autowired
	private ICategoriasService serviceCategorias;
	
	@GetMapping("/ajax/create/{categoria}")
	public String listaCategoriasAjax(Model model, @PathVariable("categoria") String categoria, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Propietario tienda = usuario.getAlmacen().getPropietario();
		List<Categoria> listaCategorias = serviceCategorias.buscarPorPropietario(tienda);
		//Verificamos si la categoria existe
		if(listaCategorias.stream().
		filter(c -> c.getNombre().equalsIgnoreCase(categoria)).
		collect(Collectors.toList()).size() == 0) {
			Categoria cat = new Categoria();
			cat.setNombre(categoria);
			cat.setTienda(tienda);
			serviceCategorias.guardar(cat);
			listaCategorias.add(cat);
		}else {
			//categoria existe
		}
		model.addAttribute("listaCategorias", listaCategorias);
        return "articulos/formulario :: listaCategorias";
	}
	
	@GetMapping("/ajax/getAll")
	public String listaTodasCategoriasAjax(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Propietario tienda = usuario.getAlmacen().getPropietario();
		List<Categoria> listaCategorias = serviceCategorias.buscarPorPropietario(tienda);
		model.addAttribute("listaCategorias", listaCategorias);
        return "articulos/formulario :: listaCategorias";
	}
}
