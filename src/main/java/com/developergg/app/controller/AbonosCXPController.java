package com.developergg.app.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.developergg.app.model.AbonoCxP;
import com.developergg.app.model.AbonoCxPDetalle;
import com.developergg.app.model.AbonoCxPPago;
import com.developergg.app.model.Compra;
import com.developergg.app.model.FormaPago;
import com.developergg.app.model.TempPagoCxp;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IAbonosCxPDetallesService;
import com.developergg.app.service.IAbonosCxPService;
import com.developergg.app.service.IAbonosCxPsPagosService;
import com.developergg.app.service.IComprasService;
import com.developergg.app.service.IFormasPagoService;
import com.developergg.app.service.ITempsPagosCxpService;

@Controller
@RequestMapping("/cxp")
public class AbonosCXPController {
	
	@Autowired
	private IAbonosCxPService serviceAbonosCxP;
	
	@Autowired
	private IAbonosCxPDetallesService serviceAbonosCxPDetalles;
	
	@Autowired
	private IFormasPagoService serviceFormasPago;
	
	@Autowired
	private IComprasService serviceCompras;
	
	@Autowired
	private ITempsPagosCxpService serviceTempsPagosCxP;
	
	@Autowired
	private IAbonosCxPsPagosService serviceAbonosCxPPagos;
	
	@GetMapping("/")
	public String listaCxP(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		//Verificamos las compras que no esten pagadas y esten procesadas
		List<Compra> compras = serviceCompras.buscarPorAlmacen(usuario.getAlmacen()).stream().
				filter(c -> 
				c.getEn_proceso() == 0 && c.getPagada() == 0 && c.getTotalRestante() > 0).
				collect(Collectors.toList());
		List<FormaPago> formasPago = serviceFormasPago.buscarPorAlmacen(usuario.getAlmacen()).
				stream().filter(p -> p.getId() > 0).collect(Collectors.toList());
		model.addAttribute("formasPago", formasPago);
		model.addAttribute("compras", compras);
		return "cxp/listaCxP";
	}
	
	@GetMapping("/ajax/loadCuerpoCxP/{id}")
	public String getPagoTemp(Model model, HttpSession session, @PathVariable("id") Integer id) {
		Compra compra = serviceCompras.buscarPorId(id);	
		//Verificamos la cuenta por cobrar de la compra
		AbonoCxP cxP = serviceAbonosCxP.buscarPorCompra(compra);
		//Verificamos los abonos de la cxp
		List<AbonoCxPDetalle> abonoDetalles = serviceAbonosCxPDetalles.buscarPorIngreso(cxP);
		//Verificamos los pagos temportales
		List<TempPagoCxp> tempPagoCxps = serviceTempsPagosCxP.buscarPorCompra(compra);
		
		if(abonoDetalles.isEmpty()) {
			abonoDetalles = new LinkedList<AbonoCxPDetalle>();
		}
		
		if(tempPagoCxps.isEmpty()) {
			tempPagoCxps = new LinkedList<>();
		}
		
		if(cxP!=null) {	
			Double precioTotal = cxP.getTotalCompra();
			Double totalRestaPagos = precioTotal-cxP.getTotalPagado();
			Double totalCambioPagos = 0.0;
			Integer mostrarCambio = 0;
			
			Double totalTempPagos = 0.0;
			if (!tempPagoCxps.isEmpty()) {
				for (TempPagoCxp tempPagoCxp : tempPagoCxps) {
					totalTempPagos += tempPagoCxp.getMonto();
				}
				if(totalTempPagos<totalRestaPagos) {
					totalRestaPagos -= totalTempPagos;
				}else if(totalTempPagos>totalRestaPagos) {
					totalCambioPagos = totalTempPagos -totalRestaPagos;
				}else {
					totalRestaPagos = 0.0;
				}
				
				//si el ultimo pago es efectivo mostramos el cambio
				if(tempPagoCxps.get(tempPagoCxps.size()-1).getFormaPago().getNombre().equalsIgnoreCase("efectivo")) {
					mostrarCambio = 1;
				}
			}
			
			model.addAttribute("nombreSuplidor",cxP.getSuplidor().getNombre());
			model.addAttribute("detalles", abonoDetalles);
			model.addAttribute("mostrarCambio", mostrarCambio);
			model.addAttribute("listaPagosTemp", tempPagoCxps);
			model.addAttribute("totalPagos", formato2d(totalTempPagos));
			model.addAttribute("totalRestaPagos", totalCambioPagos>0? 0.0:formato2d(totalRestaPagos));
			model.addAttribute("totalCambioPagos", formato2d(totalCambioPagos));
		}
		return "cxp/cuerpoPagoXP :: cuerpoPago";
	}
	
	@PostMapping("/ajax/deletePagoTemporalCxP")
	public String eliminarPagoCxP(@RequestParam("idPagoTemp") Integer idPagoTemp) {
		serviceTempsPagosCxP.eliminar(idPagoTemp);
		return "cxp/cuerpoPagoXP :: #responseDeletePago";
	}
	
