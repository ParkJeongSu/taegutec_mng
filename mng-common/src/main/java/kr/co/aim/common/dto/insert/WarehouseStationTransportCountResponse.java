package kr.co.aim.common.dto.insert;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@Schema(description = "창고 및 워크스테이션별 반송 수량 집계 응답")
public class WarehouseStationTransportCountResponse {

    @Schema(description = "창고명 (GAL_WAREHOUSE)", example = "GAL_WAREHOUSE1")
    private String galWarehouse;

    @Schema(description = "워크스테이션 ID (WORK_STATION_ID)", example = "STATION1")
    private String workStationId;

    @Schema(description = "INBOUND 총 수량", example = "12")
    private long inboundCount;

    @Schema(description = "OUTBOUND 총 수량", example = "15")
    private long outboundCount;

    @Schema(description = "RELOCATION 총 수량", example = "10")
    private long relocationCount;

    @QueryProjection
    public WarehouseStationTransportCountResponse(String galWarehouse, String workStationId, long inboundCount, long outboundCount, long relocationCount) {
        this.galWarehouse = galWarehouse;
        this.workStationId = workStationId;
        this.inboundCount = inboundCount;
        this.outboundCount = outboundCount;
        this.relocationCount = relocationCount;
    }
}