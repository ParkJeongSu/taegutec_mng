package kr.co.aim.api.service;

import kr.co.aim.api.dto.WcsRouteLinkCreateRequestDto;
import kr.co.aim.api.dto.WcsRouteLinkResponse;
import kr.co.aim.api.dto.WcsRouteLinkUpdateRequestDto;
import kr.co.aim.common.condition.WcsRouteLinkSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.WcsRouteLinkCreateCommand;
import kr.co.aim.domain.command.WcsRouteLinkUpdateCommand;
import kr.co.aim.domain.model.WcsRouteLink;
import kr.co.aim.domain.repository.WcsRouteLinkRepository;
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
public class WcsRouteLinkService {

    private final WcsRouteLinkRepository wcsRouteLinkRepository;

    /**
     * 조건 및 페이징 기반 WCS 경로 링크 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<WcsRouteLinkResponse> findRouteLinks(WcsRouteLinkSearchCondition condition, Pageable pageable) {
        Page<WcsRouteLink> pageResult = wcsRouteLinkRepository.findRouteLinks(condition, pageable);
        List<WcsRouteLinkResponse> content = new ArrayList<>();

        for (WcsRouteLink link : pageResult.getContent()) {
            if (link != null) {
                content.add(WcsRouteLinkResponse.fromDomain(link));
            }
        }

        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 2개 복합키(factoryName, routeLinkId) 기반 WCS 경로 링크 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public WcsRouteLinkResponse findById(String factoryName, Long routeLinkId) {
        if (!StringUtils.hasText(factoryName) || routeLinkId == null) {
            throw new IllegalArgumentException("조회할 경로 링크 복합키 식별자 정보가 누락되었습니다.");
        }

        Optional<WcsRouteLink> optionalLink = wcsRouteLinkRepository.findById(factoryName.trim(), routeLinkId);
        if (optionalLink.isEmpty()) {
            throw new IllegalArgumentException("해당 WCS 경로 링크가 존재하지 않습니다. [factoryName: " + factoryName + ", routeLinkId: " + routeLinkId + "]");
        }

        return WcsRouteLinkResponse.fromDomain(optionalLink.get());
    }

    /**
     * 공장별 WCS 경로 링크 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<WcsRouteLinkResponse> findByFactoryName(String factoryName) {
        if (!StringUtils.hasText(factoryName)) {
            return new ArrayList<>();
        }

        List<WcsRouteLink> list = wcsRouteLinkRepository.findByFactoryName(factoryName.trim());
        List<WcsRouteLinkResponse> result = new ArrayList<>();

        for (WcsRouteLink link : list) {
            if (link != null) {
                result.add(WcsRouteLinkResponse.fromDomain(link));
            }
        }

        return result;
    }

    /**
     * 신규 WCS 경로 링크 등록 (CREATE)
     * - 복합키 중복 검증
     * - 도메인 내부 WcsRouteLink.create(command) 호출
     * - ROUTE_LINK 저장 (이력 테이블 부재)
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsRouteLinkResponse createRouteLink(WcsRouteLinkCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("경로 링크 등록 정보가 유효하지 않습니다.");
        }
        if (!StringUtils.hasText(dto.getFactoryName())) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (dto.getRouteLinkId() == null) {
            throw new IllegalArgumentException("경로 링크 ID는 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        Long routeLinkId = dto.getRouteLinkId();

        // 1. 복합키 중복 검증
        if (wcsRouteLinkRepository.existsById(factoryName, routeLinkId)) {
            throw new IllegalArgumentException("이미 등록된 WCS 경로 링크입니다. [factoryName: " + factoryName + ", routeLinkId: " + routeLinkId + "]");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsRouteLinkCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs route link created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 생성
        WcsRouteLinkCreateCommand command = WcsRouteLinkCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .routeLinkId(routeLinkId)
                .description(dto.getDescription())
                .fromNodeId(dto.getFromNodeId())
                .length(dto.getLength())
                .passYn(dto.getPassYn())
                .priority(dto.getPriority())
                .processType(dto.getProcessType())
                .routeLinkType(dto.getRouteLinkType())
                .toNodeId(dto.getToNodeId())
                .usableYn(dto.getUsableYn())
                .useYn(dto.getUseYn())
                .build();

        WcsRouteLink routeLink = WcsRouteLink.create(command);

        // 4. ROUTE_LINK 테이블 저장
        WcsRouteLink savedRouteLink = wcsRouteLinkRepository.save(routeLink);

        log.info("WcsRouteLink created successfully: [factoryName={}, routeLinkId={}]", factoryName, routeLinkId);

        return WcsRouteLinkResponse.fromDomain(savedRouteLink);
    }

    /**
     * 복합키 기반 WCS 경로 링크 수정 (UPDATE)
     * - 대상 존재 검증
     * - 도메인 내부 routeLink.update(command) 호출
     * - ROUTE_LINK 저장 (이력 테이블 부재)
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsRouteLinkResponse updateRouteLink(String factoryName, Long routeLinkId, WcsRouteLinkUpdateRequestDto dto) {
        if (!StringUtils.hasText(factoryName) || routeLinkId == null) {
            throw new IllegalArgumentException("수정할 대상 경로 링크 복합키 식별자 정보가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 경로 링크 정보가 유효하지 않습니다.");
        }

        // 1. 대상 조회
        Optional<WcsRouteLink> optionalLink = wcsRouteLinkRepository.findById(factoryName.trim(), routeLinkId);
        if (optionalLink.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 WCS 경로 링크가 존재하지 않습니다. [factoryName: " + factoryName + ", routeLinkId: " + routeLinkId + "]");
        }

        WcsRouteLink routeLinkDomain = optionalLink.get();

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsRouteLinkModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs route link modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 수정
        WcsRouteLinkUpdateCommand command = WcsRouteLinkUpdateCommand.builder()
                .transactionInfo(tx)
                .description(dto.getDescription())
                .fromNodeId(dto.getFromNodeId())
                .length(dto.getLength())
                .passYn(dto.getPassYn())
                .priority(dto.getPriority())
                .processType(dto.getProcessType())
                .routeLinkType(dto.getRouteLinkType())
                .toNodeId(dto.getToNodeId())
                .usableYn(dto.getUsableYn())
                .useYn(dto.getUseYn())
                .build();

        routeLinkDomain.update(command);

        // 4. ROUTE_LINK 테이블 저장
        WcsRouteLink updatedRouteLink = wcsRouteLinkRepository.save(routeLinkDomain);

        log.info("WcsRouteLink updated successfully: [factoryName={}, routeLinkId={}]", factoryName, routeLinkId);

        return WcsRouteLinkResponse.fromDomain(updatedRouteLink);
    }

    /**
     * 복합키 기반 WCS 경로 링크 삭제 (DELETE)
     * - 대상 존재 확인
     * - ROUTE_LINK 테이블에서 삭제 (이력 테이블 부재)
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteRouteLink(String factoryName, Long routeLinkId) {
        if (!StringUtils.hasText(factoryName) || routeLinkId == null) {
            throw new IllegalArgumentException("삭제할 대상 경로 링크 복합키 식별자 정보가 누락되었습니다.");
        }

        if (!wcsRouteLinkRepository.existsById(factoryName.trim(), routeLinkId)) {
            throw new IllegalArgumentException("삭제할 대상 WCS 경로 링크가 존재하지 않습니다. [factoryName: " + factoryName + ", routeLinkId: " + routeLinkId + "]");
        }

        // ROUTE_LINK 테이블에서 삭제
        wcsRouteLinkRepository.deleteById(factoryName.trim(), routeLinkId);

        log.info("WcsRouteLink deleted successfully: [factoryName={}, routeLinkId={}]", factoryName, routeLinkId);
    }
}
