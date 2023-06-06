package com.barber.valencia.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.barber.valencia.entity.Servicio;
import com.barber.valencia.repository.ServicioRepository;

@Controller
@RequestMapping("/barber")
public class barberServicioController {

	@Autowired
	ServicioRepository servicioRepository;
	
	//Método para listar los servicios de la base de datos
		@GetMapping(path = {"/servicio/lista"})
		public String mostrarListaServicios(Model model) {
			//Se obtienen todos los servicios en una lista con el método findAll()
			List<Servicio> servicio= servicioRepository.findAll();
			//Se le asigna la lista al modelo
			model.addAttribute("servicios", servicio);
			//Se retorna un template
			return "barber_lista_servicios";
			
		}
	
}
