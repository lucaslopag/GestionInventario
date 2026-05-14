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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;
    private final JwtService jwtService;
    private final com.tfg.ms_users.config.RabbitMQConfig rabbitMQConfig;

    private void auditar(String accion, Long entidadId, String usuarioEmail, String detalles) {
        com.tfg.ms_users.dto.AuditoriaEventoDTO evento = com.tfg.ms_users.dto.AuditoriaEventoDTO.builder()
                .servicioOrigen("USERS")
                .accion(accion)
                .entidadId(entidadId)
                .usuarioEmail(usuarioEmail)
                .fecha(LocalDateTime.now())
                .detalles(detalles)
                .build();
        rabbitTemplate.convertAndSend(
                com.tfg.ms_users.config.RabbitMQConfig.EXCHANGE_AUDITORIA,
                com.tfg.ms_users.config.RabbitMQConfig.ROUTING_KEY_AUDITORIA,
                evento);
    }

    @Value("${spring.rabbitmq.queues.mail}")
    private String mailQueue;

    @Value("${APP_BASE_URL:http://localhost:8080}")
    private String appBaseUrl;

    public String registrar(RegisterRequest request) {
        // 1. Validar política de contraseña
        String password = request.getPassword();
        if (password == null || password.length() < 8 || !password.matches(".*\\d.*") || !password.matches(".*[a-zA-Z].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 8 caracteres, un dígito y una letra");
        }

        // 2. Verificar si el email ya existe
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
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

        // Auditar registro
        auditar("REGISTER", usuario.getId(), usuario.getEmail(), "Registro de nuevo usuario: " + usuario.getNombre());

        // 3. Enviar mensaje a RabbitMQ para mandar email
        enviarEmailConfirmacion(usuario);

        return "Usuario registrado con ÃƒÂ©xito. Revisa tu email para confirmar la cuenta.";
    }

    private void enviarEmailConfirmacion(Usuario usuario) {
        String urlConfirmacion = appBaseUrl + "/api/auth/confirmar?token=" + usuario.getTokenConfirmacion();

        EmailMessage email = EmailMessage.builder()
                .to(usuario.getEmail())
                .subject("Confirma tu cuenta - Sistema de Inventario")
                .body("Hola " + usuario.getNombre() + ",\n\n" +
                        "Por favor, confirma tu cuenta haciendo clic en el siguiente enlace:\n" +
                        urlConfirmacion + "\n\n" +
                        "Este enlace expirarÃƒÂ¡ en 24 horas.")
                .build();

        rabbitTemplate.convertAndSend(mailQueue, email);
        System.out.println("Mensaje de email enviado a la cola para: " + usuario.getEmail());
    }

    public String confirmar(String token) {
        // 1. Buscar el usuario por el token
        Usuario usuario = usuarioRepository.findByTokenConfirmacion(token)
                .orElseThrow(() -> new RuntimeException("Token de confirmaciÃƒÂ³n no vÃƒÂ¡lido"));

        // 2. Verificar si el token ha expirado
        if (usuario.getTokenExpira().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El enlace de confirmaciÃƒÂ³n ha expirado");
        }

        // 3. Confirmar la cuenta y limpiar el token
        usuario.setConfirmado(true);
        usuario.setTokenConfirmacion(null);
        usuario.setTokenExpira(null);

        usuarioRepository.save(usuario);

        return "Cuenta confirmada con ÃƒÂ©xito. Ya puedes iniciar sesiÃƒÂ³n.";
    }

    public LoginResponse login(LoginRequest request) {
        // 1. Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElse(null);
        if (usuario == null) {
            System.err.println("[LOGIN FALLIDO] Email no registrado: " + request.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas o cuenta no confirmada");
        }

        // 2. Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            System.err.println("[LOGIN FALLIDO] Contraseña incorrecta para: " + request.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas o cuenta no confirmada");
        }

        // 3. Verificar si está confirmado
        if (!usuario.isConfirmado()) {
            System.err.println("[LOGIN FALLIDO] Cuenta sin confirmar para: " + request.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas o cuenta no confirmada");
        }

        // 4. Generar Token JWT
        String token = jwtService.generateToken(usuario.getEmail(), usuario.getRol());

        // Auditar login
        auditar("LOGIN", usuario.getId(), usuario.getEmail(), "Inicio de sesiÃƒÂ³n exitoso");

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
