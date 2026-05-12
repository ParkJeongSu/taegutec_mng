package kr.co.aim.api.service;

import kr.co.aim.api.dto.EquipmentDataDashboard;
import kr.co.aim.api.dto.EquipmentGroupDashboard;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.condition.EquipmentSearchCondition;
import kr.co.aim.common.enums.ProductionOrderState;
import kr.co.aim.domain.model.Equipment;
import kr.co.aim.domain.model.EquipmentDef;
import kr.co.aim.domain.model.EquipmentGroup;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.domain.repository.EquipmentDefRepository;
import kr.co.aim.domain.repository.EquipmentGroupRepository;
import kr.co.aim.domain.repository.EquipmentRepository;
import kr.co.aim.infra.persistence.mapper.EquipmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentGroupRepository equipmentGroupRepository;
    private final EquipmentDefRepository equipmentDefRepository;
    private final EquipmentMapper equipmentMapper;
    private final HistoryService historyService;
    private final ProductionOrderService productionOrderService;

    @Transactional(value = "mssqlTransactionManager")
    public Equipment save(Equipment equipment){
        return equipmentRepository.save(equipment);
    }

    @Transactional(value = "mssqlTransactionManager")
    public Optional<Equipment> findEquipmentByEquipmentName(String equipmentName){
        return equipmentRepository.findByEquipmentName(equipmentName);
    }

    @Transactional(value = "mssqlTransactionManager")
    public Optional<EquipmentDef> findEquipmentDefByEquipmentName(String equipmentName){
        return equipmentDefRepository.findByEquipmentName(equipmentName);
    }

    @Transactional(value = "mssqlTransactionManager",readOnly = true)
    public Page<Equipment> findEquipmentByCondition(EquipmentSearchCondition condition, Pageable pageable){
        return equipmentRepository.findEquipmentByCondition(condition,pageable);
    }

    @Transactional(value = "mssqlTransactionManager",readOnly = true)
    public Page<EquipmentGroupDashboard> getEquipmentDataForDashboard(Pageable pageable){

        List<String> activeStates = new ArrayList<>();
        activeStates.add(ProductionOrderState.CREATED.getValue());
        activeStates.add(ProductionOrderState.RELEASED.getValue());
        List<ProductionOrder> activeOrders = productionOrderService.findByProductionOrderStateInOrderByCreateTimeAsc(activeStates);

        // 2. 설비별 작업 수 카운팅 (Map 활용)
        Map<String, Long> taskCountMap = new HashMap<>();
        for (ProductionOrder order : activeOrders) {
            String eqName = order.getEquipmentName();
            if (eqName != null) {
                taskCountMap.put(eqName, taskCountMap.getOrDefault(eqName, 0L) + 1);
            }
        }

        // 3. 설비군(Group) 페이징 조회
        List<EquipmentGroup> equipmentGroupList = equipmentGroupRepository.findAll();
        List<EquipmentGroupDashboard> dashboardList = new ArrayList<>();

        // 4. 모든 설비 및 설비정의 조회 (데이터가 적으므로 메모리 로드)
        List<Equipment> allEquipmentList = equipmentRepository.findAll();
        List<EquipmentDef> allEquipmentDefList = equipmentDefRepository.findAll();

        // 5. 데이터 조립 (계층 구조 생성)
        for (EquipmentGroup group : equipmentGroupList) {
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


        long totalCount = equipmentGroupList.stream().count();
        // 3. PageImpl을 사용하여 수동으로 생성하여 반환
        return new PageImpl<>(dashboardList, pageable, totalCount);
    }

}