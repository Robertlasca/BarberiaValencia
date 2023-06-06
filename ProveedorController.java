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
import com.barber.valencia.entity.Usuario;
import com.barber.valencia.repository.ProveedoRepository;
import com.barber.valencia.repository.UsuarioRepository;

@Controller
@RequestMapping("/admi")
public class ProveedorController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ProveedoRepository proveedorRepository;
	
	//Método para mostrar el formulario de registro
	@GetMapping(path = {"/proveedor/nuevo"})
	public String mostrarFormularioProveedor(Model model) {
		model.addAttribute("proveedor", new Proveedor());
		
		return "admi_registro_proveedor";
	}
	//Método para listar los proveedores de la base de datos
	@GetMapping(path = {"/proveedor/lista"})
	public String mostrarListaProveedor(Model model) {
		//Se obtienen todos los proveedores en una lista con el método findAll()
		List<Proveedor> proveedor = proveedorRepository.findAll();
		//Se le asigna la lista al modelo
        model.addAttribute("proveedores", proveedor);
     
        //Se retorna un template
		return "admi_lista_proveedor";
		
	}
	//Método para guardar un registro proveedor
	@PostMapping(path = {"/proveedor/guardar"})
	public String guardarProveedor(@ModelAttribute("proveedor")Proveedor proveedor, RedirectAttributes mensaje) {
		//Se verifica que no exista la empresa del proveedor registrado
		if(proveedorRepository.existsByEmpresa(proveedor.getEmpresa())) {
			//Si ya existe la empresa del proveedor se envia el siguiente mensaje
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Incorrecto");
			mensaje.addFlashAttribute("alertMessage", "Existe proveedor");
			//Se redirecciona a la misma vista
			return "redirect:/admi/proveedor/nuevo";
		}else {
			//Se guarda el proveedor en la base de datos
			proveedorRepository.save(proveedor);
			//Se envia el mensaje de confirmación
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "success");
			mensaje.addFlashAttribute("alertTitle", "Correcto");
			mensaje.addFlashAttribute("alertMessage", "Proveedor registrado");
			
			//Se redirecciona a la misma vista
			return "redirect:/admi/proveedor/nuevo";
			
		
		}
	}
	
	//Método para eliminar un registro proveedor
	@PostMapping(path = {"/proveedor/eliminar"})
	public String eliminarProveedor(@RequestParam("idProveedor") Integer idProveedor,RedirectAttributes mensaje) {
		//Se envia el mensaje de confirmación
		mensaje.addFlashAttribute("alerta", "¡Alerta!");
		mensaje.addFlashAttribute("alertIcon", "success");
		mensaje.addFlashAttribute("alertTitle", "Correcto");
		mensaje.addFlashAttribute("alertMessage", "Proveedor eliminado");
		//Se elimina el proveedor  mediante el id
		proveedorRepository.deleteById(idProveedor);
		//Se retorna al template de la lista de los proveedores
	    return "redirect:/admi/proveedor/lista"; 
	}
	
	//Método para mostrar el template(formulario para actualizar)
	@GetMapping(path = {"/proveedor/verActualizar/{id}"})
	public String mostrarClienteActualizar(@PathVariable("id") Integer id,Model model) {
		//Se recibe el id del proveedor para buscarlo por PathVariable
		//Se guarda el proveedor buscado
		Optional<Proveedor> proveedor = proveedorRepository.findById(id);
		Proveedor proveedores = proveedor.orElse(null);
		//Se le asigna el producto al modelo
		model.addAttribute("proveedor", proveedores);
		//Se retorna un template
		return "admi_actualizar_proveedor";
		
	}
	//Método para actualizar un registro proveedor
	@PostMapping(path = {"/proveedor/actualizar"})
	public String actualizarProveedor(@RequestParam("correoAdmi")String correoAdmi,  @RequestParam("contrasenaAdmi") String contrasenaAdmi
			,RedirectAttributes mensaje,@ModelAttribute("proveedor")Proveedor proveedorE) {
		//Se busca un usuario mediante el email y la contraseña son credenciales del administrador
		Usuario user = usuarioRepository.findUsuarioByEmailAndContrasena(correoAdmi, contrasenaAdmi);
		//Si el usuario es igual a null entonces las credenciales son incorrectas
	    if (user == null) {
	    	//Se envía el mensaje para las credenciales incorrectas
	    	mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Error");
			mensaje.addFlashAttribute("alertMessage", "Contraseña o email incorrectos");
			//Se redirecciona a la lista de los proveedores
			return "redirect:/admi/proveedor/lista"; 
	    } else {
	    		//Se busca el proveedor que se quiere actualizar mediante la Empresa
				Proveedor proveedo= proveedorRepository.findProveedorByEmpresa(proveedorE.getEmpresa());
				// Se modifica el teléfono del proveedor
		    	proveedo.setTelefono(proveedorE.getTelefono());
		    	//Se guardan los cambios en la base de datos
		    	proveedorRepository.save(proveedo);
		    	//Se envia el mensaje de confirmación
		    	mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success");
				mensaje.addFlashAttribute("alertTitle", "¡Exito!");
				mensaje.addFlashAttribute("alertMessage", "Registro actualizado");
				//Se redirecciona a la lista de los proveedores
				return "redirect:/admi/proveedor/lista"; 
	    		
	    	
	    	
	    }
	}
		
	    
	}
	
	

