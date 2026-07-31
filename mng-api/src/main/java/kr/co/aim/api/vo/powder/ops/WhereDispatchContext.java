package kr.co.aim.api.vo.powder.ops;


import kr.co.aim.domain.model.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WhereDispatchContext {
    // Source 정보 (필수)
    private final Carrier carrier;
    private final CarrierDef carrierDef;
    private final EquipmentDef sourceEquipmentDef;
    private final Equipment sourceEquipment;
    private final PortDef sourcePortDef;
    private final Port sourcePort;

    // Target 정보 (Strategy에 의해 결정됨)
    private Equipment targetEquipment;
    private Port targetPort;
    private String targetZoneName;

    public void assignTarget(Equipment equipment, Port port, String zoneName) {
        this.targetEquipment = equipment;
        this.targetPort = port;
        this.targetZoneName = zoneName;
    }
}
