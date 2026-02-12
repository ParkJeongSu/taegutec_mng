package kr.co.aim.infra.config;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;

@Configuration
@Getter
@Profile({"pex","tex","dispatcher","scheduler"})
public class RabbitConfig {

    // 1. application.yml 에서 값을 가져와 변수에 할당합니다.
    // static final 이 아니라 인스턴스 변수가 됩니다.

    @Value("${custom.rabbitmq.exchange.rpc}")
    private String rpcExchangeName;

    @Value("${custom.rabbitmq.exchange.dead}")
    private String deadLetterExchangeName;

    @Value("${custom.rabbitmq.queue.pex}")
    private String pexRequestQueueName;

    @Value("${custom.rabbitmq.queue.tex}")
    private String texRequestQueueName;

    //@Value("${custom.rabbitmq.queue.dispatcher}")
    //private String dispatcherRequestQueueName;

    @Value("${custom.rabbitmq.queue.dead}")
    private String deadLetterQueueName;

    @Value("${custom.rabbitmq.routing.pex}")
    private String pexRoutingKey;

    @Value("${custom.rabbitmq.routing.tex}")
    private String texRoutingKey;

    @Value("${custom.rabbitmq.routing.dispatcher}")
    private String dispatcherRoutingKey;

    public static final String DEAD_LETTER_EXCHANGE_KEY = "x-dead-letter-exchange";
    public static final String DEAD_LETTER_ROUTING_KEY_KEY = "x-dead-letter-routing-key";

    @Bean
    @Profile({"pex","tex","dispatcher","scheduler"})
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory
    ,Queue pexQueue, Queue texQueue, Queue deadLetterQueue) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);

        // [중요] eziframe이 날뛰기 전에 여기서 강제로 인프라를 생성합니다.
        // 이 코드는 @Bean으로 등록된 Queue, Exchange, Binding들을 즉시 브로커에 선언합니다.
        try {
            admin.initialize();
            System.out.println(">>> [RabbitAdmin] Infra initialized successfully.");
        } catch (Exception e) {
            System.err.println(">>> [RabbitAdmin] Failed to initialize infra: " + e.getMessage());
        }

        return admin;
    }

    // 1. DLQ와 DLX 빈 등록
    @Bean
    public Queue deadLetterQueue() {
        Queue queue = new Queue(getDeadLetterQueueName(), true);
        queue.setShouldDeclare(true);
        return queue;
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(getDeadLetterExchangeName());
    }

    // 2. DLQ 바인딩
    @Bean
    Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(getDeadLetterQueueName());
    }

    @Bean
    @Profile({"pex","tex","dispatcher"})
    public Queue pexQueue(){
        Queue queue = new Queue( getPexRequestQueueName(),true,false,false,
                Map.of( DEAD_LETTER_EXCHANGE_KEY, getDeadLetterExchangeName(),
                        DEAD_LETTER_ROUTING_KEY_KEY,getDeadLetterQueueName())
        );
        queue.setShouldDeclare(true);
        return queue;
    }


    @Bean
    @Profile({"pex","tex","dispatcher"})
    public Queue texQueue(){
        Queue queue = new Queue(getTexRequestQueueName(),true,false,false,
                Map.of(DEAD_LETTER_EXCHANGE_KEY,getDeadLetterExchangeName(),
                        DEAD_LETTER_ROUTING_KEY_KEY,getDeadLetterQueueName())
        );
        queue.setShouldDeclare(true);
        return queue;
    }

    @Bean
    Binding pexBinding(Queue pexQueue, DirectExchange exchange) {
        return BindingBuilder.bind(pexQueue).to(exchange).with(getPexRoutingKey());
    }

    @Bean
    Binding texBinding(Queue texQueue, DirectExchange exchange) {
        return BindingBuilder.bind(texQueue).to(exchange).with(getTexRoutingKey());
    }

//    @Bean
//    @Profile({"pex","tex","dispatcher"})
//    public Queue dispatcherQueue(){
//        return new Queue(getDispatcherRequestQueueName(),true,false,false,
//                Map.of(DEAD_LETTER_EXCHANGE_KEY, getDeadLetterExchangeName(),
//                        DEAD_LETTER_ROUTING_KEY_KEY,getDeadLetterQueueName())
//        );
//    }

    // Exchange 빈 등록
    @Bean
    @Profile({"pex","tex","dispatcher"})
    DirectExchange exchange() {
        return new DirectExchange(getRpcExchangeName());
    }

    // RabbitTemplate 설정
    @Bean
    @Profile({"pex","tex","dispatcher"})
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setReplyTimeout(60000);
        return rabbitTemplate;
    }

    // Spring Boot가 Jackson 라이브러리를 사용해 메시지를 JSON으로 자동 변환하도록 설정
    @Bean
    @Profile({"pex","tex","dispatcher"})
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
