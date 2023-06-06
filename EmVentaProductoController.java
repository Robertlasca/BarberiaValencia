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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.barber.valencia.entity.Cliente;
import com.barber.valencia.entity.Detalle_venta_producto;
import com.barber.valencia.entity.Producto;
import com.barber.valencia.entity.Venta_producto;
import com.barber.valencia.repository.ClienteRepository;
import com.barber.valencia.repository.Detalle_venta_producto_repository;
import com.barber.valencia.repository.ProductoRepository;
import com.barber.valencia.repository.Venta_producto_repository;

@Controller
@RequestMapping("/em")
public class EmVentaProductoController {

	// Mediante esta variable se obtiene la fecha actual
	private LocalDate fechaActual = LocalDate.now();
	Date fechaActualDate = Date.from(fechaActual.atStartOfDay(ZoneId.systemDefault()).toInstant());

	@Autowired
	Venta_producto_repository ventaRepository;

	@Autowired
	Detalle_venta_producto_repository dtventaRepository;

	@Autowired
	ProductoRepository productoRepository;

	@Autowired
	ClienteRepository clienteRepository;

	Producto respaldo;

	@GetMapping(path = { "/ventaProducto/nuevo" })
	public String mostrarFormulario(Model model) {
		List<Producto> productos = productoRepository.findAll();
		// Se envia la lista de proveedor y productos al modelo de la vista
		// Se obtienen todos los clientes en una lista con el método findAll()
		List<Cliente> cliente = clienteRepository.findAll();
		// Se le asigna la lista al modelo
		model.addAttribute("clientes", cliente);
		model.addAttribute("listaProductos", productos);

		return "em_registro_venta_producto";

	}

	// Método para obtener el precio del producto
	@GetMapping(path = { "/obtenerPrecioProducto/{nombre}" })
	public ResponseEntity<Double> obtenerPrecioProducto(@PathVariable("nombre") Integer id) {
		Optional<Producto> producto = productoRepository.findById(id);
		Producto product = producto.orElse(null);
		System.out.println(product.getPrecio());
		Double precio = product.getPrecio();
		return ResponseEntity.ok(precio);
	}

	// Método para guardar un registro venta de producto
	@PostMapping(path = { "/ventaProducto/guardar" })
	public String guardarVenntaProducto(@RequestParam("producto") Integer idProducto,
			@RequestParam("cliente") Integer idCliente, @RequestParam("cantidad") Integer cantidad,
			@RequestParam("precioTotal") Double precioTotal, RedirectAttributes mensaje) {

		// Se busca el id del producto y del proveedor para guardarlos en el registro
		// compra mediante findById
		Optional<Producto> productos = productoRepository.findById(idProducto);
		Producto producto = productos.orElse(null);

		// Buscamos el cliente que le corresponde la venta
		Optional<Cliente> clientes = clienteRepository.findById(idCliente);
		Cliente cliente = clientes.orElse(null);

		Venta_producto vproducto = new Venta_producto();
		Detalle_venta_producto dvProducto = new Detalle_venta_producto();
		// Comprobamos que exista el cliente
		if (producto.getExistencia() > cantidad) {

			producto.setExistencia(producto.getExistencia() - cantidad);
			productoRepository.save(producto);

			if (idCliente == null) {
				// Si el cliente no existe entonces no lo agregamos a nuestro entity
				vproducto.setFecha_venta(fechaActual.toString());

				// Se guarda en la base de datos
				ventaRepository.save(vproducto);

				dvProducto.setCamtidad(cantidad);
				dvProducto.setPrecio_total(precioTotal);
				dvProducto.setProducto(producto);
				dvProducto.setVenta_producto(vproducto);

				// Se guarda en la base de datos
				dtventaRepository.save(dvProducto);
				// Se envía un mensaje de confirmación
				mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success");
				mensaje.addFlashAttribute("alertTitle", "Correcto");
				mensaje.addFlashAttribute("alertMessage", "Venta registrada");
				// Se redirecciona a la misma vista
				return "redirect:/em/ventaProducto/nuevo";
			} else {
				// Se agrega el cliente
				vproducto.setCliente(cliente);
				// Si el cliente no existe entonces no lo agregamos a nuestro entity
				vproducto.setFecha_venta(fechaActual.toString());

				// Se guarda en la base de datos
				ventaRepository.save(vproducto);

				dvProducto.setCamtidad(cantidad);
				dvProducto.setPrecio_total(precioTotal);
				dvProducto.setProducto(producto);
				dvProducto.setVenta_producto(vproducto);

				// Se guarda en la base de datos
				dtventaRepository.save(dvProducto);
				// Se envía un mensaje de confirmación
				mensaje.addFlashAttribute("alerta", "¡Alerta!");
				mensaje.addFlashAttribute("alertIcon", "success");
				mensaje.addFlashAttribute("alertTitle", "Correcto");
				mensaje.addFlashAttribute("alertMessage", "Venta registrada");
				// Se redirecciona a la misma vista
				return "redirect:/em/ventaProducto/nuevo";

			}

		} else {
			// Se envía un mensaje de confirmación
			mensaje.addFlashAttribute("alerta", "¡Alerta!");
			mensaje.addFlashAttribute("alertIcon", "error");
			mensaje.addFlashAttribute("alertTitle", "Cantidad");
			mensaje.addFlashAttribute("alertMessage", "Insuficiente");
			// Se redirecciona a la misma vista
			return "redirect:/em/ventaProducto/nuevo";
		}

	}

	// Método para eliminar un registro venta de proucto
	@PostMapping(path = { "/ventaProducto/eliminar" })
	public String eliminarVenta(@RequestParam("id") Integer id, RedirectAttributes mensaje) {
		// Esta variable es la búsqueda del registro que se quiere eliminar
		Optional<Detalle_venta_producto> dtProducto = dtventaRepository.findById(id);
		Detalle_venta_producto vProducto = dtProducto.orElse(null);

		// Se busca el id del producto para guardarlos en el registro venta mediante
		// findById
		Optional<Producto> productos = productoRepository.findById(vProducto.getProducto().getId_producto());
		Producto producto = productos.orElse(null);

		producto.setExistencia(producto.getExistencia() + vProducto.getCamtidad());

		productoRepository.save(producto);
		vProducto.setProducto(null);
		vProducto.getVenta_producto().setCliente(null);

		dtventaRepository.delete(vProducto);

		// Se envía un mensaje de confirmación
		mensaje.addFlashAttribute("alerta", "¡Alerta!");
		mensaje.addFlashAttribute("alertIcon", "success");
		mensaje.addFlashAttribute("alertTitle", "Correcto");
		mensaje.addFlashAttribute("alertMessage", "Venta eliminada");

		return "redirect:/em/ventaProducto/lista";
	}

	// Método para listar los registros de la base de datos
	@GetMapping(path = { "/ventaProducto/lista" })
	public String mostrarListaVenta(Model model) {
		// Se obtienen todas las ventas en una lista con el método findAll()
		List<Detalle_venta_producto> dtProducto = dtventaRepository.findAll();
		// Se le asigna la lista al modelo
		model.addAttribute("lista", dtProducto);
		// Se retorna un template
		return "em_lista_venta_producto";

	}
}
