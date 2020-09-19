package com.developergg.app.controller;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetalle;
import com.developergg.app.model.FacturaDetalleSerial;
import com.developergg.app.model.FacturaDetalleServicio;
import com.developergg.app.model.FacturaDetalleTaller;
import com.developergg.app.model.Usuario;
import com.developergg.app.model.VentaContado;
import com.developergg.app.model.VentaContadoTaller;
import com.developergg.app.model.VentaCredito;
import com.developergg.app.model.VentaCreditoTaller;
import com.developergg.app.service.IFacturasDetallesSerialesService;
import com.developergg.app.service.IFacturasDetallesService;
import com.developergg.app.service.IFacturasDetallesServiciosService;
import com.developergg.app.service.IFacturasDetallesTallerService;
import com.developergg.app.service.IFacturasService;
import com.developergg.app.service.IUsuariosService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
@RequestMapping("/cuadreCaja")
public class CuadreCajaController {
	
	@Autowired
	private IUsuariosService serviceUsuarios;
	
	@Autowired
	private IFacturasService serviceFacturas;
	
	@Autowired
	private IFacturasDetallesService serviceFacturaDetalles;
	
	@Autowired
	private IFacturasDetallesServiciosService serviceFacturaDetalleServicios;
	
	@Autowired
	private IFacturasDetallesSerialesService serviceFacturaDetallesSeriales;
	
	@Autowired
	private IFacturasDetallesTallerService serviceFacturaDetallesTalleres;
	
	@Autowired
	private DataSource dataSource;

	@Value("${inventario.ruta.reporte.cuadreCaja}")
	private String rutaJreport;
	
	@Value("${inventario.ruta.imagenes}")
	private String rutaImagenes;
	
	private String tempFolder =  System.getProperty("java.io.tmpdir");
	private String pathSeparator = System.getProperty("file.separator");
	
	@GetMapping("/")
	public String cuadreCaja(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<Usuario> usuarios = serviceUsuarios.buscarPorAlmacen(usuario.getAlmacen());
		for (Usuario user : usuarios) {
			user.setPassword("");
		}
		model.addAttribute("usuarios", usuarios);
		return "cuadreCaja/cuadreCaja";
	}
	
