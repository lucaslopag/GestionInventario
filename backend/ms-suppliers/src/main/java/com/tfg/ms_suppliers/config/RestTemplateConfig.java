package com.tfg.ms_suppliers.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Configuración de RestTemplate para comunicación entre microservicios
 * Propaga automáticamente los headers del usuario a las llamadas entre servicios
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Interceptor que propaga headers de autenticación
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            try {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest httpRequest = attributes.getRequest();
                    
                    // Propagar headers de usuario
                    String userEmail = httpRequest.getHeader("X-User-Email");
                    String userRoles = httpRequest.getHeader("X-User-Roles");
                    String authorization = httpRequest.getHeader("Authorization");
                    
                    if (userEmail != null && !userEmail.isEmpty()) {
                        request.getHeaders().set("X-User-Email", userEmail);
                    }
                    if (userRoles != null && !userRoles.isEmpty()) {
                        request.getHeaders().set("X-User-Roles", userRoles);
                    }
                    if (authorization != null && !authorization.isEmpty()) {
                        request.getHeaders().set("Authorization", authorization);
                    }
                }
            } catch (IllegalStateException e) {
                // No hay request context (ej: llamadas asincrónicas)
                // Continuar sin propagar headers
            }
            
            return execution.execute(request, body);
        };
        
        restTemplate.getInterceptors().add(interceptor);
        return restTemplate;
    }
}
