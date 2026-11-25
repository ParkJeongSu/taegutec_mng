package kr.co.aim.api.strategy.impl.process.ended;

import kr.co.aim.api.service.HistoryService;
import kr.co.aim.api.service.LotService;
import kr.co.aim.api.service.LotGroupService;
import kr.co.aim.api.service.ProductionOrderService;
import kr.co.aim.api.strategy.ProcessJobEndedStrategy;
import kr.co.aim.api.context.ProcessJobEndedContext;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.MngKeyName;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.LotGroupChangeCommand;
import kr.co.aim.domain.command.ProcessJobEndedCommand;
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
public class NormalTypeEndedStrategy implements ProcessJobEndedStrategy {

    private final HistoryService historyService;
    private final LotRepository lotRepository;
    private final LotMapper lotMapper;
    private final LotGroupRepository lotGroupRepository;
    private final LotGroupMapper lotGroupMapper;


    @Override
    public boolean supports(ProcessJobEndedContext context) {
        EquipmentDef equipmentDef = context.getEquipmentDef();
        // 일반 공정 설비에 대한 로직
        if(StringUtils.equals(EquipmentDetailType.REDUCTION.getValue(),equipmentDef.getDetailEquipmentType())){
            // 환원로 설비
            return false;
        }
        else if(StringUtils.equals(EquipmentDetailType.INCOME.getValue(), equipmentDef.getDetailEquipmentType())){
            // 해포 설비
            return false;
        }
        else {
            // 일반 조업 설비
            return true;
        }
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
        String itemName = productionOrder.getItemName();
        List<MngKeyName> mngKeyNameList = context.getMngKeyNameList();

        // 일반 조업 설비
        String mngKeyName = mngKeyNameList.get(0).getMngKeyName();
        Long mngKey = Long.parseLong(mngKeyName);
        List<Lot> lotList = lotRepository.findByMngKey(mngKey);
        if( ObjectUtils.isNotEmpty(lotList) ){
            Lot lot = lotList.get(0);
            ProcessJobEndedCommand command =
                    ProcessJobEndedCommand
                            .builder()
                            .transactionInfo(tx)
                            .equipmentName(equipment.getEquipmentName())
                            .recipeName(recipeName)
                            .lotName(lotName)
                            .itemName(itemName)
                            .carrierName(carrierName)
                            .orderId(productionOrder.getOrderId())
                            .orderLineNumber(productionOrder.getOrderLineNumber())
                            .productionTaskEnd(productionOrder.getId().toString())
                            .productionOrderId(lot.getProductionOrderId())
                            .productionStatus(ProductionStatus.WAIT.getValue())
                            .processStatus(ProcessStatus.COMPLETED.getValue())
                            .quantity(quantity)
                            .mngKey(mngKey)
                            .jobEndTime(tx.eventTime())
                            .build();
            lot.processJobEnded(command);
            lot = lotRepository.save(lot);
            LotHistoryEntity historyEntity = lotMapper.toHistoryEntity(lot);
            historyService.saveHistory(historyEntity);
        }

        if(StringUtils.equals(YN.Y.getValue(),lastFlag)){
            // 해포 설비가 아닐 경우만
            // find by LotName Lot
            // find by LotName Lot
            // Lot totalQuantity 변경
            List<Lot> lotListByLotName = lotRepository.findByLotNameAndProductionStatusNot(lotName,ProductionStatus.CONSUMED.getValue());
            if(ObjectUtils.isNotEmpty(lotListByLotName)){
                BigDecimal totalQuantity = BigDecimal.ZERO;
                for(Lot lcm : lotListByLotName){
                    totalQuantity = totalQuantity.add(lcm.getQuantity());
                }

                Optional<LotGroup> optionalLot = lotGroupRepository.findByLotGroupName(lotName);
                if(optionalLot.isPresent()){
                    LotGroup lotGroup = optionalLot.get();
                    LotGroupChangeCommand command =
                            LotGroupChangeCommand
                                    .builder()
                                    .transactionInfo(tx)
                                    .totalQuantity(totalQuantity)
                                    .build();
                    lotGroup.change(command);
                    lotGroup = lotGroupRepository.save(lotGroup);
                    LotGroupHistoryEntity lotGroupHistoryEntity = lotGroupMapper.toHistoryEntity(lotGroup);
                    historyService.saveHistory(lotGroupHistoryEntity);

                }
            }
        }

    }
}