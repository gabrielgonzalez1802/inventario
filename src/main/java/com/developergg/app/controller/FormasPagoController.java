package com.developergg.app.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.developergg.app.model.Factura;
import com.developergg.app.model.FacturaDetallePagoTemp;
import com.developergg.app.model.FacturaPago;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.FormaPago;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IFacturasDetallesPagoTempService;
import com.developergg.app.service.IFacturasPagoService;
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
	public String agregarPagoFacturaCXCAjax(Model model, HttpSession session,
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
	
	@PostMapping("/ajax/deletePagoCxC")
	public String eliminarPagoCxCAjax(HttpSession session,
			@RequestParam("idPagoFactura") Integer idPagoFactura) {
		FacturaPago pago = serviceFacturasPagos.buscarPorId(idPagoFactura);
		Factura factura = pago.getFactura();
		Double abonoAcct = factura.getAbono();
		factura.setAbono(abonoAcct-pago.getCantidad());
		serviceFacturas.guardar(factura);
		serviceFacturasPagos.eliminar(idPagoFactura);
		return "facturas/listaFacturasXC :: #responseDeletePago";
	}
	
	@PostMapping("/ajax/deletePagoFactura")
	public String eliminarPagoFacturaAjax(HttpSession session,
			@RequestParam("pago") Integer idPago) {
		serviceDetallePago.eliminar(idPago);
		return "facturas/factura :: #responseDeletePago";
	}
	
}
