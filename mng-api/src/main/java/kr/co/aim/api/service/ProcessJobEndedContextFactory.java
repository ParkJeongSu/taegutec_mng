package kr.co.aim.api.service;

import kr.co.aim.api.vo.powder.ops.ProcessJobEndedContext;
import kr.co.aim.api.vo.powder.ops.ProcessJobStartedContext;
import kr.co.aim.common.format.MngKeyName;
import kr.co.aim.common.format.ProcessJobEndedBody;
import kr.co.aim.common.format.ProcessJobStartedBody;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessJobEndedContextFactory {

    private final EquipmentDefService equipmentDefService;
    private final EquipmentService equipmentService;
    private final PortDefService portDefService;
    private final PortService portService;
    private final LotCarrierMappingService lotCarrierMappingService;
    private final ProductionOrderService productionOrderService;

    public ProcessJobEndedContext createContext(TransactionInfo tx, ProcessJobEndedBody body) {
        String equipmentName = body.getEquipmentName();
        String portName = body.getPortName();
        String carrierName = body.getCarrierName();
        List<MngKeyName> mngKeyNameList =  body.getMngKeyList();
        String productionTaskId = body.getProductionTaskId();
        String recipeName = body.getRecipeName();
        String orderId = body.getOrderId();
        String orderLineNumber = body.getOrderLineNumber();
        BigDecimal quantity = body.getQuantity();
        String lotName = body.getLotName();
        String itemName = body.getItemName();


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

        // TODO: MNGKEY 이부분 해결하기
//        List<LotCarrierMapping> lotCarrierMappingList = lotCarrierMappingService.findByMngKey(mngkey);
//        if( ObjectUtils.isEmpty(lotCarrierMappingList)){
//            log.error("LotCarrierMapping not found: {}", mngkey);
//            throw new IllegalArgumentException("LotCarrierMapping not found");
//        }
//        LotCarrierMapping lotCarrierMapping = lotCarrierMappingList.get(0);

        Optional<ProductionOrder> optionalProductionOrder = productionOrderService.findById(Long.parseLong(productionTaskId));
        if (optionalProductionOrder.isEmpty()) {
            log.error("ProductionOrder not found: {}", productionTaskId);
            throw new IllegalArgumentException("ProductionOrder not found");
        }
        ProductionOrder productionOrder = optionalProductionOrder.get();


        // 4. 조회된 데이터로 Context 생성 후 반환
        return ProcessJobEndedContext.builder()
                .equipmentDef(equipmentDef)
                .equipment(equipment)
                .portDef(portDef)
                .port(port)
                //.lotCarrierMapping(lotCarrierMapping)
                .productionOrder(productionOrder)
                .carrierName(carrierName)
                .recipeName(recipeName)
                .quantity(quantity)
                .tx(tx)
                .build();
    }
}