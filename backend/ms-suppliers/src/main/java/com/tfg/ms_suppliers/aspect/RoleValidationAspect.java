package com.tfg.ms_suppliers.aspect;

import com.tfg.ms_suppliers.annotation.RequireRole;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Aspecto para validar roles en métodos controladores
 */
@Aspect
@Component
public class RoleValidationAspect {

    @Before("@annotation(requireRole)")
    public void validateRole(JoinPoint joinPoint, RequireRole requireRole) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        
        String userRoles = request.getHeader("X-User-Roles");
        if (userRoles == null || userRoles.trim().isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "No se ha definido el rol del usuario. Acceso denegado."
            );
        }

        String[] requiredRoles = requireRole.value();
        boolean hasRole = Arrays.stream(requiredRoles)
                .anyMatch(role -> userRoles.toUpperCase().contains(role.toUpperCase()));

        if (!hasRole) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Usuario no tiene permisos para realizar esta operación. Se requiere rol: " + Arrays.toString(requiredRoles)
            );
        }
    }
}
