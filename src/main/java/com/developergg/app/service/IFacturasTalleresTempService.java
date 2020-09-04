package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.FacturaTallerTemp;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.Taller;
import com.developergg.app.model.TallerDetalle;
import com.developergg.app.model.Usuario;

public interface IFacturasTalleresTempService {
	FacturaTallerTemp buscarPorId(Integer id);
	List<FacturaTallerTemp> buscarPorTaller(Taller taller);
	List<FacturaTallerTemp> BuscarPorUsuarioTaller(Usuario usuario, Taller taller);
	List<FacturaTallerTemp> buscarPorFacturaTemp(FacturaTemp facturaTemp);
	List<FacturaTallerTemp> buscarPorTallerDetalle(TallerDetalle tallerDetalle);
	void guardar(FacturaTallerTemp facturaTallerTemp);
	void eliminar(Integer id);
	void eliminar(FacturaTallerTemp facturaTallerTemp);
	void elminar(List<FacturaTallerTemp> listaFacturaTallerTemp);
}
