package kr.co.aim.domain.repository.dto.insert;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
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

}