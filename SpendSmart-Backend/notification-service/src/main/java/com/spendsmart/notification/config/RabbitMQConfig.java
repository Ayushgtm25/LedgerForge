package com.spendsmart.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RabbitMQConfig {

    @Value("${app.messaging.auth-notification-queue}")
    private String queueName;

    @Value("${app.messaging.auth-notification-exchange}")
    private String exchangeName;

    @Value("${app.messaging.auth-notification-routing-key}")
    private String routingKey;

    @Bean
    public Queue authNotificationQueue() {
        return new Queue(queueName, true); // durable queue
    }

    @Bean
    public DirectExchange authNotificationExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Binding binding(Queue authNotificationQueue, DirectExchange authNotificationExchange) {
        return BindingBuilder.bind(authNotificationQueue).to(authNotificationExchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
