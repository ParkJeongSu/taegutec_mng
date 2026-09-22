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
@Schema(description = "사용자 그룹별 메뉴 권한 등록 요청 DTO")
public class UserGroupMenuAuthCreateRequestDto {

    @Schema(description = "공장 구분", example = "INSERT", requiredMode = Schema.RequiredMode.REQUIRED)
    private String factoryName;

    @Schema(description = "사용자 그룹 고유 ID (USER_GROUP.ID)", example = "877810665130787000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userGroupId;

    @Schema(description = "메뉴 고유 ID (MENU.ID)", example = "877810665130787100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long menuId;

    @Schema(description = "조회 권한 여부 (Y/N, 미입력 시 'N')", example = "Y", defaultValue = "N")
    private String authSelect;

    @Schema(description = "등록/수정 권한 여부 (Y/N, 미입력 시 'N')", example = "Y", defaultValue = "N")
    private String authSave;

    @Schema(description = "삭제 권한 여부 (Y/N, 미입력 시 'N')", example = "N", defaultValue = "N")
    private String authDelete;

    @Schema(description = "이벤트 이름", example = "UserGroupMenuAuthCreated")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Initial menu auth assignment")
    private String eventComment;
}
