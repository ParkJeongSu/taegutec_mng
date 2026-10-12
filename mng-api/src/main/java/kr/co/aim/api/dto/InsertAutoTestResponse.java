package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.InsertAutoTest;
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
@Schema(description = "INSERT 자동 테스트 응답 DTO")
public class InsertAutoTestResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "창고 이름", example = "WH-01")
    private String wareHouseName;

    @Schema(description = "작업 스테이션 ID", example = "WS-01")
    private String workStationId;

    @Schema(description = "캐리어 이름", example = "CARRIER-001")
    private String carrierName;

    @Schema(description = "존 이름", example = "ZONE-A")
    private String zoneName;

    @Schema(description = "Inbound port Name", example = "314001")
    private String inboundPortName;

    @Schema(description = "주행 프로파일", example = "PROFILE_DEFAULT")
    private String travelProfile;

    @Schema(description = "이벤트 이름", example = "AutoTestCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-10-08T09:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Auto test scenario created")
    private String eventComment;

    public static InsertAutoTestResponse fromDomain(InsertAutoTest test) {
        if (test == null) {
            return null;
        }
        return InsertAutoTestResponse.builder()
                .id(test.getId())
                .wareHouseName(test.getWareHouseName())
                .workStationId(test.getWorkStationId())
                .carrierName(test.getCarrierName())
                .zoneName(test.getZoneName())
                .travelProfile(test.getTravelProfile())
                .inboundPortName(test.getInboundPortName())
                .eventName(test.getEventName())
                .eventTime(test.getEventTime())
                .eventUser(test.getEventUser())
                .eventComment(test.getEventComment())
                .build();
    }
}
