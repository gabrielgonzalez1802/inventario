package com.developergg.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.developergg.app.model.Compra;
import com.developergg.app.model.TempPagoCxp;

public interface TempPagoCxpRepository extends JpaRepository<TempPagoCxp, Integer> {
	List<TempPagoCxp> findByCompra(Compra compra);
}
