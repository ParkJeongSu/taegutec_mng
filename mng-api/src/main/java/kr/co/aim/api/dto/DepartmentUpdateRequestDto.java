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
@Schema(description = "부서 수정 요청 DTO")
public class DepartmentUpdateRequestDto {

    @Schema(description = "부서 고유 ID", example = "877810665130787535")
    private Long id;

    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;

    @Schema(description = "부서명", example = "생산기획팀")
    private String departmentName;

    @Schema(description = "사용 여부 (상태)", example = "ACTIVE")
    private String useState;

    @Schema(description = "이벤트 이름", example = "DepartmentModified")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "admin")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "부서 정보 수정")
    private String eventComment;
}
