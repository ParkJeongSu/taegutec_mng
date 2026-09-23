package kr.co.aim.api.service;

import kr.co.aim.api.dto.WcsCarrierCreateRequestDto;
import kr.co.aim.api.dto.WcsCarrierResponse;
import kr.co.aim.api.dto.WcsCarrierUpdateRequestDto;
import kr.co.aim.common.condition.WcsCarrierSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.WcsCarrierCreateCommand;
import kr.co.aim.domain.command.WcsCarrierUpdateCommand;
import kr.co.aim.domain.model.WcsCarrier;
import kr.co.aim.domain.repository.WcsCarrierRepository;
import kr.co.aim.infra.persistence.entity.WcsCarrierHistoryEntity;
import kr.co.aim.infra.persistence.mapper.WcsCarrierMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsCarrierHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WcsCarrierService {

    private final WcsCarrierRepository wcsCarrierRepository;
    private final WcsCarrierHistoryJpaRepository wcsCarrierHistoryJpaRepository;
    private final WcsCarrierMapper wcsCarrierMapper;

    /**
     * 조건 및 페이징 기반 WCS 캐리어 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<WcsCarrierResponse> findCarriers(WcsCarrierSearchCondition condition, Pageable pageable) {
        Page<WcsCarrier> pageResult = wcsCarrierRepository.findCarriers(condition, pageable);
        List<WcsCarrierResponse> content = new ArrayList<>();

        for (WcsCarrier carrier : pageResult.getContent()) {
            if (carrier != null) {
                content.add(WcsCarrierResponse.fromDomain(carrier));
            }
        }

        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 복합키(carrierName, factoryName) 기반 WCS 캐리어 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public WcsCarrierResponse findById(String carrierName, String factoryName) {
        if (!StringUtils.hasText(carrierName) || !StringUtils.hasText(factoryName)) {
            throw new IllegalArgumentException("조회할 캐리어 명 및 공장 구분이 누락되었습니다.");
        }

        Optional<WcsCarrier> optionalCarrier = wcsCarrierRepository.findById(carrierName.trim(), factoryName.trim());
        if (optionalCarrier.isEmpty()) {
            throw new IllegalArgumentException("해당 WCS 캐리어가 존재하지 않습니다. (carrierName: " + carrierName + ", factoryName: " + factoryName + ")");
        }

        return WcsCarrierResponse.fromDomain(optionalCarrier.get());
    }

    /**
     * 신규 WCS 캐리어 등록 (CREATE)
     * - 복합키(carrierName, factoryName) 중복 검증
     * - 도메인 내부 WcsCarrier.create(command) 호출
     * - CARRIER 저장 및 CARRIER_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsCarrierResponse createCarrier(WcsCarrierCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("캐리어 등록 정보가 유효하지 않습니다.");
        }
        if (!StringUtils.hasText(dto.getCarrierName())) {
            throw new IllegalArgumentException("캐리어 명은 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getFactoryName())) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }

        String carrierName = dto.getCarrierName().trim();
        String factoryName = dto.getFactoryName().trim();

        // 1. 복합키 중복 검증
        if (wcsCarrierRepository.existsById(carrierName, factoryName)) {
            throw new IllegalArgumentException("이미 등록된 WCS 캐리어입니다. (carrierName: " + carrierName + ", factoryName: " + factoryName + ")");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsCarrierCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs carrier created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 생성
        WcsCarrierCreateCommand command = WcsCarrierCreateCommand.builder()
                .transactionInfo(tx)
                .carrierName(carrierName)
                .factoryName(factoryName)
                .afterProcess(dto.getAfterProcess())
                .beforeProcess(dto.getBeforeProcess())
                .carrierGroup(dto.getCarrierGroup())
                .carrierStatus(dto.getCarrierStatus())
                .createTime(dto.getCreateTime())
                .currentPositionName(dto.getCurrentPositionName())
                .hotLot(dto.getHotLot())
                .lotName(dto.getLotName())
                .owner(dto.getOwner())
                .previousCarrierStatus(dto.getPreviousCarrierStatus())
                .productQuantity(dto.getProductQuantity())
                .zoneName(dto.getZoneName())
                .currentEquipmentName(dto.getCurrentEquipmentName())
                .carrierDetailType(dto.getCarrierDetailType())
                .carrierType(dto.getCarrierType())
                .transferCommandName(dto.getTransferCommandName())
                .travelProfile(dto.getTravelProfile())
                .itemName(dto.getItemName())
                .orderId(dto.getOrderId())
                .orderLineNumber(dto.getOrderLineNumber())
                .productionType(dto.getProductionType())
                .inboundTime(dto.getInboundTime())
                .outboundTime(dto.getOutboundTime())
                .weight(dto.getWeight())
                .carrierUseCount(dto.getCarrierUseCount())
                .build();

        WcsCarrier carrier = WcsCarrier.create(command);

        // 4. CARRIER 테이블 저장
        WcsCarrier savedCarrier = wcsCarrierRepository.save(carrier);

        // 5. CARRIER_HISTORY 테이블 이력 적재
        WcsCarrierHistoryEntity historyEntity = wcsCarrierMapper.toHistoryEntity(savedCarrier);
        wcsCarrierHistoryJpaRepository.save(historyEntity);

        log.info("WcsCarrier created successfully: [carrierName={}, factoryName={}]", carrierName, factoryName);

        return WcsCarrierResponse.fromDomain(savedCarrier);
    }

    /**
     * 복합키 기반 WCS 캐리어 수정 (UPDATE)
     * - 대상 존재 검증
     * - 도메인 내부 carrier.update(command) 호출
     * - CARRIER 저장 및 CARRIER_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsCarrierResponse updateCarrier(String carrierName, String factoryName, WcsCarrierUpdateRequestDto dto) {
        if (!StringUtils.hasText(carrierName) || !StringUtils.hasText(factoryName)) {
            throw new IllegalArgumentException("수정할 대상 캐리어 명 및 공장 구분이 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 캐리어 정보가 유효하지 않습니다.");
        }

        // 1. 대상 조회
        Optional<WcsCarrier> optionalCarrier = wcsCarrierRepository.findById(carrierName.trim(), factoryName.trim());
        if (optionalCarrier.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 WCS 캐리어가 존재하지 않습니다. (carrierName: " + carrierName + ", factoryName: " + factoryName + ")");
        }

        WcsCarrier carrier = optionalCarrier.get();

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsCarrierModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs carrier modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 수정
        WcsCarrierUpdateCommand command = WcsCarrierUpdateCommand.builder()
                .transactionInfo(tx)
                .afterProcess(dto.getAfterProcess())
                .beforeProcess(dto.getBeforeProcess())
                .carrierGroup(dto.getCarrierGroup())
                .carrierStatus(dto.getCarrierStatus())
                .createTime(dto.getCreateTime())
                .currentPositionName(dto.getCurrentPositionName())
                .hotLot(dto.getHotLot())
                .lotName(dto.getLotName())
                .owner(dto.getOwner())
                .previousCarrierStatus(dto.getPreviousCarrierStatus())
                .productQuantity(dto.getProductQuantity())
                .zoneName(dto.getZoneName())
                .currentEquipmentName(dto.getCurrentEquipmentName())
                .carrierDetailType(dto.getCarrierDetailType())
                .carrierType(dto.getCarrierType())
                .transferCommandName(dto.getTransferCommandName())
                .travelProfile(dto.getTravelProfile())
                .itemName(dto.getItemName())
                .orderId(dto.getOrderId())
                .orderLineNumber(dto.getOrderLineNumber())
                .productionType(dto.getProductionType())
                .inboundTime(dto.getInboundTime())
                .outboundTime(dto.getOutboundTime())
                .weight(dto.getWeight())
                .carrierUseCount(dto.getCarrierUseCount())
                .build();

        carrier.update(command);

        // 4. CARRIER 테이블 저장
        WcsCarrier updatedCarrier = wcsCarrierRepository.save(carrier);

        // 5. CARRIER_HISTORY 테이블 이력 적재
        WcsCarrierHistoryEntity historyEntity = wcsCarrierMapper.toHistoryEntity(updatedCarrier);
        wcsCarrierHistoryJpaRepository.save(historyEntity);

        log.info("WcsCarrier updated successfully: [carrierName={}, factoryName={}]", carrierName, factoryName);

        return WcsCarrierResponse.fromDomain(updatedCarrier);
    }

    /**
     * 복합키 기반 WCS 캐리어 삭제 (DELETE)
     * - 대상 조회
     * - CARRIER_HISTORY 테이블에 삭제 이력 적재
     * - CARRIER 테이블에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteCarrier(String carrierName, String factoryName, String eventUser, String eventComment) {
        if (!StringUtils.hasText(carrierName) || !StringUtils.hasText(factoryName)) {
            throw new IllegalArgumentException("삭제할 대상 캐리어 명 및 공장 구분이 누락되었습니다.");
        }

        Optional<WcsCarrier> optionalCarrier = wcsCarrierRepository.findById(carrierName.trim(), factoryName.trim());
        if (optionalCarrier.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 WCS 캐리어가 존재하지 않습니다. (carrierName: " + carrierName + ", factoryName: " + factoryName + ")");
        }

        WcsCarrier carrier = optionalCarrier.get();

        // 1. 삭제 이벤트 정보 설정
        LocalDateTime now = LocalDateTime.now();
        carrier.setLastEventName("WcsCarrierDeleted");
        carrier.setLastEventTime(now);
        carrier.setLastEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        carrier.setLastEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Wcs carrier deleted");

        // 2. 삭제 이력 적재
        WcsCarrierHistoryEntity historyEntity = wcsCarrierMapper.toHistoryEntity(carrier);
        wcsCarrierHistoryJpaRepository.save(historyEntity);

        // 3. 캐리어 삭제
        wcsCarrierRepository.deleteById(carrierName.trim(), factoryName.trim());

        log.info("WcsCarrier deleted successfully: [carrierName={}, factoryName={}]", carrierName, factoryName);
    }
}
