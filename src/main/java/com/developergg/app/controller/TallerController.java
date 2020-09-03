package com.developergg.app.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.developergg.app.model.Articulo;
import com.developergg.app.model.Perfil;
import com.developergg.app.model.Taller;
import com.developergg.app.model.TallerArticulo;
import com.developergg.app.model.TallerDetalle;
import com.developergg.app.model.TipoEquipo;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IArticulosService;
import com.developergg.app.service.IPerfilesService;
import com.developergg.app.service.ITalleresArticulosService;
import com.developergg.app.service.ITalleresDetallesService;
import com.developergg.app.service.ITalleresService;
import com.developergg.app.service.ITiposEquipoService;
import com.developergg.app.service.IUsuariosService;

@Controller
@RequestMapping("/talleres")
public class TallerController {
	
	@Autowired
	private ITalleresService serviceTalleres;
	
	@Autowired
	private IUsuariosService serviceUsuarios;
	
	@Autowired
	private IPerfilesService servicePerfiles;
	
	@Autowired
	private ITiposEquipoService serviceTipoEquipos;
	
	@Autowired
	private ITalleresDetallesService serviceTalleresDetalle;
	
	@Autowired
	private ITalleresArticulosService serviceTalleresArticulos;
	
	@Autowired
	private IArticulosService serviceArticulos;
	
	@GetMapping("/")
	public String listaTaller(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Taller> listaTaller = serviceTalleres.buscarPorAlmacen(usuario.getAlmacen());
		for (Taller taller : listaTaller) {
			if(taller.getAsignado() == null) {
				taller.setAsignado(new Usuario());
			}
			if(taller.getTipo_reparacion().equals("1")) {
				taller.setTipo_reparacion("Taller");
			}else if(taller.getTipo_reparacion().equals("2")) {
				taller.setTipo_reparacion("Garantia");
			}else if(taller.getTipo_reparacion().equals("3")) {
				taller.setTipo_reparacion("Desbloqueo");
			}
		}
		Perfil perfil = servicePerfiles.buscarPorPerfil("Tecnico");
		Taller tempTaller = new Taller();
		Taller taller =  new Taller();
		taller.setTipoEquipo(new TipoEquipo());
		taller.setAlmacen(usuario.getAlmacen());
		tempTaller.setTipoEquipo(new TipoEquipo());
		//lista de tecnicos activos por almacen 
		List<Usuario> tecnicos = serviceUsuarios.buscarPorAlmacenAndPerfil(usuario.getAlmacen(), perfil).
				stream().filter(t -> t.getEstatus() == 1).collect(Collectors.toList());
		List<TipoEquipo> tiposEquipo =  serviceTipoEquipos.buscarTodos();
		model.addAttribute("listaTaller", listaTaller);
		model.addAttribute("tecnicos", tecnicos);
		model.addAttribute("infoTaller", tempTaller);
		model.addAttribute("taller", taller);
		model.addAttribute("tiposEquipo", tiposEquipo);
		return "talleres/listaTaller";
	}
	
	@GetMapping("/procesar/{tallerId}")
	public String procesar(Model model, @PathVariable(name = "tallerId") Integer tallerId, HttpSession session) {
		Taller taller = serviceTalleres.buscarPorId(tallerId);
		if(taller==null) {
			taller = new Taller();
		}else {
			if(taller.getTipo_reparacion().equals("1")) {
				taller.setTipo_reparacion("Taller");
			}else if(taller.getTipo_reparacion().equals("2")) {
				taller.setTipo_reparacion("Garantia");
			}else if(taller.getTipo_reparacion().equals("3")) {
				taller.setTipo_reparacion("Desbloqueo");
			}
		}
		model.addAttribute("taller", taller);
		return "talleres/taller";
	}
	
	@GetMapping("/ajax/getListaArticulos/{tallerId}")
	public String getListaArticulos(Model model, @PathVariable(name = "tallerId") Integer tallerId, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<TallerArticulo> listaArticulos = serviceTalleresArticulos.
				buscarPorAlmacen(usuario.getAlmacen()).stream().
				filter(a -> a.getCantidad() > 0).collect(Collectors.toList());
		model.addAttribute("listaArticulos", listaArticulos);
		return "talleres/taller :: #articulos";
	}
	
	@GetMapping("/ajax/getInfo/{tallerId}")
	public String getIngreso(Model model, @PathVariable(name = "tallerId") Integer tallerId) {
		Taller taller = serviceTalleres.buscarPorId(tallerId);
		if(taller==null) {
			taller = new Taller();
		}
		model.addAttribute("infoTaller", taller);
		return "talleres/listaTaller :: #asignarTecnico";
	}
	
	@PostMapping("/ajax/asignarTecnico")
	public String asignaeTecnico(Model model, @RequestParam(name = "idTaller") Integer idTaller,
			@RequestParam(name = "idTecnico") Integer idTecnico) {
		Taller taller = serviceTalleres.buscarPorId(idTaller);
		int response = 0;
		if(taller!=null) {
			Usuario tecnico  = serviceUsuarios.buscarPorId(idTecnico);
			if(tecnico!=null) {
				taller.setAsignado(tecnico);
				serviceTalleres.guardar(taller);
				response = 1;
			}
		}
		model.addAttribute("responseAsignarTecnico", response);
		return "talleres/listaTaller :: #responseAsignarTecnico";
	}
	
