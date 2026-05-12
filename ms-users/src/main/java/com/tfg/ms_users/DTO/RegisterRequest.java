package com.tfg.ms_users.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
}
