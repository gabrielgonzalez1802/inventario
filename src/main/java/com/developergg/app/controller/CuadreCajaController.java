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

import com.developergg.app.model.AbonoCxC;
import com.developergg.app.model.AbonoCxCDetalle;
import com.developergg.app.model.CuadreAbonoCxC;
import com.developergg.app.model.CuadreDevolucion;
import com.developergg.app.model.CuadreGasto;
import com.developergg.app.model.CuadreIngreso;
import com.developergg.app.model.CuadreVentaContado;
import com.developergg.app.model.CuadreVentaContadoTaller;
import com.developergg.app.model.CuadreVentaCredito;
import com.developergg.app.model.CuadreVentaCreditoTaller;
import com.developergg.app.model.DevolucionFactura;
import com.developergg.app.model.DevolucionFacturaDetalle;
import com.developergg.app.model.DevolucionFacturaSerial;
import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetalle;
import com.developergg.app.model.FacturaDetalleSerial;
import com.developergg.app.model.FacturaDetalleServicio;
import com.developergg.app.model.FacturaDetalleTaller;
import com.developergg.app.model.Gasto;
import com.developergg.app.model.Ingreso;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IAbonosCxCDetallesService;
import com.developergg.app.service.IAbonosCxCService;
import com.developergg.app.service.IDevolucionesFacturasDetallesService;
import com.developergg.app.service.IDevolucionesFacturasSerialesService;
import com.developergg.app.service.IDevolucionesFacturasService;
import com.developergg.app.service.IFacturasDetallesSerialesService;
import com.developergg.app.service.IFacturasDetallesService;
import com.developergg.app.service.IFacturasDetallesServiciosService;
import com.developergg.app.service.IFacturasDetallesTallerService;
import com.developergg.app.service.IFacturasService;
import com.developergg.app.service.IGastosService;
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
	private IAbonosCxCService serviceAbonosCxC;
	
	@Autowired
	private IAbonosCxCDetallesService serviceAbonosCxCDetalle;
	
	@Autowired
	private IIngresosService serviceIngresos;
	
	@Autowired
	private IGastosService serviceGastos;
	
	@Autowired
	private IDevolucionesFacturasService serviceDevolucionesFacturas;
	
	@Autowired
	private IDevolucionesFacturasDetallesService serviceDevolucionesFacturasDetalles;
	
	@Autowired
	private IDevolucionesFacturasSerialesService serviceDevolucionesFacturasSeriales;
	
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
		
		List<CuadreVentaContado> ventasContado = new LinkedList<>();
		List<CuadreVentaContadoTaller> ventasContadoTaller = new LinkedList<>();
		List<CuadreVentaCredito> ventasCredito = new LinkedList<>();
		List<CuadreVentaCreditoTaller> ventasCreditoTaller = new LinkedList<>();
		List<CuadreAbonoCxC> cuadreAbonosCxC = new LinkedList<>();
		List<CuadreIngreso> cuadreIngresos = new LinkedList<>();
		List<CuadreGasto> cuadreGastos = new LinkedList<>();
		List<CuadreDevolucion> cuadreDevoluciones = new LinkedList<>();
		
		//Venta al contado
		List<Factura> facturasContado = serviceFacturas.buscarFacturaCuadreMultiUsuario(usuarios.get(0).getAlmacen(),
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
				
				CuadreVentaContado ventaContado = new CuadreVentaContado();
				ventaContado.setId(detalle.getId());
				ventaContado.setCantidad(detalle.getCantidad());
				ventaContado.setFecha(formato.format(factura.getFecha()));
				ventaContado.setNoFactura(factura.getCodigo());
				ventaContado.setNombreArticulo(nombreArticulo);
				ventaContado.setPrecio(detalle.getPrecio()+detalle.getItbis());
				ventaContado.setSubTotal(detalle.getSubtotal());
				ventaContado.setTable("facturas_detalle");
				ventasContado.add(ventaContado);
			}
			
			//Servicios
			for (FacturaDetalleServicio facturaDetalleServicio : facturaDetalleServicios) {
				CuadreVentaContado ventaContadoServicio = new CuadreVentaContado();
				ventaContadoServicio.setId(facturaDetalleServicio.getId());
				ventaContadoServicio.setCantidad(facturaDetalleServicio.getCantidad());
				ventaContadoServicio.setFecha(formato.format(factura.getFecha()));
				ventaContadoServicio.setNoFactura(factura.getCodigo());
				ventaContadoServicio.setNombreArticulo(facturaDetalleServicio.getDescripcion());
				ventaContadoServicio.setPrecio(facturaDetalleServicio.getPrecio()+facturaDetalleServicio.getItbis());
				ventaContadoServicio.setSubTotal(facturaDetalleServicio.getSubtotal());
				ventaContadoServicio.setTable("facturas_detalle_servicio");
				ventasContado.add(ventaContadoServicio);
			}
		}
		
		//Ventas al contado Taller
		List<Factura> facturasContadoTalleres = serviceFacturas.buscarFacturaCuadreTallerMultiUsuario(usuarios.get(0).getAlmacen(),
				0, newDesde, newHasta, usuarios);
		
		for (Factura facturaContadoTaller : facturasContadoTalleres) {
			List<FacturaDetalleTaller> facturaDetalleTalleres = serviceFacturaDetallesTalleres.buscarPorFactura(facturaContadoTaller);
			for (FacturaDetalleTaller detalleTaller : facturaDetalleTalleres) {
				CuadreVentaContadoTaller ventaContadoTaller = new CuadreVentaContadoTaller();
				ventaContadoTaller.setCantidad(detalleTaller.getCantidad());
				ventaContadoTaller.setFecha(formato.format(facturaContadoTaller.getFecha()));
				ventaContadoTaller.setNoFactura(facturaContadoTaller.getCodigo());
				ventaContadoTaller.setNombreArticulo(detalleTaller.getDescripcion());
				ventaContadoTaller.setPrecio(detalleTaller.getPrecio()+detalleTaller.getItbis());
				ventaContadoTaller.setSubTotal(detalleTaller.getSubtotal());
				ventasContadoTaller.add(ventaContadoTaller);
			}
		}
		
		//Ventas a credito
		List<Factura> facturasCreditos = serviceFacturas.buscarFacturaCuadreMultiUsuario(usuarios.get(0).getAlmacen(),
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
				
				CuadreVentaCredito ventaCredito = new CuadreVentaCredito();
				ventaCredito.setId(detalleCredito.getId());
				ventaCredito.setCantidad(detalleCredito.getCantidad());
				ventaCredito.setFecha(formato.format(facturaCredito.getFecha()));
				ventaCredito.setNoFactura(facturaCredito.getCodigo());
				ventaCredito.setNombreArticulo(nombreArticulo);
				ventaCredito.setPrecio(detalleCredito.getPrecio()+detalleCredito.getItbis());
				ventaCredito.setSubTotal(detalleCredito.getSubtotal());
				ventaCredito.setTable("facturas_detalle");
				ventasCredito.add(ventaCredito);
			}
			
			//Servicios
			for (FacturaDetalleServicio facturaDetalleServicioCredito : facturaDetalleServiciosCredito) {
				CuadreVentaCredito ventaCreditoServicio = new CuadreVentaCredito();
				ventaCreditoServicio.setId(facturaDetalleServicioCredito.getId());
				ventaCreditoServicio.setCantidad(facturaDetalleServicioCredito.getCantidad());
				ventaCreditoServicio.setFecha(formato.format(facturaCredito.getFecha()));
				ventaCreditoServicio.setNoFactura(facturaCredito.getCodigo());
				ventaCreditoServicio.setNombreArticulo(facturaDetalleServicioCredito.getDescripcion());
				ventaCreditoServicio.setPrecio(facturaDetalleServicioCredito.getPrecio()+facturaDetalleServicioCredito.getItbis());
				ventaCreditoServicio.setSubTotal(facturaDetalleServicioCredito.getSubtotal());
				ventaCreditoServicio.setTable("facturas_detalle_servicio");
				ventasCredito.add(ventaCreditoServicio);
			}
		}
		
		//Ventas credito taller
		List<Factura> facturasCreditoTalleres = serviceFacturas.buscarFacturaCuadreTallerMultiUsuario(usuarios.get(0).getAlmacen(),
				1, newDesde, newHasta, usuarios);
		
		for (Factura facturaCreditoTaller : facturasCreditoTalleres) {
			List<FacturaDetalleTaller> facturaDetalleCreditoTalleres = serviceFacturaDetallesTalleres.buscarPorFactura(facturaCreditoTaller);
			for (FacturaDetalleTaller detalleCreditoTaller : facturaDetalleCreditoTalleres) {
				CuadreVentaCreditoTaller ventaCreditoTaller = new CuadreVentaCreditoTaller();
				ventaCreditoTaller.setCantidad(detalleCreditoTaller.getCantidad());
				ventaCreditoTaller.setFecha(formato.format(facturaCreditoTaller.getFecha()));
				ventaCreditoTaller.setNoFactura(facturaCreditoTaller.getCodigo());
				ventaCreditoTaller.setNombreArticulo(detalleCreditoTaller.getDescripcion());
				ventaCreditoTaller.setPrecio(detalleCreditoTaller.getPrecio()+detalleCreditoTaller.getItbis());
				ventaCreditoTaller.setSubTotal(detalleCreditoTaller.getSubtotal());
				ventasCreditoTaller.add(ventaCreditoTaller);
			}
		}
		
		//Abono a CxC
		List<Factura> facturasCxC = serviceFacturas.buscarFacturaCuadreMultiUsuario(usuarios.get(0).getAlmacen(),
				1, newDesde, newHasta, usuarios);
		
		for (Factura facturaCxC : facturasCxC) {
			List<AbonoCxC> abonosCxC = serviceAbonosCxC.buscarPorFactura(facturaCxC);
			for (AbonoCxC ingreso : abonosCxC) {
				List<AbonoCxCDetalle> abonoCxCDetalles = serviceAbonosCxCDetalle.buscarPorIngreso(ingreso);
				for (AbonoCxCDetalle abonoCxCDetalle : abonoCxCDetalles) {
					CuadreAbonoCxC cuadreAbonoCxC = new CuadreAbonoCxC();
					cuadreAbonoCxC.setFactura(facturaCxC.getCodigo().toString());
					cuadreAbonoCxC.setFecha(formato.format(abonoCxCDetalle.getFecha()));
					cuadreAbonoCxC.setId(abonoCxCDetalle.getId());
					cuadreAbonoCxC.setMonto(abonoCxCDetalle.getAbono());
					cuadreAbonosCxC.add(cuadreAbonoCxC);
				}
			}
		}
		
		//Ingresos
		List<Ingreso> ingresos = serviceIngresos.buscarPorUsuariosAlmacenFechas(usuarios, usuarios.get(0).getAlmacen(), newDesde, newHasta);
		for (Ingreso ingreso : ingresos) {
			CuadreIngreso cuadreIngreso = new CuadreIngreso();
			cuadreIngreso.setId(ingreso.getId());
			cuadreIngreso.setFecha(formato.format(ingreso.getFecha()));
			cuadreIngreso.setMonto(ingreso.getMonto());
			cuadreIngreso.setNombre(ingreso.getNombre());
			cuadreIngreso.setTipo(ingreso.getTipo_ingreso());
			cuadreIngresos.add(cuadreIngreso);
		}
		
		List<Gasto> gastos = serviceGastos.buscarPorUsuariosAlmacenFechas(usuarios, usuarios.get(0).getAlmacen(), newDesde, newHasta);
		for (Gasto gasto : gastos) {
			CuadreGasto cuadreGasto = new CuadreGasto();
			cuadreGasto.setFecha(formato.format(gasto.getFecha()));
			cuadreGasto.setId(gasto.getId());
			cuadreGasto.setMonto(gasto.getMonto());
			cuadreGasto.setNombre(gasto.getNombre());
			cuadreGasto.setTipo(gasto.getTipo_gasto());
			cuadreGastos.add(cuadreGasto);
		}	
		
		//Devoluciones
		List<Factura> facturasDevolucion = serviceFacturas.buscarFacturaCuadreMultiUsuario(usuarios.get(0).getAlmacen(), 
				newDesde, newHasta, usuarios);
		
		for (Factura facturaDevolucion : facturasDevolucion) {
			List<DevolucionFactura> devolucionesFactura = serviceDevolucionesFacturas.buscarPorFactura(facturaDevolucion);
			for (DevolucionFactura devolucionFactura : devolucionesFactura) {
				List<DevolucionFacturaDetalle> devolucionFacturaDetalles = serviceDevolucionesFacturasDetalles.buscarPorDevolucionFactura(devolucionFactura);
				for (DevolucionFacturaDetalle detalleDevolucion : devolucionFacturaDetalles) {
					CuadreDevolucion cuadreDevolucion = new CuadreDevolucion();
					//Verificamos si viene del taller
					if(detalleDevolucion.getFacturaDetalleTaller()!=null) {
						FacturaDetalleTaller detalleTaller = detalleDevolucion.getFacturaDetalleTaller();
						cuadreDevolucion.setArticulo(detalleTaller.getDescripcion());
						cuadreDevolucion.setFactura(detalleTaller.getFactura().getCodigo().toString());
					}else {
						cuadreDevolucion.setArticulo(detalleDevolucion.getFacturaDetalle().getArticulo().getNombre());
						cuadreDevolucion.setFactura(detalleDevolucion.getFacturaDetalle().getFactura().getCodigo().toString());
					}
					cuadreDevolucion.setCantidad(detalleDevolucion.getCantidad());
					cuadreDevolucion.setFecha(formato.format(detalleDevolucion.getDevolucionFactura().getFecha()));
					cuadreDevolucion.setId(detalleDevolucion.getId());
					cuadreDevolucion.setPrecio(detalleDevolucion.getPrecio()+detalleDevolucion.getItbis());
					cuadreDevolucion.setSubTotal(detalleDevolucion.getSubTotal());
					cuadreDevolucion.setTabla("devolucion_factura_detalle");
					cuadreDevoluciones.add(cuadreDevolucion);
				}
				List<DevolucionFacturaSerial> facturaSerialesDevolucion = serviceDevolucionesFacturasSeriales.buscarPorDevolucionFactura(devolucionFactura);
				for (DevolucionFacturaSerial serialDevolucion : facturaSerialesDevolucion) {
					CuadreDevolucion cuadreDevolucionSerial = new CuadreDevolucion();
					cuadreDevolucionSerial.setArticulo(serialDevolucion.getNombreArticulo().concat(" - ").concat(serialDevolucion.getSerial()));
					cuadreDevolucionSerial.setCantidad(1);
					cuadreDevolucionSerial.setFactura(serialDevolucion.getDevolucionFactura().getFactura().getCodigo().toString());
					cuadreDevolucionSerial.setFecha(formato.format(serialDevolucion.getDevolucionFactura().getFecha()));
					cuadreDevolucionSerial.setId(serialDevolucion.getId());
					cuadreDevolucionSerial.setPrecio(serialDevolucion.getPrecio()+serialDevolucion.getItbis());
					cuadreDevolucionSerial.setSubTotal(serialDevolucion.getSubTotal());
					cuadreDevolucionSerial.setTabla("devolucion_factura_serial");
					cuadreDevoluciones.add(cuadreDevolucionSerial);
				}
			}
		}
		
		JasperReport jasperReport = JasperCompileManager.compileReport(rutaJreport);
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		//convertimos la lista a JRBeanCollectionDataSource
		JRBeanCollectionDataSource ventasContadoJRBean = new JRBeanCollectionDataSource(ventasContado);
		JRBeanCollectionDataSource ventasContadoTallerJRBean = new JRBeanCollectionDataSource(ventasContadoTaller);
		JRBeanCollectionDataSource ventasCreditoJRBean = new JRBeanCollectionDataSource(ventasCredito);
		JRBeanCollectionDataSource ventasCreditoTallerJRBean = new JRBeanCollectionDataSource(ventasCreditoTaller);
		JRBeanCollectionDataSource abonoCxCJRBean = new JRBeanCollectionDataSource(cuadreAbonosCxC);
		JRBeanCollectionDataSource ingresosRBean = new JRBeanCollectionDataSource(cuadreIngresos);
		JRBeanCollectionDataSource gastosRBean = new JRBeanCollectionDataSource(cuadreGastos);
		JRBeanCollectionDataSource devolucionesRBean = new JRBeanCollectionDataSource(cuadreDevoluciones);
		
		parameters.put("idUsuario", usuarioAcct.getId());
		parameters.put("imagen", rutaImagenes+usuarioAcct.getAlmacen().getImagen());
		parameters.put("ventasContado", ventasContadoJRBean);
		parameters.put("ventasContadoTaller", ventasContadoTallerJRBean);
		parameters.put("ventasCredito", ventasCreditoJRBean);
		parameters.put("ventasCreditoTaller", ventasCreditoTallerJRBean);
		parameters.put("cuadreAbonosCxC", abonoCxCJRBean);
		parameters.put("cuadreIngresos", ingresosRBean);
		parameters.put("cuadreGastos", gastosRBean);
		parameters.put("cuadreDevoluciones", devolucionesRBean);
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());
		
		String dataDirectory = tempFolder + pathSeparator + "factura"+usuarioAcct.getId()+".pdf";
		
		tempFolder += pathSeparator;

		JasperExportManager.exportReportToPdfFile(jasperPrint,dataDirectory);
		         
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
