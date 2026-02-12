package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.WorkOrder;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 저장소의 기능을 정의하는 인터페이스.
 * 애플리케이션의 다른 부분(서비스 계층 등)은 이 인터페이스에만 의존합니다.
 * 실제 구현 기술(JPA, JDBC 등)과는 완전히 분리됩니다.
 */
public interface WorkOrderRepository {
    /**
     * 사용자를 저장하거나 업데이트합니다.
     * @param workOrder 저장할 사용자 도메인 객체
     * @return 저장된 사용자 도메인 객체 (ID 포함)
     */
    WorkOrder save(WorkOrder workOrder);

    /**
     * ID로 사용자를 찾습니다.
     * @param id carrier ID
     * @return Optional<Carriers>
     */
    Optional<WorkOrder> findById(Long id);

    /**
     * lotName로 Lots를 찾습니다.
     * @param workOrderName
     * @return Optional<Lots>
     */
    Optional<WorkOrder> findByWorkOrderName(String workOrderName);

    /**
     * 모든 사용자를 찾습니다.
     * @return 모든 사용자 도메인 객체 리스트
     */
    List<WorkOrder> findAll();

    void deleteAllByIdInBatch(List<Long>ids);

//    Page<WorkOrderResponseDto> findWorkOrderWithConditions(WorkOrderSearchConditionDto condition, Pageable pageable);

    Optional<WorkOrder> findByWorkOrderState(String workOrderState);
}
