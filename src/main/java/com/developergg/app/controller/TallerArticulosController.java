package com.developergg.app.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.developergg.app.model.Articulo;
import com.developergg.app.model.ArticuloAjuste;
import com.developergg.app.model.FacturaTallerTemp;
import com.developergg.app.model.Taller;
import com.developergg.app.model.TallerArticulo;
import com.developergg.app.model.TallerDetalle;
import com.developergg.app.model.Usuario;
import com.developergg.app.service.IArticulosAjustesService;
import com.developergg.app.service.IFacturasTalleresTempService;
import com.developergg.app.service.ITalleresArticulosService;
import com.developergg.app.service.ITalleresDetallesService;

@Controller
@RequestMapping("/tallerArticulos")
public class TallerArticulosController {
	
	@Autowired
	private ITalleresArticulosService serviceTalleresArticulos;
	
	@Autowired
	private IArticulosAjustesService serviceArticulosAjustes;
	
	@Autowired
	private ITalleresDetallesService serviceTalleresDetalles;
	
	@Autowired
	private IFacturasTalleresTempService serviceFacturasTalleresTemp;
	
	@GetMapping("/")
	public String listaTallerArticulos(Model model, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		TallerArticulo tallerArticulo = new TallerArticulo();
		List<TallerArticulo> listaTalleresArticulos = serviceTalleresArticulos.
				buscarPorAlmacen(usuario.getAlmacen()).stream().
				filter(a -> a.getCantidad() > 0).collect(Collectors.toList());
		if(!listaTalleresArticulos.isEmpty()) {
			for (TallerArticulo item : listaTalleresArticulos) {
				if(item.getArticulo() == null) {
					item.setArticulo(new Articulo());
				}
				if(item.getTaller() == null) {
					item.setTaller(new Taller());
				}
			}
		}
		model.addAttribute("listaTalleresArticulos", listaTalleresArticulos);
		model.addAttribute("tallerArticulo", tallerArticulo);
		return "talleres/listaArticulos";
	}
	
	@GetMapping("/delete/{id}")
	public String borrarArticuloTaller(@PathVariable("id") Integer id, HttpSession session,
			RedirectAttributes attributes) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		TallerArticulo tallerArticulo = serviceTalleresArticulos.buscarPorId(id);
		
