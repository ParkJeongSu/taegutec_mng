package kr.co.aim.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.dto.powder.*;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.Utils.JsonUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.TransportJobRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.LotCarrierMappingCreateCommand;
import kr.co.aim.domain.command.LotCreateCommand;
import kr.co.aim.domain.command.TransportJobCreateCommand;
import kr.co.aim.domain.model.*;
import kr.co.aim.infra.config.RabbitConfig;
import kr.co.aim.infra.persistence.entity.LotCarrierMappingHistoryEntity;
import kr.co.aim.infra.persistence.entity.LotHistoryEntity;
import kr.co.aim.infra.persistence.mapper.LotCarrierMappingMapper;
import kr.co.aim.infra.persistence.mapper.LotMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RawMaterialReceivingService {
    private final ObjectMapper objectMapper;
    private final ProductionOrderService productionOrderService;
    private final LotService lotService;
    private final LotCarrierMappingService lotCarrierMappingService;
    private final CarrierService carrierService;
    private final CarrierDefService carrierDefService;
    private final ProductDefService productDefService;
    private final TransportJobService transportJobService;
    private final LotMapper lotMapper;
    private final LotCarrierMappingMapper lotCarrierMappingMapper;
    private final HistoryService historyService;

    private final RabbitTemplate rabbitTemplate;
    private final JsonUtils jsonUtils;
    private final NamingRuleService namingRuleService;

    /**
     * 1. 원자재 입고 시작
     * 작업자가 입고 오더를 선택하고 입고 작업을 개시합니다.
     */
    @Transactional(value = "mssqlTransactionManager")
    public Page<RawMaterialReceivingStartResponse> startReceiving(RawMaterialReceivingStartRequest request) {
        // 비즈니스 로직
        // 1. ProductionOrder 조회 후 원자재 입고 type validation
        // 2. Lot 생성
        // 3. 반환

        List<RawMaterialReceivingStart> list = request.getList();
        RawMaterialReceivingStart first = list.get(0);
        TransactionInfo tx = TransactionInfo.now(EventName.RAW_MATERIAL_RECEIVING_START.getValue(), first.getEventUser(),first.getEventComment());
        List<RawMaterialReceivingStartResponse> responseList = new ArrayList<>();

        for(RawMaterialReceivingStart rawMaterialReceivingStart : list) {
            Long id = rawMaterialReceivingStart.getId();
            String orderId = rawMaterialReceivingStart.getOrderId();
            String lotName = rawMaterialReceivingStart.getLotName();
            String itemName = rawMaterialReceivingStart.getItemName();

            Optional<ProductionOrder> optionalProductionOrder = productionOrderService.findById(id);

            if(optionalProductionOrder.isEmpty()) {
                throw new RuntimeException("production order not found");
            }

            ProductionOrder productionOrder = optionalProductionOrder.get();

            if(!StringUtils.equals(ProductionOrderType.MATERIAL_INBOUND.getValue(),productionOrder.getProductionOrderType())){
                throw new RuntimeException("production order type not match");
            }
            LotCreateCommand command =
                    LotCreateCommand
                            .builder()
                            .transactionInfo(tx)
                            .lotName(productionOrder.getLotName())
                            .originalLotName(productionOrder.getLotName())
                            .lotStatus(LotState.CREATED.getValue())
                            .itemId(productionOrder.getItemName())
                            .totalQuantity(productionOrder.getPlanQuantity())
                            .holdState(HoldState.NOT_ON_HOLD.getValue())
                            .build();
            Lot lot = Lot.create(command);
            lot = lotService.save(lot);
            LotHistoryEntity historyEntity = lotMapper.toHistoryEntity(lot);
            historyService.saveHistory(historyEntity);
            responseList.add(RawMaterialReceivingStartResponse.from(rawMaterialReceivingStart));
        }


        return new PageImpl<>(responseList, Pageable.unpaged(), list.size());
    }

    /**
     * 2. 원자재 팔레트 및 가방(Bag) 결합
     * 입고대의 팔레트 바코드/ID와 원자재 가방(Lot/Bag) 정보를 매핑합니다.
     */
    @Transactional(value = "mssqlTransactionManager")
    public Page<PalletBagBindingResponse> bindPalletAndBag(PalletBagBindingRequest request) {
        // 비즈니스 로직
        // 1. LotName 존재 Validation
        // 2. Carrier 존재 Validation
        // 3. LotCarrierMapping 데이터 생성 이때, 기본 가방의 무게값 gal Quantity에 생성
        // 4. 반환
        Long id = request.getId();
        String orderId = request.getOrderId();
        String lotName = request.getLotName();
        String itemName = request.getItemName();
        String carrierName = request.getCarrierName();
        BigDecimal quantity = request.getQuantity();
        String eventUser = request.getEventUser();
        String eventComment = request.getEventComment();

        Optional<ProductionOrder> optionalProductionOrder = productionOrderService.findById(id);

        if(optionalProductionOrder.isEmpty()) {
            throw new RuntimeException("production order not found");
        }

        ProductionOrder productionOrder = optionalProductionOrder.get();

        Optional<Lot> optionalLot = lotService.findByLotName(lotName);
        if(optionalLot.isEmpty()) {
            throw new RuntimeException("lot not found");
        }
        Lot lot = optionalLot.get();

        Optional<Carrier> optionalCarrier = carrierService.findByCarrierName(carrierName);

        if(optionalCarrier.isEmpty()) {
            throw new RuntimeException("carrier not found");
        }
        Carrier carrier = optionalCarrier.get();

        Optional<ProductDef> optionalProductDef = productDefService.findByProductDefName(itemName);

        if(optionalProductDef.isEmpty()) {
            throw new RuntimeException("itemName not found");
        }
        ProductDef productDef = optionalProductDef.get();

        TransactionInfo tx = TransactionInfo.now(EventName.BIND_PALLET_BAG.getValue(), eventUser, eventComment);
        LotCarrierMappingCreateCommand command =
                LotCarrierMappingCreateCommand
                        .builder()
                        .transactionInfo(tx)
                        .lotName(productionOrder.getLotName())
                        .carrierName(carrier.getCarrierName())
                        .orderId(productionOrder.getOrderId())
                        .orderLineNumber(productionOrder.getOrderLineNumber())
                        .productionOrderId(productionOrder.getId())
                        .productionStatus(ProductionStatus.WAIT.getValue())
                        .processStatus(ProcessStatus.WAIT.getValue())
                        .quantity(quantity)
                        .galQuantity(productDef.getDefaultReceiveQuantity())
                        .holdState(HoldState.NOT_ON_HOLD.getValue())
                        .build();

        LotCarrierMapping lotCarrierMapping = LotCarrierMapping.create(command);
        lotCarrierMapping = lotCarrierMappingService.save(lotCarrierMapping);
        LotCarrierMappingHistoryEntity historyEntity = lotCarrierMappingMapper.toHistoryEntity(lotCarrierMapping);
        historyService.saveHistory(historyEntity);

        List<PalletBagBindingResponse> responseList = new ArrayList<>();
        responseList.add(PalletBagBindingResponse.from(request));

        return new PageImpl<>(responseList, Pageable.unpaged(), responseList.size());
    }

    /**
     * 3. 창고로 입고 시작 (창고 이송 요청)
     * 결합이 완료된 팔레트를 창고(자동창고/랙 등)로 입고/이송 처리합니다.
     */
    @Transactional(value = "mssqlTransactionManager")
    public Page<WarehouseInboundStartResponse> startWarehouseInbound(WarehouseInboundStartRequest request) {
        // 비즈니스 로직
        // 1. LotName 존재 Validation
        // 2. Carrier 존재 Validation
        // 3. LotCarrierMapping 데이터 존재 Validation
        // 4. WCS에 반송 요청
        // 5. 반환

        Long id = request.getId();
        String carrierName = request.getCarrierName();
        String sourceEquipmentName = request.getSourceEquipmentName();
        String sourceZoneName = request.getSourceZoneName();
        String sourcePositionType = request.getSourcePositionType();
        String sourcePositionName = request.getSourcePositionName();
        String destinationEquipmentName = request.getDestinationEquipmentName();
        String destinationZoneName = request.getDestinationZoneName();
        String destinationPositionType = request.getDestinationPositionType();
        String destinationPositionName = request.getDestinationPositionName();
        String orderId = request.getOrderId();
        String lotName = request.getLotName();
        String itemName = request.getItemName();
        String requestSource = request.getRequestSource();
        String carrierType = request.getCarrierType();
        String eventUser = request.getEventUser();
        String eventComment = request.getEventComment();

        Optional<ProductionOrder> optionalProductionOrder = productionOrderService.findById(id);

        if(optionalProductionOrder.isEmpty()) {
            throw new RuntimeException("production order not found");
        }

        ProductionOrder productionOrder = optionalProductionOrder.get();

        Optional<Lot> optionalLot = lotService.findByLotName(lotName);
        if(optionalLot.isEmpty()) {
            throw new RuntimeException("lot not found");
        }
        Lot lot = optionalLot.get();

        Optional<Carrier> optionalCarrier = carrierService.findByCarrierName(carrierName);

        if(optionalCarrier.isEmpty()) {
            throw new RuntimeException("carrier not found");
        }
        Carrier carrier = optionalCarrier.get();

        Optional<LotCarrierMapping> optionalLotCarrierMapping = lotCarrierMappingService.findByLotNameAndCarrierName(lot.getLotName(), carrier.getCarrierName());

        if(optionalLotCarrierMapping.isEmpty()) {
            throw new RuntimeException("lot carrier not found");
        }
        LotCarrierMapping lotCarrierMapping = optionalLotCarrierMapping.get();

        Optional<CarrierDef> optionalCarrierDef = carrierDefService.findByCarrierDefName(carrier.getCarrierDefName());
        if(optionalCarrierDef.isEmpty()) {
            throw new RuntimeException("carrier Def not found");
        }
        CarrierDef carrierDef = optionalCarrierDef.get();

        TransactionInfo tx = TransactionInfo.now(EventName.RAW_MATERIAL_INBOUND_REQUEST.getValue(), SystemName.MNG.getValue(), "");

        String transactionId = FormatUtils.getTransactionId(tx.eventTime());
        String transportJobName = namingRuleService.getTransportJobName(SystemName.MNG.getValue(), tx.eventTime());

        TransportJobCreateCommand command = TransportJobCreateCommand.builder()
                .transportJobName(transportJobName)
                .carrierName(carrier.getCarrierName())
                .transportJobState(TransportJobState.REQUESTED.getValue())
                .carrierType(carrierDef.getCarrierType())
                .sourceEquipmentName(sourceEquipmentName)
                .sourcePortName(sourcePositionName)
                .sourceZoneName(sourceZoneName)
                .sourcePositionTypeName(sourcePositionType)
                .sourcePositionName(sourcePositionType)
                .destinationEquipmentName(destinationEquipmentName)
                //.destinationPortName()
                .destinationZoneName(destinationZoneName)
                .destinationPositionTypeName(destinationPositionName)
                .destinationPositionName(destinationPositionName)
                .createTime(tx.eventTime())
                .transactionInfo(tx)
                .build();

        TransportJob transportJob = transportJobService.createTransportJob(command);

        BaseMessage<TransportJobRequestBody> transportJobRequestBodyBaseMessage = new BaseMessage<>();
        transportJobRequestBodyBaseMessage.setTransactionId(transactionId);
        transportJobRequestBodyBaseMessage.setMessageFrom(SystemName.MNG.getValue());
        transportJobRequestBodyBaseMessage.setMessageOwner(SystemName.MNG.getValue());
        transportJobRequestBodyBaseMessage.setMessageTo(SystemName.WCS.getValue());
        transportJobRequestBodyBaseMessage.setEventTime(transactionId);
        transportJobRequestBodyBaseMessage.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
        transportJobRequestBodyBaseMessage.setResultCode(ResultCode.OK.getValue());
        transportJobRequestBodyBaseMessage.setBody(transportJobService.createTransportJobMessage(transportJob));

        jsonUtils.writePrettyJson(transportJobRequestBodyBaseMessage);;

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_WCS,
                RabbitConfig.ROUTING_WCS,
                request );

        List<WarehouseInboundStartResponse> warehouseInboundStartResponseList = new ArrayList<>();
        warehouseInboundStartResponseList.add(WarehouseInboundStartResponse.from(request));
        return new PageImpl<>(warehouseInboundStartResponseList, Pageable.unpaged(), warehouseInboundStartResponseList.size());
    }
}