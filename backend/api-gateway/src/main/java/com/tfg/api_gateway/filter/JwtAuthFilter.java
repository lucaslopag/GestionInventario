package com.tfg.api_gateway.filter;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/confirmar");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        if (isPublicRoute(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            String email = claims.getSubject();
            String rolFromClaims = claims.get("roles", String.class);
            
            // Validar que el rol existe
            if (rolFromClaims == null || rolFromClaims.trim().isEmpty()) {
                System.err.println("[WARN] Token sin rol definido para: " + email);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
            
            // Normalizar roles (eliminar espacios, convertir a mayúsculas)
            String normalizedRole = rolFromClaims.trim().toUpperCase();
            List<String> roles = List.of(normalizedRole.split("\\s*,\\s*"));

            if (!isAuthorized(path, method, roles)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Roles", normalizedRole)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SignatureException | WeakKeyException | IllegalArgumentException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    // Lógica de permisos centralizada
    private boolean isAuthorized(String path, HttpMethod method, List<String> roles) {
        boolean isAdmin = roles.contains("ADMIN");
        
        // Rutas de Auditoría: solo ADMIN puede hacer peticiones que no sean GET
        if (path.startsWith("/api/auditoria") && !"GET".equalsIgnoreCase(method.name()) && !isAdmin) return false;

        // Rutas de Catálogo y Proveedores:
        // GET: Permitido para todos (ADMIN/EMPLEADO)
        // POST/PUT/DELETE: Solo ADMIN
        if ((path.startsWith("/api/productos") || path.startsWith("/api/proveedores"))) {
           if (!"GET".equalsIgnoreCase(method.name()) && !isAdmin) return false;
        }

        // Por defecto permitir si el token es válido
        return true;
    }

    private boolean isWriteMethod(HttpMethod method) {
        return HttpMethod.POST.equals(method) || 
               HttpMethod.PUT.equals(method) || 
               HttpMethod.DELETE.equals(method) ||
               HttpMethod.PATCH.equals(method);
    }

private boolean isPublicRoute(String path) {
    return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
}   

    @Override
    public int getOrder() {
        return -1;
    }
}
