package kr.co.aim.api.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.service.*;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.Utils.JsonUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.TransportOrderRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.TransportOrderCreateCommand;
import kr.co.aim.domain.model.TransportOrder;
import kr.co.aim.infra.config.RabbitConfig;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderDEntity;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderMEntity;
import kr.co.aim.infra.persistence.db2entity.insert.IdocEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("scheduler")
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertTransferScheduler {

    private final InsertExternalInterfaceService insertExternalInterfaceService;
    private final TransportOrderService transportOrderService;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final JsonUtils jsonUtils;

    @Scheduled(fixedDelay = 5000) // 5초마다 실행
    @SchedulerLock(name = "insertOrderDB2ToMSSQL",
            lockAtMostFor = "PT2M",     // 작업 최장 소요시간 + 버퍼
            lockAtLeastFor = "PT5S")    // 최소 간격(선택)
    public void insertOrderDB2ToMSSQL() {
        // 1단계 DB2에서 idocList를 가져온다. (DB2 트랜잭션)

        // 값이 존재한다면, 반복문 그리고 try catch 문

        // 2단계 h2orderMList와 h2orderDList를 조회해서 가져온다. (DB2 트랜잭션)
        // 2.1 단계 h2orderMList와 h2orderDList의 수량 validation

        // 3단계 transportOrder 에 해당 데이터를 insert 한다. (MSSQL 트랜잭션)

        // 4단계 정상적으로 MSSQL이 수행되었다면, Idoc의 errorCode와 dtimemode를 수정한다.

        // 5단계 errorCode와 dtimemode를 수정하고, tex로 transportJobRequest를 보낸다.

        List<Long> idocTypIds = new ArrayList<>();
        Integer state = 10;
        Integer errorCode = 0;
        idocTypIds.add(IdocTypeId.INBOUND.getValue());
        idocTypIds.add(IdocTypeId.OUTBOUND.getValue());
        idocTypIds.add(IdocTypeId.RELOCATION.getValue());
        //List<IdocEntity> idocEntities = insertExternalInterfaceService.selectByIdocTypIdsAndErrorCode(idocTypIds,errorCode);
        List<IdocEntity> idocEntities = insertExternalInterfaceService.selectByIdocTypIdsAndStateAndErrorCode(idocTypIds,state,errorCode);

        if(CollectionUtils.isEmpty(idocEntities)){
            return;
        }
        for(IdocEntity idocEntity : idocEntities){
            try {
                List<H2OrderMEntity> h2OrderMEntities = insertExternalInterfaceService.selectH2OrderMEntityByIdocId(idocEntity.getLineId());
                List<H2OrderDEntity> h2OrderDEntities = insertExternalInterfaceService.selectH2OrderDEntityByIdocId(idocEntity.getLineId());

                // validation
                // O : Outbound
                // I : Inbound
                // R : Relocation
                // O : OrderM 1 : OrderD 1
                // I : OrderM 1 : OrderD 1
                // R : OrderM 1 : OrderD 2
                // validation Logic

                // create TransportOrder
                TransportOrder transportOrder = null;
                TransactionInfo transactionInfo = TransactionInfo.now(
                        "Transfer",
                        SystemName.MNG.getValue(),
                        "");

                if(
                        StringUtils.equals(h2OrderMEntities.get(0).getCOrderTy() , TransportOrderType.OUTBOUND.getValue())
                         || StringUtils.equals(h2OrderMEntities.get(0).getCOrderTy() , TransportOrderType.INBOUND.getValue())
                ){
                    H2OrderMEntity master = h2OrderMEntities.get(0);
                    H2OrderDEntity detail = h2OrderDEntities.get(0);
                    TransportOrderCreateCommand command =
                            TransportOrderCreateCommand
                                    .builder()
                                    .transactionInfo(transactionInfo)
                                    .transportOrderId(master.getCOrderId())
                                    .idocId(idocEntity.getLineId())
                                    .description("")
                                    .carrierName(detail.getCCoId())
                                    .transportType(master.getCOrderTy())
                                    .transportStatus(TransportOrderStatus.CREATED.getValue())
                                    .lastTransactionCode("")
                                    .carrierType(detail.getCCoTy())
                                    .priority(master.getCOrderPrio())
                                    .galId(master.getCGalId())
                                    .galWarehouse(master.getCGalWhs())
                                    .locationId(master.getCLocId())
                                    .workStationId(master.getCWcId())
                                    //.sourceZoneName() relocation 시 사용
                                    .destinationZoneName(detail.getCZone())
                                    //.errorText()
                                    //.actualWeight()
                                    .requestedZoneName(detail.getCZone())
                                    //.actualZoneName()
                                    //.actualLocationId()
                                    .travelProfile(detail.getCDrivingProfile())
                                    .createTime(transactionInfo.eventTime())
                                    //.releaseTime()
                                    //.completeTime()
                                    //.retrievalTime(master.getCDtPick())
                                    .createUser(SystemName.GAL.getValue())
                                    //.releaseUser()
                                    //.completeUser()
                                    .build();
                    transportOrder = TransportOrder.create(command);
                }
                else if(StringUtils.equals(h2OrderMEntities.get(0).getCOrderTy() , TransportOrderType.RELOCATION.getValue())){
                    H2OrderMEntity master = h2OrderMEntities.get(0);
                    H2OrderDEntity source = h2OrderDEntities.get(0);
                    H2OrderDEntity target = h2OrderDEntities.get(1);

                    TransportOrderCreateCommand command =
                            TransportOrderCreateCommand
                                    .builder()
                                    .transactionInfo(transactionInfo)
                                    .transportOrderId(master.getCOrderId())
                                    .idocId(idocEntity.getLineId())
                                    .description("")
                                    .carrierName(source.getCCoId())
                                    .transportType(master.getCOrderTy())
                                    .transportStatus(TransportOrderStatus.CREATED.getValue())
                                    .lastTransactionCode("")
                                    .carrierType(source.getCCoTy())
                                    .priority(master.getCOrderPrio())
                                    .galId(master.getCGalId())
                                    .galWarehouse(master.getCGalWhs())
                                    .locationId(master.getCLocId())
                                    .workStationId(master.getCWcId())
                                    .sourceZoneName(source.getCZone()) //relocation 시 사용
                                    .destinationZoneName(target.getCZone())
                                    //.errorText()
                                    //.actualWeight()
                                    .requestedZoneName(target.getCZone())
                                    //.actualZoneName()
                                    //.actualLocationId()
                                    //.drivingProfile() // carrier 가 가지고 있는 profile사용
                                    .createTime(transactionInfo.eventTime())
                                    //.releaseTime()
                                    //.completeTime()
                                    //.retrievalTime(master.getCDtPick())
                                    .createUser(SystemName.GAL.getValue())
                                    //.releaseUser()
                                    //.completeUser()
                                    .build();
                    transportOrder = TransportOrder.create(command);
                }

                // transportOrder 객체 생성

                if(ObjectUtils.isEmpty(transportOrder)){
                    throw new RuntimeException("transportOrder create error");
                }

                // insert transportOrder (MSSQL 트랜잭션)
                transportOrder = transportOrderService.createTransportOrder(transportOrder);

                // 정상적으로 수행했기 때문에, errorCode (60 : processed)와 dtimemode를 수정
                insertExternalInterfaceService.transferCompleted(idocEntity.getLineId());

                if(IdocTypeId.INBOUND.getValue().equals( idocEntity.getIdocTypId() )
                        || IdocTypeId.RELOCATION.getValue().equals( idocEntity.getIdocTypId() )) {
                    // 메시지 전송
                    String transactionId = FormatUtils.getTransactionId(transactionInfo.eventTime());

                    BaseMessage<TransportOrderRequestBody> request = new BaseMessage<>();
                    request.setMessageFrom(SystemName.MNG.getValue());
                    request.setMessageOwner(SystemName.MNG.getValue());
                    request.setMessageTo(SystemName.MNG.getValue());
                    request.setEventTime(transactionId);
                    request.setResultMessage("");
                    request.setResultCode(ResultCode.OK.getValue());
                    request.setTransactionId(transactionId);
                    request.setMessageName(MessageList.TRANSPORT_ORDER_REQUEST.getMessageName());
                    TransportOrderRequestBody body =
                            TransportOrderRequestBody
                                    .builder()
                                    .id(transportOrder.getId())
                                    .build();

                    request.setBody(body);
                    jsonUtils.writePrettyJson(request);

                    // 5. String 으로 변환된 메시지 reply
                    rabbitTemplate.convertAndSend( RabbitConfig.EXCHANGE_TEX,RabbitConfig.ROUTING_TEX, request );
                    log.info("Send Completed");
                }
            } catch (Exception e) {
                // 만일 transfer 도중 문제가 생겼다면
                // errorcode 99 dtimemode를 수정
                insertExternalInterfaceService.transferFail(idocEntity.getLineId());
            }
        }

    }


}