package com.developergg.app.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.developergg.app.model.FacturaDetallePagoTemp;
import com.developergg.app.model.FacturaTallerTemp;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.FormaPago;
import com.developergg.app.model.Taller;
import com.developergg.app.model.TallerDetalle;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IFacturasDetallesPagoTempService;
import com.developergg.app.service.IFacturasTalleresTempService;
import com.developergg.app.service.IFacturasTempService;
import com.developergg.app.service.IFormasPagoService;
import com.developergg.app.service.ITalleresDetallesService;
import com.developergg.app.service.ITalleresService;

@Controller
@RequestMapping("/facturasTalleresTemp")
public class FacturasTalleresTempController {
	
	@Autowired
	private IFacturasTempService serviceFacturasTemp; 
	
	@Autowired
	private ITalleresService serviceTalleres;
	
	@Autowired
	private ITalleresDetallesService serviceTalleresDetalles;
	
	@Autowired
	private IFacturasTalleresTempService serviceFacturasTalleresTemp;
	
	@Autowired
	private IFormasPagoService serviceFormasPago;
	
	@Autowired
	private IFacturasDetallesPagoTempService serviceDetallePagoTemp;
		
	@PostMapping("/ajax/addDetailsTaller")
	public String agregarDetallesTallerInvoice(Model model, HttpSession session,
			@RequestParam(name = "idTaller") Integer idTaller) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaTemp factura = serviceFacturasTemp.buscarPorUsuario(usuario);
		double itBis = 0.0;
		double precio =0.0;
		
		Taller taller = serviceTalleres.buscarPorId(idTaller);
		List<TallerDetalle> listaDetallesTalleres = serviceTalleresDetalles.buscarPorTaller(taller);
		
		for (TallerDetalle tallerDetalle : listaDetallesTalleres) {
			
			FacturaTallerTemp facturaTallerTemp = new FacturaTallerTemp();
			
			//Verificamos si el comprobante fiscal paga itbis
			if (factura.getComprobanteFiscal().getPaga_itbis() == 1) {
				// verificamos si el comprobante fiscal incluye el itbis en el precio
				if (factura.getComprobanteFiscal().getIncluye_itbis() == 1) {
					// realizamos las conversiones
					String temp = "1." + factura.getComprobanteFiscal().getValor_itbis().intValue();
					precio = formato2d(tallerDetalle.getSubtotal() / Double.parseDouble(temp));
					itBis = formato2d(tallerDetalle.getSubtotal() - precio);
				} else {
					// realizamos las conversiones
					precio = tallerDetalle.getSubtotal();
					itBis= formato2d(precio * (factura.getComprobanteFiscal().getValor_itbis()/100.00)); 
				}
			}else {
				precio = tallerDetalle.getPrecio();
				itBis = 0.0;
			}
						
			if(tallerDetalle.getArticulo()!=null) {
				facturaTallerTemp.setArticulo(tallerDetalle.getArticulo());
			}
			
			int cant = tallerDetalle.getCantidad();
			
			facturaTallerTemp.setFacturaTemp(factura);
			facturaTallerTemp.setCantidad(cant);
			facturaTallerTemp.setPrecio(precio);
			facturaTallerTemp.setItbis(itBis);
			facturaTallerTemp.setSubtotal((cant * precio) + (cant * itBis));
			facturaTallerTemp.setComprobanteFiscal(factura.getComprobanteFiscal());
			facturaTallerTemp.setDescripcion(tallerDetalle.getItem());
			facturaTallerTemp.setTaller(taller);
			facturaTallerTemp.setTallerDetalle(tallerDetalle);
			facturaTallerTemp.setUsuario(usuario);
			facturaTallerTemp.setAlmacen(usuario.getAlmacen());
			facturaTallerTemp.setInitialPrice(tallerDetalle.getPrecio());
			serviceFacturasTalleresTemp.guardar(facturaTallerTemp);
		}
		
		//Verificamos si hay algun avance en el taller y si la condicion de pago es contado
		if(taller.getAvance().doubleValue()>0 && factura.getCondicionPago().getDia() == 0) {
			//Si tiene avance el creamos la forma de pago avance taller con el monto del avance
			FacturaDetallePagoTemp pagoTemp = new FacturaDetallePagoTemp();
			FormaPago formaPago = serviceFormasPago.buscarPorId(0);
			pagoTemp.setFacturaTemp(factura);
			pagoTemp.setFormaPago(formaPago);
			pagoTemp.setMonto(taller.getAvance());
			serviceDetallePagoTemp.guardar(pagoTemp);
		}
		
		taller.setFacturaTemp(factura);
		factura.setTaller(taller);
		serviceFacturasTemp.guardar(factura);
		serviceTalleres.guardar(taller);
		model.addAttribute("infoTaller", taller);
		return "facturas/factura :: #responseInfoClienteTaller";
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
}
