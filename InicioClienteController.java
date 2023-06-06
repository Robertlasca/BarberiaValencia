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

import com.barber.valencia.entity.Cita;
import com.barber.valencia.entity.Cliente;
import com.barber.valencia.entity.Detalle_venta_servicio;
import com.barber.valencia.entity.Empleado;
import com.barber.valencia.entity.Servicio;
import com.barber.valencia.entity.Usuario;
import com.barber.valencia.entity.Ventas_servicio;
import com.barber.valencia.repository.CitaRepository;
import com.barber.valencia.repository.ClienteRepository;
import com.barber.valencia.repository.EmpleadoRepository;
import com.barber.valencia.repository.ServicioRepository;
import com.barber.valencia.repository.UsuarioRepository;

@Controller
@RequestMapping("/cliente")
public class InicioClienteController {

	@Autowired
	UsuarioRepository usuarioRepository;

	@Autowired
	ClienteRepository clienteRepository;

	@Autowired
	ServicioRepository servicioRepository;

	@Autowired
	CitaRepository citaRepository;

	@Autowired
	EmpleadoRepository empleadoRepository;

	Integer id;

	@GetMapping(path = { "/inicio" })
	public String mostrarFormularioCita(Model model) {

		return "inicio";
	}

	@GetMapping(path = { "/principal" })
	public String mostrarFormulario(Model model) {

		return "cliente_inicio";
	}

	@GetMapping(path = { "/principal/{id}" })
	public String mostrarInicio(@PathVariable("id") Integer idUsua) {
		id = idUsua;
		return "cliente_inicio";
	}

	// Método para mostrar el template(formulario para actualizar)
	@GetMapping(path = { "/datos" })
	public String mostrarActualizar(Model model) {
		// Se recibe el id del servicio para buscarlo por PathVariable
		// Se guarda el servicio buscado
		Optional<Usuario> user = usuarioRepository.findById(id);
		Usuario usuario = user.orElse(null);

		Cliente cliente = clienteRepository.findClienteByUsuario(usuario);

		// Se le asigna el producto al modelo
		model.addAttribute("cliente", cliente);
		// Se retorna un template
		return "cliente_datos";

	}

