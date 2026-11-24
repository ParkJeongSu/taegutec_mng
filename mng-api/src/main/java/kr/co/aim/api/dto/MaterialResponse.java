package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.Material;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "자재(MATERIAL) 응답 DTO")
public class MaterialResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "자재 ID", example = "MAT-001")
    private String materialId;

    @Schema(description = "설명", example = "Raw material")
    private String description;

    @Schema(description = "자재 분류 ID", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialClassId;

    @Schema(description = "자재 그룹 ID", example = "877810665130787536")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialGroupId;

    @Schema(description = "캐리어 이름", example = "CARRIER-001")
    private String carrierName;

    @Schema(description = "수량", example = "100.000")
    private BigDecimal quantity;

    @Schema(description = "이벤트 이름", example = "MaterialCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-11-24T16:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Material created")
    private String eventComment;

    public static MaterialResponse fromDomain(Material domain) {
        if (domain == null) {
            return null;
        }
        return MaterialResponse.builder()
                .id(domain.getId())
                .materialId(domain.getMaterialId())
                .description(domain.getDescription())
                .materialClassId(domain.getMaterialClassId())
                .materialGroupId(domain.getMaterialGroupId())
                .carrierName(domain.getCarrierName())
                .quantity(domain.getQuantity())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }
}
