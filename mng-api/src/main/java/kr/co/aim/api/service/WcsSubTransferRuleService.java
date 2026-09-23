package kr.co.aim.api.service;

import kr.co.aim.api.dto.WcsSubTransferRuleCreateRequestDto;
import kr.co.aim.api.dto.WcsSubTransferRuleResponse;
import kr.co.aim.api.dto.WcsSubTransferRuleUpdateRequestDto;
import kr.co.aim.common.condition.WcsSubTransferRuleSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.WcsSubTransferRuleCreateCommand;
import kr.co.aim.domain.command.WcsSubTransferRuleUpdateCommand;
import kr.co.aim.domain.model.WcsSubTransferRule;
import kr.co.aim.domain.repository.WcsSubTransferRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WcsSubTransferRuleService {

    private final WcsSubTransferRuleRepository wcsSubTransferRuleRepository;

    /**
     * 조건 및 페이징 기반 WCS 세부 반송 룰 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<WcsSubTransferRuleResponse> findSubTransferRules(WcsSubTransferRuleSearchCondition condition, Pageable pageable) {
        Page<WcsSubTransferRule> pageResult = wcsSubTransferRuleRepository.findSubTransferRules(condition, pageable);
        List<WcsSubTransferRuleResponse> content = new ArrayList<>();

        for (WcsSubTransferRule rule : pageResult.getContent()) {
            if (rule != null) {
                content.add(WcsSubTransferRuleResponse.fromDomain(rule));
            }
        }

        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 4개 복합키 기반 WCS 세부 반송 룰 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public WcsSubTransferRuleResponse findById(String factoryName, String equipmentName, String moduleName, Long routeLinkId) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName)
                || !StringUtils.hasText(moduleName) || routeLinkId == null) {
            throw new IllegalArgumentException("조회할 세부 반송 룰 복합키 식별자 정보가 누락되었습니다.");
        }

        Optional<WcsSubTransferRule> optionalRule = wcsSubTransferRuleRepository.findById(
                factoryName.trim(), equipmentName.trim(), moduleName.trim(), routeLinkId);
        if (optionalRule.isEmpty()) {
            throw new IllegalArgumentException("해당 WCS 세부 반송 룰이 존재하지 않습니다. [factoryName: " + factoryName
                    + ", equipmentName: " + equipmentName + ", moduleName: " + moduleName + ", routeLinkId: " + routeLinkId + "]");
        }

        return WcsSubTransferRuleResponse.fromDomain(optionalRule.get());
    }

    /**
     * 공장 및 설비별 세부 반송 룰 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<WcsSubTransferRuleResponse> findByFactoryNameAndEquipmentName(String factoryName, String equipmentName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName)) {
            return new ArrayList<>();
        }

        List<WcsSubTransferRule> list = wcsSubTransferRuleRepository.findByFactoryNameAndEquipmentName(
                factoryName.trim(), equipmentName.trim());
        List<WcsSubTransferRuleResponse> result = new ArrayList<>();

        for (WcsSubTransferRule rule : list) {
            if (rule != null) {
                result.add(WcsSubTransferRuleResponse.fromDomain(rule));
            }
        }

        return result;
    }

    /**
     * 신규 WCS 세부 반송 룰 등록 (CREATE)
     * - 4개 복합키 중복 검증
     * - 도메인 내부 WcsSubTransferRule.create(command) 호출
     * - SUB_TRANSFER_RULE 저장 (이력 테이블 부재)
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsSubTransferRuleResponse createSubTransferRule(WcsSubTransferRuleCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("세부 반송 룰 등록 정보가 유효하지 않습니다.");
        }
        if (!StringUtils.hasText(dto.getFactoryName())) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getEquipmentName())) {
            throw new IllegalArgumentException("설비 명은 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getModuleName())) {
            throw new IllegalArgumentException("모듈 명은 필수 입력 항목입니다.");
        }
        if (dto.getRouteLinkId() == null) {
            throw new IllegalArgumentException("경로 링크 ID는 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        String equipmentName = dto.getEquipmentName().trim();
        String moduleName = dto.getModuleName().trim();
        Long routeLinkId = dto.getRouteLinkId();

        // 1. 복합키 중복 검증
        if (wcsSubTransferRuleRepository.existsById(factoryName, equipmentName, moduleName, routeLinkId)) {
            throw new IllegalArgumentException("이미 등록된 WCS 세부 반송 룰입니다. [factoryName: " + factoryName
                    + ", equipmentName: " + equipmentName + ", moduleName: " + moduleName + ", routeLinkId: " + routeLinkId + "]");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsSubTransferRuleCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs sub transfer rule created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 생성
        WcsSubTransferRuleCreateCommand command = WcsSubTransferRuleCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .equipmentName(equipmentName)
                .moduleName(moduleName)
                .routeLinkId(routeLinkId)
                .carrierCount(dto.getCarrierCount())
                .description(dto.getDescription())
                .moduleType(dto.getModuleType())
                .ngStatus(dto.getNgStatus())
                .build();

        WcsSubTransferRule rule = WcsSubTransferRule.create(command);

        // 4. SUB_TRANSFER_RULE 테이블 저장
        WcsSubTransferRule savedRule = wcsSubTransferRuleRepository.save(rule);

        log.info("WcsSubTransferRule created successfully: [factoryName={}, equipmentName={}, moduleName={}, routeLinkId={}]",
                factoryName, equipmentName, moduleName, routeLinkId);

        return WcsSubTransferRuleResponse.fromDomain(savedRule);
    }

    /**
     * 4개 복합키 기반 WCS 세부 반송 룰 수정 (UPDATE)
     * - 대상 존재 검증
     * - 도메인 내부 rule.update(command) 호출
     * - SUB_TRANSFER_RULE 저장 (이력 테이블 부재)
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsSubTransferRuleResponse updateSubTransferRule(
            String factoryName, String equipmentName, String moduleName, Long routeLinkId,
            WcsSubTransferRuleUpdateRequestDto dto) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName)
                || !StringUtils.hasText(moduleName) || routeLinkId == null) {
            throw new IllegalArgumentException("수정할 대상 세부 반송 룰 복합키 식별자 정보가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 세부 반송 룰 정보가 유효하지 않습니다.");
        }

        // 1. 대상 조회
        Optional<WcsSubTransferRule> optionalRule = wcsSubTransferRuleRepository.findById(
                factoryName.trim(), equipmentName.trim(), moduleName.trim(), routeLinkId);
        if (optionalRule.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 WCS 세부 반송 룰이 존재하지 않습니다. [factoryName: " + factoryName
                    + ", equipmentName: " + equipmentName + ", moduleName: " + moduleName + ", routeLinkId: " + routeLinkId + "]");
        }

        WcsSubTransferRule ruleDomain = optionalRule.get();

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsSubTransferRuleModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs sub transfer rule modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 수정
        WcsSubTransferRuleUpdateCommand command = WcsSubTransferRuleUpdateCommand.builder()
                .transactionInfo(tx)
                .carrierCount(dto.getCarrierCount())
                .description(dto.getDescription())
                .moduleType(dto.getModuleType())
                .ngStatus(dto.getNgStatus())
                .build();

        ruleDomain.update(command);

        // 4. SUB_TRANSFER_RULE 테이블 저장
        WcsSubTransferRule updatedRule = wcsSubTransferRuleRepository.save(ruleDomain);

        log.info("WcsSubTransferRule updated successfully: [factoryName={}, equipmentName={}, moduleName={}, routeLinkId={}]",
                factoryName, equipmentName, moduleName, routeLinkId);

        return WcsSubTransferRuleResponse.fromDomain(updatedRule);
    }

    /**
     * 4개 복합키 기반 WCS 세부 반송 룰 삭제 (DELETE)
     * - 대상 존재 확인
     * - SUB_TRANSFER_RULE 테이블에서 삭제 (이력 테이블 부재)
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteSubTransferRule(String factoryName, String equipmentName, String moduleName, Long routeLinkId) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName)
                || !StringUtils.hasText(moduleName) || routeLinkId == null) {
            throw new IllegalArgumentException("삭제할 대상 세부 반송 룰 복합키 식별자 정보가 누락되었습니다.");
        }

        if (!wcsSubTransferRuleRepository.existsById(factoryName.trim(), equipmentName.trim(), moduleName.trim(), routeLinkId)) {
            throw new IllegalArgumentException("삭제할 대상 WCS 세부 반송 룰이 존재하지 않습니다. [factoryName: " + factoryName
                    + ", equipmentName: " + equipmentName + ", moduleName: " + moduleName + ", routeLinkId: " + routeLinkId + "]");
        }

        // SUB_TRANSFER_RULE 테이블에서 삭제
        wcsSubTransferRuleRepository.deleteById(factoryName.trim(), equipmentName.trim(), moduleName.trim(), routeLinkId);

        log.info("WcsSubTransferRule deleted successfully: [factoryName={}, equipmentName={}, moduleName={}, routeLinkId={}]",
                factoryName, equipmentName, moduleName, routeLinkId);
    }
}
