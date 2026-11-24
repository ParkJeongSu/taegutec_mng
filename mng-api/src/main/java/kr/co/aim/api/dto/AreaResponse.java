package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.Area;
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
@Schema(description = "구역(AREA) 응답 DTO")
public class AreaResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "구역명", example = "AREA-01")
    private String areaName;

    @Schema(description = "설명", example = "Main area")
    private String description;

    @Schema(description = "이벤트 이름", example = "AreaCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-11-24T16:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Area created")
    private String eventComment;

    public static AreaResponse fromDomain(Area domain) {
        if (domain == null) {
            return null;
        }
        return AreaResponse.builder()
                .id(domain.getId())
                .areaName(domain.getAreaName())
                .description(domain.getDescription())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }
}
