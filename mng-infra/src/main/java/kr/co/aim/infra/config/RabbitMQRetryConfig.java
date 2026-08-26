package kr.co.aim.infra.config;

import org.aopalliance.aop.Advice;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"pex", "tex", "scheduler", "simulator"})
public class RabbitMQRetryConfig {

    @Value("${custom.rabbitmq.retry.enabled:false}")
    private boolean retryEnabled;

    @Value("${custom.rabbitmq.retry.max-attempts:3}")
    private int maxAttempts;

    @Value("${custom.rabbitmq.retry.initial-interval:1000}")
    private long initialInterval;

    @Value("${custom.rabbitmq.retry.multiplier:2.0}")
    private double multiplier;

    @Value("${custom.rabbitmq.retry.max-interval:5000}")
    private long maxInterval;

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);

        // 예외 발생 시 원본 큐로 Requeue 방지 (DLX로 라우팅되도록 설정)
        factory.setDefaultRequeueRejected(false);
        factory.setContainerCustomizer(c -> c.setShutdownTimeout(60_000L));

        // YML 설정에 따른 Retry Advice 동적 적용
        if (retryEnabled) {
            factory.setAdviceChain(retryInterceptor());
        }

        return factory;
    }

    private Advice retryInterceptor() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(maxAttempts)
                .backOffOptions(initialInterval, multiplier, maxInterval)
                .recoverer(new RejectAndDontRequeueRecoverer()) // 소진 시 nack(requeue=false) 발생 -> DLQ 이동
                .build();
    }
}