package kr.co.aim.common.condition;

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
@Schema(description = "사용자-그룹 매핑 검색 조건")
public class UserGroupMemberSearchCondition {

    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;

    @Schema(description = "사용자 고유 ID (SYS_USER.ID)", example = "877810665130787500")
    private Long userId;

    @Schema(description = "사용자 그룹 고유 ID (USER_GROUP.ID)", example = "877810665130787000")
    private Long userGroupId;

    @Schema(description = "사용자 사번 (SYS_USER.USER_ID)", example = "admin")
    private String employeeId;

    @Schema(description = "사용자 성명 (SYS_USER.USER_NAME)", example = "관리자")
    private String userName;

    @Schema(description = "사용자 그룹명 (USER_GROUP.USER_GROUP_NAME)", example = "ADMIN_GROUP")
    private String userGroupName;
}
