package com.tfg.ms_audit.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_AUDITORIA = "exchange.auditoria";

    public static final String COLA_AUDITORIA_USERS = "cola.auditoria.users";
    public static final String COLA_AUDITORIA_CATALOG = "cola.auditoria.catalog";
    public static final String COLA_AUDITORIA_INVENTORY = "cola.auditoria.inventory";
    public static final String COLA_AUDITORIA_SUPPLIERS = "cola.auditoria.suppliers";

    public static final String ROUTING_KEY_USERS = "audit.users";
    public static final String ROUTING_KEY_CATALOG = "audit.catalog";
    public static final String ROUTING_KEY_INVENTORY = "audit.inventory";
    public static final String ROUTING_KEY_SUPPLIERS = "audit.suppliers";

    @Bean
    public TopicExchange auditExchange() {
        return new TopicExchange(EXCHANGE_AUDITORIA);
    }

    @Bean
    public Queue colaUsers() {
        return QueueBuilder.durable(COLA_AUDITORIA_USERS).build();
    }

    @Bean
    public Queue colaCatalog() {
        return QueueBuilder.durable(COLA_AUDITORIA_CATALOG).build();
    }

    @Bean
    public Queue colaInventory() {
        return QueueBuilder.durable(COLA_AUDITORIA_INVENTORY).build();
    }

    @Bean
    public Queue colaSuppliers() {
        return QueueBuilder.durable(COLA_AUDITORIA_SUPPLIERS).build();
    }

    @Bean
    public Binding bindingUsers(Queue colaUsers, TopicExchange auditExchange) {
        return BindingBuilder.bind(colaUsers).to(auditExchange).with(ROUTING_KEY_USERS);
    }

    @Bean
    public Binding bindingCatalog(Queue colaCatalog, TopicExchange auditExchange) {
        return BindingBuilder.bind(colaCatalog).to(auditExchange).with(ROUTING_KEY_CATALOG);
    }

    @Bean
    public Binding bindingInventory(Queue colaInventory, TopicExchange auditExchange) {
        return BindingBuilder.bind(colaInventory).to(auditExchange).with(ROUTING_KEY_INVENTORY);
    }

    @Bean
    public Binding bindingSuppliers(Queue colaSuppliers, TopicExchange auditExchange) {
        return BindingBuilder.bind(colaSuppliers).to(auditExchange).with(ROUTING_KEY_SUPPLIERS);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}