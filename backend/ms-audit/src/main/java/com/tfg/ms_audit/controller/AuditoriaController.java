package com.tfg.ms_audit.controller;

import com.tfg.ms_audit.repository.LogAuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    @Autowired
    private LogAuditoriaRepository logRepository;

    @GetMapping
    public ResponseEntity<?> listarTodos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (!esAdmin(roles)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: solo ADMIN puede ver la auditoría");
        }
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime inicio = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime fin = hasta != null ? hasta.atTime(LocalTime.MAX) : null;
        return ResponseEntity.ok(logRepository.findAllFiltrado(inicio, fin, pageable));
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<?> listarPorProducto(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (!esAdmin(roles)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: solo ADMIN puede ver la auditoría");
        }
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime inicio = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime fin = hasta != null ? hasta.atTime(LocalTime.MAX) : null;
        return ResponseEntity.ok(logRepository.findByProductoIdFiltrado(id, inicio, fin, pageable));
    }

    @GetMapping("/usuario/{email}")
    public ResponseEntity<?> listarPorUsuario(
            @PathVariable String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (!esAdmin(roles)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: solo ADMIN puede ver la auditoría");
        }
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime inicio = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime fin = hasta != null ? hasta.atTime(LocalTime.MAX) : null;
        return ResponseEntity.ok(logRepository.findByUsuarioEmailFiltrado(email, inicio, fin, pageable));
    }

    @GetMapping("/servicio/{nombre}")
    public ResponseEntity<?> listarPorServicio(
            @PathVariable String nombre,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (!esAdmin(roles)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: solo ADMIN puede ver la auditoría");
        }
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime inicio = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime fin = hasta != null ? hasta.atTime(LocalTime.MAX) : null;
        return ResponseEntity.ok(logRepository.findByServicioOrigenFiltrado(nombre.toUpperCase(), inicio, fin, pageable));
    }

    private boolean esAdmin(String roles) {
        return roles != null && roles.toUpperCase().contains("ADMIN");
    }
}