package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.ZoneMapping;
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
@Schema(description = "존 매핑(ZONE_MAPPING) 응답 DTO")
public class ZoneMappingResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "존 ID", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long zoneId;

    @Schema(description = "오더 라인 번호", example = "0010")
    private String orderLineNumber;

    @Schema(description = "이벤트 이름", example = "ZoneMappingCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-11-24T16:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "ZoneMapping created")
    private String eventComment;

    public static ZoneMappingResponse fromDomain(ZoneMapping domain) {
        if (domain == null) {
            return null;
        }
        return ZoneMappingResponse.builder()
                .id(domain.getId())
                .zoneId(domain.getZoneId())
                .orderLineNumber(domain.getOrderLineNumber())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }
}
