package kr.co.aim.infra.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Profile({"pex","tex","scheduler","simulator"})
public class RabbitConfig {

    // --- Public Static 상수 (외부 참조용) ---
    public static String EXCHANGE_PEX;
    public static String EXCHANGE_TEX;
    public static String EXCHANGE_EAS;
    public static String EXCHANGE_WMS;
    public static String EXCHANGE_WCS;
    public static String EXCHANGE_MANTI;
    public static String EXCHANGE_DEAD;
    public static String EXCHANGE_SCHEDULER;

    public static String QUEUE_PEX;
    public static String QUEUE_TEX;
    public static String QUEUE_TEX_SYNC;
    public static String QUEUE_EAS;
    public static String QUEUE_WMS;
    public static String QUEUE_WMS_SYNC;
    public static String QUEUE_WCS;
    public static String QUEUE_MANTI;
    public static String QUEUE_DEAD;
    public static String QUEUE_SCHEDULER;

    public static String ROUTING_PEX;
    public static String ROUTING_TEX;
    public static String ROUTING_TEX_SYNC;
    public static String ROUTING_EAS;
    public static String ROUTING_WMS;
    public static String ROUTING_WMS_SYNC;
    public static String ROUTING_WCS;
    public static String ROUTING_MANTI;
    public static String ROUTING_DEAD;
    public static String ROUTING_SCHEDULER;

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
    @Value("${custom.rabbitmq.exchange.scheduler}") public void setExScheduler(String v) { EXCHANGE_SCHEDULER = v; }

    @Value("${custom.rabbitmq.queue.pex}") public void setQp(String v) { QUEUE_PEX = v; }
    @Value("${custom.rabbitmq.queue.tex}") public void setQt(String v) { QUEUE_TEX = v; }
    @Value("${custom.rabbitmq.queue.tex.sync}") public void setQTexSync(String v) { QUEUE_TEX_SYNC = v; }
    @Value("${custom.rabbitmq.queue.eas}") public void setQe(String v) { QUEUE_EAS = v; }
    @Value("${custom.rabbitmq.queue.wms}") public void setQw(String v) { QUEUE_WMS = v; }
    @Value("${custom.rabbitmq.queue.wms.sync}") public void setQWmsSync(String v) { QUEUE_WMS_SYNC = v; }
    @Value("${custom.rabbitmq.queue.wcs}") public void setQc(String v) { QUEUE_WCS = v; }
    @Value("${custom.rabbitmq.queue.manti}") public void setQm(String v) { QUEUE_MANTI = v; }
    @Value("${custom.rabbitmq.queue.dead}") public void setQd(String v) { QUEUE_DEAD = v; }
    @Value("${custom.rabbitmq.queue.scheduler}") public void setQs(String v) { QUEUE_SCHEDULER = v; }

    @Value("${custom.rabbitmq.routing.pex}") public void setRp(String v) { ROUTING_PEX = v; }
    @Value("${custom.rabbitmq.routing.tex}") public void setRt(String v) { ROUTING_TEX = v; }
    @Value("${custom.rabbitmq.routing.tex.sync}") public void setRoutingTexSync(String v) { ROUTING_TEX_SYNC = v; }
    @Value("${custom.rabbitmq.routing.eas}") public void setRe(String v) { ROUTING_EAS = v; }
    @Value("${custom.rabbitmq.routing.wms}") public void setRw(String v) { ROUTING_WMS = v; }
    @Value("${custom.rabbitmq.routing.wms.sync}") public void setRoutingWmsSync(String v) { ROUTING_WMS_SYNC = v; }
    @Value("${custom.rabbitmq.routing.wcs}") public void setRc(String v) { ROUTING_WCS = v; }
    @Value("${custom.rabbitmq.routing.manti}") public void setRm(String v) { ROUTING_MANTI = v; }
    @Value("${custom.rabbitmq.routing.dead}") public void setRd(String v) { ROUTING_DEAD = v; }
    @Value("${custom.rabbitmq.routing.scheduler}") public void setRs(String v) { ROUTING_SCHEDULER = v; }

    // --- RabbitAdmin 인프라 초기화 ---
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        try {
            admin.initialize();
            log.info(">>> [RabbitAdmin] All Infra (PEX, TEX, EAS, WMS, WCS, MANTI) initialized.");
        } catch (Exception e) {
            log.error(">>> [RabbitAdmin] Initialization failed: " + e.getMessage());
        }
        return admin;
    }

    private Map<String, Object> queueArgs() {
        Map<String, Object> args = new HashMap<>();
        args.put(DLX_KEY, EXCHANGE_DEAD);
        args.put(DLK_KEY, ROUTING_DEAD);
        args.put("x-message-ttl", 600000); // 10분 (600,000ms)
        return args;
    }

    @Bean public Queue pexQueue() { return new Queue(QUEUE_PEX, true, false, false,queueArgs()); }
    @Bean public Queue texQueue() { return new Queue(QUEUE_TEX, true, false, false,queueArgs()); }
    @Bean public Queue texQueueSync() { return new Queue(QUEUE_TEX_SYNC, true, false, false,queueArgs()); }
    @Bean public Queue deadLetterQueue() { return new Queue(QUEUE_DEAD, true); }
    @Bean public Queue schedulerQueue() { return new Queue(QUEUE_SCHEDULER, true, false, false,queueArgs()); }
    // Exchanges
    @Bean public DirectExchange pexExchange() { return new DirectExchange(EXCHANGE_PEX); }
    @Bean public DirectExchange texExchange() { return new DirectExchange(EXCHANGE_TEX); }
    @Bean public DirectExchange texSyncExchange() { return new DirectExchange(EXCHANGE_TEX); }
    @Bean public DirectExchange deadLetterExchange() { return new DirectExchange(EXCHANGE_DEAD); }
    @Bean public DirectExchange schedulerExchange() { return new DirectExchange(EXCHANGE_SCHEDULER); }

    // Bindings
    @Bean Binding pexBinding() { return BindingBuilder.bind(pexQueue()).to(pexExchange()).with(ROUTING_PEX); }
    @Bean Binding texBinding() { return BindingBuilder.bind(texQueue()).to(texExchange()).with(ROUTING_TEX); }
    @Bean Binding texSyncBinding() { return BindingBuilder.bind(texQueueSync()).to(texSyncExchange()).with(ROUTING_TEX_SYNC); }
    @Bean Binding deadLetterBinding() { return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(ROUTING_DEAD); }
    @Bean Binding schedulerBinding() { return BindingBuilder.bind(schedulerQueue()).to(schedulerExchange()).with(ROUTING_SCHEDULER); }

    // --- Template & Converter ---
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setReplyTimeout(30000);
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}