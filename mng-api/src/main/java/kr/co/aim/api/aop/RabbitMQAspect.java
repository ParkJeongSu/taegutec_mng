package kr.co.aim.api.aop;


import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.common.format.request.MessageHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

//@Aspect
@Component
@RequiredArgsConstructor
@Profile({"pex","tex","dispatcher"})
@Slf4j
public class RabbitMQAspect {
    private final ObjectMapper objectMapper;

    // 1. @RabbitListener 어노테이션이 달린 모든 메서드를 Pointcut으로 지정
    // @Pointcut("@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener)")
    public void rabbitListenerPointcut() {
    }

    // 2. Pointcut으로 지정된 메서드 실행 전후에 Around Advice 적용
    // @Around("rabbitListenerPointcut()")
    public Object setMdcAroundRabbitListener(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 3. MDC에 식별자(transactionId) 추가. 메시지 헤더 값을 사용할 수도 있습니다.
            try {
                MessageHeader messageHeader = objectMapper.readValue(joinPoint.getArgs()[0].toString(), MessageHeader.class);
                //String transactionId = messageHeader.getHeader().getTransactionId();
                String transactionId = messageHeader.getMessageName();
                MDC.put("transactionId", transactionId);
            } catch (Exception e) {
                log.info("MDC set error");
            }
            log.info("business logic start");
            // 4. 원래의 @RabbitListener 메서드 실행
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error(e.getMessage());
            // 추가적으로 로그 더 기록
            throw e;
        }
        finally {
            log.info("business logic end");
            // 5. (가장 중요) 메서드 실행이 끝나면 반드시 MDC를 비워줍니다.
            // 이렇게 하지 않으면 스레드 풀의 다른 스레드에 값이 오염됩니다.
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
