package kr.co.aim.common.dto.insert;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "WorkStation별 Inbound/Outbound 오더 수량 통계")
public class WorkStationTransportCountResponse {

    @Schema(description = "워크스테이션 ID", example = "341")
    private String workStationId;

    @Schema(description = "Inbound 총 수량", example = "25")
    private Long inboundCount;

    @Schema(description = "Outbound 총 수량", example = "18")
    private Long outboundCount;

    @Schema(description = "합계 수량", example = "43")
    private Long totalCount;

    @QueryProjection
    public WorkStationTransportCountResponse(String workStationId, Long inboundCount, Long outboundCount) {
        this.workStationId = workStationId;
        this.inboundCount = inboundCount != null ? inboundCount : 0L;
        this.outboundCount = outboundCount != null ? outboundCount : 0L;
        this.totalCount = this.inboundCount + this.outboundCount;
    }
}