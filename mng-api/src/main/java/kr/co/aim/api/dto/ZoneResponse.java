package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.Zone;
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
@Schema(description = "존(ZONE) 응답 DTO")
public class ZoneResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "존 이름", example = "ZONE-A")
    private String zoneName;

    @Schema(description = "존 타입", example = "STORAGE")
    private String zoneType;

    @Schema(description = "설명", example = "Storage zone A")
    private String description;

    @Schema(description = "존 색상", example = "#FFFFFF")
    private String zoneColor;

    @Schema(description = "이벤트 이름", example = "ZoneCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-11-24T16:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Zone created")
    private String eventComment;

    public static ZoneResponse fromDomain(Zone domain) {
        if (domain == null) {
            return null;
        }
        return ZoneResponse.builder()
                .id(domain.getId())
                .zoneName(domain.getZoneName())
                .zoneType(domain.getZoneType())
                .description(domain.getDescription())
                .zoneColor(domain.getZoneColor())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }
}
