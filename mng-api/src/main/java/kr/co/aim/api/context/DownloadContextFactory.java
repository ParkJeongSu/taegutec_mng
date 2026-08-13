package kr.co.aim.api.context;

import kr.co.aim.api.service.*;
import kr.co.aim.common.format.LoadCompletedBody;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DownloadContextFactory {

    private final CarrierService carrierService;
    private final CarrierDefService carrierDefService;
    private final EquipmentDefService equipmentDefService;
    private final EquipmentService equipmentService;
    private final PortDefService portDefService;
    private final PortService portService;

    public DownloadContext createContext(TransactionInfo transactionInfo, LoadCompletedBody body) {
        String equipmentName = body.getEquipmentName();
        String portName = body.getPortName();
        String carrierName = body.getCarrierName();

        // 2. Equipment 및 EquipmentDef 조회
        Optional<EquipmentDef> optionalEquipmentDef = equipmentDefService.findEquipmentDefByEquipmentName(equipmentName);
        if (optionalEquipmentDef.isEmpty()) {
            log.error("Equipment Def not found: {}", equipmentName);
            throw new IllegalArgumentException("Equipment Def not found");
        }
        EquipmentDef equipmentDef = optionalEquipmentDef.get();

        Optional<Equipment> optionalEquipment = equipmentService.findEquipmentByEquipmentName(equipmentName);
        if (optionalEquipment.isEmpty()) {
            log.error("Equipment not found: {}", equipmentName);
            throw new IllegalArgumentException("Equipment not found");
        }
        Equipment equipment = optionalEquipment.get();

        // 3. Port 및 PortDef 조회
        Optional<PortDef> optionalPortDef = portDefService.findPortDefByEquipmentNameAndPortName(equipmentName, portName);
        if (optionalPortDef.isEmpty()) {
            log.error("Port Def not found: {} - {}", equipmentName, portName);
            throw new IllegalArgumentException("Port Def not found");
        }
        PortDef portDef = optionalPortDef.get();

        Optional<Port> optionalPort = portService.findPortByEquipmentNameAndPortName(equipmentName, portName);
        if (optionalPort.isEmpty()) {
            log.error("Port not found: {} - {}", equipmentName, portName);
            throw new IllegalArgumentException("Port not found");
        }
        Port port = optionalPort.get();

        Optional<Carrier> optionalCarrier = carrierService.findByCarrierName(carrierName);
        if (optionalCarrier.isEmpty()) {
            log.error("Carrier not found: {} - {}", carrierName, carrierName);
            throw new IllegalArgumentException("Carrier not found");
        }
        Carrier carrier = optionalCarrier.get();

        Optional<CarrierDef> optionalCarrierDef = carrierDefService.findByCarrierDefName(carrier.getCarrierDefName());
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