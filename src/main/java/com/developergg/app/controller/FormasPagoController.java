package com.developergg.app.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.developergg.app.model.AbonoCxC;
import com.developergg.app.model.AbonoCxCDetalle;
import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetallePagoTemp;
import com.developergg.app.model.FacturaPago;
import com.developergg.app.model.FacturaPagoTemp;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.FormaPago;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IAbonosCxCDetallesService;
import com.developergg.app.service.IAbonosCxCService;
import com.developergg.app.service.IFacturasDetallesPagoTempService;
import com.developergg.app.service.IFacturasPagoService;
import com.developergg.app.service.IFacturasPagoTempService;
import com.developergg.app.service.IFacturasService;
import com.developergg.app.service.IFacturasTempService;
import com.developergg.app.service.IFormasPagoService;

@Controller
@RequestMapping("/formasPago")
public class FormasPagoController {
	
	@Autowired
	private IFacturasTempService serviceFacturasTemp;
	
	@Autowired
	private IFormasPagoService serviceFormasPago;
	
	@Autowired
	private IFacturasDetallesPagoTempService serviceDetallePago;
	
	@Autowired
	private IFacturasService serviceFacturas;
	
	@Autowired
	private IFacturasPagoService serviceFacturasPagos;
	
	@Autowired
	private IFacturasPagoTempService serviceFacturasPagosTemp;
	
	@Autowired
	private IAbonosCxCService serviceAbonosCxC;
	
	@Autowired
	private IAbonosCxCDetallesService serviceDetallesAbonosCxC;
	
	@PostMapping("/ajax/addPagoFactura")
	public String agregarPagoFacturaAjax(Model model, HttpSession session,
			@RequestParam("formaPago") Integer idFormaPago, @RequestParam("montoFormaPago") Double monto, 
			@RequestParam("montoTotal") Double montoTotal) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaTemp facturaTemp = serviceFacturasTemp.buscarPorUsuario(usuario);
		FormaPago formaPago = serviceFormasPago.buscarPorId(idFormaPago);
		FacturaDetallePagoTemp pagoTemp = new FacturaDetallePagoTemp();
		List<FacturaDetallePagoTemp> listaPagos = serviceDetallePago.buscarPorFacturaTemp(facturaTemp);
		//verificamos si el ultimo pago de la lista es efectivo, de lo contrario debe cumplir el monto exacto
		Double acumulado = 0.0;
		boolean autorizado = false;
		
		if(!listaPagos.isEmpty()) {
			for (FacturaDetallePagoTemp facturaDetallePagoTemp : listaPagos) {
				acumulado+=facturaDetallePagoTemp.getMonto();
			}
			if(!listaPagos.get(listaPagos.size()-1).getFormaPago().getNombre().equalsIgnoreCase("efectivo")) {
				//verificamos que el acumulado mas el nuevo monto no sea mayor al exacto de la factura
				if((acumulado+monto)<=montoTotal) {
					autorizado = true;
				}else {
					//verificamos si el pago actual es efectivo
					if(formaPago.getNombre().equalsIgnoreCase("efectivo")) {
						autorizado = true;
					}
				}
			}else {
				autorizado = true;
			}
		}else {	
			if(formaPago.getNombre().equalsIgnoreCase("Efectivo")) {
				autorizado = true;
			}else if(!formaPago.getNombre().equalsIgnoreCase("Efectivo") && monto<=montoTotal){
				autorizado = true;
			}		
		}
		
