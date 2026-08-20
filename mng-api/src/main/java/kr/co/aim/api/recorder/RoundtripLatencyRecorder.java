package kr.co.aim.api.recorder;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoundtripLatencyRecorder {

    private final MeterRegistry meterRegistry;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public void recordByJobName(String transportJobName) {
        if (StringUtils.isBlank(transportJobName)) {
            return;
        }

        try {
            // "G_20260409150200000" -> "20260409150200000" 추출
            String[] parts = transportJobName.split("_");
            if (parts.length < 2) {
                return;
            }
            String timeStr = parts[1];
            LocalDateTime sentDateTime = LocalDateTime.parse(timeStr, FORMATTER);

            // 발송 시점 밀리초 계산
            long sentEpochMilli = sentDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long latencyMs = System.currentTimeMillis() - sentEpochMilli;

            if (latencyMs < 0) {
                latencyMs = 0;
            }

            // Prometheus Timer에 기록
            Timer.builder("transport_job_roundtrip_latency_seconds")
                    .description("Transport Job Roundtrip Latency")
                    .tag("message_name", "TransportJobReply")
                    .publishPercentiles(0.5, 0.9, 0.95, 0.99)
                    .serviceLevelObjectives(
                            Duration.ofMillis(500),
                            Duration.ofMillis(1000),
                            Duration.ofMillis(3000),
                            Duration.ofMillis(5000)
                    )
                    .register(meterRegistry)
                    .record(latencyMs, TimeUnit.MILLISECONDS);

            log.info("[ROUNDTRIP] Job: {}, Latency: {} ms", transportJobName, latencyMs);

        } catch (Exception e) {
            log.warn("JobName 파싱 실패: {}", transportJobName);
        }
    }
}