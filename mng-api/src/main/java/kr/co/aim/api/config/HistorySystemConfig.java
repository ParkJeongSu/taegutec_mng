package kr.co.aim.api.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class HistorySystemConfig {

    /**
     * 시스템별로 조회 가능한 History 리소스 이름(resourceName) 목록
     * Key: 시스템 이름 (String)
     * Value: 리소스 이름 목록 (List<String>)
     */
    @Bean(name = "historySystemMap")
    public Map<String, List<String>> systemResourceMap() {
        Map<String, List<String>> map = new HashMap<>();

        // 1. Modeler 시스템
        map.put("MODELER", List.of(
                "alarm-action",
                "alarm-action-mail-detail",
                "alarm",
                "alarm-def",
                "alarm-user-group",
                "alarm-user-group-users",
                "auth",
                "auth-menu",
                "carriers",
                "carrier-def",
                "equipments",
                "equipment-def",
                "equipment-group",
                "ports",
                "port-def",
                "system-def",
                "users"
        ));

        // 2. MES 시스템
        map.put("MNG", List.of(
                "alarm",
                "lot",
                "machine",
                "alarm-def" // 공통 리소스
        ));


        return map;
    }
}
