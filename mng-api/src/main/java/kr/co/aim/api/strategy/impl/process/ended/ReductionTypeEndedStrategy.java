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
import kr.co.aim.domain.command.LotGroupChangeCommand;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.LotRepository;
import kr.co.aim.domain.repository.LotGroupRepository;
import kr.co.aim.infra.persistence.entity.LotHistoryEntity;
import kr.co.aim.infra.persistence.entity.LotGroupHistoryEntity;
import kr.co.aim.infra.persistence.mapper.LotMapper;
import kr.co.aim.infra.persistence.mapper.LotGroupMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReductionTypeEndedStrategy implements ProcessJobEndedStrategy {

    private final HistoryService historyService;
    private final LotRepository lotRepository;
    private final LotMapper lotMapper;
    private final LotGroupRepository lotGroupRepository;
    private final LotGroupMapper lotGroupMapper;


    @Override
    public boolean supports(ProcessJobEndedContext context) {
        EquipmentDef equipmentDef = context.getEquipmentDef();
        // 환원로 설비
        return StringUtils.equals(EquipmentDetailType.REDUCTION.getValue(), equipmentDef.getDetailEquipmentType());
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

        if(StringUtils.equals(YN.Y.getValue(),lastFlag)){
            // 해포 설비가 아닐 경우만
            // find by LotName Lot
            // find by LotName Lot
            // Lot totalQuantity 변경
            List<Lot> lotList = lotRepository.findByLotNameAndProductionStatusNot(lotName,ProductionStatus.CONSUMED.getValue());
            if(ObjectUtils.isNotEmpty(lotList)){
                BigDecimal totalQuantity = BigDecimal.ZERO;

                for(Lot lcm : lotList){
                    totalQuantity = totalQuantity.add(lcm.getQuantity());
                }

                Optional<LotGroup> optionalLot = lotGroupRepository.findByLotGroupName(lotName);
                if(optionalLot.isPresent()){
                    LotGroup lotGroup = optionalLot.get();
                    LotGroupChangeCommand lotChangeCommand =
                            LotGroupChangeCommand
                                    .builder()
                                    .transactionInfo(tx)
                                    .totalQuantity(totalQuantity)
                                    .build();
                    lotGroup.change(lotChangeCommand);
                    lotGroup = lotGroupRepository.save(lotGroup);
                    LotGroupHistoryEntity lotGroupHistoryEntity = lotGroupMapper.toHistoryEntity(lotGroup);
                    historyService.saveHistory(lotGroupHistoryEntity);
                }
            }
        }

    }
}