package kr.co.aim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

//Page 관련 경고 에러
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
//Page 관련 경고 에러
@EnableScheduling
//@SpringBootApplication
//@SpringBootApplication(scanBasePackages = {"eziframe", "ezi.lib","kr.co.aim"})
//@ConfigurationPropertiesScan("eziframe")
// Simulator 일때
@SpringBootApplication(
		scanBasePackages = "kr.co.aim",
		exclude = {
				// 1. 스프링 부트 기본 RabbitMQ 자동 설정 차단
				org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.class,
				// 2. 만약 ezieco 라이브러리 내부에 별도 AutoConfig 클래스가 있다면 그것도 추가
				// 예: ezieco.eziframe.middleware.config.RabbitMQAutoConfiguration.class
		}
)
// Simulator 일때
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
