package com.tfg.ms_users.controller;

import com.tfg.ms_users.dto.LoginRequest;
import com.tfg.ms_users.dto.LoginResponse;
import com.tfg.ms_users.dto.RegisterRequest;
import com.tfg.ms_users.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registrar(request));
    }

    @GetMapping("/confirmar")
    public ResponseEntity<Void> confirmar(@RequestParam String token) {
        try {
            authService.confirmar(token);
            return ResponseEntity.status(org.springframework.http.HttpStatus.FOUND)
                    .location(java.net.URI.create("/confirmacion.html?status=ok"))
                    .build();
        } catch (Exception e) {
            String reason = "error";
            if (e.getMessage().contains("expirado")) reason = "expired";
            return ResponseEntity.status(org.springframework.http.HttpStatus.FOUND)
                    .location(java.net.URI.create("/confirmacion.html?status=error&reason=" + reason))
                    .build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<com.tfg.ms_users.entity.Usuario> getMe(
            @RequestHeader(value = "X-User-Email", required = false) String email) {
        
        if (email == null) {
            return ResponseEntity.status(401).build();
        }
        
        return ResponseEntity.ok(authService.getMe(email));
    }
}