	@PostMapping("/procesar")
	public void procesar(HttpSession session,String desde, String hasta, Integer idUsuario, HttpServletRequest request, 
            HttpServletResponse response
            ) throws JRException, SQLException{
		Usuario usuarioAcct = (Usuario) session.getAttribute("usuario");
		
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Date newDesde = null;
		Date newHasta = null;
		try {
			newDesde = formato.parse(desde);
			newHasta = formato.parse(hasta);
		} catch (ParseException e) {
			System.out.println("Formato errado");
		}
		
		List<Usuario> usuarios = new LinkedList<>();
		if(idUsuario == 0) {
			//Todos los usuarios del almacen
			usuarios = serviceUsuarios.buscarPorAlmacen(usuarioAcct.getAlmacen());
		}else {
			Usuario usuario = serviceUsuarios.buscarPorId(idUsuario);
			usuarios.add(usuario);
		}
		
		List<VentaContado> ventasContado = new LinkedList<>();
		List<VentaContadoTaller> ventasContadoTaller = new LinkedList<>();
		List<VentaCredito> ventasCredito = new LinkedList<>();
		List<VentaCreditoTaller> ventasCreditoTaller = new LinkedList<>();
		
		//Venta al contado
		List<Factura> facturasContado = serviceFacturas.buscarFacturaCuadreContadoMultiUsuario(usuarios.get(0).getAlmacen(),
				0, null, newDesde, newHasta, usuarios);
	
		for (Factura factura : facturasContado) {
			List<FacturaDetalle> facturaDetalles = serviceFacturaDetalles.buscarPorFactura(factura);
			List<FacturaDetalleServicio> facturaDetalleServicios = serviceFacturaDetalleServicios.buscarPorFactura(factura);
			for (FacturaDetalle detalle : facturaDetalles) {
				String nombreArticulo = detalle.getArticulo().getNombre();
				if(detalle.getArticulo().getImei().equals("SI") || detalle.getArticulo().getImei().equals("1")) {
					//Articulo con serial
					List<FacturaDetalleSerial> facturaDetalleSeriales = serviceFacturaDetallesSeriales.buscarPorDetalle(detalle);
					if(!facturaDetalleSeriales.isEmpty()) {
						nombreArticulo += " - ";
						for (FacturaDetalleSerial serial : facturaDetalleSeriales) {
							nombreArticulo+=serial.getSerial()+",";
						}
						nombreArticulo = nombreArticulo.substring(0, nombreArticulo.length() - 1);
					}
				}
				
				VentaContado ventaContado = new VentaContado();
				ventaContado.setId(detalle.getId());
				ventaContado.setCantidad(detalle.getCantidad());
				ventaContado.setFecha(formato.format(factura.getFecha()));
				ventaContado.setNoFactura(factura.getCodigo());
				ventaContado.setNombreArticulo(nombreArticulo);
				ventaContado.setPrecio(detalle.getPrecio());
				ventaContado.setSubTotal(detalle.getSubtotal());
				ventaContado.setTable("facturas_detalle");
				ventasContado.add(ventaContado);
			}
			
			//Servicios
			for (FacturaDetalleServicio facturaDetalleServicio : facturaDetalleServicios) {
				VentaContado ventaContadoServicio = new VentaContado();
				ventaContadoServicio.setId(facturaDetalleServicio.getId());
				ventaContadoServicio.setCantidad(facturaDetalleServicio.getCantidad());
				ventaContadoServicio.setFecha(formato.format(factura.getFecha()));
				ventaContadoServicio.setNoFactura(factura.getCodigo());
				ventaContadoServicio.setNombreArticulo(facturaDetalleServicio.getDescripcion());
				ventaContadoServicio.setPrecio(facturaDetalleServicio.getPrecio());
				ventaContadoServicio.setSubTotal(facturaDetalleServicio.getSubtotal());
				ventaContadoServicio.setTable("facturas_detalle_servicio");
				ventasContado.add(ventaContadoServicio);
			}
		}
		
		//Ventas al contado Taller
		List<Factura> facturasContadoTalleres = serviceFacturas.buscarFacturaCuadreContadoTallerMultiUsuario(usuarios.get(0).getAlmacen(),
				0, newDesde, newHasta, usuarios);
		
		for (Factura facturaContadoTaller : facturasContadoTalleres) {
			List<FacturaDetalleTaller> facturaDetalleTalleres = serviceFacturaDetallesTalleres.buscarPorFactura(facturaContadoTaller);
			for (FacturaDetalleTaller detalleTaller : facturaDetalleTalleres) {
				VentaContadoTaller ventaContadoTaller = new VentaContadoTaller();
				ventaContadoTaller.setCantidad(detalleTaller.getCantidad());
				ventaContadoTaller.setFecha(formato.format(facturaContadoTaller.getFecha()));
				ventaContadoTaller.setNoFactura(facturaContadoTaller.getCodigo());
				ventaContadoTaller.setNombreArticulo(detalleTaller.getDescripcion());
				ventaContadoTaller.setPrecio(detalleTaller.getPrecio());
				ventaContadoTaller.setSubTotal(detalleTaller.getSubtotal());
				ventasContadoTaller.add(ventaContadoTaller);
			}
		}
		
		//Ventas a credito
		List<Factura> facturasCreditos = serviceFacturas.buscarFacturaCuadreContadoMultiUsuario(usuarios.get(0).getAlmacen(),
				1, null, newDesde, newHasta, usuarios);
		
		for (Factura facturaCredito : facturasCreditos) {
			List<FacturaDetalle> facturaDetallesCredito = serviceFacturaDetalles.buscarPorFactura(facturaCredito);
			List<FacturaDetalleServicio> facturaDetalleServiciosCredito = serviceFacturaDetalleServicios.buscarPorFactura(facturaCredito);
			for (FacturaDetalle detalleCredito : facturaDetallesCredito) {
				String nombreArticulo = detalleCredito.getArticulo().getNombre();
				if(detalleCredito.getArticulo().getImei().equals("SI") || detalleCredito.getArticulo().getImei().equals("1")) {
					//Articulo con serial
					List<FacturaDetalleSerial> facturaDetalleSeriales = serviceFacturaDetallesSeriales.buscarPorDetalle(detalleCredito);
					if(!facturaDetalleSeriales.isEmpty()) {
						nombreArticulo += " - ";
						for (FacturaDetalleSerial serial : facturaDetalleSeriales) {
							nombreArticulo+=serial.getSerial()+",";
						}
						nombreArticulo = nombreArticulo.substring(0, nombreArticulo.length() - 1);
					}
				}
				
				VentaCredito ventaCredito = new VentaCredito();
				ventaCredito.setId(detalleCredito.getId());
				ventaCredito.setCantidad(detalleCredito.getCantidad());
				ventaCredito.setFecha(formato.format(facturaCredito.getFecha()));
				ventaCredito.setNoFactura(facturaCredito.getCodigo());
				ventaCredito.setNombreArticulo(nombreArticulo);
				ventaCredito.setPrecio(detalleCredito.getPrecio());
				ventaCredito.setSubTotal(detalleCredito.getSubtotal());
				ventaCredito.setTable("facturas_detalle");
				ventasCredito.add(ventaCredito);
			}
			
			//Servicios
			for (FacturaDetalleServicio facturaDetalleServicioCredito : facturaDetalleServiciosCredito) {
				VentaCredito ventaCreditoServicio = new VentaCredito();
				ventaCreditoServicio.setId(facturaDetalleServicioCredito.getId());
				ventaCreditoServicio.setCantidad(facturaDetalleServicioCredito.getCantidad());
				ventaCreditoServicio.setFecha(formato.format(facturaCredito.getFecha()));
				ventaCreditoServicio.setNoFactura(facturaCredito.getCodigo());
				ventaCreditoServicio.setNombreArticulo(facturaDetalleServicioCredito.getDescripcion());
				ventaCreditoServicio.setPrecio(facturaDetalleServicioCredito.getPrecio());
				ventaCreditoServicio.setSubTotal(facturaDetalleServicioCredito.getSubtotal());
				ventaCreditoServicio.setTable("facturas_detalle_servicio");
				ventasCredito.add(ventaCreditoServicio);
			}
		}
		
		//Ventas credito taller
		List<Factura> facturasCreditoTalleres = serviceFacturas.buscarFacturaCuadreContadoTallerMultiUsuario(usuarios.get(0).getAlmacen(),
				1, newDesde, newHasta, usuarios);
		
		for (Factura facturaCreditoTaller : facturasCreditoTalleres) {
			List<FacturaDetalleTaller> facturaDetalleCreditoTalleres = serviceFacturaDetallesTalleres.buscarPorFactura(facturaCreditoTaller);
			for (FacturaDetalleTaller detalleCreditoTaller : facturaDetalleCreditoTalleres) {
				VentaCreditoTaller ventaCreditoTaller = new VentaCreditoTaller();
				ventaCreditoTaller.setCantidad(detalleCreditoTaller.getCantidad());
				ventaCreditoTaller.setFecha(formato.format(facturaCreditoTaller.getFecha()));
				ventaCreditoTaller.setNoFactura(facturaCreditoTaller.getCodigo());
				ventaCreditoTaller.setNombreArticulo(detalleCreditoTaller.getDescripcion());
				ventaCreditoTaller.setPrecio(detalleCreditoTaller.getPrecio());
				ventaCreditoTaller.setSubTotal(detalleCreditoTaller.getSubtotal());
				ventasCreditoTaller.add(ventaCreditoTaller);
			}
		}
				
		JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreport);
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		//convertimos la lista a JRBeanCollectionDataSource
		JRBeanCollectionDataSource ventasContadoJRBean = new JRBeanCollectionDataSource(ventasContado);
		JRBeanCollectionDataSource ventasContadoTallerJRBean = new JRBeanCollectionDataSource(ventasContadoTaller);
		JRBeanCollectionDataSource ventasCreditoJRBean = new JRBeanCollectionDataSource(ventasCredito);
		JRBeanCollectionDataSource ventasCreditoTallerJRBean = new JRBeanCollectionDataSource(ventasCreditoTaller);
				
		parameters.put("idUsuario", usuarioAcct.getId());
		parameters.put("imagen", rutaImagenes+usuarioAcct.getAlmacen().getImagen());
		parameters.put("ventasContado", ventasContadoJRBean);
		parameters.put("ventasContadoTaller", ventasContadoTallerJRBean);
		parameters.put("ventasCredito", ventasCreditoJRBean);
		parameters.put("ventasCreditoTaller", ventasCreditoTallerJRBean);
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());
		
		String dataDirectory = tempFolder + pathSeparator + "factura"+usuarioAcct.getId()+".pdf";
		
		tempFolder += pathSeparator;

		JasperExportManager.exportReportToPdfFile(jasperPrint,dataDirectory);
		
		 //If user is not authorized - he should be thrown out from here itself
         
        //Authorized user will download the file
        Path file = Paths.get(tempFolder, "factura"+usuarioAcct.getId()+".pdf");
        if (Files.exists(file)) 
        {
            String mimeType = URLConnection.guessContentTypeFromName(tempFolder+"factura"+usuarioAcct.getId()+".pdf");
            if (mimeType == null) mimeType = "application/octet-stream";
            response.setContentType(mimeType);
            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + "factura"+usuarioAcct.getId()+".pdf" + "\""));
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
	
}
