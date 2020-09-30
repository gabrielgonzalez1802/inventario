package com.developergg.app.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.developergg.app.model.Usuario;
import com.developergg.app.model.Vendedor;
import com.developergg.app.service.IVendedoresService;

@Controller
@RequestMapping("/vendedores")
public class VendedoresController {
	
	@Autowired
	private IVendedoresService serviceVendedores;

	@GetMapping("/")
	public String listaVendedores(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Vendedor> vendedores = serviceVendedores.buscarPorAlmacen(usuario.getAlmacen()).
		stream().filter(v -> v.getEliminado() == 0).collect(Collectors.toList());
		model.addAttribute("vendedores", vendedores);
		model.addAttribute("vendedor", new Vendedor());
		return "vendedores/listaVendedores";
	}
	
	@PostMapping("/ajax/agregarVendedor")
	public String agregarVendedor(Model model, HttpSession session, String nombreVendedor,
		String telefonoVendedor, String celularVendedor, String direccionVendedor,
		String cedulaVendedor, String emailVendedor) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Vendedor vendedor = new Vendedor();
		vendedor.setAlmacen(usuario.getAlmacen());
		vendedor.setCedula(cedulaVendedor);
		vendedor.setCelular(celularVendedor);
		vendedor.setCodigo("");
		vendedor.setDireccion(direccionVendedor);
		vendedor.setEmail(emailVendedor);
		vendedor.setNombre(nombreVendedor);
		vendedor.setTelefono(telefonoVendedor);
		serviceVendedores.guardar(vendedor);
		model.addAttribute("vendedorGuardado", vendedor.getId());
		return "vendedores/listaVendedores :: #vendedorGuardado";
	}
	
	@PostMapping("/ajax/modificarVendedor")
	public String modificarVendedor(Model model, HttpSession session, Integer id,
		String nombre, String telefono, String celular, String direccion, 
		String cedula, String email) {
		Vendedor vendedor = serviceVendedores.buscarPorIdVendedor(id);
		vendedor.setCedula(cedula);
		vendedor.setCelular(celular);
		vendedor.setDireccion(direccion);
		vendedor.setEmail(email);
		vendedor.setNombre(nombre);
		vendedor.setTelefono(telefono);
		serviceVendedores.guardar(vendedor);
		model.addAttribute("vendedorGuardado", vendedor.getId());
		return "vendedores/listaVendedores :: #vendedorGuardado";
	}
	
	@GetMapping("/ajax/getVendedor/{id}")
	public String getVendedor(Model model, HttpSession session, @PathVariable(name = "id") Integer id) {
		Vendedor vendedor = serviceVendedores.buscarPorIdVendedor(id);
		if(vendedor == null) {
			vendedor = new Vendedor();
		}
		model.addAttribute("vendedor", vendedor);
		return "vendedores/listaVendedores :: #modificarVendedor";
	}
	
	@GetMapping("/delete/{id}")
	public String eliminarVendedor(HttpSession session, @PathVariable(name = "id") Integer id) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Vendedor vendedor = serviceVendedores.buscarPorIdVendedor(id);
		if(vendedor != null) {
			vendedor.setEliminado(1);
			vendedor.setFecha_eliminado(new Date());
			vendedor.setUsuario(usuario);
			serviceVendedores.guardar(vendedor);
		}
		return "redirect:/vendedores/";
	}
	
}
