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
import com.barber.valencia.entity.Empleado;
import com.barber.valencia.entity.Usuario;
import com.barber.valencia.repository.ClienteRepository;
import com.barber.valencia.repository.UsuarioRepository;

@Controller
@RequestMapping("/admi")
public class ClienteController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	
	
	//Método para mostrar el formulario de registro
	@GetMapping(path = {"/cliente/nuevo"})
	public String mostrarFormularioEmpleado(Model model) {
		model.addAttribute("usuario",new Usuario());
		model.addAttribute("cliente", new Cliente());
		
		return "registro_cliente";
		
		
	}
	//Método para guardar un registro cliente
	@PostMapping(path = {"/cliente/guardar"})
	public String guardarCliente(@ModelAttribute("usuario") Usuario usuario,@ModelAttribute("cliente")Cliente cliente, RedirectAttributes mensaje) {

		//Se verifica que no exista un correo igual registrado
		if(usuarioRepository.existsByEmail(usuario.getEmail())) {
			//Si ya existe el correo se envia el siguiente mensaje
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Incorrecto");
			mensaje.addFlashAttribute("alertMessage", "Correo registrado");
			//Se redirecciona a la misma vista
			return "redirect:/admi/cliente/nuevo";
		}else if(usuarioRepository.existsByNumeroTelefono(usuario.getNumeroTelefono())) {
			//Se verifica que no exista un télefono igual registrado
			//Si ya existe el télefono se envia el siguiente mensaje
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Incorrecto");
			mensaje.addFlashAttribute("alertMessage", "Telefono registrado");
			//Se redirecciona a la misma vista
			return "redirect:/admi/cliente/nuevo";
			
		}else {
			
			
			//En el objeto usuario cambia el rol por cliente
			usuario.setRol("cliente");
			//Se le asigna el usuario relacionado
			cliente.setUsuario(usuario);
			//Se le asigna la contraseña
			usuario.setContrasena(""+usuario.getContrasena());
			
			//Se guarda el registro usuario
			usuarioRepository.save(usuario);
			//Se guarda el registro cliente
			clienteRepository.save(cliente);
			
			//Se envia el mensaje de confirmación
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "success");
			mensaje.addFlashAttribute("alertTitle", "Correcto");
			mensaje.addFlashAttribute("alertMessage", "Cliente registrado");
			
			
			//Se redirecciona a la misma vista
			return "redirect:/admi/cliente/nuevo";
			
		
		}
		
	}
	
	//Método para listar los clientes de la base de datos
	@GetMapping(path = {"/cliente/lista"})
	public String mostrarListaClientes(Model model) {
		//Se obtienen todos los clientes en una lista con el método findAll()
		List<Cliente> cliente= clienteRepository.findAll();
		//Se le asigna la lista al modelo
		model.addAttribute("clientes", cliente);
		//Se retorna un template
		return "admi_lista_clientes";
	}
	//Método para eliminar un registro cliente
	@PostMapping(path = {"/cliente/eliminar"})
	public String eliminarCliente(@RequestParam("idCliente") Integer idCliente, @RequestParam("idUsuario") Integer idUsuario,RedirectAttributes mensaje) {
	    //Se envía un mensaje de confirmación
		mensaje.addFlashAttribute("alerta", "¡Alerta!");
		mensaje.addFlashAttribute("alertIcon", "success");
		mensaje.addFlashAttribute("alertTitle", "Correcto");
		mensaje.addFlashAttribute("alertMessage", "Cliente eliminado");
		//Se elimina el cliente mediante el id
		clienteRepository.deleteById(idCliente);
		//Se elimina el usuario asignado al cliente mediante el id
		usuarioRepository.deleteById(idUsuario);
		
		//Se retorna al template de la lista de los cliente
	    return "redirect:/admi/cliente/lista"; 
	}
	//Método para mostrar el template(formulario para actualizar)
	@GetMapping(path = {"/cliente/verActualizar/{id}"})
	public String mostrarClienteActualizar(@PathVariable("id") Integer id,Model model) {
		//Se recibe el id del cliente para buscarlo por PathVariable
		//Se guarda el cliente buscado
		Optional<Cliente> cliente = clienteRepository.findById(id);
		
		Cliente clientes = cliente.orElse(null);
		//Se le asigna el cliente al modelo
		model.addAttribute("cliente", clientes);
		//Se retorna el template
		return "admi_actualizar_cliente";
		
	}
	
	//Método para actualizar un registro cliente
	@PostMapping(path = {"/cliente/actualizar"})
	public String actualizarEmpleado(@RequestParam("correoAdmi")String correoAdmi,  @RequestParam("contrasenaAdmi") String contrasenaAdmi,
			@RequestParam("rol") String rol,@RequestParam("contrasena") String contrasena,RedirectAttributes mensaje,@ModelAttribute("cliente")Cliente clienteE) {
		//Se busca un usuario mediante el email y la contraseña son credenciales del administrador
		Usuario user = usuarioRepository.findUsuarioByEmailAndContrasena(correoAdmi, contrasenaAdmi);
		//Si el usuario es igual a null entonces las credenciales son incorrectas
	    if (user == null) {
	    	//Se envía el mensaje para las credenciales incorrectas
	    	mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Error");
			mensaje.addFlashAttribute("alertMessage", "Contraseña o email incorrectos");
			//Se redirecciona a la lista de los clientes
			return "redirect:/admi/cliente/lista"; 
	    } else {
	    	
	    	//Se busca el usuario que se quiere actualizar mediante el Email
	    	Usuario usert=usuarioRepository.findUsuarioByEmail(clienteE.getUsuario().getEmail());
	    	//Despues buscamos el cliente que le corresponde a ese usuario
	    	Cliente client=clienteRepository.findClienteByUsuario(usert);
	    	//Si la contraseña del cliente es un campo vacío quiere decir que no se quiere cambiar
	    	if(contrasena == "") {
	    		//Se modifica los atributos correspondientes rol y teléfono
		    	client.getUsuario().setRol(rol);
		    	client.getUsuario().setNumeroTelefono(clienteE.getUsuario().getNumeroTelefono());
		    	
		    	//Se envia un mensaje de confirmación
		    	mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success");
				mensaje.addFlashAttribute("alertTitle", "¡Exito!");
				mensaje.addFlashAttribute("alertMessage", "Registro actualizado");
				//Se guarda el cliente con los cambios en la base de datos
				clienteRepository.save(client);
				// Redirección a la lista de los clientes
				return "redirect:/admi/cliente/lista"; 
				
	    		
	    	}else {
	    		//Se modifica los atributos correspondientes rol, teléfono y contraseña
		    	client.getUsuario().setRol(rol);
		    	client.getUsuario().setContrasena(contrasena);
		    	client.getUsuario().setNumeroTelefono(clienteE.getUsuario().getNumeroTelefono());
		    	//Se guardan los cambios del registro en la base de datos
		    	clienteRepository.save(client);
		    	
		    	//Mensaje de confirmación
		    	mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success");
				mensaje.addFlashAttribute("alertTitle", "¡Exito!");
				mensaje.addFlashAttribute("alertMessage", "Registro actualizado");
				// Redirección a la lista de los clientes
				return "redirect:/admi/cliente/lista"; 
	    		
	    	}
	    	
	    	
	    	
	    	
	    	
			
	    }
	    
		
	    
	}
	
	
	

}
