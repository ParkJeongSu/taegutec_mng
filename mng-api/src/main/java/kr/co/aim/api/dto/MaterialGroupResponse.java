package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.MaterialGroup;
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
@Schema(description = "자재 그룹(MATERIAL_GROUP) 응답 DTO")
public class MaterialGroupResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "자재 그룹 정의명", example = "GRP-001")
    private String materialGroupDefName;

    @Schema(description = "설명", example = "Material group")
    private String description;

    @Schema(description = "이벤트 이름", example = "MaterialGroupCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-11-24T16:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "MaterialGroup created")
    private String eventComment;

    public static MaterialGroupResponse fromDomain(MaterialGroup domain) {
        if (domain == null) {
            return null;
        }
        return MaterialGroupResponse.builder()
                .id(domain.getId())
                .materialGroupDefName(domain.getMaterialGroupDefName())
                .description(domain.getDescription())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }
}
