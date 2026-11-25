package kr.co.aim.api.strategy.impl.order.process;

import kr.co.aim.api.service.*;
import kr.co.aim.api.strategy.ProductionOrderProcessStrategy;
import kr.co.aim.api.strategy.SelectStrategy;
import kr.co.aim.api.context.ProductionOrderProcessContext;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.AllocatedCommand;
import kr.co.aim.domain.command.LotGroupSplitCommand;
import kr.co.aim.domain.model.LotGroup;
import kr.co.aim.domain.model.Lot;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.domain.repository.LotRepository;
import kr.co.aim.domain.repository.LotGroupRepository;
import kr.co.aim.domain.repository.ProductionOrderRepository;
import kr.co.aim.infra.persistence.entity.LotHistoryEntity;
import kr.co.aim.infra.persistence.entity.LotGroupHistoryEntity;
import kr.co.aim.infra.persistence.mapper.LotMapper;
import kr.co.aim.infra.persistence.mapper.LotGroupMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessProductionIssueTypeStrategy implements ProductionOrderProcessStrategy {

    private final LotRepository lotRepository;
    private final List<SelectStrategy> selectStrategyList;
    private final CarrierSelectionService carrierSelectionService;
    private final LotMapper lotMapper;
    private final HistoryService historyService;
    private final ProductionOrderService productionOrderService;
    private final ProductionOrderRepository productionOrderRepository;
    private final LotGroupRepository lotGroupRepository;
    private final LotGroupMapper lotGroupMapper;

    @Override
    public boolean supports(ProductionOrderProcessContext context) {
        return context.getProductionOrder().getProductionOrderType().equals(ProductionOrderType.PRODUCTION_ISSUE.getValue());
    }

    @Override
    public void productionOrderProcess(ProductionOrderProcessContext context) {
        // 1. ProductionOrder 조회
        ProductionOrder productionOrder = context.getProductionOrder();

        // 원자재 Lot 정보 조회
        Optional<LotGroup> optionalLot = lotGroupRepository.findByLotGroupName(productionOrder.getMaterialLotName());
        if(optionalLot.isEmpty()){
            throw new RuntimeException("lot not found");
        }
        LotGroup materialLot = optionalLot.get();

        SelectStrategy targetStrategy = null;
        for (SelectStrategy strategy : selectStrategyList) {
            if (strategy.supports(productionOrder)) {
                targetStrategy = strategy;
                break; // 적합한 전략을 찾았으므로 루프 탈출
            }
        }

        // 조건에 맞는 전략을 찾지 못한 경우 예외 처리
        if (targetStrategy == null) {
            throw new IllegalArgumentException("No dispatch strategy found for context");
        }

        // 3. 할당 가능한 Lot 목록 조회 (Inbound 시간순/생성순 정렬 데이터)
        List<Lot> availableMappings = targetStrategy.selectAvailableCarrier(productionOrder);

        if (CollectionUtils.isEmpty(availableMappings)) {
            log.warn("No available Lots found for OrderId: {}, LineNo: {}", productionOrder.getOrderId(), productionOrder.getOrderLineNumber());
            return;
        }

        // 4. DP 서비스를 통한 캐리어 최적 조합 선택
        List<Lot> selectedMappings = carrierSelectionService.selectBestCarriersByGalQuantity(availableMappings,productionOrder.getPlanQuantity());

        // 5. 선택된 캐리어 및 오더 상태 변경
        // Lot을 새롭게 생성하고, 해당 Lot 을 새로운 order와 orderLineNumber : 0 으로 변경
        if (CollectionUtils.isNotEmpty(selectedMappings)) {
            TransactionInfo transactionInfo = TransactionInfo.now(EventName.ALLOCATE.getValue(), SystemName.MNG.getValue(), "Carrier Allocated by DP Knapsack");

            // Lot Split
            LotGroupSplitCommand lotSplitCommand =
                    LotGroupSplitCommand
                            .builder()
                            .transactionInfo(transactionInfo)
                            .lotGroupName(productionOrder.getLotName())
                            .originalLotGroupName(productionOrder.getMaterialLotName())
                            .lotGroupStatus(LotStatus.WIP.getValue())
                            .itemId(productionOrder.getItemName())
                            .splitQuantity(productionOrder.getPlanQuantity())
                            .holdState(HoldState.NOT_ON_HOLD.getValue())
                            .build();
            LotGroup newLot = materialLot.split(lotSplitCommand);
            materialLot = lotGroupRepository.save(materialLot);
            LotGroupHistoryEntity materialLotHistoryEntity = lotGroupMapper.toHistoryEntity(materialLot);
            historyService.saveHistory(materialLotHistoryEntity);
            newLot = lotGroupRepository.save(newLot);
            LotGroupHistoryEntity  newLotHistoryEntity = lotGroupMapper.toHistoryEntity(newLot);
            historyService.saveHistory(newLotHistoryEntity);

            // Lot 정보 변경
            // TODO : 동일한 mngKey 값으로 변경하고 nextRRN 요청
            int seq = 1;
            for (Lot mapping : selectedMappings) {
                AllocatedCommand command =
                        AllocatedCommand
                                .builder()
                                .orderId(productionOrder.getGalOrderId())
                                .orderLineNumber("0")
                                .productionOrderId(productionOrder.getId())
                                .seq(seq++)
                                .productionStatus(ProductionStatus.WAIT.getValue())
                                .build();
                mapping.allocated(command);
                mapping = lotRepository.save(mapping);
                LotHistoryEntity historyEntity = lotMapper.toHistoryEntity(mapping);
                historyService.saveHistory(historyEntity);
            }

            // ProductionOrder 상태를 COMPLETED로 변경
            Optional<ProductionOrder> optionalProductionOrder = productionOrderService.updateOrderState(transactionInfo,productionOrder.getId(), ProductionOrderState.COMPLETED.getValue());

            log.info("Successfully allocated {} carriers for ProductionOrder ID: {}", selectedMappings.size(), productionOrder.getId());
        } else {
            log.warn("Failed to allocate carriers for ProductionOrder ID: {}. Holding allocation.", productionOrder.getId());
            // 조합 실패 시 오더 상태 원복 또는 별도 에러 처리 진행
        }
    }
}