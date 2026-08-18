package kr.co.aim.common.dto.insert;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "WorkStation별 반송 오더 현황 통계")
public class TransportOrderStatisticsResponse {

    @Schema(description = "워크스테이션 ID", example = "WS01")
    private String workStationId;

    @Schema(description = "당일 완료 수량 (전체)", example = "120")
    private Long completedCount;

    @Schema(description = "진행 중 INBOUND 수량", example = "15")
    private Long inboundProgressCount;

    @Schema(description = "진행 중 OUTBOUND 수량", example = "8")
    private Long outboundProgressCount;

    @Schema(description = "현재 진행 중인 총 반송 수량 (IN+OUT)", example = "23")
    private Long totalProgressCount;

    @QueryProjection
    public TransportOrderStatisticsResponse(String workStationId, Long completedCount, Long inboundProgressCount, Long outboundProgressCount) {
        this.workStationId = workStationId;
        this.completedCount = completedCount != null ? completedCount : 0L;
        this.inboundProgressCount = inboundProgressCount != null ? inboundProgressCount : 0L;
        this.outboundProgressCount = outboundProgressCount != null ? outboundProgressCount : 0L;
        this.totalProgressCount = this.inboundProgressCount + this.outboundProgressCount;
    }
}