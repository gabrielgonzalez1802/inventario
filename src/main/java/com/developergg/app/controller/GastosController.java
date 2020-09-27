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

import com.developergg.app.model.Gasto;
import com.developergg.app.model.ReporteGasto;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IGastosService;
import com.developergg.app.service.IUsuariosService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
@RequestMapping("/gastos")
public class GastosController {
	
	@Autowired
	private IGastosService serviceGastos;
	
	@Autowired
	private IUsuariosService serviceUsuarios;
	
	@Autowired
	private DataSource dataSource;
	
	@Value("${inventario.ruta.imagenes}")
	private String rutaImagenes;
	
	@Value("${inventario.ruta.reporte.gasto}")
	private String rutaJreportGasto;
	
	private String tempFolder =  System.getProperty("java.io.tmpdir");
	private String pathSeparator = System.getProperty("file.separator");
	
	@GetMapping("/")
	public String listaGastos(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Gasto> listaGastos = serviceGastos.buscarPorAlmacen(usuario.getAlmacen());
		model.addAttribute("listaGastos", listaGastos);
		model.addAttribute("gasto", new Gasto());
		return "gastos/listaGastos";
	}
	
	@PostMapping("/ajax/agregarGasto")
	public String agregarGastos(Model model, HttpSession session, @RequestParam(name = "nombreGasto") String nombreGasto,
			@RequestParam(name = "tipoGasto") String tipoGasto, @RequestParam(name = "montoGasto") Double montoGasto,
			@RequestParam(name = "decripcionGasto") String decripcionGasto) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
	    String now = LocalDateTime.now().format(formatter);
		Gasto gasto = new Gasto();
		gasto.setUsuario(usuario);
		gasto.setAlmacen(usuario.getAlmacen());
		gasto.setDescripcion(decripcionGasto);
		gasto.setFecha(new Date());
		gasto.setMonto(montoGasto);
		gasto.setNombre(nombreGasto);
		gasto.setTipo_gasto(tipoGasto);
		gasto.setHora(now);
		serviceGastos.guardar(gasto);
		model.addAttribute("gastoGuardado", gasto.getId());
		return "gastos/listaGastos :: #gastoGuardado";
	}
	
	@GetMapping("/delete/{idGasto}")
	public String borrarGasto(@PathVariable("idGasto") Integer idGasto) {
		serviceGastos.eliminar(idGasto);
		return "redirect:/gastos/";
	}
	
	@PostMapping("/ajax/modificarGasto")
	public String modificarGastos(Model model, HttpSession session, @RequestParam(name = "idGasto") Integer idGasto,
			@RequestParam(name = "nombreGasto") String nombreGasto, @RequestParam(name = "tipoGasto") String tipoGasto, @RequestParam(name = "montoGasto") Double montoGasto,
			@RequestParam(name = "decripcionGasto") String decripcionGasto) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
	    String now = LocalDateTime.now().format(formatter);
		Gasto gasto = serviceGastos.buscarPorId(idGasto);
		gasto.setDescripcion(decripcionGasto);
		gasto.setFecha(new Date());
		gasto.setMonto(montoGasto);
		gasto.setNombre(nombreGasto);
		gasto.setTipo_gasto(tipoGasto);
		gasto.setHora(now);
		serviceGastos.guardar(gasto);
		model.addAttribute("gastoGuardado", gasto.getId());
		return "gastos/listaGastos :: #gastoGuardado";
	}
	
	@GetMapping("/ajax/getGasto/{idGasto}")
	public String getGasto(Model model, @PathVariable(name = "idGasto") Integer idGasto) {
		Gasto gasto = serviceGastos.buscarPorId(idGasto);
		if(gasto==null) {
			gasto = new Gasto();
		}
		model.addAttribute("gasto", gasto);
		return "gastos/listaGastos :: #modificarGasto";
	}
	
	@GetMapping("/formularioReporteGasto")
	public String formularioReporteGasto(Model model, HttpSession session) {
		Usuario usuarioAcct = (Usuario) session.getAttribute("usuario");
		List<Usuario> usuarios = serviceUsuarios.buscarPorAlmacen(usuarioAcct.getAlmacen());
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("dateAcct", new Date());
		return "gastos/generarReporteGasto";
	}
	
	@PostMapping("/reporteGasto")
	public void reporteGasto(HttpSession session, HttpServletRequest request, 
				HttpServletResponse response, Integer idUsuario, String desde, String hasta) throws JRException, SQLException, ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Usuario usuarioAcct = (Usuario) session.getAttribute("usuario");
		List<ReporteGasto> reporteGastos = new LinkedList<>();
		List<Usuario> usuarios = new LinkedList<>();
		
		if(idUsuario == 0) {
			usuarios = serviceUsuarios.buscarPorAlmacen(usuarioAcct.getAlmacen());
		}else {
			usuarios.add(serviceUsuarios.buscarPorId(idUsuario));
		}
		
		List<Gasto> gastos = serviceGastos.buscarPorUsuariosAlmacenFechas(usuarios, usuarioAcct.getAlmacen(),
				formato.parse(desde), formato.parse(hasta));

		for (Gasto gasto : gastos) {
			ReporteGasto reporteGasto = new ReporteGasto();
			reporteGasto.setId(gasto.getId());
			reporteGasto.setDetalle(gasto.getNombre());
			reporteGasto.setUsuario(usuarioAcct.getUsername());
			reporteGasto.setMonto(gasto.getMonto());
			reporteGasto.setFecha(formato.format(gasto.getFecha()));

			if (idUsuario == 0) {
				// Todos usuarios
				reporteGastos.add(reporteGasto);
			} else {
				// usuario especifico
				if (gasto.getUsuario().getId() == idUsuario) {
					reporteGastos.add(reporteGasto);
				}
			}
		}
		
		//Compilamos el reporte
		JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreportGasto);

		Map<String, Object> parameters = new HashMap<String, Object>();

		// convertimos la lista a JRBeanCollectionDataSource
		JRBeanCollectionDataSource comprobantesReporteJRBean = new JRBeanCollectionDataSource(reporteGastos);

		parameters.put("idUsuario", usuarioAcct.getId());
		parameters.put("imagen", rutaImagenes + usuarioAcct.getAlmacen().getImagen());
		parameters.put("total", reporteGastos.size());
		parameters.put("fechaDesde", desde);
		parameters.put("fechaHasta", hasta);
		parameters.put("author", usuarioAcct.getUsername());
		parameters.put("usuario", idUsuario== 0 ? "TODOS" : usuarios.get(0).getNombre());
		parameters.put("reporteGasto", comprobantesReporteJRBean);

		// Objeto de impresion jr
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

		String dataDirectory = tempFolder + pathSeparator + "reporteGasto" + usuarioAcct.getId() + ".pdf";

		tempFolder += pathSeparator;

		JasperExportManager.exportReportToPdfFile(jasperPrint, dataDirectory);

		Path file = Paths.get(tempFolder, "reporteGasto" + usuarioAcct.getId() + ".pdf");
		if (Files.exists(file)) {
			String mimeType = URLConnection
					.guessContentTypeFromName(tempFolder + "reporteGasto" + usuarioAcct.getId() + ".pdf");
			if (mimeType == null)
				mimeType = "application/octet-stream";
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition",
					String.format("inline; filename=\"" + "reporteGasto" + usuarioAcct.getId() + ".pdf" + "\""));
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
