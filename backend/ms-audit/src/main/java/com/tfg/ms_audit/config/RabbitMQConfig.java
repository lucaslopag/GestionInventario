package com.tfg.ms_audit.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COLA_AUDITORIA = "cola.auditoria";
    public static final String COLA_AUDITORIA_DLQ = "cola.auditoria.dlq";

    @Bean
    public Queue colaAuditoria() {
        return QueueBuilder.durable(COLA_AUDITORIA)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", COLA_AUDITORIA_DLQ)
                .build();
    }

    @Bean
    public Queue colaAuditoriaDlq() {
        return QueueBuilder.durable(COLA_AUDITORIA_DLQ).build();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}