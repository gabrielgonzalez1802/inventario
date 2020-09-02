package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Almacen;
import com.developergg.app.model.Perfil;
import com.developergg.app.model.Usuario;

public interface UsuariosRepository extends JpaRepository<Usuario, Integer> {
	Usuario findByUsername(String username);
	List<Usuario> findByAlmacenAndPrivilegio(Almacen almacen, Perfil privilegio);
}
