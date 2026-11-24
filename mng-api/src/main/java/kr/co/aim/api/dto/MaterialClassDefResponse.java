package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.MaterialClassDef;
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
@Schema(description = "자재 분류 정의(MATERIAL_CLASS_DEF) 응답 DTO")
public class MaterialClassDefResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "자재 분류 정의명", example = "RAW_MATERIAL")
    private String materialClassDefName;

    @Schema(description = "자재 분류 타입", example = "POWDER")
    private String materialClassType;

    @Schema(description = "상위 분류 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentClassId;

    @Schema(description = "분류 레벨", example = "1")
    private Integer classLevel;

    @Schema(description = "설명", example = "원자재 분류")
    private String description;

    @Schema(description = "이벤트 이름", example = "MaterialClassDefCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-11-24T15:30:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Material class def created")
    private String eventComment;

    public static MaterialClassDefResponse fromDomain(MaterialClassDef domain) {
        if (domain == null) {
            return null;
        }
        return MaterialClassDefResponse.builder()
                .id(domain.getId())
                .materialClassDefName(domain.getMaterialClassDefName())
                .materialClassType(domain.getMaterialClassType())
                .parentClassId(domain.getParentClassId())
                .classLevel(domain.getClassLevel())
                .description(domain.getDescription())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }
}
