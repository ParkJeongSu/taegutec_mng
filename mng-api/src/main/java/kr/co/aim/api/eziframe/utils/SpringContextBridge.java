package kr.co.aim.api.eziframe.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

//@Component
public class SpringContextBridge implements ApplicationContextAware {

    // 1. 스프링 컨텍스트를 담을 정적(static) 변수
    private static ApplicationContext applicationContext;

    /**
     * 2. 스프링이 구동될 때 이 메서드를 자동으로 호출하여 컨텍스트를 주입해줍니다.
     * (ApplicationContextAware 인터페이스 구현체이기 때문)
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        // 정적 변수에 주입된 컨텍스트를 할당
        applicationContext = context; 
    }

    /**
     * 3. BPEL이나 외부에서 빈을 가져올 때 사용하는 정적 메서드
     */
    public static Object getBean(String beanName) {
        if (applicationContext == null) {
             throw new IllegalStateException("Spring Context가 아직 초기화되지 않았습니다.");
        }
        return applicationContext.getBean(beanName);
    }

    /**
     * 4. 클래스 타입으로 가져오는 오버로딩 메서드 (더 안전함)
     */
    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
             throw new IllegalStateException("Spring Context가 아직 초기화되지 않았습니다.");
        }
        return applicationContext.getBean(clazz);
    }
}