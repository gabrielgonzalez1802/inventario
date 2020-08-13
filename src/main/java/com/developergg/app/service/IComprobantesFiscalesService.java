package com.developergg.app.service;

import java.util.List;

import com.developergg.app.model.ComprobanteFiscal;
import com.developergg.app.model.Propietario;

public interface IComprobantesFiscalesService {
	ComprobanteFiscal buscarPorId(Integer idComprobanteFiscal);
	List<ComprobanteFiscal> buscarPorTienda(Propietario tienda);
	void guardar(ComprobanteFiscal comprobanteFiscal);
	void eliminar(Integer idComprobanteFiscal);
}
