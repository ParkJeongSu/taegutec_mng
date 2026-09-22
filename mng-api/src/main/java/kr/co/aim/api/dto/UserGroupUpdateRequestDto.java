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
@Schema(description = "사용자 그룹 수정 요청 DTO")
public class UserGroupUpdateRequestDto {

    @Schema(description = "사용자 그룹 고유 ID (TSID)", example = "877810665130787535")
    private Long id;

    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;

    @Schema(description = "사용자 그룹명", example = "ADMIN_GROUP")
    private String userGroupName;

    @Schema(description = "설명", example = "시스템 관리자 그룹 수정")
    private String description;

    @Schema(description = "사용 여부 (상태)", example = "ACTIVE")
    private String useState;

    @Schema(description = "이벤트 이름", example = "UserGroupModified")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "사용자 그룹 정보 수정")
    private String eventComment;
}
