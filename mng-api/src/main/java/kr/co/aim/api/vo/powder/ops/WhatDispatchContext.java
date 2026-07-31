package kr.co.aim.api.vo.powder.ops;


import kr.co.aim.domain.model.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WhatDispatchContext {
    // Source 정보 (필수)
    private final EquipmentDef equipmentDef;
    private final Equipment equipment;
    private final PortDef portDef;
    private final Port port;

    // Carrier 정보
    private Carrier carrier;
    private CarrierDef carrierDef;

    public void assignCarrier(Carrier carrier, CarrierDef carrierDef) {
        this.carrier = carrier;
        this.carrierDef = carrierDef;
    }
}
