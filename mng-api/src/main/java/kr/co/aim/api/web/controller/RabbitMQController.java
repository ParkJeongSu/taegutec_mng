package kr.co.aim.api.web.controller;

//import ezieco.eziframe.middleware.event.BaseMessageFactory;
//import ezieco.eziframe.middleware.vendor.rabbitmq.sender.RabbitMQDefaultSender;
//import ezieco.eziframe.middleware.vendor.rabbitmq.sender.RabbitMQReplyingSender;
//import kr.co.aim.common.format.LoadCompletedBody;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@ConditionalOnProperty(prefix = "middleware.mng.enable", name = "rabbit", matchIfMissing = true)
//@RequestMapping("/api/rabbit")
//@RequiredArgsConstructor
//@Slf4j
//public class RabbitMQController {
//
//    private final RabbitMQDefaultSender rabbitMQDefaultSender;
//    private final RabbitMQReplyingSender rabbitMQReplyingSender;
//
//    @Value("${middleware.mng.rabbit-mq.routing-key}")
//    private String routingKey;
//
//    @PostMapping("/load-complete")
//    public ResponseEntity<String> createPorts(@RequestBody LoadCompletedBody requestDto) {
//    	rabbitMQDefaultSender.send(BaseMessageFactory.create("loadComplete", requestDto), routingKey);
//        return ResponseEntity.ok("ok");
//    }
//
//}
