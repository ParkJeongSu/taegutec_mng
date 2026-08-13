package kr.co.aim.api.context;


import kr.co.aim.common.format.MngKeyName;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.model.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProcessJobEndedContext {

    private final EquipmentDef equipmentDef;
    private final Equipment equipment;
    private final PortDef portDef;
    private final Port port;
    private final LotCarrierMapping lotCarrierMapping;
    private final ProductionOrder productionOrder;
    private final String carrierName;
    private final String recipeName;
    private final BigDecimal quantity;
    private final String lastFlag;
    private final List<MngKeyName> mngKeyNameList;
    private final TransactionInfo tx;


}
