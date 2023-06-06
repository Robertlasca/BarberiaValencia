package com.barber.valencia.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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
import com.barber.valencia.repository.Detalle_venta_repository;
import com.barber.valencia.repository.EmpleadoRepository;
import com.barber.valencia.repository.ServicioRepository;
import com.barber.valencia.repository.UsuarioRepository;
import com.barber.valencia.repository.Ventas_servicio_repository;

@Controller
@RequestMapping("/barber")
public class barberInicioController {
	// Mediante esta variable se obtiene la fecha actual
	private LocalDate fechaActual = LocalDate.now();
	Date fechaActualDate = Date.from(fechaActual.atStartOfDay(ZoneId.systemDefault()).toInstant());

	@Autowired
	private Detalle_venta_repository dtventaRepository;

	@Autowired
	private Ventas_servicio_repository ventaRepository;

	@Autowired
	private ServicioRepository servicioRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private CitaRepository citaRepository;

	@Autowired
	EmpleadoRepository empleadoRepository;

	@Autowired
	UsuarioRepository usuarioRepository;

	private Integer id;

	@GetMapping(path = { "/inicio/{id}" })
	public String mostrarInicio(@PathVariable("id") Integer idUsua) {
		id = idUsua;
		System.out.println("Este es el id del usuario" + id);
		return "barber_inicio";
	}

	@GetMapping(path = { "/inicio" })
	public String mostrarInicioBarber() {

		System.out.println("Este es el id del usuario" + id);
		return "barber_inicio";
	}

	// Método para mostrar el template(formulario para actualizar)
	@GetMapping(path = { "/datos" })
	public String mostrarActualizar(Model model) {
		// Se recibe el id del servicio para buscarlo por PathVariable
		// Se guarda el servicio buscado
		Optional<Usuario> user = usuarioRepository.findById(id);
		Usuario usuario = user.orElse(null);

		Empleado empleado = empleadoRepository.findEmpleadoByUsuario(usuario);

		// Se le asigna el producto al modelo
		model.addAttribute("empleado", empleado);
		// Se retorna un template
		return "barber_datos";

	}

