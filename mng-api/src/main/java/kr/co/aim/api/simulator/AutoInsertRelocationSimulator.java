package kr.co.aim.api.simulator;
import kr.co.aim.api.dto.InsertAutoRelocationTestUpdateRequestDto;
import kr.co.aim.api.dto.InsertAutoTestResultCreateRequestDto;
import kr.co.aim.api.service.InsertAutoRelocationTestService;
import kr.co.aim.api.service.InsertAutoTestService;
import kr.co.aim.common.enums.*;
import kr.co.aim.domain.model.InsertAutoRelocationTest;
import kr.co.aim.domain.model.WcsCarrier;
import kr.co.aim.domain.model.WcsShelf;
import kr.co.aim.domain.repository.*;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderDEntity;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderMEntity;
import kr.co.aim.infra.persistence.db2entity.insert.IdocEntity;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.H2OrderDJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.H2OrderMJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.IdocJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler"})
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class AutoInsertRelocationSimulator {

    private final InsertAutoTestRepository insertAutoTestRepository;
    private final InsertAutoTestService insertAutoTestService;
    private final InsertAutoRelocationTestService insertAutoRelocationTestService;
    private final InsertAutoRelocationTestRepository insertAutoRelocationTestRepository;
    private final WcsCarrierRepository wcsCarrierRepository;
    private final WcsShelfRepository  wcsShelfRepository;

    private final IdocJpaRepository idocJpaRepository;
    private final H2OrderMJpaRepository h2OrderMJpaRepository;
    private final H2OrderDJpaRepository h2OrderDJpaRepository;

    /**
     * 10초마다 outbound 상태가 가능하다면 outbound 명령을 시작
     */
    @Scheduled(fixedRate = 10000)
    @SchedulerLock(name = "autoInsertRelocationSimulator",
            lockAtMostFor = "PT10S",
            lockAtLeastFor = "PT0.9S") // 1초 주기에 맞춰 잠금이 풀리도록 0.9초 설정
    public void autoInsertRelocationSimulator() {

        // 최초 inbound 가능 상태가 된다면, InsertAutoTestResult 에 시작 상태로 만든다

        List<InsertAutoRelocationTest> insertAutoRelocationTests = insertAutoRelocationTestRepository.findAll();
        for(InsertAutoRelocationTest insertAutoRelocationTest : insertAutoRelocationTests){

            // wcs 캐리어 조회
            Optional<WcsCarrier> optionalWcsCarrier = wcsCarrierRepository.findByCarrierNameAndFactoryName(insertAutoRelocationTest.getCarrierName(),"insert");
            WcsCarrier wcsCarrier;
            if(optionalWcsCarrier.isPresent()){
                wcsCarrier = optionalWcsCarrier.get();
            }
            else{
                continue;
            }

            Optional<WcsShelf> optionalWcsShelf1 = wcsShelfRepository.findByFactoryNameAndStockerNameAndShelfName(
                    "insert",
                    insertAutoRelocationTest.getWareHouseName1(),
                    wcsCarrier.getCurrentPositionName()
            );

            // outbound 가능 상태 판단
            WcsShelf wcsShelf;
            if(optionalWcsShelf1.isPresent()){

                if(StringUtils.equals(AutoTestState.RELOCATION_REQUESTED1.getValue(), insertAutoRelocationTest.getState())){
                    continue;
                }
                wcsShelf = optionalWcsShelf1.get();
                // create insertAuto Test Result
                InsertAutoTestResultCreateRequestDto dto =
                        InsertAutoTestResultCreateRequestDto
                                .builder()
                                .autoTestId(insertAutoRelocationTest.getId())
                                .state("success")
                                .result("relocation1-completed")
                                .build();
                insertAutoTestService.addTestResult(insertAutoRelocationTest.getId(),dto);

                InsertAutoRelocationTestUpdateRequestDto insertAutoRelocationTestUpdateRequestDto =
                        InsertAutoRelocationTestUpdateRequestDto
                                .builder()
                                .wareHouseName1(insertAutoRelocationTest.getWareHouseName1())
                                .wareHouseName2(insertAutoRelocationTest.getWareHouseName2())
                                .zoneName1(insertAutoRelocationTest.getZoneName1())
                                .zoneName2(insertAutoRelocationTest.getZoneName2())
                                .carrierName(insertAutoRelocationTest.getCarrierName())
                                .state(AutoTestState.RELOCATION_REQUESTED1.getValue())
                                .build();

                insertAutoRelocationTestService.updateAutoRelocationTest(insertAutoRelocationTest.getId(),insertAutoRelocationTestUpdateRequestDto);

                // db2 create outbound order
                createRelocation1(insertAutoRelocationTest,wcsShelf,wcsCarrier);
                continue;
            }

            Optional<WcsShelf> optionalWcsShelf2 = wcsShelfRepository.findByFactoryNameAndStockerNameAndShelfName(
                    "insert",
                    insertAutoRelocationTest.getWareHouseName2(),
                    wcsCarrier.getCurrentPositionName()
            );

            if(optionalWcsShelf2.isPresent()){

                if(StringUtils.equals(AutoTestState.RELOCATION_REQUESTED2.getValue(), insertAutoRelocationTest.getState())){
                    continue;
                }
                wcsShelf = optionalWcsShelf2.get();
                // create insertAuto Test Result
                InsertAutoTestResultCreateRequestDto dto =
                        InsertAutoTestResultCreateRequestDto
                                .builder()
                                .autoTestId(insertAutoRelocationTest.getId())
                                .state("success")
                                .result("relocation2-completed")
                                .build();
                insertAutoTestService.addTestResult(insertAutoRelocationTest.getId(),dto);

                InsertAutoRelocationTestUpdateRequestDto insertAutoRelocationTestUpdateRequestDto =
                        InsertAutoRelocationTestUpdateRequestDto
                                .builder()
                                .wareHouseName1(insertAutoRelocationTest.getWareHouseName1())
                                .wareHouseName2(insertAutoRelocationTest.getWareHouseName2())
                                .zoneName1(insertAutoRelocationTest.getZoneName1())
                                .zoneName2(insertAutoRelocationTest.getZoneName2())
                                .carrierName(insertAutoRelocationTest.getCarrierName())
                                .state(AutoTestState.RELOCATION_REQUESTED2.getValue())
                                .build();

                insertAutoRelocationTestService.updateAutoRelocationTest(insertAutoRelocationTest.getId(),insertAutoRelocationTestUpdateRequestDto);

                // db2 create outbound order
                createRelocation2(insertAutoRelocationTest,wcsShelf,wcsCarrier);
                continue;
            }



        }

    }

    private IdocEntity buildBaseIdocForOrderCreate(LocalDateTime now, IdocTypeId idocTypeId) {
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
    public void createRelocation1( InsertAutoRelocationTest insertAutoRelocationTest,WcsShelf wcsShelf, WcsCarrier wcsCarrier ){
        Long maxOrderId = h2OrderMJpaRepository.findMaxOrderId();
        String orderId = maxOrderId.toString();     // 생성 orderId
        //String location = insertAutoTest.getInboundPortName();        // 넣는 위치
        //String locationGroup = insertAutoTest.getWorkStationId();   // 넣는 위치 그룹
        int number = ThreadLocalRandom.current().nextInt(1_000_000, 10_000_000);
        String galId = String.valueOf(number);           // GAL ID
        String carrierId = insertAutoRelocationTest.getCarrierName();       // Carrier ID
        String carrierType = "TBD";     // Carrier Type
        Integer orderPriority = 5;     // order의 우선순위

        LocalDateTime now = LocalDateTime.now().withNano(0);

        String sourceZoneName = insertAutoRelocationTest.getZoneName1();
        String targetZoneName = insertAutoRelocationTest.getZoneName2();

        IdocEntity newIdoc = buildBaseIdocForOrderCreate(now,IdocTypeId.RELOCATION);
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
                        .cOrderTy(TransportOrderType.RELOCATION.getValue())
                        .cOrderPrio(orderPriority)
                        .cGalId(galId)
                        .build();
        h2OrderMJpaRepository.save(h2OrderMEntity);

        Long h2orderDLineId = h2OrderDJpaRepository.findMaxLineId();

        H2OrderDEntity sourceH2OrderDEntity =
                H2OrderDEntity
                        .builder()
                        .lineId(h2orderDLineId)
                        .idocId(newIdoc.getLineId())
                        .dtimeCre(now)
                        .dtimeMod(now)
                        .usrMod("SIMULATOR")
                        .pgmMod("SIMULATOR")
                        .modCnt(0)
                        .dataCode("40")
                        .cClient("001")
                        .cOrderId(orderId)
                        .cOrderTy(TransportOrderType.RELOCATION.getValue())
                        .cOrderLn(1L)
                        .cCoId(carrierId)
                        .cCoTy(carrierType)
                        .cZone(sourceZoneName)
                        .build();
        h2OrderDJpaRepository.save(sourceH2OrderDEntity);

        H2OrderDEntity targetH2OrderDEntity =
                H2OrderDEntity
                        .builder()
                        .lineId(h2orderDLineId+1)
                        .idocId(newIdoc.getLineId())
                        .dtimeCre(now)
                        .dtimeMod(now)
                        .usrMod("SIMULATOR")
                        .pgmMod("SIMULATOR")
                        .modCnt(0)
                        .dataCode("40")
                        .cClient("001")
                        .cOrderId(orderId)
                        .cOrderTy(TransportOrderType.RELOCATION.getValue())
                        .cOrderLn(1L)
                        .cCoId(carrierId)
                        .cCoTy(carrierType)
                        .cZone(targetZoneName)
                        .build();
        h2OrderDJpaRepository.save(targetH2OrderDEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void createRelocation2( InsertAutoRelocationTest insertAutoRelocationTest,WcsShelf wcsShelf, WcsCarrier wcsCarrier ){
        Long maxOrderId = h2OrderMJpaRepository.findMaxOrderId();
        String orderId = maxOrderId.toString();     // 생성 orderId
        //String location = insertAutoTest.getInboundPortName();        // 넣는 위치
        //String locationGroup = insertAutoTest.getWorkStationId();   // 넣는 위치 그룹
        int number = ThreadLocalRandom.current().nextInt(1_000_000, 10_000_000);
        String galId = String.valueOf(number);           // GAL ID
        String carrierId = insertAutoRelocationTest.getCarrierName();       // Carrier ID
        String carrierType = "TBD";     // Carrier Type
        Integer orderPriority = 5;     // order의 우선순위

        LocalDateTime now = LocalDateTime.now().withNano(0);

        String sourceZoneName = insertAutoRelocationTest.getZoneName2();
        String targetZoneName = insertAutoRelocationTest.getZoneName1();

        IdocEntity newIdoc = buildBaseIdocForOrderCreate(now,IdocTypeId.RELOCATION);
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
                        .cOrderTy(TransportOrderType.RELOCATION.getValue())
                        .cOrderPrio(orderPriority)
                        .cGalId(galId)
                        .build();
        h2OrderMJpaRepository.save(h2OrderMEntity);

        Long h2orderDLineId = h2OrderDJpaRepository.findMaxLineId();

        H2OrderDEntity sourceH2OrderDEntity =
                H2OrderDEntity
                        .builder()
                        .lineId(h2orderDLineId)
                        .idocId(newIdoc.getLineId())
                        .dtimeCre(now)
                        .dtimeMod(now)
                        .usrMod("SIMULATOR")
                        .pgmMod("SIMULATOR")
                        .modCnt(0)
                        .dataCode("40")
                        .cClient("001")
                        .cOrderId(orderId)
                        .cOrderTy(TransportOrderType.RELOCATION.getValue())
                        .cOrderLn(1L)
                        .cCoId(carrierId)
                        .cCoTy(carrierType)
                        .cZone(sourceZoneName)
                        .build();
        h2OrderDJpaRepository.save(sourceH2OrderDEntity);

        H2OrderDEntity targetH2OrderDEntity =
                H2OrderDEntity
                        .builder()
                        .lineId(h2orderDLineId+1)
                        .idocId(newIdoc.getLineId())
                        .dtimeCre(now)
                        .dtimeMod(now)
                        .usrMod("SIMULATOR")
                        .pgmMod("SIMULATOR")
                        .modCnt(0)
                        .dataCode("40")
                        .cClient("001")
                        .cOrderId(orderId)
                        .cOrderTy(TransportOrderType.RELOCATION.getValue())
                        .cOrderLn(1L)
                        .cCoId(carrierId)
                        .cCoTy(carrierType)
                        .cZone(targetZoneName)
                        .build();
        h2OrderDJpaRepository.save(targetH2OrderDEntity);
    }

}