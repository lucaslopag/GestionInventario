package com.tfg.ms_catalog.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String nombre;

    @NotNull(message = "El precio neto no puede estar vacío")
    @Min(value = 0, message = "El precio neto debe ser mayor o igual a 0")
    private Double precioNeto;
    private String sku;
    private Boolean activo;
}