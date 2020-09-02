package com.developergg.app.controller;

import java.util.Date;
import java.util.List;
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

import com.developergg.app.model.Perfil;
import com.developergg.app.model.Taller;
import com.developergg.app.model.TipoEquipo;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IPerfilesService;
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
