package kr.co.aim.api.rabbitmq.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.rabbitmq.controller.dispatcher.MessageDispatcher;
import kr.co.aim.common.Utils.JsonUtils;
import kr.co.aim.common.enums.ResultCode;
import kr.co.aim.common.enums.SystemName;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.format.request.MessageHeader;
import kr.co.aim.common.handler.MessageHandler;
import kr.co.aim.common.handler.MessageWorker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"tex"})
public class TEXSyncMessageListener implements MessageWorker{

    private final MessageDispatcher messageDispatcher;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final JsonUtils jsonUtils;
    @Value("${custom.rabbitmq.retry.enabled:false}")
    private boolean retryEnabled;

    @RabbitListener(
            id = "tex-sync-Listener",
            queues= "${custom.rabbitmq.queue.tex.sync}"
    )
    public Object process(org.springframework.amqp.core.Message message) {
        String jsonString = "";
        String correlationId = message.getMessageProperties().getCorrelationId();
        String replyTo = message.getMessageProperties().getReplyTo();
        MessageHeader messageHeader = null;
        String messageName = "";
        try {
            jsonString = new String(message.getBody(), StandardCharsets.UTF_8);
            jsonUtils.writePrettyJson(jsonString);
            log.info("correlation: {}",correlationId);
            log.info("reply: {}",replyTo);
            messageHeader = objectMapper.readValue(jsonString, MessageHeader.class);
            //String messageName = messageHeader.getHeader().getMessageName();
            messageName = messageHeader.getMessageName();
            log.info("messageName : {}", messageName);
            // 2. Dispatcher를 통해 적절한 핸들러 찾기
            MessageHandler<String> handler = messageDispatcher.getHandler(messageName);

            Object replyObject = null;
            if (handler != null) {
                // 3. 핸들러에게 작업 위임
                replyObject = handler.handle(jsonString);
            } else {
                log.warn("⚠️ No handler found for messageName: {}", messageName);
                throw new IllegalArgumentException("지원하지 않는 메시지 이름입니다: " + messageName);
            }
            // 3. 정상 응답 회신 (서비스가 준 OK 혹은 비즈니스 NG 객체)
            if (replyObject != null && replyTo != null) {
                sendReply(replyTo, correlationId, replyObject);
            }
        }
        catch (Exception e) {
            // 🔥 [핵심] 미처 생각하지 못한 모든 에러(시스템 에러 등)는 일로 떨어집니다.
            // 서비스 단의 DB 작업은 이미 안전하게 Rollback된 상태입니다.
            if (retryEnabled) {
                throw new RuntimeException("Message processing failed", e);
            }
            log.error("❌ [비동기 시스템 에러] 메시지 처리 중 오류가 발생하여 작업을 롤백합니다. (비동기이므로 응답 생략) 원인: {}", e.getMessage(), e);
            if (replyTo != null) {
                // 예기치 못한 시스템 에러용 공통 NG 메시지를 생성하여 전송
                Object errorReply = buildSystemErrorReply(messageName, messageHeader, e.getMessage());
                sendReply(replyTo, correlationId, errorReply);
            }
        }
        return null;
    }

    // 응답 전송 공통 로직
    private void sendReply(String replyTo, String correlationId, Object replyObject) {
        log.info("🚀 Replying to queue: {} with correlationId: {}", replyTo, correlationId);
        jsonUtils.writePrettyJson(replyObject);

        rabbitTemplate.convertAndSend("", replyTo, replyObject, m -> {
            m.getMessageProperties().setCorrelationId(correlationId);
            return m;
        });
    }

    /**
     * 예상치 못한 시스템 예외 발생 시 공통 NG 메시지를 동적으로 생성
     */
    private BaseMessage<?> buildSystemErrorReply(String incomingMessageName, MessageHeader incomingHeader, String errorMessage) {
        // 1. 메시지명 동적 변환 (예: ZONE_REQUEST -> ZONE_REPLY)
        String replyMessageName = incomingMessageName;
        if (incomingMessageName != null && incomingMessageName.endsWith("Request")) {
            replyMessageName = incomingMessageName.replace("Request", "Reply");
        } else if (incomingMessageName != null && incomingMessageName.contains("_REQUEST")) {
            replyMessageName = incomingMessageName.replace("_REQUEST", "_REPLY");
        }

        // 2. 공통 에러 응답 객체 생성 (프로젝트의 BaseMessage 구조에 맞게 세팅)
        BaseMessage<Object> errorReply = new BaseMessage<>();

        // 원본 헤더 정보를 복사하여 매칭성 유지
        if (incomingHeader != null) {
            errorReply.setTransactionId(incomingHeader.getTransactionId());
            errorReply.setMessageOwner(incomingHeader.getMessageOwner());
        }

        errorReply.setMessageName(replyMessageName); // 동적으로 바뀐 메시지명 세팅!
        errorReply.setMessageFrom(SystemName.MNG.getValue());
        errorReply.setResultCode(ResultCode.NG.getValue());
        errorReply.setResultMessage("System Runtime Error: " + errorMessage);

        // Body는 빈 객체나 기본 에러 바디를 빌더로 넣어줍니다.
        errorReply.setBody(null);

        return errorReply;
    }
}
