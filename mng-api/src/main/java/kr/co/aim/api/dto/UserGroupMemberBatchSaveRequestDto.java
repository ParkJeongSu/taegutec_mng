package kr.co.aim.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 그룹별 사용자 매핑 일괄 저장 요청 DTO")
public class UserGroupMemberBatchSaveRequestDto {

    @Schema(description = "공장 구분", example = "INSERT", requiredMode = Schema.RequiredMode.REQUIRED)
    private String factoryName;

    @Schema(description = "사용자 그룹 ID (USER_GROUP.ID)", example = "877810665130787535", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userGroupId;

    @Schema(description = "이벤트 이름", example = "UserGroupMembersBatchSaved")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Batch user group members updated")
    private String eventComment;

    @Schema(description = "배정할 사용자 ID(SYS_USER.ID) 목록")
    private List<Long> userIdList;
}