package com.developergg.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/facturas")
public class FacturasController {

	@GetMapping("/create")
	public String create() {
		return "facturas/factura";
	}
	
}
