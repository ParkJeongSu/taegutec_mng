package kr.co.aim.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.dto.insert.IfEventQueueDto;
import kr.co.aim.api.strategy.FactoryIfEventQueueStrategy;
import kr.co.aim.api.vo.insert.ops.InsertEventQueueReportVo;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.*;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.IfEventQueueCreateCommand;
import kr.co.aim.domain.model.*;
import kr.co.aim.infra.persistence.mapper.PortMapper;
import kr.co.aim.infra.persistence.mapper.TransportJobMapper;
import kr.co.aim.infra.persistence.mapper.TransportOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertIfEventQueueService implements FactoryIfEventQueueStrategy {

    private final HistoryService historyService;
    private final ObjectMapper objectMapper;

    private final PortService portService;
    private final PortMapper portMapper;

    private final TransportJobService transportJobService;
    private final TransportJobMapper transportJobMapper;

    private final TransportOrderService transportOrderService;
    private final TransportOrderMapper transportOrderMapper;

    private final IfEventQueueService ifEventQueueService;

    /**
     * 1. 큐에 처음 넣을 때 (신규 생성)
     * try{
     * InterfaceEventLogService.enqueue(vo);
     * }
     * catch(Exception e){
     * log.error("로그 저장 실패");
     * }
     * 위 방식으로 호출 해야함
     */
    @Override
    @Transactional(value = "mssqlTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void enqueueIfEventQueue(Object vo) {
        // Java 17의 Pattern Matching 사용
        if (vo instanceof InsertEventQueueReportVo reportVo) {
            // save EventLog로 변경
            Optional<IfEventQueueDto> optionalIfEventQueueDto = createEventLogDto(reportVo);
            if(optionalIfEventQueueDto.isEmpty()){
                return;
            }
            IfEventQueueDto dto = optionalIfEventQueueDto.get();
            TransactionInfo tx = TransactionInfo.now("saveInterfaceEventLog",SystemName.MNG.getValue(), "",reportVo.getTx().eventTime());

            // DTO 객체를 JSON 문자열로 직접 변환합니다.
            String jsonPayload = "";
            try {
                jsonPayload = objectMapper.writeValueAsString(dto);
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
        }else {
            log.error("잘못된 객체 타입이 전달되었습니다: {}", vo != null ? vo.getClass().getName() : "null");
        }

    }

    private Optional<IfEventQueueDto> createEventLogDto(InsertEventQueueReportVo vo) {
        String messageName = vo.getMessageName();
        Optional<Port> optionalPort = vo.getOptionalPort();
        Optional<PortDef> optionalPortDef = vo.getOptionalPortDef();
        Port port = null;
        PortDef portDef = null;
        String transportJobName =  vo.getTransportJobName();
        String eventType = "";
        String transactionCode ="";
        String carrierName = vo.getCarrierName(); // 어떠한 경우에도 공백이 없음
        String idocId = "";
        String orderId = "";
        String orderLineNumber = "";
        String orderType = "";
        // actualLocationId : Rack Location or location on conveyor on System
        String actualLocationId = vo.getActualRackLocationId();
        if (StringUtils.equals(MessageList.LOAD_COMPLETE.getMessageName(), messageName)) {
            if(optionalPortDef.isPresent()){
                portDef = optionalPortDef.get();
            }else{
                return Optional.empty();
            }

            if (StringUtils.equals(PortDetailType.INBOUND.getValue(), portDef.getDetailPortType())) {
                // Inbound Station Occupied case
                // 106 report
                eventType = GALTransportStatus.StationOccupied.name();
                transactionCode = GALTransportStatus.StationOccupied.getValue();
                idocId = "";
                orderId = "";
                orderLineNumber = "";
                orderType = TransportOrderType.INBOUND.getValue();
            } else if (StringUtils.equals(PortDetailType.WORKSTATION.getValue(), portDef.getDetailPortType())) {
                // 반송잡이 있으면 해당 반송잡으로 아래보고
                // outbound case
                // 108 Outbound Arrival At workStation report
                // 90 outbound order Done report
                // 반송잡이 없다면,
                // 가장 최신 변경된 transportOrder 으로 108,90 보고
                TransportOrder transportOrder = null;
                if(StringUtils.isNotBlank(transportJobName)){
                    Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
                    if(optionalTransportJob.isPresent()){
                        TransportJob transportJob = optionalTransportJob.get();
                        Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(transportJob.getOrderId());
                        if(optionalTransportOrder.isPresent()){
                            transportOrder = optionalTransportOrder.get();
                        }
                    }
                }
                if( ObjectUtils.isEmpty(transportOrder)){
                    List<String> transportStatus = new ArrayList<>();
                    transportStatus.add(TransportOrderStatus.COMPLETED.getValue());
                    List<TransportOrder> transportOrders = transportOrderService.findTransportOrderByCondition(
                            carrierName,
                            TransportOrderType.OUTBOUND.getValue(),
                            transportStatus);
                    if(transportOrders.isEmpty()){
                        throw new RuntimeException("Not Exists TransportOrder");
                    }
                    transportOrder = transportOrders.get(0);
                }
                eventType = GALTransportStatus.ArrivedAtWorkStation.name();
                transactionCode = GALTransportStatus.ArrivedAtWorkStation.getValue();
                idocId = transportOrder.getIdocId().toString();
                orderId = transportOrder.getTransportOrderId();
                orderLineNumber = "";
                orderType = transportOrder.getTransportType();
            }
            else if(StringUtils.equals(PortDetailType.OUT_OF_RACK.getValue(), portDef.getDetailPortType())){
                // inbound case
                // outbound case
                // 109 Out Of Rack report
                // transportJobName exists
                TransportOrder transportOrder = null;
                if(StringUtils.isNotBlank(transportJobName)){
                    Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
                    if(optionalTransportJob.isPresent()){
                        TransportJob transportJob = optionalTransportJob.get();
                        Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(transportJob.getOrderId());
                        if(optionalTransportOrder.isPresent()){
                            transportOrder = optionalTransportOrder.get();
                        }
                    }
                }
                if(ObjectUtils.isEmpty(transportOrder)){
                    List<String> transportStatus = new ArrayList<>();
                    transportStatus.add(TransportOrderStatus.COMPLETED.getValue());
                    List<TransportOrder> transportOrders = transportOrderService.findTransportOrderByCondition(
                            carrierName,
                            TransportOrderType.OUTBOUND.getValue(),
                            transportStatus);
                    if(transportOrders.isEmpty()){
                        throw new RuntimeException("Not Exists TransportOrder");
                    }
                    transportOrder = transportOrders.get(0);
                }
                eventType = GALTransportStatus.OutOfRack.name();
                transactionCode = GALTransportStatus.OutOfRack.getValue();
                idocId = transportOrder.getIdocId().toString();
                orderId = transportOrder.getTransportOrderId();
                orderLineNumber = "";
                orderType = transportOrder.getTransportType();
            }
            else if(StringUtils.equals(PortDetailType.BOTH_OF_RACK.getValue(), portDef.getDetailPortType())){
                // inbound case
                // outbound case
                // 109 Out Of Rack report
                // transportJobName exists
                TransportOrder transportOrder = null;
                if(StringUtils.isNotBlank(transportJobName)){
                    Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
                    if(optionalTransportJob.isPresent()){
                        TransportJob transportJob = optionalTransportJob.get();
                        Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(transportJob.getOrderId());
                        if(optionalTransportOrder.isPresent()){
                            transportOrder = optionalTransportOrder.get();
                        }
                    }
                }
                if(ObjectUtils.isEmpty(transportOrder)){
                    List<String> transportStatus = new ArrayList<>();
                    transportStatus.add(TransportOrderStatus.COMPLETED.getValue());
                    List<TransportOrder> transportOrders = transportOrderService.findTransportOrderByCondition(
                            carrierName,
                            TransportOrderType.OUTBOUND.getValue(),
                            transportStatus);
                    if(transportOrders.isEmpty()){
                        throw new RuntimeException("Not Exists TransportOrder");
                    }
                    transportOrder = transportOrders.get(0);
                }
                eventType = GALTransportStatus.OutOfRack.name();
                transactionCode = GALTransportStatus.OutOfRack.getValue();
                idocId = transportOrder.getIdocId().toString();
                orderId = transportOrder.getTransportOrderId();
                orderLineNumber = "";
                orderType = transportOrder.getTransportType();
            }
        }
        else if (StringUtils.equals(MessageList.UNLOAD_COMPLETE.getMessageName(), messageName)) {
            if(optionalPortDef.isPresent()){
                portDef = optionalPortDef.get();
            }else{
                return Optional.empty();
            }
            if (StringUtils.equals(PortDetailType.INBOUND.getValue(), portDef.getDetailPortType())) {
                // Inbound Workstation empty
                // 105 repot
                // transportJobName 은 존재
                // 만일 주지 않더라도, inbound 가 시작된 가장 느린 carrier 를 기준으로 transportOrder find

                TransportOrder transportOrder = null;
                if(StringUtils.isNotBlank(transportJobName)){
                    Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
                    if(optionalTransportJob.isPresent()){
                        TransportJob transportJob = optionalTransportJob.get();
                        Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(transportJob.getOrderId());
                        if(optionalTransportOrder.isPresent()){
                            transportOrder = optionalTransportOrder.get();
                        }
                    }
                }
                if( ObjectUtils.isEmpty(transportOrder) ){
                    List<String> transportStatus = new ArrayList<>();
                    transportStatus.add(TransportOrderStatus.STARTED.getValue());
                    List<TransportOrder> transportOrders = transportOrderService.findTransportOrderByCondition(
                            carrierName,
                            TransportOrderType.INBOUND.getValue(),
                            transportStatus);
                    if(transportOrders.isEmpty()){
                        throw new RuntimeException("Not Exists TransportOrder");
                    }
                    transportOrder = transportOrders.get(0);
                }
                eventType = GALTransportStatus.CarrierScanned.name();
                transactionCode = GALTransportStatus.CarrierScanned.getValue();
                idocId = transportOrder.getIdocId().toString();
                orderId = transportOrder.getTransportOrderId();
                orderLineNumber = "";
                orderType = TransportOrderType.INBOUND.getValue();
            }
            else{
                return Optional.empty();
            }
        }
        else if (StringUtils.equals(MessageList.CARRIER_SCANNED.getMessageName(), messageName)) {
            // Inbound ContainerId is Scanned
            // 126 repot
            // transportJobName 은 존재
            TransportOrder transportOrder = null;
            if(StringUtils.isNotBlank(transportJobName)){
                Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
                if(optionalTransportJob.isPresent()){
                    TransportJob transportJob = optionalTransportJob.get();
                    Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(transportJob.getOrderId());
                    if(optionalTransportOrder.isPresent()){
                        transportOrder = optionalTransportOrder.get();
                    }
                }
            }
            if( ObjectUtils.isEmpty(transportOrder)){
                List<String> transportStatus = new ArrayList<>();
                transportStatus.add(TransportOrderStatus.STARTED.getValue());
                List<TransportOrder> transportOrders = transportOrderService.findTransportOrderByCondition(
                        carrierName,
                        TransportOrderType.INBOUND.getValue(),
                        transportStatus);
                if(transportOrders.isEmpty()){
                    throw new RuntimeException("Not Exists TransportOrder");
                }
                transportOrder = transportOrders.get(0);
            }
            eventType = GALTransportStatus.CarrierScanned.name();
            transactionCode = GALTransportStatus.CarrierScanned.getValue();
            idocId = transportOrder.getIdocId().toString();
            orderId = transportOrder.getTransportOrderId();
            orderLineNumber = "";
            orderType = transportOrder.getTransportType();
        }
        else if (StringUtils.equals(MessageList.CARRIER_LOCATION_CHANGED.getMessageName(), messageName)) {
            // 반송 잡은 무조건 존재
            // 이 경우는 TransportOrder가 있을 수도 없을 수도 있음
            // TransportOrder가 없다면, 상위로 보고하지 않음
            if(optionalPortDef.isPresent()){
                portDef = optionalPortDef.get();
            }else{
                return Optional.empty();
            }
            if (StringUtils.equals(PortDetailType.INBOUND.getValue(), portDef.getDetailPortType())) {
                // Inbound Station Occupied case
                // 106 report
                eventType = GALTransportStatus.StationOccupied.name();
                transactionCode = GALTransportStatus.StationOccupied.getValue();
                idocId = "";
                orderId = "";
                orderLineNumber = "";
                orderType = TransportOrderType.INBOUND.getValue();
            } else if (StringUtils.equals(PortDetailType.WORKSTATION.getValue(), portDef.getDetailPortType())) {
                // 반송잡이 있으면 해당 반송잡으로 아래보고
                // outbound case
                // 108 Outbound Arrival At workStation report
                // 90 outbound order Done report
                // 반송잡이 없다면,
                // 가장 최신 변경된 transportOrder 으로 108,90 보고
                TransportOrder transportOrder = null;
                if(StringUtils.isNotBlank(transportJobName)){
                    Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
                    if(optionalTransportJob.isPresent()){
                        TransportJob transportJob = optionalTransportJob.get();
                        Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(transportJob.getOrderId());
                        if(optionalTransportOrder.isPresent()){
                            transportOrder = optionalTransportOrder.get();
                        }
                    }
                }
                if( ObjectUtils.isEmpty(transportOrder)){
                    List<String> transportStatus = new ArrayList<>();
                    transportStatus.add(TransportOrderStatus.COMPLETED.getValue());
                    List<TransportOrder> transportOrders = transportOrderService.findTransportOrderByCondition(
                            carrierName,
                            TransportOrderType.OUTBOUND.getValue(),
                            transportStatus);
                    if(transportOrders.isEmpty()){
                        throw new RuntimeException("Not Exists TransportOrder");
                    }
                    transportOrder = transportOrders.get(0);
                }
                eventType = GALTransportStatus.ArrivedAtWorkStation.name();
                transactionCode = GALTransportStatus.ArrivedAtWorkStation.getValue();
                idocId = transportOrder.getIdocId().toString();
                orderId = transportOrder.getTransportOrderId();
                orderLineNumber = "";
                orderType = transportOrder.getTransportType();
            }
            else if(StringUtils.equals(PortDetailType.OUT_OF_RACK.getValue(), portDef.getDetailPortType())){
                // inbound case
                // outbound case
                // 109 Out Of Rack report
                // transportJobName exists
                TransportOrder transportOrder = null;
                if(StringUtils.isNotBlank(transportJobName)){
                    Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
                    if(optionalTransportJob.isPresent()){
                        TransportJob transportJob = optionalTransportJob.get();
                        Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(transportJob.getOrderId());
                        if(optionalTransportOrder.isPresent()){
                            transportOrder = optionalTransportOrder.get();
                        }
                    }
                }
                if(ObjectUtils.isEmpty(transportOrder)){
                    List<String> transportStatus = new ArrayList<>();
                    transportStatus.add(TransportOrderStatus.COMPLETED.getValue());
                    List<TransportOrder> transportOrders = transportOrderService.findTransportOrderByCondition(
                            carrierName,
                            TransportOrderType.OUTBOUND.getValue(),
                            transportStatus);
                    if(transportOrders.isEmpty()){
                        throw new RuntimeException("Not Exists TransportOrder");
                    }
                    transportOrder = transportOrders.get(0);
                }
                eventType = GALTransportStatus.OutOfRack.name();
                transactionCode = GALTransportStatus.OutOfRack.getValue();
                idocId = transportOrder.getIdocId().toString();
                orderId = transportOrder.getTransportOrderId();
                orderLineNumber = "";
                orderType = transportOrder.getTransportType();
            }
            else if(StringUtils.equals(PortDetailType.BOTH_OF_RACK.getValue(), portDef.getDetailPortType())){
                // inbound case
                // outbound case
                // 109 Out Of Rack report
                // transportJobName exists
                TransportOrder transportOrder = null;
                if(StringUtils.isNotBlank(transportJobName)){
                    Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
                    if(optionalTransportJob.isPresent()){
                        TransportJob transportJob = optionalTransportJob.get();
                        Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(transportJob.getOrderId());
                        if(optionalTransportOrder.isPresent()){
                            transportOrder = optionalTransportOrder.get();
                        }
                    }
                }
                if(ObjectUtils.isEmpty(transportOrder)){
                    List<String> transportStatus = new ArrayList<>();
                    transportStatus.add(TransportOrderStatus.COMPLETED.getValue());
                    List<TransportOrder> transportOrders = transportOrderService.findTransportOrderByCondition(
                            carrierName,
                            TransportOrderType.OUTBOUND.getValue(),
                            transportStatus);
                    if(transportOrders.isEmpty()){
                        throw new RuntimeException("Not Exists TransportOrder");
                    }
                    transportOrder = transportOrders.get(0);
                }
                eventType = GALTransportStatus.OutOfRack.name();
                transactionCode = GALTransportStatus.OutOfRack.getValue();
                idocId = transportOrder.getIdocId().toString();
                orderId = transportOrder.getTransportOrderId();
                orderLineNumber = "";
                orderType = transportOrder.getTransportType();
            }

        } else if (StringUtils.equals(MessageList.TRANSPORT_JOB_COMPLETED.getMessageName(), messageName)) {
            // 무조건 TransportJob 은 존재

            // Type : Inbound Case
            // 107 Arrival at Rack report
            // 92 Inbound order Done report

            // Type : Outbound Case
            // 109 Out of Rack report

            // Type : Relocation Case
            // #1 orderId 가 존재하면
            // 107 Arrival at Rack report
            // 94 Relocation order confirmation report

            // #2 orderId 가 존재하지 않는다면
            // 114 internal Relocation report
            TransportOrder transportOrder = null;
            if(StringUtils.isNotBlank(transportJobName)){
                Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
                if(optionalTransportJob.isPresent()){
                    TransportJob transportJob = optionalTransportJob.get();

                    if(StringUtils.equals(SystemName.GAL.getValue(), transportJob.getRequestSource())){
                        // GAL order 에 의한 반송
                        Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(transportJob.getOrderId());
                        if(optionalTransportOrder.isPresent()){
                            transportOrder = optionalTransportOrder.get();
                        }
                        else{
                            throw new RuntimeException("Not Exists TransportOrder");
                        }
                        if(StringUtils.equals(TransportOrderType.OUTBOUND.getValue(), transportOrder.getTransportType())){
                            eventType = GALTransportStatus.OutOfRack.name();
                            transactionCode = GALTransportStatus.OutOfRack.getValue();
                            idocId = transportOrder.getIdocId().toString();
                            orderId = transportOrder.getTransportOrderId();
                            orderLineNumber = "";
                            orderType = transportOrder.getTransportType();
                        }
                        else if(StringUtils.equals(TransportOrderType.INBOUND.getValue(), transportOrder.getTransportType())){
                            eventType = GALTransportStatus.ArrivedAtRack.name();
                            transactionCode = GALTransportStatus.ArrivedAtRack.getValue();
                            idocId = transportOrder.getIdocId().toString();
                            orderId = transportOrder.getTransportOrderId();
                            orderLineNumber = "";
                            orderType = transportOrder.getTransportType();
                        }
                        else if(StringUtils.equals(TransportOrderType.RELOCATION.getValue(), transportOrder.getTransportType())){
                            eventType = GALTransportStatus.OutOfRack.name();
                            transactionCode = GALTransportStatus.OutOfRack.getValue();
                            idocId = transportOrder.getIdocId().toString();
                            orderId = transportOrder.getTransportOrderId();
                            orderLineNumber = "";
                            orderType = transportOrder.getTransportType();
                        }
                        else{
                            throw new RuntimeException("TransportOrderType Error");
                        }
                    }
                    else if(StringUtils.equals(SystemName.WCS.getValue(), transportJob.getRequestSource())){
                        // WCS 자체 반송
                        // R :내부적인 relocation이 발생한 case
                        // 114
                        // 그 외에는
                        // outbound : OutOfRack
                        // inbound : ArrivedAtRack
                        if(StringUtils.equals(TransportOrderType.OUTBOUND.getValue(), transportJob.getTransportType())){
                            eventType = GALTransportStatus.OutOfRack.name();
                            transactionCode = GALTransportStatus.OutOfRack.getValue();
                            idocId = "";
                            orderId = "";
                            orderLineNumber = "";
                            orderType = transportJob.getTransportType();
                        }
                        else if(StringUtils.equals(TransportOrderType.INBOUND.getValue(), transportJob.getTransportType())){
                            eventType = GALTransportStatus.ArrivedAtRack.name();
                            transactionCode = GALTransportStatus.ArrivedAtRack.getValue();
                            idocId = "";
                            orderId = "";
                            orderLineNumber = "";
                            orderType = transportJob.getTransportType();
                        }
                        else if(StringUtils.equals(TransportOrderType.RELOCATION.getValue(), transportJob.getTransportType())){
                            eventType = GALTransportStatus.InternalRelocation.name();
                            transactionCode = GALTransportStatus.InternalRelocation.getValue();
                            idocId = "";
                            orderId = "";
                            orderLineNumber = "";
                            orderType = transportJob.getTransportType();
                        }
                    }

                }
                else{
                    throw new RuntimeException("Not Exists TransportJob");
                }
            }
            else{
                throw new RuntimeException("Not Exists TransportJob");
            }
        } else if (StringUtils.equals(MessageList.TRANSPORT_JOB_REPLY.getMessageName(), messageName)) {
            // 무조건 TransportJob 은 존재
            // reply 는 무조건 gal에 의한 order 후 reply
            // Type : Inbound Case
            // Type : Outbound Case
            // Type : Relocation Case
            // 2 Accept report
            Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
            if(optionalTransportJob.isPresent()){
                TransportJob transportJob = optionalTransportJob.get();
                Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(transportJob.getOrderId());
                TransportOrder transportOrder = null;
                if(optionalTransportOrder.isPresent()){
                    transportOrder = optionalTransportOrder.get();
                    eventType = GALTransportStatus.Accept.name();
                    transactionCode = GALTransportStatus.Accept.getValue();
                    idocId = transportOrder.getIdocId().toString();
                    orderId = transportOrder.getTransportOrderId();
                    orderLineNumber = "";
                    orderType = transportOrder.getTransportType();
                }
                else{
                    throw new RuntimeException("Not Exists TransportOrder");
                }
            }else{
                throw new RuntimeException("Not Exists TransportJob");
            }
        } else if (StringUtils.equals(MessageList.TRANSPORT_JOB_STARTED.getMessageName(), messageName)) {
            // GAL에 의한 반송은 TransportJob 이 존재
            // WCS 자체적인 반송은 TransportJob 이 존재하지 않음
            // Type : Inbound Case
            // Type : Outbound Case
            // Type : Relocation Case
            // 6 Released report
            if(StringUtils.equals(SystemName.GAL.getValue(), vo.getRequestSource())){
                Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
                if(optionalTransportJob.isPresent()){
                    TransportJob transportJob = optionalTransportJob.get();
                    Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(transportJob.getOrderId());
                    TransportOrder transportOrder = null;
                    if(optionalTransportOrder.isPresent()){
                        transportOrder = optionalTransportOrder.get();
                        eventType = GALTransportStatus.Released.name();
                        transactionCode = GALTransportStatus.Released.getValue();
                        idocId = transportOrder.getIdocId().toString();
                        orderId = transportOrder.getTransportOrderId();
                        orderLineNumber = "";
                        orderType = transportOrder.getTransportType();
                    }
                    else{
                        throw new RuntimeException("Not Exists TransportOrder");
                    }
                }else{
                    throw new RuntimeException("Not Exists TransportJob");
                }
            }else {
                eventType = GALTransportStatus.Released.name();
                transactionCode = GALTransportStatus.Released.getValue();
                idocId = "";
                orderId = "";
                orderLineNumber = "";
                orderType = vo.getOrderType();
            }
        }
        else if (StringUtils.equals(MessageList.TRANSPORT_JOB_CANCEL_STARTED.getMessageName(), messageName)) {
            return Optional.empty();
        }
        else if (StringUtils.equals(MessageList.TRANSPORT_JOB_CANCEL_COMPLETED.getMessageName(), messageName)) {
            Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
            if(optionalTransportJob.isPresent()){
                TransportJob transportJob = optionalTransportJob.get();
                if(StringUtils.equals(TransportOrderType.INBOUND.getValue(), transportJob.getTransportType())){
                    Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(transportJob.getOrderId());
                    TransportOrder transportOrder = null;
                    if(optionalTransportOrder.isPresent()){
                        transportOrder = optionalTransportOrder.get();
                        eventType = GALTransportStatus.ArrivedAtWorkstationWithError.name();
                        transactionCode = GALTransportStatus.ArrivedAtWorkstationWithError.getValue();
                        idocId = transportOrder.getIdocId().toString();
                        orderId = transportOrder.getTransportOrderId();
                        orderLineNumber = "";
                        orderType = transportOrder.getTransportType();
                    }
                    else{
                        return Optional.empty();
                    }
                }
                else if(StringUtils.equals(TransportOrderType.OUTBOUND.getValue(), transportJob.getTransportType())){
                    //TODO: Outbound 의 경우 단하나의 경우 binEmpty 가 발생하는데 이부분 찬우 책임님과 소통
                    Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(transportJob.getOrderId());
                    TransportOrder transportOrder = null;
                    if(optionalTransportOrder.isPresent()){
                        transportOrder = optionalTransportOrder.get();
                        eventType = GALTransportStatus.BinEmpty.name();
                        transactionCode = GALTransportStatus.BinEmpty.getValue();
                        idocId = transportOrder.getIdocId().toString();
                        orderId = transportOrder.getTransportOrderId();
                        orderLineNumber = "";
                        orderType = transportOrder.getTransportType();
                    }
                    else{
                        return Optional.empty();
                    }
                }
                else{
                    return Optional.empty();
                }

            }else{
                throw new RuntimeException("Not Exists TransportJob");
            }
        }
        else if(StringUtils.equals(MessageList.CARRIER_BLOCKED.getMessageName(), messageName)){
            eventType = GALTransportStatus.NotAllowedPickUp.name();
            transactionCode = GALTransportStatus.NotAllowedPickUp.getValue();
            idocId = "";
            orderId = "";
            orderLineNumber = "";
            orderType = "";
        }
        else if(StringUtils.equals(MessageList.CARRIER_UNBLOCKED.getMessageName(), messageName)){
            eventType = GALTransportStatus.ArrivedAtRack.name();
            transactionCode = GALTransportStatus.ArrivedAtRack.getValue();
            idocId = "";
            orderId = "";
            orderLineNumber = "";
            orderType = "";
        }
        else{
            return Optional.empty();
        }
        if(StringUtils.isNotBlank(transactionCode)){
            // TODO: 추가되는 dto 관련된건 여기다가 추가하기
            IfEventQueueDto dto = IfEventQueueDto
                    .builder()
                    .messageName(messageName)
                    .eventType(eventType)
                    .transactionCode(transactionCode)
                    .carrierName(carrierName)
                    .idocId(idocId)
                    .orderId(orderId)
                    .orderLineNumber(orderLineNumber)
                    .orderType(orderType)
                    .errorTexts(vo.getErrorTexts())
                    .actualWeight(vo.getActualWeight())
                    .actualZoneName(vo.getActualZoneName())
                    .actualLocationId(actualLocationId)
                    .build();
            return Optional.ofNullable(dto);
        }
        return Optional.empty();
    }
}
