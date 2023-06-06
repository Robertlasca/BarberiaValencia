package com.barber.valencia.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.barber.valencia.entity.Cita;
import com.barber.valencia.entity.Cliente;
import com.barber.valencia.entity.Empleado;
import com.barber.valencia.entity.Usuario;
import com.barber.valencia.repository.CitaRepository;
import com.barber.valencia.repository.ClienteRepository;
import com.barber.valencia.repository.EmpleadoRepository;
import com.barber.valencia.repository.UsuarioRepository;

@Controller
@RequestMapping("/admi")
public class AppControllers {

	private final UsuarioRepository usuarioRepository;
	private final CitaRepository citaRepository;
	private final EmpleadoRepository empleadoRepository;
	private final ClienteRepository clienteRepository;
	// Mediante esta variable se obtiene la fecha actual
	private LocalDate fechaActual = LocalDate.now();
	

	@Autowired
	public AppControllers(UsuarioRepository usuarioRepository,CitaRepository citaRepository,EmpleadoRepository empleadoRepository,ClienteRepository clienteRepository) {
		this.usuarioRepository = usuarioRepository;
		this.empleadoRepository=empleadoRepository;
		this.citaRepository=citaRepository;
		this.clienteRepository=clienteRepository;
	}

	@GetMapping(path = { "/login", "/" })
	public String verLogin(Model model) {

		return "inicio_cliente";
	}

	@PostMapping(path = { "/login/usuario", "/" })
	public String login(@RequestParam String email, @RequestParam String contrasena, RedirectAttributes mensaje,
			Model model) {

		Usuario user = usuarioRepository.findUsuarioByEmailAndContrasena(email, contrasena);

		if (user == null) {
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "¡Hola!");
			mensaje.addFlashAttribute("alertMessage", "Contraseña o email incorrectos");

			return "redirect:/admi/login";
		} else {

			if (user.getRol().equalsIgnoreCase("Administrador")) {
				mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success");
				mensaje.addFlashAttribute("alertTitle", "¡Hola!");
				mensaje.addFlashAttribute("alertMessage", "Bienvenido");
				model.addAttribute("usuario", user);
				return "redirect:/admi/inicio/" + user.getId_usuario() + "";

			} else if (user.getRol().equalsIgnoreCase("Barbero")) {
				Empleado empleado = empleadoRepository.findEmpleadoByUsuario(user);
				List<Cita> cita = citaRepository.findByEmpleadoAndFechaAndEstado(empleado, fechaActual.toString(), "Pendiente");
				System.out.println("Este es el tamaño de la cita"+cita.size());

				// Se obtienen todas las citas en una lista con el método findAll()
				if(cita.size()!=0) {
					mensaje.addFlashAttribute("alerta", "¡Alerta!");
					mensaje.addFlashAttribute("alertIcon", "success");
					mensaje.addFlashAttribute("alertTitle", "Revisa tus citas");
					mensaje.addFlashAttribute("alertMessage", "¡Hoy tienes citas!");
					return "redirect:/barber/miscita/lista/" + user.getId_usuario() + "";
				}else {
					mensaje.addFlashAttribute("alerta", "¡Alerta!");
					mensaje.addFlashAttribute("alertIcon", "success");
					mensaje.addFlashAttribute("alertTitle", "¡Hola!");
					mensaje.addFlashAttribute("alertMessage", "Bienvenido");
					return "redirect:/barber/inicio/" + user.getId_usuario() + "";
					
				}
				

			}  else if (user.getRol().equalsIgnoreCase("Encargado de mostrador")) {
				mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success");
				mensaje.addFlashAttribute("alertTitle", "¡Hola!");
				mensaje.addFlashAttribute("alertMessage", "Bienvenido");
				return "redirect:/em/inicio/" + user.getId_usuario() + "";

			}else if (user.getRol().equalsIgnoreCase("Cliente")) {
				
				Cliente cliente = clienteRepository.findClienteByUsuario(user);
				List<Cita> cita = citaRepository.findByClienteAndFechaAndEstado(cliente, fechaActual.toString(), "Pendiente");
				System.out.println("Este es el tamaño de la cita"+cita.size());
				
				if(cita.size()!=0) {
					mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success"); 
				mensaje.addFlashAttribute("alertTitle", "Revisa tus citas");
				mensaje.addFlashAttribute("alertMessage", "¡Hoy tienes citas!");
				
				return "redirect:/cliente/cita/lista/" + user.getId_usuario() + "";
					
				}else {
					mensaje.addFlashAttribute("alerta", "¡Alerta!");
					mensaje.addFlashAttribute("alertIcon", "success");
					mensaje.addFlashAttribute("alertTitle", "¡Hola!");
					mensaje.addFlashAttribute("alertMessage", "Bienvenido");
					return "redirect:/cliente/principal/" + user.getId_usuario() + "";
					
					
				}
				
			

			}else {
				mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success");
				mensaje.addFlashAttribute("alertTitle", "¡Hola!");
				mensaje.addFlashAttribute("alertMessage", "Bienvenido");
				return "redirect:/barber/inicio/" + user.getId_usuario() + "";

			}

		}
	}
	
	


}
