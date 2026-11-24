package kr.co.aim.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "INSERT 자동 재배치 테스트 수정 요청 DTO")
public class InsertAutoRelocationTestUpdateRequestDto {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
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

    @Schema(description = "테스트 진행현황", example = "IN_PROGRESS")
    private String state;

    @Schema(description = "이벤트 이름", example = "AutoRelocationTestModified")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Auto relocation test scenario modified")
    private String eventComment;
}
