package kr.co.aim.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "자재(MATERIAL) 생성 요청 DTO")
public class MaterialCreateRequestDto {

    @Schema(description = "자재 ID", example = "MAT-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String materialId;

    @Schema(description = "설명", example = "Raw material")
    private String description;

    @Schema(description = "자재 분류 ID", example = "877810665130787535")
    private Long materialClassId;

    @Schema(description = "자재 그룹 ID", example = "877810665130787536")
    private Long materialGroupId;

    @Schema(description = "캐리어 이름", example = "CARRIER-001")
    private String carrierName;

    @Schema(description = "수량", example = "100.000")
    private BigDecimal quantity;

    @Schema(description = "이벤트 이름", example = "MaterialCreated")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Material created")
    private String eventComment;
}
