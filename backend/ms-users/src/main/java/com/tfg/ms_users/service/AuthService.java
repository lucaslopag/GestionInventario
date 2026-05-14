package com.tfg.ms_users.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.tfg.ms_users.config.RabbitMQConfig;
import com.tfg.ms_users.dto.AuditoriaEventoDTO;
import com.tfg.ms_users.dto.EmailMessage;
import com.tfg.ms_users.dto.LoginRequest;
import com.tfg.ms_users.dto.LoginResponse;
import com.tfg.ms_users.dto.RegisterRequest;
import com.tfg.ms_users.entity.Usuario;
import com.tfg.ms_users.exceptions.PasswordPolicyException;
import com.tfg.ms_users.repository.UsuarioRepository;
import com.tfg.ms_users.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;
    private final JwtService jwtService;

    private void auditar(String accion, Long entidadId, String usuarioEmail, String detalles) {
        AuditoriaEventoDTO evento = AuditoriaEventoDTO.builder()
                .servicioOrigen("USERS")
                .accion(accion)
                .entidadId(entidadId)
                .usuarioEmail(usuarioEmail)
                .fecha(LocalDateTime.now())
                .detalles(detalles)
                .build();
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_AUDITORIA,
                RabbitMQConfig.ROUTING_KEY_AUDITORIA,
                evento);
    }

    @Value("${spring.rabbitmq.queues.mail}")
    private String mailQueue;

    @Value("${APP_BASE_URL:http://localhost:8080}")
    private String appBaseUrl;

    @Transactional
    public String registrar(RegisterRequest request) {
        // 1. Validar política de contraseña (mínimo 8 caracteres, letras y números)
        String password = request.getPassword();
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";
        
        if (password == null || !password.matches(passwordRegex)) {
            throw new PasswordPolicyException("La contraseña debe tener al menos 8 caracteres, un dígito y una letra");
        }
        // 2. Verificar si el email ya existe
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya esta registrado");
        }

        String token = UUID.randomUUID().toString();
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol("EMPLEADO") // Asignar el rol 'EMPLEADO' por defecto según el FDD.
                .confirmado(false)
                .tokenConfirmacion(token)
                .tokenExpira(LocalDateTime.now().plusHours(24))
                .build();

        usuarioRepository.save(usuario);

        // Auditar registro (tolerante a fallos de RabbitMQ)
        try {
            auditar("REGISTER", usuario.getId(), usuario.getEmail(), "Registro de nuevo usuario: " + usuario.getNombre());
        } catch (Exception e) {
            System.err.println("[WARN] No se pudo enviar evento de auditoria (RabbitMQ no disponible): " + e.getMessage());
        }

        // 3. Enviar mensaje a RabbitMQ para mandar email (tolerante a fallos)
        try {
            enviarEmailConfirmacion(usuario);
        } catch (Exception e) {
            System.err.println("[WARN] No se pudo enviar email de confirmacion (RabbitMQ no disponible): " + e.getMessage());
        }

        return "Usuario registrado con exito. Revisa tu email para confirmar la cuenta.";
    }

    private void enviarEmailConfirmacion(Usuario usuario) {
        String urlConfirmacion = appBaseUrl + "/api/auth/confirmar?token=" + usuario.getTokenConfirmacion();

        EmailMessage email = EmailMessage.builder()
                .to(usuario.getEmail())
                .subject("Confirma tu cuenta - Sistema de Inventario")
                .body("Hola " + usuario.getNombre() + ",\n\n" +
                        "Por favor, confirma tu cuenta haciendo clic en el siguiente enlace:\n" +
                        urlConfirmacion + "\n\n" +
                        "Este enlace expirara en 24 horas.")
                .build();

        rabbitTemplate.convertAndSend(mailQueue, email);
        System.out.println("Mensaje de email enviado a la cola para: " + usuario.getEmail());
    }

    public String confirmar(String token) {
        // 1. Buscar el usuario por el token
        Usuario usuario = usuarioRepository.findByTokenConfirmacion(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token de confirmacion no valido"));

        // 2. Verificar si el token ha expirado
        if (usuario.getTokenExpira().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "El enlace de confirmacion ha expirado");
        }

        // 3. Confirmar la cuenta y limpiar el token
        usuario.setConfirmado(true);
        usuario.setTokenConfirmacion(null);
        usuario.setTokenExpira(null);

        usuarioRepository.save(usuario);

        return "Cuenta confirmada con exito. Ya puedes iniciar sesion.";
    }

    public LoginResponse login(LoginRequest request) {
        // 1. Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElse(null);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas o cuenta no confirmada");
        }

        // 2. Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas o cuenta no confirmada");
        }

        // 3. Verificar si está confirmado
        if (!usuario.isConfirmado()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas o cuenta no confirmada");
        }

        // 4. Generar Token JWT
        String token = jwtService.generateToken(usuario.getEmail(), usuario.getRol());

        // Auditar login (tolerante a fallos de RabbitMQ)
        try {
            auditar("LOGIN", usuario.getId(), usuario.getEmail(), "Inicio de sesion exitoso");
        } catch (Exception e) {
            System.err.println("[WARN] No se pudo enviar evento de auditoria en login (RabbitMQ no disponible): " + e.getMessage());
        }

        return LoginResponse.builder()
                .token(token)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol())
                .build();
    }

    public Usuario getMe(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}
