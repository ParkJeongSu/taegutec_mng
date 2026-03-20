package kr.co.aim.infra.config;

import lombok.Getter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Getter
@Profile({"pex","tex","scheduler"})
public class RabbitConfig {

    // --- Public Static 상수 (외부 참조용) ---
    public static String EXCHANGE_PEX;    // rpc.exchange
    public static String EXCHANGE_TEX;    // rpc.exchange
    public static String EXCHANGE_EAS;
    public static String EXCHANGE_WMS;
    public static String EXCHANGE_WCS;
    public static String EXCHANGE_MANTI;
    public static String EXCHANGE_DEAD;

    public static String QUEUE_PEX;
    public static String QUEUE_TEX;
    public static String QUEUE_EAS;
    public static String QUEUE_WMS;
    public static String QUEUE_WCS;
    public static String QUEUE_MANTI;
    public static String QUEUE_DEAD;

    public static String ROUTING_PEX;
    public static String ROUTING_TEX;
    public static String ROUTING_EAS;
    public static String ROUTING_WMS;
    public static String ROUTING_WCS;
    public static String ROUTING_MANTI;
    public static String ROUTING_DEAD;

    public static final String DLX_KEY = "x-dead-letter-exchange";
    public static final String DLK_KEY = "x-dead-letter-routing-key";

    // --- Setter 주입 (Static 필드 할당) ---
    @Value("${custom.rabbitmq.exchange.pex}") public void setExPex(String v) { EXCHANGE_PEX = v; }
    @Value("${custom.rabbitmq.exchange.tex}") public void setExTex(String v) { EXCHANGE_TEX = v; }
    @Value("${custom.rabbitmq.exchange.eas}") public void setExEas(String v) { EXCHANGE_EAS = v; }
    @Value("${custom.rabbitmq.exchange.wms}") public void setExWms(String v) { EXCHANGE_WMS = v; }
    @Value("${custom.rabbitmq.exchange.wcs}") public void setExWcs(String v) { EXCHANGE_WCS = v; }
    @Value("${custom.rabbitmq.exchange.manti}") public void setExManti(String v) { EXCHANGE_MANTI = v; }
    @Value("${custom.rabbitmq.exchange.dead}") public void setExDead(String v) { EXCHANGE_DEAD = v; }

    @Value("${custom.rabbitmq.queue.pex}") public void setQp(String v) { QUEUE_PEX = v; }
    @Value("${custom.rabbitmq.queue.tex}") public void setQt(String v) { QUEUE_TEX = v; }
    @Value("${custom.rabbitmq.queue.eas}") public void setQe(String v) { QUEUE_EAS = v; }
    @Value("${custom.rabbitmq.queue.wms}") public void setQw(String v) { QUEUE_WMS = v; }
    @Value("${custom.rabbitmq.queue.wcs}") public void setQc(String v) { QUEUE_WCS = v; }
    @Value("${custom.rabbitmq.queue.manti}") public void setQm(String v) { QUEUE_MANTI = v; }
    @Value("${custom.rabbitmq.queue.dead}") public void setQd(String v) { QUEUE_DEAD = v; }

    @Value("${custom.rabbitmq.routing.pex}") public void setRp(String v) { ROUTING_PEX = v; }
    @Value("${custom.rabbitmq.routing.tex}") public void setRt(String v) { ROUTING_TEX = v; }
    @Value("${custom.rabbitmq.routing.eas}") public void setRe(String v) { ROUTING_EAS = v; }
    @Value("${custom.rabbitmq.routing.wms}") public void setRw(String v) { ROUTING_WMS = v; }
    @Value("${custom.rabbitmq.routing.wcs}") public void setRc(String v) { ROUTING_WCS = v; }
    @Value("${custom.rabbitmq.routing.manti}") public void setRm(String v) { ROUTING_MANTI = v; }
    @Value("${custom.rabbitmq.routing.dead}") public void setRd(String v) { ROUTING_DEAD = v; }

    // --- RabbitAdmin 인프라 초기화 ---
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        try {
            admin.initialize();
            System.out.println(">>> [RabbitAdmin] All Infra (PEX, TEX, EAS, WMS, WCS, MANTI) initialized.");
        } catch (Exception e) {
            System.err.println(">>> [RabbitAdmin] Initialization failed: " + e.getMessage());
        }
        return admin;
    }

    // --- Queue & Exchange Beans ---
    @Bean public Queue deadLetterQueue() { return new Queue(QUEUE_DEAD, true); }
    @Bean public DirectExchange deadLetterExchange() { return new DirectExchange(EXCHANGE_DEAD); }
    @Bean Binding deadLetterBinding() { return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(ROUTING_DEAD); }

    // 공통 Argument 생성 메서드 (람다 대신 사용)
    private Map<String, Object> dlqArgs() {
        Map<String, Object> args = new HashMap<>();
        args.put(DLX_KEY, EXCHANGE_DEAD);
        args.put(DLK_KEY, ROUTING_DEAD);
        return args;
    }

    @Bean public Queue pexQueue() { return new Queue(QUEUE_PEX, true, false, false, dlqArgs()); }
    @Bean public Queue texQueue() { return new Queue(QUEUE_TEX, true, false, false, dlqArgs()); }
    //@Bean public Queue easQueue() { return new Queue(QUEUE_EAS, true, false, false, dlqArgs()); }
    //@Bean public Queue wmsQueue() { return new Queue(QUEUE_WMS, true, false, false, dlqArgs()); }
    //@Bean public Queue wcsQueue() { return new Queue(QUEUE_WCS, true, false, false, dlqArgs()); }
    //@Bean public Queue mantiQueue() { return new Queue(QUEUE_MANTI, true, false, false, dlqArgs()); }

    // Exchanges
    @Bean public DirectExchange pexExchange() { return new DirectExchange(EXCHANGE_PEX); } // rpc.exchange
    @Bean public DirectExchange texExchange() { return new DirectExchange(EXCHANGE_TEX); } // rpc.exchange
    //@Bean public DirectExchange easExchange() { return new DirectExchange(EXCHANGE_EAS); }
    //@Bean public DirectExchange wmsExchange() { return new DirectExchange(EXCHANGE_WMS); }
    //@Bean public DirectExchange wcsExchange() { return new DirectExchange(EXCHANGE_WCS); }
    //@Bean public DirectExchange mantiExchange() { return new DirectExchange(EXCHANGE_MANTI); }

    // Bindings
    @Bean Binding pexBinding() { return BindingBuilder.bind(pexQueue()).to(pexExchange()).with(ROUTING_PEX); }
    @Bean Binding texBinding() { return BindingBuilder.bind(texQueue()).to(texExchange()).with(ROUTING_TEX); }
    //@Bean Binding easBinding() { return BindingBuilder.bind(easQueue()).to(easExchange()).with(ROUTING_EAS); }
    //@Bean Binding wmsBinding() { return BindingBuilder.bind(wmsQueue()).to(wmsExchange()).with(ROUTING_WMS); }
    //@Bean Binding wcsBinding() { return BindingBuilder.bind(wcsQueue()).to(wcsExchange()).with(ROUTING_WCS); }
    //@Bean Binding mantiBinding() { return BindingBuilder.bind(mantiQueue()).to(mantiExchange()).with(ROUTING_MANTI); }

    // --- Template & Converter ---
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setReplyTimeout(60000);
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}