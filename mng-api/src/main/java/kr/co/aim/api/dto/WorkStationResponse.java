package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.WorkStation;
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
@Schema(description = "작업 스테이션(WORK_STATION) 응답 DTO")
public class WorkStationResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "작업 스테이션명", example = "WS-01")
    private String workStationName;

    @Schema(description = "설비명", example = "EQP-01")
    private String equipmentName;

    @Schema(description = "이벤트 이름", example = "WorkStationCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-11-25T15:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "WorkStation created")
    private String eventComment;

    public static WorkStationResponse fromDomain(WorkStation domain) {
        if (domain == null) {
            return null;
        }
        return WorkStationResponse.builder()
                .id(domain.getId())
                .workStationName(domain.getWorkStationName())
                .equipmentName(domain.getEquipmentName())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }
}
