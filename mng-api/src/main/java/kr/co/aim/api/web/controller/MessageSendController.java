package kr.co.aim.api.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.format.AreYouThereRequestBody;
import kr.co.aim.common.format.Header;
import kr.co.aim.common.format.OrderCreateRequestBody;
import kr.co.aim.common.format.ZoneRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.format.request.Sample;
import kr.co.aim.common.format.response.ReplySample;
import kr.co.aim.infra.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@Hidden
@RestController
@RequiredArgsConstructor
@Slf4j
@Profile("!simulator")
public class MessageSendController {
    // Test 용 controller

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper; // ObjectMapper 선언

    @PostMapping("/send")
    public String sendMessage(@RequestParam String message) {
        rabbitTemplate.convertAndSend("demo-queue", message);
        return "Sent: " + message;
    }

    @PostMapping("/send2")
    public String sendMessage2(@RequestParam String message){
        // 응답이 null이거나 타입이 맞지 않는 경우 처리
        Object response = rabbitTemplate.convertSendAndReceive(
                "demo-queue",
                message
        );
        if (response == null) {
            log.warn("🚀 [Client] Received null response.");
            return "Error: No response from server.";
        }

        log.info("🚀 [Client] Received response: {}", response);
        return response.toString();
    }

    @SneakyThrows
    @PostMapping("/send3")
    public ReplySample sendMessage3(@RequestParam String message){

        Sample request = Sample.builder().messageName(message).messageContent("messagecontent").build();

        // 3. DTO 객체를 JSON 문자열로 직접 변환합니다.
        String jsonPayload = objectMapper.writeValueAsString(request);

        log.info("Sending JSON Payload: {}", jsonPayload);
        Object response = rabbitTemplate.convertSendAndReceive(
                "demo-queue",
                jsonPayload
        );


        if (response == null) {
            log.warn("🚀 [Client] Received null response.");
            return ReplySample.builder().messageName("message1").messageContent("error").build();
        }

        log.info("🚀 [Client] Received response: {}", response);

        // --- 핵심 변환 로직 ---
        try {
            // ObjectMapper를 사용해 LinkedHashMap을 ReplySample 객체로 변환
            ReplySample replySample = objectMapper.convertValue(response, ReplySample.class);
            log.info("🚀 [Client] Converted to ReplySample: {}", replySample);
            return replySample;
        } catch (IllegalArgumentException e) {
            log.error("🚀 [Client] Failed to convert LinkedHashMap to ReplySample", e);
            return ReplySample.builder().messageName("message1").messageContent("conversion_error").build();
        }

        //return (ReplySample)response;
    }

    @SneakyThrows
    @PostMapping("/send4")
    public void sendMessage4(@RequestParam String message){

        BaseMessage<AreYouThereRequestBody> request = new BaseMessage<>();
        Header header = Header.builder().messageName("AreYouThereRequest")
                .eventComment("test")
                .eventUser("pjs")
                .version("1.0")
                .replyQueueName("abc")
                .timestamp("test")
                .transactionId("123")
                .build();
        AreYouThereRequestBody body = new AreYouThereRequestBody();

        request.setEventTime(request.getEventTime());
        request.setMessageFrom("MNG");
        request.setMessageName(MessageList.ARE_YOU_THERE_REQUEST.getMessageName());
        request.setMessageOwner("MNG");
        request.setMessageTo(request.getMessageFrom());
        request.setResultCode("0");
        request.setResultMessage("0");
        request.setTransactionId(request.getTransactionId());
        request.setBody(body);

        // 3. DTO 객체를 JSON 문자열로 직접 변환합니다.
        String jsonPayload = objectMapper.writeValueAsString(request);
        log.info("Sending JSON Payload: {}" , jsonPayload);
        rabbitTemplate.convertAndSend(
                "demo-queue",
                jsonPayload
        );
    }

    @SneakyThrows
    @PostMapping("/order-create-reqeust-to-wms")
    public void sendOrderCreateRequestToWMS(){

        BaseMessage<OrderCreateRequestBody> request = new BaseMessage<>();
        OrderCreateRequestBody body = new OrderCreateRequestBody();

        request.setEventTime(request.getEventTime());
        request.setMessageFrom("MNG");
        request.setMessageName(MessageList.ORDER_CREATE_REQUEST.getMessageName());
        request.setMessageOwner("MNG");
        request.setMessageTo(request.getMessageFrom());
        request.setResultCode("0");
        request.setResultMessage("0");
        request.setTransactionId(request.getTransactionId());
        request.setBody(body);

        // 3. DTO 객체를 JSON 문자열로 직접 변환합니다.
        String jsonPayload = objectMapper.writeValueAsString(request);
        log.info("Sending JSON Payload: {}" , jsonPayload);

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_WMS,
                RabbitConfig.ROUTING_WMS,
                request );
    }

    @SneakyThrows
    @PostMapping("/zone-reqeust-to-wms")
    public void sendZoneRequestToWMS(){

        BaseMessage<ZoneRequestBody> request = new BaseMessage<>();
        ZoneRequestBody body = new ZoneRequestBody();

        request.setEventTime(request.getEventTime());
        request.setMessageFrom("MNG");
        request.setMessageName(MessageList.ZONE_REQUEST.getMessageName());
        request.setMessageOwner("MNG");
        request.setMessageTo(request.getMessageFrom());
        request.setResultCode("0");
        request.setResultMessage("0");
        request.setTransactionId(request.getTransactionId());
        request.setBody(body);

        // 3. DTO 객체를 JSON 문자열로 직접 변환합니다.
        String jsonPayload = objectMapper.writeValueAsString(request);
        log.info("Sending JSON Payload: {}" , jsonPayload);

        Object reply = rabbitTemplate.convertSendAndReceive(
                RabbitConfig.EXCHANGE_WMS,
                RabbitConfig.ROUTING_WMS_SYNC,
                request );

        // 2. 응답결과 null 체크 및 로그 출력
        if (reply == null) {
            // 실제 어떤 클래스 타입으로 변환되어 들어왔는지 콘솔에서 확인 가능합니다.
            log.info("✅ 반환된 객체의 실제 타입: {}", reply.getClass().getName());
            log.info("✅ 반환된 객체의 내용: {}", reply.toString());
            return;
        }else {
            log.error("❌ 응답 타임아웃");
        }

        log.info("✅ [Simulator] Reply Received. Class Type: {}", reply.getClass().getName());

        // 3. 타입에 따른 안전한 로그 처리
        if (reply instanceof org.springframework.amqp.core.Message) {
            // 만약 컨버터가 작동하지 않고 순수 Message 객체로 왔을 경우
            org.springframework.amqp.core.Message msg = (org.springframework.amqp.core.Message) reply;
            String replyBody = new String(msg.getBody(), StandardCharsets.UTF_8);
            log.info("📝 [Message Type Reply] Body: {}", replyBody);
        } else if (reply instanceof String) {
            // 문자열로 바로 반환되었을 경우
            log.info("📝 [String Type Reply] Body: {}", reply);
        } else {
            // Jackson 컨버터에 의해 Map 또는 DTO 객체로 자동 파싱되었을 경우
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reply);
            log.info("📝 [Object Type Reply] Pretty JSON:\n{}", prettyJson);
        }
    }
}