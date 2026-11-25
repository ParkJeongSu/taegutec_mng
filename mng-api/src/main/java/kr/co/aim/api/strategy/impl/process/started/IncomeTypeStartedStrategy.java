package kr.co.aim.api.strategy.impl.process.started;

import kr.co.aim.api.service.HistoryService;
import kr.co.aim.api.service.LotService;
import kr.co.aim.api.service.ProductionOrderService;
import kr.co.aim.api.strategy.ProcessJobStartedStrategy;
import kr.co.aim.api.context.ProcessJobStartedContext;
import kr.co.aim.common.enums.EquipmentDetailType;
import kr.co.aim.common.enums.ProcessStatus;
import kr.co.aim.common.enums.ProductionStatus;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.ProcessJobStartedCommand;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.LotRepository;
import kr.co.aim.infra.persistence.entity.LotHistoryEntity;
import kr.co.aim.infra.persistence.mapper.LotMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class IncomeTypeStartedStrategy implements ProcessJobStartedStrategy {

    private final HistoryService historyService;
    private final LotRepository lotRepository;
    private final LotMapper lotMapper;

    @Override
    public boolean supports(ProcessJobStartedContext context) {

        EquipmentDef equipmentDef = context.getEquipmentDef();

        if(StringUtils.equals(EquipmentDetailType.REDUCTION.getValue(),equipmentDef.getDetailEquipmentType())){
            // 환원로 설비
            return false;
        }
        else if(StringUtils.equals(EquipmentDetailType.INCOME.getValue(), equipmentDef.getDetailEquipmentType())){
            // 해포 설비
            return true;
        }
        else {
            // 일반 조업 설비
            return false;
        }
    }

    @Override
    public void processJobStarted(ProcessJobStartedContext context) {

        Lot lot =  context.getLot();
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
                        .mngKey( lot.getMngKey())
                        .jobStartTime(tx.eventTime())
                        .build();
        lot.processJobStarted(command);
        lot = lotRepository.save(lot);
        LotHistoryEntity historyEntity = lotMapper.toHistoryEntity(lot);
        historyService.saveHistory(historyEntity);

    }
}