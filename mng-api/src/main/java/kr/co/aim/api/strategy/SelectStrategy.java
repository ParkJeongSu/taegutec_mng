package kr.co.aim.api.strategy;

import kr.co.aim.domain.model.Lot;
import kr.co.aim.domain.model.ProductionOrder;

import java.util.List;

public interface SelectStrategy {

    /**
     * 해당 전략을 적용할 수 있는 조건인지 판단
     */
    boolean supports(ProductionOrder productionOrder);

    /**
     * Order에 따른 available Carrier Select
     */
    List<Lot> selectAvailableCarrier(ProductionOrder productionOrder);
}
