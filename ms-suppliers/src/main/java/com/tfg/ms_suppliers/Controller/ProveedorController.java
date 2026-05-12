package com.tfg.ms_suppliers.Controller;

import com.tfg.ms_suppliers.DTO.ProveedorDTO;
import com.tfg.ms_suppliers.Service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> getAll(@RequestHeader("X-User-Email") String usuarioEmail) {
        return ResponseEntity.ok(proveedorService.findAll(usuarioEmail));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> getById(@PathVariable Long id, @RequestHeader("X-User-Email") String usuarioEmail) {
        ProveedorDTO proveedor = proveedorService.findById(id, usuarioEmail);
        if (proveedor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(proveedor);
    }

    @PostMapping
    public ResponseEntity<ProveedorDTO> create(@Valid @RequestBody ProveedorDTO proveedorDTO, @RequestHeader("X-User-Email") String usuarioEmail) {
        ProveedorDTO nuevoProveedor = proveedorService.save(proveedorDTO, usuarioEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProveedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDTO> update(@PathVariable Long id, @Valid @RequestBody ProveedorDTO proveedorDTO, @RequestHeader("X-User-Email") String usuarioEmail) {
        ProveedorDTO actualizado = proveedorService.update(id, proveedorDTO, usuarioEmail);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestHeader("X-User-Email") String usuarioEmail) {
        proveedorService.delete(id, usuarioEmail);
        return ResponseEntity.noContent().build();
    }
}
