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
@Schema(description = "사용자 그룹 검색 조건")
public class UserGroupSearchCondition {

    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;

    @Schema(description = "사용자 그룹명", example = "ADMIN_GROUP")
    private String userGroupName;

    @Schema(description = "설명", example = "관리자 그룹")
    private String description;

    @Schema(description = "사용 여부 (상태)", example = "ACTIVE")
    private String useState;
}
