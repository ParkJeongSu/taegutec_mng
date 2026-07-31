package kr.co.aim.api.service;

import kr.co.aim.api.vo.powder.ops.WhereDispatchContext;
import kr.co.aim.common.format.DestinationDispatchRequestBody;
import kr.co.aim.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class WhereDispatchContextFactory {

    private final CarrierService carrierService;
    private final CarrierDefService carrierDefService;
    private final EquipmentDefService equipmentDefService;
    private final EquipmentService equipmentService;
    private final PortDefService portDefService;
    private final PortService portService;

    public WhereDispatchContext createContext(DestinationDispatchRequestBody body) {
        String equipmentName = body.getEquipmentName();
        String portName = body.getPortName();
        String carrierName = body.getCarrierName();

        // 1. Carrier 및 CarrierDef 조회
        Optional<Carrier> optionalCarrier = carrierService.findByCarrierName(carrierName);
        if (optionalCarrier.isEmpty()) {
            log.error("Carrier not found: {}", carrierName);
            throw new IllegalArgumentException("Carrier not found: " + carrierName);
        }
        Carrier carrier = optionalCarrier.get();

        Optional<CarrierDef> optionalCarrierDef = carrierDefService.findByCarrierDefName(carrier.getCarrierDefName());
        if (optionalCarrierDef.isEmpty()) {
            log.error("Carrier Def not found for Carrier: {}", carrierName);
            throw new IllegalArgumentException("Carrier Def not found");
        }
        CarrierDef carrierDef = optionalCarrierDef.get();

        // 2. Source Equipment 및 EquipmentDef 조회
        Optional<EquipmentDef> optionalSourceEquipmentDef = equipmentDefService.findEquipmentDefByEquipmentName(equipmentName);
        if (optionalSourceEquipmentDef.isEmpty()) {
            log.error("Source Equipment Def not found: {}", equipmentName);
            throw new IllegalArgumentException("Source Equipment Def not found");
        }
        EquipmentDef sourceEquipmentDef = optionalSourceEquipmentDef.get();

        Optional<Equipment> optionalSourceEquipment = equipmentService.findEquipmentByEquipmentName(equipmentName);
        if (optionalSourceEquipment.isEmpty()) {
            log.error("Source Equipment not found: {}", equipmentName);
            throw new IllegalArgumentException("Source Equipment not found");
        }
        Equipment sourceEquipment = optionalSourceEquipment.get();

        // 3. Source Port 및 PortDef 조회
        Optional<PortDef> optionalSourcePortDef = portDefService.findPortDefByEquipmentNameAndPortName(equipmentName, portName);
        if (optionalSourcePortDef.isEmpty()) {
            log.error("Source Port Def not found: {} - {}", equipmentName, portName);
            throw new IllegalArgumentException("Source Port Def not found");
        }
        PortDef sourcePortDef = optionalSourcePortDef.get();

        Optional<Port> optionalSourcePort = portService.findPortByEquipmentNameAndPortName(equipmentName, portName);
        if (optionalSourcePort.isEmpty()) {
            log.error("Source Port not found: {} - {}", equipmentName, portName);
            throw new IllegalArgumentException("Source Port not found");
        }
        Port sourcePort = optionalSourcePort.get();

        // 4. 조회된 데이터로 Context 생성 후 반환
        return WhereDispatchContext.builder()
                .carrier(carrier)
                .carrierDef(carrierDef)
                .sourceEquipmentDef(sourceEquipmentDef)
                .sourceEquipment(sourceEquipment)
                .sourcePortDef(sourcePortDef)
                .sourcePort(sourcePort)
                .build();
    }
}