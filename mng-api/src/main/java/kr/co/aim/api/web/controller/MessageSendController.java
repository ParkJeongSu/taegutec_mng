package kr.co.aim.api.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.format.AreYouThereBody;
import kr.co.aim.common.format.Header;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.format.request.Sample;
import kr.co.aim.common.format.response.ReplySample;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Profile("!simulator")
public class MessageSendController {

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

        System.out.println("Sending JSON Payload: " + jsonPayload);
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

        BaseMessage<AreYouThereBody> request = new BaseMessage<>();
        Header header = Header.builder().messageName("AreYouThereRequest")
                .eventComment("test")
                .eventUser("pjs")
                .version("1.0")
                .replyQueueName("abc")
                .timestamp("test")
                .transactionId("123")
                .build();
        AreYouThereBody body = AreYouThereBody.
                builder()
                        .equipmentName("equipment1")
                .build();
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

        System.out.println("Sending JSON Payload: " + jsonPayload);
        rabbitTemplate.convertAndSend(
                "demo-queue",
                jsonPayload
        );
    }
}