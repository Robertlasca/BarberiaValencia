package com.barber.valencia.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.barber.valencia.entity.Cliente;
import com.barber.valencia.entity.Proveedor;
import com.barber.valencia.entity.Servicio;
import com.barber.valencia.entity.Usuario;
import com.barber.valencia.repository.ServicioRepository;
import com.barber.valencia.repository.UsuarioRepository;

@Controller
@RequestMapping("/admi")
public class ServicioController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ServicioRepository servicioRepository;
	
	//Método para mostrar el formulario de registro
	@GetMapping(path = {"/servicio/nuevo"})
	public String mostrarFormularioServicio(Model model) {
		model.addAttribute("servicio",new Servicio());

		return "admi_registro_servicio";
	}
	
	//Método para guardar un registro servicio
	@PostMapping(path = {"/servicio/guardar"})
	public String guardarServicio(@ModelAttribute("servicio")Servicio servicio, RedirectAttributes mensaje) {
		//Se verifica que no exista el nombre del servicio registrado
		if(servicioRepository.existsByNombre(servicio.getNombre())) {
			//Si ya existe el nombre del servicio se envia el siguiente mensaje
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Incorrecto");
			mensaje.addFlashAttribute("alertMessage", "Existe servicio");
			//Se redirecciona a la misma vista
			return "redirect:/admi/servicio/nuevo";
		}else {
			//Se guarda el servicio en la base de datos
			servicioRepository.save(servicio);
			//Se envia el mensaje de confirmación
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "success");
			mensaje.addFlashAttribute("alertTitle", "Correcto");
			mensaje.addFlashAttribute("alertMessage", "Servicio registrado");
			//Se redirecciona a la misma vista
			return "redirect:/admi/servicio/nuevo";
			
		
		}
	}
	//Método para listar los servicios de la base de datos
	@GetMapping(path = {"/servicio/lista"})
	public String mostrarListaServicios(Model model) {
		//Se obtienen todos los servicios en una lista con el método findAll()
		List<Servicio> servicio= servicioRepository.findAll();
		//Se le asigna la lista al modelo
		model.addAttribute("servicios", servicio);
		//Se retorna un template
		return "admi_lista_servicio";
		
	}
	//Método para eliminar un registro servicio
	@PostMapping(path = {"/servicio/eliminar"})
	public String eliminarServicio(@RequestParam("idServicio") Integer idServicio,RedirectAttributes mensaje) {
		//Se envia el mensaje de confirmación
		mensaje.addFlashAttribute("alerta", "¡Alerta!");
		mensaje.addFlashAttribute("alertIcon", "success");
		mensaje.addFlashAttribute("alertTitle", "Correcto");
		mensaje.addFlashAttribute("alertMessage", "Servicio eliminado");
		//Se elimina el servicio  mediante el id
		servicioRepository.deleteById(idServicio);
		//Se retorna al template de la lista de los servicios
	    return "redirect:/admi/servicio/lista"; 
	}
	//Método para mostrar el template(formulario para actualizar)
	@GetMapping(path = {"/servicio/verActualizar/{id}"})
	public String mostrarServicioActualizar(@PathVariable("id") Integer id,Model model) {
		//Se recibe el id del servicio para buscarlo por PathVariable
		//Se guarda el servicio buscado
		Optional<Servicio> servicio = servicioRepository.findById(id);
		Servicio servicios = servicio.orElse(null);
		//Se le asigna el producto al modelo
		model.addAttribute("servicio",servicios);
		//Se retorna un template
		return "admi_actualizar_servicio";
		
	}
	
	
	@PostMapping(path = {"/servicio/actualizar"})
	public String actualizarServicio(@RequestParam("correoAdmi")String correoAdmi,  @RequestParam("contrasenaAdmi") String contrasenaAdmi
			,RedirectAttributes mensaje,@ModelAttribute("servicio")Servicio servicioE) {
		//Se busca un usuario mediante el email y la contraseña son credenciales del administrador
		Usuario user = usuarioRepository.findUsuarioByEmailAndContrasena(correoAdmi, contrasenaAdmi);
		//Si el usuario es igual a null entonces las credenciales son incorrectas
	    if (user == null) {
	    	//Se envía el mensaje para las credenciales incorrectas
	    	mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Error");
			mensaje.addFlashAttribute("alertMessage", "Contraseña o email incorrectos");
		
			//Se redirecciona a la lista de los servicios
			return "redirect:/admi/servicio/lista"; 
	    } else {
	    		//Se busca el servicio que se quiere actualizar mediante el nombre
				Servicio servicio=servicioRepository.findServicioByNombre(servicioE.getNombre());
				// Se modifica la descripción y el precio del servicio
				servicio.setDescripcion(servicioE.getDescripcion());
				servicio.setPrecio(servicioE.getPrecio());
				servicio.setDescuento(servicioE.getDescuento());
				//Se guardan los cambios en la base de datos
		    	servicioRepository.save(servicio);
		    	//Se envia el mensaje de confirmación
		    	mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success");
				mensaje.addFlashAttribute("alertTitle", "¡Exito!");
				mensaje.addFlashAttribute("alertMessage", "Registro actualizado");
				//Se redirecciona a la lista de los servicios
				return "redirect:/admi/servicio/lista"; 
	    		
	     }
	}
	
	
	
	
	

}
