package kr.co.aim.api.service;

import kr.co.aim.common.condition.*;
import kr.co.aim.common.enums.ProductionOrderState;
import kr.co.aim.domain.model.Carrier;
import kr.co.aim.domain.model.CarrierHistory;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.domain.model.ProductionOrderSummary;
import kr.co.aim.domain.repository.ProductionOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler","web"})
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class ProductionOrderService {
    private final CarrierService carrierService;
    private final ProductionOrderRepository productionOrderRepository;

    @Transactional(readOnly = true) // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public List<ProductionOrder> findActiveProductionOrderList(String equipmentName) {
        List<String> productionOrderStateList = new ArrayList<>();
        productionOrderStateList.add(ProductionOrderState.REQUESTED.getValue());
        productionOrderStateList.add(ProductionOrderState.RELEASED.getValue());
        return productionOrderRepository.findByEquipmentNameAndProductionOrderStateInOrderByCreateTimeAsc(
                equipmentName,
                productionOrderStateList
        );
    }

    @Transactional(readOnly = true) // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public List<ProductionOrder> findNewProductionOrderList(String equipmentName) {
        List<String> productionOrderStateList = new ArrayList<>();
        productionOrderStateList.add(ProductionOrderState.CREATED.getValue());
        return productionOrderRepository.findByEquipmentNameAndProductionOrderStateInOrderByCreateTimeAsc(
                equipmentName,
                productionOrderStateList
        );
    }

    @Transactional(readOnly = true) // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public Page<ProductionOrderSummary> findProductionOrderSummaryByCondition(ProductionOrderSummarySearchCondition condition, Pageable pageable) {
        return productionOrderRepository.findProductionOrderSummaryByCondition(condition,pageable);
    }

    @Transactional(readOnly = true) // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public Page<ProductionOrder> findProductionOrderByCondition(ProductionOrderSearchByOrderId condition, Pageable pageable) {
        return productionOrderRepository.findProductionOrderByCondition(condition,pageable);
    }

    @Transactional(readOnly = true) // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public Page<ProductionOrder> findProductionOrderHistoryByCondition(ProductionOrderHistorySearchCondition condition, Pageable pageable) {
        return null;
    }

    @Transactional(readOnly = true) // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public Page<Carrier> findCarrierByCondition(ProductionOrderSearchOrderIdAndOrderLineNumber condition, Pageable pageable) {

        String orderId = condition.getOrderId();
        String orderLineNumber = condition.getOrderLineNumber();
        String eventName = "A"; // TODO : 쏟아 부은 EVENT_NAME으로 변경

        // 3. 통합 리스트 생성 및 변환 (Entity -> Domain)
        List<Carrier> totalList = new ArrayList<>();

//        List<CarrierHistory> carrierHistoryList = carrierService.findByOrderIdAndOrderLineNumberAndEventName(orderId,orderLineNumber,eventName);
//        List<Carrier> historyToCarrierList = carrierHistoryList.stream().map(Carrier::fromHistory).collect(Collectors.toList());
//        List<Carrier> carrierList = carrierService.findByOrderIdAndOrderLineNumber(orderId,orderLineNumber);
//
//        for(Carrier carrier  : carrierList) {
//            totalList.add(carrier);
//        }
//
//        for(Carrier carrier  : historyToCarrierList) {
//            totalList.add(carrier);
//        }

        return new PageImpl<>(totalList, pageable, totalList.size());
    }


}