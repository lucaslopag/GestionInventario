package com.tfg.ms_users.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailMessage {
    private String to;
    private String subject;
    private String body;
}
