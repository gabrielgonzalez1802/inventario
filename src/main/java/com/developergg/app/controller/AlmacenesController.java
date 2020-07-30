package com.developergg.app.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Propietario;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IAlmacenesService;
import com.developergg.app.service.IPropietariosService;
import com.developergg.app.util.Utileria;

@Controller
@RequestMapping("/almacenes")
public class AlmacenesController {
	
	@Autowired
	private IAlmacenesService serviceAlmacen;
	
	@Autowired
	private IPropietariosService servicePropietarios;
	
	@Value("${inventario.ruta.imagenes}")
	private String ruta;
	
	@GetMapping("/")
	public String listaAlmacenes(HttpSession session, Authentication auth, Model model) {
		List<Almacen> almacenes= null;
		//lista de almacenes por tienda, si es super usuario mostrar todas y Si el usuario no tiene rol asignado buscara por propietario
		almacenes = serviceAlmacen.buscarTodos();
		model.addAttribute("almacenes", almacenes);
		return "almacenes/listaAlmacenes";
	}
	
	@GetMapping("/create")
	public String crear(HttpSession session, Model model, Authentication auth) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Propietario> propietario = servicePropietarios.buscarTodos();
		model.addAttribute("usuario", usuario);
		model.addAttribute("propietarios", propietario);
		model.addAttribute("almacen", new Almacen());
		return "almacenes/formularioCrear";
	}
	
	@GetMapping("/edit/{id}")
	public String modificar(@PathVariable("id") Integer idAlmacen, HttpSession session,
			Model model, Authentication auth, RedirectAttributes attributes) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Almacen almacen = serviceAlmacen.buscarPorId(idAlmacen);
		//si el id del almacen no existe retornara al formulario de crear con un error
		if(almacen==null) {
			attributes.addFlashAttribute("msg3", "No existe");
			return "redirect:/almacenes/create";
		}
		
		if(almacen.getImagen() == null || (almacen.getImagen().equals(""))) {
			almacen.setImagen("no-image.png");
		}
		
		List<Propietario> propietario = servicePropietarios.buscarTodos();
		model.addAttribute("usuario", usuario);
		model.addAttribute("propietarios", propietario);
		model.addAttribute("almacen", almacen);
		return "almacenes/formularioEdit";
	}
	
	@PostMapping("/save")
	public String guardar(Almacen almacen, @RequestParam(name = "archivoImagen",  required = false) MultipartFile multiPart
			,HttpSession session, Model model, 
			 RedirectAttributes attributes) throws ParseException {
		String method = almacen.getId()!=null?"UPDATE":"CREATE";

		//verificamos la imagen
		if (!multiPart.isEmpty()) {
			String nombreImagen = Utileria.guardarArchivo(multiPart, ruta);
			if (nombreImagen != null){ // La imagen si se subio
				// Procesamos la variable nombreImagen
				almacen.setImagen(nombreImagen);
			}
		}
		
		serviceAlmacen.save(almacen);
		if(method.equals("UPDATE")) {
			model.addAttribute("msg2", "Almacen modificado");
			attributes.addFlashAttribute("msg2", "Almacen modificado");
		}else {
			attributes.addFlashAttribute("msg", "Almacen creado");
		}
		return "redirect:/almacenes/create";
	}
	
	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
	
}
