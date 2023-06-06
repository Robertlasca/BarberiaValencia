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

import com.barber.valencia.entity.Producto;
import com.barber.valencia.entity.Proveedor;
import com.barber.valencia.entity.Servicio;
import com.barber.valencia.entity.Usuario;
import com.barber.valencia.repository.ProductoRepository;
import com.barber.valencia.repository.UsuarioRepository;

@Controller
@RequestMapping("/admi")
public class ProductoController {
	
	@Autowired 
	private ProductoRepository productoRepository;
	
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	private Integer id;
	
	@GetMapping(path = {"/inicio/{id}"})
	public String mostrarInicio(@PathVariable("id") Integer idUsua) {
		id=idUsua;
		System.out.println("Este es el id del usuario"+id);
		return "admi_inicio_admi";
	}
	
	//Método para mostrar el formulario de registro
	@GetMapping(path = {"/producto/nuevo"})
	public String mostrarFormularioProducto(Model model) {
		model.addAttribute("producto",new Producto());

		return "admi_registro_producto";
	}
	//Método para guardar un registro producto
	@PostMapping(path = {"/producto/guardar"})
	public String guardarProducto(@ModelAttribute("producto")Producto producto, RedirectAttributes mensaje) {
		//Se verifica que no exista el nombre del producto registrado
		if(productoRepository.existsByNombre(producto.getNombre())) {
			//Si ya existe el nombre del producto se envia el siguiente mensaje
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Incorrecto");
			mensaje.addFlashAttribute("alertMessage", "Existe producto");
			//Se redirecciona a la misma vista
			return "redirect:/admi/producto/nuevo";
		}else {
			//Se guarda el producto en la base de datos
			productoRepository.save(producto);
			//Se envia el mensaje de confirmación
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "success");
			mensaje.addFlashAttribute("alertTitle", "Correcto");
			mensaje.addFlashAttribute("alertMessage", "Producto registrado");
			
			
			//Se redirecciona a la misma vista
			return "redirect:/admi/producto/nuevo";
			
		
		}
	}
	
	//Método para eliminar un registro producto
	@PostMapping(path = {"/producto/eliminar"})
	public String eliminarProducto(@RequestParam("idProducto") Integer idProducto,RedirectAttributes mensaje) {
		//Se envia el mensaje de confirmación
		mensaje.addFlashAttribute("alerta", "¡Alerta!");
		mensaje.addFlashAttribute("alertIcon", "success");
		mensaje.addFlashAttribute("alertTitle", "Correcto");
		mensaje.addFlashAttribute("alertMessage", "Producto eliminado");
		//Se elimina el producto  mediante el id
		productoRepository.deleteById(idProducto);
		//Se retorna al template de la lista de los productos
	    return "redirect:/admi/producto/lista"; 
	}
	
	
	//Método para listar los productos de la base de datos
	@GetMapping(path = {"/producto/lista"})
	public String mostrarListaProducto(Model model) {
		//Se obtienen todos los productos en una lista con el método findAll()
		List<Producto> producto= productoRepository.findAll();
		//Se le asigna la lista al modelo
		model.addAttribute("productos", producto);
		//Se retorna un template
		return "admi_lista_productos";
		
	}
	
	//Método para mostrar el template(formulario para actualizar)
	@GetMapping(path = {"/producto/verActualizar/{id}"})
	public String mostrarProductoActualizar(@PathVariable("id") Integer id,Model model) {
		//Se recibe el id del producto para buscarlo por PathVariable
		//Se guarda el producto buscado
		Optional<Producto> producto= productoRepository.findById(id);
		Producto productos= producto.orElse(null);
		//Se le asigna el producto al modelo
		model.addAttribute("producto",productos);
		//Se retorna un template
		return "admi_actualizar_producto";
		
	}
	//Método para actualizar un registro producto
	@PostMapping(path = {"/producto/actualizar"})
	public String actualizarProducto(@RequestParam("correoAdmi")String correoAdmi,  @RequestParam("contrasenaAdmi") String contrasenaAdmi
			,RedirectAttributes mensaje,@ModelAttribute("producto")Producto productoE) {
		//Se busca un usuario mediante el email y la contraseña son credenciales del administrador
		Usuario user = usuarioRepository.findUsuarioByEmailAndContrasena(correoAdmi, contrasenaAdmi);
		//Si el usuario es igual a null entonces las credenciales son incorrectas
	    if (user == null) {
	    	//Se envía el mensaje para las credenciales incorrectas
	    	mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Error");
			mensaje.addFlashAttribute("alertMessage", "Contraseña o email incorrectos");
			
			//Se redirecciona a la lista de los productos
		    return "redirect:/admi/producto/lista"; 
	    } else {
	    		//Se busca el producto que se quiere actualizar mediante el Nombre
	    		Producto producto = productoRepository.findProductoByNombre(productoE.getNombre());
	    		//Se modifica la descripción, existencia y precio
	    		producto.setDescripcion(productoE.getDescripcion());
	    		producto.setExistencia(productoE.getExistencia());
	    		producto.setPrecio(productoE.getPrecio());
	    		producto.setDescuento(productoE.getDescuento());
	    		//Se guardan los cambios en la base de datos
	    		productoRepository.save(producto);
	    		//Se envia el mensaje de confirmación
		    	mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success");
				mensaje.addFlashAttribute("alertTitle", "¡Exito!");
				mensaje.addFlashAttribute("alertMessage", "Producto actualizado");
				return "redirect:/admi/producto/lista"; 
	    		
	     }
	}
	
	

}
