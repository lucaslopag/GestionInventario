package com.tfg.ms_inventory.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoRequestDTO {
    private Long productoId;
    private Long proveedorId;
    private Integer cantidad;
    @NotBlank(message = "El tipo no puede estar vacío")
    @Pattern(regexp = "ENTRADA|SALIDA", message = "El tipo solo puede ser ENTRADA o SALIDA")
    private String tipo;
    private String usuarioEmail;
    private LocalDateTime fecha;
}
