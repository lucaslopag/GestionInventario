package com.tfg.ms_mail.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class MailListener {

    @Autowired
    private JavaMailSender mailSender;

    @RabbitListener(queues = "cola.mails")
    public void recibirMensajeMail(Map<String, String> mensaje) {
        String email = mensaje.get("email");
        String nombre = mensaje.get("nombre");
        String token = mensaje.get("token");

        System.out.println(" Recibido mensaje para enviar email a: " + email);

        try {
            SimpleMailMessage emailMsg = new SimpleMailMessage();
            emailMsg.setTo(email);
            emailMsg.setSubject("Confirma tu cuenta en Sistema de Inventario");
            emailMsg.setText("Hola " + nombre + ",\n\n" +
                    "Confirma tu cuenta aquí:\n" +
                    "http://localhost:8080/api/auth/confirmar?token=" + token + "\n\n" +
                    "Saludos,\nSistema de Gestión de Inventario");

            mailSender.send(emailMsg);
            System.out.println(" Email enviado a: " + email);
        } catch (Exception e) {
            System.err.println(" Error enviando email: " + e.getMessage());
            throw e;
        }
    }
}