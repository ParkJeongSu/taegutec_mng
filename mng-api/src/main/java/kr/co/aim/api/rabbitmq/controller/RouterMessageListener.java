package kr.co.aim.api.rabbitmq.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.service.RouterService;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.format.request.MessageHeader;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"pex","tex","dispatcher"})
public class RouterMessageListener {

    private final RouterService routerService;
    private final ObjectMapper objectMapper;
    private final Set<String> pexMessageNames =
            Set.of(
                    MessageList.ALARM_REPORT.getMessageName(),
                    "cd",
                    "ef"
            );
    private final Set<String> texMessageNames =
            Set.of(
                    "ab",
                    "cd",
                    "ef"
            );

//    @RabbitListener(id = "dispatcher-Listener",
//            queues= RabbitConfig.DISPATCHER_REQUEST_QUEUE_NAME,
//            concurrency = "10",
//            containerFactory = "rabbitListenerContainerFactory"
//    )
    @SneakyThrows
    public void recevieDispatcher(org.springframework.amqp.core.Message message) {

        String jsonString = new String(message.getBody(), StandardCharsets.UTF_8);
        
        log.info("Received raw message: {}", jsonString);
        
        // 1. MessageName 추출
        MessageHeader messageHeader = objectMapper.readValue(jsonString, MessageHeader.class);
        //String messageName = messageHeader.getHeader().getMessageName();
        String messageName = messageHeader.getMessageName();
        log.info("messageName : {}", messageName);

//        if(true)
//        {
//            throw new sampleError();
//        }

        routerService.routePEXMessage(jsonString);
        /*
        if(pexMessageNames.contains(messageName)) {
            routerService.routePEXMessage(message);
            log.info("pex route success");
        }
        if(texMessageNames.contains(messageName)){
            routerService.routeTEXMessage(message);
            log.info("tex route success");
        }
        */
        log.info("route finished");
    }
}
