package kr.co.aim.api.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.service.*;
import kr.co.aim.api.vo.powder.ops.H2TransPReportVo;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.Utils.JsonUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.OrderCreateRequestBody;
import kr.co.aim.common.format.ProductionOrderBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.ProductionOrderCreateCommand;
import kr.co.aim.domain.model.ProductDef;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.infra.config.RabbitConfig;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderDPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderMPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.H2PartMPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.IdocPEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("scheduler")
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PowderTransferScheduler {

    private final PowderExternalInterfaceService powderExternalInterfaceService;
    private final ProductionOrderService productionOrderService;
    private final ProductDefService productDefService;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final JsonUtils jsonUtils;

    @Scheduled(fixedDelay = 5000) // 5초마다 실행
    @SchedulerLock(name = "powderOrderDB2ToMSSQL",
            lockAtMostFor = "PT2M",     // 작업 최장 소요시간 + 버퍼
            lockAtLeastFor = "PT5S")    // 최소 간격(선택)
    public void powderOrderDB2ToMSSQL() {
        // 1단계 DB2에서 idocList를 가져온다. (DB2 트랜잭션)

        // 값이 존재한다면, 반복문 그리고 try catch 문

        // 2단계 h2orderMList와 h2orderDList를 조회해서 가져온다. (DB2 트랜잭션)

        // 3단계 ProductionOrder 에 해당 데이터를 생성 한다. (MSSQL 트랜잭션)

        // 4단계 정상적으로 MSSQL이 수행되었다면, Idoc의 errorCode와 dtimemode를 수정한다.

        // 5단계 특정 메시지타입의 경우 해당 시스템으로 전송
        // MATERIAL_ISSUE > WMS

        // 6단계 errorCode와 dtimemode를 수정하고,
        List<IdocPEntity> idocEntities = powderExternalInterfaceService.findByStateAndErrorCode(IdocState.INITIAL.getValue(),IdocErrorCode.INIT.getValue());

        if(CollectionUtils.isEmpty(idocEntities)){
            return;
        }
        TransactionInfo transactionInfo = TransactionInfo.now(EventName.TRANSFER.getValue(), SystemName.MNG.getValue(), "");
        for(IdocPEntity idocEntity : idocEntities){

            try {
                // Part
                if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.PART.getCode())){
                    createPart(idocEntity);
                }
                else{
                    // H2OrderMPEntity 조회 및 List<H2OrderDPEntity> 조회 by IdocId

                    // List<H2OrderDPEntity> 갯수만큼 Production Order 생성

                    // 특정 타입의 경우 각 시스템별로 Message 전송

                    H2OrderMPEntity h2OrderMEntity = powderExternalInterfaceService.selectH2OrderMEntityByIdocId(idocEntity.getLineId());
                    List<H2OrderDPEntity> h2OrderDEntities = powderExternalInterfaceService.selectH2OrderDEntityByIdocId(idocEntity.getLineId());
                    for(H2OrderDPEntity  h2OrderDPEntity : h2OrderDEntities){
                        String productionOrderType = "";
                        if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.MATERIAL_INBOUND.getCode())){
                            // 원자재 입고
                            productionOrderType = ProductionOrderType.MATERIAL_INBOUND.getValue();
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.OUTBOUND.getCode())){
                            // 출하
                            productionOrderType = ProductionOrderType.OUTBOUND.getValue();
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.UNPACKING.getCode())){
                            // 해포
                            productionOrderType = ProductionOrderType.UNPACKING.getValue();
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.PRODUCTION_ISSUE.getCode())){
                            // MATERIAL_ISSUE
                            productionOrderType = ProductionOrderType.PRODUCTION_ISSUE.getValue();
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.PRODUCTION.getCode())){
                            // 조업
                            productionOrderType = ProductionOrderType.PRODUCTION.getValue();
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.RRN_REPLY.getCode())){
                            // RRN_REPLY
                            productionOrderType = ProductionOrderType.RRN_REPLY.getValue();
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.ENTER_TO_STOCK.getCode())){
                            // ENTER_TO_STOCK
                            productionOrderType = ProductionOrderType.ENTER_TO_STOCK.getValue();
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.PACKING_ISSUE.getCode())){
                            // PACKING_ISSUE
                            productionOrderType = ProductionOrderType.PACKING_ISSUE.getValue();
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.PACKING.getCode())){
                            // PACKING
                            productionOrderType = ProductionOrderType.PACKING.getValue();
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.CHANGE_RRN.getCode())){
                            // CHANGE_RRN
                            productionOrderType = ProductionOrderType.CHANGE_RRN.getValue();
                        }
                        // Production Order 생성
                        ProductionOrderCreateCommand command =
                                ProductionOrderCreateCommand
                                        .builder()
                                        .orderId(h2OrderDPEntity.getCOrderId())
                                        .orderLineNumber(h2OrderDPEntity.getRrn().toString())
                                        .lotName(h2OrderDPEntity.getLot().toString())
                                        //.description()
                                        .itemName(h2OrderDPEntity.getCPartId())
                                        //.recipeName()
                                        //.carrierName()
                                        .idocId(idocEntity.getLineId())
                                        .h2OrderDpLineId(h2OrderDPEntity.getLineId())
                                        .galKey(h2OrderDPEntity.getGalKey())
                                        .productionOrderType(productionOrderType)
                                        .productionOrderState(ProductionOrderState.CREATED.getValue())
                                        //.reportState()
                                        .holdState(HoldState.NOT_ON_HOLD.getValue())
                                        //.reasonCode()
                                        .equipmentName(h2OrderDPEntity.getMachine())
                                        .planQuantity(h2OrderDPEntity.getQty())
                                        .releasedQuantity(BigDecimal.ZERO)
                                        .startedQuantity(BigDecimal.ZERO)
                                        .endedQuantity(BigDecimal.ZERO)
                                        .scrappedQuantity(BigDecimal.ZERO)
                                        .createTime(transactionInfo.eventTime())
                                        //.releaseTime()
                                        //.completeTime()
                                        //.validationTime()
                                        .createUser(SystemName.MNG.getValue())
                                        //.releaseUser()
                                        //.completeUser()
                                        //.dueDate()
                                        //.eventName()
                                        //.eventTime()
                                        //.eventUser()
                                        //.eventComment()
                                        .transactionInfo(transactionInfo)
                                        .build();

                        ProductionOrder productionOrder = ProductionOrder.create(command);

                        productionOrder = productionOrderService.createProductionOrder(productionOrder);

                        // ProductionOrder 에 대한 Validation
                        sendProductionOrderValidationToPEX(transactionInfo,productionOrder);

                        //필요한 경우 각 시스템에 order 전송
                        if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.MATERIAL_INBOUND.getCode())) {
                            // 원자재 입고
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.OUTBOUND.getCode())){
                            // 출하
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.UNPACKING.getCode())){
                            // 해포
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.PRODUCTION_ISSUE.getCode())){
                            // MATERIAL_ISSUE
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.PRODUCTION.getCode())){
                            // 조업
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.RRN_REPLY.getCode())){
                            // RRN_REPLY
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.ENTER_TO_STOCK.getCode())){
                            // ENTER_TO_STOCK
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.PACKING_ISSUE.getCode())){
                            // PACKING_ISSUE
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.PACKING.getCode())){
                            // PACKING
                        }
                        else if(Objects.equals(idocEntity.getIdocTypId(), ProductionOrderType.CHANGE_RRN.getCode())){
                            // CHANGE_RRN
                        }

                    }

                }

                // 정상적으로 수행했기 때문에, errorCode (60 : processed)와 dtimemode를 수정
                powderExternalInterfaceService.transferCompleted(idocEntity.getLineId());

            } catch (Exception e) {
                // 만일 transfer 도중 문제가 생겼다면
                // errorcode 99 dtimemode를 수정
                powderExternalInterfaceService.transferFail(idocEntity.getLineId());
            }
        }

    }

    private void createPart(IdocPEntity idocEntity) {
        // List PartM 조회
        Pageable pageable = Pageable.unpaged();
        Page<H2PartMPEntity> partMPEntities = powderExternalInterfaceService.getPartList(idocEntity.getLineId(),pageable);
        List<H2PartMPEntity> h2PartMPEntities = partMPEntities.getContent();
        // product_def 조회

        // 데이터 있으면 변경
        // 데이터 없으면 생성
        // n개의 데이터 erp 생성(4) 수정(230) 보고
        for(H2PartMPEntity h2PartMP : h2PartMPEntities){
            Optional<ProductDef> optionalProductDef = productDefService.findByH2PartMPEntity(h2PartMP);
            ProductDef productDef = null;
            if(optionalProductDef.isEmpty()){
                // 생성 보고 (4)
                productDef = productDefService.createProductDef(h2PartMP);
                H2TransPReportVo vo =
                        H2TransPReportVo
                                .builder()
                                .status(GALProductionStatus.CREATED_PART_MASTER)
                                .cPartId(h2PartMP.getCPartId())
                                .build();
                powderExternalInterfaceService.reportPartMPCreated(vo);

            }else {
                // 생성 보고 (230)
                productDef = productDefService.createProductDef(h2PartMP);
                H2TransPReportVo vo =
                        H2TransPReportVo
                                .builder()
                                .status(GALProductionStatus.CHANGED_PART_MASTER)
                                .cPartId(h2PartMP.getCPartId())
                                .build();
                powderExternalInterfaceService.reportPartMPModified(vo);
            }
        }
    }

    private void sendProductionOrderValidationToPEX(TransactionInfo transactionInfo,ProductionOrder productionOrder){
        // 메시지 전송
        String transactionId = FormatUtils.getTransactionId(transactionInfo.eventTime());

        BaseMessage<ProductionOrderBody> request = new BaseMessage<>();
        request.setMessageFrom(SystemName.MNG.getValue());
        request.setMessageOwner(SystemName.MNG.getValue());
        request.setMessageTo(SystemName.MNG.getValue());
        request.setEventTime(transactionId);
        request.setResultMessage("");
        request.setResultCode(ResultCode.OK.getValue());
        request.setTransactionId(transactionId);
        request.setMessageName(MessageList.PRODUCTION_ORDER_VALIDATION_REQUEST.getMessageName());
        ProductionOrderBody order =
                ProductionOrderBody
                        .builder()
                        .id(productionOrder.getId())
                        .orderId(productionOrder.getOrderId())
                        .orderLineNumber(productionOrder.getOrderLineNumber())
                        .lotName(productionOrder.getLotName())
                        .description(productionOrder.getDescription())
                        .itemName(productionOrder.getItemName())
                        .recipeName(productionOrder.getRecipeName())
                        .carrierName(productionOrder.getCarrierName())
                        .idocId(productionOrder.getIdocId())
                        .h2OrderDpLineId(productionOrder.getH2OrderDpLineId())
                        .galKey(productionOrder.getGalKey())
                        .productionOrderType(productionOrder.getProductionOrderType())
                        .productionOrderState(productionOrder.getProductionOrderState())
                        .reportState(productionOrder.getReportState())
                        .holdState(productionOrder.getHoldState())
                        .reasonCode(productionOrder.getReasonCode())
                        .equipmentName(productionOrder.getEquipmentName())
                        .planQuantity(productionOrder.getPlanQuantity())
                        .releasedQuantity(productionOrder.getReleasedQuantity())
                        .startedQuantity(productionOrder.getStartedQuantity())
                        .endedQuantity(productionOrder.getEndedQuantity())
                        .scrappedQuantity(productionOrder.getScrappedQuantity())
                        .createTime(productionOrder.getCreateTime())
                        .releaseTime(productionOrder.getReleaseTime())
                        .completeTime(productionOrder.getCompleteTime())
                        .validationTime(productionOrder.getValidationTime())
                        .createUser(productionOrder.getCreateUser())
                        .releaseUser(productionOrder.getReleaseUser())
                        .completeUser(productionOrder.getCompleteUser())
                        .dueDate(productionOrder.getDueDate())
                        .eventName(productionOrder.getEventName())
                        .eventTime(productionOrder.getEventTime())
                        .eventUser(productionOrder.getEventUser())
                        .eventComment(productionOrder.getEventComment())
                        .build();

        request.setBody(order);
        jsonUtils.writePrettyJson(request);

        // 5. String 으로 변환된 메시지 reply
        rabbitTemplate.convertAndSend( RabbitConfig.EXCHANGE_PEX,RabbitConfig.ROUTING_PEX, request );
        log.info("Send Completed");
    }

    private void sendToWMS(TransactionInfo transactionInfo,ProductionOrder productionOrder){
        // 메시지 전송
        String transactionId = FormatUtils.getTransactionId(transactionInfo.eventTime());

        BaseMessage<OrderCreateRequestBody> request = new BaseMessage<>();
        request.setMessageFrom(SystemName.MNG.getValue());
        request.setMessageOwner(SystemName.MNG.getValue());
        request.setMessageTo(SystemName.MNG.getValue());
        request.setEventTime(transactionId);
        request.setResultMessage("");
        request.setResultCode(ResultCode.OK.getValue());
        request.setTransactionId(transactionId);
        request.setMessageName(MessageList.TRANSPORT_ORDER_REQUEST.getMessageName());
        ProductionOrderBody order =
                ProductionOrderBody
                        .builder()
                        .id(productionOrder.getId())
                        .orderId(productionOrder.getOrderId())
                        .orderLineNumber(productionOrder.getOrderLineNumber())
                        .lotName(productionOrder.getLotName())
                        .description(productionOrder.getDescription())
                        .itemName(productionOrder.getItemName())
                        .recipeName(productionOrder.getRecipeName())
                        .carrierName(productionOrder.getCarrierName())
                        .idocId(productionOrder.getIdocId())
                        .h2OrderDpLineId(productionOrder.getH2OrderDpLineId())
                        .galKey(productionOrder.getGalKey())
                        .productionOrderType(productionOrder.getProductionOrderType())
                        .productionOrderState(productionOrder.getProductionOrderState())
                        .reportState(productionOrder.getReportState())
                        .holdState(productionOrder.getHoldState())
                        .reasonCode(productionOrder.getReasonCode())
                        .equipmentName(productionOrder.getEquipmentName())
                        .planQuantity(productionOrder.getPlanQuantity())
                        .releasedQuantity(productionOrder.getReleasedQuantity())
                        .startedQuantity(productionOrder.getStartedQuantity())
                        .endedQuantity(productionOrder.getEndedQuantity())
                        .scrappedQuantity(productionOrder.getScrappedQuantity())
                        .createTime(productionOrder.getCreateTime())
                        .releaseTime(productionOrder.getReleaseTime())
                        .completeTime(productionOrder.getCompleteTime())
                        .validationTime(productionOrder.getValidationTime())
                        .createUser(productionOrder.getCreateUser())
                        .releaseUser(productionOrder.getReleaseUser())
                        .completeUser(productionOrder.getCompleteUser())
                        .dueDate(productionOrder.getDueDate())
                        .eventName(productionOrder.getEventName())
                        .eventTime(productionOrder.getEventTime())
                        .eventUser(productionOrder.getEventUser())
                        .eventComment(productionOrder.getEventComment())
                        .build();

        request.getBody().getOrderList().add(order);
        jsonUtils.writePrettyJson(request);

        // 5. String 으로 변환된 메시지 reply
        rabbitTemplate.convertAndSend( RabbitConfig.EXCHANGE_WMS,RabbitConfig.ROUTING_WMS, request );
        log.info("Send Completed");
    }


}