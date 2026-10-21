package kr.co.aim.domain.repository.dto.insert;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "일일 전체 Transport Order 수량 집계 응답")
public class DailyTransportSummaryResponse {

    @Schema(description = "총 반송 수량", example = "37")
    private long totalCount;

    @Schema(description = "INBOUND 수량", example = "12")
    private long inboundCount;

    @Schema(description = "OUTBOUND 수량", example = "15")
    private long outboundCount;

    @Schema(description = "RELOCATION 수량", example = "10")
    private long relocationCount;

}