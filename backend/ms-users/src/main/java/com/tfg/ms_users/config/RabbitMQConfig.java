package com.tfg.ms_users.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_AUDITORIA = "exchange.auditoria";
    public static final String ROUTING_KEY_AUDITORIA = "audit.users";

    @Value("${spring.rabbitmq.queues.mail}")
    private String mailQueue;

    @Bean
    public Queue mailQueue() {
        return org.springframework.amqp.core.QueueBuilder.durable(mailQueue)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", mailQueue + ".dlq")
                .build();
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory, com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate = new org.springframework.amqp.rabbit.core.RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter(objectMapper));
        return rabbitTemplate;
    }
}
