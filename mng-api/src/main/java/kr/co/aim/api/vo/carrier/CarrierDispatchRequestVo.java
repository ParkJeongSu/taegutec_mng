package kr.co.aim.api.vo.carrier;

import kr.co.aim.common.enums.PortTransportState;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.model.Equipment;
import kr.co.aim.domain.model.EquipmentDef;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.model.PortDef;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class CarrierDispatchRequestVo {
    private final Port port;
    private final PortDef portDef;
    private final EquipmentDef equipmentDef;
    private final Equipment equipment;
}