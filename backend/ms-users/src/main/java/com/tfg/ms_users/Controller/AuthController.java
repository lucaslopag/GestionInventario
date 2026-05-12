package com.tfg.ms_users.Controller;

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
    public ResponseEntity<String> confirmar(@RequestParam String token) {
        return ResponseEntity.ok(authService.confirmar(token));
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
