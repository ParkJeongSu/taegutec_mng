package kr.co.aim.common.dto.powder;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@Schema(description = "설비별 일일 Production Order 수량 집계 응답")
public class EquipmentProductionCountResponse {

    @Schema(description = "집계 일자", example = "2026-09-07")
    private LocalDate targetDate;

    @Schema(description = "설비명 (EQUIPMENT_NAME)", example = "MC_01")
    private String equipmentName;

    @Schema(description = "계획 수량 총합 (PLAN_QUANTITY)", example = "500.00")
    private BigDecimal planQuantity;

    @Schema(description = "진행(출고) 수량 총합 (RELEASED_QUANTITY)", example = "400.00")
    private BigDecimal releasedQuantity;

    @Schema(description = "진행한(시작) 수량 총합 (STARTED_QUANTITY)", example = "380.00")
    private BigDecimal startedQuantity;

    @Schema(description = "진행완료 수량 총합 (ENDED_QUANTITY)", example = "350.00")
    private BigDecimal endedQuantity;

    @Schema(description = "스크랩 수량 총합 (SCRAPPED_QUANTITY)", example = "20.00")
    private BigDecimal scrappedQuantity;

    @QueryProjection
    public EquipmentProductionCountResponse(LocalDate targetDate, String equipmentName, BigDecimal planQuantity, BigDecimal releasedQuantity, BigDecimal startedQuantity, BigDecimal endedQuantity, BigDecimal scrappedQuantity) {
        this.targetDate = targetDate;
        this.equipmentName = equipmentName;
        this.planQuantity = planQuantity != null ? planQuantity : BigDecimal.ZERO;
        this.releasedQuantity = releasedQuantity != null ? releasedQuantity : BigDecimal.ZERO;
        this.startedQuantity = startedQuantity != null ? startedQuantity : BigDecimal.ZERO;
        this.endedQuantity = endedQuantity != null ? endedQuantity : BigDecimal.ZERO;
        this.scrappedQuantity = scrappedQuantity != null ? scrappedQuantity : BigDecimal.ZERO;
    }
}