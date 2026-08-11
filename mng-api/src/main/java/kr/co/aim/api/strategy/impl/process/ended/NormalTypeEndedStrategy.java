package kr.co.aim.api.strategy.impl.process.ended;

import kr.co.aim.api.service.HistoryService;
import kr.co.aim.api.service.LotCarrierMappingService;
import kr.co.aim.api.service.LotService;
import kr.co.aim.api.service.ProductionOrderService;
import kr.co.aim.api.strategy.ProcessJobEndedStrategy;
import kr.co.aim.api.vo.powder.ops.ProcessJobEndedContext;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.MngKeyName;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.LotChangeCommand;
import kr.co.aim.domain.command.ProcessJobEndedCommand;
import kr.co.aim.domain.model.*;
import kr.co.aim.infra.persistence.entity.LotCarrierMappingHistoryEntity;
import kr.co.aim.infra.persistence.entity.LotHistoryEntity;
import kr.co.aim.infra.persistence.mapper.LotCarrierMappingMapper;
import kr.co.aim.infra.persistence.mapper.LotMapper;
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
    private final ProductionOrderService productionOrderService;
    private final LotCarrierMappingService lotCarrierMappingService;
    private final LotCarrierMappingMapper lotCarrierMappingMapper;
    private final LotService lotService;
    private final LotMapper lotMapper;


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
        List<LotCarrierMapping> lotCarrierMappingList = lotCarrierMappingService.findByMngKey(mngKey);
        if( ObjectUtils.isNotEmpty(lotCarrierMappingList) ){
            LotCarrierMapping lotCarrierMapping = lotCarrierMappingList.get(0);
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
                            .productionOrderId(lotCarrierMapping.getProductionOrderId())
                            .productionStatus(ProductionStatus.WAIT.getValue())
                            .processStatus(ProcessStatus.COMPLETED.getValue())
                            .quantity(quantity)
                            .mngKey(mngKey)
                            .jobEndTime(tx.eventTime())
                            .build();
            lotCarrierMapping.processJobEnded(command);
            lotCarrierMapping = lotCarrierMappingService.save(lotCarrierMapping);
            LotCarrierMappingHistoryEntity historyEntity = lotCarrierMappingMapper.toHistoryEntity(lotCarrierMapping);
            historyService.saveHistory(historyEntity);
        }

        if(StringUtils.equals(YN.Y.getValue(),lastFlag)){
            // 해포 설비가 아닐 경우만
            // find by LotName LotCarrierMapping
            // find by LotName Lot
            // Lot totalQuantity 변경
            List<LotCarrierMapping> lotCarrierMappingListByLotName = lotCarrierMappingService.findByLotNameAndProductionStatusNot(lotName,ProductionStatus.CONSUMED.getValue());
            if(ObjectUtils.isNotEmpty(lotCarrierMappingListByLotName)){
                BigDecimal totalQuantity = BigDecimal.ZERO;
                for(LotCarrierMapping lcm : lotCarrierMappingListByLotName){
                    totalQuantity = totalQuantity.add(lcm.getQuantity());
                }

                Optional<Lot> optionalLot = lotService.findByLotName(lotName);
                if(optionalLot.isPresent()){
                    Lot lot = optionalLot.get();
                    LotChangeCommand command =
                            LotChangeCommand
                                    .builder()
                                    .transactionInfo(tx)
                                    .totalQuantity(totalQuantity)
                                    .build();
                    lot.change(command);
                    lot = lotService.save(lot);
                    LotHistoryEntity lotHistoryEntity = lotMapper.toHistoryEntity(lot);
                    historyService.saveHistory(lotHistoryEntity);

                }
            }
        }

    }
}