package com.hexocnc.lead.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public DirectExchange leadExchange(@Value("${app.rabbit.exchange}") final String name) {
        return new DirectExchange(name, true, false);
    }

    @Bean
    public Queue leadCreatedQueue(@Value("${app.rabbit.lead-created.queue}") final String name) {
        return QueueBuilder.durable(name).build();
    }

    @Bean
    public Binding leadCreatedBinding(final Queue leadCreatedQueue,
                                      final DirectExchange leadExchange,
                                      @Value("${app.rabbit.lead-created.routing-key}") final String routingKey) {
        return BindingBuilder.bind(leadCreatedQueue).to(leadExchange).with(routingKey);
    }

    /** JSON (de)serialization; the target type is inferred from the listener method parameter. */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
