package com.tfg.ms_users.dto;

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
