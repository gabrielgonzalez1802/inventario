package com.developergg.app.controller;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.developergg.app.model.Ingreso;
import com.developergg.app.model.ReporteIngreso;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IIngresosService;
import com.developergg.app.service.IUsuariosService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
@RequestMapping("/ingresos")
public class IngresosController {
	
	@Autowired
	private IIngresosService serviceIngresos;
	
	@Autowired
	private IUsuariosService serviceUsuarios;
	
	@Autowired
	private DataSource dataSource;
	
	@Value("${inventario.ruta.imagenes}")
	private String rutaImagenes;
	
	@Value("${inventario.ruta.reporte.ingreso}")
	private String rutaJreportIngreso;
	
	private String tempFolder =  System.getProperty("java.io.tmpdir");
	private String pathSeparator = System.getProperty("file.separator");
	
	@GetMapping("/")
	public String listaIngresos(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Ingreso> listaIngresos = serviceIngresos.buscarPorAlmacen(usuario.getAlmacen());
		model.addAttribute("listaIngresos", listaIngresos);
		model.addAttribute("ingreso", new Ingreso());
		return "ingresos/listaIngresos";
	}
	
	@PostMapping("/ajax/agregarIngreso")
	public String agregarIngreso(Model model, HttpSession session, @RequestParam(name = "nombreIngreso") String nombreIngreso,
			@RequestParam(name = "tipoIngreso") String tipoIngreso, @RequestParam(name = "montoIngreso") Double montoIngreso,
			@RequestParam(name = "decripcionIngreso") String decripcionIngreso) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
	    String now = LocalDateTime.now().format(formatter);
		Ingreso ingreso = new Ingreso();
		ingreso.setUsuario(usuario);
		ingreso.setAlmacen(usuario.getAlmacen());
		ingreso.setDescripcion(decripcionIngreso);
		ingreso.setFecha(new Date());
		ingreso.setMonto(montoIngreso);
		ingreso.setNombre(nombreIngreso);
		ingreso.setTipo_ingreso(tipoIngreso);
		ingreso.setHora(now);
		serviceIngresos.guardar(ingreso);
		model.addAttribute("ingresoGuardado", ingreso.getId());
		return "ingresos/listaIngresos :: #ingresoGuardado";
	}
	
	@GetMapping("/delete/{idIngreso}")
	public String borrarIngreso(@PathVariable("idIngreso") Integer idIngreso) {
		serviceIngresos.eliminar(idIngreso);
		return "redirect:/ingresos/";
	}
	
	@PostMapping("/ajax/modificarIngreso")
	public String modificarIngresos(Model model, HttpSession session, @RequestParam(name = "idIngreso") Integer idIngreso,
			@RequestParam(name = "nombreIngreso") String nombreIngreso, @RequestParam(name = "tipoIngreso") String tipoIngreso, 
			@RequestParam(name = "montoIngreso") Double montoIngreso, @RequestParam(name = "decripcionIngreso") String decripcionIngreso) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
	    String now = LocalDateTime.now().format(formatter);
		Ingreso ingreso = serviceIngresos.buscarPorId(idIngreso);
		ingreso.setDescripcion(decripcionIngreso);
		ingreso.setFecha(new Date());
		ingreso.setMonto(montoIngreso);
		ingreso.setNombre(nombreIngreso);
		ingreso.setTipo_ingreso(tipoIngreso);
		ingreso.setHora(now);
		serviceIngresos.guardar(ingreso);
		model.addAttribute("ingresoGuardado", ingreso.getId());
		return "ingresos/listaIngresos :: #ingresoGuardado";
	}
	
	@GetMapping("/ajax/getIngreso/{idIngreso}")
	public String getIngreso(Model model, @PathVariable(name = "idIngreso") Integer idIngreso) {
		Ingreso ingreso = serviceIngresos.buscarPorId(idIngreso);
		if(ingreso==null) {
			ingreso = new Ingreso();
		}
		model.addAttribute("ingreso", ingreso);
		return "ingresos/listaIngresos :: #modificarIngreso";
	}
	
	@GetMapping("/formularioReporteIngreso")
	public String formularioReporteIngreso(Model model, HttpSession session) {
		Usuario usuarioAcct = (Usuario) session.getAttribute("usuario");
		List<Usuario> usuarios = serviceUsuarios.buscarPorAlmacen(usuarioAcct.getAlmacen());
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("dateAcct", new Date());
		return "ingresos/generarReporteIngreso";
	}
	
	@PostMapping("/reporteIngreso")
	public void reporteIngreso(HttpSession session, HttpServletRequest request, 
				HttpServletResponse response, Integer idUsuario, String desde, String hasta) throws JRException, SQLException, ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Usuario usuarioAcct = (Usuario) session.getAttribute("usuario");
		List<ReporteIngreso> reporteIngresos = new LinkedList<>();
		List<Usuario> usuarios = new LinkedList<>();
		
		if(idUsuario == 0) {
			usuarios = serviceUsuarios.buscarPorAlmacen(usuarioAcct.getAlmacen());
		}else {
			usuarios.add(serviceUsuarios.buscarPorId(idUsuario));
		}
		
		List<Ingreso> ingresos = serviceIngresos.buscarPorUsuariosAlmacenFechas(usuarios, usuarioAcct.getAlmacen(),
				formato.parse(desde), formato.parse(hasta));

		for (Ingreso ingreso : ingresos) {
			ReporteIngreso reporteIngreso = new ReporteIngreso();
			reporteIngreso.setId(ingreso.getId());
			reporteIngreso.setDetalle(ingreso.getNombre());
			reporteIngreso.setUsuario(usuarioAcct.getUsername());
			reporteIngreso.setMonto(ingreso.getMonto());
			reporteIngreso.setFecha(formato.format(ingreso.getFecha()));

			if (idUsuario == 0) {
				// Todos usuarios
				reporteIngresos.add(reporteIngreso);
			} else {
				// usuario especifico
				if (ingreso.getUsuario().getId() == idUsuario) {
					reporteIngresos.add(reporteIngreso);
				}
			}
		}
		
		// Compilamos el reporte
		JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreportIngreso);

		Map<String, Object> parameters = new HashMap<String, Object>();

		// convertimos la lista a JRBeanCollectionDataSource
		JRBeanCollectionDataSource comprobantesReporteJRBean = new JRBeanCollectionDataSource(reporteIngresos);

		parameters.put("idUsuario", usuarioAcct.getId());
		parameters.put("imagen", rutaImagenes + usuarioAcct.getAlmacen().getImagen());
		parameters.put("total", reporteIngresos.size());
		parameters.put("fechaDesde", desde);
		parameters.put("fechaHasta", hasta);
		parameters.put("author", usuarioAcct.getUsername());
		parameters.put("usuario", idUsuario == 0 ? "TODOS" : usuarios.get(0).getNombre());
		parameters.put("reporteIngreso",comprobantesReporteJRBean);

		// Objeto de impresion jr
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

		String dataDirectory = tempFolder + pathSeparator + "reporteIngresos" + usuarioAcct.getId() + ".pdf";

		tempFolder += pathSeparator;

		JasperExportManager.exportReportToPdfFile(jasperPrint, dataDirectory);

		Path file = Paths.get(tempFolder, "reporteIngresos" + usuarioAcct.getId() + ".pdf");
		if (Files.exists(file)) {
			String mimeType = URLConnection
					.guessContentTypeFromName(tempFolder + "reporteIngresos" + usuarioAcct.getId() + ".pdf");
			if (mimeType == null)
				mimeType = "application/octet-stream";
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition",
					String.format("inline; filename=\"" + "reporteIngresos" + usuarioAcct.getId() + ".pdf" + "\""));
			try {
				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();
				Files.delete(file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}
	
}