	@PostMapping("/ajax/addTaller/")
	public String addTaller(Model model, @ModelAttribute("taller") Taller taller, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		taller.setAlmacen(usuario.getAlmacen());
		taller.setFecha(new Date());
		taller.setUsuario(usuario);
		serviceTalleres.guardar(taller);
		model.addAttribute("responseAddRecepcion", taller.getId()!=null?1:0);
		return "talleres/listaTaller :: #responseAddRecepcion";
	}
	
	@GetMapping("/ajax/loadCuerpoTaller/{idTaller}")
	public String getCuerpoFacturaTemp(Model model, HttpSession session, @PathVariable("idTaller") Integer idTaller) {
		Taller taller = serviceTalleres.buscarPorId(idTaller);
		Double total = 0.0;
		//Cargamos los detalles
		List<TallerDetalle> listaDetalles = serviceTalleresDetalle.buscarPorTaller(taller);
		if(!listaDetalles.isEmpty()) {
			for (TallerDetalle tallerDetalle : listaDetalles) {
				total += tallerDetalle.getPrecio();
			}
		}
		model.addAttribute("listaDetalles", listaDetalles);
		model.addAttribute("total", total);
		return "talleres/cuerpoTaller :: cuerpoTaller";
	}
	
	@PostMapping("/ajax/addDetail/")
	public String addDetail( HttpSession session, Model model, @RequestParam("idTaller") Integer idTaller,
			 @RequestParam("nombre") String nombre, @RequestParam("costo") Double costo,
			 @RequestParam("cantidad") Integer cantidad, @RequestParam("precio") Double precio,
			 @RequestParam("idArticulo") Integer idArticulo, @RequestParam("tallerArticuloId") Integer tallerArticuloId) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Taller taller = serviceTalleres.buscarPorId(idTaller);
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
	    String now = LocalDateTime.now().format(formatter);
		TallerDetalle detalle = new TallerDetalle();
		detalle.setCantidad(cantidad);
		detalle.setCosto(costo);
		detalle.setFecha(new Date());
		detalle.setHora(now);
		detalle.setItem(nombre);
		detalle.setPrecio(precio);
		detalle.setTaller(taller);
		detalle.setUsuario(usuario);
		
		if(idArticulo>0) {
			Articulo articulo = serviceArticulos.buscarPorId(idArticulo);
			if(articulo!=null) {
				detalle.setArticulo(articulo);
			}
		}
		
		if(tallerArticuloId>0) {
			TallerArticulo tallerArticulo = serviceTalleresArticulos.buscarPorId(tallerArticuloId);
			if(tallerArticulo!=null) {
				detalle.setTallerArticulo(tallerArticulo);
				tallerArticulo.setCantidad(tallerArticulo.getCantidad()-cantidad);
				serviceTalleresArticulos.guardar(tallerArticulo);
			}
		}
		
		serviceTalleresDetalle.guardar(detalle);
		return "talleres/taller :: #responseAddDetail";
	}
	
	@PostMapping("/ajax/deleteDetail/")
	public String deleteDetail(Model model, HttpSession session, @RequestParam("idDetalle") Integer idDetalle) {
		TallerDetalle item = serviceTalleresDetalle.buscarPorId(idDetalle);
		if(item!=null) {
			//Verificamos si tinee registro en los articulos del taller
			if(item.getTallerArticulo()!=null) {
				TallerArticulo tallerArticulo = item.getTallerArticulo();
				tallerArticulo.setCantidad(tallerArticulo.getCantidad()+item.getCantidad());
				serviceTalleresArticulos.guardar(tallerArticulo);
			}
			serviceTalleresDetalle.eliminar(item);
		}
		return "talleres/taller :: #responseDeleteDetail";
	}
	
	@PostMapping("/ajax/updateEstado/")
	public String updateEstado(Model model, @RequestParam("idTaller") Integer idTaller,
			 @RequestParam("estado") String estado) {
		Taller taller = serviceTalleres.buscarPorId(idTaller);
		if(taller!=null) {
			taller.setEstado(estado); 
			serviceTalleres.guardar(taller);
		}
		return "talleres/listaTaller :: #responseAddDetail";
	}
	
	@PostMapping("/ajax/liberarEquipo/")
	public String liberarEquipo(Model model, @RequestParam("idTaller") Integer idTaller) {
		Taller taller = serviceTalleres.buscarPorId(idTaller);
		int response = 0;
		if(taller!=null) {
			taller.setAsignado(null);
			serviceTalleres.guardar(taller);
		}
		if(taller.getAsignado() == null) {
			response = 1;
		}
		model.addAttribute("responseLiberado",response);
		return "talleres/taller :: #responseLiberado";
	}
	
	@PostMapping("/ajax/addRecepcion/")
	public String addRecepcion(Model model, @ModelAttribute("taller") Taller taller, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		taller.setAlmacen(usuario.getAlmacen());
		taller.setFecha(new Date());
		taller.setUsuario(usuario);
		serviceTalleres.guardar(taller);
		model.addAttribute("responseAddRecepcion", taller.getId()!=null?1:0);
		return "facturas/factura :: #responseAddRecepcion";
	}
}
