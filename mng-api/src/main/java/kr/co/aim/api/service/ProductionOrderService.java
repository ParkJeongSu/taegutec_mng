package kr.co.aim.api.service;

import kr.co.aim.common.enums.ProductionOrderState;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.domain.repository.ProductionOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler"})
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class ProductionOrderService {

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


}