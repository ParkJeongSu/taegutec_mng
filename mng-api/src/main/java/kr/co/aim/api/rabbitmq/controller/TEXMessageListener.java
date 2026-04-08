package kr.co.aim.api.rabbitmq.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.rabbitmq.controller.dispatcher.MessageDispatcher;
import kr.co.aim.common.format.request.MessageHeader;
import kr.co.aim.common.handler.MessageHandler;
import kr.co.aim.common.handler.MessageWorker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"tex"})
public class TEXMessageListener implements MessageWorker{

    private final MessageDispatcher messageDispatcher;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(
            id = "tex-Listener",
            queues= "${custom.rabbitmq.queue.tex}",
            concurrency = "10",
            containerFactory = "rabbitListenerContainerFactory"
    )
    @SneakyThrows
    public Object process(org.springframework.amqp.core.Message message) {
        // 1. 바디를 꺼내서 직접 String으로 변환
        String jsonString = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("Received raw message: {}", jsonString);

        String correlation = message.getMessageProperties().getCorrelationId();
        String reply = message.getMessageProperties().getReplyTo();

        log.info("correlation: {}",correlation);
        log.info("reply: {}",reply);

        // 1. JSON 트리를 읽어 헤더 부분만 추출
        // 아예 header 부분에 있는 메시지로 로직처리
        /*
        JsonNode rootNode = objectMapper.readTree(jsonString);
        JsonNode headerNode = rootNode.get("header"); // "header" 필드만 접근

        if (headerNode == null) {
            log.error("❌ Message header is missing!");
            return null;
        }
        // 1. MessageName 추출
        Header messageHeader = objectMapper.treeToValue(headerNode, Header.class);
        */

        MessageHeader messageHeader = objectMapper.readValue(jsonString, MessageHeader.class);
        //String messageName = messageHeader.getHeader().getMessageName();
        String messageName = messageHeader.getMessageName();
        log.info("messageName : {}", messageName);
        // 2. Dispatcher를 통해 적절한 핸들러 찾기
        MessageHandler<String> handler = messageDispatcher.getHandler(messageName);

        Object replyObject = null;
        if (handler != null) {
            // 3. 핸들러에게 작업 위임
            replyObject = handler.handle(jsonString);
        } else {
            log.warn("⚠️ No handler found for messageName: {}", messageName);
        }
        if (replyObject != null) {
            // 1. 응답 시 요청의 correlationId를 그대로 유지해야 함
            String correlationId = message.getMessageProperties().getCorrelationId();
            String replyTo = message.getMessageProperties().getReplyTo();

            if (replyTo != null) {
                log.info("🚀 Replying to queue: {} with correlationId: {}", replyTo, correlationId);

                // 2. replyTo 주소를 Routing Key로 사용 (Exchange는 기본 익스체인지 "" 사용)
                rabbitTemplate.convertAndSend("", replyTo, replyObject, m -> {
                    m.getMessageProperties().setCorrelationId(correlationId);
                    return m;
                });
            }
        }
        //return replyObject;
        return null;
    }
}
