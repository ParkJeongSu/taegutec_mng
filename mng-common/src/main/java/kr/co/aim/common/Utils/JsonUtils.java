package kr.co.aim.common.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JsonUtils {

    private final ObjectMapper objectMapper;

    public void writePrettyJson(String jsonString) {
        // --- JSON 예쁘게 로그 찍기 ---
        try {
            Object jsonObject = objectMapper.readValue(jsonString, Object.class);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            log.info("\n=== [Received Message Body] ===\n{}", prettyJson);
        } catch (Exception e) {
            log.warn("Failed to pretty print JSON, logging raw string: {}", jsonString);
        }
        // ----------------------------
    }
}