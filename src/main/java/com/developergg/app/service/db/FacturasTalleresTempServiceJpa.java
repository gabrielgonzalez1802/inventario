package com.developergg.app.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developergg.app.model.FacturaTallerTemp;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.Taller;
import com.developergg.app.model.TallerDetalle;
import com.developergg.app.model.Usuario;
import com.developergg.app.repository.FacturasTalleresTempRepository;
import com.developergg.app.service.IFacturasTalleresTempService;

@Service
public class FacturasTalleresTempServiceJpa implements IFacturasTalleresTempService{
	
	@Autowired
	private FacturasTalleresTempRepository repo;

	@Override
	public FacturaTallerTemp buscarPorId(Integer id) {
		Optional<FacturaTallerTemp> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<FacturaTallerTemp> buscarPorTaller(Taller taller) {
		return repo.findByTaller(taller);
	}

	@Override
	public void guardar(FacturaTallerTemp facturaTallerTemp) {
		repo.save(facturaTallerTemp);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<FacturaTallerTemp> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public List<FacturaTallerTemp> BuscarPorUsuarioTaller(Usuario usuario, Taller taller) {
		return repo.findByUsuarioAndTaller(usuario, taller);
	}

	@Override
	public List<FacturaTallerTemp> buscarPorFacturaTemp(FacturaTemp facturaTemp) {
		return repo.findByFacturaTemp(facturaTemp);
	}

	@Override
	public void elminar(List<FacturaTallerTemp> listaFacturaTallerTemp) {
		repo.deleteAll(listaFacturaTallerTemp);
	}

	@Override
	public List<FacturaTallerTemp> buscarPorTallerDetalle(TallerDetalle tallerDetalle) {
		return repo.findByTallerDetalle(tallerDetalle);
	}

	@Override
	public void eliminar(FacturaTallerTemp facturaTallerTemp) {
		repo.delete(facturaTallerTemp);
	}

}
