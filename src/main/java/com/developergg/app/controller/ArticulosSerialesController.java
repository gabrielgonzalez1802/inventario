package com.developergg.app.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
import com.developergg.app.model.ArticuloSerial;
import com.developergg.app.model.ArticuloSerialTemp;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IArticulosSeriales;
import com.developergg.app.service.IArticulosSerialesTemp;
import com.developergg.app.service.IArticulosService;

@Controller
@RequestMapping("/articulosSeriales")
public class ArticulosSerialesController {
	
	@Autowired
	private IArticulosSeriales serviceArticulosSeriales;
	
	@Autowired
	private IArticulosSerialesTemp serviceArticulosSerialesTemp;

	@Autowired
	private IArticulosService serviceArticulos;
	
	@GetMapping("/ajax/getLastTemp/{id}")
	public String listaUltimosSerialesTemp(Model model, HttpSession session, @PathVariable(name = "id") Integer id) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Articulo articulo = serviceArticulos.buscarPorId(id);
		List<ArticuloSerialTemp> lista = serviceArticulosSerialesTemp.buscarPorArticuloAlmacen(articulo, usuario.getAlmacen());
		model.addAttribute("existe", 0);
		model.addAttribute("listaUltimosSeriales", lista);
		return "articulos/formularioSerial :: #infoListaSeriales";
	}
	
	@GetMapping("/ajax/serialTemp")
	public String listaSerialTempAjax(Model model, @ModelAttribute("articuloSerial") ArticuloSerialTemp articuloSerialTemp, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<ArticuloSerialTemp> lista = serviceArticulosSerialesTemp
				.buscarPorIdAlmacenDesc(usuario.getAlmacen().getId())
				.stream().filter(s -> s.getEliminado() == 0).
		collect(Collectors.toList());
		int existe = 0;
		//Verificamos si el serial existe
		List<ArticuloSerial> serial = serviceArticulosSeriales.buscarPorSerialAndAlmacen(articuloSerialTemp.getSerial(), usuario.getAlmacen());
		if(lista.stream().
		filter(s -> s.getSerial().equalsIgnoreCase(articuloSerialTemp.getSerial())).
		collect(Collectors.toList()).size() == 0 && serial.size() == 0) {
//			ArticuloSerial articuloSerial = new ArticuloSerial();
			articuloSerialTemp.setAlmacen(usuario.getAlmacen());
			articuloSerialTemp.setFecha(new Date());
			articuloSerialTemp.setId_usuario(usuario.getId());
			serviceArticulosSerialesTemp.guardar(articuloSerialTemp);
			lista.add(articuloSerialTemp);
		}else {
			//serial existe
			existe = 1;
		}
		model.addAttribute("existe", existe);
		model.addAttribute("listaUltimosSeriales", lista);
		return "articulos/formularioSerial :: #infoListaSeriales";
	}

	@GetMapping("/ajax/serialTempDelete/{id}")
	public String borrarSerialesTemporalesAjax(Model model, @PathVariable("id") Integer id, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		//verificamos is el serial existe
		ArticuloSerialTemp articuloSerialTemp = serviceArticulosSerialesTemp.buscarPorIdArticuloSerialTemp(id);
		serviceArticulosSerialesTemp.eliminar(articuloSerialTemp);
		List<ArticuloSerialTemp> lista = serviceArticulosSerialesTemp
				.buscarPorIdAlmacenDesc(usuario.getAlmacen().getId())
				.stream().filter(s -> s.getEliminado() == 0).
		collect(Collectors.toList());
		model.addAttribute("listaUltimosSeriales", lista);
		return "articulos/formularioSerial ::  #infoListaSeriales";
	}
	
	@GetMapping("/ajax/allSerialTempDelete/{id}")
	public String borrarTodosLosSerialesTemporalesAjax(Model model, @PathVariable("id") Integer id, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Articulo articulo = serviceArticulos.buscarPorId(id);
		List<ArticuloSerialTemp> serialesTemp = serviceArticulosSerialesTemp.buscarPorArticuloAlmacen(articulo, usuario.getAlmacen());
		serviceArticulosSerialesTemp.eliminar(serialesTemp);
		List<ArticuloSerialTemp> lista = new LinkedList<>();
		model.addAttribute("listaUltimosSeriales", lista);
		return "articulos/formularioSerial ::  #infoListaSeriales";
	}
	
	@PostMapping("/guardarSeriales/{id}")
	public String guardarSeriales(@PathVariable("id") Integer id, HttpSession session, Model model) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Articulo articulo = serviceArticulos.buscarPorId(id);
		List<ArticuloSerialTemp> lista = serviceArticulosSerialesTemp.buscarPorArticuloAlmacen(articulo, usuario.getAlmacen());
		for (ArticuloSerialTemp articuloSerialTemp : lista) {
			ArticuloSerial articuloSerial = new ArticuloSerial();
			articuloSerial.setAlmacen(usuario.getAlmacen());
			articuloSerial.setArticulo(articuloSerialTemp.getArticulo());
			articuloSerial.setCosto(articuloSerialTemp.getCosto());
			articuloSerial.setEstado(articuloSerialTemp.getEstado());
			articuloSerial.setFecha(new Date());
			articuloSerial.setSuplidor(articuloSerialTemp.getSuplidor());
			articuloSerial.setId_usuario(usuario.getId());
			articuloSerial.setNo_factura(articuloSerialTemp.getNo_factura());
			articuloSerial.setPrecio_maximo(articuloSerialTemp.getPrecio_maximo());
			articuloSerial.setPrecio_minimo(articuloSerialTemp.getPrecio_minimo());
			articuloSerial.setPrecio_mayor(articuloSerialTemp.getPrecio_mayor());
			articuloSerial.setSerial(articuloSerialTemp.getSerial());
			serviceArticulosSeriales.guardar(articuloSerial);
		}
		
		//Borramos los registros temporales
		serviceArticulosSerialesTemp.eliminar(lista);
		return "articulos/formularioSerial ::  #responseAddSerials";
	}
}
