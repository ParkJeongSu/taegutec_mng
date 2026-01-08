package kr.co.aim.api.rabbitmq.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.rabbitmq.controller.dispatcher.MessageDispatcher;
import kr.co.aim.common.format.request.MessageHeader;
import kr.co.aim.common.handler.MessageHandler;
import kr.co.aim.common.handler.MessageWorker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
//@Component
@RequiredArgsConstructor
//@Profile({"pex"})
public class PEXMessageListener_backup implements MessageWorker{

    private final MessageDispatcher messageDispatcher;
    private final ObjectMapper objectMapper;

//    @RabbitListener(
//            id = "pex-Listener",
//            queues= RabbitConfig.PEX_REQUEST_QUEUE_NAME,
//            concurrency = "10",
//            containerFactory = "rabbitListenerContainerFactory"
//    )
    @SneakyThrows
    public Object process(org.springframework.amqp.core.Message message) {
    	// 1. 바디를 꺼내서 직접 String으로 변환
        String jsonString = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("Received raw message: {}", jsonString);

        String correlation = message.getMessageProperties().getCorrelationId();
        String reply = message.getMessageProperties().getReplyTo();

        log.info("correlation: {}",correlation);
        log.info("reply: {}",reply);


        // 1. MessageName 추출
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
        return replyObject;
    }
}
