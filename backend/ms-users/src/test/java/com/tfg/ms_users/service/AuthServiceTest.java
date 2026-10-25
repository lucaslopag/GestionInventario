package com.tfg.ms_users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tfg.ms_users.dto.RegisterRequest;
import com.tfg.ms_users.entity.Usuario;
import com.tfg.ms_users.repository.UsuarioRepository;
import com.tfg.ms_users.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private JwtService jwtService;

    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(usuarioRepository, passwordEncoder, rabbitTemplate, jwtService);
    }

    private RegisterRequest peticion(String rol) {
        return new RegisterRequest("Nombre Prueba", "prueba@ejemplo.com", "Clave1234", rol);
    }

    private String rolGuardado() {
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        return captor.getValue().getRol();
    }

    @Test
    void sinSesionNoSePuedeCrearUnAdmin() {
        service.registrar(peticion("ADMIN"), null);

        assertEquals("EMPLEADO", rolGuardado());
    }

    @Test
    void unEmpleadoNoPuedeCrearUnAdmin() {
        service.registrar(peticion("ADMIN"), "EMPLEADO");

        assertEquals("EMPLEADO", rolGuardado());
    }

    @Test
    void unAdminPuedeCrearUnAdmin() {
        service.registrar(peticion("admin"), "ADMIN");

        assertEquals("ADMIN", rolGuardado());
    }

    @Test
    void unRolDesconocidoSeConvierteEnEmpleado() {
        service.registrar(peticion("SUPERADMIN"), "ADMIN");

        assertEquals("EMPLEADO", rolGuardado());
    }

    @Test
    void sinRolPedidoSeCreaComoEmpleado() {
        service.registrar(peticion(null), null);

        assertEquals("EMPLEADO", rolGuardado());
    }
}
