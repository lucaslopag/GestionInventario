package com.tfg.ms_users.controller;

import com.tfg.ms_users.entity.Usuario;
import com.tfg.ms_users.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // GET /usuarios — lista todos los usuarios (solo ADMIN)
    @GetMapping
    public ResponseEntity<?> listarUsuarios(
            @RequestHeader(value = "X-User-Roles", defaultValue = "") String roles) {

        if (!roles.contains("ADMIN")) {
            return ResponseEntity.status(403).body(Map.of("message", "Acceso denegado: se requiere rol ADMIN."));
        }
        List<Usuario> usuarios = usuarioRepository.findAll();
        // Limpiar datos sensibles antes de devolver
        usuarios.forEach(u -> {
            u.setPassword(null);
            u.setTokenConfirmacion(null);
        });
        return ResponseEntity.ok(usuarios);
    }

    // PUT /usuarios/{id}/rol — cambiar rol de usuario (solo ADMIN)
    @PutMapping("/{id}/rol")
    public ResponseEntity<?> cambiarRol(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-Roles", defaultValue = "") String roles,
            @RequestHeader(value = "X-User-Email", defaultValue = "") String callerEmail) {

        if (!roles.contains("ADMIN")) {
            return ResponseEntity.status(403).body(Map.of("message", "Acceso denegado: se requiere rol ADMIN."));
        }

        String nuevoRol = body.get("rol");
        if (nuevoRol == null || (!nuevoRol.equals("ADMIN") && !nuevoRol.equals("EMPLEADO"))) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rol inválido. Valores permitidos: ADMIN, EMPLEADO."));
        }

        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = opt.get();
        // No permitir que un admin se quite su propio rol
        if (usuario.getEmail().equals(callerEmail) && nuevoRol.equals("EMPLEADO")) {
            return ResponseEntity.badRequest().body(Map.of("message", "No puedes quitarte el rol ADMIN a ti mismo."));
        }

        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);
        usuario.setPassword(null);
        usuario.setTokenConfirmacion(null);
        return ResponseEntity.ok(Map.of("message", "Rol actualizado correctamente.", "usuario", usuario));
    }

    // DELETE /usuarios/{id} — eliminar usuario (solo ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Roles", defaultValue = "") String roles,
            @RequestHeader(value = "X-User-Email", defaultValue = "") String callerEmail) {

        if (!roles.contains("ADMIN")) {
            return ResponseEntity.status(403).body(Map.of("message", "Acceso denegado: se requiere rol ADMIN."));
        }

        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (opt.get().getEmail().equals(callerEmail)) {
            return ResponseEntity.badRequest().body(Map.of("message", "No puedes eliminar tu propia cuenta."));
        }

        usuarioRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Usuario eliminado correctamente."));
    }

    // PUT /usuarios/me — actualizar perfil propio (cualquier usuario autenticado)
    @PutMapping("/me")
    public ResponseEntity<?> actualizarPerfil(
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-Email", defaultValue = "") String email) {

        if (email.isBlank()) {
            return ResponseEntity.status(401).body(Map.of("message", "No autenticado."));
        }

        Optional<Usuario> opt = usuarioRepository.findByEmail(email);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = opt.get();

        if (body.containsKey("nombre") && !body.get("nombre").isBlank()) {
            usuario.setNombre(body.get("nombre").trim());
        }

        if (body.containsKey("password") && !body.get("password").isBlank()) {
            String pass = body.get("password");
            if (pass.length() < 8) {
                return ResponseEntity.badRequest().body(Map.of("message", "La contraseña debe tener al menos 8 caracteres."));
            }
            usuario.setPassword(passwordEncoder.encode(pass));
        }

        usuarioRepository.save(usuario);
        usuario.setPassword(null);
        usuario.setTokenConfirmacion(null);
        return ResponseEntity.ok(Map.of("message", "Perfil actualizado correctamente.", "usuario", usuario));
    }
}