		if(tallerArticulo != null) {
			//Si el articulo viene del inventario volvera al mismo como una entrada
			if(tallerArticulo.getArticulo()!=null) {
				if(tallerArticulo.getCantidad()>0) {
					ArticuloAjuste articuloAjuste = new ArticuloAjuste();
					articuloAjuste.setAlmacen(usuario.getAlmacen());
					articuloAjuste.setArticulo(tallerArticulo.getArticulo());
					articuloAjuste.setCantidad(tallerArticulo.getCantidad());
					articuloAjuste.setCosto(tallerArticulo.getCosto());
					
					// verificar si el registro tiene inventario
					List<ArticuloAjuste> lista = serviceArticulosAjustes.buscarPorArticuloYAlmacen(tallerArticulo.getArticulo(), usuario.getAlmacen());
					if(lista.isEmpty()) {
						articuloAjuste.setExistencia(articuloAjuste.getCantidad());
						articuloAjuste.setDisponible(articuloAjuste.getCantidad());
						articuloAjuste.setFecha(new Date());
						articuloAjuste.setUsuario(usuario);
						serviceArticulosAjustes.guardar(articuloAjuste);
					}else {
						//ordenamos el ultimo elemento de la lista
						ArticuloAjuste newArticuloAjuste = lista.get(lista.size()-1);
						ArticuloAjuste newArticuloAjusteDefinitive = new ArticuloAjuste();
						newArticuloAjusteDefinitive.setAlmacen(usuario.getAlmacen());
						newArticuloAjusteDefinitive.setArticulo(tallerArticulo.getArticulo());
						newArticuloAjusteDefinitive.setCosto(tallerArticulo.getArticulo().getCosto());
						newArticuloAjusteDefinitive.setFecha(new Date());
						newArticuloAjusteDefinitive.setUsuario(usuario);
						newArticuloAjusteDefinitive.setTipoMovimiento("Entrada");
						newArticuloAjusteDefinitive.setCantidad(tallerArticulo.getCantidad());
						newArticuloAjusteDefinitive.setExistencia(newArticuloAjuste.getDisponible());
						newArticuloAjusteDefinitive.setDisponible(newArticuloAjusteDefinitive.getExistencia()+newArticuloAjusteDefinitive.getCantidad());	
						newArticuloAjusteDefinitive.setNo_factura(articuloAjuste.getNo_factura());
						serviceArticulosAjustes.guardar(newArticuloAjusteDefinitive);
						if(newArticuloAjusteDefinitive.getId()!=null) {
							//Borramos registros temporales
							List<TallerDetalle> tallerDetalles = serviceTalleresDetalles.buscarPorTallerArticulo(tallerArticulo);
							if(!tallerDetalles.isEmpty()) {
								for (TallerDetalle tallerDetalle : tallerDetalles) {
									//Borramos registros temporales en la factura
									List<FacturaTallerTemp> listaFacturaTalleresTemp = serviceFacturasTalleresTemp.buscarPorTallerDetalle(tallerDetalle);
									if(!listaFacturaTalleresTemp.isEmpty()) {
										for (FacturaTallerTemp talleresTemp : listaFacturaTalleresTemp) {
											serviceFacturasTalleresTemp.eliminar(talleresTemp);
										}
									}
								}
								serviceTalleresDetalles.eliminar(tallerDetalles);
							}
							serviceTalleresArticulos.eliminar(id);
						}
					}
				}else {
					attributes.addFlashAttribute("msgError", "No se puede borrar registros en 0");
				}
			}else {
				List<TallerDetalle> tallerDetalles = serviceTalleresDetalles.buscarPorTallerArticulo(tallerArticulo);
				if(!tallerDetalles.isEmpty()) {
					//verificamos si hay registros temporales que utilicen el articulo
					for (TallerDetalle tallerDetalle : tallerDetalles) {
						List<FacturaTallerTemp> listaFacturaTalleresTemp = serviceFacturasTalleresTemp.buscarPorTallerDetalle(tallerDetalle);
						if(!listaFacturaTalleresTemp.isEmpty()) {
							for (FacturaTallerTemp talleresTemp : listaFacturaTalleresTemp) {
								serviceFacturasTalleresTemp.eliminar(talleresTemp);
							}
						}
					}
					serviceTalleresDetalles.eliminar(tallerDetalles);
				}
				serviceTalleresArticulos.eliminar(id);
			}
		}
		return "redirect:/tallerArticulos/";
	}
	
	@PostMapping("/ajax/addTallerArticulo/")
	public String addTaller(Model model, @ModelAttribute("tallerArticulo") TallerArticulo tallerArticulo, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
	    String now = LocalDateTime.now().format(formatter);
		tallerArticulo.setFecha(new Date());
		tallerArticulo.setHora(now);
		tallerArticulo.setUsuario(usuario);
		tallerArticulo.setAlmacen(usuario.getAlmacen());
		serviceTalleresArticulos.guardar(tallerArticulo);
		model.addAttribute("responseAddRecepcion", tallerArticulo.getId()!=null?1:0);
		return "talleres/listaArticulos :: #responseAddRecepcion";
	}
	
	@GetMapping("/ajax/getInfo/{tallerArticuloId}")
	public String getIngreso(Model model, @PathVariable(name = "tallerArticuloId") Integer tallerArticuloId) {
		TallerArticulo item = serviceTalleresArticulos.buscarPorId(tallerArticuloId);
		if(item==null) {
			item = new TallerArticulo();
			model.addAttribute("disponible", 0);
			model.addAttribute("precioArticulo", 0);
			model.addAttribute("costoArticulo", 0);
			model.addAttribute("idArticulo", 0);
		}else {
			model.addAttribute("disponible", item.getCantidad());
			model.addAttribute("precioArticulo", item.getPrecio());
			if(item.getArticulo()==null) {
				model.addAttribute("costoArticulo", 0);
				model.addAttribute("idArticulo", 0);
			}else {
				model.addAttribute("costoArticulo", item.getArticulo().getCosto());
				model.addAttribute("idArticulo", item.getArticulo().getId());
			}
		}
		return "talleres/taller :: #infoSelection";
	}
}
