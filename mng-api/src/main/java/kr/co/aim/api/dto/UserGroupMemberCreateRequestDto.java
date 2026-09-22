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
@Schema(description = "사용자-그룹 매핑 등록 요청 DTO")
public class UserGroupMemberCreateRequestDto {

    @Schema(description = "공장 구분", example = "INSERT", requiredMode = Schema.RequiredMode.REQUIRED)
    private String factoryName;

    @Schema(description = "사용자 고유 ID (SYS_USER.ID)", example = "877810665130787500", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "사용자 그룹 고유 ID (USER_GROUP.ID)", example = "877810665130787000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userGroupId;

    @Schema(description = "이벤트 이름", example = "UserGroupMemberCreated")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "사용자 그룹 매핑 등록")
    private String eventComment;
}
