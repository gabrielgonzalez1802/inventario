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

import com.developergg.app.model.Gasto;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IGastosService;

@Controller
@RequestMapping("/gastos")
public class GastosController {
	
	@Autowired
	private IGastosService serviceGastos;
	
	@GetMapping("/")
	public String listaGastos(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Gasto> listaGastos = serviceGastos.buscarPorAlmacen(usuario.getAlmacen());
		model.addAttribute("listaGastos", listaGastos);
		model.addAttribute("gasto", new Gasto());
		return "gastos/listaGastos";
	}
	
	@PostMapping("/ajax/agregarGasto")
	public String agregarGastos(Model model, HttpSession session, @RequestParam(name = "nombreGasto") String nombreGasto,
			@RequestParam(name = "tipoGasto") String tipoGasto, @RequestParam(name = "montoGasto") Double montoGasto,
			@RequestParam(name = "decripcionGasto") String decripcionGasto) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
	    String now = LocalDateTime.now().format(formatter);
		Gasto gasto = new Gasto();
		gasto.setUsuario(usuario);
		gasto.setAlmacen(usuario.getAlmacen());
		gasto.setDescripcion(decripcionGasto);
		gasto.setFecha(new Date());
		gasto.setMonto(montoGasto);
		gasto.setNombre(nombreGasto);
		gasto.setTipo_gasto(tipoGasto);
		gasto.setHora(now);
		serviceGastos.guardar(gasto);
		model.addAttribute("gastoGuardado", gasto.getId());
		return "gastos/listaGastos :: #gastoGuardado";
	}
	
	@GetMapping("/delete/{idGasto}")
	public String borrarGasto(@PathVariable("idGasto") Integer idGasto) {
		serviceGastos.eliminar(idGasto);
		return "redirect:/gastos/";
	}
	
	@PostMapping("/ajax/modificarGasto")
	public String modificarGastos(Model model, HttpSession session, @RequestParam(name = "idGasto") Integer idGasto,
			@RequestParam(name = "nombreGasto") String nombreGasto, @RequestParam(name = "tipoGasto") String tipoGasto, @RequestParam(name = "montoGasto") Double montoGasto,
			@RequestParam(name = "decripcionGasto") String decripcionGasto) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
	    String now = LocalDateTime.now().format(formatter);
		Gasto gasto = serviceGastos.buscarPorId(idGasto);
		gasto.setDescripcion(decripcionGasto);
		gasto.setFecha(new Date());
		gasto.setMonto(montoGasto);
		gasto.setNombre(nombreGasto);
		gasto.setTipo_gasto(tipoGasto);
		gasto.setHora(now);
		serviceGastos.guardar(gasto);
		model.addAttribute("gastoGuardado", gasto.getId());
		return "gastos/listaGastos :: #gastoGuardado";
	}
	
	@GetMapping("/ajax/getGasto/{idGasto}")
	public String getGasto(Model model, @PathVariable(name = "idGasto") Integer idGasto) {
		Gasto gasto = serviceGastos.buscarPorId(idGasto);
		if(gasto==null) {
			gasto = new Gasto();
		}
		model.addAttribute("gasto", gasto);
		return "gastos/listaGastos :: #modificarGasto";
	}
	
}
