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
        String email = mensaje.get("to");
        String subject = mensaje.get("subject");
        String body = mensaje.get("body");

        System.out.println(" Recibido mensaje para enviar email a: " + email);

        try {
            SimpleMailMessage emailMsg = new SimpleMailMessage();
            emailMsg.setTo(email);
            emailMsg.setSubject(subject);
            emailMsg.setText(body);

            mailSender.send(emailMsg);
            System.out.println(" Email enviado a: " + email);
        } catch (Exception e) {
            System.err.println(" Error enviando email: " + e.getMessage());
            throw e;
        }
    }
}