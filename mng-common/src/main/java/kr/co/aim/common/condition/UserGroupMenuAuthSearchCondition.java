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
@Schema(description = "사용자 그룹별 메뉴 권한 검색 조건")
public class UserGroupMenuAuthSearchCondition {

    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;

    @Schema(description = "사용자 그룹 고유 ID (USER_GROUP.ID)", example = "877810665130787000")
    private Long userGroupId;

    @Schema(description = "사용자 그룹명 (USER_GROUP.USER_GROUP_NAME)", example = "ADMIN")
    private String userGroupName;

    @Schema(description = "메뉴 고유 ID (MENU.ID)", example = "877810665130787100")
    private Long menuId;

    @Schema(description = "메뉴명 (MENU.MENU_NAME)", example = "사용자 관리")
    private String menuName;

    @Schema(description = "조회 권한 여부 (Y/N)", example = "Y")
    private String authSelect;

    @Schema(description = "저장/수정 권한 여부 (Y/N)", example = "Y")
    private String authSave;

    @Schema(description = "삭제 권한 여부 (Y/N)", example = "N")
    private String authDelete;
}
