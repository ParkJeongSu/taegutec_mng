package kr.co.aim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

//Page 관련 경고 에러
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
//Page 관련 경고 에러
//@SpringBootApplication
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"eziframe", "ezi.lib","kr.co.aim"})
@ConfigurationPropertiesScan("eziframe")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
