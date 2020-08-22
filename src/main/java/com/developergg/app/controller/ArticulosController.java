package com.developergg.app.controller;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.ListUtils;
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
import com.developergg.app.model.Cliente;
import com.developergg.app.model.ComprobanteFiscal;
import com.developergg.app.model.FacturaDetallePagoTemp;
import com.developergg.app.model.FacturaDetalleTemp;
import com.developergg.app.model.FacturaSerialTemp;
import com.developergg.app.model.FacturaServicioTemp;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.Propietario;
import com.developergg.app.model.SerialTemporal;
import com.developergg.app.model.Suplidor;
import com.developergg.app.model.Usuario;
import com.developergg.app.model.Vendedor;
import com.developergg.app.service.IArticulosAjustesService;
import com.developergg.app.service.IArticulosSeriales;
import com.developergg.app.service.IArticulosService;
import com.developergg.app.service.ICategoriasService;
import com.developergg.app.service.IClientesService;
import com.developergg.app.service.IComprobantesFiscalesService;
import com.developergg.app.service.IFacturasDetallesPagoTempService;
import com.developergg.app.service.IFacturasDetallesTempService;
import com.developergg.app.service.IFacturasSerialesTempService;
import com.developergg.app.service.IFacturasServiciosTempService;
import com.developergg.app.service.IFacturasTempService;
import com.developergg.app.service.ISuplidoresService;
import com.developergg.app.service.IVendedoresService;
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
	
	@Autowired
	private IFacturasServiciosTempService serviceServiciosTemp;
	
	@Autowired
	private IFacturasDetallesTempService serviceFacturasDetallesTemp;
	
	@Autowired
	private IFacturasSerialesTempService serviceSerialesTemp;
	
	@Autowired
	private IClientesService serviceClientes;
	
	@Autowired
	private IVendedoresService serviceVendedores;
	
	@Autowired
	private IComprobantesFiscalesService serviceComprobantesFiscales;
	
	@Autowired
	private IFacturasTempService serviceFacturasTemp;
	
	@Autowired
	private IFacturasDetallesPagoTempService serviceDetallesPagosTemp;
	
	@Value("${inventario.ruta.imagenes}")
	private String ruta;
	
	private List<Articulo> lista;
	
