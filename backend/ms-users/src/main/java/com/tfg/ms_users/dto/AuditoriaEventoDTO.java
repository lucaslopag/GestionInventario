package com.tfg.ms_users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaEventoDTO {
    private String servicioOrigen;
    private String accion;
    private Long entidadId;
    private String usuarioEmail;
    private LocalDateTime fecha;
    private String detalles;
    private Long productoId;
    private Long proveedorId;
    private Integer cantidad;
    private Integer stockResultante;

    public static class AuditoriaEventoDTOBuilder {
        // El builder de Lombok ya manejarÃ¡ los campos nuevos si se usa @Builder en la clase
    }
}
