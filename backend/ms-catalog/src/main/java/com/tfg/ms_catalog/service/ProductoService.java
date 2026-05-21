package com.tfg.ms_catalog.service;

import com.tfg.ms_catalog.dto.ProductoDTO;
import com.tfg.ms_catalog.model.Producto;
import com.tfg.ms_catalog.repository.ProductoRepository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private void auditar(String accion, Long entidadId, String usuarioEmail, String detalles) {
        com.tfg.ms_catalog.dto.AuditoriaEventoDTO evento = new com.tfg.ms_catalog.dto.AuditoriaEventoDTO(
            "CATALOG", accion, entidadId, usuarioEmail, java.time.LocalDateTime.now(), detalles
        );
        rabbitTemplate.convertAndSend(
            com.tfg.ms_catalog.config.RabbitMQConfig.EXCHANGE_AUDITORIA, 
            com.tfg.ms_catalog.config.RabbitMQConfig.ROUTING_KEY_AUDITORIA, 
            evento
        );
    }

    public ProductoDTO crearProducto(ProductoDTO productoDTO, String usuarioEmail) {
        Producto producto = new Producto();
        producto.setNombre(productoDTO.getNombre());
        producto.setPrecioNeto(productoDTO.getPrecioNeto());
        producto.setCategoria(productoDTO.getCategoria());
        producto.setActivo(true);

        String skuGenerado = "PROD-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        producto.setSku(skuGenerado);

        Producto productoGuardado = productoRepository.save(producto);
        auditar("POST", productoGuardado.getId(), usuarioEmail, "CreaciÃƒÂ³n de producto: " + producto.getNombre());
        return turnToDto(productoGuardado);
    }

    public List<ProductoDTO> obtenerTodosLosProductos(String usuarioEmail) {
        auditar("GET_ALL", null, usuarioEmail, "Consulta de todos los productos activos");
        List<Producto> productos = productoRepository.findByActivoTrue();

        return productos.stream()
                .map(this::turnToDto)
                .collect(Collectors.toList());
    }

    public ProductoDTO actualizarProducto(Long id, ProductoDTO dto, String usuarioEmail) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (dto.getNombre() != null) {
            producto.setNombre(dto.getNombre());
        }

        if (dto.getPrecioNeto() != null) {
            producto.setPrecioNeto(dto.getPrecioNeto());
        }

        if (dto.getCategoria() != null) {
            producto.setCategoria(dto.getCategoria());
        }

        Producto productoActualizado = productoRepository.save(producto);
        auditar("PUT", id, usuarioEmail, "ActualizaciÃƒÂ³n de producto");
        return turnToDto(productoActualizado);
    }

    public void eliminarProductoLogicamente(Long id, String usuarioEmail) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setActivo(false);
        productoRepository.save(producto);
        auditar("DELETE_LOGICO", id, usuarioEmail, "Borrado lÃƒÂ³gico de producto");
    }

    public void eliminarProducto(Long id, String usuarioEmail) {
        productoRepository.deleteById(id);
        auditar("DELETE_FISICO", id, usuarioEmail, "Borrado fÃƒÂ­sico de producto");
    }

    public ProductoDTO getProductoById(Long id, String usuarioEmail) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        auditar("GET", id, usuarioEmail, "Consulta de producto por ID");
        return turnToDto(producto);
    }

    public List<ProductoDTO> obtenerProductosInactivos(String usuarioEmail) {
        auditar("GET_ALL_INACTIVOS", null, usuarioEmail, "Consulta de productos inactivos");
        List<Producto> productos = productoRepository.findByActivoFalse();

        return productos.stream()
                .map(this::turnToDto)
                .collect(Collectors.toList());
    }

    public List<ProductoDTO> obtenerProductosPorNombre(String nombre, String usuarioEmail) {
        auditar("GET_SEARCH", null, usuarioEmail, "BÃƒÂºsqueda de productos activos por nombre: " + nombre);
        List<Producto> productos = productoRepository.findByNombreContaining(nombre);

        return productos.stream()
                .map(this::turnToDto)
                .collect(Collectors.toList());
    }

    public List<ProductoDTO> obtenerProductosInactivosPorNombre(String nombre, String usuarioEmail) {
        auditar("GET_SEARCH_INACTIVOS", null, usuarioEmail, "BÃƒÂºsqueda de productos inactivos por nombre: " + nombre);
        List<Producto> productos = productoRepository.findByNombreContainingAndActivoFalse(nombre);

        return productos.stream()
                .map(this::turnToDto)
                .collect(Collectors.toList());
    }

    public ProductoDTO getProductoBySku(String sku, String usuarioEmail) {
        Producto producto = productoRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        auditar("GET_SKU", producto.getId(), usuarioEmail, "Consulta de producto por SKU: " + sku);
        return turnToDto(producto);
    }

    private ProductoDTO turnToDto(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setSku(producto.getSku());
        dto.setPrecioNeto(producto.getPrecioNeto());
        dto.setCategoria(producto.getCategoria());
        dto.setActivo(producto.getActivo() != null ? producto.getActivo() : true);
        return dto;
    }

}
