package com.tfg.ms_audit.controller;

import com.tfg.ms_audit.model.LogAuditoria;
import com.tfg.ms_audit.repository.LogAuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    @Autowired
    private LogAuditoriaRepository logRepository;

    @GetMapping
    public ResponseEntity<?> listarTodos(
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (!esAdmin(roles)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: solo ADMIN puede ver la auditoría");
        }
        return ResponseEntity.ok(logRepository.findAllByOrderByFechaDesc());
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<?> listarPorProducto(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (!esAdmin(roles)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: solo ADMIN puede ver la auditoría");
        }
        return ResponseEntity.ok(logRepository.findByProductoIdOrderByFechaDesc(id));
    }

    @GetMapping("/usuario/{email}")
    public ResponseEntity<?> listarPorUsuario(
            @PathVariable String email,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (!esAdmin(roles)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: solo ADMIN puede ver la auditoría");
        }
        return ResponseEntity.ok(logRepository.findByUsuarioEmailOrderByFechaDesc(email));
    }

    private boolean esAdmin(String roles) {
        return roles != null && roles.toUpperCase().contains("ADMIN");
    }
}