		pagoTemp.setFacturaTemp(facturaTemp);
		pagoTemp.setFormaPago(formaPago);
		pagoTemp.setMonto(monto);
		if(autorizado) {
			serviceDetallePago.guardar(pagoTemp);
		}
		model.addAttribute("responseAddPago", autorizado?1:0);
		return "facturas/factura :: #responseAddPago";
	}
	
	@PostMapping("/ajax/addPagoFacturaCXC")
	public String agregarPagoFacturaCXCTempAjax(Model model, HttpSession session,
			@RequestParam("formaPago") Integer idFormaPago, @RequestParam("montoFormaPago") Double monto, 
			@RequestParam("idFactura") Integer idFactura) {
		Factura factura = serviceFacturas.buscarPorId(idFactura);
		FormaPago formaPago = serviceFormasPago.buscarPorId(idFormaPago);
		FacturaPago pago = new FacturaPago();
		List<FacturaPago> listaPagos = serviceFacturasPagos.buscarPorFactura(factura);
		//verificamos si el ultimo pago de la lista es efectivo, de lo contrario debe cumplir el monto exacto
		Double acumulado = 0.0;
		boolean autorizado = false;
		
		if(!listaPagos.isEmpty()) {
			for (FacturaPago facturaPago : listaPagos) {
				acumulado+=facturaPago.getCantidad();
			}
			if(!listaPagos.get(listaPagos.size()-1).getFormaPago().getNombre().equalsIgnoreCase("efectivo")) {
				//verificamos que el acumulado mas el nuevo monto no sea mayor al exacto de la factura
				if((acumulado+monto)<=factura.getTotal_venta()) {
					autorizado = true;
				}else {
					//verificamos si el pago actual es efectivo
					if(formaPago.getNombre().equalsIgnoreCase("efectivo")) {
						autorizado = true;
					}
				}
			}else {
				autorizado = true;
			}
		}else {	
			if(formaPago.getNombre().equalsIgnoreCase("Efectivo")) {
				autorizado = true;
			}else if(!formaPago.getNombre().equalsIgnoreCase("Efectivo") && monto<=factura.getTotal_venta()){
				autorizado = true;
			}		
		}
		
		pago.setFactura(factura);
		pago.setFormaPago(formaPago);
		pago.setCantidad(monto);
		if(autorizado) {
			Double abonoAcct = factura.getAbono();
			factura.setAbono(abonoAcct+pago.getCantidad());
			serviceFacturasPagos.guardar(pago);
		}
		model.addAttribute("responseAddPago", autorizado?1:0);
		return "facturas/listaFacturasXC :: #responseAddPago";
	}
	
	@PostMapping("/ajax/addPagoTempFacturaCXC")
	public String agregarPagoFacturaCXCAjax(Model model, HttpSession session,
			@RequestParam("formaPago") Integer idFormaPago, @RequestParam("montoFormaPago") Double monto, 
			@RequestParam("idFactura") Integer idFactura) {
		Factura factura = serviceFacturas.buscarPorId(idFactura);
		FormaPago formaPago = serviceFormasPago.buscarPorId(idFormaPago);
		FacturaPagoTemp pago = new FacturaPagoTemp();
		List<FacturaPagoTemp> listaPagos = serviceFacturasPagosTemp.buscarPorFactura(factura);
		//verificamos si el ultimo pago de la lista es efectivo, de lo contrario debe cumplir el monto exacto
		Double acumulado = 0.0;
		boolean autorizado = false;
		
		if(!listaPagos.isEmpty()) {
			for (FacturaPagoTemp facturaPago : listaPagos) {
				acumulado+=facturaPago.getCantidad();
			}
			if(!listaPagos.get(listaPagos.size()-1).getFormaPago().getNombre().equalsIgnoreCase("efectivo")) {
				//verificamos que el acumulado mas el nuevo monto no sea mayor al exacto de la factura
				if((acumulado+monto)<=factura.getTotal_venta()) {
					autorizado = true;
				}else {
					//verificamos si el pago actual es efectivo
					if(formaPago.getNombre().equalsIgnoreCase("efectivo")) {
						autorizado = true;
					}
				}
			}else {
				autorizado = true;
			}
		}else {	
			if(formaPago.getNombre().equalsIgnoreCase("Efectivo")) {
				autorizado = true;
			}else if(!formaPago.getNombre().equalsIgnoreCase("Efectivo") && monto<=factura.getTotal_venta()){
				autorizado = true;
			}		
		}
		
		pago.setFactura(factura);
		pago.setFormaPago(formaPago);
		pago.setCantidad(monto);
		if(autorizado) {
			serviceFacturasPagosTemp.guardar(pago);
		}
		model.addAttribute("responseAddPago", autorizado?1:0);
		return "facturas/listaFacturasXC :: #responseAddPago";
	}
	
	@PostMapping("/ajax/guardarAbonoCxC")
	public String guardarAbonoCxC(HttpSession session, Model model, @RequestParam(name = "idFactura") Integer idFactura) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Factura factura = serviceFacturas.buscarPorId(idFactura);
		Double restante = factura.getTotal_venta()-factura.getAbono();
		Double abonado = 0.0;
		if(factura!=null) {
			//buscamos los pagos temporales
			List<FacturaPagoTemp> listaPagosTemp = serviceFacturasPagosTemp.buscarPorFactura(factura);
			
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
	        String now = LocalDateTime.now().format(formatter);
	        
	        List<AbonoCxC> listaAbonos = serviceAbonosCxC.buscarPorFactura(factura);
	        AbonoCxC abono = null;
	        //Si no tiene abono lo creamos, de lo contrario actualizamos en base al abono
	        if(listaAbonos.isEmpty()) {
				//creamos el abono
	        	listaAbonos = new LinkedList<>();
				abono = new AbonoCxC();
				abono.setAlmacen(usuario.getAlmacen());
				abono.setCliente(factura.getCliente());
				abono.setFactura(factura);
				abono.setUsuario(usuario);
//				abono.setCodigo(factura.getCodigo());
				serviceAbonosCxC.guardar(abono);
				listaAbonos.add(abono);
	        }else {
	        	abono = listaAbonos.get(0);
	        }
			
			if(!listaPagosTemp.isEmpty()) {
				for (FacturaPagoTemp facturaPagoTemp : listaPagosTemp) {
					AbonoCxCDetalle detalle = new AbonoCxCDetalle();
					detalle.setFormaPago(facturaPagoTemp.getFormaPago());
					detalle.setIngreso(abono);
					detalle.setAbono(facturaPagoTemp.getCantidad());
					detalle.setFecha(new Date());
					detalle.setHora(now);
					restante-=detalle.getAbono();
					abonado+=detalle.getAbono();
					serviceDetallesAbonosCxC.guardar(detalle);
				}
				abono.setTotalAbono(abonado);
				abono.setTotalRestante(restante<0?0:restante);
				abono.setTotalPagado(abonado);
				if(restante<0) {
					abono.setTotalDevuelto(restante*-1);
				}
				serviceAbonosCxC.guardar(abono);
				factura.setAbono(factura.getAbono()+abono.getTotalAbono());
				serviceFacturas.guardar(factura);
				//Borramos los pagos temportales
				serviceFacturasPagosTemp.eliminarAll(listaPagosTemp);
			}
		}
		return "facturas/listaFacturasXC :: #responseAddPago";
	}
	
	@PostMapping("/ajax/deletePagoTemporalCxC")
	public String eliminarPagoCxCAjax(@RequestParam("idPagoFactura") Integer idPagoTempFactura) {
		serviceFacturasPagosTemp.eliminar(idPagoTempFactura);
		return "facturas/listaFacturasXC :: #responseDeletePago";
	}
	
	@PostMapping("/ajax/deleteAllTemporalCxC")
	public String eliminarAllPagoCxCAjax(@RequestParam("idFactura") Integer idFactura) {
		Factura factura = serviceFacturas.buscarPorId(idFactura);
		//pagos temporales de la factura
		List<FacturaPagoTemp> pagosTemp = serviceFacturasPagosTemp.buscarPorFactura(factura);
		serviceFacturasPagosTemp.eliminarAll(pagosTemp);
		return "facturas/listaFacturasXC :: #responseDeletePago";
	}
	
	@PostMapping("/ajax/deletePagoFactura")
	public String eliminarPagoFacturaAjax(HttpSession session,
			@RequestParam("pago") Integer idPago) {
		serviceDetallePago.eliminar(idPago);
		return "facturas/factura :: #responseDeletePago";
	}
	
}
