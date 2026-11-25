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
@Schema(description = "작업 스테이션(WORK_STATION) 수정 요청 DTO")
public class WorkStationUpdateRequestDto {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    private Long id;

    @Schema(description = "작업 스테이션명", example = "WS-01")
    private String workStationName;

    @Schema(description = "설비명", example = "EQP-01")
    private String equipmentName;

    @Schema(description = "이벤트 이름", example = "WorkStationModified")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "WorkStation modified")
    private String eventComment;
}
