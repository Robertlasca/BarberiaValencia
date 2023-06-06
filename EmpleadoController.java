package com.barber.valencia.controller;
import com.barber.valencia.entity.Empleado;

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

import com.barber.valencia.entity.Empleado;
import com.barber.valencia.entity.Usuario;
import com.barber.valencia.repository.EmpleadoRepository;
import com.barber.valencia.repository.UsuarioRepository;
import org.springframework.web.servlet.ModelAndView;
@Controller
@RequestMapping("/admi")
public class EmpleadoController {
	
	private final UsuarioRepository usuarioRepository;
	private final EmpleadoRepository empleadoRepository;
	private final EmpleadoRepository empleados;
	
	
	@Autowired
    public EmpleadoController(UsuarioRepository usuarioRepository, EmpleadoRepository empleadoRepository,EmpleadoRepository empleados) {
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.empleados = empleados;
    }
	//Método para mostrar el formulario de registro
	@GetMapping(path = {"/empleado/nuevo"})
	public String mostrarFormularioEmpleado(Model model) {
		model.addAttribute("usuario",new Usuario());
		model.addAttribute("empleado", new Empleado());
		
		return "admi_registro_empleado";
	}
	
	//Método para listar los empleados de la base de datos
	@GetMapping(path = {"/empleado/lista"})
	public String mostrarListaEmpleado(Model model) {
		//Se obtienen todos los clientes en una lista con el método findAll()
		List<Empleado> empleado = empleadoRepository.findAll();
		//Se le asigna la lista al modelo
        model.addAttribute("empleados", empleado);
        //Se retorna un template
		return "admi_lista_empleados";
		
	}
	
	//Método para mostrar el template(formulario para actualizar)
	@GetMapping(path = {"/empleado/verActualizar/{id}"})
	public String mostrarEmpleadoActualizar(@PathVariable("id") Integer id,Model model) {
		//Se recibe el id del empleado para buscarlo por PathVariable
		//Se guarda el empleado buscado
		Optional<Empleado> empleadose = empleados.findById(id);
		Empleado empleado = empleadose.orElse(null);
		//Se le asigna el cliente al modelo
		model.addAttribute("empleado", empleado);
		//Se retorna el template
		return "admi_actualizar_empleado";
		
	}
	
	//Método para actualizar un registro empleado
	@PostMapping(path = {"/empleado/actualizar"})
	public String actualizarEmpleado(@RequestParam("correoAdmi")String correoAdmi,  @RequestParam("contrasenaAdmi") String contrasenaAdmi,
			@RequestParam("especialidad") String especialidad,@RequestParam("rol") String rol,@RequestParam("contrasena") String contrasena,RedirectAttributes mensaje,@ModelAttribute("empleado")Empleado empleadoE) {
		//Se busca un usuario mediante el email y la contraseña son credenciales del administrador
		Usuario user = usuarioRepository.findUsuarioByEmailAndContrasena(correoAdmi, contrasenaAdmi);
		//Si el usuario es igual a null entonces las credenciales son incorrectas
		if (user == null) {
			//Se envía el mensaje para las credenciales incorrectas
	    	mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Error");
			mensaje.addFlashAttribute("alertMessage", "Contraseña o email incorrectos");
			
			//Se redirecciona a la lista de empleados
		    return "redirect:/admi/empleado/lista"; 
	    } else {
	    	//Se busca el usuario que se queire actualizar mediante el Email
	    	Usuario usert=usuarioRepository.findUsuarioByEmail(empleadoE.getUsuario().getEmail());
	    	//Despues buscamos el empleado que le corresponde a ese usuario
	    	Empleado emplead=empleadoRepository.findEmpleadoByUsuario(usert);
	    	//Si la contraseña del empleado es un campo vacío quiere decir que no se quiere cambiar
	    	if(contrasena == "") {
	    		//Se modifica los atributos correspondientes rol, especialidad y teléfono
	    		emplead.setEspecialidad(especialidad);
		    	emplead.getUsuario().setRol(rol);
		    	emplead.getUsuario().setNumeroTelefono(empleadoE.getUsuario().getNumeroTelefono());
		    	
		    	//Se envia un mensaje de confirmación
		    	mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success");
				mensaje.addFlashAttribute("alertTitle", "¡Exito!");
				mensaje.addFlashAttribute("alertMessage", "Registro actualizado");
				
				//Se guarda el empleado con los cambios en la base de datos
				empleadoRepository.save(emplead);
				// Redirección a la lista de los empleados
				return "redirect:/admi/empleado/lista"; 
				
	    		
	    	}else {
	    		//Se modifica los atributos correspondientes rol, especialidad, contraseña y teléfono
	    		emplead.setEspecialidad(especialidad);
		    	emplead.getUsuario().setRol(rol);
		    	emplead.getUsuario().setContrasena(contrasena);
		    	emplead.getUsuario().setNumeroTelefono(empleadoE.getUsuario().getNumeroTelefono());
		    	//Se guarda el empleado con los cambios en la base de datos
		    	empleadoRepository.save(emplead);
		    	//Se envia un mensaje de confirmación
		    	mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success");
				mensaje.addFlashAttribute("alertTitle", "¡Exito!");
				mensaje.addFlashAttribute("alertMessage", "Registro actualizado");
				// Redirección a la lista de los empleados
				return "redirect:/admi/empleado/lista"; 
	    		
	    	}
	    	
	    	
	    }
	}
	
