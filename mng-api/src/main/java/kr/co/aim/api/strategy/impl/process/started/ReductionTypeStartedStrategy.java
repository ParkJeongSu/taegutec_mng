package kr.co.aim.api.strategy.impl.process.started;

import kr.co.aim.api.service.HistoryService;
import kr.co.aim.api.service.LotCarrierMappingService;
import kr.co.aim.api.service.ProductionOrderService;
import kr.co.aim.api.strategy.ProcessJobStartedStrategy;
import kr.co.aim.api.context.ProcessJobStartedContext;
import kr.co.aim.common.enums.EquipmentDetailType;
import kr.co.aim.common.enums.ProcessStatus;
import kr.co.aim.common.enums.ProductionStatus;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.ProcessJobStartedCommand;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.LotCarrierMappingRepository;
import kr.co.aim.infra.persistence.entity.LotCarrierMappingHistoryEntity;
import kr.co.aim.infra.persistence.mapper.LotCarrierMappingMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ReductionTypeStartedStrategy implements ProcessJobStartedStrategy {

    private final HistoryService historyService;
    private final LotCarrierMappingRepository lotCarrierMappingRepository;
    private final LotCarrierMappingMapper lotCarrierMappingMapper;

    @Override
    public boolean supports(ProcessJobStartedContext context) {

        EquipmentDef equipmentDef = context.getEquipmentDef();

        if(StringUtils.equals(EquipmentDetailType.REDUCTION.getValue(),equipmentDef.getDetailEquipmentType())){
            // 환원로 설비
            return true;
        }
        else if(StringUtils.equals(EquipmentDetailType.INCOME.getValue(), equipmentDef.getDetailEquipmentType())){
            // 해포 설비
            return false;
        }
        else {
            // 일반 조업 설비
            return false;
        }
    }

    @Override
    public void processJobStarted(ProcessJobStartedContext context) {

        LotCarrierMapping lotCarrierMapping =  context.getLotCarrierMapping();
        TransactionInfo tx = context.getTx();

        EquipmentDef equipmentDef = context.getEquipmentDef();
        Equipment equipment = context.getEquipment();
        PortDef portDef = context.getPortDef();
        Port port = context.getPort();
        ProductionOrder productionOrder = context.getProductionOrder();
        String recipeName = context.getRecipeName();
        String carrierName = context.getCarrierName();
        BigDecimal quantity = context.getQuantity();


        String productionStatus = ProductionStatus.CONSUMED.getValue();

        ProcessJobStartedCommand command =
                ProcessJobStartedCommand
                        .builder()
                        .transactionInfo(tx)
                        .equipmentName(equipment.getEquipmentName())
                        .recipeName(recipeName)
                        .lotName(productionOrder.getLotName())
                        .itemName(productionOrder.getItemName())
                        .carrierName(carrierName)
                        .orderId(productionOrder.getOrderId())
                        .orderLineNumber(productionOrder.getOrderLineNumber())
                        .productionOrderId( productionOrder.getId())
                        .productionStatus(productionStatus)
                        .processStatus(ProcessStatus.RUN.getValue())
                        .quantity(quantity)
                        .mngKey( lotCarrierMapping.getMngKey())
                        .jobStartTime(tx.eventTime())
                        .build();
        lotCarrierMapping.processJobStarted(command);
        lotCarrierMapping = lotCarrierMappingRepository.save(lotCarrierMapping);
        LotCarrierMappingHistoryEntity historyEntity = lotCarrierMappingMapper.toHistoryEntity(lotCarrierMapping);
        historyService.saveHistory(historyEntity);

    }
}