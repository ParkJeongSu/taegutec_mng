package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.InsertAutoRelocationTest;
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
@Schema(description = "INSERT 자동 재배치 테스트 응답 DTO")
public class InsertAutoRelocationTestResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "출발 창고 이름", example = "WH-01")
    private String wareHouseName1;

    @Schema(description = "도착 창고 이름", example = "WH-02")
    private String wareHouseName2;

    @Schema(description = "출발 존 이름", example = "ZONE-A")
    private String zoneName1;

    @Schema(description = "도착 존 이름", example = "ZONE-B")
    private String zoneName2;

    @Schema(description = "캐리어 이름", example = "CARRIER-001")
    private String carrierName;

    @Schema(description = "테스트 진행현황", example = "CREATED")
    private String state;

    @Schema(description = "이벤트 이름", example = "AutoRelocationTestCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-11-24T09:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Auto relocation test scenario created")
    private String eventComment;

    public static InsertAutoRelocationTestResponse fromDomain(InsertAutoRelocationTest test) {
        if (test == null) {
            return null;
        }
        return InsertAutoRelocationTestResponse.builder()
                .id(test.getId())
                .wareHouseName1(test.getWareHouseName1())
                .wareHouseName2(test.getWareHouseName2())
                .zoneName1(test.getZoneName1())
                .zoneName2(test.getZoneName2())
                .carrierName(test.getCarrierName())
                .state(test.getState())
                .eventName(test.getEventName())
                .eventTime(test.getEventTime())
                .eventUser(test.getEventUser())
                .eventComment(test.getEventComment())
                .build();
    }
}
