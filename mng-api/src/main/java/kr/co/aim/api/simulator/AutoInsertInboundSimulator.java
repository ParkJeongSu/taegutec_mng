package kr.co.aim.api.simulator;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.api.dto.InsertAutoTestResultCreateRequestDto;
import kr.co.aim.api.service.HistoryService;
import kr.co.aim.api.service.InsertAutoTestService;
import kr.co.aim.api.service.NamingRuleService;
import kr.co.aim.api.service.TransportJobService;
import kr.co.aim.api.vo.insert.sim.OrderCreateVo;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.TransportJobRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.TransportJobCreateCommand;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.*;
import kr.co.aim.infra.config.RabbitConfig;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderDEntity;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderMEntity;
import kr.co.aim.infra.persistence.db2entity.insert.IdocEntity;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.H2OrderDJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.H2OrderMJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.H2TransJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.IdocJpaRepository;
import kr.co.aim.infra.persistence.entity.TransportOrderHistoryEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
@Profile({"web"})
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class AutoInsertInboundSimulator {

    private final InsertAutoTestRepository insertAutoTestRepository;
    private final InsertAutoTestService insertAutoTestService;
    private final WcsCarrierRepository wcsCarrierRepository;
    private final WcsPortRepository wcsPortRepository;

    private final IdocJpaRepository idocJpaRepository;
    private final H2OrderMJpaRepository h2OrderMJpaRepository;
    private final H2OrderDJpaRepository h2OrderDJpaRepository;


    /**
     * 10초마다 inbound 상태가 가능하다면 inbound 명령을 시작
     */
    @Scheduled(fixedRate = 10000)
    public void autoInsertInboundSimulator() {
        // 최초 inbound 가능 상태가 된다면, InsertAutoTestResult 에 시작 상태로 만든다

        //

        List<InsertAutoTest> insertAutoTestList = insertAutoTestRepository.findAll();
        for(InsertAutoTest  insertAutoTest : insertAutoTestList){

            // wcs 캐리어 조회
            Optional<WcsCarrier> optionalWcsCarrier = wcsCarrierRepository.findByCarrierNameAndFactoryName(insertAutoTest.getCarrierName(),"insert");
            WcsCarrier wcsCarrier;
            if(optionalWcsCarrier.isPresent()){
                wcsCarrier = optionalWcsCarrier.get();
            }
            else{
                continue;
            }

            Optional<WcsPort> optionalWcsPort =  wcsPortRepository.findByEquipmentNameAndPortName (insertAutoTest.getWorkStationId(), wcsCarrier.getCurrentPositionName());
            WcsPort wcsPort;
            if(optionalWcsPort.isPresent()){
                wcsPort = optionalWcsPort.get();
            }
            else{
                continue;
            }

            // inbound 가능 상태 판단
            if(StringUtils.equals(insertAutoTest.getInboundPortName(),wcsPort.getPortName())){

            }else{
                continue;
            }

            // create insertAuto Test Result
            InsertAutoTestResultCreateRequestDto dto =
                    InsertAutoTestResultCreateRequestDto
                            .builder()
                            .autoTestId(insertAutoTest.getId())
                            .state("success")
                            .result("outbound-completed")
                            .build();
            insertAutoTestService.addTestResult(insertAutoTest.getId(),dto);

            // db2 inbound order Create

            createInbound(insertAutoTest,wcsPort,wcsCarrier);
        }

    }

    private IdocEntity buildBaseIdocForOrderCreate(LocalDateTime now,IdocTypeId idocTypeId) {
        return IdocEntity.builder()
                .lineId(idocJpaRepository.findMaxLineIdForCreateOrder())
                .idocTypId(idocTypeId.getValue())
                .state(IdocState.INITIAL.getValue())
                .errorCode(IdocErrorCode.INIT.getValue())
                .source(IdocMachine.GAL.getValue())
                .destination( IdocMachine.MNG.getValue())
                .dtimeCre(now)
                .dtimeMod(now)
                .modCnt(0)
                .build();
    }

    @Transactional(value = "db2TransactionManager")
    public void createInbound(InsertAutoTest insertAutoTest, WcsPort wcsPort, WcsCarrier wcsCarrier){

        Long maxOrderId = h2OrderMJpaRepository.findMaxOrderId();
        String orderId = maxOrderId.toString();           // 생성 orderId
        String location = insertAutoTest.getInboundPortName();        // 넣는 위치
        String locationGroup = insertAutoTest.getWorkStationId();   // 넣는 위치 그룹

        int number = ThreadLocalRandom.current().nextInt(1_000_000, 10_000_000);
        String galId = String.valueOf(number);           // GAL ID
        String carrierId = insertAutoTest.getCarrierName();       // Carrier ID
        String carrierType = "TBD";     // Carrier Type
        String zoneName = insertAutoTest.getZoneName();        // 창고 Zone Name
        String speed = insertAutoTest.getTravelProfile();           // 속도
        Integer orderPriority = 5;     // order의 우선순위

        LocalDateTime now = LocalDateTime.now().withNano(0);

        IdocEntity newIdoc = buildBaseIdocForOrderCreate(now,IdocTypeId.INBOUND);
        idocJpaRepository.save(newIdoc);

        H2OrderMEntity h2OrderMEntity =
                H2OrderMEntity
                        .builder()
                        .lineId(h2OrderMJpaRepository.findMaxLineId())
                        .idocId(newIdoc.getLineId())
                        .dtimeCre(now)
                        .dtimeMod(now)
                        .usrMod("SIMULATOR")
                        .pgmMod("SIMULATOR")
                        .modCnt(0)
                        .dataCode("10")
                        .bookCtrl("10")
                        .cClient("001")
                        .cOrderId(orderId)
                        .cOrderTy(TransportOrderType.INBOUND.getValue())
                        .cOrderPrio(orderPriority)
                        //.cTCode()
                        .cLocId(location)
                        .cWcId(locationGroup)
                        .cGalId(galId)
                        //.cGalWhs()
                        .build();
        h2OrderMJpaRepository.save(h2OrderMEntity);

        H2OrderDEntity h2OrderDEntity =
                H2OrderDEntity
                        .builder()
                        .lineId(h2OrderDJpaRepository.findMaxLineId())
                        .idocId(newIdoc.getLineId())
                        .dtimeCre(now)
                        .dtimeMod(now)
                        .usrMod("SIMULATOR")
                        .pgmMod("SIMULATOR")
                        .modCnt(0)
                        .dataCode("20")
                        .cClient("001")
                        .cOrderId(orderId)
                        .cOrderTy(TransportOrderType.INBOUND.getValue())
                        .cOrderLn(1L)
                        .cCoId(carrierId)
                        .cCoTy(carrierType)
                        .cZone(zoneName)
                        .cDrivingProfile(speed)
                        .build();
        h2OrderDJpaRepository.save(h2OrderDEntity);

    }


}