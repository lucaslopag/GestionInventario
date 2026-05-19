package com.tfg.ms_inventory.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_AUDITORIA = "cola.auditoria.inventory";
    public static final String EXCHANGE_AUDITORIA = "exchange.auditoria";
    public static final String ROUTING_KEY_AUDITORIA = "audit.inventory";

    @Bean
    public Queue auditoriaQueue() {
        return QueueBuilder.durable(QUEUE_AUDITORIA).build();
    }

    @Bean
    public TopicExchange auditoriaExchange() {
        return new TopicExchange(EXCHANGE_AUDITORIA);
    }

    @Bean
    public Binding bindingAuditoria(Queue auditoriaQueue, TopicExchange auditoriaExchange) {
        return BindingBuilder.bind(auditoriaQueue).to(auditoriaExchange).with(ROUTING_KEY_AUDITORIA);
    }

    // Conversor a JSON para que RabbitMQ entienda los objetos Java (AuditoriaEventoDTO)
    @Bean
    public MessageConverter jsonMessageConverter(com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
