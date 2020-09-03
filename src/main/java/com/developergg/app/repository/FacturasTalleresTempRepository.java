package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.FacturaTallerTemp;
import com.developergg.app.model.FacturaTemp;
import com.developergg.app.model.Taller;
import com.developergg.app.model.Usuario;

public interface FacturasTalleresTempRepository extends JpaRepository<FacturaTallerTemp, Integer> {
	List<FacturaTallerTemp> findByTaller(Taller taller);
	List<FacturaTallerTemp> findByUsuarioAndTaller(Usuario usuario, Taller taller);
	List<FacturaTallerTemp> findByFacturaTemp(FacturaTemp facturaTemp);
}
