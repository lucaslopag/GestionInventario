package com.tfg.ms_catalog.Service;

import com.tfg.ms_catalog.DTO.ProductoDTO;
import com.tfg.ms_catalog.Model.Producto;
import com.tfg.ms_catalog.Repository.ProductoRepository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public ProductoDTO crearProducto(ProductoDTO productoDTO) {
        Producto producto = new Producto();
        producto.setNombre(productoDTO.getNombre());
        producto.setPrecioNeto(productoDTO.getPrecioNeto());
        producto.setActivo(true);

        String skuGenerado = "PROD-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        producto.setSku(skuGenerado);

        Producto productoGuardado = productoRepository.save(producto);

        return turnToDto(productoGuardado);
    }

    public List<ProductoDTO> obtenerTodosLosProductos() {
        List<Producto> productos = productoRepository.findByActivoTrue();

        return productos.stream()
                .map(this::turnToDto)
                .collect(Collectors.toList());
    }

    public ProductoDTO actualizarProducto(Long id, ProductoDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (dto.getNombre() != null) {
            producto.setNombre(dto.getNombre());
        }

        if (dto.getPrecioNeto() != null) {
            producto.setPrecioNeto(dto.getPrecioNeto());
        }

        Producto productoActualizado = productoRepository.save(producto);
        return turnToDto(productoActualizado);
    }

    public void eliminarProductoLogicamente(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public ProductoDTO getProductoById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return turnToDto(producto);
    }

    public List<ProductoDTO> obtenerProductosInactivos() {
        List<Producto> productos = productoRepository.findByActivoFalse();

        return productos.stream()
                .map(this::turnToDto)
                .collect(Collectors.toList());
    }

    public List<ProductoDTO> obtenerProductosPorNombre(String nombre) {
        List<Producto> productos = productoRepository.findByNombreContaining(nombre);

        return productos.stream()
                .map(this::turnToDto)
                .collect(Collectors.toList());
    }

    public List<ProductoDTO> obtenerProductosInactivosPorNombre(String nombre) {
        List<Producto> productos = productoRepository.findByNombreContainingAndActivoFalse(nombre);

        return productos.stream()
                .map(this::turnToDto)
                .collect(Collectors.toList());
    }

    public ProductoDTO getProductoBySku(String sku) {
        Producto producto = productoRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return turnToDto(producto);
    }

    private ProductoDTO turnToDto(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setSku(producto.getSku());
        dto.setPrecioNeto(producto.getPrecioNeto());
        return dto;
    }

}
