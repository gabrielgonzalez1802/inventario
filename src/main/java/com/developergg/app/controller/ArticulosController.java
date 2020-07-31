package com.developergg.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloSerial;
import com.developergg.app.model.Categoria;
import com.developergg.app.model.Propietario;
import com.developergg.app.model.Suplidor;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IArticulosService;
import com.developergg.app.service.ICategoriasService;
import com.developergg.app.service.ISuplidoresService;
import com.developergg.app.util.Utileria;

@Controller
@RequestMapping(value = "/articulos")
public class ArticulosController {
	
	@Autowired
	private IArticulosService serviceArticulos;
	
	@Autowired
	private ICategoriasService serviceCategorias;
	
	@Autowired
	private ISuplidoresService serviceSuplidores;
	
	@Value("${inventario.ruta.imagenes}")
	private String ruta;
	
	private List<Articulo> lista;
	
	@GetMapping("/")
	public String mostrarArticulos(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//articulos por tienda que no esten eliminados
		lista = serviceArticulos.buscarPorTienda(usuario.getAlmacen().getPropietario())
				.stream().filter(p -> p.getEliminado() == 0).collect(Collectors.toList());
		model.addAttribute("articulos", lista);
		return "articulos/listaArticulos";
	}
	
	@GetMapping("/create")
	public String crear(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Categoria> categorias = serviceCategorias.buscarPorPropietario(usuario.getAlmacen().getPropietario());
		model.addAttribute("categorias",categorias);
		model.addAttribute("articulos", lista);
		model.addAttribute("articulo", new Articulo());
		return "articulos/formulario";
	}
	
	@PostMapping("/save")
	public String guardar(Articulo articulo, RedirectAttributes attributes, Model model,
			@RequestParam(name = "archivoImagen", required = false) MultipartFile multiPart, HttpSession session) {
		String method = "CREATE";
		Articulo original = null;
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//verificamos la imagen
		if (!multiPart.isEmpty()) {
			String nombreImagen = Utileria.guardarArchivo(multiPart, ruta);
			if (nombreImagen != null){ // La imagen si se subio
				// Procesamos la variable nombreImagen
				articulo.setImagen(nombreImagen);
			}
		}
		
		articulo.setIdUsuario(usuario.getId());
		
		if(articulo.getId() != null) {
			method = "UPDATE";
			original = serviceArticulos.buscarPorId(articulo.getId());
			if(articulo.getPrecio_maximo() == null) {
				articulo.setPrecio_maximo(original.getPrecio_maximo());
			}
			if(articulo.getPrecio_mayor() == null) {
				articulo.setPrecio_mayor(original.getPrecio_mayor());
			}
			if(articulo.getPrecio_minimo() == null) {
				articulo.setPrecio_minimo(original.getPrecio_minimo());
			}
			if(articulo.getRango_precio_maximo_desde() == null) {
				articulo.setRango_precio_maximo_desde(original.getRango_precio_maximo_desde());
			}
			if(articulo.getRango_precio_maximo_hasta() == null) {
				articulo.setRango_precio_maximo_hasta(original.getRango_precio_maximo_hasta());
			}
			if(articulo.getRango_precio_mayor_desde() == null) {
				articulo.setRango_precio_mayor_desde(original.getRango_precio_mayor_desde());
			}
			if(articulo.getRango_precio_mayor_hasta() == null) {
				articulo.setRango_precio_mayor_hasta(original.getRango_precio_mayor_hasta());
			}
			if(articulo.getRango_precio_minimo_desde() == null) {
				articulo.setRango_precio_minimo_desde(original.getRango_precio_minimo_desde());
			}
			if(articulo.getRango_precio_minimo_hasta() == null) {
				articulo.setRango_precio_minimo_hasta(original.getRango_precio_minimo_hasta());
			}
		}
		
		articulo.setTienda(usuario.getAlmacen().getPropietario());
		serviceArticulos.save(articulo);
		if(method.equals("UPDATE")) {
			model.addAttribute("msg2", "Almacen modificado");
			attributes.addFlashAttribute("msg2", "Almacen modificado");
		}else {
			attributes.addFlashAttribute("msg", "Almacen creado");
		}
		return "redirect:/articulos/create";
	}
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable(name = "id") Integer idArticulo, Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		List<Categoria> categorias = serviceCategorias.buscarPorPropietario(usuario.getAlmacen().getPropietario());
		model.addAttribute("categorias",categorias);
		model.addAttribute("articulos", lista);
		model.addAttribute("articulo", articulo);
		return "articulos/formularioEdit";
	}
	
	@GetMapping("/inventario/serial/{id}")
	public String entradaSalida(@PathVariable(name = "id") Integer idArticulo, RedirectAttributes attributes,
			Model model, HttpSession session) {
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		// si el articulo no existe o esta en eliminado (eliminado logico) retornara al
		// formulario de crear con un error
		if(articulo==null || articulo.getEliminado() == 1) {
			attributes.addFlashAttribute("msg3", "No existe");
			return "redirect:/articulos/create";
		}
		//buscamos la lista de suplidores por almacen que no esten eliminados
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Suplidor> suplidores = serviceSuplidores.buscarPorAlmacenDisponible(usuario.getAlmacen())
				.stream().filter(s -> s.getEliminado() == 0).collect(Collectors.toList());
		Suplidor suplidor = new Suplidor();
		ArticuloSerial articuloSerial = new ArticuloSerial();
		articuloSerial.setArticulo(articulo);
		suplidor.setAlmacen(usuario.getAlmacen());
		model.addAttribute("articulo", articulo);
		model.addAttribute("suplidor", suplidor);
		model.addAttribute("articuloSerial",articuloSerial);
		model.addAttribute("suplidores", suplidores);
		return "articulos/formularioSerial";
	}
	
	@PostMapping("/inventario/serial/save")
	public String guardarInventarioConSertial(ArticuloSerial articuloSerial, 
			@RequestParam("factura") String factura, @RequestParam("ultimosImei") String ultimosImei) {
		System.out.println("estoy aqui");
		return "redirect:/";
	}
	
	@ModelAttribute
	public void setGenericos(Model model, HttpSession session) {
		if(session.getAttribute("almacen") != null) {
			model.addAttribute("almacen", (Almacen) session.getAttribute("almacen"));
			model.addAttribute("propietario", (Propietario) session.getAttribute("propietario"));
		}
	}
	
}
