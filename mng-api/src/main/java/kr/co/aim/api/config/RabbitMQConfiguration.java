package kr.co.aim.api.config;

import ezieco.eziframe.middleware.event.MessageConverter;
import ezieco.eziframe.middleware.event.MessageDispatcher;
import ezieco.eziframe.middleware.vendor.rabbitmq.factory.RabbitMQFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@ConditionalOnProperty(prefix = "middleware.mng.enable", name = "rabbit", matchIfMissing = true)
public class RabbitMQConfiguration {

    @Bean
    public static RabbitMQFactory rabbitMQFactory(@Lazy MessageConverter messageConverter,
            @Lazy MessageDispatcher messageDispatcher) {

        return RabbitMQFactory.builder()
                .messageConverter(messageConverter)
                .messageDispatcher(messageDispatcher)
                .build();
    }
}
