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
@Schema(description = "자재 그룹(MATERIAL_GROUP) 생성 요청 DTO")
public class MaterialGroupCreateRequestDto {

    @Schema(description = "자재 그룹 명", example = "GRP-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String materialGroupName;

    @Schema(description = "설명", example = "Material group")
    private String description;

    @Schema(description = "이벤트 이름", example = "MaterialGroupCreated")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "MaterialGroup created")
    private String eventComment;
}