	@PostMapping(path = { "/actualizar" })
	public String actualizarServicio(@RequestParam("correoAdmi") String correoAdmi,
			@RequestParam("contrasenaAdmi") String contrasenaAdmi,
			@RequestParam("contrasenaNueva") String contrasenaNueva, @RequestParam("telefono") String telefono,
			RedirectAttributes mensaje) {
		Optional<Usuario> user = usuarioRepository.findById(id);
		Usuario usuario = user.orElse(null);

		Cliente cliente = clienteRepository.findClienteByUsuario(usuario);

		System.out.println("Esta es la contrasena nueva" + contrasenaNueva);
		System.out.println("Esta es la contrasena admi" + contrasenaAdmi);

		if (contrasenaNueva == null || contrasenaNueva == "") {
			usuario.setNumeroTelefono(telefono);
			usuarioRepository.save(usuario);
			// Se envia el mensaje de confirmación
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "success");
			mensaje.addFlashAttribute("alertTitle", "¡Exito!");
			mensaje.addFlashAttribute("alertMessage", "Registro actualizado");
			// Se redirecciona a la lista de los servicios
			return "redirect:/cliente/datos";

		} else {
			// Se busca un usuario mediante el email y la contraseña son credenciales del
			// administrador
			Usuario usuari = usuarioRepository.findUsuarioByEmailAndContrasena(correoAdmi, contrasenaAdmi);
			// Si el usuario es igual a null entonces las credenciales son incorrectas
			if (usuari == null) {
				// Se envia el mensaje de confirmación
				mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "error");
				mensaje.addFlashAttribute("alertTitle", "¡Error!");
				mensaje.addFlashAttribute("alertMessage", "Tu contraseña actual es incorrecta");
				// Se redirecciona a la lista de los servicios
				return "redirect:/cliente/datos";
			} else {

				usuario.setContrasena(contrasenaNueva);
				usuario.setNumeroTelefono(telefono);
				usuarioRepository.save(usuario);

				// Se envia el mensaje de confirmación
				mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success");
				mensaje.addFlashAttribute("alertTitle", "¡Exito!");
				mensaje.addFlashAttribute("alertMessage", "Registro actualizado");
				// Se redirecciona a la lista de los servicios
				return "redirect:/cliente/datos";

			}

		}

	}

	@GetMapping(path = { "/cita" })
	public String registrarCita(Model model) {

		Optional<Usuario> user = usuarioRepository.findById(id);
		Usuario usuario = user.orElse(null);

		Cliente clientes = clienteRepository.findClienteByUsuario(usuario);

		List<Servicio> servicios = servicioRepository.findAll();

		model.addAttribute("listaServicios", servicios);

		return "cliente_cita";
	}

	// Método para listar las citas
	@GetMapping(path = { "/cita/lista" })
	public String mostrarListaCitas(Model model, RedirectAttributes mensaje) {
		Optional<Usuario> user = usuarioRepository.findById(id);
		Usuario usuario = user.orElse(null);

		Cliente cliente = clienteRepository.findClienteByUsuario(usuario);
		// Se obtienen todas las citas en una lista con el método findAll()
		List<Cita> cita = citaRepository.findByClienteAndEstado(cliente, "Pendiente");
		if (cita.size() <= 0) {
			// Se envia el mensaje de confirmación
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "¡Lo siento!");
			mensaje.addFlashAttribute("alertMessage", "Por el momento no tienes citas");
			// Se redirecciona a la lista de los servicios
			return "redirect:/cliente/principal";

		} else {
			// Se le asigna la lista al modelo
			model.addAttribute("lista", cita);
			// Se retorna un template
			return "cliente_lista_cita";

		}

	}

	@GetMapping(path = { "/cita/lista/{id}" })
	public String mostrarListaCitas(@PathVariable("id") Integer idUsua, Model model, RedirectAttributes mensaje) {
		id = idUsua;
		Optional<Usuario> user = usuarioRepository.findById(id);
		Usuario usuario = user.orElse(null);

		Cliente cliente = clienteRepository.findClienteByUsuario(usuario);
		// Se obtienen todas las citas en una lista con el método findAll()
		List<Cita> cita = citaRepository.findByClienteAndEstado(cliente, "Pendiente");
		if (cita.size() <= 0) {
			// Se envia el mensaje de confirmación
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "¡Lo siento!");
			mensaje.addFlashAttribute("alertMessage", "Por el momento no tienes citas");
			// Se redirecciona a la lista de los servicios
			return "redirect:/cliente/principal";

		} else {
			// Se le asigna la lista al modelo
			model.addAttribute("lista", cita);
			// Se retorna un template
			return "cliente_lista_cita";

		}

	}

	// Método para eliminar un registro cita
	@PostMapping(path = { "/cita/eliminar" })
	public String eliminarCita(@RequestParam("id") Integer idCita, RedirectAttributes mensaje) {
		// Se busca la cita que se quiere eliminar
		Optional<Cita> citas = citaRepository.findById(idCita);
		Cita cita = citas.orElse(null);
		cita.setEstado("Cancelada");
		citaRepository.save(cita);
		// Se envía un mensaje de confirmación
		mensaje.addFlashAttribute("alerta", "¡Alerta!");
		mensaje.addFlashAttribute("alertIcon", "success");
		mensaje.addFlashAttribute("alertTitle", "Correcto");
		mensaje.addFlashAttribute("alertMessage", "Cita cancelada");

		// Se retorna a la lista de las citas
		return "redirect:/cliente/cita/lista";
	}

	// Método para guardar un registro de una cita
	@PostMapping(path = { "/cita/guardar" })
	public String guardarCita(@RequestParam("fecha") String fecha, @RequestParam("hora") String hora,
			@RequestParam("empleado") Integer id_empleado, @RequestParam("servi") Integer id_servicio,
			RedirectAttributes mensaje) {
		Optional<Usuario> user = usuarioRepository.findById(id);
		Usuario usuario = user.orElse(null);

		Cliente cliente = clienteRepository.findClienteByUsuario(usuario);
		System.out.println("Valor del barbero" + id_empleado);

		Optional<Empleado> empleados = empleadoRepository.findById(id_empleado);

		Empleado empleado = empleados.orElse(null);

		// Primero se verifica que no exista una cita con el mismo empleado a la misma
		// hora y fecha
		Cita citas = citaRepository.findCitaByEmpleadoAndFechaAndHora(empleado, fecha, hora);

		// Si la cita es igual null entonces quiere decir que se puede registrar la cita
		if (citas == null) {
			// Se instancia la cita
			Cita cita = new Cita();

			// Se busca el servicio que le corresponde a la cita
			Optional<Servicio> servicios = servicioRepository.findById(id_servicio);
			Servicio servicio = servicios.orElse(null);

			// Le asignamos los datos a la cita
			cita.setCliente(cliente);
			cita.setEstado("Pendiente");
			cita.setFecha(fecha);
			cita.setPago("Pendiente");
			cita.setServicio(servicio);
			cita.setEmpleado(empleado);
			cita.setHora(hora);
			// Se guarda el registro en la base de datos
			citaRepository.save(cita);

			// Mensaje de confirmación

			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "success");
			mensaje.addFlashAttribute("alertTitle", "Exito");
			mensaje.addFlashAttribute("alertMessage", "Cita agendada");
			// Se redirecciona a la misma vista
			return "redirect:/cliente/cita";

		} else {
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Error");
			mensaje.addFlashAttribute("alertMessage", "Empleado no disponible en esta fecha y hora");
			// Se redirecciona a la misma vista
			return "redirect:/cliente/cita";
		}

	}

}
