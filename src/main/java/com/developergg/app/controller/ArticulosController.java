package com.developergg.app.controller;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

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
import com.developergg.app.model.ArticuloReporte;
import com.developergg.app.model.ArticuloSerial;
import com.developergg.app.model.Categoria;
import com.developergg.app.model.Cliente;
import com.developergg.app.model.ComprobanteFiscal;
import com.developergg.app.model.FacturaDetallePagoTemp;
import com.developergg.app.model.FacturaDetalleTemp;
import com.developergg.app.model.FacturaSerialTemp;
import com.developergg.app.model.FacturaServicioTemp;
import com.developergg.app.model.FacturaTallerTemp;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.InventarioArticulo;
import com.developergg.app.model.Propietario;
import com.developergg.app.model.SerialTemporal;
import com.developergg.app.model.Suplidor;
import com.developergg.app.model.TallerArticulo;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IAlmacenesService;
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
import com.developergg.app.service.IFacturasTalleresTempService;
import com.developergg.app.service.IFacturasTempService;
import com.developergg.app.service.ISuplidoresService;
import com.developergg.app.service.ITalleresArticulosService;
import com.developergg.app.util.Utileria;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

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
	private IComprobantesFiscalesService serviceComprobantesFiscales;
	
	@Autowired
	private IFacturasTempService serviceFacturasTemp;
	
	@Autowired
	private IFacturasDetallesPagoTempService serviceDetallesPagosTemp;
	
	@Autowired
	private IFacturasTalleresTempService serviceFacturasTalleresTemp;

	@Autowired
	private ITalleresArticulosService serviceTalleresArticulos;
	
	@Autowired
	private IAlmacenesService serviceAlmacenes;
	
	@Value("${inventario.ruta.reporte.articulos}")
	private String rutaJreport;
	
	@Value("${inventario.ruta.reporte.valorInventario}")
	private String rutaJreportValorInventario;
	
	@Value("${inventario.ruta.reporte.movimientoArticulo}")
	private String rutaJreportMovimientoArticulos;
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private DataSource dataSource;
	
	@Value("${inventario.ruta.imagenes}")
	private String ruta;
	
	@Value("${inventario.ruta.imagenes}")
	private String rutaImagenes;
	
	private List<Articulo> lista;
	
	DecimalFormat df2 = new DecimalFormat("###.##");
	
	private String tempFolder =  System.getProperty("java.io.tmpdir");
	private String pathSeparator = System.getProperty("file.separator");
		
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
	
	@GetMapping("/formularioReporteMovimientoArticulos")
	public String formularioReporteMovimientoArticulos(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Articulo> articulos = serviceArticulos.buscarPorTienda(usuario.getAlmacen().getPropietario());
		model.addAttribute("articulos", articulos);
		model.addAttribute("dateAcct", new Date());
		return "articulos/generarReporteMovimientoArticulo";
	}
	
	@PostMapping("/reporteMovimientoArticulo")
	public void reporteMovimientoArticulo(HttpSession session, HttpServletRequest request, 
			HttpServletResponse response, Integer idArticulo, Integer movimiento, String desde, String hasta) throws JRException, SQLException, ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Articulo> articulos = new LinkedList<>();
		List<ArticuloAjuste> articulosAjuste = new LinkedList<>();
		List<InventarioArticulo> inventarioArticulos = new LinkedList<>();
		String tempMovimiento = "";
		
		if(idArticulo == 0) {
			articulos = serviceArticulos.buscarPorTienda(usuario.getAlmacen().getPropietario()).
					stream().filter(a -> a.getEliminado() == 0).collect(Collectors.toList());
		}else {
			articulos.add(serviceArticulos.buscarPorId(idArticulo));
		}
				
		if(movimiento == 0) {
			//Todos
			articulosAjuste = serviceArticulosAjustes.buscarPorAlmacenFechas(usuario.getAlmacen(),
					formato.parse(desde), formato.parse(hasta));
			tempMovimiento="TODOS";
		}else if(movimiento == 1) {
			tempMovimiento="Entrada";
			articulosAjuste = serviceArticulosAjustes.buscarPorAlmacenTipoMovimientoArticulosFechas(usuario.getAlmacen(),
					"entrada", articulos, formato.parse(desde), formato.parse(hasta));
		}else if(movimiento == 2) {
			tempMovimiento="Salida";
			articulosAjuste = serviceArticulosAjustes.buscarPorAlmacenTipoMovimientoArticulosFechas(usuario.getAlmacen(),
					"salida", articulos, formato.parse(desde), formato.parse(hasta));
		}
		
		for (ArticuloAjuste articuloAjuste : articulosAjuste) {
			InventarioArticulo inventarioArticulo = new InventarioArticulo();
			inventarioArticulo.setCantidad(articuloAjuste.getCantidad());
			inventarioArticulo.setFecha(formato.format(articuloAjuste.getFecha()));
			inventarioArticulo.setId(articuloAjuste.getId());
			inventarioArticulo.setMovimiento(articuloAjuste.getTipoMovimiento());
			inventarioArticulo.setTabla("articulos_ajuste");
			inventarioArticulo.setProcedencia(articuloAjuste.getProcedencia());
			inventarioArticulo.setArticulo(articuloAjuste.getArticulo().getNombre());
			inventarioArticulos.add(inventarioArticulo);
		}
		
		//Compilamos el reporte
		JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreportMovimientoArticulos);

		Map<String, Object> parameters = new HashMap<String, Object>();

		// convertimos la lista a JRBeanCollectionDataSource
		JRBeanCollectionDataSource movimientoArticuloReporteJRBean = new JRBeanCollectionDataSource(inventarioArticulos);

		parameters.put("idUsuario", usuario.getId());
		parameters.put("imagen", rutaImagenes + usuario.getAlmacen().getImagen());
		parameters.put("total", inventarioArticulos.size());
		parameters.put("fechaDesde", desde);
		parameters.put("fechaHasta", hasta);
		parameters.put("author", usuario.getUsername());
		parameters.put("articulo", idArticulo == 0 ? "TODOS" : articulos.get(0).getNombre());
		parameters.put("movimiento", tempMovimiento);
		parameters.put("reporteMovimientoArticulo", movimientoArticuloReporteJRBean);

		// Objeto de impresion jr
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

		String dataDirectory = tempFolder + pathSeparator + "movimientoArticulo" + usuario.getId() + ".pdf";

		tempFolder += pathSeparator;

		JasperExportManager.exportReportToPdfFile(jasperPrint, dataDirectory);

		Path file = Paths.get(tempFolder, "movimientoArticulo" + usuario.getId() + ".pdf");
		if (Files.exists(file)) {
			String mimeType = URLConnection
					.guessContentTypeFromName(tempFolder + "movimientoArticulo" + usuario.getId() + ".pdf");
			if (mimeType == null)
				mimeType = "application/octet-stream";
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition",
					String.format("inline; filename=\"" + "movimientoArticulo" + usuario.getId() + ".pdf" + "\""));
			try {
				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();
				Files.delete(file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	@GetMapping("/create")
	public String crear(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Categoria> categorias = serviceCategorias.buscarPorPropietario(usuario.getAlmacen().getPropietario());
		lista = serviceArticulos.buscarPorTienda(usuario.getAlmacen().getPropietario())
				.stream().filter(p -> p.getEliminado() == 0).collect(Collectors.toList());
		if(lista.isEmpty()) {
			lista = new LinkedList<>();
			Articulo newArticulo = new Articulo();
			newArticulo.setCodigo("1");
			model.addAttribute("categorias",categorias);
			model.addAttribute("articulos", lista);
			model.addAttribute("articulo", newArticulo);
		}else {
			Articulo articuloTemp = lista.get(lista.size()-1);
			Integer codigo = Integer.parseInt(articuloTemp.getCodigo());
			codigo++;
			Articulo newArticulo = new Articulo();
			newArticulo.setCodigo(String.valueOf(codigo));
			model.addAttribute("categorias",categorias);
			model.addAttribute("articulos", lista);
			model.addAttribute("articulo", newArticulo);
		}
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
			articulo.setImei(original.getImei());
		}
		
		articulo.setTienda(usuario.getAlmacen().getPropietario());
		serviceArticulos.save(articulo);
		if(method.equals("UPDATE")) {
			model.addAttribute("msg2", "Articulo modificado");
			attributes.addFlashAttribute("msg2", "Articulo modificado");
		}else {
			attributes.addFlashAttribute("msg", "Articulo creado");
		}
		return "redirect:/articulos/create";
	}
	
	@PostMapping("/sinSerial/save")
	public String saveArticulosSinSerial(ArticuloAjuste articuloAjuste, Model model, HttpSession session,
			RedirectAttributes attributes) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//Actualizamos el articulo
		articuloAjuste.setCosto(articuloAjuste.getArticulo().getCosto());
		Articulo articulo = serviceArticulos.buscarPorId(articuloAjuste.getArticulo().getId());
		int difPrecios = 0;
		//verificamos si hay diferencias en los montos para actualizarlos
		if(!articulo.getCosto().equals(articuloAjuste.getArticulo().getCosto())) {
			articulo.setCosto(articuloAjuste.getArticulo().getCosto());
			difPrecios++;
		}
		if(!articulo.getPrecio_maximo().equals(articuloAjuste.getArticulo().getPrecio_maximo())) {
			articulo.setPrecio_maximo(articuloAjuste.getArticulo().getPrecio_maximo());
			difPrecios++;
		}
		if(!articulo.getPrecio_minimo().equals(articuloAjuste.getArticulo().getPrecio_minimo())) {
			articulo.setPrecio_minimo(articuloAjuste.getArticulo().getPrecio_minimo());
			difPrecios++;
		}
		if(!articulo.getPrecio_mayor().equals(articuloAjuste.getArticulo().getPrecio_mayor())) {
			articulo.setPrecio_mayor(articuloAjuste.getArticulo().getPrecio_mayor());
			difPrecios++;
		}
		if(difPrecios>0) {
			serviceArticulos.guardar(articulo);
		}
		// verificar si el registro tiene inventario
		List<ArticuloAjuste> lista = serviceArticulosAjustes.buscarPorArticuloYAlmacen(articulo, usuario.getAlmacen());
		//Caso para entradas y salidas
		if(articuloAjuste.getTipoMovimiento().equalsIgnoreCase("entrada") || (articuloAjuste.getTipoMovimiento().equalsIgnoreCase("salida"))
				|| (articuloAjuste.getTipoMovimiento().equalsIgnoreCase("enviarTaller"))) {
			// Si no tiene registros se crea uno nuevo, de lo contrario, 
			//utilizamos los valores del inventario de articulo como referencia para el nuevo registro
			if(lista.isEmpty()) {
				if(!articuloAjuste.getTipoMovimiento().equalsIgnoreCase("enviarTaller")) {
					articuloAjuste.setExistencia(articuloAjuste.getCantidad());
					articuloAjuste.setDisponible(articuloAjuste.getCantidad());
					articuloAjuste.setFecha(new Date());
					articuloAjuste.setAlmacen(usuario.getAlmacen());
					articuloAjuste.setUsuario(usuario);
					articuloAjuste.setProcedencia("inventario");
					serviceArticulosAjustes.guardar(articuloAjuste);
					if(articuloAjuste.getId()!=null) {
						attributes.addFlashAttribute("msg", "Registro creado");
					}else {
						attributes.addFlashAttribute("msg3", "Registro no creado");
					}
				}else {
					//Enviar al taller
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
				    String now = LocalDateTime.now().format(formatter);
				    TallerArticulo tallerArticulo = new TallerArticulo();
				    tallerArticulo.setCantidad(articuloAjuste.getCantidad());
				    tallerArticulo.setArticulo(articuloAjuste.getArticulo());
					tallerArticulo.setFecha(new Date());
					tallerArticulo.setPrecio(articuloAjuste.getArticulo().getPrecio_mayor());
					tallerArticulo.setNombre(articulo.getNombre());
					tallerArticulo.setHora(now);
					tallerArticulo.setUsuario(usuario);
					tallerArticulo.setAlmacen(usuario.getAlmacen());
					tallerArticulo.setCosto(articuloAjuste.getCosto());
					serviceTalleresArticulos.guardar(tallerArticulo);
					if(tallerArticulo.getId()!=null) {
						articuloAjuste.setExistencia(0);
						articuloAjuste.setDisponible(0);
						articuloAjuste.setCantidad(articuloAjuste.getCantidad());
						articuloAjuste.setTipoMovimiento(articuloAjuste.getTipoMovimiento());
						articuloAjuste.setFecha(new Date());
						articuloAjuste.setAlmacen(usuario.getAlmacen());
						articuloAjuste.setUsuario(usuario);
						articuloAjuste.setProcedencia("inventario");
						serviceArticulosAjustes.guardar(articuloAjuste);
						if(articuloAjuste.getId()!=null) {
							attributes.addFlashAttribute("msg", "Registro creado");
						}else {
							attributes.addFlashAttribute("msg3", "Registro no creado");
						}
					}
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
				newArticuloAjusteDefinitive.setProcedencia("inventario");
				
				//verificamos si el tipo de movimiento es salida y la cantidad a retirar es mayor al inventario
				if (newArticuloAjusteDefinitive.getTipoMovimiento().equalsIgnoreCase("salida")) {
					if(newArticuloAjuste.getDisponible() >= articuloAjuste.getCantidad()) {
						newArticuloAjusteDefinitive.setDisponible(newArticuloAjusteDefinitive.getExistencia()-articuloAjuste.getCantidad());
					}else {
						attributes.addFlashAttribute("msg4", "Registro no creado");
						return "redirect:/articulos/";
					}
				}else if (newArticuloAjusteDefinitive.getTipoMovimiento().equalsIgnoreCase("entrada")) {
					//entradas con registros anteriores
					newArticuloAjusteDefinitive.setDisponible(newArticuloAjusteDefinitive.getExistencia()+newArticuloAjusteDefinitive.getCantidad());	
				}else {
					//Enviar al taller
					newArticuloAjusteDefinitive.setDisponible(newArticuloAjusteDefinitive.getExistencia());
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
				    String now = LocalDateTime.now().format(formatter);
				    TallerArticulo tallerArticulo = new TallerArticulo();
				    tallerArticulo.setCantidad(articuloAjuste.getCantidad());
				    tallerArticulo.setArticulo(newArticuloAjusteDefinitive.getArticulo());
					tallerArticulo.setFecha(new Date());
					tallerArticulo.setPrecio(newArticuloAjusteDefinitive.getArticulo().getPrecio_mayor());
					tallerArticulo.setNombre(newArticuloAjusteDefinitive.getArticulo().getNombre());
					tallerArticulo.setHora(now);
					tallerArticulo.setUsuario(usuario);
					tallerArticulo.setAlmacen(usuario.getAlmacen());
					serviceTalleresArticulos.guardar(tallerArticulo);
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
		if(articulo.getImei().equals("0")){
			articulo.setImei("NO");
		}
		if(articulo.getImei().equals("1")){
			articulo.setImei("SI");
		}
		model.addAttribute("categorias",categorias);
		model.addAttribute("articulos", lista);
		model.addAttribute("articulo", articulo);
		return "articulos/formularioEdit";
	}
	
	@SuppressWarnings("unused")
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable(name = "id") Integer idArticulo, Model model, HttpSession session, RedirectAttributes attributes) {
		Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//Buscamos los almacenes asociados al articulo
		Query nativeQuery = em.createNativeQuery("SELECT id_almacen FROM articulos_ajuste a " + 
				"WHERE a.id_articulo =" + articulo.getId() + 
				" GROUP BY id_almacen");
		@SuppressWarnings("unchecked")
		List<Integer> results = nativeQuery.getResultList();
		
		int cant = 0;
		
		if(!results.isEmpty()) {
			for (Integer objects : results) {
				Almacen almacen = serviceAlmacenes.buscarPorId(objects);
				//si tiene no tiene serial
				if(articulo.getImei().equalsIgnoreCase("0") || articulo.getImei().equalsIgnoreCase("NO")) {
					List<ArticuloAjuste> articulosAjustes = serviceArticulosAjustes.buscarPorArticuloYAlmacen(articulo, almacen);
					if(articulosAjustes.isEmpty()) {
						cant = 0;
					}else {
						ArticuloAjuste newArticuloAjuste = articulosAjustes.get(articulosAjustes.size()-1);
//						articulo.setCantidad(newArticuloAjuste.getDisponible());
						cant = newArticuloAjuste.getDisponible();
						if(cant>0) {
							break;
						}
					}
				}else {
					//Validacion para articulos con serial
					List<ArticuloSerial> articuloSerials = serviceArticulosSeriales
							.buscarPorArticuloAlmacen(articulo, almacen).stream()
							.filter(s -> s.getEstado().equalsIgnoreCase("Disponible"))
							.collect(Collectors.toList());
					if(!articuloSerials.isEmpty()) {
						cant++;
						break;
					}
					for (ArticuloSerial articuloSerial : articuloSerials) {
						//buscamos el articulo
						if(articuloSerial.getArticulo().getId() == articulo.getId()) {
							articulo.setPrecio_maximo(articuloSerial.getPrecio_maximo());
							articulo.setPrecio_mayor(articuloSerial.getPrecio_mayor());
							articulo.setPrecio_minimo(articuloSerial.getPrecio_minimo());
						}
					}
				}
			}
		}
		
		if(cant>0) {
			attributes.addFlashAttribute("msg7", "No se puede eliminar");
			return "redirect:/articulos/";
		}
		
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
		
		//setiamos precios de referencia tomando en cuenta el ultimo serial agregado del producto
		List<ArticuloSerial> seriales = serviceArticulosSeriales.buscarPorArticuloAlmacen(articulo, usuario.getAlmacen()).
			stream().filter(s -> s.getEstado().equalsIgnoreCase("disponible")).collect(Collectors.toList());
		
		if(!seriales.isEmpty()) {
			//obtenemos el ultimo registro
			ArticuloSerial  serialTemp = seriales.get(seriales.size()-1);
			articuloSerial.setPrecio_maximo(serialTemp.getPrecio_maximo());
			articuloSerial.setPrecio_minimo(serialTemp.getPrecio_minimo());
			articuloSerial.setPrecio_mayor(serialTemp.getPrecio_mayor());
		}
		
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
			//Buscamos los seriales que pertenezcan al articulo, por almacen que no esten eliminados y esten disponibles y no esten en uso
			lista = serviceArticulosSeriales.buscarPorArticuloAlmacen(articulo, usuario.getAlmacen()).
					stream().filter(s -> 
					(s.getEliminado() == 0 && s.getEstado().equalsIgnoreCase("Disponible") && s.getEn_uso() == 0)).
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
		model.addAttribute("tipoArticulo", articulo.getImei());
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
		return "facturas/factura :: #precioSinSerial";
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
		return "facturas/factura :: #precioSinSerial";
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
		double itBis = 0.0;
		double precioUnitario = precio;
		FacturaServicioTemp servicioFac = new FacturaServicioTemp();
		servicioFac.setUsuario(usuario);
		servicioFac.setAlmacen(usuario.getAlmacen());
		servicioFac.setDescripcion(descripcion);
		servicioFac.setCosto(costo);
		servicioFac.setCantidad(cantidad);
		servicioFac.setInitialPrice(precioUnitario);
		
		//Verificamos si el comprobante fiscal paga itbis
		if (factura.getComprobanteFiscal().getPaga_itbis() == 1) {
			// verificamos si el comprobante fiscal incluye el itbis en el precio
			if (factura.getComprobanteFiscal().getIncluye_itbis() == 1) {
				// realizamos las conversiones
				String temp = "1." + factura.getComprobanteFiscal().getValor_itbis().intValue();
				precio = formato2d(precioUnitario / Double.parseDouble(temp));
				itBis = formato2d(precioUnitario - precio);
			} else {
				double valorItbis = factura.getComprobanteFiscal().getValor_itbis().intValue();
				itBis= formato2d(precioUnitario * valorItbis/100); 
			}
		}else {
			itBis = 0.0;
			precio = precioUnitario;
		}
				
		servicioFac.setPrecio(precio);
		servicioFac.setItbis(itBis);
		servicioFac.setSubtotal((cantidad * precio) + (cantidad * itBis));
		servicioFac.setComprobanteFiscal(factura.getComprobanteFiscal());
		serviceServiciosTemp.guardar(servicioFac);
		return "facturas/factura :: #responseAddService";
	}
	
	@PostMapping("/ajax/addArticuloSinSerial/")	
	public String agregarArticuloSinSerial(HttpSession session, @RequestParam("idArticulo") Integer idArticulo,
			@RequestParam("cantidad") Integer cantidad, @RequestParam("precioAcct") Double precio,
			@RequestParam("conItbis") String conItbis, @RequestParam("disponible") Integer disponible,
			@RequestParam("maximo") Double maximo, @RequestParam("valorItbis") Double valorItbis, 
			@RequestParam("initialPrice") Double initialPrice, @RequestParam("incluyeItbis") Integer incluyeItbis, @RequestParam("realPrice") Double realPrice) {
		Double itBis = 0.0;
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		double precioUnitario = realPrice/cantidad;
		
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
				
		//verificamos si el articulo tiene itbis
		if(articulo.getItbis().equalsIgnoreCase("SI")) {
			if(conItbis.equals("SI")) {
				//verificamos si incluye itbis en el precio
				if(incluyeItbis == 1) {
//					//incluye itbis
					String temp = "1."+valorItbis.intValue();
					precio = formato2d(precioUnitario / Double.parseDouble(temp));
					itBis = formato2d(precioUnitario - precio);
				}else {
					itBis= formato2d(precioUnitario * valorItbis/100); 
				}
			}
		}else {
			itBis = 0.0;
			precio = initialPrice;
		}
			
		facturaTemp.setPrecio(precio);
		facturaTemp.setExistencia(disponible);
		facturaTemp.setItbis(itBis);
		facturaTemp.setPrecio_maximo(maximo);
		facturaTemp.setSubtotal((cantidad * precio) + (cantidad * itBis));
		facturaTemp.setComprobanteFiscal(factura.getComprobanteFiscal());
		facturaTemp.setInitialPrice(initialPrice);
		serviceFacturasDetallesTemp.guardar(facturaTemp);
		return "facturas/factura :: #responseAddArticuloSinSerial";
	}
	
	@PostMapping("/ajax/addArticuloConSerial/")	
	public String agregarArticuloConSerial(HttpSession session, @RequestParam("idArticulo") Integer idArticulo,
			@RequestParam("cantidad") Integer cantidad, @RequestParam("precio") Double precio,
			@RequestParam("seriales") String seriales, @RequestParam("idCliente") Integer idCliente,
			@RequestParam("comprobanteFiscalId") Integer comprobanteFiscalId, @RequestParam(name = "initialPrice",required = false) Double initialPrice, 
			@RequestParam("realPrice") Double realPrice, String columnas) {
		Double itBis = 0.0;
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
					int cant = 1; //Se toma unitario
					FacturaDetalleTemp facturaTemp = new FacturaDetalleTemp();
					facturaTemp.setArticulo(articulo);
					facturaTemp.setUsuario(usuario);
					facturaTemp.setAlmacen(usuario.getAlmacen());
					facturaTemp.setCantidad(cant);
					facturaTemp.setConItbis(articulo.getItbis().equals("SI")?"1":"0");
					facturaTemp.setImei(articulo.getImei().equals("SI")?"1":"0");
					
					initialPrice = listSerial.getPrecio();

					precio = listSerial.getPrecio();
					double precioUnitario = listSerial.getPrecio();
					
					//verificamos si el articulo tiene itbis
					if(articulo.getItbis().equals("SI")) {
						if(comprobanteFiscal.getPaga_itbis()==1) {
							//No incluye itbis en el precio
							if(comprobanteFiscal.getIncluye_itbis()==0) { 
								itBis= formato2d(precio * (comprobanteFiscal.getValor_itbis()/100.00)); 
							}else {
								//incluye itbis 
								String temp = "1."+comprobanteFiscal.getValor_itbis().intValue();
								precio = formato2d(precioUnitario / Double.parseDouble(temp));
								itBis = formato2d(precioUnitario - precio);
							}
						}
					}else {
						itBis = 0.0;
						precio = initialPrice;
					}
					
					facturaTemp.setPrecio(precio);
					facturaTemp.setExistencia(cant);
					facturaTemp.setItbis(itBis);
					facturaTemp.setPrecio_maximo(articulo.getPrecio_maximo());
					facturaTemp.setSubtotal((cant * precio) + (cant * itBis));
					facturaTemp.setComprobanteFiscal(factura.getComprobanteFiscal());
					facturaTemp.setInitialPrice(initialPrice);
					serviceFacturasDetallesTemp.guardar(facturaTemp);
					
					FacturaSerialTemp serialTemp = new FacturaSerialTemp();
					serialTemp.setId_serial(listSerial.getSerial());
					serialTemp.setIdDetalle(facturaTemp);
					serialTemp.setFacturaTemp(factura);
					//verificamos el serial del articulo
					List<ArticuloSerial> serialesDelArticulo = serviceArticulosSeriales.buscarPorSerialAndAlmacen(String.valueOf(listSerial.getSerial()), usuario.getAlmacen());
					if(!serialesDelArticulo.isEmpty()) {
						serialTemp.setArticuloSerial(serialesDelArticulo.get(0));
						serialesDelArticulo.get(0).setEn_uso(1);
						serviceArticulosSeriales.guardar(serialesDelArticulo.get(0));
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
				double precioUnitario = precio/cantidad;
				
				//verificamos si el articulo tiene itbis
				if(articulo.getItbis().equals("SI")) {
					initialPrice = initialPrice/cantidad;
					if(comprobanteFiscal.getPaga_itbis()==1) {
						//No incluye itbis en el precio
						if(comprobanteFiscal.getIncluye_itbis()==0) {
							itBis= formato2d(precioUnitario * (comprobanteFiscal.getValor_itbis()/100.00)); 
							precio = precioUnitario;
						}else {
							//incluye itbis
							String temp = "1."+comprobanteFiscal.getValor_itbis().intValue();
							precio = formato2d(precioUnitario / Double.parseDouble(temp));
							itBis = formato2d(precioUnitario - precio);
						}
					}
				}else {
					itBis = 0.0;
					precio = initialPrice/cantidad;
				}
				
				facturaTemp.setPrecio(precio);
				facturaTemp.setExistencia(cantidad);
				facturaTemp.setItbis(itBis);
				facturaTemp.setPrecio_maximo(articulo.getPrecio_maximo());
				facturaTemp.setSubtotal((cantidad * precio) + (cantidad * itBis));
				facturaTemp.setComprobanteFiscal(factura.getComprobanteFiscal());
				facturaTemp.setInitialPrice(initialPrice);
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
							serialesDelArticulo.get(0).setEn_uso(1);
							serviceArticulosSeriales.guardar(serialesDelArticulo.get(0));
						}
						serviceSerialesTemp.guardar(serialTemp);
					}
				}
			
			}
			
		}else {
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
						double precioUnitario = precio/cantidad;
						itBis= formato2d(precioUnitario * (comprobanteFiscal.getValor_itbis()/100.00)); 
						precio = precioUnitario;
					}else {
						
						double precioUnitario = precio;
						//incluye itbis
						String temp = "1."+comprobanteFiscal.getValor_itbis().intValue();
						precio = formato2d(precioUnitario / Double.parseDouble(temp));
						itBis = formato2d(precioUnitario - precio);
					}
				}
			}else {
				itBis = 0.0;
				precio = initialPrice;
			}
			
			facturaTemp.setPrecio(precio);
			facturaTemp.setExistencia(cantidad);
			facturaTemp.setItbis(itBis);
			facturaTemp.setPrecio_maximo(articulo.getPrecio_maximo());
			facturaTemp.setSubtotal((cantidad * precio) + (cantidad * itBis));
			facturaTemp.setComprobanteFiscal(factura.getComprobanteFiscal());
			facturaTemp.setInitialPrice(initialPrice);
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
						serialesDelArticulo.get(0).setEn_uso(1);
						serviceArticulosSeriales.guardar(serialesDelArticulo.get(0));
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
			@RequestParam("precio") Double precio, @RequestParam("realPrice") Double realPrice,
			@RequestParam("idCliente") Integer idCliente) {
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
	public String verificarPreciosArticuloConSerialNotMinimo(Model model, HttpSession session,
			@RequestParam("idArticulo") Integer idArticulo, @RequestParam("cantidad") Integer cantidad,
			@RequestParam("seriales") String seriales, @RequestParam("precio") Double precio) {
		 
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

		//Buscamos los talleres en la factura
		List<FacturaTallerTemp> facturaTalleresTemp = serviceFacturasTalleresTemp.buscarPorFacturaTemp(facturaTemp);
		
		Double total = 0.0;
		Double cantXPrice = 0.0;
		Double cantXItBis = 0.0;
		//Calculamos el total
		for (FacturaDetalleTemp facturaDetalleTemp : facturaDetallesTemp) {
			cantXPrice+=facturaDetalleTemp.getCantidad()*facturaDetalleTemp.getPrecio();
			cantXItBis+=facturaDetalleTemp.getCantidad()*facturaDetalleTemp.getItbis();
			total+=facturaDetalleTemp.getSubtotal();
		}
		
		//Validaciones para servicios
		for (FacturaServicioTemp facturaServicioTemp : facturaServiciosTemp) {
			cantXPrice+=facturaServicioTemp.getCantidad()*facturaServicioTemp.getPrecio();
			cantXItBis+=facturaServicioTemp.getCantidad()*facturaServicioTemp.getItbis();
			total+=facturaServicioTemp.getSubtotal();
		}
		
		for (FacturaTallerTemp facturaTallerTemp : facturaTalleresTemp) {
			cantXPrice+=facturaTallerTemp.getCantidad()*facturaTallerTemp.getPrecio();
			cantXItBis+=facturaTallerTemp.getCantidad()*facturaTallerTemp.getItbis();
			total+=facturaTallerTemp.getSubtotal();
		}
		
		model.addAttribute("facturaTalleres", facturaTalleresTemp);
		model.addAttribute("facturaDetalles", facturaDetallesTemp);
		model.addAttribute("facturaServicios", facturaServiciosTemp);
		model.addAttribute("subTotalItbis",formato2d(cantXPrice));
		model.addAttribute("subTotal2",formato2d(cantXItBis));
		model.addAttribute("total", formato2d(total));
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
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaDetalleTemp detalleTemp = serviceFacturasDetallesTemp.buscarPorId(idDetalle);
		//validacion para articulos con imei
		Articulo articulo = detalleTemp.getArticulo();
		if(articulo.getImei().equalsIgnoreCase("SI")) {
			//obtenemos los seriales asociados al articulo en la factura
			List<FacturaSerialTemp> seriales = serviceSerialesTemp.buscarPorDetalleTemp(detalleTemp);
			//recorremos todos los seriales
			for (FacturaSerialTemp facturaSerialTemp : seriales) {
				//verificamos el serial del articulo
				List<ArticuloSerial> serialesDelArticulo = serviceArticulosSeriales.buscarPorSerialAndAlmacen(String.valueOf(facturaSerialTemp.getId_serial()), usuario.getAlmacen());
				if(!serialesDelArticulo.isEmpty()) {
					ArticuloSerial temp = serialesDelArticulo.get(0);
					temp.setEn_uso(0);
					serviceArticulosSeriales.guardar(temp);
				}
			}
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
		if(serialesArray.length == 1 && serialesArray[0].equals("")) {
			precio = 0.0;
		}else {
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
		}
		model.addAttribute("precioConSerial", precio);
		model.addAttribute("cantidadConSerial", serialesArray.length);
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
	
	@GetMapping("/ajax/getVendedorAcct/")
	public String obtenerVendedor(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaTemp factura = serviceFacturasTemp.buscarPorUsuario(usuario);
		if(factura.getVendedor()!=null) {
			model.addAttribute("actualVendedor", factura.getVendedor().getId());
		}else {
			model.addAttribute("actualVendedor", "0");
		}
		return "facturas/factura :: #vendedorAcct";
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
	
	@GetMapping("/reporte")
	public String formularioReporteArticulo(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//Articulos que no esten eliminados
		List<Articulo> articulos = serviceArticulos.buscarPorTienda(usuario.getAlmacen().getPropietario()).
				stream().filter(a -> a.getEliminado() == 0).collect(Collectors.toList());
		List<Categoria> categorias = serviceCategorias.buscarPorPropietario(usuario.getAlmacen().getPropietario());
		model.addAttribute("categorias", categorias);
		model.addAttribute("articulos", articulos);
		return "articulos/generarReporteArticulos";
	}
	
	@PostMapping("/generarReporte")
	public void reporteArticulos(HttpSession session, HttpServletRequest request, 
			HttpServletResponse response, @RequestParam(name = "idArticulo") Integer idArticulo, 
			@RequestParam(name = "idCategoria") Integer idCategoria, @RequestParam(name = "tipoArticulo") Integer tipoArticulo,
			@RequestParam(name = "gama") Integer gama, @RequestParam(name = "conItbis") Integer conItbis,
			@RequestParam(name = "tipoPrecio") Integer tipoPrecio, @RequestParam(name = "verEliminados") Integer verEliminados,
			@RequestParam(name = "ordenar") Integer ordenar, @RequestParam(name = "tipoOrden") Integer tipoOrden
//			,@RequestParam(name = "verCosto") Integer verCosto, @RequestParam(name = "verPrecio") Integer verPrecio,
//			@RequestParam(name = "verExistencia") Integer verExistencia
			) throws JRException, SQLException {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		
		List<Categoria> categorias = new LinkedList<>();
		List<Articulo> articulos = new LinkedList<>();
		List<ArticuloReporte> articulosReporte = new LinkedList<>();
		
		Integer verCosto = 1;
		
		String tempTipoPrecio = "";
		String categoriasIds = "";
		String articulosIds = "";
		String tempSerial = "";
		String tempGama = "";
		String tempConItbis = "";
		String tempOrderBy = "";
		String tempCategoria = "";
				
		//Validacion de orden
		if(ordenar == 1) {
			tempOrderBy = "nombre";
		} else if (ordenar == 2) {
			tempOrderBy = "codigo";
		} else if (ordenar == 3) {
			tempOrderBy = "nombreCategoria";
		}
		
		//Validacion de con itbis
		if (conItbis == 0) {
			tempConItbis = "TODOS";
		} else if (conItbis == 1) {
			tempConItbis = "'1','SI'";
		} else if (conItbis == 2) {
			tempConItbis = "'0','NO'";
		}
			
		//Validacion de gama
		if (gama == 0) {
			tempGama = "TODOS";
		} else if (gama == 1) {
			tempGama = "'1','ALTA'";
		} else if (gama == 2) {
			tempGama = "'2','MEDIA'";
		} else {
			tempGama = "'3','BAJA'";
		}

		//Validacion tipo de precio
		if(tipoPrecio == 1) {
			tempTipoPrecio = "maximo";
		}else if(tipoPrecio == 2) {
			tempTipoPrecio = "minimo";
		}else {
			tempTipoPrecio = "mayor";
		}
		
		//Validacion de seriales
		if(tipoArticulo == 0) {
			tempSerial="TODOS";
		}else if(tipoArticulo == 1) {
			tempSerial="'1','SI'";
		}else {
			tempSerial="'0','NO'";
		}
		
		//Validacion de articulos
		if(idArticulo == 0) {
			if(verEliminados == 1) {
				articulos = serviceArticulos.buscarPorTienda(usuario.getAlmacen().getPropietario());
			}else {
				articulos = serviceArticulos.buscarPorTienda(usuario.getAlmacen().getPropietario())
						.stream().filter(p -> p.getEliminado() == 0).collect(Collectors.toList());
			}
			for (Articulo articulo : articulos) {
				articulosIds+=articulo.getId().toString()+",";
			}
			articulosIds = articulosIds.substring(0, articulosIds.length() - 1);
		}else {
			articulos.add(serviceArticulos.buscarPorId(idArticulo));
			articulosIds+=articulos.get(0).getId();
		}
		
		//Validacion de categorias
		if(idCategoria == 0) {
			 categorias = serviceCategorias.buscarPorPropietario(usuario.getAlmacen().getPropietario());
			 for (Categoria categoria : categorias) {
				 categoriasIds+=categoria.getId().toString()+",";
			 }
			 categoriasIds = categoriasIds.substring(0, categoriasIds.length() - 1);
			 tempCategoria = "TODAS";
		}else {
			categorias.add(serviceCategorias.buscarPorId(idCategoria));
			categoriasIds = categorias.get(0).getId().toString();
			tempCategoria = categorias.get(0).getNombre();
		}
		
		StringBuilder query = new StringBuilder("SELECT ");
		query.append("a.id_articulo, a.codigo, a.nombre, a.costo, c.nombre AS nombreCategoria, ")
		.append("precio_").append(tempTipoPrecio).append(", a.imei ")
		.append("FROM articulos a ")
		.append("INNER JOIN propietario p ON p.id_tienda = a.id_tienda ")
		.append("INNER JOIN categorias c ON c.id_categoria = a.id_categoria ")
		.append("WHERE ").append("a.id_tienda = ").append(usuario.getAlmacen().getPropietario().getId()).append(" ")
		.append("AND a.id_categoria IN (").append(categoriasIds).append(") ")
		.append("AND a.id_articulo IN (").append(articulosIds).append(") ");
		if(!tempSerial.equals("TODOS")) { 
			query.append("AND a.imei IN (").append(tempSerial).append(") ");
		}
		if(!tempGama.equals("TODOS")) { 
			query.append("AND a.gama IN (").append(tempGama).append(") ");
		}
		if(!tempConItbis.equals("TODOS")) { 
			query.append("AND a.itbis IN (").append(tempConItbis).append(") ");
		}
		query.append("ORDER BY ").append(tempOrderBy).append(" ")
		.append(tipoOrden==1?"ASC":"DESC");

		Query nativeQuery = em.createNativeQuery(query.toString());
		@SuppressWarnings("unchecked")
		List<Object[]> results = nativeQuery.getResultList();
		
		for (Object[] objects : results) {
			ArticuloReporte articuloReporte = new ArticuloReporte();
			articuloReporte.setVercosto(verCosto);
			articuloReporte.setCategoria(objects[4].toString());
			articuloReporte.setCodigo(objects[1].toString());
			articuloReporte.setConImei(objects[6].toString());
			articuloReporte.setCosto(Double.parseDouble(objects[3].toString()));
			articuloReporte.setIdArticulo(Integer.parseInt(objects[0].toString()));
			articuloReporte.setNombre(objects[2].toString());
			articuloReporte.setPrecio(objects[5]!=null?Double.parseDouble(objects[5].toString()):0.0);
			//determinamos el tipo de equipo
			if(objects[6].toString().equalsIgnoreCase("NO") || objects[6].toString().equalsIgnoreCase("0")) {
				//Sin serial
				StringBuilder queryInvSinSerial = new StringBuilder("SELECT existencia_disponible ");
				queryInvSinSerial.append("FROM articulos_ajuste a ")
				.append("WHERE ")
				.append("id_articulo = ").append(objects[0].toString()).append(" AND ")
				.append("id_almacen = ").append(usuario.getAlmacen().getId()).append(" ")
				.append("ORDER BY id_ajuste DESC LIMIT 1");
				
				Query nativeQuery2 = em.createNativeQuery(queryInvSinSerial.toString());
				@SuppressWarnings("unchecked")
				List<Integer> results2 = nativeQuery2.getResultList();
				for (Integer objects2 : results2) {
					articuloReporte.setDisponible(objects2);
				}
				articulosReporte.add(articuloReporte);
			}else {
				//Con serial
				StringBuilder queryInvConSerial = new StringBuilder("SELECT id_articulo, COUNT(estado) AS total, estado ");
				queryInvConSerial.append("FROM articulos_seriales ")
				.append("WHERE ")
				.append("id_almacen = ").append(usuario.getAlmacen().getId()).append(" AND ")
				.append("id_articulo = ").append(objects[0].toString()).append(" ")
				.append("GROUP BY estado, id_articulo ")
				.append("HAVING estado = 'Disponible'");
				
				Query nativeQuery2 = em.createNativeQuery(queryInvConSerial.toString());
				@SuppressWarnings("unchecked")
				List<Object[]> results2 = nativeQuery2.getResultList();
				for (Object[] objects2 : results2) {
					articuloReporte.setDisponible(Integer.parseInt(objects2[1].toString()));
				}
				articulosReporte.add(articuloReporte);
			}
		}
		
		//Compilamos el reporte
		JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreport);
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		//convertimos la lista a JRBeanCollectionDataSource
		JRBeanCollectionDataSource articulosReporteJRBean = new JRBeanCollectionDataSource(articulosReporte);
		
		parameters.put("idUsuario", usuario.getId());
		parameters.put("imagen", rutaImagenes+usuario.getAlmacen().getImagen());
		parameters.put("articulosReporte", articulosReporteJRBean);
		parameters.put("total", articulosReporte.size());
		parameters.put("fecha", formato.format(new Date()));
		parameters.put("author", usuario.getUsername());
		parameters.put("categoria", tempCategoria);
		
		//Objeto de impresion jr
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());
	
		String dataDirectory = tempFolder + pathSeparator + "reporteArticulos"+usuario.getId()+".pdf";
		
		tempFolder += pathSeparator;
		
		JasperExportManager.exportReportToPdfFile(jasperPrint,dataDirectory);
        
        Path file = Paths.get(tempFolder, "reporteArticulos"+usuario.getId()+".pdf");
        if (Files.exists(file)) 
        {
            String mimeType = URLConnection.guessContentTypeFromName(tempFolder+"reporteArticulos"+usuario.getId()+".pdf");
            if (mimeType == null) mimeType = "application/octet-stream";
            response.setContentType(mimeType);
            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + "reporteArticulos"+usuario.getId()+".pdf" + "\""));
            try
            {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
				Files.delete(file);
            } 
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
	}
	
	@GetMapping("/reporteValorInventario")
	public String formularioReporteValorInventario(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Categoria> categorias = serviceCategorias.buscarPorPropietario(usuario.getAlmacen().getPropietario());
		model.addAttribute("categorias", categorias);
		return "articulos/generarReporteValorInventario";
	}
	
	@PostMapping("/generarValorInventario")
	public void valorInventario(HttpSession session, HttpServletRequest request, 
			HttpServletResponse response, Integer idCategoria) throws JRException, SQLException {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Categoria categoria = null;
		List<Articulo> articulos = null;
		List<ArticuloReporte> articulosReporte = new LinkedList<>();
		List<ArticuloReporte> articulosReporteSeriales = new LinkedList<>();
		if(idCategoria == 0) {
			articulos = serviceArticulos.buscarPorTienda(usuario.getAlmacen().getPropietario()).stream()
					.filter(a -> a.getEliminado() == 0).collect(Collectors.toList());
		}else {
			categoria = serviceCategorias.buscarPorId(idCategoria);
			articulos = serviceArticulos.buscarPorCategoria(categoria).stream()
					.filter(a -> a.getEliminado() == 0).collect(Collectors.toList());;
		}
		
		for (Articulo articulo : articulos) {
			if(articulo.getImei().equalsIgnoreCase("NO") || articulo.getImei().equalsIgnoreCase("0")) {
				//Sin serial
				ArticuloReporte articuloReporte = new ArticuloReporte();
				List<ArticuloAjuste> articulosAjustes = serviceArticulosAjustes.buscarPorArticuloYAlmacen(articulo, usuario.getAlmacen());
				if(articulosAjustes.isEmpty()) {
					articulo.setCantidad(0);
				}else {
					ArticuloAjuste newArticuloAjuste = articulosAjustes.get(articulosAjustes.size()-1);
					articulo.setCantidad(newArticuloAjuste.getDisponible());
				}
				articuloReporte.setCategoria(articulo.getCategoria().getNombre());
				articuloReporte.setCodigo(articulo.getCodigo());
				articuloReporte.setConImei("NO");
				articuloReporte.setCosto(articulo.getCosto());
				articuloReporte.setDisponible(articulo.getCantidad());
				articuloReporte.setIdArticulo(articulo.getId());
				articuloReporte.setNombre(articulo.getNombre());
				articuloReporte.setPrecio(articulo.getPrecio_maximo());
				articuloReporte.setTotal(articulo.getCantidad() * articulo.getCosto());
				articulosReporte.add(articuloReporte);
			}else {
				int count = 0;
				List<ArticuloSerial> articulosSeriales = serviceArticulosSeriales
						.buscarPorArticuloAlmacen(articulo, usuario.getAlmacen()).stream()
						.filter(s -> s.getEstado().equalsIgnoreCase("Disponible"))
						.collect(Collectors.toList());
				if(!articulosSeriales.isEmpty()) {
					articulo.setCantidad(articulosSeriales.size());
				}else {
					articulo.setCantidad(0);
				}
				for (ArticuloSerial articuloSerial : articulosSeriales) {
					if(count == 0) {
						ArticuloReporte articuloReporte = new ArticuloReporte();
						articuloReporte.setCategoria(articulo.getCategoria().getNombre());
						articuloReporte.setCodigo(articulo.getCodigo());
						articuloReporte.setConImei("SI");
						articuloReporte.setCosto(articuloSerial.getCosto());
						articuloReporte.setDisponible(articulo.getCantidad());
						articuloReporte.setIdArticulo(articulo.getId());
						articuloReporte.setNombre(articulo.getNombre());
						articuloReporte.setPrecio(articulo.getPrecio_maximo());
						articuloReporte.setTotal(articulo.getCantidad() * articuloSerial.getCosto());
						articuloReporte.setSerial(articuloSerial.getSerial());
						articulosReporteSeriales.add(articuloReporte);
					}else {
						int coincidencia = 0;
						for (ArticuloReporte articuloReporteSerial : articulosReporteSeriales) {
							//Verificamos articulos de igual nombre e igual costo
							if (articuloReporteSerial.getCosto().doubleValue() == articuloSerial.getCosto().doubleValue()) {
								articuloReporteSerial.setSerial(
										articuloReporteSerial.getSerial() + "," + articuloSerial.getSerial());
								coincidencia++;
							}
						}
						
						if(coincidencia==0 && articulosReporteSeriales.size()>1) {
							ArticuloReporte articuloReporte = new ArticuloReporte();
							articuloReporte.setCategoria(articulo.getCategoria().getNombre());
							articuloReporte.setCodigo(articulo.getCodigo());
							articuloReporte.setConImei("SI");
							articuloReporte.setCosto(articuloSerial.getCosto());
							articuloReporte.setDisponible(articulo.getCantidad());
							articuloReporte.setIdArticulo(articulo.getId());
							articuloReporte.setNombre(articulo.getNombre());
							articuloReporte.setPrecio(articulo.getPrecio_maximo());
							articuloReporte.setTotal(articulo.getCantidad() * articuloSerial.getCosto());
							articuloReporte.setSerial(articuloSerial.getSerial());
							articulosReporteSeriales.add(articuloReporte);
						}
					}
					count++;
				}
			}
		}
		
		for (ArticuloReporte articuloReporteSerial : articulosReporteSeriales) {
			String[] tempSerials = articuloReporteSerial.getSerial().split(",");
			articuloReporteSerial.setDisponible(tempSerials.length);
			articuloReporteSerial.setTotal(articuloReporteSerial.getDisponible()*articuloReporteSerial.getCosto());
			articuloReporteSerial.setNombre(articuloReporteSerial.getNombre()+" - "+articuloReporteSerial.getSerial());
			articulosReporte.add(articuloReporteSerial);
		}
		
		//Compilamos el reporte
		JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreportValorInventario);
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		//convertimos la lista a JRBeanCollectionDataSource
		JRBeanCollectionDataSource articulosReporteJRBean = new JRBeanCollectionDataSource(articulosReporte);
		
		parameters.put("idUsuario", usuario.getId());
		parameters.put("imagen", rutaImagenes+usuario.getAlmacen().getImagen());
		parameters.put("articulosReporte", articulosReporteJRBean);
		parameters.put("total", articulosReporte.size());
		parameters.put("fecha", formato.format(new Date()));
		parameters.put("author", usuario.getUsername());
		parameters.put("categoria", idCategoria==0?"TODAS":categoria.getNombre());
		
		//Objeto de impresion jr
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());
	
		String dataDirectory = tempFolder + pathSeparator + "reporteValorInventario"+usuario.getId()+".pdf";
		
		tempFolder += pathSeparator;
		
		JasperExportManager.exportReportToPdfFile(jasperPrint,dataDirectory);
        
        Path file = Paths.get(tempFolder, "reporteValorInventario"+usuario.getId()+".pdf");
        if (Files.exists(file)) 
        {
            String mimeType = URLConnection.guessContentTypeFromName(tempFolder+"reporteValorInventario"+usuario.getId()+".pdf");
            if (mimeType == null) mimeType = "application/octet-stream";
            response.setContentType(mimeType);
            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + "reporteValorInventario"+usuario.getId()+".pdf" + "\""));
            try
            {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
				Files.delete(file);
            } 
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
		
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
		
	@ModelAttribute
	public void setGenericos(Model model, HttpSession session) {
		if(session.getAttribute("almacen") != null) {
			model.addAttribute("almacen", (Almacen) session.getAttribute("almacen"));
			model.addAttribute("propietario", (Propietario) session.getAttribute("propietario"));
		}
	}
	
}
