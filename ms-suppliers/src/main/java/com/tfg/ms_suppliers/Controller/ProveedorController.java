package com.tfg.ms_suppliers.Controller;

import com.tfg.ms_suppliers.DTO.ProveedorDTO;
import com.tfg.ms_suppliers.Model.Proveedor;
import com.tfg.ms_suppliers.Service.ProveedorService;
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
    public ResponseEntity<List<ProveedorDTO>> getAll() {
        return ResponseEntity.ok(proveedorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> getById(@PathVariable Long id) {
        ProveedorDTO proveedor = proveedorService.findById(id);
        if (proveedor != null) {
            return ResponseEntity.ok(proveedor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ProveedorDTO> create(@RequestBody Proveedor proveedor) {
        ProveedorDTO nuevoProveedor = proveedorService.save(proveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProveedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDTO> update(@PathVariable Long id, @RequestBody Proveedor proveedor) {
        ProveedorDTO proveedorActualizado = proveedorService.update(id, proveedor);
        if (proveedorActualizado != null) {
            return ResponseEntity.ok(proveedorActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ProveedorDTO proveedorBorrado = proveedorService.delete(id);
        if (proveedorBorrado != null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
