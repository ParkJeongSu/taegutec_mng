package kr.co.aim.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.dto.powder.IfEventQueueDto;
import kr.co.aim.api.strategy.FactoryIfEventQueueStrategy;
import kr.co.aim.api.vo.powder.ops.PowderEventQueueReportVo;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.IfEventQueueCreateCommand;
import kr.co.aim.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@Profile({"pex","tex","scheduler","web"})
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PowderIfEventQueueService implements FactoryIfEventQueueStrategy {

    private final ObjectMapper objectMapper;
    private final IfEventQueueService ifEventQueueService;

    @Override
    @Transactional(value = "mssqlTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void enqueueIfEventQueue(Object vo) {
        // Java 17의 Pattern Matching 사용
        if (vo instanceof PowderEventQueueReportVo reportVo) {
            // save EventLog로 변경
            List<IfEventQueueDto> ifEventQueueDtoList = createEventQueueDto(reportVo);
            if(CollectionUtils.isNotEmpty(ifEventQueueDtoList)){
                for(IfEventQueueDto dto : ifEventQueueDtoList){
                    TransactionInfo tx = TransactionInfo.now(EventName.SAVE_INTERFACE_EVENT_LOG.getValue(),SystemName.MNG.getValue(), "");
                    // DTO 객체를 JSON 문자열로 직접 변환합니다.
                    String jsonPayload = "";
                    try {
                        //jsonPayload = objectMapper.writeValueAsString(dto);
                        jsonPayload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
                    } catch (JsonProcessingException e) {
                        log.error("dto -> String error");
                        // 로깅 및 예외 처리
                        throw new RuntimeException("InterfaceEventLogDto를 JSON으로 변환하는 중 오류가 발생했습니다.", e);
                    }
                    log.info("Sending JSON Payload: {}", jsonPayload);
                    IfEventQueueCreateCommand command =
                            IfEventQueueCreateCommand
                                    .builder()
                                    .transactionInfo(tx)
                                    .eventType(dto.getEventType())
                                    .payload(jsonPayload)
                                    .ifStatus(IfEventQueueState.READY.getValue())
                                    .carrierName(dto.getCarrierName())
                                    .idocId(dto.getIdocId())
                                    .orderId(dto.getOrderId())
                                    .orderLineNumber(dto.getOrderLineNumber())
                                    .retryCNT(0)
                                    .errMSG("")
                                    .createTime(tx.eventTime())
                                    .build();
                    IfEventQueue interfaceEventLog = IfEventQueue.create(command);
                    ifEventQueueService.save(interfaceEventLog);
                }
            }
        }else {
            log.error("잘못된 객체 타입이 전달되었습니다: {}", vo != null ? vo.getClass().getName() : "null");
        }

    }

    private List<IfEventQueueDto> createEventQueueDto(PowderEventQueueReportVo vo) {
        List<IfEventQueueDto> ifEventQueueDtoList = new ArrayList<>();
        String messageName = vo.getMessageName();
        if (StringUtils.equals(MessageList.LOAD_COMPLETE.getMessageName(), messageName)) {
            List<IfEventQueueDto> result = handleLoadCompleted(vo);
            if(ObjectUtils.isNotEmpty(result)){
                ifEventQueueDtoList.addAll(result);
            }
        }
        else if(StringUtils.equals(MessageList.PRODUCTION_ORDER_VALIDATION_REQUEST.getMessageName(), messageName)){
            List<IfEventQueueDto> result = handleProductionOrderValidation(vo);
            if(ObjectUtils.isNotEmpty(result)){
                ifEventQueueDtoList.addAll(result);
            }
        }
        else if(StringUtils.equals(MessageList.MATERIAL_INBOUND_START.getMessageName(), messageName)){
            List<IfEventQueueDto> result = handleMaterialInboundStart(vo);
            if(ObjectUtils.isNotEmpty(result)){
                ifEventQueueDtoList.addAll(result);
            }
        }
        else if(StringUtils.equals(MessageList.BAG_ON_PALLET.getMessageName(), messageName)){
            List<IfEventQueueDto> result = handleBagOnPallet(vo);
            if(ObjectUtils.isNotEmpty(result)){
                ifEventQueueDtoList.addAll(result);
            }
        }
        else if(StringUtils.equals(MessageList.TRANSPORT_JOB_STARTED.getMessageName(), messageName)){
            List<IfEventQueueDto> result = handleTransportJobStarted(vo);
            if(ObjectUtils.isNotEmpty(result)){
                ifEventQueueDtoList.addAll(result);
            }
        }
        else if(StringUtils.equals(MessageList.TRANSPORT_JOB_COMPLETED.getMessageName(), messageName)){
            List<IfEventQueueDto> result = handleTransportJobCompleted(vo);
            if(ObjectUtils.isNotEmpty(result)){
                ifEventQueueDtoList.addAll(result);
            }
        }
        else if(StringUtils.equals(MessageList.PROCESS_JOB_STARTED.getMessageName(), messageName)){
            List<IfEventQueueDto> result = handleProcessJobStarted(vo);
            if(ObjectUtils.isNotEmpty(result)){
                ifEventQueueDtoList.addAll(result);
            }
        }
        else if(StringUtils.equals(MessageList.PROCESS_JOB_ENDED.getMessageName(), messageName)){
            List<IfEventQueueDto> result = handleProcessJobEnded(vo);
            if(ObjectUtils.isNotEmpty(result)){
                ifEventQueueDtoList.addAll(result);
            }
        }

        return ifEventQueueDtoList;
    }

    private List<IfEventQueueDto> handleProductionOrderValidation(PowderEventQueueReportVo vo){
        List<IfEventQueueDto>  ifEventQueueDtoList = new ArrayList<>();
        String messageName = vo.getMessageName();
        ProductionOrder productionOrder = vo.getProductionOrder();
        TransactionInfo tx = vo.getTx();
        String resultCode = vo.getResultCode();

        if(ObjectUtils.isEmpty(productionOrder)){
            throw new RuntimeException("production Order is null");
        }
        if(StringUtils.equals(productionOrder.getProductionOrderType(),ProductionOrderType.RRN_REPLY.getValue())){
            return ifEventQueueDtoList;
        }

        String eventType = "";
        String transactionCode = "";

        if(StringUtils.equals(resultCode,ResultCode.OK.getValue())){
            eventType = GALProductionStatus.ACCEPT.name();
            transactionCode = GALProductionStatus.ACCEPT.getValue();
        }else {
            eventType = GALProductionStatus.ERROR.name();
            transactionCode = GALProductionStatus.ERROR.getValue();
        }

        IfEventQueueDto dto = IfEventQueueDto
                .builder()
                .productionOrderId(productionOrder.getId().toString())
                .messageName(messageName)
                .eventType(eventType)
                .transactionCode(transactionCode)
                .idocId(productionOrder.getIdocId().toString())
                .orderId(productionOrder.getOrderId())
                .orderLineNumber(productionOrder.getOrderLineNumber())
                .quantity(productionOrder.getPlanQuantity())
                .orderType(productionOrder.getProductionOrderType())
                .galKey(productionOrder.getGalKey())
                .lotName(productionOrder.getLotName())
                .itemName(productionOrder.getItemName())
                .resultStatus("")
                .errorReason("")
                .build();

        ifEventQueueDtoList.add(dto);
        return  ifEventQueueDtoList;
    }

    private List<IfEventQueueDto> handleMaterialInboundStart(PowderEventQueueReportVo vo){
        List<IfEventQueueDto>  ifEventQueueDtoList = new ArrayList<>();
        String messageName = vo.getMessageName();
        ProductionOrder productionOrder = vo.getProductionOrder();
        TransactionInfo tx = vo.getTx();

        if(ObjectUtils.isEmpty(productionOrder)){
            throw new RuntimeException("production Order is null");
        }

        String eventType = GALProductionStatus.RELEASE.name();
        String transactionCode = GALProductionStatus.RELEASE.getValue();;

        IfEventQueueDto dto = IfEventQueueDto
                .builder()
                .productionOrderId(productionOrder.getId().toString())
                .messageName(messageName)
                .eventType(eventType)
                .transactionCode(transactionCode)
                .idocId(productionOrder.getIdocId().toString())
                .orderId(productionOrder.getOrderId())
                .orderLineNumber(productionOrder.getOrderLineNumber())
                .quantity(productionOrder.getPlanQuantity())
                .orderType(productionOrder.getProductionOrderType())
                .galKey(productionOrder.getGalKey())
                .lotName(productionOrder.getLotName())
                .itemName(productionOrder.getItemName())
                .resultStatus("")
                .errorReason("")
                .build();

        ifEventQueueDtoList.add(dto);
        return  ifEventQueueDtoList;
    }

    private List<IfEventQueueDto> handleBagOnPallet(PowderEventQueueReportVo vo){
        List<IfEventQueueDto>  ifEventQueueDtoList = new ArrayList<>();
        String messageName = vo.getMessageName();
        ProductionOrder productionOrder = vo.getProductionOrder();
        TransactionInfo tx = vo.getTx();

        if(ObjectUtils.isEmpty(productionOrder)){
            throw new RuntimeException("production Order is null");
        }

        String eventType = GALProductionStatus.FIBC_ON_PALLET.name();
        String transactionCode = GALProductionStatus.FIBC_ON_PALLET.getValue();;

        IfEventQueueDto dto = IfEventQueueDto
                .builder()
                .productionOrderId(productionOrder.getId().toString())
                .messageName(messageName)
                .eventType(eventType)
                .transactionCode(transactionCode)
                .idocId(productionOrder.getIdocId().toString())
                .orderId(productionOrder.getOrderId())
                .orderLineNumber(productionOrder.getOrderLineNumber())
                .quantity(productionOrder.getPlanQuantity())
                .orderType(productionOrder.getProductionOrderType())
                .galKey(productionOrder.getGalKey())
                .lotName(productionOrder.getLotName())
                .itemName(productionOrder.getItemName())
                .resultStatus("")
                .errorReason("")
                .build();

        ifEventQueueDtoList.add(dto);
        return  ifEventQueueDtoList;
    }

    private List<IfEventQueueDto> handleLoadCompleted(PowderEventQueueReportVo vo){
        List<IfEventQueueDto>  ifEventQueueDtoList = new ArrayList<>();
        // TODO: 비지니스 로직 추가
        return  ifEventQueueDtoList;
    }

    private List<IfEventQueueDto> handleProcessJobStarted(PowderEventQueueReportVo vo){
        List<IfEventQueueDto>  ifEventQueueDtoList = new ArrayList<>();

        String messageName = vo.getMessageName();
        TransactionInfo tx = vo.getTx();
        EquipmentDef equipmentDef = vo.getEquipmentDef();
        Equipment equipment = vo.getEquipment();
        PortDef portDef = vo.getPortDef();
        Port port = vo.getPort();
        ProductionOrder productionOrder = vo.getProductionOrder();
        String carrierName = vo.getCarrierName();
        String orderType = vo.getOrderType();
        String resultCode = vo.getResultCode();
        String resultMessage = vo.getResultMessage();
        String recipeName = vo.getRecipeName();
        String lotName = vo.getLotName();
        String itemName = vo.getItemName();
        String productionStatus = vo.getProductionStatus();
        String processStatus = vo.getProcessStatus();
        BigDecimal quantity = vo.getQuantity();
        BigDecimal scrapQuantity = vo.getScrapQuantity();
        Long mngKey = vo.getMngKey();


        String eventType = "";
        String transactionCode = "";

        if(ObjectUtils.isEmpty(equipmentDef)){
            return ifEventQueueDtoList;
        }
        if(StringUtils.equals(EquipmentDetailType.INCOME.getValue(),equipmentDef.getDetailEquipmentType())){
            eventType = GALProductionStatus.UNPACK_STARTED.name();
            transactionCode = GALProductionStatus.UNPACK_STARTED.getValue();
        }
        else {
            eventType = GALProductionStatus.PRODUCTION_STARTED.name();
            transactionCode = GALProductionStatus.PRODUCTION_STARTED.getValue();
        }

        String idocId = "";
        String orderId = "";
        String orderLineNumber = "";
        String productionOrderId = "";
        String galKey = "";

        if(ObjectUtils.isNotEmpty(productionOrder)){
            idocId = productionOrder.getIdocId().toString();
            productionOrderId = productionOrder.getId().toString();
            orderId = productionOrder.getOrderId();
            orderLineNumber = productionOrder.getOrderLineNumber();
            orderType = productionOrder.getProductionOrderType();
            galKey = productionOrder.getGalKey();
        }

        IfEventQueueDto dto = IfEventQueueDto
                .builder()
                .productionOrderId(productionOrderId)
                .messageName(messageName)
                .eventType(eventType)
                .transactionCode(transactionCode)
                .carrierName(carrierName)
                .idocId(idocId)
                .orderId(orderId)
                .orderLineNumber(orderLineNumber)
                .quantity(quantity)
                //.missQuantity()
                //.scrapQuantity()
                .orderType(orderType)
                .galKey(galKey)
                .lotName(lotName)
                .itemName(itemName)
                .mngKey(mngKey.toString())
                .resultStatus("")
                .errorReason("")
                .build();

        ifEventQueueDtoList.add(dto);


        return  ifEventQueueDtoList;
    }

    private List<IfEventQueueDto> handleProcessJobEnded(PowderEventQueueReportVo vo){
        List<IfEventQueueDto>  ifEventQueueDtoList = new ArrayList<>();
        String messageName = vo.getMessageName();
        TransactionInfo tx = vo.getTx();
        EquipmentDef equipmentDef = vo.getEquipmentDef();
        Equipment equipment = vo.getEquipment();
        PortDef portDef = vo.getPortDef();
        Port port = vo.getPort();
        ProductionOrder productionOrder = vo.getProductionOrder();
        String carrierName = vo.getCarrierName();
        String orderType = vo.getOrderType();
        String resultCode = vo.getResultCode();
        String resultMessage = vo.getResultMessage();
        String recipeName = vo.getRecipeName();
        String lotName = vo.getLotName();
        String itemName = vo.getItemName();
        String productionStatus = vo.getProductionStatus();
        String processStatus = vo.getProcessStatus();
        BigDecimal quantity = vo.getQuantity();
        BigDecimal scrapQuantity = vo.getScrapQuantity();
        BigDecimal missQuantity = vo.getMissQuantity();
        Long mngKey = vo.getMngKey();


        String eventType = "";
        String transactionCode = "";

        if(ObjectUtils.isEmpty(equipmentDef)){
            return ifEventQueueDtoList;
        }
        if(StringUtils.equals(EquipmentDetailType.INCOME.getValue(),equipmentDef.getDetailEquipmentType())){
            eventType = GALProductionStatus.UNPACK_ENDED.name();
            transactionCode = GALProductionStatus.UNPACK_ENDED.getValue();
        }
        else {
            eventType = GALProductionStatus.PRODUCTION_ENDED.name();
            transactionCode = GALProductionStatus.PRODUCTION_ENDED.getValue();
        }

        String idocId = "";
        String orderId = "";
        String orderLineNumber = "";
        String productionOrderId = "";
        String galKey = "";

        if(ObjectUtils.isNotEmpty(productionOrder)){
            idocId = productionOrder.getIdocId().toString();
            productionOrderId = productionOrder.getId().toString();
            orderId = productionOrder.getOrderId();
            orderLineNumber = productionOrder.getOrderLineNumber();
            orderType = productionOrder.getProductionOrderType();
            galKey = productionOrder.getGalKey();
        }

        IfEventQueueDto dto = IfEventQueueDto
                .builder()
                .productionOrderId(productionOrderId)
                .messageName(messageName)
                .eventType(eventType)
                .transactionCode(transactionCode)
                .carrierName(carrierName)
                .idocId(idocId)
                .orderId(orderId)
                .orderLineNumber(orderLineNumber)
                .quantity(quantity)
                .missQuantity(missQuantity)
                .scrapQuantity(scrapQuantity)
                .orderType(orderType)
                .galKey(galKey)
                .lotName(lotName)
                .itemName(itemName)
                .mngKey(mngKey.toString())
                .resultStatus("")
                .errorReason("")
                .build();

        ifEventQueueDtoList.add(dto);

        eventType = GALProductionStatus.ORDER_LINE_NO_COMPLETED.name();
        transactionCode = GALProductionStatus.ORDER_LINE_NO_COMPLETED.getValue();

        dto = IfEventQueueDto
                .builder()
                .productionOrderId(productionOrder.getId().toString())
                .messageName(messageName)
                .eventType(eventType)
                .transactionCode(transactionCode)
                .idocId(productionOrder.getIdocId().toString())
                .orderId(productionOrder.getOrderId())
                .orderLineNumber(productionOrder.getOrderLineNumber())
                .quantity(productionOrder.getPlanQuantity())
                .orderType(productionOrder.getProductionOrderType())
                .galKey(productionOrder.getGalKey())
                .lotName(productionOrder.getLotName())
                .itemName(productionOrder.getItemName())
                .resultStatus("")
                .errorReason("")
                .build();

        ifEventQueueDtoList.add(dto);

        eventType = GALProductionStatus.ORDER_LINE_NO_COMPLETED.name();
        transactionCode = GALProductionStatus.ORDER_LINE_NO_COMPLETED.getValue();

        dto = IfEventQueueDto
                .builder()
                .productionOrderId(productionOrder.getId().toString())
                .messageName(messageName)
                .eventType(eventType)
                .transactionCode(transactionCode)
                .idocId(productionOrder.getIdocId().toString())
                .orderId(productionOrder.getOrderId())
                .orderLineNumber(productionOrder.getOrderLineNumber())
                .quantity(productionOrder.getPlanQuantity())
                .orderType(productionOrder.getProductionOrderType())
                .galKey(productionOrder.getGalKey())
                .lotName(productionOrder.getLotName())
                .itemName(productionOrder.getItemName())
                .resultStatus("")
                .errorReason("")
                .build();

        ifEventQueueDtoList.add(dto);

        eventType = GALProductionStatus.WHAT_IS_NEXT_RRN.name();
        transactionCode = GALProductionStatus.WHAT_IS_NEXT_RRN.getValue();

        dto = IfEventQueueDto
                .builder()
                .productionOrderId(productionOrder.getId().toString())
                .messageName(messageName)
                .eventType(eventType)
                .transactionCode(transactionCode)
                .idocId(productionOrder.getIdocId().toString())
                .orderId(productionOrder.getOrderId())
                .orderLineNumber(productionOrder.getOrderLineNumber())
                .quantity(productionOrder.getPlanQuantity())
                .orderType(productionOrder.getProductionOrderType())
                .galKey(productionOrder.getGalKey())
                .lotName(productionOrder.getLotName())
                .itemName(productionOrder.getItemName())
                .mngKey(mngKey.toString())
                .resultStatus("")
                .errorReason("")
                .build();

        ifEventQueueDtoList.add(dto);

        return  ifEventQueueDtoList;
    }

    private List<IfEventQueueDto> handleTransportJobStarted(PowderEventQueueReportVo vo){
        List<IfEventQueueDto>  ifEventQueueDtoList = new ArrayList<>();
        String messageName = vo.getMessageName();
        ProductionOrder productionOrder = vo.getProductionOrder();
        TransactionInfo tx = vo.getTx();

        if(ObjectUtils.isEmpty(productionOrder)){
            throw new RuntimeException("production Order is null");
        }

        if(
                StringUtils.equals(ProductionOrderType.UNPACKING.getValue(),productionOrder.getProductionOrderType())
                        || StringUtils.equals(ProductionOrderType.PRODUCTION.getValue(),productionOrder.getProductionOrderType())
        )
        {
            String eventType = GALProductionStatus.RELEASE.name();
            String transactionCode = GALProductionStatus.RELEASE.getValue();;

            IfEventQueueDto dto = IfEventQueueDto
                    .builder()
                    .productionOrderId(productionOrder.getId().toString())
                    .messageName(messageName)
                    .eventType(eventType)
                    .transactionCode(transactionCode)
                    .idocId(productionOrder.getIdocId().toString())
                    .orderId(productionOrder.getOrderId())
                    .orderLineNumber(productionOrder.getOrderLineNumber())
                    .quantity(productionOrder.getPlanQuantity())
                    .orderType(productionOrder.getProductionOrderType())
                    .galKey(productionOrder.getGalKey())
                    .lotName(productionOrder.getLotName())
                    .itemName(productionOrder.getItemName())
                    .resultStatus("")
                    .errorReason("")
                    .build();

            ifEventQueueDtoList.add(dto);
        }
        else{
            return ifEventQueueDtoList;
        }

        return  ifEventQueueDtoList;
    }

    private List<IfEventQueueDto> handleTransportJobCompleted(PowderEventQueueReportVo vo){
        List<IfEventQueueDto>  ifEventQueueDtoList = new ArrayList<>();
        String messageName = vo.getMessageName();
        ProductionOrder productionOrder = vo.getProductionOrder();
        TransactionInfo tx = vo.getTx();

        if(ObjectUtils.isEmpty(productionOrder)){
            throw new RuntimeException("production Order is null");
        }

        if(StringUtils.equals(ProductionOrderType.MATERIAL_INBOUND.getValue(),productionOrder.getProductionOrderType())){
            // 원자재 입고에 대해서만 완료 로직 수행 아닌 경우
            // 그냥 ifEventQueueDtoList 반환
        }
        else{
            return ifEventQueueDtoList;
        }

        String eventType = GALProductionStatus.PALLET_LOAD_COMPLETED_TO_WAREHOUSE.name();
        String transactionCode = GALProductionStatus.PALLET_LOAD_COMPLETED_TO_WAREHOUSE.getValue();;

        IfEventQueueDto dto = IfEventQueueDto
                .builder()
                .productionOrderId(productionOrder.getId().toString())
                .messageName(messageName)
                .eventType(eventType)
                .transactionCode(transactionCode)
                .idocId(productionOrder.getIdocId().toString())
                .orderId(productionOrder.getOrderId())
                .orderLineNumber(productionOrder.getOrderLineNumber())
                .quantity(productionOrder.getPlanQuantity())
                .orderType(productionOrder.getProductionOrderType())
                .galKey(productionOrder.getGalKey())
                .lotName(productionOrder.getLotName())
                .itemName(productionOrder.getItemName())
                .resultStatus("")
                .errorReason("")
                .build();

        ifEventQueueDtoList.add(dto);

        eventType = GALProductionStatus.ORDER_LINE_NO_COMPLETED.name();
        transactionCode = GALProductionStatus.ORDER_LINE_NO_COMPLETED.getValue();

        dto = IfEventQueueDto
                .builder()
                .productionOrderId(productionOrder.getId().toString())
                .messageName(messageName)
                .eventType(eventType)
                .transactionCode(transactionCode)
                .idocId(productionOrder.getIdocId().toString())
                .orderId(productionOrder.getOrderId())
                .orderLineNumber(productionOrder.getOrderLineNumber())
                .quantity(productionOrder.getPlanQuantity())
                .orderType(productionOrder.getProductionOrderType())
                .galKey(productionOrder.getGalKey())
                .lotName(productionOrder.getLotName())
                .itemName(productionOrder.getItemName())
                .resultStatus("")
                .errorReason("")
                .build();

        ifEventQueueDtoList.add(dto);

        eventType = GALProductionStatus.ORDER_COMPLETED.name();
        transactionCode = GALProductionStatus.ORDER_COMPLETED.getValue();

        dto = IfEventQueueDto
                .builder()
                .productionOrderId(productionOrder.getId().toString())
                .messageName(messageName)
                .eventType(eventType)
                .transactionCode(transactionCode)
                .idocId(productionOrder.getIdocId().toString())
                .orderId(productionOrder.getOrderId())
                .orderLineNumber(productionOrder.getOrderLineNumber())
                .quantity(productionOrder.getPlanQuantity())
                .orderType(productionOrder.getProductionOrderType())
                .galKey(productionOrder.getGalKey())
                .lotName(productionOrder.getLotName())
                .itemName(productionOrder.getItemName())
                .resultStatus("")
                .errorReason("")
                .build();

        ifEventQueueDtoList.add(dto);

        return ifEventQueueDtoList;
    }
}
