package com.barber.valencia.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.barber.valencia.entity.Compra;
import com.barber.valencia.entity.Detalle_compra;
import com.barber.valencia.entity.Detalle_venta_servicio;
import com.barber.valencia.entity.Producto;
import com.barber.valencia.entity.Proveedor;
import com.barber.valencia.entity.Servicio;
import com.barber.valencia.entity.Ventas_servicio;
import com.barber.valencia.repository.ClienteRepository;
import com.barber.valencia.repository.Detalle_venta_repository;
import com.barber.valencia.repository.ServicioRepository;
import com.barber.valencia.repository.Ventas_servicio_repository;
@Controller
@RequestMapping("/admi")
public class VServicioController {
	//Mediante esta variable se obtiene la fecha actual
	private LocalDate fechaActual = LocalDate.now();
	Date fechaActualDate = Date.from(fechaActual.atStartOfDay(ZoneId.systemDefault()).toInstant());
		
	 @Autowired
	 Detalle_venta_repository dtventaRepository;
	 
	 @Autowired
	 Ventas_servicio_repository ventaRepository;
	 
	 @Autowired
	 ServicioRepository servicioRepository;
	 
	 @Autowired
	 ClienteRepository clienteRepository;
	 
	
	 
	//Método para mostrar el formulario de registro de venta de un producto
	@GetMapping(path = {"/ventaServicio/nuevo"})
	public String mostrarFormulario(Model model) {
		List<Servicio> servicios= servicioRepository.findAll();
		//Se obtienen todos los clientes en una lista con el método findAll()
		List<Cliente> cliente= clienteRepository.findAll();
		//Se le asigna la lista al modelo
		model.addAttribute("clientes", cliente);
		//Se envia la lista de proveedor y productos al modelo de la vista
		model.addAttribute("listaServicios",servicios);
		
		return "admi_registro_venta_servicio";
		
		
	}
	
	//Método para obtener el precio del producto
	@GetMapping(path = {"/obtenerPrecio/{nombre}"})
    public ResponseEntity<Double> obtenerPrecio(@PathVariable("nombre") Integer id) {
        Optional<Servicio> servicio = servicioRepository.findById(id);
        Servicio servi=servicio.orElse(null);
        System.out.println(servi.getPrecio());
        Double precio=servi.getPrecio();
        return ResponseEntity.ok(precio);
    }
	
	//Método para obtener el precio del producto
		@GetMapping(path = {"/descuentoServicio/{nombre}"})
	    public ResponseEntity<Integer> obtenerDescuento(@PathVariable("nombre") Integer id) {
	        Optional<Servicio> servicio = servicioRepository.findById(id);
	        Servicio servi=servicio.orElse(null);
	        
	        Integer descuento= servi.getDescuento();
	        return ResponseEntity.ok(descuento);
	    }
		
	
	//Método para guardar un registro venta de servicio
		@PostMapping(path = {"/ventaServicio/guardar"})
		public String guardarServicio(@RequestParam("servi") Integer idServicio,@RequestParam("cliente") Integer idCliente, RedirectAttributes mensaje) {
			
			    //Se busca el id del producto y del proveedor para guardarlos en el registro compra mediante findById
				Optional <Servicio> servicios= servicioRepository.findById(idServicio);
				Servicio servicio=servicios.orElse(null);
				
				//Buscamos el cliente que le corresponde la venta
				Optional <Cliente> clientes= clienteRepository.findById(idCliente);
				Cliente cliente=clientes.orElse(null);
				
				Ventas_servicio vservicio=new Ventas_servicio();
				Detalle_venta_servicio dvServicio= new Detalle_venta_servicio();
				//Comprobamos que exista el cliente
				if(idCliente==null) {
					//Si el cliente no existe entonces no lo agregamos a nuestro entity
					vservicio.setFecha_venta(fechaActual.toString());
					//Se guarda en la base de datos
					ventaRepository.save(vservicio);
					
					dvServicio.setVenta_servicio(vservicio);
					dvServicio.setServicio(servicio);
					dvServicio.setPrecio_total(servicio.getPrecio());
					//Se guarda en la base de datos
					dtventaRepository.save(dvServicio);
					
				}else {
					//Se agrega el cliente
					vservicio.setCliente(cliente);
					vservicio.setFecha_venta(fechaActual.toString());
					//Se guarda en la base de datos
					ventaRepository.save(vservicio);
					
					dvServicio.setVenta_servicio(vservicio);
					dvServicio.setServicio(servicio);
					dvServicio.setPrecio_total(servicio.getPrecio());
					//Se guarda en la base de datos
					dtventaRepository.save(dvServicio);
					
				}
				
			
				
				
				//Se envía un mensaje de confirmación
				mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success");
				mensaje.addFlashAttribute("alertTitle", "Correcto");
				mensaje.addFlashAttribute("alertMessage", "Venta registrada");
				//Se redirecciona a la misma vista
				return "redirect:/admi/ventaServicio/nuevo";
				
		}
		
		//Método para listar las ventas de servicio de la base de datos
		@GetMapping(path = {"/ventaServicio/lista"})
		public String mostrarListaVenta(Model model) {
			//Se obtienen todas los registros en una lista con el método findAll()
			List<Detalle_venta_servicio> dtServicio= dtventaRepository.findAll();
			//Se le asigna la lista al modelo
	        model.addAttribute("lista", dtServicio);
	      //Se retorna un template
			return "admi_lista_venta_servicio";
			
		}
		
		
		//Método para eliminar un registro venta de servicio
		@PostMapping(path = {"/ventaServicio/eliminar"})
		public String eliminarCompra(@RequestParam("id") Integer id,RedirectAttributes mensaje) {
			//Esta variable es la búsqueda del registro que se quiere eliminar
			
			Optional<Detalle_venta_servicio> dtServicio= dtventaRepository.findById(id);
			Detalle_venta_servicio vServicio= dtServicio.orElse(null);
			
			vServicio.setServicio(null);
			vServicio.getVenta_servicio().setCliente(null);
			
			dtventaRepository.delete(vServicio);
			
			
			//Se envía un mensaje de confirmación
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "success");
			mensaje.addFlashAttribute("alertTitle", "Correcto");
			mensaje.addFlashAttribute("alertMessage", "Venta eliminada");
			
		    return "redirect:/admi/ventaServicio/lista"; 
		}
		
		
}
