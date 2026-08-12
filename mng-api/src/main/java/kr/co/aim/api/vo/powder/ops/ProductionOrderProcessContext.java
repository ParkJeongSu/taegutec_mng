package kr.co.aim.api.vo.powder.ops;


import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.model.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductionOrderProcessContext {


    private final ProductionOrder productionOrder;
    private final TransactionInfo tx;


}
