package kr.co.aim.api.aop;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import kr.co.aim.common.format.request.MessageHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Profile({"pex","tex"})
@Slf4j
public class RabbitMQAspect {
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry; // 프로메테우스 메트릭 등록 객체

    // 1. @RabbitListener 어노테이션이 달린 모든 메서드를 Pointcut으로 지정
    @Pointcut("@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener)")
    public void rabbitListenerPointcut() {
    }

    //     2. Pointcut으로 지정된 메서드 실행 전후에 Around Advice 적용
    @Around("rabbitListenerPointcut()")
    public Object setMdcAroundRabbitListener(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        String messageName = "UNKNOWN";
        try {
            stopWatch.start();
            Object[] args = joinPoint.getArgs();

            if (args != null && args.length > 0 && args[0] instanceof org.springframework.amqp.core.Message) {
                try {
                    // 1. Message 객체로 캐스팅
                    org.springframework.amqp.core.Message message = (org.springframework.amqp.core.Message) args[0];

                    // 2. 바디(byte[]) 추출
                    byte[] body = message.getBody();

                    // 3. ObjectMapper로 필요한 헤더 정보만 읽기
                    MessageHeader header = objectMapper.readValue(body, MessageHeader.class);

                    if (ObjectUtils.isNotEmpty( header.getTransactionId())) {
                        MDC.put("transactionId", header.getTransactionId());
                    }
                    if (ObjectUtils.isNotEmpty(header.getMessageName())) {
                        messageName = header.getMessageName();
                        MDC.put("messageName", messageName);
                    }
                } catch (Exception e) {
                    // 파싱 실패 시 로깅 (상세 에러 확인을 위해 e.getMessage() 추가 권장)
                    log.info("MDC set error: " + e.getMessage());
                }
            }

            log.info("Message [{}] business logic start",messageName);
            return joinPoint.proceed();

        } catch (Exception e) {
            log.error("Aspect logic error: " + e.getMessage());
            throw e;
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            // 프로메테우스 Timer 메트릭 기록
            Timer.builder("rabbitmq_message_process_time_seconds")
                    .description("RabbitMQ 메시지 처리 시간 및 건수")
                    .tag("message_name", messageName)
                    // 95%, 99% 사용자가 경험한 소요 시간을 별도로 추적
                    .publishPercentiles(0.95, 0.99)
                    // 지연 시간 경계선 설정 (예: 1초, 3초, 5초, 10초 이상 걸린 건수 카운팅)
                    .serviceLevelObjectives(
                            Duration.ofMillis(1000),
                            Duration.ofMillis(3000),
                            Duration.ofMillis(5000),
                            Duration.ofMillis(10000)
                    )
                    .register(meterRegistry)
                    .record(executionTime, TimeUnit.MILLISECONDS);
            // 2. 임계치(예: 3초 이상)를 초과한 건에 대해서만 둔감하지 않게 로그/MDC 남기기
            if (executionTime > 3000) { // 3초 이상 걸린 경우
                log.warn("SLOW MESSAGE DETECTED! Message: [{}], Duration: {} ms, TxId: [{}]",
                        messageName, executionTime, MDC.get("transactionId"));
            }
            log.info("Message [{}] processed in {} ms", messageName, executionTime);
            log.info("Message [{}] business logic end",messageName);
            MDC.clear();
        }
    }

    /* X{traceId} 를 이용해 mdc 패턴 사용
        <configuration>
            <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
                <encoder>
                    <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} [%X{traceId}] - %msg%n</pattern>
                </encoder>
            </appender>

            <root level="INFO">
                <appender-ref ref="CONSOLE"/>
            </root>
        </configuration>
    * */

}
