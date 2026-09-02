package kr.co.aim.api.context;

import kr.co.aim.api.service.*;
import kr.co.aim.common.format.CarrierDispatchRequestBody;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.EquipmentDefRepository;
import kr.co.aim.domain.repository.EquipmentRepository;
import kr.co.aim.domain.repository.PortDefRepository;
import kr.co.aim.domain.repository.PortRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class WhatDispatchContextFactory {

    private final EquipmentDefRepository equipmentDefRepository;
    private final EquipmentRepository equipmentRepository;
    private final PortDefRepository portDefRepository;
    private final PortRepository portRepository;

    public WhatDispatchContext createContext(CarrierDispatchRequestBody body) {
        String equipmentName = body.getEquipmentName();
        String portName = body.getPortName();

        // 2. Equipment 및 EquipmentDef 조회
        Optional<EquipmentDef> optionalEquipmentDef = equipmentDefRepository.findByEquipmentName(equipmentName);
        if (optionalEquipmentDef.isEmpty()) {
            log.error("Equipment Def not found: {}", equipmentName);
            throw new IllegalArgumentException("Equipment Def not found");
        }
        EquipmentDef equipmentDef = optionalEquipmentDef.get();

        Optional<Equipment> optionalEquipment = equipmentRepository.findByEquipmentName(equipmentName);
        if (optionalEquipment.isEmpty()) {
            log.error("Equipment not found: {}", equipmentName);
            throw new IllegalArgumentException("Equipment not found");
        }
        Equipment equipment = optionalEquipment.get();

        // 3. Port 및 PortDef 조회
        Optional<PortDef> optionalPortDef = portDefRepository.findByEquipmentNameAndPortName(equipmentName, portName);
        if (optionalPortDef.isEmpty()) {
            log.error("Port Def not found: {} - {}", equipmentName, portName);
            throw new IllegalArgumentException("Port Def not found");
        }
        PortDef portDef = optionalPortDef.get();

        Optional<Port> optionalPort = portRepository.findByEquipmentNameAndPortName(equipmentName, portName);
        if (optionalPort.isEmpty()) {
            log.error("Port not found: {} - {}", equipmentName, portName);
            throw new IllegalArgumentException("Port not found");
        }
        Port port = optionalPort.get();

        // 4. 조회된 데이터로 Context 생성 후 반환
        return WhatDispatchContext.builder()
                .equipmentDef(equipmentDef)
                .equipment(equipment)
                .portDef(portDef)
                .port(port)
                .build();
    }
}