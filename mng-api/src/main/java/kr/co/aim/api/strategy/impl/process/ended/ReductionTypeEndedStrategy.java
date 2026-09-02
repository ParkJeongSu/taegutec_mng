package kr.co.aim.api.strategy.impl.process.ended;

import kr.co.aim.api.service.HistoryService;
import kr.co.aim.api.service.LotCarrierMappingService;
import kr.co.aim.api.service.LotService;
import kr.co.aim.api.service.ProductionOrderService;
import kr.co.aim.api.strategy.ProcessJobEndedStrategy;
import kr.co.aim.api.context.ProcessJobEndedContext;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.LotCarrierMappingCreateCommand;
import kr.co.aim.domain.command.LotChangeCommand;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.LotCarrierMappingRepository;
import kr.co.aim.domain.repository.LotRepository;
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
public class ReductionTypeEndedStrategy implements ProcessJobEndedStrategy {

    private final HistoryService historyService;
    private final LotCarrierMappingRepository lotCarrierMappingRepository;
    private final LotCarrierMappingMapper lotCarrierMappingMapper;
    private final LotRepository lotRepository;
    private final LotMapper lotMapper;


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
        LotCarrierMappingCreateCommand command =
                LotCarrierMappingCreateCommand
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

        LotCarrierMapping lotCarrierMapping = LotCarrierMapping.create(command);
        lotCarrierMapping = lotCarrierMappingRepository.save(lotCarrierMapping);
        LotCarrierMappingHistoryEntity historyEntity = lotCarrierMappingMapper.toHistoryEntity(lotCarrierMapping);
        historyService.saveHistory(historyEntity);

        if(StringUtils.equals(YN.Y.getValue(),lastFlag)){
            // 해포 설비가 아닐 경우만
            // find by LotName LotCarrierMapping
            // find by LotName Lot
            // Lot totalQuantity 변경
            List<LotCarrierMapping> lotCarrierMappingList = lotCarrierMappingRepository.findByLotNameAndProductionStatusNot(lotName,ProductionStatus.CONSUMED.getValue());
            if(ObjectUtils.isNotEmpty(lotCarrierMappingList)){
                BigDecimal totalQuantity = BigDecimal.ZERO;

                for(LotCarrierMapping lcm : lotCarrierMappingList){
                    totalQuantity = totalQuantity.add(lcm.getQuantity());
                }

                Optional<Lot> optionalLot = lotRepository.findByLotName(lotName);
                if(optionalLot.isPresent()){
                    Lot lot = optionalLot.get();
                    LotChangeCommand lotChangeCommand =
                            LotChangeCommand
                                    .builder()
                                    .transactionInfo(tx)
                                    .totalQuantity(totalQuantity)
                                    .build();
                    lot.change(lotChangeCommand);
                    lot = lotRepository.save(lot);
                    LotHistoryEntity lotHistoryEntity = lotMapper.toHistoryEntity(lot);
                    historyService.saveHistory(lotHistoryEntity);
                }
            }
        }

    }
}