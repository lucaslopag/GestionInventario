package com.tfg.ms_mail.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COLA_MAILS = "cola.mails";
    public static final String COLA_MAILS_DLQ = "cola.mails.dlq";

    @Bean
    public Queue colaMails() {
        return QueueBuilder.durable(COLA_MAILS)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", COLA_MAILS_DLQ)
                .build();
    }

    @Bean
    public Queue colaMailsDlq() {
        return QueueBuilder.durable(COLA_MAILS_DLQ).build();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}