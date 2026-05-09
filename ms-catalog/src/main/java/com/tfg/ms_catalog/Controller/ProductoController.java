package com.tfg.ms_catalog.Controller;

import com.tfg.ms_catalog.DTO.ProductoDTO;
import com.tfg.ms_catalog.Service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        return new ResponseEntity<>(productoService.crearProducto(productoDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerTodosLosProductos() {
        return ResponseEntity.ok(productoService.obtenerTodosLosProductos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoLogicamente(@PathVariable Long id) {
        productoService.eliminarProductoLogicamente(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/fisico")
    public ResponseEntity<Void> eliminarProductoFisicamente(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.getProductoById(id));
    }

    @GetMapping("/inactivos")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosInactivos() {
        return ResponseEntity.ok(productoService.obtenerProductosInactivos());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.obtenerProductosPorNombre(nombre));
    }

    @GetMapping("/inactivos/buscar")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosInactivosPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.obtenerProductosInactivosPorNombre(nombre));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductoDTO> getProductoBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productoService.getProductoBySku(sku));
    }
}
