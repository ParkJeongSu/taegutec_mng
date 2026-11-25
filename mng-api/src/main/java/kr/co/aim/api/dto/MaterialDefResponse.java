package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.MaterialDef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "자재 정의(MATERIAL_DEF) 응답 DTO")
public class MaterialDefResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "자재 정의명", example = "MAT-DEF-001")
    private String materialDefName;

    @Schema(description = "설명", example = "Material definition")
    private String description;

    @Schema(description = "자재 분류 ID", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialClassId;

    @Schema(description = "자재 그룹 ID", example = "877810665130787536")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialGroupId;

    @Schema(description = "단위", example = "KG")
    private String unit;

    @Schema(description = "시리얼 추적 여부", example = "N")
    private String isSerialTracked;

    @Schema(description = "이벤트 이름", example = "MaterialDefCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-11-25T15:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "MaterialDef created")
    private String eventComment;

    public static MaterialDefResponse fromDomain(MaterialDef domain) {
        if (domain == null) {
            return null;
        }
        return MaterialDefResponse.builder()
                .id(domain.getId())
                .materialDefName(domain.getMaterialDefName())
                .description(domain.getDescription())
                .materialClassId(domain.getMaterialClassId())
                .materialGroupId(domain.getMaterialGroupId())
                .unit(domain.getUnit())
                .isSerialTracked(domain.getIsSerialTracked())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }
}
