package kr.co.aim.api.service;

import kr.co.aim.api.context.ProcessJobEndedContextFactory;
import kr.co.aim.api.context.ProcessJobStartedContextFactory;
import kr.co.aim.api.strategy.FactoryIfEventQueueStrategy;
import kr.co.aim.api.strategy.ProcessJobEndedStrategy;
import kr.co.aim.api.strategy.ProcessJobStartedStrategy;
import kr.co.aim.api.vo.powder.ops.PowderEventQueueReportVo;
import kr.co.aim.api.context.ProcessJobEndedContext;
import kr.co.aim.api.context.ProcessJobStartedContext;
import kr.co.aim.common.enums.ProcessStatus;
import kr.co.aim.common.format.MngKeyName;
import kr.co.aim.common.format.ProcessJobEndedBody;
import kr.co.aim.common.format.ProcessJobStartedBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.ProcessJobStartedCommand;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.domain.repository.CarrierDefRepository;
import kr.co.aim.domain.repository.CarrierRepository;
import kr.co.aim.infra.persistence.entity.ProductionOrderHistoryEntity;
import kr.co.aim.infra.persistence.mapper.LotCarrierMappingMapper;
import kr.co.aim.infra.persistence.mapper.ProductionOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class ProcessJobService {

    private final CarrierDefRepository carrierDefRepository;
    private final CarrierRepository carrierRepository;
    private final EquipmentDefService equipmentDefService;
    private final EquipmentService equipmentService;
    private final LotCarrierMappingService lotCarrierMappingService;
    private final LotCarrierMappingMapper lotCarrierMappingMapper;
    private final ProductionOrderService productionOrderService;
    private final ProcessJobStartedContextFactory processJobStartedContextFactory;
    private final ProcessJobEndedContextFactory processJobEndedContextFactory;
    private final List<ProcessJobStartedStrategy> processJobStartedStrategy;
    private final List<ProcessJobEndedStrategy> processJobEndedStrategy;
    private final FactoryIfEventQueueStrategy factoryIfEventQueueStrategy;
    private final ProductionOrderMapper productionOrderMapper;
    private final HistoryService historyService;


    @Transactional(value = "mssqlTransactionManager")
    public void processJobStarted(BaseMessage<ProcessJobStartedBody> message) {
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String productionTaskId = message.getBody().getProductionTaskId();
        String recipeName = message.getBody().getRecipeName();
        String orderId = message.getBody().getOrderId();
        String orderLineNumber = message.getBody().getOrderLineNumber();
        BigDecimal quantity = message.getBody().getQuantity();
        String mngKey = message.getBody().getMngKey();
        String lotName = message.getBody().getLotName();
        String itemName = message.getBody().getItemName();

        TransactionInfo tx = TransactionInfo.now(messageName,equipmentName,resultMessage);
        // 1. 요청 검증 및 DispatchContext 조회 (Guard Clause 캡슐화)
        ProcessJobStartedContext context = processJobStartedContextFactory.createContext(tx,message.getBody());

        // 2. 적합한 Strategy 탐색 및 목적지 결정
        ProcessJobStartedStrategy targetStrategy = null;
        for (ProcessJobStartedStrategy strategy : processJobStartedStrategy) {
            if (strategy.supports(context)) {
                targetStrategy = strategy;
                break; // 적합한 전략을 찾았으므로 루프 탈출
            }
        }

        // 조건에 맞는 전략을 찾지 못한 경우 예외 처리
        if (targetStrategy == null) {
            throw new IllegalArgumentException("No dispatch strategy found for context");
        }

        targetStrategy.processJobStarted(context);

        // ProductionOrder 수량 데이터 변경
        ProductionOrder productionOrder = context.getProductionOrder();
        ProcessJobStartedCommand command =
                ProcessJobStartedCommand
                .builder()
                .transactionInfo(tx)
                .equipmentName(equipmentName)
                .recipeName(recipeName)
                .lotName(lotName)
                .itemName(itemName)
                .carrierName(carrierName)
                .orderId(orderId)
                .orderLineNumber(orderLineNumber)
                .productionOrderId(Long.parseLong(productionTaskId))
                .processStatus(ProcessStatus.RUN.getValue())
                .quantity(quantity)
                .jobStartTime(tx.eventTime())
                .build();
        productionOrder.processJobStarted(command);
        productionOrder = productionOrderService.save(productionOrder);
        ProductionOrderHistoryEntity productionOrderHistoryEntity = productionOrderMapper.toHistoryEntity(productionOrder);
        historyService.saveHistory(productionOrderHistoryEntity);

        // powder EventQueue
        try{
            PowderEventQueueReportVo powderEventQueueReportVo
                    = PowderEventQueueReportVo
                    .builder()
                    .messageName(messageName)
                    .equipmentDef(context.getEquipmentDef())
                    .equipment(context.getEquipment())
                    .carrierName(carrierName)
                    .tx(tx)
                    .build();
            factoryIfEventQueueStrategy.enqueueIfEventQueue(powderEventQueueReportVo);
        }
        catch(Exception e){
            log.error("EventQueue enqueue error",e);
        }
    }

    @Transactional(value = "mssqlTransactionManager")
    public void processJobEnded(BaseMessage<ProcessJobEndedBody> message) {

        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String productionTaskId = message.getBody().getProductionTaskId();
        String productionTaskEnd = message.getBody().getLastFlag();
        String recipeName = message.getBody().getRecipeName();
        String orderId = message.getBody().getOrderId();
        String orderLineNumber = message.getBody().getOrderLineNumber();
        BigDecimal quantity = message.getBody().getQuantity();
        List<MngKeyName> mngKeyNameList = message.getBody().getMngKeyList();
        String lotName = message.getBody().getLotName();
        String itemName = message.getBody().getItemName();
        Long mngKey = null;

        TransactionInfo tx = TransactionInfo.now(messageName,equipmentName,resultMessage);
        // 1. 요청 검증 및 DispatchContext 조회 (Guard Clause 캡슐화)
        ProcessJobEndedContext context = processJobEndedContextFactory.createContext(tx,message.getBody());

        // 2. 적합한 Strategy 탐색 및 목적지 결정
        ProcessJobEndedStrategy targetStrategy = null;
        for (ProcessJobEndedStrategy strategy : processJobEndedStrategy) {
            if (strategy.supports(context)) {
                targetStrategy = strategy;
                break; // 적합한 전략을 찾았으므로 루프 탈출
            }
        }

        // 조건에 맞는 전략을 찾지 못한 경우 예외 처리
        if (targetStrategy == null) {
            throw new IllegalArgumentException("No dispatch strategy found for context");
        }

        targetStrategy.processJobEnded(context);

        // TODO : GAL 조업 완료 보고 이때, 주의할 점은 해포 설비와 조업 설비 TC 코드 다름 주의 완료 보고, 그리고 next rrn 요청 보고
        // powder EventQueue
        try{
            PowderEventQueueReportVo powderEventQueueReportVo
                    = PowderEventQueueReportVo
                    .builder()
                    .messageName(messageName)
                    .equipmentDef(context.getEquipmentDef())
                    .equipment(context.getEquipment())
                    .carrierName(carrierName)
                    .productionOrder(context.getProductionOrder())
                    .mngKey(mngKey)
                    .tx(tx)
                    .build();
            factoryIfEventQueueStrategy.enqueueIfEventQueue(powderEventQueueReportVo);
        }
        catch(Exception e){
            log.error("EventQueue enqueue error",e);
        }
    }
}