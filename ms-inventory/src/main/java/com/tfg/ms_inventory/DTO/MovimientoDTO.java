package com.tfg.ms_inventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO {
    private Long id;
    private Long productoId;
    private Long proveedorId;
    private String usuarioEmail;
    private Integer cantidad;
    private String tipo;
    private LocalDateTime fecha;
    private Integer stockResultante;
}
