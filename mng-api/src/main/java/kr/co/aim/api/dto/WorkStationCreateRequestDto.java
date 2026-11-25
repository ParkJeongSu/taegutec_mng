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
@Schema(description = "작업 스테이션(WORK_STATION) 생성 요청 DTO")
public class WorkStationCreateRequestDto {

    @Schema(description = "작업 스테이션명", example = "WS-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String workStationName;

    @Schema(description = "설비명", example = "EQP-01")
    private String equipmentName;

    @Schema(description = "이벤트 이름", example = "WorkStationCreated")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "WorkStation created")
    private String eventComment;
}
