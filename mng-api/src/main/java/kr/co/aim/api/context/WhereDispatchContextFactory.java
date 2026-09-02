package kr.co.aim.api.context;

import kr.co.aim.api.service.*;
import kr.co.aim.common.format.DestinationDispatchRequestBody;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class WhereDispatchContextFactory {

    private final CarrierRepository carrierRepository;
    private final CarrierDefRepository carrierDefRepository;
    private final EquipmentDefRepository equipmentDefRepository;
    private final EquipmentRepository equipmentRepository;
    private final PortDefRepository portDefRepository;
    private final PortRepository portRepository;

    public WhereDispatchContext createContext(DestinationDispatchRequestBody body) {
        String equipmentName = body.getEquipmentName();
        String portName = body.getPortName();
        String carrierName = body.getCarrierName();

        // 1. Carrier 및 CarrierDef 조회
        Optional<Carrier> optionalCarrier = carrierRepository.findByCarrierName(carrierName);
        if (optionalCarrier.isEmpty()) {
            log.error("Carrier not found: {}", carrierName);
            throw new IllegalArgumentException("Carrier not found: " + carrierName);
        }
        Carrier carrier = optionalCarrier.get();

        Optional<CarrierDef> optionalCarrierDef = carrierDefRepository.findByCarrierDefName(carrier.getCarrierDefName());
        if (optionalCarrierDef.isEmpty()) {
            log.error("Carrier Def not found for Carrier: {}", carrierName);
            throw new IllegalArgumentException("Carrier Def not found");
        }
        CarrierDef carrierDef = optionalCarrierDef.get();

        // 2. Source Equipment 및 EquipmentDef 조회
        Optional<EquipmentDef> optionalSourceEquipmentDef = equipmentDefRepository.findByEquipmentName(equipmentName);
        if (optionalSourceEquipmentDef.isEmpty()) {
            log.error("Source Equipment Def not found: {}", equipmentName);
            throw new IllegalArgumentException("Source Equipment Def not found");
        }
        EquipmentDef sourceEquipmentDef = optionalSourceEquipmentDef.get();

        Optional<Equipment> optionalSourceEquipment = equipmentRepository.findByEquipmentName(equipmentName);
        if (optionalSourceEquipment.isEmpty()) {
            log.error("Source Equipment not found: {}", equipmentName);
            throw new IllegalArgumentException("Source Equipment not found");
        }
        Equipment sourceEquipment = optionalSourceEquipment.get();

        // 3. Source Port 및 PortDef 조회
        Optional<PortDef> optionalSourcePortDef = portDefRepository.findByEquipmentNameAndPortName(equipmentName, portName);
        if (optionalSourcePortDef.isEmpty()) {
            log.error("Source Port Def not found: {} - {}", equipmentName, portName);
            throw new IllegalArgumentException("Source Port Def not found");
        }
        PortDef sourcePortDef = optionalSourcePortDef.get();

        Optional<Port> optionalSourcePort = portRepository.findByEquipmentNameAndPortName(equipmentName, portName);
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