	@PostMapping("ajax/deleteAllTemporalCxP")
	public String eliminarDatosTemporalesCxP(@RequestParam("idCompra") Integer idCompra) {
		Compra compra = serviceCompras.buscarPorId(idCompra);
		List<TempPagoCxp> pagosTemp = serviceTempsPagosCxP.buscarPorCompra(compra);
		serviceTempsPagosCxP.eliminar(pagosTemp);
		return "cxp/cuerpoPagoXP :: #responseDeletePago";
	}
	
	@PostMapping("/ajax/addPagoTempCXP")
	public String agregarPagoCXP(Model model, HttpSession session,
			@RequestParam("formaPago") Integer idFormaPago, @RequestParam("montoFormaPago") Double monto, 
			@RequestParam("idCompra") Integer idCompra) {
		Compra compra = serviceCompras.buscarPorId(idCompra);
		AbonoCxP abonoCxP = serviceAbonosCxP.buscarPorCompra(compra);
		FormaPago formaPago = serviceFormasPago.buscarPorId(idFormaPago);
		List<TempPagoCxp> listaPagos = serviceTempsPagosCxP.buscarPorCompra(compra); 
		if(listaPagos.isEmpty()) {
			listaPagos = new LinkedList<>();
		}
		
		TempPagoCxp pagoTemp = new TempPagoCxp();
		//pagos temporales
		Double acumulado = 0.0;
		boolean autorizado = false;
		Double restante = abonoCxP.getTotalCompra()-abonoCxP.getTotalPagado();
		
		if(!listaPagos.isEmpty()) {
			for (TempPagoCxp pago : listaPagos) {
				acumulado+=pago.getMonto();
			}
			if(!listaPagos.get(listaPagos.size()-1).getFormaPago().getNombre().equalsIgnoreCase("efectivo")) {
				//verificamos que el acumulado mas el nuevo monto no sea mayor al exacto de la factura
				if((acumulado+monto)<=restante) {
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
			}else if(!formaPago.getNombre().equalsIgnoreCase("Efectivo") && monto<=restante){
				autorizado = true;
			}		
		}
		
		pagoTemp.setCompra(compra);
		pagoTemp.setFormaPago(formaPago);
		pagoTemp.setMonto(monto);
		if(autorizado) {
			serviceTempsPagosCxP.guardar(pagoTemp);
		}
		model.addAttribute("responseAddPago", autorizado?1:0);
		return "cxp/listaCxP :: #responseAddPago";
	}
	
	@PostMapping("/ajax/guardarAbonoCxP")
	public String guardarAbonoCxP(HttpSession session, Model model, 
			@RequestParam(name = "idCompra") Integer idCompra, @RequestParam(name = "devuelto") Double devuelto) {
		Compra compra = serviceCompras.buscarPorId(idCompra);
		AbonoCxP cxP = serviceAbonosCxP.buscarPorCompra(compra);
		Double restante = compra.getTotalCompra()-compra.getTotalPagado();
		Double abonado = 0.0;
		if(devuelto.isNaN()) {
			devuelto = 0.0;
		}
		// buscamos los pagos temporales
		List<TempPagoCxp> pagosTemp = serviceTempsPagosCxP.buscarPorCompra(compra);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
		String now = LocalDateTime.now().format(formatter);

		// creamos el detalle
		AbonoCxPDetalle detalle = new AbonoCxPDetalle();
		detalle.setAbono(abonado);
		detalle.setFecha(new Date());
		detalle.setHora(now);
		detalle.setIngreso(cxP);
		serviceAbonosCxPDetalles.guardar(detalle);

		for (TempPagoCxp tempPagoCxp : pagosTemp) {
			abonado += tempPagoCxp.getMonto();
			AbonoCxPPago pago = new AbonoCxPPago();
			pago.setCompra(compra);
			pago.setFormaPago(tempPagoCxp.getFormaPago());
			pago.setMonto(tempPagoCxp.getMonto());
			pago.setDetalle(detalle);
			serviceAbonosCxPPagos.guardar(pago);
		}

		detalle.setAbono(abonado);
		serviceAbonosCxPDetalles.guardar(detalle);
		restante -= abonado;

		cxP.setTotalRestante(compra.getTotalNeto()-compra.getTotalPagado());
		cxP.setTotalPagado(compra.getTotalPagado()+abonado);
		cxP.setTotalDevuelto(compra.getTotalDevuelto()+devuelto);

		if (cxP.getTotalRestante() == 0) {
			compra.setPagada(1);
			compra.setTotalPagado(cxP.getTotalPagado());
			compra.setTotalRestante(cxP.getTotalRestante());
			compra.setTotalDevuelto(cxP.getTotalDevuelto() + devuelto);
		}else {
			compra.setTotalPagado(compra.getTotalPagado()+abonado);
			compra.setTotalDevuelto(compra.getTotalDevuelto()+devuelto);
			compra.setTotalRestante(compra.getTotalNeto()-compra.getTotalPagado());
		}
		serviceAbonosCxP.guardar(cxP);
		serviceCompras.guardar(compra);
		
		//Borramos pagos temporales
		serviceTempsPagosCxP.eliminar(pagosTemp);
		model.addAttribute("responseAddPago", 1);
		return "cxp/listaCxP :: #responseAddPago";
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
}
