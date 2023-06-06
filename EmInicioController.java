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

import com.barber.valencia.entity.Compra;
import com.barber.valencia.entity.Detalle_compra;
import com.barber.valencia.entity.Producto;
import com.barber.valencia.entity.Proveedor;
import com.barber.valencia.repository.CitaRepository;
import com.barber.valencia.repository.ClienteRepository;
import com.barber.valencia.repository.CompraRepository;
import com.barber.valencia.repository.Detalle_compra_repository;
import com.barber.valencia.repository.Detalle_venta_repository;
import com.barber.valencia.repository.EmpleadoRepository;
import com.barber.valencia.repository.ProductoRepository;
import com.barber.valencia.repository.ProveedoRepository;
import com.barber.valencia.repository.ServicioRepository;
import com.barber.valencia.repository.UsuarioRepository;
import com.barber.valencia.repository.Ventas_servicio_repository;

@Controller
@RequestMapping("/em")
public class EmInicioController {
	// Mediante esta variable se obtiene la fecha actual
	private LocalDate fechaActual = LocalDate.now();
	Date fechaActualDate = Date.from(fechaActual.atStartOfDay(ZoneId.systemDefault()).toInstant());

	@Autowired
	private Detalle_venta_repository dtventaRepository;

	@Autowired
	private Ventas_servicio_repository ventaRepository;
	@Autowired
	private EmpleadoRepository empleadoRepository;

	@Autowired
	private ServicioRepository servicioRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private CitaRepository citaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	CompraRepository compraRepository;
	
	@Autowired
	Detalle_compra_repository dtCompraRepository;
	
	@Autowired
	ProductoRepository productoRepository;
	
	@Autowired
	ProveedoRepository proveedorRepository;
	
	@GetMapping(path = { "/inicio/{id}" })
	public String mostrarInicio(@PathVariable("id") Integer idUsua) {
		
		return "inicio_em";
	}

	@GetMapping(path = { "/inicio" })
	public String mostrarInicioEm() {

		return "inicio_em";
	}
	
	//Compra
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
			return "em_registro_compra";
			
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
				return "redirect:/em/compra/nuevo";
				
		}
		
		//Método para listar las compras de la base de datos
		@GetMapping(path = {"/compra/lista"})
		public String mostrarListaCompra(Model model) {
			//Se obtienen todas los clientes en una lista con el método findAll()
			List<Detalle_compra> dtcompra = dtCompraRepository.findAll();
			//Se le asigna la lista al modelo
	        model.addAttribute("dtcompra", dtcompra);
	      //Se retorna un template
			return "em_lista_compras";
			
		}
		
		
		//Cita
		
		

}
