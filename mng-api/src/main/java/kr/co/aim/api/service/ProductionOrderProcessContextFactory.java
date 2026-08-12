package kr.co.aim.api.service;

import kr.co.aim.api.vo.powder.ops.ProductionOrderProcessContext;
import kr.co.aim.common.format.ProductionOrderProcessRequestBody;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductionOrderProcessContextFactory {


    private final ProductionOrderService productionOrderService;

    public ProductionOrderProcessContext createContext(TransactionInfo transactionInfo, ProductionOrderProcessRequestBody body) {

        Long id = body.getId();
        Optional<ProductionOrder> optionalProductionOrder = productionOrderService.findById(id);
        if(optionalProductionOrder.isEmpty()){
            log.error("production order not found {} ",id);
            throw new RuntimeException("production order not found");
        }
        ProductionOrder productionOrder = optionalProductionOrder.get();

        // 4. 조회된 데이터로 Context 생성 후 반환
        return ProductionOrderProcessContext
                .builder()
                .productionOrder(productionOrder)
                .tx(transactionInfo)
                .build();
    }
}