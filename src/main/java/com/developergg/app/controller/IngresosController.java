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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.developergg.app.model.Ingreso;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IIngresosService;

@Controller
@RequestMapping("/ingresos")
public class IngresosController {
	
	@Autowired
	private IIngresosService serviceIngresos;
	
	@GetMapping("/")
	public String listaIngresos(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Ingreso> listaIngresos = serviceIngresos.buscarPorAlmacen(usuario.getAlmacen());
		model.addAttribute("listaIngresos", listaIngresos);
		model.addAttribute("ingreso", new Ingreso());
		return "ingresos/listaIngresos";
	}
	
	@PostMapping("/ajax/agregarIngreso")
	public String agregarIngreso(Model model, HttpSession session, @RequestParam(name = "nombreIngreso") String nombreIngreso,
			@RequestParam(name = "tipoIngreso") String tipoIngreso, @RequestParam(name = "montoIngreso") Double montoIngreso,
			@RequestParam(name = "decripcionIngreso") String decripcionIngreso) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
	    String now = LocalDateTime.now().format(formatter);
		Ingreso ingreso = new Ingreso();
		ingreso.setUsuario(usuario);
		ingreso.setAlmacen(usuario.getAlmacen());
		ingreso.setDescripcion(decripcionIngreso);
		ingreso.setFecha(new Date());
		ingreso.setMonto(montoIngreso);
		ingreso.setNombre(nombreIngreso);
		ingreso.setTipo_ingreso(tipoIngreso);
		ingreso.setHora(now);
		serviceIngresos.guardar(ingreso);
		model.addAttribute("ingresoGuardado", ingreso.getId());
		return "ingresos/listaIngresos :: #ingresoGuardado";
	}
	
	@GetMapping("/delete/{idIngreso}")
	public String borrarIngreso(@PathVariable("idIngreso") Integer idIngreso) {
		serviceIngresos.eliminar(idIngreso);
		return "redirect:/ingresos/";
	}
	
	@PostMapping("/ajax/modificarIngreso")
	public String modificarIngresos(Model model, HttpSession session, @RequestParam(name = "idIngreso") Integer idIngreso,
			@RequestParam(name = "nombreIngreso") String nombreIngreso, @RequestParam(name = "tipoIngreso") String tipoIngreso, 
			@RequestParam(name = "montoIngreso") Double montoIngreso, @RequestParam(name = "decripcionIngreso") String decripcionIngreso) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
	    String now = LocalDateTime.now().format(formatter);
		Ingreso ingreso = serviceIngresos.buscarPorId(idIngreso);
		ingreso.setDescripcion(decripcionIngreso);
		ingreso.setFecha(new Date());
		ingreso.setMonto(montoIngreso);
		ingreso.setNombre(nombreIngreso);
		ingreso.setTipo_ingreso(tipoIngreso);
		ingreso.setHora(now);
		serviceIngresos.guardar(ingreso);
		model.addAttribute("ingresoGuardado", ingreso.getId());
		return "ingresos/listaIngresos :: #ingresoGuardado";
	}
	
	@GetMapping("/ajax/getIngreso/{idIngreso}")
	public String getIngreso(Model model, @PathVariable(name = "idIngreso") Integer idIngreso) {
		Ingreso ingreso = serviceIngresos.buscarPorId(idIngreso);
		if(ingreso==null) {
			ingreso = new Ingreso();
		}
		model.addAttribute("ingreso", ingreso);
		return "ingresos/listaIngresos :: #modificarIngreso";
	}
	
}
