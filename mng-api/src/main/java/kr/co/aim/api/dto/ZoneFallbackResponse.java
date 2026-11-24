package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.ZoneFallback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "존 폴백(ZONE_FALLBACK) 응답 DTO")
public class ZoneFallbackResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "원본 존 ID", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sourceZoneId;

    @Schema(description = "대상 존 ID", example = "877810665130787536")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long targetZoneId;

    @Schema(description = "우선순위", example = "1")
    private Integer priority;

    @Schema(description = "이벤트 이름", example = "ZoneFallbackCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-11-24T16:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "ZoneFallback created")
    private String eventComment;

    public static ZoneFallbackResponse fromDomain(ZoneFallback domain) {
        if (domain == null) {
            return null;
        }
        return ZoneFallbackResponse.builder()
                .id(domain.getId())
                .sourceZoneId(domain.getSourceZoneId())
                .targetZoneId(domain.getTargetZoneId())
                .priority(domain.getPriority())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }
}
