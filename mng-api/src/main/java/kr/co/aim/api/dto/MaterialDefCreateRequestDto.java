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
@Schema(description = "자재 정의(MATERIAL_DEF) 생성 요청 DTO")
public class MaterialDefCreateRequestDto {

    @Schema(description = "자재 정의명", example = "MAT-DEF-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String materialDefName;

    @Schema(description = "설명", example = "Material definition")
    private String description;

    @Schema(description = "자재 분류 ID", example = "877810665130787535")
    private Long materialClassId;

    @Schema(description = "자재 그룹 ID", example = "877810665130787536")
    private Long materialGroupId;

    @Schema(description = "단위", example = "KG")
    private String unit;

    @Schema(description = "시리얼 추적 여부", example = "N")
    private String isSerialTracked;

    @Schema(description = "이벤트 이름", example = "MaterialDefCreated")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "MaterialDef created")
    private String eventComment;
}
