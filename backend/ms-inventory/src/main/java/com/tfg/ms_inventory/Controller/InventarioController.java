package com.tfg.ms_inventory.Controller;

import com.tfg.ms_inventory.DTO.MovimientoDTO;
import com.tfg.ms_inventory.DTO.MovimientoRequestDTO;
import com.tfg.ms_inventory.DTO.StockDTO;
import com.tfg.ms_inventory.Service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @PostMapping("/entrada")
    public ResponseEntity<MovimientoDTO> registrarEntrada(
            @RequestBody MovimientoRequestDTO movimientoRequest,
            @RequestHeader("X-User-Email") String usuarioEmail) {
        
        movimientoRequest.setTipo("ENTRADA");
        movimientoRequest.setUsuarioEmail(usuarioEmail);
        
        MovimientoDTO nuevoMovimiento = inventarioService.procesarMovimiento(movimientoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMovimiento);
    }

    @PostMapping("/salida")
    public ResponseEntity<MovimientoDTO> registrarSalida(
            @RequestBody MovimientoRequestDTO movimientoRequest,
            @RequestHeader("X-User-Email") String usuarioEmail) {
        
        movimientoRequest.setTipo("SALIDA");
        movimientoRequest.setUsuarioEmail(usuarioEmail);
        
        MovimientoDTO nuevoMovimiento = inventarioService.procesarMovimiento(movimientoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMovimiento);
    }

    @GetMapping("/stock/{id}")
    public ResponseEntity<StockDTO> consultarStock(
            @PathVariable Long id,
            @RequestHeader("X-User-Email") String usuarioEmail) {
        
        StockDTO stock = inventarioService.consultarStock(id, usuarioEmail);
        if (stock == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stock);
    }

    @GetMapping("/movimientos")
    public ResponseEntity<List<MovimientoDTO>> obtenerHistorialMovimientos(
            @RequestHeader("X-User-Email") String usuarioEmail) {
        
        return ResponseEntity.ok(inventarioService.obtenerMovimientos(usuarioEmail));
    }
}
