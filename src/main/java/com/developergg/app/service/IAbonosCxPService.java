package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.AbonoCxP;
import com.developergg.app.model.Almacen;
import com.developergg.app.model.Compra;
import com.developergg.app.model.Suplidor;

public interface IAbonosCxPService {
	AbonoCxP buscarPorId(Integer id);
	AbonoCxP buscarPorCompra(Compra compra);
	List<AbonoCxP> buscarPorAlmacen(Almacen almacen);
	List<AbonoCxP> buscarPorSuplidor(Suplidor suplidor);
	void guardar(AbonoCxP abonoCxP);
	void eliminar(AbonoCxP abonoCxP);
	void eliminar(Integer id);
}
