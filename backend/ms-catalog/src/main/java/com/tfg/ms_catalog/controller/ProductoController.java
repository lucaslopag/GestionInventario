package com.tfg.ms_catalog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.ms_catalog.dto.ProductoDTO;
import com.tfg.ms_catalog.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO productoDTO,
            @RequestHeader("X-User-Email") String usuarioEmail) {
        return new ResponseEntity<>(productoService.crearProducto(productoDTO, usuarioEmail), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerTodosLosProductos(
            @RequestHeader("X-User-Email") String usuarioEmail) {
        return ResponseEntity.ok(productoService.obtenerTodosLosProductos(usuarioEmail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto,
            @RequestHeader("X-User-Email") String usuarioEmail) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, dto, usuarioEmail));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoLogicamente(@PathVariable Long id,
            @RequestHeader("X-User-Email") String usuarioEmail) {
        productoService.eliminarProductoLogicamente(id, usuarioEmail);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/fisico")
    public ResponseEntity<Void> eliminarProductoFisicamente(@PathVariable Long id,
            @RequestHeader("X-User-Email") String usuarioEmail) {
        productoService.eliminarProducto(id, usuarioEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Long id,
            @RequestHeader("X-User-Email") String usuarioEmail) {
        return ResponseEntity.ok(productoService.getProductoById(id, usuarioEmail));
    }

    @GetMapping("/inactivos")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosInactivos(
            @RequestHeader("X-User-Email") String usuarioEmail) {
        return ResponseEntity.ok(productoService.obtenerProductosInactivos(usuarioEmail));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosPorNombre(@RequestParam String nombre,
            @RequestHeader("X-User-Email") String usuarioEmail) {
        return ResponseEntity.ok(productoService.obtenerProductosPorNombre(nombre, usuarioEmail));
    }

    @GetMapping("/inactivos/buscar")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosInactivosPorNombre(@RequestParam String nombre,
            @RequestHeader("X-User-Email") String usuarioEmail) {
        return ResponseEntity.ok(productoService.obtenerProductosInactivosPorNombre(nombre, usuarioEmail));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductoDTO> getProductoBySku(@PathVariable String sku,
            @RequestHeader("X-User-Email") String usuarioEmail) {
        return ResponseEntity.ok(productoService.getProductoBySku(sku, usuarioEmail));
    }
}
