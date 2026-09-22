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
@Schema(description = "사용자-그룹 매핑 수정 요청 DTO")
public class UserGroupMemberUpdateRequestDto {

    @Schema(description = "매핑 고유 ID (TSID)", example = "877810665130787535")
    private Long id;

    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;

    @Schema(description = "사용자 고유 ID (SYS_USER.ID)", example = "877810665130787500")
    private Long userId;

    @Schema(description = "사용자 그룹 고유 ID (USER_GROUP.ID)", example = "877810665130787000")
    private Long userGroupId;

    @Schema(description = "이벤트 이름", example = "UserGroupMemberModified")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "사용자 그룹 매핑 수정")
    private String eventComment;
}
