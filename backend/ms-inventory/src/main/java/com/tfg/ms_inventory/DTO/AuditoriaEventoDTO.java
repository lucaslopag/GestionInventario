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
    private Long productoId;
    private Long proveedorId;
    private Integer cantidad;
    private Integer stockResultante;

    public AuditoriaEventoDTO(String servicioOrigen, String accion, Long entidadId, String usuarioEmail, LocalDateTime fecha, String detalles) {
        this.servicioOrigen = servicioOrigen;
        this.accion = accion;
        this.entidadId = entidadId;
        this.usuarioEmail = usuarioEmail;
        this.fecha = fecha;
        this.detalles = detalles;
    }
}
