package com.barber.valencia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.barber.valencia.entity.Compra;
import com.barber.valencia.entity.Detalle_compra;
import com.barber.valencia.entity.Empleado;
import com.barber.valencia.entity.Producto;
import com.barber.valencia.entity.Proveedor;
import com.barber.valencia.entity.Servicio;
import com.barber.valencia.repository.CompraRepository;
import com.barber.valencia.repository.Detalle_compra_repository;
import com.barber.valencia.repository.ProductoRepository;
import com.barber.valencia.repository.ProveedoRepository;
import com.barber.valencia.repository.UsuarioRepository;

@Controller
@RequestMapping("/admi")
public class CompraController {
	//Mediante esta variable se obtiene la fecha actual
	private LocalDate fechaActual = LocalDate.now();
	Date fechaActualDate = Date.from(fechaActual.atStartOfDay(ZoneId.systemDefault()).toInstant());
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	CompraRepository compraRepository;
	
	@Autowired
	Detalle_compra_repository dtCompraRepository;
	
	@Autowired
	ProductoRepository productoRepository;
	
	@Autowired
	ProveedoRepository proveedorRepository;
	
	//Método para mostrar el formulario de registro de compra
	@GetMapping(path = {"/compra/nuevo"})
	public String mostrarFormularioCompra(Model model) {
		List<Producto> productos= productoRepository.findAll();
		List<Proveedor> proveedores= proveedorRepository.findAll();
		//Se envia la lista de proveedor y productos al modelo de la vista
		model.addAttribute("listaProductos",productos);
		model.addAttribute("listaProveedores",proveedores);
		model.addAttribute("producto",new Producto());
		model.addAttribute("compra",new Compra());
		model.addAttribute("dtCompra",new Detalle_compra());
		
		//Se retorna el template
		return "admi_registro_compra";
		
	}
	
	
	//Método para guardar un registro compra
	@PostMapping(path = {"/compra/guardar"})
	public String guardarServicio(@ModelAttribute("compra")Compra compra,@ModelAttribute("dtCompra")Detalle_compra dtcompra,@RequestParam("produS") Integer produS,@RequestParam("proveS") Integer proveS, RedirectAttributes mensaje) {
		
		    //Se busca el id del producto y del proveedor para guardarlos en el registro compra mediante findById
			Optional <Proveedor> proveedor= proveedorRepository.findById(proveS);
			Optional <Producto> producto= productoRepository.findById(produS);
			Proveedor provee = proveedor.orElse(null);
			Producto produ= producto.orElse(null);
			
			//Se le asigna la fecha actual al registro
			compra.setFecha_compra(fechaActual.toString());
			//Se le asigna el proveedor
			compra.setProveedor(provee);
			
			//Se registra la compra en la base de datos
			compraRepository.save(compra);
			
			//Se le asigna el producto al registro detalle compra
			dtcompra.setProducto(produ);
			//Se le asigna la compra al objeto detalle compra
			dtcompra.setCompra(compra);
			//Se le asigna la fecha actual al registro detalle compra
			dtcompra.setFecha_compra(fechaActual.toString());
			//Se guarda en la base de datos el registro detalle compra
			dtCompraRepository.save(dtcompra);
			
			//Se actualiza la existencia del producto(se suma la cantidad actual con la que se ingreso)
			produ.setExistencia(produ.getExistencia()+dtcompra.getCantidad());
			//Se guardan los cambios al producto
			productoRepository.save(produ);
			
			//Se envía un mensaje de confirmación
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "success");
			mensaje.addFlashAttribute("alertTitle", "Correcto");
			mensaje.addFlashAttribute("alertMessage", "Compra registrada");
			//Se redirecciona a la misma vista
			return "redirect:/admi/compra/nuevo";
			
	}
	
	//Método para listar las compras de la base de datos
	@GetMapping(path = {"/compra/lista"})
	public String mostrarListaCompra(Model model) {
		//Se obtienen todas los clientes en una lista con el método findAll()
		List<Detalle_compra> dtcompra = dtCompraRepository.findAll();
		//Se le asigna la lista al modelo
        model.addAttribute("dtcompra", dtcompra);
      //Se retorna un template
		return "admi_lista_compras";
		
	}
	
	//Método para eliminar un registro compra
	@PostMapping(path = {"/compra/eliminar"})
	public String eliminarCompra(@RequestParam("id") Integer id,RedirectAttributes mensaje) {
		//Esta variable es la búsqueda del registro que se quiere eliminar
		Optional<Detalle_compra> dtcompra = dtCompraRepository.findById(id);
		Detalle_compra dtcompras=dtcompra.orElse(null);
		//Esta variable es la búsqueda del producto el cual se va actualizar su existencia
		Optional <Producto> producto= productoRepository.findById(dtcompras.getProducto().getId_producto());
		Producto produ= producto.orElse(null);
		//Se actualiza la existencia del producto
		produ.setExistencia(produ.getExistencia()-dtcompras.getCantidad());
		//Se guardan los cambios
		productoRepository.save(produ);
		
		//Se cambia el producto y el proveedor para que no elimine en cascada
		dtcompras.setProducto(null);
		dtcompras.getCompra().setProveedor(null);
		//Se envía un mensaje de confirmación
		mensaje.addFlashAttribute("alerta", "¡Alerta!");
		mensaje.addFlashAttribute("alertIcon", "success");
		mensaje.addFlashAttribute("alertTitle", "Correcto");
		mensaje.addFlashAttribute("alertMessage", "Compra eliminada");
		
		//Se elimina el registro
		dtCompraRepository.delete(dtcompras);
		//Se redirecciona a la lista de compras
	    return "redirect:/admi/compra/lista"; 
	}
	
	
	
	
}
