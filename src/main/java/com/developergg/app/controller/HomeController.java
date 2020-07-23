package com.developergg.app.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.CodigoActivacion;
import com.developergg.app.model.Propietario;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IAlmacenesService;
import com.developergg.app.service.ICodigoActivacionService;
import com.developergg.app.service.IPropietariosService;
import com.developergg.app.service.IUsuariosService;

@Controller
public class HomeController {
	
	@Autowired
	private IUsuariosService serviceUsuarios;
	
	@Autowired
	private IAlmacenesService serviceAlmacen;
	
	@Autowired
	private IPropietariosService servicePropietario;
	
	@Autowired
	private ICodigoActivacionService serviceCodigoActivacion;
	
	@GetMapping("/login")
	public String mostrarLogin() {
		return "login";
	}
	
	@GetMapping("/registro" )
	public String mostrarRegistroEquipos() {
		return "registro";
	}
	
	@PostMapping("/registro")
	public String registrarDispositivo(@RequestParam("codigo") String codigo, Model model,
			RedirectAttributes attributes) {
		//verificamos si el código coincide y esta activo
		CodigoActivacion codigoAct = serviceCodigoActivacion.buscarPorCodigo(codigo);
		if(codigoAct!=null) {
			if(codigoAct.getEstado()==1) {
				model.addAttribute("authority", 1);
				model.addAttribute("code", codigo);
			}
			return "/registro";
		}
		attributes.addFlashAttribute("msg", "Codigo Invalido");
		return "redirect:/registro";
	}
	
	@GetMapping("/")
	public String mostrarRegistro(Model model, Authentication auth, HttpSession session) {
		//Usuario logueado
		String userName = auth.getName();
		List<Almacen> almacenes= null;
		Usuario usuario = null;
		System.out.println("username: "+userName);
		if(session.getAttribute("usuario") == null) {
			usuario = serviceUsuarios.buscarPorUsername(userName);
			usuario.setPassword(null);
			System.out.println("Usuario: "+usuario);
			session.setAttribute("usuario", usuario);
		}else {
			usuario = (Usuario) session.getAttribute("usuario");
		}
		
		//Obtenemos la informacion del propietario
		Propietario propietario = servicePropietario.buscarPorIdUsuario(usuario.getId());
		
		//Roles del usuario
		for (GrantedAuthority rol : auth.getAuthorities()) {
			System.out.println("ROL: "+rol.getAuthority());
			//Si el usuario tiene asociado el rol Root o Super Administrador carga todas las sucursales
			if(rol.getAuthority().equalsIgnoreCase("Root") || (rol.getAuthority().equalsIgnoreCase("Super Administrador"))) {
				almacenes = serviceAlmacen.buscarTodos();
			}else {
				almacenes = serviceAlmacen.buscarPorIdTienda(propietario.getId());
			}
		}
		Almacen almacenAcct = serviceAlmacen.buscarPorIdTienda(propietario.getId()).get(0);
		model.addAttribute("usuario", usuario);
		model.addAttribute("perfil", usuario.getPerfiles().get(0).getPerfil());
    	model.addAttribute("almacenAcct", almacenAcct);
		model.addAttribute("almacenes", almacenes);
		return "/home";
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request){
		SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
		logoutHandler.logout(request, null, null);
		return "redirect:/";
	}
	
	@GetMapping("/home")
	public String mostrarHome(Model model) {
		return "home";
	}
	
    @ModelAttribute
    public void setGenericos(Model model) {
    	model.addAttribute("authority", false);
    }

}
