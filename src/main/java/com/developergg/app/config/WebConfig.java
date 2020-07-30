package com.developergg.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Value("${inventario.ruta.imagenes}")
	private String rutaImagenes;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/logos/**").addResourceLocations("file:/inventario-java/img/"); //Linux
		registry.addResourceHandler("/logos/**").addResourceLocations("file:"+rutaImagenes); //Windows
	}
}
