package kr.co.aim.api.strategy.impl.process.ended;

import kr.co.aim.api.service.HistoryService;
import kr.co.aim.api.service.LotService;
import kr.co.aim.api.service.LotGroupService;
import kr.co.aim.api.service.ProductionOrderService;
import kr.co.aim.api.strategy.ProcessJobEndedStrategy;
import kr.co.aim.api.context.ProcessJobEndedContext;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.LotCreateCommand;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.LotRepository;
import kr.co.aim.infra.persistence.entity.LotHistoryEntity;
import kr.co.aim.infra.persistence.mapper.LotMapper;
import kr.co.aim.infra.persistence.mapper.LotGroupMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class IncomeTypeEndedStrategy implements ProcessJobEndedStrategy {

    private final HistoryService historyService;
    private final LotRepository lotRepository;
    private final LotMapper lotMapper;


    @Override
    public boolean supports(ProcessJobEndedContext context) {
        EquipmentDef equipmentDef = context.getEquipmentDef();
        // 해포 설비
        return StringUtils.equals(EquipmentDetailType.INCOME.getValue(), equipmentDef.getDetailEquipmentType());
    }

    @Override
    public void processJobEnded(ProcessJobEndedContext context) {

        EquipmentDef equipmentDef = context.getEquipmentDef();
        Equipment equipment = context.getEquipment();
        PortDef portDef = context.getPortDef();
        Port port = context.getPort();
        ProductionOrder productionOrder = context.getProductionOrder();
        String carrierName = context.getCarrierName();
        String recipeName = context.getRecipeName();
        BigDecimal quantity = context.getQuantity();
        String lastFlag = context.getLastFlag();
        TransactionInfo tx = context.getTx();
        String lotName = productionOrder.getLotName();


        Long mngKey = TsidUtils.nextId();
        LotCreateCommand command =
                LotCreateCommand
                        .builder()
                        .transactionInfo(tx)
                        .lotName(productionOrder.getLotName())
                        .carrierName(carrierName)
                        .orderId(productionOrder.getOrderId())
                        .orderLineNumber(productionOrder.getOrderLineNumber())
                        .productionOrderId(productionOrder.getId())
                        .productionStatus(ProductionStatus.WAIT.getValue())
                        .processStatus(ProcessStatus.COMPLETED.getValue())
                        .quantity(quantity)
                        .jobEndTime(tx.eventTime())
                        .rrnRequestState(RRNRequestState.REQUESTED.getValue())
                        .rrnRequestTime(tx.eventTime())
                        .mngKey(mngKey)
                        .rrnReplyTime(null)
                        .holdState(HoldState.NOT_ON_HOLD.getValue())
                        .build();

        Lot lot = Lot.create(command);
        lot = lotRepository.save(lot);
        LotHistoryEntity historyEntity = lotMapper.toHistoryEntity(lot);
        historyService.saveHistory(historyEntity);
    }
}