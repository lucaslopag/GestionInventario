package com.tfg.ms_catalog.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_AUDITORIA = "cola.auditoria";
    public static final String QUEUE_AUDITORIA_DLQ = "cola.auditoria.dlq";
    public static final String EXCHANGE_AUDITORIA = "exchange.auditoria";
    public static final String ROUTING_KEY_AUDITORIA = "routing.auditoria";

    @Bean
    public Queue auditoriaQueue() {
        return QueueBuilder.durable(QUEUE_AUDITORIA)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_AUDITORIA_DLQ)
                .build();
    }

    @Bean
    public Queue auditoriaDlqQueue() {
        return QueueBuilder.durable(QUEUE_AUDITORIA_DLQ).build();
    }

    @Bean
    public DirectExchange auditoriaExchange() {
        return new DirectExchange(EXCHANGE_AUDITORIA);
    }

    @Bean
    public Binding bindingAuditoria(Queue auditoriaQueue, DirectExchange auditoriaExchange) {
        return BindingBuilder.bind(auditoriaQueue).to(auditoriaExchange).with(ROUTING_KEY_AUDITORIA);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
