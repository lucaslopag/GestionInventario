package com.tfg.ms_users.service;

import com.tfg.ms_users.dto.RegisterRequest;
import com.tfg.ms_users.entity.Usuario;
import com.tfg.ms_users.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import com.tfg.ms_users.dto.EmailMessage;
import com.tfg.ms_users.dto.LoginRequest;
import com.tfg.ms_users.dto.LoginResponse;
import com.tfg.ms_users.security.JwtService;
import java.util.UUID;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;
    private final JwtService jwtService;

    @Value("${spring.rabbitmq.queues.mail}")
    private String mailQueue;

    @Value("${APP_BASE_URL:http://localhost:80}")
    private String appBaseUrl;

    public String registrar(RegisterRequest request) {
        // 1. Verificar si el email ya existe
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // 2. Crear el nuevo usuario
        String token = UUID.randomUUID().toString();
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol("ROLE_USER")
                .confirmado(false)
                .tokenConfirmacion(token)
                .tokenExpira(LocalDateTime.now().plusHours(24))
                .build();

        usuarioRepository.save(usuario);

        // 3. Enviar mensaje a RabbitMQ para mandar email
        enviarEmailConfirmacion(usuario);

        return "Usuario registrado con éxito. Revisa tu email para confirmar la cuenta.";
    }

    private void enviarEmailConfirmacion(Usuario usuario) {
        String urlConfirmacion = appBaseUrl + "/api/auth/confirmar?token=" + usuario.getTokenConfirmacion();
        
        EmailMessage email = EmailMessage.builder()
                .to(usuario.getEmail())
                .subject("Confirma tu cuenta - Sistema de Inventario")
                .body("Hola " + usuario.getNombre() + ",\n\n" +
                      "Por favor, confirma tu cuenta haciendo clic en el siguiente enlace:\n" +
                      urlConfirmacion + "\n\n" +
                      "Este enlace expirará en 24 horas.")
                .build();

        rabbitTemplate.convertAndSend(mailQueue, email);
        System.out.println("Mensaje de email enviado a la cola para: " + usuario.getEmail());
    }

    public String confirmar(String token) {
        // 1. Buscar el usuario por el token
        Usuario usuario = usuarioRepository.findByTokenConfirmacion(token)
                .orElseThrow(() -> new RuntimeException("Token de confirmación no válido"));

        // 2. Verificar si el token ha expirado
        if (usuario.getTokenExpira().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El enlace de confirmación ha expirado");
        }

        // 3. Confirmar la cuenta y limpiar el token
        usuario.setConfirmado(true);
        usuario.setTokenConfirmacion(null);
        usuario.setTokenExpira(null);

        usuarioRepository.save(usuario);

        return "Cuenta confirmada con éxito. Ya puedes iniciar sesión.";
    }

    public LoginResponse login(LoginRequest request) {
        // 1. Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        // 2. Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        // 3. Verificar si está confirmado
        if (!usuario.isConfirmado()) {
            throw new RuntimeException("Debes confirmar tu cuenta antes de iniciar sesión");
        }

        // 4. Generar Token JWT
        String token = jwtService.generateToken(usuario.getEmail(), usuario.getRol());

        return LoginResponse.builder()
                .token(token)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol())
                .build();
    }

    public Usuario getMe(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
