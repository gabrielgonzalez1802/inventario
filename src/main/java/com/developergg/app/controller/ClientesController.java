package com.developergg.app.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.developergg.app.model.Cliente;
import com.developergg.app.model.Usuario;
import com.developergg.app.model.Vendedor;
import com.developergg.app.service.IClientesService;
import com.developergg.app.service.IVendedoresService;

@Controller
@RequestMapping("/clientes")
public class ClientesController {

	@Autowired
	private IClientesService serviceClientes;
	
	@Autowired
	private IVendedoresService serviceVendedores;
	
	@GetMapping("/")
	public String listaClientes(HttpSession session, Model model) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//Buscamos los clientes que pertenezcan al almacen del usuario y no esten eliminados
		List<Cliente> clientes = serviceClientes.buscarPorAlmacen(usuario.getAlmacen()).stream()
				.filter(c -> c.getEliminado() == 0).collect(Collectors.toList());
		model.addAttribute("clientes", clientes);
		return "clientes/listaClientes";
	}
	
	@GetMapping("/create")
	public String crear(HttpSession session, Model model, Authentication auth) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Vendedor> vendedores = serviceVendedores.buscarPorAlmacen(usuario.getAlmacen());
		Cliente cliente = new Cliente();
		Integer code = 0;
		List<Cliente> allClientes = serviceClientes.buscarPorAlmacen(usuario.getAlmacen());
		if(!allClientes.isEmpty()) {
			Cliente clienteTemp = allClientes.get(allClientes.size()-1);
			code=Integer.parseInt(clienteTemp.getCodigo())+1;
			cliente.setCodigo(code.toString());
		}else {
			cliente.setCodigo("1");
		}
		cliente.setAlmacen(usuario.getAlmacen());
		model.addAttribute("cliente", cliente);
		model.addAttribute("vendedores", vendedores);
		return "clientes/formulario";
	}
	
	@GetMapping("/edit/{id}")
	public String modificar(@PathVariable("id") Integer idCliente, HttpSession session,
			Model model, Authentication auth, RedirectAttributes attributes) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Cliente cliente = serviceClientes.buscarPorIdCliente(idCliente);
		//si el cliente no existe retornara al formulario de crear con un error
		if(cliente==null) {
			attributes.addFlashAttribute("msg3", "No existe");
			return "redirect:/clientes/create";
		}
		List<Vendedor> vendedores = serviceVendedores.buscarPorAlmacen(usuario.getAlmacen());
		model.addAttribute("cliente", cliente);
		model.addAttribute("vendedores", vendedores);
		return "clientes/formulario";
	}
	
	@PostMapping("/save") //borrado logico
	public String guardar(Cliente cliente, HttpSession session, Model model, 
			 RedirectAttributes attributes) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		String method = cliente.getId()!=null?"UPDATE":"CREATE";
		cliente.setAlmacen(usuario.getAlmacen());
		serviceClientes.guardar(cliente);
		if(method.equals("UPDATE")) {
			attributes.addFlashAttribute("msg2", "Cliente Modificador");
		}else {
			attributes.addFlashAttribute("msg", "Cliente creado");
		}
		serviceClientes.guardar(cliente);
		return "redirect:/clientes/";
	}
	
	@GetMapping("/delete/{id}")
	public String eliminar(@PathVariable("id") Integer idCliente, HttpSession session,
			Model model, Authentication auth, RedirectAttributes attributes) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Cliente cliente = serviceClientes.buscarPorIdCliente(idCliente);
		//si el cliente no existe retornara al formulario de crear con un error
		if(cliente==null) {
			attributes.addFlashAttribute("msg3", "No existe");
			return "redirect:/clientes/create";
		}
		cliente.setEliminado(1);
		cliente.setUsuario(usuario);
		cliente.setFecha_eliminado(new Date());
		serviceClientes.guardar(cliente);
		attributes.addFlashAttribute("msg4", "Cliente eliminado");
		return "redirect:/clientes/";
	}
	
}
