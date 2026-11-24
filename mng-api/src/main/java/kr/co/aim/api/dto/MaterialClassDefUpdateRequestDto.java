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
@Schema(description = "자재 분류 정의(MATERIAL_CLASS_DEF) 수정 요청 DTO")
public class MaterialClassDefUpdateRequestDto {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    private Long id;

    @Schema(description = "자재 분류 정의명", example = "RAW_MATERIAL")
    private String materialClassDefName;

    @Schema(description = "자재 분류 타입", example = "POWDER")
    private String materialClassType;

    @Schema(description = "상위 분류 ID (TSID)", example = "877810665130787535")
    private Long parentClassId;

    @Schema(description = "분류 레벨", example = "1")
    private Integer classLevel;

    @Schema(description = "설명", example = "원자재 분류 수정")
    private String description;

    @Schema(description = "이벤트 이름", example = "MaterialClassDefModified")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Material class def modified")
    private String eventComment;
}
