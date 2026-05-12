package com.tfg.ms_inventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaEventoDTO {
    private String servicioOrigen;
    private String accion;
    private Long entidadId;
    private String usuarioEmail;
    private LocalDateTime fecha;
    private String detalles;
}