//	private final double impuestoTemp = 0.18;
	
	DecimalFormat df2 = new DecimalFormat("###.##");
		
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
					for (ArticuloSerial articuloSerial : articuloSerials) {
						//buscamos el articulo
						if(articuloSerial.getArticulo().getId() == a.getId()) {
							a.setPrecio_maximo(articuloSerial.getPrecio_maximo());
							a.setPrecio_mayor(articuloSerial.getPrecio_mayor());
							a.setPrecio_minimo(articuloSerial.getPrecio_minimo());
						}
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
			if(original.getImei().equalsIgnoreCase("SI")) {
				articulo.setImei(original.getImei());
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
	
	@GetMapping("/ajax/getInfoArticulo/{id}")
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
		model.addAttribute("conItbis", articulo.getItbis());
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
	
	@GetMapping("/ajax/getPriceWhitClient/{id}/{cant}/{idCliente}")
	public String obtenerPrecioArticuloWhitClienteAjax(@PathVariable("id") Integer idArticulo,
			@PathVariable("cant") Integer cantidad, Model model,
			@PathVariable("idCliente") Integer idCliente, HttpSession session) {
		//Buscamos el articulo a partir del id
		Double precio = 0.0;
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		Cliente cliente = serviceClientes.buscarPorIdCliente(idCliente);
		if(cliente.getPrecio().equalsIgnoreCase("precio_1")) {
			precio = articulo.getPrecio_maximo();
		}else if(cliente.getPrecio().equalsIgnoreCase("precio_2")) {
			precio = articulo.getPrecio_minimo();
		}else if(cliente.getPrecio().equalsIgnoreCase("precio_3")) {
			precio = articulo.getPrecio_mayor();
		}
		model.addAttribute("precioArticulo", precio);
		return "facturas/factura :: #precioRango";
	}
	
	@GetMapping("/ajax/getPriceWhitClientWhitSerial/{id}/{cant}/{idCliente}")
	public String obtenerPrecioArticuloWhitClienteSerialAjax(@PathVariable("id") Integer idArticulo,
			@PathVariable("cant") Integer cantidad, Model model,
			@PathVariable("idCliente") Integer idCliente, HttpSession session) {
		//Buscamos el articulo a partir del id
		Double precio = 0.0;
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		precio = articulo.getCosto();
		model.addAttribute("precioArticulo", precio);
		return "facturas/factura :: #precioRango";
	}
	
	@PostMapping("/ajax/addService/")	
	public String agregarServicio(HttpSession session, @RequestParam("descripcion") String descripcion, @RequestParam("costo") Double costo,
			@RequestParam("precio") Double precio, @RequestParam("cantidad") Integer cantidad) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaTemp factura = serviceFacturasTemp.buscarPorUsuario(usuario);
		double itbis = 0.0;
		double subtotal = 0.0;
		double total = 0.0;
		double subTotalItbis = 0.0;
		FacturaServicioTemp servicioFac = new FacturaServicioTemp();
		servicioFac.setUsuario(usuario);
		servicioFac.setAlmacen(usuario.getAlmacen()); //redundante por el usuario. validar
		servicioFac.setDescripcion(descripcion);
		servicioFac.setCosto(costo);
		servicioFac.setCantidad(cantidad);
		
		//Verificamos si el comprobante fiscal paga itbis
		
		if (factura.getComprobanteFiscal().getPaga_itbis() == 1) {
			// verificamos si el comprobante fiscal incluye el itbis en el precio
			if (factura.getComprobanteFiscal().getIncluye_itbis() == 1) {
				// realizamos las conversiones
				String tempSv = "1." + factura.getComprobanteFiscal().getValor_itbis().intValue();
				Double precioTempSv = precio / Double.parseDouble(tempSv);
				Double itBisTempSv = (cantidad * precioTempSv)
						* (factura.getComprobanteFiscal().getValor_itbis() / 100.00);
				precio = Double.parseDouble(df2.format(precioTempSv).replace(",", "."));
				itbis = Double.parseDouble(df2.format(itBisTempSv).replace(",", "."));
				subtotal = Double
						.parseDouble(df2.format((cantidad * precio)
								+ itbis).replace(",", "."));
				total += subtotal;
			} else {
				// realizamos las conversiones
				Double itBisTempServ = (cantidad * precio)
						* (factura.getComprobanteFiscal().getValor_itbis() / 100.00);
				precio = Double.parseDouble(df2.format(precio).replace(",", "."));
				itbis = Double.parseDouble(df2.format(itBisTempServ).replace(",", "."));
				subtotal = Double
						.parseDouble(df2.format((cantidad * precio)
								+ itbis).replace(",", "."));
				total += subtotal;
			}
		}else {
			total = precio;
		}
		
		subTotalItbis+=itbis;
		
		servicioFac.setPrecio(precio);
		servicioFac.setItbis(subTotalItbis);
		servicioFac.setSubtotal(total);
		servicioFac.setComprobanteFiscal(factura.getComprobanteFiscal());
		serviceServiciosTemp.guardar(servicioFac);
		return "facturas/factura :: #responseAddService";
	}
	
	@PostMapping("/ajax/addArticuloSinSerial/")	
	public String agregarArticuloSinSerial(HttpSession session, @RequestParam("idArticulo") Integer idArticulo,
			@RequestParam("cantidad") Integer cantidad, @RequestParam("precio") Double precio,
			@RequestParam("conItbis") String conItbis, @RequestParam("disponible") Integer disponible,
			@RequestParam("maximo") Double maximo, @RequestParam("valorItbis") Double valorItbis,
			@RequestParam("incluyeItbis") Integer incluyeItbis, @RequestParam("realPrice") Double realPrice) {
		Double itBis = 0.0;
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//Buscamos el articulo
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		FacturaTemp factura = serviceFacturasTemp.buscarPorUsuario(usuario);
		FacturaDetalleTemp facturaTemp = new FacturaDetalleTemp();
		facturaTemp.setArticulo(articulo);
		facturaTemp.setUsuario(usuario);
		facturaTemp.setAlmacen(usuario.getAlmacen());
		facturaTemp.setCantidad(cantidad);
		facturaTemp.setConItbis(conItbis.equals("SI")?"1":"0");
		incluyeItbis = factura.getComprobanteFiscal().getIncluye_itbis();
		
		Double subtotal = 0.0;
		
		//verificamos si el articulo tiene itbis
		if(articulo.getItbis().equalsIgnoreCase("SI")) {
			if(conItbis.equals("SI")) {
				//verificamos si incluye itbis en el precio
				if(incluyeItbis == 1) {
					String temp = "1."+valorItbis.intValue();
					subtotal = realPrice / Double.parseDouble(temp);
					itBis = realPrice - subtotal;
				}else {
					itBis= realPrice * valorItbis/100; 
				}
			}
		}else {
			itBis = 0.0;
			subtotal = realPrice;
		}
				
		itBis *= cantidad;
		
		Double newPrecio = incluyeItbis==1?subtotal:realPrice;
		newPrecio = Double.parseDouble(df2.format(newPrecio).replace(",", "."));
		facturaTemp.setPrecio(newPrecio);
		facturaTemp.setExistencia(disponible);
		facturaTemp.setItbis(Double.parseDouble(df2.format(itBis).replace(",", ".")));
		facturaTemp.setPrecio_maximo(maximo);
		facturaTemp.setSubtotal(cantidad*newPrecio+itBis);
		facturaTemp.setComprobanteFiscal(factura.getComprobanteFiscal());
		serviceFacturasDetallesTemp.guardar(facturaTemp);
		return "facturas/factura :: #responseAddArticuloSinSerial";
	}
	
	@PostMapping("/ajax/addArticuloConSerial/")	
	public String agregarArticuloConSerial(HttpSession session, @RequestParam("idArticulo") Integer idArticulo,
			@RequestParam("cantidad") Integer cantidad, @RequestParam("precio") Double precio,
			@RequestParam("seriales") String seriales, @RequestParam("idCliente") Integer idCliente,
			@RequestParam("comprobanteFiscalId") Integer comprobanteFiscalId, @RequestParam("realPrice") Double realPrice, String columnas) {
		Double itBis = 0.0;
		Double subTotal = 0.0;
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//Buscamos el articulo
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		FacturaTemp factura = serviceFacturasTemp.buscarPorUsuario(usuario);
		ComprobanteFiscal comprobanteFiscal = factura.getComprobanteFiscal();
		Cliente cliente = serviceClientes.buscarPorIdCliente(idCliente);
		String[] serialesArray = seriales.split(",");
		double tempPriceSerial = 0.0;

		//Validacion para articulos con serial
		List<ArticuloSerial> articuloSerials = serviceArticulosSeriales
				.buscarPorArticuloAlmacen(articulo, usuario.getAlmacen()).stream()
				.filter(s -> s.getEstado().equalsIgnoreCase("Disponible"))
				.collect(Collectors.toList());
		if(!articuloSerials.isEmpty()) {
			articulo.setCantidad(articuloSerials.size());
		}else {
			articulo.setCantidad(0);
		}
		
		List<ArticuloSerial> serialAcct = new LinkedList<>();
		//extraemos la informacion de los seriales actuales
		for (ArticuloSerial articuloSerial : articuloSerials) {
			for (String temp : serialesArray) {
				if(articuloSerial.getSerial().equalsIgnoreCase(temp)) {
					serialAcct.add(articuloSerial);
				}
			}
		}
		
		if(cantidad>1) {
			boolean mismoPrecio = true;
			//verificamos si los seriales tienen el mismo precio
			for (ArticuloSerial articuloSerial : serialAcct) {
				if(tempPriceSerial == 0.0) {
					if(cliente!=null) {
						//verificamos el precio del cliente
						if(cliente.getPrecio().equalsIgnoreCase("precio_1")) {
							tempPriceSerial = articuloSerial.getPrecio_maximo();
						}
						
						if(cliente.getPrecio().equalsIgnoreCase("precio_2")) {
							tempPriceSerial = articuloSerial.getPrecio_minimo();
						}
						
						if(cliente.getPrecio().equalsIgnoreCase("precio_3")) {
							tempPriceSerial = articuloSerial.getPrecio_mayor();
						}
					}else {
						tempPriceSerial= articuloSerial.getPrecio_mayor();
					}
				}else {
					if(cliente!=null) {
						Double precioCliente =0.0;
						//verificamos el precio del cliente
						if(cliente.getPrecio().equalsIgnoreCase("precio_1")) {
							precioCliente = articuloSerial.getPrecio_maximo();
						}
						
						if(cliente.getPrecio().equalsIgnoreCase("precio_2")) {
							precioCliente = articuloSerial.getPrecio_minimo();
						}
						
						if(cliente.getPrecio().equalsIgnoreCase("precio_3")) {
							precioCliente = articuloSerial.getPrecio_mayor();
						}
						
						if(tempPriceSerial!=precioCliente) {
							mismoPrecio = false;
						}
						
					}else {
						if(tempPriceSerial!=articuloSerial.getPrecio_mayor()) {
							mismoPrecio = false;
						}
					}
				}
			}
			
			if(!mismoPrecio) {
				String[] info = columnas.split(",");
				List<String> lista = Arrays.asList(info).subList(1, info.length);
				List<SerialTemporal> serialesTemporales = new LinkedList<>();
				List<List<String>> output = ListUtils.partition(lista, 3);
				
				for (List<String> list : output) {
					SerialTemporal serialTemp = new SerialTemporal();
					int count = 0;
					for (String content : list) {
						if(count==0) {
							serialTemp.setId(Integer.parseInt(content));
						}
						if(count==1) {
							serialTemp.setSerial(Integer.parseInt(content));
						}
						if(count==2) {
							serialTemp.setPrecio(Double.parseDouble(content));
						}
						count++;
					}
					serialesTemporales.add(serialTemp);
				}
								
				//verificamos si tienen seriales con distintos precios, esto determinara los items en el detalle
				for (SerialTemporal listSerial : serialesTemporales) {
					FacturaDetalleTemp facturaTemp = new FacturaDetalleTemp();
					facturaTemp.setArticulo(articulo);
					facturaTemp.setUsuario(usuario);
					facturaTemp.setAlmacen(usuario.getAlmacen());
					facturaTemp.setCantidad(1);
					facturaTemp.setConItbis(articulo.getItbis().equals("SI")?"1":"0");
					facturaTemp.setImei(articulo.getImei().equals("SI")?"1":"0");
					
					precio = listSerial.getPrecio();
					
					//verificamos si el articulo tiene itbis
					if(articulo.getItbis().equals("SI")) {
						if(comprobanteFiscal.getPaga_itbis()==1) {
							//No incluye itbis en el precio
							if(comprobanteFiscal.getIncluye_itbis()==0) {
								itBis= precio * (comprobanteFiscal.getValor_itbis()/100.00); 
							}else {
								//incluye itbis
								String temp = "1."+comprobanteFiscal.getValor_itbis().intValue();
								subTotal = precio / Double.parseDouble(temp);
								itBis = precio - subTotal;
							}
						}
					}else {
						subTotal = precio;
					}
					
					Double newPrecio = comprobanteFiscal.getIncluye_itbis()==1?subTotal:precio;
					newPrecio = Double.parseDouble(df2.format(newPrecio).replace(",", "."));				
					facturaTemp.setPrecio(newPrecio);
					facturaTemp.setExistencia(articulo.getCantidad());
					facturaTemp.setItbis(Double.parseDouble(df2.format(itBis).replace(",", ".")));
					facturaTemp.setPrecio_maximo(articulo.getPrecio_maximo());
					facturaTemp.setSubtotal(Double.parseDouble(df2.format(1*newPrecio+itBis).replace(",", ".")));
					facturaTemp.setComprobanteFiscal(factura.getComprobanteFiscal());
					serviceFacturasDetallesTemp.guardar(facturaTemp);
					
					FacturaSerialTemp serialTemp = new FacturaSerialTemp();
					serialTemp.setId_serial(listSerial.getSerial());
					serialTemp.setIdDetalle(facturaTemp);
					serialTemp.setFacturaTemp(factura);
					//verificamos el serial del articulo
					List<ArticuloSerial> serialesDelArticulo = serviceArticulosSeriales.buscarPorSerialAndAlmacen(String.valueOf(listSerial.getSerial()), usuario.getAlmacen());
					if(!serialesDelArticulo.isEmpty()) {
						serialTemp.setArticuloSerial(serialesDelArticulo.get(0));
					}
					serviceSerialesTemp.guardar(serialTemp);
				}
			}else {
				//Mismo precio
				FacturaDetalleTemp facturaTemp = new FacturaDetalleTemp();
				facturaTemp.setArticulo(articulo);
				facturaTemp.setUsuario(usuario);
				facturaTemp.setAlmacen(usuario.getAlmacen());
				facturaTemp.setCantidad(serialesArray.length);
				
				facturaTemp.setConItbis(articulo.getItbis().equals("SI")?"1":"0");
				facturaTemp.setImei(articulo.getImei().equals("SI")?"1":"0");
				
				//como tienen el mismo precio sacamos el valor individual
				precio/=cantidad;
				
				//verificamos si el articulo tiene itbis
				if(articulo.getItbis().equals("SI")) {
					if(comprobanteFiscal.getPaga_itbis()==1) {
						//No incluye itbis en el precio
						if(comprobanteFiscal.getIncluye_itbis()==0) {
							itBis= precio * (comprobanteFiscal.getValor_itbis()/100.00); 
						}else {
							//incluye itbis
							String temp = "1."+comprobanteFiscal.getValor_itbis().intValue();
							subTotal = precio / Double.parseDouble(temp);
							itBis = precio - subTotal;
						}
					}
				}else {
					subTotal = precio;
				}
				
				itBis *= cantidad;
				
				Double newPrecio = comprobanteFiscal.getIncluye_itbis()==1?subTotal:precio;
				newPrecio = Double.parseDouble(df2.format(newPrecio).replace(",", "."));				
				facturaTemp.setPrecio(newPrecio);
				facturaTemp.setExistencia(articulo.getCantidad());
				facturaTemp.setItbis(Double.parseDouble(df2.format(itBis).replace(",", ".")));
				facturaTemp.setPrecio_maximo(articulo.getPrecio_maximo());
				facturaTemp.setSubtotal(cantidad*newPrecio+itBis);
				facturaTemp.setComprobanteFiscal(factura.getComprobanteFiscal());
				serviceFacturasDetallesTemp.guardar(facturaTemp);
				
				if(facturaTemp.getId()!=null) {
					for (String serial : serialesArray) {
						FacturaSerialTemp serialTemp = new FacturaSerialTemp();
						serialTemp.setId_serial(Integer.parseInt(serial));
						serialTemp.setIdDetalle(facturaTemp);
						serialTemp.setFacturaTemp(factura);
						//verificamos el serial del articulo
						List<ArticuloSerial> serialesDelArticulo = serviceArticulosSeriales.buscarPorSerialAndAlmacen(String.valueOf(serial), usuario.getAlmacen());
						if(!serialesDelArticulo.isEmpty()) {
							serialTemp.setArticuloSerial(serialesDelArticulo.get(0));
						}
						serviceSerialesTemp.guardar(serialTemp);
					}
				}
			
			}
			
		}else { //ggonzalez verificar urgente
			Double newPrecio = 0.0;
			FacturaDetalleTemp facturaTemp = new FacturaDetalleTemp();
			facturaTemp.setArticulo(articulo);
			facturaTemp.setUsuario(usuario);
			facturaTemp.setAlmacen(usuario.getAlmacen());
			facturaTemp.setCantidad(serialesArray.length);
			
			facturaTemp.setConItbis(articulo.getItbis().equals("SI")?"1":"0");
			facturaTemp.setImei(articulo.getImei().equals("SI")?"1":"0");
			
			//verificamos si el articulo tiene itbis
			if(articulo.getItbis().equals("SI")) {
				if(comprobanteFiscal.getPaga_itbis()==1) {
					//No incluye itbis en el precio
					if(comprobanteFiscal.getIncluye_itbis()==0) {
						itBis= precio * (comprobanteFiscal.getValor_itbis()/100.00); 
						newPrecio = precio;
						subTotal = cantidad*precio+itBis;
					}else {
						//incluye itbis
						String temp = "1."+comprobanteFiscal.getValor_itbis().intValue();
						newPrecio = precio / Double.parseDouble(temp);
						itBis = newPrecio * (comprobanteFiscal.getValor_itbis().intValue()/100.00);
						subTotal = newPrecio + itBis;
					}
				}
			}else {
				subTotal = precio;
			}
			
			itBis *= cantidad;

			facturaTemp.setPrecio(Double.parseDouble(df2.format(newPrecio).replace(",", ".")));
			facturaTemp.setExistencia(articulo.getCantidad());
			facturaTemp.setItbis(Double.parseDouble(df2.format(itBis).replace(",", ".")));
			facturaTemp.setPrecio_maximo(articulo.getPrecio_maximo());
			facturaTemp.setSubtotal(Double.parseDouble(df2.format(subTotal).replace(",", ".")));
			facturaTemp.setComprobanteFiscal(factura.getComprobanteFiscal());
			serviceFacturasDetallesTemp.guardar(facturaTemp);
			
			if(facturaTemp.getId()!=null) {
				for (String serial : serialesArray) {
					FacturaSerialTemp serialTemp = new FacturaSerialTemp();
					serialTemp.setId_serial(Integer.parseInt(serial));
					serialTemp.setIdDetalle(facturaTemp);
					serialTemp.setFacturaTemp(factura);
					//verificamos el serial del articulo
					List<ArticuloSerial> serialesDelArticulo = serviceArticulosSeriales.buscarPorSerialAndAlmacen(String.valueOf(serial), usuario.getAlmacen());
					if(!serialesDelArticulo.isEmpty()) {
						serialTemp.setArticuloSerial(serialesDelArticulo.get(0));
					}
					serviceSerialesTemp.guardar(serialTemp);
				}
			}
		}

		return "facturas/factura :: #responseAddArticuloConSerial";
	}
	
	@PostMapping("/ajax/verificarPreciosDeSeriales/")	
	public String verificarPreciosArticuloConSerial(Model model, HttpSession session, @RequestParam("idArticulo") Integer idArticulo,
			@RequestParam("cantidad") Integer cantidad, @RequestParam("seriales") String seriales,
			@RequestParam("idDetalle") Integer idDetalle, @RequestParam("precio") Double precio,
			@RequestParam("realPrice") Double realPrice, @RequestParam("idCliente") Integer idCliente) {
		//verificamos si es mas de un serial
		String[] serialesArray = seriales.split(",");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		Cliente cliente = serviceClientes.buscarPorIdCliente(idCliente);
		//lista de seriales asociados al articulo que esten disponibles
		List<ArticuloSerial> listaSeriales = serviceArticulosSeriales.buscarPorArticuloAlmacen(articulo, usuario.getAlmacen()).
				stream().filter(s-> s.getEstado().equalsIgnoreCase("Disponible")).
				collect(Collectors.toList());
		
		Double temporalPrice = 0.0;
		Double tipoPrice = 0.0;
		Boolean distinto = false;
		
		List<ArticuloSerial> listaSerialesAcct = new LinkedList<>();
	
		for (ArticuloSerial articuloSerial : listaSeriales) {
			for (String serial : serialesArray) {
				if(articuloSerial.getSerial().equalsIgnoreCase(serial)) {
					//sino hay cliente selecionamos el precio mayor
					if(cliente == null) {
						tipoPrice = articuloSerial.getPrecio_mayor();
						temporalPrice = articuloSerial.getPrecio_mayor();
					}else {
						//verificamos el precio 
						if(cliente.getPrecio().equalsIgnoreCase("precio_1")) {
							if(temporalPrice==0.0) {
								temporalPrice = articuloSerial.getPrecio_maximo();
							}
							tipoPrice = articuloSerial.getPrecio_maximo();
						}else if(cliente.getPrecio().equalsIgnoreCase("precio_2")) {
							if(temporalPrice==0.0) {
								temporalPrice = articuloSerial.getPrecio_minimo();
							}
							tipoPrice = articuloSerial.getPrecio_minimo();
						}else if(cliente.getPrecio().equalsIgnoreCase("precio_3")) {
							if(temporalPrice==0.0) {
								temporalPrice = articuloSerial.getPrecio_mayor();
							}
							tipoPrice = articuloSerial.getPrecio_mayor();
						}
					}
					
					//si los precios son distintos mostrara el modal en el front
					if(temporalPrice.doubleValue()!=tipoPrice.doubleValue()) {
						distinto = true;
					}

					//agregamos el serial a la lista de seriales actuales
					articuloSerial.setTemporalPrice(tipoPrice);
					listaSerialesAcct.add(articuloSerial);
				}
			}
		}
		
		if(cliente==null) {
			double tempPrecio = 0.0;
			for (ArticuloSerial articuloSerial2 : listaSerialesAcct) {
				if(tempPrecio == 0.0) {
					tempPrecio = articuloSerial2.getPrecio_mayor();
				}
				if(tempPrecio!=articuloSerial2.getPrecio_mayor()) {
					distinto=true;
					break;
				}
			}
		}
		
		if(distinto) {
			model.addAttribute("listaSerialesAcct", listaSerialesAcct);
		}
		model.addAttribute("estatus", distinto?1:0);
		return "facturas/factura :: #responsePreciosSeriales";
	}
	
	@PostMapping("/ajax/verificarPreciosDeSerialesNotMinimo/")	
	public String verificarPreciosArticuloConSerialNotMinimo(Model model, HttpSession session, @RequestParam("idArticulo") Integer idArticulo,
			@RequestParam("cantidad") Integer cantidad, @RequestParam("seriales") String seriales,
			@RequestParam("idDetalle") Integer idDetalle, @RequestParam("precio") Double precio) {
		 
		//verificamos si es mas de un serial
		String[] serialesArray = seriales.split(",");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		//lista de seriales asociados al articulo que esten disponibles
		List<ArticuloSerial> listaSeriales = serviceArticulosSeriales.buscarPorArticuloAlmacen(articulo, usuario.getAlmacen()).
				stream().filter(s-> s.getEstado().equalsIgnoreCase("Disponible")).
				collect(Collectors.toList());
		
		Double temporalPrice = 0.0;
		Boolean menor = false;
		
		temporalPrice = precio/cantidad;
			
		for (ArticuloSerial articuloSerial : listaSeriales) {
			for (String serial : serialesArray) {
				if(articuloSerial.getSerial().equalsIgnoreCase(serial)) {

					//si los precios son distintos mostrara el modal en el front
					if(temporalPrice < articuloSerial.getPrecio_minimo()) {
						menor = true;
						break;
					}
				}
			}
		}
		
		model.addAttribute("estatusUpdateSerialNotMinimo", menor?1:0);
		return "facturas/factura :: #responsePreciosSerialesNotMinimo";
	}
	
	@PostMapping("/ajax/modificarPreciosDeSeriales/")	
	public String modificarPrecioSeriales(Model model, HttpSession session,
			@RequestParam("infoSeriales") String infoSeriales, @RequestParam("idCliente") Integer idCliente) {
		String[] info = infoSeriales.split(",");
		List<String> lista = Arrays.asList(info).subList(1, info.length);
		List<SerialTemporal> serialesTemporales = new LinkedList<>();
		List<List<String>> output = ListUtils.partition(lista, 3);
		
		for (List<String> list : output) {
			SerialTemporal serialTemp = new SerialTemporal();
			int count = 0;
			for (String content : list) {
				if(count==0) {
					serialTemp.setId(Integer.parseInt(content));
				}
				if(count==1) {
					serialTemp.setSerial(Integer.parseInt(content));
				}
				if(count==2) {
					serialTemp.setPrecio(Double.parseDouble(content));
				}
				count++;
			}
			serialesTemporales.add(serialTemp);
		}
		
		int tempStatus = 0;
		double tempPrice = 0.0;
		//recorremos los seriales para hacer las validaciones
		for (SerialTemporal serialTemp : serialesTemporales) {
			//obtenemos la informacion del serial
			ArticuloSerial serial = serviceArticulosSeriales.buscarPorIdArticuloSerial(serialTemp.getId());
			//verificamos que el precio no sea menor a el precio minimo
			if(serialTemp.getPrecio()>=serial.getPrecio_minimo()) {
				tempPrice+=serialTemp.getPrecio();
				tempStatus  = 1;
			}else {
				tempStatus = 0;
				break;
			}
		}
		
		model.addAttribute("autorizado", tempStatus==1?1:0);
		model.addAttribute("sumaSeriales", tempPrice);
		model.addAttribute("estatusUpdateSerial", tempStatus);
		return "facturas/factura :: #estatusModificarSeriales";
	}
	
	@GetMapping("/ajax/loadCuerpoFactura/{incluyeItbis}")
	public String getCuerpoFacturaTemp(Model model, HttpSession session, @PathVariable("incluyeItbis") Integer incluyeItbis) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//Buscamos los detalles en la factura
		List<FacturaDetalleTemp> facturaDetallesTemp = serviceFacturasDetallesTemp.buscarPorUsuarioAlmacen(usuario, usuario.getAlmacen());
		FacturaTemp facturaTemp = serviceFacturasTemp.buscarPorUsuario(usuario);
		//Buscamos los servicios en la factura
		List<FacturaServicioTemp> facturaServiciosTemp = serviceServiciosTemp.buscarPorUsuarioAlmacen(usuario, usuario.getAlmacen());
		Double total = 0.0;
		Double subTotalItbis = 0.0;
		//Calculamos el total
		for (FacturaDetalleTemp facturaDetalleTemp : facturaDetallesTemp) {
			//verificamos si en el detalle el articulo incluye itbis en el precio
			if(facturaTemp.getComprobanteFiscal().getIncluye_itbis()==1) {
				//verificamos si el valor inicial del comprobante fiscal en la factura incluye itbis
				if(facturaDetalleTemp.getComprobanteFiscal().getIncluye_itbis()==1) {
					//realizamos las conversiones
					total+=facturaDetalleTemp.getSubtotal();
				}else {
					//realizamos las conversiones
					String temp = "1."+facturaTemp.getComprobanteFiscal().getValor_itbis().intValue();
					Double precioTemp = facturaDetalleTemp.getPrecio() / Double.parseDouble(temp);
					Double itBisTemp = (facturaDetalleTemp.getCantidad() * precioTemp) * (facturaTemp.getComprobanteFiscal().getValor_itbis()/100.00);
					facturaDetalleTemp.setPrecio(Double.parseDouble(df2.format(precioTemp).replace(",", ".")));
					facturaDetalleTemp.setItbis(Double.parseDouble(df2.format(itBisTemp).replace(",", ".")));
					facturaDetalleTemp.setSubtotal(Double.parseDouble(df2.format((facturaDetalleTemp.getCantidad()*facturaDetalleTemp.getPrecio())+facturaDetalleTemp.getItbis()).replace(",", ".")));
					total+=facturaDetalleTemp.getSubtotal();
				}
			}else {
				//verificamos si el valor inicial del comprobante fiscal en la factura incluye itbis
				if(facturaDetalleTemp.getComprobanteFiscal().getIncluye_itbis()==1) {
					//realizamos las conversiones
					facturaDetalleTemp.setPrecio(Double.parseDouble(df2.format((facturaDetalleTemp.getSubtotal()/facturaDetalleTemp.getCantidad())).replace(",", ".")));
					facturaDetalleTemp.setItbis(Double.parseDouble(df2.format((facturaDetalleTemp.getCantidad()*facturaDetalleTemp.getPrecio())*(facturaTemp.getComprobanteFiscal().getValor_itbis()/100.00)).replace(",", ".")));
					facturaDetalleTemp.setSubtotal(Double.parseDouble(df2.format((facturaDetalleTemp.getCantidad()*facturaDetalleTemp.getPrecio())+facturaDetalleTemp.getItbis()).replace(",", ".")));
					total+=facturaDetalleTemp.getSubtotal();
				}else {
					total+=facturaDetalleTemp.getSubtotal();
				}
			}
			subTotalItbis+=facturaDetalleTemp.getItbis();
			List<FacturaSerialTemp> facturaSerialesTemp = serviceSerialesTemp.buscarPorDetalleTemp(facturaDetalleTemp);
			if(!facturaSerialesTemp.isEmpty()) {
				for (FacturaSerialTemp facturaSerialTemp : facturaSerialesTemp) {
					facturaDetalleTemp.agregar(facturaSerialTemp);
				}
			}
		}
		
		//Validaciones para servicios
		for (FacturaServicioTemp facturaServicioTemp : facturaServiciosTemp) {
			//Verificamos si el comprobante fiscal paga itbis
			if(facturaTemp.getComprobanteFiscal().getPaga_itbis() == 1) {
				//verificamos si el comprobante fiscal incluye el itbis en el precio				
				if(facturaTemp.getComprobanteFiscal().getIncluye_itbis() == 1) {
					//realizamos las conversiones
					String tempSv = "1."+facturaTemp.getComprobanteFiscal().getValor_itbis().intValue();
					Double precioTempSv = facturaServicioTemp.getPrecio() / Double.parseDouble(tempSv);
					Double itBisTempSv = (facturaServicioTemp.getCantidad() * precioTempSv) * (facturaTemp.getComprobanteFiscal().getValor_itbis()/100.00);
					facturaServicioTemp.setPrecio(Double.parseDouble(df2.format(precioTempSv).replace(",", ".")));
					facturaServicioTemp.setItbis(Double.parseDouble(df2.format(itBisTempSv).replace(",", ".")));
					facturaServicioTemp.setSubtotal(Double.parseDouble(df2.format((facturaServicioTemp.getCantidad()*facturaServicioTemp.getPrecio())+facturaServicioTemp.getItbis()).replace(",", ".")));
					total+=facturaServicioTemp.getSubtotal();
				}else {
					//realizamos las conversiones
					Double itBisTempServ = (facturaServicioTemp.getCantidad() * facturaServicioTemp.getPrecio()) * (facturaTemp.getComprobanteFiscal().getValor_itbis()/100.00);
					facturaServicioTemp.setPrecio(Double.parseDouble(df2.format(facturaServicioTemp.getPrecio()).replace(",", ".")));
					facturaServicioTemp.setItbis(Double.parseDouble(df2.format(itBisTempServ).replace(",", ".")));
					facturaServicioTemp.setSubtotal(Double.parseDouble(df2.format((facturaServicioTemp.getCantidad()*facturaServicioTemp.getPrecio())+facturaServicioTemp.getItbis()).replace(",", ".")));
					total+=facturaServicioTemp.getSubtotal();
				}
			}else {
				//verificamos si el valor inicial del comprobante fiscal en la factura incluye itbis
				if(facturaServicioTemp.getComprobanteFiscal().getIncluye_itbis()==1) {
					//realizamos las conversiones
					facturaServicioTemp.setPrecio(Double.parseDouble(df2.format((facturaServicioTemp.getSubtotal()/facturaServicioTemp.getCantidad())).replace(",", ".")));
					facturaServicioTemp.setItbis(Double.parseDouble(df2.format((facturaServicioTemp.getCantidad()*facturaServicioTemp.getPrecio())*(facturaTemp.getComprobanteFiscal().getValor_itbis()/100.00)).replace(",", ".")));
					facturaServicioTemp.setSubtotal(Double.parseDouble(df2.format((facturaServicioTemp.getCantidad()*facturaServicioTemp.getPrecio())+facturaServicioTemp.getItbis()).replace(",", ".")));
					total+=facturaServicioTemp.getSubtotal();
				}else {
					total+=facturaServicioTemp.getSubtotal();
				}
			}
			subTotalItbis+=facturaServicioTemp.getItbis();
		}
		
		model.addAttribute("facturaDetalles", facturaDetallesTemp);
		model.addAttribute("facturaServicios", facturaServiciosTemp);
		model.addAttribute("subTotalItbis", Double.parseDouble(df2.format(subTotalItbis).replace(",", ".")));
		model.addAttribute("total", Double.parseDouble(df2.format(total).replace(",", ".")));
		return "facturas/cuerpoFactura :: cuerpoFactura";
	}
	
	@GetMapping("/ajax/loadCuerpoFacturaPago/{precioTotal}")
	public String getCuerpoFacturaPagoTemp(Model model, HttpSession session, @PathVariable("precioTotal") Double precioTotal) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaTemp facturaTemp = serviceFacturasTemp.buscarPorUsuario(usuario);
		List<FacturaDetallePagoTemp> listaPagos = serviceDetallesPagosTemp.buscarPorFacturaTemp(facturaTemp);
		
		Double totalPagos = 0.0;
		Double totalRestaPagos = 0.0;
		Double totalCambioPagos = 0.0;
		Integer mostrarCambio = 0;
		
		if(!listaPagos.isEmpty()) {
			for (FacturaDetallePagoTemp facturaDetallePagoTemp : listaPagos) {
				totalPagos+=facturaDetallePagoTemp.getMonto();
			}
			if(totalPagos<precioTotal) {
				totalRestaPagos = precioTotal-totalPagos;
			}else if(totalPagos>precioTotal) {
				totalCambioPagos = (precioTotal-totalPagos)*-1;
			}
			
			//si el ultimo pago es efectivo mostramos el cambio
			if(listaPagos.get(listaPagos.size()-1).getFormaPago().getNombre().equalsIgnoreCase("efectivo")) {
				mostrarCambio = 1;
			}
		}else {
			totalRestaPagos = precioTotal;
		}
		
		model.addAttribute("mostrarCambio", mostrarCambio);
		model.addAttribute("listaPagos", listaPagos);
		model.addAttribute("totalPagos", df2.format(totalPagos));
		model.addAttribute("totalRestaPagos", df2.format(totalRestaPagos));
		model.addAttribute("totalCambioPagos", df2.format(totalCambioPagos));
		return "facturas/cuerpoPago :: cuerpoPago";
	}
	
	@PostMapping("/ajax/deleteService/")
	public String quitarServicioFactura(Model model, @RequestParam("idServicio") Integer idServicio) {
		serviceServiciosTemp.eliminar(idServicio);
		return "facturas/factura :: #responseDeleteService";
	}
	
	@GetMapping("/ajax/obtenerSeriales/{id}")
	public String obtenerSeriales(Model model, @PathVariable("id") Integer idFacturaDetalle) {
		FacturaDetalleTemp detalle = serviceFacturasDetallesTemp.buscarPorId(idFacturaDetalle);
		List<FacturaSerialTemp> seriales = serviceSerialesTemp.buscarPorDetalleTemp(detalle);
		model.addAttribute("listaSeriales", seriales);
		model.addAttribute("idDetalleArticulo", detalle.getId());
		model.addAttribute("detalleArticulo", detalle.getArticulo().getNombre());
		return "facturas/factura :: infoSeriales";
	}
	
	@PostMapping("/ajax/deleteSerial/")
	public String quitarSerialFactura(Model model, @RequestParam("idSerial") Integer idSerial, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//actualizar detalle
		FacturaSerialTemp serialTemp = serviceSerialesTemp.buscarPorId(idSerial);
		FacturaDetalleTemp detalleTemp = serialTemp.getIdDetalle();
		//Obtenemos la lista de los seriales que pertenecen al articulo
		ArticuloSerial articuloSerial = serviceArticulosSeriales.buscarPorArticuloAlmacen(detalleTemp.getArticulo(), usuario.getAlmacen()).
				stream().filter(a -> 
						(a.getEstado().equalsIgnoreCase("Disponible") && 
						a.getSerial().equalsIgnoreCase(String.valueOf(serialTemp.getId_serial())))).
				collect(Collectors.toList()).get(0);
		FacturaTemp factura = serviceFacturasTemp.buscarPorUsuario(usuario);		
		//Obtenemos los valores reales del serial
		Double precioRealSerial = articuloSerial.getCosto();
		int tempCantidad = detalleTemp.getCantidad();
		double tempPrecio = detalleTemp.getPrecio();
		detalleTemp.setCantidad(tempCantidad-1);
		detalleTemp.setPrecio(tempPrecio-precioRealSerial);
		if(detalleTemp.getConItbis().equalsIgnoreCase("1") || detalleTemp.getConItbis().equalsIgnoreCase("SI")) {
			detalleTemp.setItbis(detalleTemp.getPrecio()*(factura.getComprobanteFiscal().getValor_itbis()/100.00)); 
			detalleTemp.setSubtotal(detalleTemp.getPrecio()+detalleTemp.getItbis());
		}else {
			detalleTemp.setItbis(0.0);
			detalleTemp.setSubtotal(detalleTemp.getPrecio());
		}
		serviceFacturasDetallesTemp.guardar(detalleTemp);
		serviceSerialesTemp.eliminar(idSerial);
		return "facturas/factura :: #responseDeleteSerial";
	}
	
	@PostMapping("/ajax/addSerialArticuloFactura/")
	public String agregarSerialFactura(Model model, @RequestParam("idDetalle") Integer idDetalle,
			 @RequestParam("serial") Integer serial, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaDetalleTemp detalleTemp = serviceFacturasDetallesTemp.buscarPorId(idDetalle);
		//Buscamos si el articulo ya tiene el serial incluido
		Articulo articulo = detalleTemp.getArticulo();
		FacturaTemp factura = serviceFacturasTemp.buscarPorUsuario(usuario);
		//Buscamos los seriales originales disponibles del articulo que esten disponibles
		List<ArticuloSerial> serialesOriginl = serviceArticulosSeriales.buscarPorArticuloAlmacen(articulo, usuario.getAlmacen()).
				stream().filter(s -> 
				(s.getEliminado() == 0 && s.getEstado().equalsIgnoreCase("Disponible"))).
				collect(Collectors.toList());
		
		boolean existeEnOriginal = false;
		
		//Verificamos que el serial que ingrese el usuario pertenezca al articulo
		for (ArticuloSerial articuloSerial : serialesOriginl) {
			if(articuloSerial.getSerial().replace(" ", "").equalsIgnoreCase(String.valueOf(serial))) {
				existeEnOriginal = true;
				break;
			}
		}
				
		//Si el serial esta dispoponible y conincide con los seriales del articulo verificamos que no este asociado en el detalle
		if(existeEnOriginal) {
			List<FacturaSerialTemp> listaSerialesFactura = serviceSerialesTemp.buscarPorDetalleTemp(detalleTemp);
			boolean existeEnDetalle = false;
			for (FacturaSerialTemp facturaSerialTemp : listaSerialesFactura) {
				if(facturaSerialTemp.getId_serial().equals(serial)) {
					existeEnDetalle = true;
					break;
				}
			}
			//Si no existe en el detalle lo agregamos
			if(!existeEnDetalle) {
				FacturaSerialTemp newSerial = new FacturaSerialTemp();
				newSerial.setId_serial(serial);
				newSerial.setIdDetalle(detalleTemp);
				//verificamos el serial del articulo
				List<ArticuloSerial> serialesDelArticulo = serviceArticulosSeriales.buscarPorSerialAndAlmacen(String.valueOf(serial), usuario.getAlmacen());
				if(!serialesDelArticulo.isEmpty()) {
					newSerial.setArticuloSerial(serialesDelArticulo.get(0));
				}
				newSerial.setFacturaTemp(factura);
				serviceSerialesTemp.guardar(newSerial);
				if(newSerial.getId()!=null) {
					//Obtenemos los datos del serial original
					ArticuloSerial serialOriginal = serviceArticulosSeriales.buscarPorSerial(String.valueOf(serial));
					//Actualizamos el detalle del articulo en la factura
					detalleTemp.setCantidad(detalleTemp.getCantidad()+1);
					detalleTemp.setPrecio(detalleTemp.getPrecio()+serialOriginal.getCosto());
					if(detalleTemp.getConItbis().equalsIgnoreCase("1") || detalleTemp.getConItbis().equalsIgnoreCase("SI")) {
						detalleTemp.setItbis(detalleTemp.getPrecio()*(factura.getComprobanteFiscal().getValor_itbis()/100.00)); 
						detalleTemp.setSubtotal(detalleTemp.getPrecio()+detalleTemp.getItbis());
					}else {
						detalleTemp.setItbis(0.0);
						detalleTemp.setSubtotal(detalleTemp.getPrecio());
					}
					serviceFacturasDetallesTemp.guardar(detalleTemp);
					model.addAttribute("response","GUARDADO");
				}
			}else {
				model.addAttribute("response","EXISTE EN DETALLE");
			}
		}else {
			model.addAttribute("response","NO EXISTE EN ORIGINAL");
		}
		return "facturas/factura :: #AddSerialStatus";
	}
	
	@PostMapping("/ajax/deleteArticuloFactura/")
	public String borrarArticuloFactura(Model model, @RequestParam("idDetalle") Integer idDetalle, 
			HttpSession session) {
		FacturaDetalleTemp detalleTemp = serviceFacturasDetallesTemp.buscarPorId(idDetalle);
		//validacion para articulos con imei
		Articulo articulo = detalleTemp.getArticulo();
		if(articulo.getImei().equalsIgnoreCase("SI")) {
			//obtenemos los seriales asociados al articulo en la factura
			List<FacturaSerialTemp> seriales = serviceSerialesTemp.buscarPorDetalleTemp(detalleTemp);
			//recorremos todos los seriales
			serviceSerialesTemp.eliminarListaSeriales(seriales);
			//borramos el detalle en la factura
			serviceFacturasDetallesTemp.eliminar(idDetalle);
			model.addAttribute("response", 1);
		}else {
			//Articulos sin imei
			serviceFacturasDetallesTemp.eliminar(idDetalle);
			model.addAttribute("response", 1);
		}
		return "facturas/factura :: #responseDeleteArticulo";
	}
	
	@PostMapping("/ajax/obtenerInfoDeSeriales/")
	public String obtenerInfoDeSeriales(Model model, @RequestParam("seriales") String seriales, 
			HttpSession session, @RequestParam("clienteId") Integer clienteId) {
		Double precio = 0.0;
		String precioCliente = "";
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Cliente cliente = serviceClientes.buscarPorIdCliente(clienteId);
		if(cliente!=null) {
			precioCliente = cliente.getPrecio();
		}
		String[] serialesArray = seriales.split(",");
		for (String serial : serialesArray) {
			ArticuloSerial articuloSerial = serviceArticulosSeriales.buscarPorSerialAndAlmacen(serial, usuario.getAlmacen()).get(0);
			if(articuloSerial!=null) {
				if(precioCliente.equalsIgnoreCase("precio_1")) {
					precio+=articuloSerial.getPrecio_maximo();
				}else if(precioCliente.equalsIgnoreCase("precio_2")) {
					precio+=articuloSerial.getPrecio_minimo();
				}else {
					precio+=articuloSerial.getPrecio_mayor();
				}
			}
		}
		model.addAttribute("precioArticulo", precio);
		model.addAttribute("cantidad", serialesArray.length);
		return "facturas/factura :: #cantidadYprecio";
	}
	
	@GetMapping("/ajax/getClienteAcct/")
	public String obtenerCliente(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaTemp factura = serviceFacturasTemp.buscarPorUsuario(usuario);
		if(factura.getCliente()!=null) {
			model.addAttribute("actualCliente", factura.getCliente().getId());
			model.addAttribute("actualRnc", factura.getCliente().getRnc());
		}else {
			model.addAttribute("actualCliente", "0");
			model.addAttribute("actualRnc", "0");
		}
		return "facturas/factura :: #clienteAcct";
	}
	
	@GetMapping("/ajax/getInfoCliente/{id}")
	public String obtenerInformacionClienteAjax(@PathVariable("id") Integer idCliente, Model model, HttpSession session) {
		//Buscamos el cliente a partir del id
		Cliente cliente = serviceClientes.buscarPorIdCliente(idCliente);
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaTemp factura = serviceFacturasTemp.buscarPorUsuario(usuario);
		factura.setCliente(cliente);
		serviceFacturasTemp.guardar(factura);
		model.addAttribute("idCliente", cliente!=null?cliente.getId():"0");
		model.addAttribute("nombreCliente", cliente!=null?cliente.getNombre():"");
		model.addAttribute("telefonoCliente", cliente!=null?cliente.getTelefono():"");
		model.addAttribute("rncCliente", cliente!=null?cliente.getRnc():"");
		model.addAttribute("precioCliente", cliente!=null?cliente.getPrecio():"");
		return "facturas/factura :: #nuevoCliente";
	}
	
	@GetMapping("/ajax/listaVendedores/")
	public String obtenerVendedores(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//Lista de vendedores por almacen que no esten eliminaos
		List<Vendedor> vendedores = serviceVendedores.buscarPorAlmacen(usuario.getAlmacen()).
				stream().filter(v -> v.getEliminado() == 0).
				collect(Collectors.toList());
		model.addAttribute("vendedores", vendedores);
		return "facturas/factura :: #seleccionVendedor";
	}
	
	@GetMapping("/ajax/getComprobanteFiscal/{id}")
	public String obtenerComprobanteFiscal(HttpSession session, Model model, @PathVariable("id") Integer idComprobanteFiscal) {
		ComprobanteFiscal comprobanteFiscal = serviceComprobantesFiscales.buscarPorId(idComprobanteFiscal);
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaTemp facturaTemp = serviceFacturasTemp.buscarPorUsuario(usuario);
		facturaTemp.setComprobanteFiscal(comprobanteFiscal);
		serviceFacturasTemp.guardar(facturaTemp);
		model.addAttribute("idComprobanteFiscal", comprobanteFiscal.getId());
		model.addAttribute("pagaItbis", comprobanteFiscal.getPaga_itbis());
		model.addAttribute("incluyeItbis", comprobanteFiscal.getIncluye_itbis());
		model.addAttribute("valorItbis", comprobanteFiscal.getValor_itbis());
		return "facturas/factura :: #comprobanteFiscalInfo";
	}
		
	@ModelAttribute
	public void setGenericos(Model model, HttpSession session) {
		if(session.getAttribute("almacen") != null) {
			model.addAttribute("almacen", (Almacen) session.getAttribute("almacen"));
			model.addAttribute("propietario", (Propietario) session.getAttribute("propietario"));
		}
	}
	
}
