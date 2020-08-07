package com.developergg.app.controller;

import java.util.Date;
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
import com.developergg.app.model.ArticuloAjuste;
import com.developergg.app.model.ArticuloSerial;
import com.developergg.app.model.Categoria;
import com.developergg.app.model.Propietario;
import com.developergg.app.model.Suplidor;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IArticulosAjustesService;
import com.developergg.app.service.IArticulosSeriales;
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
	
	@Autowired
	private IArticulosAjustesService serviceArticulosAjustes;
	
	@Autowired
	private IArticulosSeriales serviceArticulosSeriales;
	
	@Value("${inventario.ruta.imagenes}")
	private String ruta;
	
	private List<Articulo> lista;
		
	@GetMapping("/")
	public String mostrarArticulos(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//articulos por tienda que no esten eliminados
		lista = serviceArticulos.buscarPorTienda(usuario.getAlmacen().getPropietario())
				.stream().filter(p -> p.getEliminado() == 0).collect(Collectors.toList());
		lista.forEach((a)-> {
				//si tiene no tiene serial
				if(a.getImei().equalsIgnoreCase("0") || a.getImei().equalsIgnoreCase("NO")) {
					List<ArticuloAjuste> articulosAjustes = serviceArticulosAjustes.buscarPorArticuloYAlmacen(a, usuario.getAlmacen());
					if(articulosAjustes.isEmpty()) {
						a.setCantidad(0);
					}else {
						ArticuloAjuste newArticuloAjuste = articulosAjustes.get(articulosAjustes.size()-1);
						a.setCantidad(newArticuloAjuste.getDisponible());
					}
				}else {
					//Validacion para articulos con serial
					List<ArticuloSerial> articuloSerials = serviceArticulosSeriales
							.buscarPorArticuloAlmacen(a, usuario.getAlmacen()).stream()
							.filter(s -> s.getEstado().equalsIgnoreCase("Disponible"))
							.collect(Collectors.toList());
					if(!articuloSerials.isEmpty()) {
						a.setCantidad(articuloSerials.size());
					}else {
						a.setCantidad(0);
					}
				}
			});
		model.addAttribute("articulos", lista);
		return "articulos/listaArticulos";
	}
	
	@GetMapping("/create")
	public String crear(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Categoria> categorias = serviceCategorias.buscarPorPropietario(usuario.getAlmacen().getPropietario());
		Articulo articuloTemp = lista.get(lista.size()-1);
		Integer codigo = Integer.parseInt(articuloTemp.getCodigo());
		codigo++;
		Articulo newArticulo = new Articulo();
		newArticulo.setCodigo(String.valueOf(codigo));
		model.addAttribute("categorias",categorias);
		model.addAttribute("articulos", lista);
		model.addAttribute("articulo", newArticulo);
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
	
	@PostMapping("/sinSerial/save")
	public String saveArticulosSinSerial(ArticuloAjuste articuloAjuste, Model model, HttpSession session,
			RedirectAttributes attributes) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		articuloAjuste.setCosto(articuloAjuste.getArticulo().getCosto());
		Articulo articulo = serviceArticulos.buscarPorId(articuloAjuste.getArticulo().getId());
		// verificar si el registro tiene inventario
		List<ArticuloAjuste> lista = serviceArticulosAjustes.buscarPorArticuloYAlmacen(articulo, usuario.getAlmacen());
		//Caso para entradas y salidas
		if(articuloAjuste.getTipoMovimiento().equalsIgnoreCase("entrada") || (articuloAjuste.getTipoMovimiento().equalsIgnoreCase("salida"))) {
			// Si no tiene registros se crea uno nuevo, de lo contrario, 
			//utilizamos los valores del inventario de articulo como referencia para el nuevo registro
			if(lista.isEmpty()) {
				articuloAjuste.setExistencia(articuloAjuste.getCantidad());
				articuloAjuste.setDisponible(articuloAjuste.getCantidad());
				articuloAjuste.setFecha(new Date());
				articuloAjuste.setAlmacen(usuario.getAlmacen());
				articuloAjuste.setUsuario(usuario);
				serviceArticulosAjustes.guardar(articuloAjuste);
				if(articuloAjuste.getId()!=null) {
					attributes.addFlashAttribute("msg", "Registro creado");
				}else {
					attributes.addFlashAttribute("msg3", "Registro no creado");
				}
			}else {
				//ordenamos el ultimo elemento de la lista
				ArticuloAjuste newArticuloAjuste = lista.get(lista.size()-1);
				ArticuloAjuste newArticuloAjusteDefinitive = new ArticuloAjuste();
				newArticuloAjusteDefinitive.setId(null);
				newArticuloAjusteDefinitive.setAlmacen(usuario.getAlmacen());
				newArticuloAjusteDefinitive.setFecha(new Date());
				newArticuloAjusteDefinitive.setUsuario(usuario);
				newArticuloAjusteDefinitive.setTipoMovimiento(articuloAjuste.getTipoMovimiento());
				newArticuloAjusteDefinitive.setCantidad(articuloAjuste.getCantidad());
				newArticuloAjusteDefinitive.setCosto(articuloAjuste.getCosto());
				newArticuloAjusteDefinitive.setExistencia(newArticuloAjuste.getDisponible());
				newArticuloAjusteDefinitive.setArticulo(articulo);
				newArticuloAjusteDefinitive.setSuplidor(articuloAjuste.getSuplidor());
				newArticuloAjusteDefinitive.setNo_factura(articuloAjuste.getNo_factura());
				
				//verificamos si el tipo de movimiento es salida y la cantidad a retirar es mayor al inventario
				if (newArticuloAjusteDefinitive.getTipoMovimiento().equalsIgnoreCase("salida")) {
					if(newArticuloAjuste.getDisponible() >= articuloAjuste.getCantidad()) {
						newArticuloAjusteDefinitive.setDisponible(newArticuloAjusteDefinitive.getExistencia()-articuloAjuste.getCantidad());
					}else {
						attributes.addFlashAttribute("msg4", "Registro no creado");
						return "redirect:/articulos/";
					}
				}else {
					//entradas con registros anteriores
					newArticuloAjusteDefinitive.setDisponible(newArticuloAjusteDefinitive.getExistencia()+newArticuloAjusteDefinitive.getCantidad());	
				}
				serviceArticulosAjustes.guardar(newArticuloAjusteDefinitive);
				if(newArticuloAjusteDefinitive.getId()!=null) {
					attributes.addFlashAttribute("msg", "Registro creado");
				}else {
					attributes.addFlashAttribute("msg3", "Registro no creado");
				}
			}
		}
		return "redirect:/articulos/";
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
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable(name = "id") Integer idArticulo, Model model, HttpSession session, RedirectAttributes attributes) {
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		if(articulo == null) {
			attributes.addFlashAttribute("msg3", "No existe");
			return "redirect:/articulos/create";
		}
		if(articulo.getCantidad() > 0) {
			attributes.addFlashAttribute("msg6", "No se puede eliminar");
			return "redirect:/articulos/";
		}
		articulo.setEliminado(1);
		articulo.setFecha_eliminado(new Date());
		serviceArticulos.guardar(articulo);
		attributes.addFlashAttribute("msg5", "Eliminado");
		return "redirect:/articulos/";
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
		model.addAttribute("articuloSerial", articuloSerial);
		model.addAttribute("suplidores", suplidores);
		return "articulos/formularioSerial";
	}
	
	@GetMapping("/inventario/sinSerial/{id}")
	public String entradaSalidaSinSerial(@PathVariable(name = "id") Integer idArticulo, RedirectAttributes attributes,
			Model model, HttpSession session) {
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		// si el articulo no existe o esta en eliminado (eliminado logico) retornara al
		// formulario de crear con un error
		if(articulo==null || articulo.getEliminado() == 1) {
			attributes.addFlashAttribute("msg3", "No existe");
			return "redirect:/articulos/create";
		}
		
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		//creamos el objeto ArticuloAjuste y asociamos los atributos comunes
		ArticuloAjuste articuloAjuste = new ArticuloAjuste();
		articuloAjuste.setAlmacen(usuario.getAlmacen());
		articuloAjuste.setArticulo(articulo);
		
		//buscamos la lista de suplidores por almacen que no esten eliminados
		List<Suplidor> suplidores = serviceSuplidores.buscarPorAlmacenDisponible(usuario.getAlmacen())
				.stream().filter(s -> s.getEliminado() == 0).collect(Collectors.toList());
		model.addAttribute("articulo", articulo);
		model.addAttribute("articuloAjuste",articuloAjuste);
		model.addAttribute("suplidores", suplidores);
		return "articulos/formularioSinSerial";
	}
	
	@GetMapping("/ajax/getAll")
	public String listaArticulosAjax(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		if(lista==null) {
			//articulos por tienda que no esten eliminados
			lista = serviceArticulos.buscarPorTienda(usuario.getAlmacen().getPropietario())
					.stream().filter(p -> p.getEliminado() == 0).collect(Collectors.toList());
		}
		model.addAttribute("listaArticulos", lista);
		return "facturas/factura :: listaArticulos";
	}
	
	@GetMapping("/ajax/getAll/{txt}")
	public String listaArticulosAjaxWhitTxt(@PathVariable("txt") String txt, Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Articulo> articuloExistente = null;
		//buscamos a partir de 3 caracteres
		if(txt.contains("_")) {
			txt = txt.replace("_", " ");
		}
		if(txt.length()>2) {
			//Buscamos los articulos por nombre o codigo y propietario que no este eliminado
			articuloExistente = serviceArticulos.buscarPorNombreOrCodigo(txt, usuario.getAlmacen().getPropietario()).
					stream().filter(a -> a.getEliminado() == 0).collect(Collectors.toList());
			//Buscamos los seriales por almacen que no esten eliminados
			List<ArticuloSerial> seriales = serviceArticulosSeriales.buscarPorSerialAndAlmacen(txt, usuario.getAlmacen()).
					stream().filter(s -> (s.getEliminado() == 0 && s.getEstado().equalsIgnoreCase("Disponible"))).collect(Collectors.toList());
			if(!articuloExistente.isEmpty()) {
				for (Articulo articulo : articuloExistente) {
					//Buscamos el articulo por serial
					if(articulo.getImei().equals("SI")) {
						//verificamos si en la lista de seriales ya se encuentra el articulo
						for (ArticuloSerial articuloSerial : seriales) {											
							if(articuloSerial.getArticulo().getId()==articulo.getId()) {
								seriales.remove(articuloSerial);
							}
						}
					}else {
						//Verificamos el inventario de los articulos que no tienen imei
						List<ArticuloAjuste> articulosAjustes = serviceArticulosAjustes.buscarPorArticuloYAlmacen(articulo, usuario.getAlmacen());
						if(articulosAjustes.isEmpty()) {
							articuloExistente.remove(articulo);
						}
					}
				}
			}
			
			//si la lista de seriales tiene articulos los incluimos en la lista de articulos
			if(!seriales.isEmpty()) {
				for (ArticuloSerial articuloSerial : seriales) {
					articuloExistente.add(articuloSerial.getArticulo());
				}
			}
			
		}
		
		model.addAttribute("listaArticulos", articuloExistente);
		return "facturas/factura :: listaArticulos";
	}
	
	@GetMapping("/ajax/getType/{id}")
	public String obtenerTipoDeArticuloAjax(@PathVariable("id") Integer idArticulo, Model model, HttpSession session) {
		//Buscamos el articulo a partir del id
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		model.addAttribute("tipoArticulo", articulo.getImei());
		return "facturas/factura :: #tipoArticulo";
	}
	
	@GetMapping("/ajax/getSerialesForArticulo/{id}")
	public String obtenerListaSerialesAjax(@PathVariable("id") Integer idArticulo, Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//Buscamos el articulo a partir del id
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		List<ArticuloSerial> lista = null;
		if(articulo!=null) {
			//Buscamos los seriales que pertenezcan al articulo, por almacen que no esten eliminados y esten disponibles
			lista = serviceArticulosSeriales.buscarPorArticuloAlmacen(articulo, usuario.getAlmacen()).
					stream().filter(s -> 
					(s.getEliminado() == 0 && s.getEstado().equalsIgnoreCase("Disponible"))).
					collect(Collectors.toList());
		}
		model.addAttribute("listaMultiSerial", lista);
		return "facturas/factura :: listaMultiSerial";
	}
	
	@GetMapping("/ajax/getArticuloSinserial/{id}")
	public String obtenerArticuloWhitIdAjax(@PathVariable("id") Integer idArticulo, Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//Buscamos el articulo a partir del id
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		if(articulo.getImei().equalsIgnoreCase("NO") || articulo.getImei().equalsIgnoreCase("0")) {
			List<ArticuloAjuste> articulosAjustes = serviceArticulosAjustes.buscarPorArticuloYAlmacen(articulo, usuario.getAlmacen());
			if(articulosAjustes.isEmpty()) {
				articulo.setCantidad(0);
			}else {
				ArticuloAjuste newArticuloAjuste = articulosAjustes.get(articulosAjustes.size()-1);
				articulo.setCantidad(newArticuloAjuste.getDisponible());
			}
		}
		model.addAttribute("idArticuloBuscado", articulo.getId());
		model.addAttribute("nombreArticuloBuscado", articulo.getNombre());
		model.addAttribute("existencia", articulo.getCantidad());
		model.addAttribute("precioMaximo", articulo.getPrecio_maximo());
		model.addAttribute("precioMinimo", articulo.getPrecio_minimo());
		model.addAttribute("precioMayor", articulo.getPrecio_mayor());
		model.addAttribute("minMax", articulo.getRango_precio_maximo_desde());
		model.addAttribute("maxMax", articulo.getRango_precio_maximo_hasta());
		model.addAttribute("minMin", articulo.getRango_precio_minimo_desde());
		model.addAttribute("maxMin", articulo.getRango_precio_minimo_hasta());
		model.addAttribute("minMay", articulo.getRango_precio_mayor_desde());
		model.addAttribute("maxMay", articulo.getRango_precio_mayor_hasta());
		return "facturas/factura :: #articuloBuscado";
	}
	
	@GetMapping("/ajax/getPrice/{id}/{cant}")
	public String obtenerPrecioArticuloAjax(@PathVariable("id") Integer idArticulo, @PathVariable("cant") Integer cantidad, Model model, HttpSession session) {
		//Buscamos el articulo a partir del id
		Double precio = 0.0;
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		if(articulo.getRango_precio_maximo_desde() >= cantidad || articulo.getRango_precio_maximo_hasta() >= cantidad) {
			precio = articulo.getPrecio_maximo();
		} else if(articulo.getRango_precio_minimo_desde() >= cantidad || articulo.getRango_precio_minimo_hasta() >= cantidad) {
			precio = articulo.getPrecio_minimo();
		} else if(articulo.getRango_precio_mayor_desde() >= cantidad || articulo.getRango_precio_mayor_hasta() >= cantidad) {
			precio = articulo.getPrecio_mayor();
		} 
		
		model.addAttribute("precioArticulo", precio);
		return "facturas/factura :: #precioRango";
	}
	
	@PostMapping("/ajax/addService/")	
	public String agregarServicio(HttpSession session, @RequestParam("descripcion") String descripcion, @RequestParam("costo") Double costo,
			@RequestParam("precio") Double precio, @RequestParam("cantidad") Integer cantidad) {
//		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		return "facturas/factura :: #precioRango";
	}
		
	@ModelAttribute
	public void setGenericos(Model model, HttpSession session) {
		if(session.getAttribute("almacen") != null) {
			model.addAttribute("almacen", (Almacen) session.getAttribute("almacen"));
			model.addAttribute("propietario", (Propietario) session.getAttribute("propietario"));
		}
	}
	
}
