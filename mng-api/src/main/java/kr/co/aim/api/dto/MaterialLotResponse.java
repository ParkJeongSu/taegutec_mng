package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.MaterialLot;
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
@Schema(description = "자재 로트(MATERIAL_LOT) 응답 DTO")
public class MaterialLotResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "자재 로트 ID", example = "MLOT-001")
    private String materialLotId;

    @Schema(description = "자재 정의 ID", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialDefId;

    @Schema(description = "상위 자재 로트 ID", example = "877810665130787536")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentMaterialLotId;

    @Schema(description = "캐리어 이름", example = "CARRIER-001")
    private String carrierName;

    @Schema(description = "수량", example = "100.000")
    private BigDecimal quantity;

    @Schema(description = "상태", example = "CREATED")
    private String state;

    @Schema(description = "이벤트 이름", example = "MaterialLotCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-11-25T15:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "MaterialLot created")
    private String eventComment;

    public static MaterialLotResponse fromDomain(MaterialLot domain) {
        if (domain == null) {
            return null;
        }
        return MaterialLotResponse.builder()
                .id(domain.getId())
                .materialLotId(domain.getMaterialLotId())
                .materialDefId(domain.getMaterialDefId())
                .parentMaterialLotId(domain.getParentMaterialLotId())
                .carrierName(domain.getCarrierName())
                .quantity(domain.getQuantity())
                .state(domain.getState())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }
}
