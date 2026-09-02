package kr.co.aim.api.service;

import kr.co.aim.api.dto.DashboardResponseDto;
import kr.co.aim.api.dto.EquipmentDataDashboard;
import kr.co.aim.api.dto.EquipmentGroupDashboard;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.condition.CarrierSearchCondition;
import kr.co.aim.common.enums.ProductionOrderState;
import kr.co.aim.common.enums.TransportJobState;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class DashboardService {

    private final ProductionOrderRepository productionOrderRepository;
    private final TransportJobRepository transportJobRepository;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentGroupDefRepository equipmentGroupDefRepository;
    private final EquipmentDefRepository equipmentDefRepository;

    public Page<DashboardResponseDto> getDashboardInfo() {

        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();              // 2026-05-15T00:00
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);         // 2026-05-15T23:59:59.999999999

        String completedOrderState = ProductionOrderState.COMPLETED.getValue();
        String completedTransportState = TransportJobState.COMPLETED.getValue();
        String cancelTransportState = TransportJobState.CANCELLED.getValue();

        List<ProductionOrder> todayOrderReceivedOrders = productionOrderRepository.findByCreateTimeBetween(startOfToday, endOfToday);
        List<ProductionOrder> todayOrderCompletedOrders = productionOrderRepository.findByCreateTimeBetweenAndProductionOrderState(startOfToday, endOfToday, completedOrderState);

        List<TransportJob> todayJobTotalList  = transportJobRepository.findByCreateTimeBetween(startOfToday, endOfToday);
        List<TransportJob> todayJobSuccessList =  transportJobRepository.findByCreateTimeBetweenAndTransportJobState(startOfToday, endOfToday,completedTransportState);
        List<TransportJob> todayJobFailList =  transportJobRepository.findByCreateTimeBetweenAndTransportJobState(startOfToday, endOfToday,cancelTransportState);

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

        List<ProductionOrder> todayOrderReceivedOrders = productionOrderRepository.findByCreateTimeBetween(startOfToday, endOfToday);
        List<ProductionOrder> todayOrderCompletedOrders = productionOrderRepository.findByCreateTimeBetweenAndProductionOrderState(startOfToday, endOfToday, completedOrderState);

        List<TransportJob> todayJobTotalList  = transportJobRepository.findByCreateTimeBetween(startOfToday, endOfToday);
        List<TransportJob> todayJobSuccessList =  transportJobRepository.findByCreateTimeBetweenAndTransportJobState(startOfToday, endOfToday,completedTransportState);
        List<TransportJob> todayJobFailList =  transportJobRepository.findByCreateTimeBetweenAndTransportJobState(startOfToday, endOfToday,cancelTransportState);

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

    @Transactional(value = "mssqlTransactionManager",readOnly = true)
    public Page<EquipmentGroupDashboard> getEquipmentDataForDashboard(Pageable pageable){

        List<String> activeStates = new ArrayList<>();
        activeStates.add(ProductionOrderState.CREATED.getValue());
        activeStates.add(ProductionOrderState.RELEASED.getValue());
        List<ProductionOrder> activeOrders = productionOrderRepository.findByProductionOrderStateInOrderByCreateTimeAsc(activeStates);

        // 2. 설비별 작업 수 카운팅 (Map 활용)
        Map<String, Long> taskCountMap = new HashMap<>();
        for (ProductionOrder order : activeOrders) {
            String eqName = order.getEquipmentName();
            if (eqName != null) {
                taskCountMap.put(eqName, taskCountMap.getOrDefault(eqName, 0L) + 1);
            }
        }

        // 3. 설비군(Group) 페이징 조회
        List<EquipmentGroupDef> equipmentGroupDefList = equipmentGroupDefRepository.findAll();
        List<EquipmentGroupDashboard> dashboardList = new ArrayList<>();

        // 4. 모든 설비 및 설비정의 조회 (데이터가 적으므로 메모리 로드)
        List<Equipment> allEquipmentList = equipmentRepository.findAll();
        List<EquipmentDef> allEquipmentDefList = equipmentDefRepository.findAll();

        // 5. 데이터 조립 (계층 구조 생성)
        for (EquipmentGroupDef group : equipmentGroupDefList) {
            List<EquipmentDataDashboard> equipmentList = new ArrayList<>();
            long groupTotalTaskCount = 0;

            // 설비군 ID에 해당하는 설비정의 찾기 -> 그 설비정의에 해당하는 설비 찾기
            for (EquipmentDef equipmentDef : allEquipmentDefList) {
                if (group.getEquipmentGroupName().equals(equipmentDef.getEquipmentGroupName())) {

                    for (Equipment equipment : allEquipmentList) {
                        // 설비명이나 특정 ID를 통해 설비와 설비정의를 매핑 (구조에 따라 조건 수정 필요)
                        // 여기서는 설비정의의 특정 속성과 설비가 매핑된다고 가정하거나
                        // 설비 엔티티에 Def ID가 있다면 그것을 사용합니다.
                        if (equipment.getEquipmentName().equals(equipmentDef.getEquipmentName())) {

                            long count = taskCountMap.getOrDefault(equipment.getEquipmentName(), 0L);
                            groupTotalTaskCount += count;

                            EquipmentDataDashboard eqDto = EquipmentDataDashboard.builder()
                                    .id(equipment.getId())
                                    .equipmentName(equipment.getEquipmentName())
                                    .taskCount(count)
                                    .equipmentState(equipment.getEquipmentState())
                                    .communicationState(equipment.getCommunicationState())
                                    .productionOrderId(equipment.getProductionOrderId())
                                    // ... 필요한 나머지 필드 매핑
                                    .build();

                            equipmentList.add(eqDto);
                        }
                    }
                }
            }

            // Group DTO 생성
            EquipmentGroupDashboard groupDto = EquipmentGroupDashboard.builder()
                    .id(TsidUtils.nextId())
                    .equipmentGroupName(group.getEquipmentGroupName())
                    .totalTaskCount(groupTotalTaskCount)
                    .description(group.getDescription())
                    .equipmentList(equipmentList)
                    .build();

            dashboardList.add(groupDto);
        }


        long totalCount = equipmentGroupDefList.stream().count();
        // 3. PageImpl을 사용하여 수동으로 생성하여 반환
        return new PageImpl<>(dashboardList, pageable, totalCount);
    }



}