	//Método para eliminar un registro empleado
	@PostMapping(path = {"/empleado/eliminar"})
	public String eliminarEmpleado(@RequestParam("idEmpleado") Integer idEmpleado, @RequestParam("idUsuario") Integer idUsuario,RedirectAttributes mensaje) {
		//Se envía un mensaje de confirmación
		mensaje.addFlashAttribute("alerta", "¡Alerta!");
		mensaje.addFlashAttribute("alertIcon", "success");
		mensaje.addFlashAttribute("alertTitle", "Correcto");
		mensaje.addFlashAttribute("alertMessage", "Empleado eliminado");
		//Se elimina el empleado mediante el id
		empleadoRepository.deleteById(idEmpleado);
		//Se elimina el usuario asignado al empleado mediante el id
		usuarioRepository.deleteById(idUsuario);
		//Se retorna al template de la lista de los empleados
	    return "redirect:/admi/empleado/lista"; 
	}
	
	//Método para guardar un registro empleado
	@PostMapping(path = {"/empleado/guardar"})
	public String guardarEmpleado(@ModelAttribute("usuario") Usuario usuario,@ModelAttribute("empleado")Empleado empleado, RedirectAttributes mensaje,@RequestParam("especialidad") String especialidad,@RequestParam("rol") String rol) {
		
		//Se verifica que no exista un correo igual registrado
		if(usuarioRepository.existsByEmail(usuario.getEmail())) {
			//Si ya existe el correo se envia el siguiente mensaje
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Incorrecto");
			mensaje.addFlashAttribute("alertMessage", "Correo registrado");
			//Se redirecciona a la misma vista
			return "redirect:/admi/empleado/nuevo";
		}else if(usuarioRepository.existsByNumeroTelefono(usuario.getNumeroTelefono())) {
			//Se verifica que no exista un télefono igual registrado
			//Si ya existe el télefono se envia el siguiente mensaje
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Incorrecto");
			mensaje.addFlashAttribute("alertMessage", "Telefono registrado");
			//Se redirecciona a la misma vista
			return "redirect:/admi/empleado/nuevo";
			
		}else {
			//En el objeto usuario cambia el rol
			usuario.setRol(rol);
			//Se le asigna el usuario relacionado
			empleado.setUsuario(usuario);
			//Se le asigna la especialidad
			empleado.setEspecialidad(especialidad);
			//Se le asigna la contraseña
			usuario.setContrasena(""+usuario.getContrasena());
			//Se guarda el registro usuario
			usuarioRepository.save(usuario);
			//Se guarda el registro empleado
			empleadoRepository.save(empleado);
			
			//Se envia el mensaje de confirmación
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "success");
			mensaje.addFlashAttribute("alertTitle", "Correcto");
			mensaje.addFlashAttribute("alertMessage", "Empleado registrado");
			
			
			//Se redirecciona a la misma vista
			return "redirect:/admi/empleado/nuevo";
			
		
		}
		
		
		
		
	}
	
	  
	
	
	

}
