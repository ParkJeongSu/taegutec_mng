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
@Schema(description = "INSERT 자동 테스트 결과 등록 요청 DTO")
public class InsertAutoTestResultCreateRequestDto {

    @Schema(description = "자동 테스트 마스터 ID (TSID)", example = "877810665130787535", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long autoTestId;

    @Schema(description = "테스트 진행 상태", example = "RUNNING")
    private String state;

    @Schema(description = "테스트 수행 결과", example = "SUCCESS")
    private String result;

    @Schema(description = "이벤트 이름", example = "TestResultRecorded")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Round-trip completed successfully")
    private String eventComment;
}
