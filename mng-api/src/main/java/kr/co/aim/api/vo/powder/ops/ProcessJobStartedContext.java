package kr.co.aim.api.vo.powder.ops;


import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.model.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProcessJobStartedContext {

    private final EquipmentDef equipmentDef;
    private final Equipment equipment;
    private final PortDef portDef;
    private final Port port;
    private final LotCarrierMapping lotCarrierMapping;
    private final ProductionOrder productionOrder;
    private final String carrierName;
    private final String recipeName;
    private final BigDecimal quantity;
    private final TransactionInfo tx;


}
