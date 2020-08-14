package com.developergg.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(name = "/condicionesPago")
public class CondicionesPagoController {
	public String listaCondicionesPago() {
		return "/";
	}
}
