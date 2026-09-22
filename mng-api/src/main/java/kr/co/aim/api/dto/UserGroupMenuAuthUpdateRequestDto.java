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
@Schema(description = "사용자 그룹별 메뉴 권한 수정 요청 DTO")
public class UserGroupMenuAuthUpdateRequestDto {

    @Schema(description = "조회 권한 여부 (Y/N)", example = "Y")
    private String authSelect;

    @Schema(description = "등록/수정 권한 여부 (Y/N)", example = "Y")
    private String authSave;

    @Schema(description = "삭제 권한 여부 (Y/N)", example = "N")
    private String authDelete;

    @Schema(description = "이벤트 이름", example = "UserGroupMenuAuthModified")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Menu auth updated")
    private String eventComment;
}
