package kr.co.aim.api.service;

import kr.co.aim.api.dto.WcsRouteNodeCreateRequestDto;
import kr.co.aim.api.dto.WcsRouteNodeResponse;
import kr.co.aim.api.dto.WcsRouteNodeUpdateRequestDto;
import kr.co.aim.common.condition.WcsRouteNodeSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.WcsRouteNodeCreateCommand;
import kr.co.aim.domain.command.WcsRouteNodeUpdateCommand;
import kr.co.aim.domain.model.WcsRouteNode;
import kr.co.aim.domain.repository.WcsRouteNodeRepository;
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
public class WcsRouteNodeService {

    private final WcsRouteNodeRepository wcsRouteNodeRepository;

    /**
     * 조건 및 페이징 기반 WCS 경로 노드 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<WcsRouteNodeResponse> findRouteNodes(WcsRouteNodeSearchCondition condition, Pageable pageable) {
        Page<WcsRouteNode> pageResult = wcsRouteNodeRepository.findRouteNodes(condition, pageable);
        List<WcsRouteNodeResponse> content = new ArrayList<>();

        for (WcsRouteNode node : pageResult.getContent()) {
            if (node != null) {
                content.add(WcsRouteNodeResponse.fromDomain(node));
            }
        }

        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 2개 복합키(factoryName, routeNodeId) 기반 WCS 경로 노드 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public WcsRouteNodeResponse findById(String factoryName, Long routeNodeId) {
        if (!StringUtils.hasText(factoryName) || routeNodeId == null) {
            throw new IllegalArgumentException("조회할 경로 노드 복합키 식별자 정보가 누락되었습니다.");
        }

        Optional<WcsRouteNode> optionalNode = wcsRouteNodeRepository.findById(factoryName.trim(), routeNodeId);
        if (optionalNode.isEmpty()) {
            throw new IllegalArgumentException("해당 WCS 경로 노드가 존재하지 않습니다. [factoryName: " + factoryName + ", routeNodeId: " + routeNodeId + "]");
        }

        return WcsRouteNodeResponse.fromDomain(optionalNode.get());
    }

    /**
     * 공장별 WCS 경로 노드 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<WcsRouteNodeResponse> findByFactoryName(String factoryName) {
        if (!StringUtils.hasText(factoryName)) {
            return new ArrayList<>();
        }

        List<WcsRouteNode> list = wcsRouteNodeRepository.findByFactoryName(factoryName.trim());
        List<WcsRouteNodeResponse> result = new ArrayList<>();

        for (WcsRouteNode node : list) {
            if (node != null) {
                result.add(WcsRouteNodeResponse.fromDomain(node));
            }
        }

        return result;
    }

    /**
     * 신규 WCS 경로 노드 등록 (CREATE)
     * - 복합키 중복 검증
     * - 도메인 내부 WcsRouteNode.create(command) 호출
     * - ROUTE_NODE 저장 (이력 테이블 부재)
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsRouteNodeResponse createRouteNode(WcsRouteNodeCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("경로 노드 등록 정보가 유효하지 않습니다.");
        }
        if (!StringUtils.hasText(dto.getFactoryName())) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (dto.getRouteNodeId() == null) {
            throw new IllegalArgumentException("경로 노드 ID는 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        Long routeNodeId = dto.getRouteNodeId();

        // 1. 복합키 중복 검증
        if (wcsRouteNodeRepository.existsById(factoryName, routeNodeId)) {
            throw new IllegalArgumentException("이미 등록된 WCS 경로 노드입니다. [factoryName: " + factoryName + ", routeNodeId: " + routeNodeId + "]");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsRouteNodeCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs route node created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 생성
        WcsRouteNodeCreateCommand command = WcsRouteNodeCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .routeNodeId(routeNodeId)
                .bayId(dto.getBayId())
                .controllerType(dto.getControllerType())
                .craneId(dto.getCraneId())
                .description(dto.getDescription())
                .equipmentId(dto.getEquipmentId())
                .nodeEquipmentType(dto.getNodeEquipmentType())
                .nodeId(dto.getNodeId())
                .nodeName(dto.getNodeName())
                .nodeSeq(dto.getNodeSeq())
                .rerouteType(dto.getRerouteType())
                .routeNodeType(dto.getRouteNodeType())
                .unitId(dto.getUnitId())
                .useYn(dto.getUseYn())
                .build();

        WcsRouteNode routeNode = WcsRouteNode.create(command);

        // 4. ROUTE_NODE 테이블 저장
        WcsRouteNode savedRouteNode = wcsRouteNodeRepository.save(routeNode);

        log.info("WcsRouteNode created successfully: [factoryName={}, routeNodeId={}]", factoryName, routeNodeId);

        return WcsRouteNodeResponse.fromDomain(savedRouteNode);
    }

    /**
     * 복합키 기반 WCS 경로 노드 수정 (UPDATE)
     * - 대상 존재 검증
     * - 도메인 내부 routeNode.update(command) 호출
     * - ROUTE_NODE 저장 (이력 테이블 부재)
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsRouteNodeResponse updateRouteNode(String factoryName, Long routeNodeId, WcsRouteNodeUpdateRequestDto dto) {
        if (!StringUtils.hasText(factoryName) || routeNodeId == null) {
            throw new IllegalArgumentException("수정할 대상 경로 노드 복합키 식별자 정보가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 경로 노드 정보가 유효하지 않습니다.");
        }

        // 1. 대상 조회
        Optional<WcsRouteNode> optionalNode = wcsRouteNodeRepository.findById(factoryName.trim(), routeNodeId);
        if (optionalNode.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 WCS 경로 노드가 존재하지 않습니다. [factoryName: " + factoryName + ", routeNodeId: " + routeNodeId + "]");
        }

        WcsRouteNode routeNodeDomain = optionalNode.get();

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsRouteNodeModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs route node modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 수정
        WcsRouteNodeUpdateCommand command = WcsRouteNodeUpdateCommand.builder()
                .transactionInfo(tx)
                .bayId(dto.getBayId())
                .controllerType(dto.getControllerType())
                .craneId(dto.getCraneId())
                .description(dto.getDescription())
                .equipmentId(dto.getEquipmentId())
                .nodeEquipmentType(dto.getNodeEquipmentType())
                .nodeId(dto.getNodeId())
                .nodeName(dto.getNodeName())
                .nodeSeq(dto.getNodeSeq())
                .rerouteType(dto.getRerouteType())
                .routeNodeType(dto.getRouteNodeType())
                .unitId(dto.getUnitId())
                .useYn(dto.getUseYn())
                .build();

        routeNodeDomain.update(command);

        // 4. ROUTE_NODE 테이블 저장
        WcsRouteNode updatedRouteNode = wcsRouteNodeRepository.save(routeNodeDomain);

        log.info("WcsRouteNode updated successfully: [factoryName={}, routeNodeId={}]", factoryName, routeNodeId);

        return WcsRouteNodeResponse.fromDomain(updatedRouteNode);
    }

    /**
     * 복합키 기반 WCS 경로 노드 삭제 (DELETE)
     * - 대상 존재 확인
     * - ROUTE_NODE 테이블에서 삭제 (이력 테이블 부재)
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteRouteNode(String factoryName, Long routeNodeId) {
        if (!StringUtils.hasText(factoryName) || routeNodeId == null) {
            throw new IllegalArgumentException("삭제할 대상 경로 노드 복합키 식별자 정보가 누락되었습니다.");
        }

        if (!wcsRouteNodeRepository.existsById(factoryName.trim(), routeNodeId)) {
            throw new IllegalArgumentException("삭제할 대상 WCS 경로 노드가 존재하지 않습니다. [factoryName: " + factoryName + ", routeNodeId: " + routeNodeId + "]");
        }

        // ROUTE_NODE 테이블에서 삭제
        wcsRouteNodeRepository.deleteById(factoryName.trim(), routeNodeId);

        log.info("WcsRouteNode deleted successfully: [factoryName={}, routeNodeId={}]", factoryName, routeNodeId);
    }
}
