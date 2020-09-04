package com.developergg.app.controller;

import java.text.DecimalFormat;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.developergg.app.model.FacturaTallerTemp;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.Taller;
import com.developergg.app.model.TallerDetalle;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IFacturasTalleresTempService;
import com.developergg.app.service.IFacturasTempService;
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
	
	private DecimalFormat df2 = new DecimalFormat("###.##");
	
	@PostMapping("/ajax/addDetailsTaller")
	public String agregarDetallesTallerInvoice(Model model, HttpSession session,
			@RequestParam(name = "idTaller") Integer idTaller) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FacturaTemp factura = serviceFacturasTemp.buscarPorUsuario(usuario);
		double itbis = 0.0;
		double subtotal = 0.0;
		double total = 0.0;
		double subTotalItbis = 0.0;
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
					String tempSv = "1." + factura.getComprobanteFiscal().getValor_itbis().intValue();
					Double precioTempSv = tallerDetalle.getSubtotal() / Double.parseDouble(tempSv);
					Double itBisTempSv = (tallerDetalle.getCantidad() * precioTempSv)
							* (factura.getComprobanteFiscal().getValor_itbis() / 100.00);
					precio = Double.parseDouble(df2.format(precioTempSv).replace(",", "."));
					itbis = Double.parseDouble(df2.format(itBisTempSv).replace(",", "."));
					subtotal = Double
							.parseDouble(df2.format((tallerDetalle.getCantidad() * precio)
									+ itbis).replace(",", "."));
					total += subtotal;
				} else {
					// realizamos las conversiones
					precio = tallerDetalle.getSubtotal();
					Double itBisTempServ = (tallerDetalle.getCantidad() * precio)
							* (factura.getComprobanteFiscal().getValor_itbis() / 100.00);
					precio = Double.parseDouble(df2.format(precio).replace(",", "."));
					itbis = Double.parseDouble(df2.format(itBisTempServ).replace(",", "."));
					subtotal = Double
							.parseDouble(df2.format((tallerDetalle.getCantidad() * precio)
									+ itbis).replace(",", "."));
					total = subtotal;
				}
			}else {
				precio = tallerDetalle.getPrecio(); //TODO: GGONZALEZ REVISAR
				total = tallerDetalle.getSubtotal();
			}
			
			subTotalItbis+=itbis;
			
			if(tallerDetalle.getArticulo()!=null) {
				facturaTallerTemp.setArticulo(tallerDetalle.getArticulo());
//				facturaTallerTemp.setTallerArticulo(tallerArticulo);
			}
			
			facturaTallerTemp.setFacturaTemp(factura);
			facturaTallerTemp.setCantidad(tallerDetalle.getCantidad());
			facturaTallerTemp.setPrecio(precio);
			facturaTallerTemp.setItbis(subTotalItbis);
			facturaTallerTemp.setSubtotal(total);
			facturaTallerTemp.setComprobanteFiscal(factura.getComprobanteFiscal());
			facturaTallerTemp.setDescripcion(tallerDetalle.getItem());
			facturaTallerTemp.setTaller(taller);
			facturaTallerTemp.setTallerDetalle(tallerDetalle);
			facturaTallerTemp.setUsuario(usuario);
			facturaTallerTemp.setAlmacen(usuario.getAlmacen());
			serviceFacturasTalleresTemp.guardar(facturaTallerTemp);
		}
		
		taller.setFacturaTemp(factura);
		factura.setTaller(taller);
		serviceFacturasTemp.guardar(factura);
		serviceTalleres.guardar(taller);
		return "facturas/factura :: #response";
	}
	
}
