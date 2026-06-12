package kr.co.aim.api.service;

import kr.co.aim.api.dto.DashboardResponseDto;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.condition.CarrierSearchCondition;
import kr.co.aim.common.enums.ProductionOrderState;
import kr.co.aim.common.enums.TransportJobState;
import kr.co.aim.domain.model.Carrier;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.domain.model.TransportJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class DashboardService {

    private final ProductionOrderService productionOrderService;
    private final TransportJobService transportJobService;

    public Page<DashboardResponseDto> getDashboardInfo() {

        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();              // 2026-05-15T00:00
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);         // 2026-05-15T23:59:59.999999999

        String completedOrderState = ProductionOrderState.COMPLETED.getValue();
        String completedTransportState = TransportJobState.COMPLETED.getValue();
        String cancelTransportState = TransportJobState.CANCELLED.getValue();

        List<ProductionOrder> todayOrderReceivedOrders = productionOrderService.findByCreateTimeBetween(startOfToday, endOfToday);
        List<ProductionOrder> todayOrderCompletedOrders = productionOrderService.findByCreateTimeBetweenAndProductionOrderState(startOfToday, endOfToday, completedOrderState);

        List<TransportJob> todayJobTotalList  = transportJobService.findByCreateTimeBetween(startOfToday, endOfToday);
        List<TransportJob> todayJobSuccessList =  transportJobService.findByCreateTimeBetweenAndTransportJobState(startOfToday, endOfToday,completedTransportState);
        List<TransportJob> todayJobFailList =  transportJobService.findByCreateTimeBetweenAndTransportJobState(startOfToday, endOfToday,cancelTransportState);

        DashboardResponseDto dashboardResponseDto = DashboardResponseDto
                .builder()
                .id(TsidUtils.nextId())
                .todayOrderReceivedCount(todayOrderReceivedOrders.size())
                .todayOrderCompletedCount(todayOrderCompletedOrders.size())
                .todayTransportTotalCount(todayJobTotalList.size())
                .todayTransportSuccessCount(todayJobSuccessList.size())
                .todayTransportFailureCount(todayJobFailList.size())
                .build();

        List<DashboardResponseDto> dtoList = Collections.singletonList(dashboardResponseDto);

        return new PageImpl<>(dtoList);
    }

    public Page<DashboardResponseDto> getDashboardInfoV2() {

        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();              // 2026-05-15T00:00
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);         // 2026-05-15T23:59:59.999999999

        String completedOrderState = ProductionOrderState.COMPLETED.getValue();
        String completedTransportState = TransportJobState.COMPLETED.getValue();
        String cancelTransportState = TransportJobState.CANCELLED.getValue();

        List<ProductionOrder> todayOrderReceivedOrders = productionOrderService.findByCreateTimeBetween(startOfToday, endOfToday);
        List<ProductionOrder> todayOrderCompletedOrders = productionOrderService.findByCreateTimeBetweenAndProductionOrderState(startOfToday, endOfToday, completedOrderState);

        List<TransportJob> todayJobTotalList  = transportJobService.findByCreateTimeBetween(startOfToday, endOfToday);
        List<TransportJob> todayJobSuccessList =  transportJobService.findByCreateTimeBetweenAndTransportJobState(startOfToday, endOfToday,completedTransportState);
        List<TransportJob> todayJobFailList =  transportJobService.findByCreateTimeBetweenAndTransportJobState(startOfToday, endOfToday,cancelTransportState);

        DashboardResponseDto dashboardResponseDto = DashboardResponseDto
                .builder()
                .id(TsidUtils.nextId())
                .todayOrderReceivedCount(todayOrderReceivedOrders.size())
                .todayOrderCompletedCount(todayOrderCompletedOrders.size())
                .todayTransportTotalCount(todayJobTotalList.size())
                .todayTransportSuccessCount(todayJobSuccessList.size())
                .todayTransportFailureCount(todayJobFailList.size())
                .build();

        List<DashboardResponseDto> dtoList = Collections.singletonList(dashboardResponseDto);

        return new PageImpl<>(dtoList);
    }
}