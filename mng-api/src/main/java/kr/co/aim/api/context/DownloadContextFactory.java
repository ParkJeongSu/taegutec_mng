package kr.co.aim.api.context;

import kr.co.aim.api.service.*;
import kr.co.aim.common.format.LoadCompletedBody;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DownloadContextFactory {

    private final CarrierRepository carrierRepository;
    private final CarrierDefRepository carrierDefRepository;
    private final EquipmentDefRepository equipmentDefRepository;
    private final EquipmentRepository equipmentRepository;
    private final PortDefRepository portDefRepository;
    private final PortRepository portRepository;

    public DownloadContext createContext(TransactionInfo transactionInfo, LoadCompletedBody body) {
        String equipmentName = body.getEquipmentName();
        String portName = body.getPortName();
        String carrierName = body.getCarrierName();

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

        Optional<Carrier> optionalCarrier = carrierRepository.findByCarrierName(carrierName);
        if (optionalCarrier.isEmpty()) {
            log.error("Carrier not found: {} - {}", carrierName, carrierName);
            throw new IllegalArgumentException("Carrier not found");
        }
        Carrier carrier = optionalCarrier.get();

        Optional<CarrierDef> optionalCarrierDef = carrierDefRepository.findByCarrierDefName(carrier.getCarrierDefName());
        if (optionalCarrierDef.isEmpty()) {
            log.error("Carrier Def not found: {} - {}", carrierName, carrierName);
            throw new IllegalArgumentException("Carrier Def not found");
        }
        CarrierDef carrierDef = optionalCarrierDef.get();

        // 4. 조회된 데이터로 Context 생성 후 반환
        return DownloadContext
                .builder()
                .equipmentDef(equipmentDef)
                .equipment(equipment)
                .portDef(portDef)
                .port(port)
                .carrierDef(carrierDef)
                .carrier(carrier)
                .tx(transactionInfo)
                .build();
    }
}