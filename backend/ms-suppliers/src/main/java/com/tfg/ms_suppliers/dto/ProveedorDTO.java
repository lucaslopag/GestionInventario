package com.tfg.ms_suppliers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorDTO {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;

    @Email(message = "El email debe ser valido")
    @NotBlank(message = "El email no puede estar vacio")
    private String email;

    @NotBlank(message = "La direccion no puede estar vacia")
    private String direccion;
}