	@PostMapping(path = { "/actualizar" })
	public String actualizarServicio(@RequestParam("correoAdmi") String correoAdmi,
			@RequestParam("contrasenaAdmi") String contrasenaAdmi,
			@RequestParam("contrasenaNueva") String contrasenaNueva, @RequestParam("telefono") String telefono,
			RedirectAttributes mensaje) {
		Optional<Usuario> user = usuarioRepository.findById(id);
		Usuario usuario = user.orElse(null);

		Empleado empleado = empleadoRepository.findEmpleadoByUsuario(usuario);

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
			return "redirect:/barber/datos";

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
				return "redirect:/barber/datos";
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
				return "redirect:/barber/datos";

			}

		}

	}

	// Citas
	@GetMapping(path = { "/cita/nuevo" })
	public String mostrarFormularioCita(Model model) {

		List<Servicio> servicios = servicioRepository.findAll();
		List<Cliente> cliente = clienteRepository.findAll();
		// Se le asigna la lista al modelo
		model.addAttribute("clientes", cliente);
		model.addAttribute("listaServicios", servicios);

		return "barber_registro_cita";

	}

	// Citas
	@GetMapping(path = { "/miscita/lista/{id}" })
	public String mostrarCita(Model model, @PathVariable("id") Integer idUsua) {
		id = idUsua;
		Optional<Usuario> user = usuarioRepository.findById(id);
		Usuario usuario = user.orElse(null);

		Empleado empleado = empleadoRepository.findEmpleadoByUsuario(usuario);

		// Se obtienen todas las citas en una lista con el método findAll()
		List<Cita> cita = citaRepository.findByEmpleadoAndFechaAndEstado(empleado, fechaActual.toString(), "Pendiente");

		// Se le asigna la lista al modelo
		model.addAttribute("lista", cita);

		return "barber_mis_citas";
	}

	// Citas
	@GetMapping(path = { "/miscita/lista" })
	public String mostrarCita(Model model, RedirectAttributes mensaje) {

		Optional<Usuario> user = usuarioRepository.findById(id);
		Usuario usuario = user.orElse(null);

		Empleado empleado = empleadoRepository.findEmpleadoByUsuario(usuario);
		List<Cita> cita = citaRepository.findByEmpleadoAndFechaAndEstado(empleado, fechaActual.toString(), "Pendiente");
		System.out.print("tamaño de lista"+cita.size());
		// Se obtienen todas las citas en una lista con el método findAll()
		if (cita.size() == 0) {
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Disculpa");
			mensaje.addFlashAttribute("alertMessage", "¡Por el momento no tienes citas!");
			return "redirect:/barber/cita/lista";

		} else {
			
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "success");
			mensaje.addFlashAttribute("alertTitle", "Revisas tus citas");
			mensaje.addFlashAttribute("alertMessage", "¡Hoy tienes citas!");
			return "redirect:/barber/miscita/listas";

		}

	}

	@GetMapping(path = { "/miscita/listas" })
	public String mostrarCitas(Model model) {
		// Se le asigna la lista al modelo
		Optional<Usuario> user = usuarioRepository.findById(id);
		Usuario usuario = user.orElse(null);

		Empleado empleado = empleadoRepository.findEmpleadoByUsuario(usuario);
		List<Cita> cita = citaRepository.findByEmpleadoAndFechaAndEstado(empleado, fechaActual.toString(), "Pendiente");
		model.addAttribute("lista", cita);

		return "barber_mis_citas";
	}

	// Método para guardar un registro de una cita
	@PostMapping(path = { "/cita/guardar" })
	public String guardarCita(@RequestParam("fecha") String fecha, @RequestParam("hora") String hora,
			@RequestParam("servi") Integer id_servicio, @RequestParam("cliente") Integer id_cliente,
			@RequestParam(value = "submitBtn", required = false) String submitBtn, RedirectAttributes mensaje) {
		Optional<Usuario> user = usuarioRepository.findById(id);
		Usuario usuario = user.orElse(null);

		Empleado empleado = empleadoRepository.findEmpleadoByUsuario(usuario);

		// Primero se verifica que no exista una cita con el mismo empleado a la misma
		// hora y fecha
		Cita citas = citaRepository.findCitaByEmpleadoAndFechaAndHora(empleado, fecha, hora);

		// Si la cita es igual null entonces quiere decir que se puede registrar la cita
		if (citas == null) {
			// Se instancia la cita
			Cita cita = new Cita();
			// Se busca el cliente al que le corresponde la cita
			Optional<Cliente> clientes = clienteRepository.findById(id_cliente);
			Cliente cliente = clientes.orElse(null);
			// Se busca el servicio que le corresponde a la cita
			Optional<Servicio> servicios = servicioRepository.findById(id_servicio);
			Servicio servicio = servicios.orElse(null);

			// If para verificar que boton fue presionado
			if (submitBtn != null && submitBtn.equals("agendar")) {
				// Verificamos que el id de cliente sea diferente de null
				if (id_cliente == 0) {
					// Le asignamos los datos a la cita
					cita.setEstado("Pendiente");
					cita.setFecha(fecha);
					cita.setPago("Pendiente");
					cita.setServicio(servicio);
					cita.setEmpleado(empleado);
					cita.setHora(hora);
					// Se guarda el registro en la base de datos
					citaRepository.save(cita);

				} else {
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

				}

				// Mensaje de confirmación

				mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success");
				mensaje.addFlashAttribute("alertTitle", "Exito");
				mensaje.addFlashAttribute("alertMessage", "Cita agendada");
				// Se redirecciona a la misma vista
				return "redirect:/barber/cita/nuevo";

			} else if (submitBtn != null && submitBtn.equals("agendarPagar")) {
				// Se crean los objetos correspondientes a la venta de un servicio
				Ventas_servicio vservicio = new Ventas_servicio();
				Detalle_venta_servicio dvServicio = new Detalle_venta_servicio();
				// Verificamos que el id de cliente sea diferente de null
				if (id_cliente == null) {
					// Le asignamos los datos a la cita
					cita.setEstado("Pendiente");
					cita.setFecha(fecha);
					cita.setPago("Pagada");
					cita.setServicio(servicio);
					cita.setEmpleado(empleado);
					cita.setHora(hora);
					// Se guarda el registro en la base de datos
					citaRepository.save(cita);

					// Se le asigna la fecha a la venta
					vservicio.setFecha_venta(fechaActual.toString());
					// Se guarda en la base de datos
					ventaRepository.save(vservicio);

					dvServicio.setVenta_servicio(vservicio);
					dvServicio.setServicio(servicio);
					dvServicio.setPrecio_total(servicio.getPrecio());
					// Se guarda en la base de datos
					dtventaRepository.save(dvServicio);

				} else {
					// Le asignamos los datos a la cita
					cita.setCliente(cliente);
					cita.setEstado("Pendiente");
					cita.setFecha(fecha);
					cita.setPago("Pagada");
					cita.setServicio(servicio);
					cita.setEmpleado(empleado);
					cita.setHora(hora);
					citaRepository.save(cita);

					// Se agrega el cliente
					vservicio.setCliente(cliente);
					vservicio.setFecha_venta(fechaActual.toString());
					// Se guarda en la base de datos
					ventaRepository.save(vservicio);

					dvServicio.setVenta_servicio(vservicio);
					dvServicio.setServicio(servicio);
					dvServicio.setPrecio_total(servicio.getPrecio());
					// Se guarda en la base de datos
					dtventaRepository.save(dvServicio);

				}

				// Mensaje confirmación
				mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success");
				mensaje.addFlashAttribute("alertTitle", "Exito");
				mensaje.addFlashAttribute("alertMessage", "Cita agendada");
				// Se redirecciona a la misma vista
				return "redirect:/barber/cita/nuevo";
			} else {

				return "barber_registro_cita";
			}
		} else {
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Error");
			mensaje.addFlashAttribute("alertMessage", "¡Ya tienes una cita esta fecha y esta hora!");
			// Se redirecciona a la misma vista
			return "redirect:/barber/cita/nuevo";
		}

	}

	// Mis citas
	// Método para listar las citas
	@GetMapping(path = { "/cita/lista" })
	public String mostrarListaCitas(Model model) {
		Optional<Usuario> user = usuarioRepository.findById(id);
		Usuario usuario = user.orElse(null);

		Empleado empleado = empleadoRepository.findEmpleadoByUsuario(usuario);

		// Se obtienen todas las citas en una lista con el método findAll()
		List<Cita> cita = citaRepository.findByEmpleado(empleado);
		// Se le asigna la lista al modelo
		model.addAttribute("lista", cita);
		// Se retorna un template
		return "barber_lista_citas";
	}

	// Método para eliminar un registro cita
	@PostMapping(path = { "/cita/eliminar" })
	public String eliminarCita(@RequestParam("id") Integer idCita, RedirectAttributes mensaje) {
		// Se busca la cita que se quiere eliminar
		Optional<Cita> citas = citaRepository.findById(idCita);
		Cita cita = citas.orElse(null);
		cita.setEmpleado(null);
		cita.setCliente(null);
		cita.setServicio(null);
		citaRepository.save(cita);
		// Se envía un mensaje de confirmación
		mensaje.addFlashAttribute("alerta", "¡Alerta!");
		mensaje.addFlashAttribute("alertIcon", "success");
		mensaje.addFlashAttribute("alertTitle", "Correcto");
		mensaje.addFlashAttribute("alertMessage", "Cita eliminada");
		// Se elimina la cita
		citaRepository.deleteById(idCita);

		// Se retorna a la lista de las citas
		return "redirect:/barber/cita/lista";
	}

	// Método para mostrar el template(formulario para actualizar)
	@GetMapping(path = { "/cita/verActualizar/{id}" })
	public String mostrarCitaActualizar(@PathVariable("id") Integer id_cita, Model model) {
		// Se recibe el id del cliente para buscarlo por PathVariable
		// Se guarda el cliente buscado
		Optional<Cita> cita = citaRepository.findById(id_cita);

		Cita citas = cita.orElse(null);
		// Se le asigna el cliente al modelo
		model.addAttribute("cita", citas);
		// Se retorna el template
		return "barber_actualizar_cita";

	}

	// Método para actualizar un registro cita
	@PostMapping(path = { "/cita/actualizar" })
	public String actualizarCita(@RequestParam("correoAdmi") String correoAdmi,
			@RequestParam("contrasenaAdmi") String contrasenaAdmi, @RequestParam("estado") String estado,
			RedirectAttributes mensaje, @ModelAttribute("cita") Cita citaE, @RequestParam("id") Integer idCita) {

		// Se busca un usuario mediante el email y la contraseña son credenciales del

		Usuario user = usuarioRepository.findUsuarioByEmailAndContrasena(correoAdmi, contrasenaAdmi);
		// Se busca la cita que quiere actualizar
		Optional<Cita> citas = citaRepository.findById(idCita);
		Cita cita = citas.orElse(null);

		// Se verifica el estado de la cita
		if (!estado.equalsIgnoreCase(cita.getEstado())) {

			// Si el usuario es igual a null entonces las credenciales son incorrectas
			if (user == null) {
				// Se envía el mensaje para las credenciales incorrectas
				mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "error");
				mensaje.addFlashAttribute("alertTitle", "Error");
				mensaje.addFlashAttribute("alertMessage", "Contraseña o email incorrectos");
				// Se redirecciona a la lista de los clientes
				return "redirect:/barber/cita/lista";
			} else {
				// Dependiendo del estado de la cita se modifican los atributos
				if (estado.equalsIgnoreCase("Cancelada")) {
					// Se envía un mensaje de confirmación
					mensaje.addFlashAttribute("alerta", "¡Alerta!");
					mensaje.addFlashAttribute("alertIcon", "success");
					mensaje.addFlashAttribute("alertTitle", "Correcto");
					mensaje.addFlashAttribute("alertMessage", "Cita actualizada");
					cita.setEstado("Cancelada");
					citaRepository.save(cita);

				} else if (estado.equalsIgnoreCase("Terminada")) {
					// Se envía un mensaje de confirmación
					mensaje.addFlashAttribute("alerta", "¡Alerta!");
					mensaje.addFlashAttribute("alertIcon", "success");
					mensaje.addFlashAttribute("alertTitle", "Correcto");
					mensaje.addFlashAttribute("alertMessage", "Cita actualizada");
					cita.setEstado("Terminada");
					cita.setPago("Pagada");
					citaRepository.save(cita);

				}

				// Redirección a la lista de los clientes
				return "redirect:/barber/cita/lista";

			}

		} else {
			// Se envía el mensaje para las credenciales incorrectas
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Error");
			mensaje.addFlashAttribute("alertMessage", "Esta cita ya tiene este estado");
			// Se redirecciona a la lista de los clientes
			return "redirect:/barber/cita/verActualizar/" + cita.getId_cita() + "";
		}

	}

}
