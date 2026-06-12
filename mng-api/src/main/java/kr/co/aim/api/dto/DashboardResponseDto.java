package kr.co.aim.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DashboardResponseDto {

    private Long id;

    // === 주문 관련 지표 (Today's Order Metrics) ===
    private int todayOrderReceivedCount;
    private int todayOrderCompletedCount;

    // === 반송 관련 지표 (Today's Return Metrics) ===
    private int todayTransportTotalCount;
    private int todayTransportSuccessCount;
    private int todayTransportFailureCount;
}