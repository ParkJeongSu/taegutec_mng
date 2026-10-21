package kr.co.aim.domain.repository.dto.powder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "일일 전체 Production Order 수량 집계 응답")
public class DailyProductionSummaryResponse {

    @Schema(description = "집계 일자", example = "2026-09-07")
    private LocalDate targetDate;

    @Schema(description = "계획 수량 총합 (PLAN_QUANTITY)", example = "1000.00")
    private BigDecimal totalPlanQuantity;

    @Schema(description = "진행(출고) 수량 총합 (RELEASED_QUANTITY)", example = "800.00")
    private BigDecimal totalReleasedQuantity;

    @Schema(description = "진행한(시작) 수량 총합 (STARTED_QUANTITY)", example = "750.00")
    private BigDecimal totalStartedQuantity;

    @Schema(description = "진행완료 수량 총합 (ENDED_QUANTITY)", example = "700.00")
    private BigDecimal totalEndedQuantity;

    @Schema(description = "스크랩 수량 총합 (SCRAPPED_QUANTITY)", example = "50.00")
    private BigDecimal totalScrappedQuantity;

}