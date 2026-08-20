package kr.co.aim.api.service;

import jakarta.annotation.PostConstruct;
import kr.co.aim.common.handler.MetaDataEnum;
import kr.co.aim.common.handler.ParentMetaDataEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class MetaDataService {

    // "alarm-type" -> com.yourcompany.enums.AlarmType.class
    private final Map<String, Class<? extends MetaDataEnum>> enumRegistry = new ConcurrentHashMap<>();

    // 애플리케이션 시작 시 Enum 스캔
    @PostConstruct
    public void init() {
        log.info("Initializing MetaDataEnum registry...");

        // 1. Spring의 클래스 스캐너 사용
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false); // false: @Component 등 기본 필터 사용 안 함

        // 2. 'MetaDataEnum' 인터페이스를 구현한 클래스만 찾도록 필터 추가
        scanner.addIncludeFilter(new AssignableTypeFilter(MetaDataEnum.class));

        // 3. 'enums' 패키지 스캔
        String basePackage = "kr.co.aim.common.enums";

        scanner.findCandidateComponents(basePackage).forEach(bd -> {
            try {
                Class<?> clazz = Class.forName(bd.getBeanClassName());

                // Enum이면서 MetaDataEnum을 구현했는지 확인
                if (clazz.isEnum() && MetaDataEnum.class.isAssignableFrom(clazz)) {
                    Class<? extends MetaDataEnum> enumClass = (Class<? extends MetaDataEnum>) clazz;

                    // 클래스 이름(예: AlarmType)을 API URL(예: alarm-type)로 변환
                    String enumKey = convertCamelCaseToKebabCase(enumClass.getSimpleName());

                    //log.info("Registering MetaDataEnum: {} -> {}", enumKey, enumClass.getName());
                    enumRegistry.put(enumKey, enumClass);
                }
            } catch (ClassNotFoundException e) {
                log.error("Could not find class for bean definition: {}", bd.getBeanClassName(), e);
            }
        });
    }

    /**
     * 컨트롤러에서 호출할 메인 메소드
     */
    public List<Map<String, String>> getMetaData(String enumKey) {
        Class<? extends MetaDataEnum> enumClass = enumRegistry.get(enumKey);

        if (enumClass == null) {
            // 혹은 적절한 404 예외 처리
            throw new IllegalArgumentException("No meta-data enum found for key: " + enumKey);
        }

        List<Map<String, String>> result = new ArrayList<>();

        // Enum 상수들을 순회하며 프론트엔드 형식으로 변환
        for (MetaDataEnum constant : enumClass.getEnumConstants()) {
            Map<String, String> entry = new HashMap<>();
            entry.put("label", constant.name()); // 예: "Set" (v-select의 item-title)
            // 핵심: 어떤 타입이든 안전하게 문자열로 변환하여 Map에 저장
            Object val = constant.getValue();
            entry.put("code", val != null ? String.valueOf(val) : "");
            result.add(entry);
        }
        return result;
    }

    /**
     * 컨트롤러에서 호출할 메인 메소드
     */
    public List<Map<String, String>> getMetaData(String enumKey,String enumValue) {
        Class<? extends MetaDataEnum> enumClass = enumRegistry.get(enumKey);

        if (enumClass == null) {
            // 혹은 적절한 404 예외 처리
            throw new IllegalArgumentException("No meta-data enum found for key: " + enumKey);
        }

        List<Map<String, String>> result = new ArrayList<>();

        // Enum 상수들을 순회하며 프론트엔드 형식으로 변환
        for (MetaDataEnum constant : enumClass.getEnumConstants()) {
            if(constant.name().equalsIgnoreCase(enumValue)){

                ParentMetaDataEnum parent = (ParentMetaDataEnum)constant;
                List<MetaDataEnum> metaDataEnumList = parent.getChildList();

                for(MetaDataEnum c : metaDataEnumList){
                    Map<String, String> entry = new HashMap<>();
                    entry.put("label", c.name()); // 예: "Set" (v-select의 item-title)
                    // 핵심: 어떤 타입이든 안전하게 문자열로 변환하여 Map에 저장
                    Object val = constant.getValue();
                    entry.put("code", val != null ? String.valueOf(val) : "");
                    result.add(entry);
                }
            }
        }
        return result;
    }

    /**
     * 컨트롤러에서 호출할 메인 메소드
     */
    public List<String> getMetaDataList(String enumKey) {
        Class<? extends MetaDataEnum> enumClass = enumRegistry.get(enumKey);

        if (enumClass == null) {
            // 혹은 적절한 404 예외 처리
            throw new IllegalArgumentException("No meta-data enum found for key: " + enumKey);
        }

        List<String> result = new ArrayList<>();

        // Enum 상수들을 순회하며 프론트엔드 형식으로 변환
        for (MetaDataEnum constant : enumClass.getEnumConstants()) {
            // 핵심: 어떤 타입이든 안전하게 문자열로 변환하여 Map에 저장
            Object val = constant.getValue();
            String value = String.valueOf(val);
            result.add(value);
        }
        result.sort(String::compareTo);

        return result;
    }

    /**
     * (선택 사항) 등록된 모든 메타데이터 키 목록 반환
     */
    public Set<String> getAllMetaDataKeys() {
        return enumRegistry.keySet();
    }

    /**
     * CamelCase (AlarmType) -> kebab-case (alarm-type) 변환 헬퍼
     */
    private String convertCamelCaseToKebabCase(String input) {
        if (input == null || input.isEmpty()) return "";
        // "([a-z0-9])([A-Z])" -> "$1-$2"
        return input.replaceAll("([a-z0-9])([A-Z])", "$1-$2").toLowerCase();
    }
}
