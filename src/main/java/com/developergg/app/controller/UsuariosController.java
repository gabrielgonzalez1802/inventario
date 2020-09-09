package com.developergg.app.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.developergg.app.model.Perfil;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IAlmacenesService;
import com.developergg.app.service.IPerfilesService;
import com.developergg.app.service.IUsuariosService;

@Controller
@RequestMapping(value = "/usuarios")
public class UsuariosController {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private IUsuariosService serviceUsuarios;
	
	@Autowired
	private IAlmacenesService serviceAlmacenes;
	
	@Autowired
	private IPerfilesService servicePerfiles;
	
	@GetMapping("/")
	public String listaUsuarios(Model model) {
		List<Usuario> usuarios = serviceUsuarios.buscarTodos();
		model.addAttribute("usuarios", usuarios);
		return "usuarios/listaUsuarios";
	}
	
	@GetMapping("/edit/{id}")
	public String formulario(@PathVariable("id") Integer idUsuario, Model model) {
		Usuario usuario = serviceUsuarios.buscarPorId(idUsuario);
		model.addAttribute("almacenes", serviceAlmacenes.buscarTodos());
		model.addAttribute("usuario", usuario);
		return "usuarios/formularioEdit.html";
	}
	
	@GetMapping("/create")
	public String crear(Model model) {
		Usuario usuario = new Usuario();
		List<Perfil> perfiles = servicePerfiles.buscarTodos();
		model.addAttribute("usuario", usuario);
		model.addAttribute("almacenes", serviceAlmacenes.buscarTodos());
		model.addAttribute("perfiles", perfiles);
		return "usuarios/formularioCrear";
	}
	
	@PostMapping("/save")
	public String guardarRegistro(Usuario usuario, RedirectAttributes attributes, Model model) {
		//Verificamos si viene de la opcion de crear usuario y el mismo ya existe
		if(serviceUsuarios.buscarPorUsername(usuario.getUsername())!=null && usuario.getId()==null) {
			attributes.addFlashAttribute("msgExiste", "El registro existe!");
			return "redirect:/usuarios/create";
		}
		Usuario original;
		//update
		if(usuario.getId()!=null) {
			original = serviceUsuarios.buscarPorId(usuario.getId());
			original.setAlmacen(usuario.getAlmacen());
			original.setEstatus(usuario.getEstatus());
			original.setNombre(usuario.getNombre());
			original.setUsername(usuario.getUsername());
		}else {
			//create
			original = usuario;
			String pdwPlano = usuario.getPassword();
			String pwdEncriptado = passwordEncoder.encode(pdwPlano);
			original.setPassword(pwdEncriptado);
			
			//Si encuentra el privilegio setea el por defecto
			if(usuario.getPrivilegio() == null) {
				Perfil perfil = new Perfil();
				perfil.setId(0);
				usuario.setPrivilegio(perfil);
			}
			original.setEstatus(1);
		}
		
		original.setFechaRegistro(new Date());
		serviceUsuarios.guardar(original);
		attributes.addFlashAttribute("msg", "El registro fue guardado correctamente!");
		return "redirect:/usuarios/create";
	}
	
	@GetMapping("/pwd")
	public String cambiarPassword(Model model) {
		return "usuarios/formularioPassword";
	}
	
	@PostMapping("/pwd")
	public String cambiarPassword(HttpSession session, Model model ,@RequestParam("pwd") String pwd, 
			@RequestParam("password") String password) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//Verificamos que coincidan las contraseñas
		if(pwd.equals(password)) {
			String pwdEncriptado = passwordEncoder.encode(password);
			usuario.setPassword(pwdEncriptado);
			serviceUsuarios.guardar(usuario);
			model.addAttribute("msg", "Contraseña modificada");
		}else {
			model.addAttribute("msgError", "Las contraseñas no coinciden");
		}
		return "usuarios/formularioPassword";
	}
	
	@GetMapping("/bcrypt/{texto}")
	@ResponseBody
	public String encriptar(@PathVariable("texto") String texto) {
		return texto + " Encriptado en Bcrypt: "+passwordEncoder.encode(texto);
	}
}
