package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequiredArgsConstructor
@Slf4j
@Profile("!simulator")
public class ControlController {

    private final RabbitListenerEndpointRegistry registry;
    private final ApplicationContext applicationContext;

    @PostMapping("/stop")
    public String stop() {

        // 1. 여기서 권한 체크나 로그 기록 (보안 작업)
        log.info("Authorized shutdown request received.");


        System.out.println("애플리케이션 종료 작업 시작...");
        log.info("애플리케이션 종료 작업 시작...");
        // 1. 모든 리스너 컨테이너의 메시지 소비 중단
        System.out.println("모든 리스너 컨테이너 종료 명령...");
        log.info("모든 리스너 컨테이너 종료 명령...");
        for (MessageListenerContainer container : registry.getListenerContainers()) {
            if (container.isRunning()) {
                log.info("container stop 명령시작");
                container.stop(new StopRunnable()); // 논블로킹, 현재 메시지 처리 후 종료
            }
        }

        // 2. 현재 처리 중인 메시지들이 완료될 때까지 대기 (30초)
        System.out.println("현재 처리 중인 메시지 완료를 위해 30초 대기...");
        log.info("현재 처리 중인 메시지 완료를 위해 15초 대기시작...");
        try {
            Thread.sleep(15_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("모든 리스너 컨테이너가 종료되었습니다.");
        log.info("모든 리스너 컨테이너가 종료되었습니다.");
        // 비동기로 종료
        new Thread(() -> {
            try {
                Thread.sleep(1000); // 응답 반환 기다리기
                // 2. 스프링 컨테이너 닫기 (Bean 소멸)
                ((ConfigurableApplicationContext) applicationContext).close();
                try {
                    Thread.sleep(15_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                // 3. [핵심] JVM 강제 종료
                // 0은 정상 종료를 의미합니다. 이 코드가 없으면 좀비 프로세스가 될 수 있습니다.
                System.out.println("JVM 프로세스를 강제 종료합니다.");
                System.exit(0);

            } catch (InterruptedException ignored) {}
            ((ConfigurableApplicationContext) applicationContext).close();
        }).start();
        return "Stopped gracefully";
    }

    class StopRunnable implements Runnable{
        @Override
        public void run() {
            log.info("정상 종료 되었습니다.");
        }

    }

}
