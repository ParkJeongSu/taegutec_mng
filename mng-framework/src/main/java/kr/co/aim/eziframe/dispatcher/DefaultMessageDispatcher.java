package kr.co.aim.eziframe.dispatcher;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ezieco.eziframe.middleware.event.data.Header;
import org.opensearch.client.opensearch.OpenSearchClient; // 추가
import org.opensearch.client.opensearch._types.Result;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ezieco.eziframe.middleware.event.MessageAckProcessor;
import ezieco.eziframe.middleware.event.MessageDispatcher;
import ezieco.eziframe.middleware.event.MessageListener;
import ezieco.eziframe.middleware.event.data.BaseMessage;
import ezieco.eziframe.middleware.event.utils.MethodCallHandler;
import kr.co.aim.common.handler.MessageWorker;
import kr.co.aim.eziframe.converter.DefaultMessageConverter;
import kr.co.aim.eziframe.eziflow.EziFlowManager;
import kr.co.aim.eziframe.handler.MessageRouter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DefaultMessageDispatcher implements MessageDispatcher {

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20, 40, 60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100),
            new CustomizableThreadFactory("MyRabbitMQ-Dispatcher-")
    );

    // 1. ThreadPoolExecutor 직접 생성 (작성하신 방식 + 거절 정책)
    private final ThreadPoolExecutor logExecutor = new ThreadPoolExecutor(
            5,                                     // Core Pool Size (평소 유지)
            10,                                     // Max Pool Size (최대 확장)
            60L, TimeUnit.SECONDS,                  // Keep Alive (유휴 스레드 생존 시간)
            new ArrayBlockingQueue<>(100),          // Queue Size (대기열 제한 -> OOM 방지)
            new CustomizableThreadFactory("OpenSearch-Log-Worker-"), // 스레드 이름 편리하게 지정
            new ThreadPoolExecutor.DiscardPolicy() // [중요] 큐가 꽉 찼을 때 처리 정책
    );

    private final ApplicationContext applicationContext;
    private final MethodCallHandler methodCallHandler;
    private final EziFlowManager eziflowManager;
    private final ObjectMapper objectMapper;
    private final MessageRouter messageRouter;
    private final MessageWorker messageWorker;
    private final OpenSearchClient openSearchClient;

    public DefaultMessageDispatcher(
            MethodCallHandler methodCallHandler,
            ApplicationContext applicationContext,
            EziFlowManager eziflowManager,
            ObjectMapper objectMapper,
            MessageRouter messageRouter,
            MessageWorker messageWorker,
            OpenSearchClient openSearchClient
    ) {
        this.methodCallHandler = methodCallHandler;
        this.applicationContext = applicationContext;
        this.eziflowManager = eziflowManager;
        this.objectMapper = objectMapper;
        this.messageRouter = messageRouter;
        this.messageWorker = messageWorker;
        this.openSearchClient = openSearchClient;
    }

    /**
     * <p>메시지 수신 후 해당 함수가 실행된다.</p>
     * <p>일반적으로 BPEL worker 에 task를 전달한다. </p>
     *
     * @param messageListener 메시지를 수신한 listener
     * @param baseMessage     MessageConverter를 통해 변환된 메시지
     * @see DefaultMessageConverter 메시지 변환 로직
     */
    @Override
    public void dispatch(MessageListener messageListener, BaseMessage<?> baseMessage) {
        log.info("listener={}, baseMessage={}", messageListener, baseMessage);
        try {

            String messageData = baseMessage.getBody().toString();
            Header headerData = baseMessage.getHeader();

            log.info("message : {}", messageData);
            log.info("header : {}", headerData);

            String rawJson = baseMessage.getBody().toString();
            byte[] bodyBytes = rawJson.getBytes(StandardCharsets.UTF_8);

            // 2. MessageProperties 설정 (헤더 정보 복사)
            MessageProperties properties = new MessageProperties();
            Header header = baseMessage.getHeader();

            TypeReference<kr.co.aim.common.format.request.BaseMessage<?>> typeRef = new TypeReference<>() {
            };
            kr.co.aim.common.format.request.BaseMessage<?> requestMessage = objectMapper.readValue(messageData, typeRef);
            String messageName = requestMessage.getMessageName();

            if (header != null) {
                // CorrelationId 설정
                properties.setCorrelationId(header.getCorrelationId());

                // others 맵에 들어있는 replyTo 추출 및 설정
                if (header.getOthers() != null && header.getOthers().containsKey("replyTo")) {
                    properties.setReplyTo(header.getOthers().get("replyTo").toString());
                }
                // 기타 헤더 정보들을 MessageProperties 헤더에 복사
                /*
                properties.setHeader("messageName", header.getMessageName());
                properties.setHeader("transactionId", header.getTransactionId());
                */
            }

            // 3. Spring AMQP Message 객체 최종 생성
            org.springframework.amqp.core.Message springMessage = new org.springframework.amqp.core.Message(bodyBytes, properties);
            /*
            org.springframework.amqp.core.Message springMessage = MessageBuilder
                    .withBody(bodyBytes) // 바디 넣기
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON) // "이거 JSON이야"라고 명찰 붙이기
                    .setCorrelationId(header.getCorrelationId())
                    .setReplyTo(header.getOthers().get("replyTo").toString())
                    .setContentEncoding("UTF-8")
                    .build();
             */
            //OpenSearch 로 메시지 보내기
            //logExecutor.submit(()->{ this.saveLogToOpenSearch(requestMessage); });

            //methodCallHandler.invoke(baseMessage);
            log.info("TransactionId : {} before Start", requestMessage.getTransactionId());
            if (messageRouter.isBpel(messageName)) {
                log.info("TransactionId : {} before BPEL Start", requestMessage.getTransactionId());
                //eziflowManager.execute(messageName, message);
                eziflowManager.executeBpel(messageName, springMessage, requestMessage.getTransactionId());
            } else {
                log.info("TransactionId : {} before Message Start", requestMessage.getTransactionId());
                threadPoolExecutor.submit(() -> {
                    try {
                        MDC.put("transactionId", requestMessage.getTransactionId());
                        log.info("business logic start");
                        messageWorker.process(springMessage);
                    } catch (Exception e) {
                        // TODO: handle exception
                    } finally {
                        log.info("business logic end");
                        MDC.remove("transactionId");
                    }

                });

            }
            log.info("TransactionId : {} End", requestMessage.getTransactionId());
        } catch (Exception e) {
            throw new RuntimeException("can not execute function.");
        } finally {

        }

    }

    /**
     * <p>로직 수행 후 해당 middleware에게 메시지 수신을 알린다.</p>
     *
     * @param messageListener
     * @param baseMessage
     * @param messageAckProcessor
     */
    @Override
    public void dispatch(MessageListener messageListener, BaseMessage<?> baseMessage, MessageAckProcessor messageAckProcessor) {
        threadPoolExecutor.submit(() -> {
            MDC.put("MSGNAME", baseMessage.getHeader().getMessageName());
            MDC.put("TRXID", baseMessage.getHeader().getTransactionId());
            try {
                methodCallHandler.invoke(baseMessage);
            } catch (Exception e) {
                throw new RuntimeException("can not execute function.");
            } finally {
                messageAckProcessor.ack();
                MDC.remove("MSGNAME");
                MDC.remove("TRXID");
            }
        });
    }

    /**
     * <p>kafka 에서만 동작합니다. 메시지를 poll 했을 시 여러 record 가 들어올 경우 한꺼번에 처리 가능하도록 구현 가능</p>
     *
     * @param messageListener
     * @param eventData
     * @see eziframe.middleware.vendor.kafka.listener.KafkaBatchListener
     */
    @Override
    public void batchDispatch(MessageListener messageListener, List<BaseMessage<?>> eventData) {
        log.info("listener={}, count={}", messageListener.getName(), eventData.size());
    }


    /**
     * application 종료시 호출됩니다.
     */
    @Override
    public void gracefulShutdown() {
        log.info("start clean up.");
        threadPoolExecutor.shutdown();
        logExecutor.shutdown();
        try {
            if (!threadPoolExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                log.error("Pool did not terminate.");
                threadPoolExecutor.shutdownNow();
            }
        } catch (InterruptedException ie) {
            threadPoolExecutor.shutdownNow();
        }
        log.info("shutdown complete");
    }


    // 로그 저장 로직을 별도 메서드로 분리 (가독성 향상)
    private void saveLogToOpenSearch(kr.co.aim.common.format.request.BaseMessage<?> requestMessage) {
        try {
            // 1. 날짜 처리 로직 (기존 유지)
            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            ZonedDateTime nowKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

            if (requestMessage.getEventTime() == null || requestMessage.getEventTime().isEmpty()) {
                requestMessage.setEventTime(nowKST.format(formatter));
            }

            // 2. 인덱스 이름 생성 (기존 유지)
            String dynamicIndexName = "transaction_log-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM"));

            // 3 IndexRequest 생성 (Builder 사용, 람다 X)
            // 제네릭 <?> 타입을 명시해야 합니다. 여기서는 BaseMessage<?> 객체를 넣을 것입니다.
            IndexRequest.Builder<kr.co.aim.common.format.request.BaseMessage<?>> builder = new IndexRequest.Builder<>();

            builder.index(dynamicIndexName);      // 인덱스명 설정
            builder.document(requestMessage);     // 객체 자체를 주입 (알아서 JSON 됨)

            // 아이디를 지정하고 싶다면: builder.id("my-id-123"); 
            // 지정 안 하면 OpenSearch가 자동 생성

            // 4. 요청 전송 및 응답 수신
            // build()를 호출하여 Request 객체 완성
            IndexResponse response = openSearchClient.index(builder.build());

            // 5. 응답 상태 확인
            // status() 대신 result()를 사용하여 결과를 확인합니다.
            Result result = response.result();

            if (result == Result.Created || result == Result.Updated) {
                log.info("Success! Doc ID: {}", response.id());
            } else {
                // 실패라기보다 다른 상태일 수 있음 (NoOp 등)
                log.warn("Document indexed with status: {}", result);
            }

        } catch (Exception e) {
            log.error("OpenSearch Save Error", e);
        }
    }
}
